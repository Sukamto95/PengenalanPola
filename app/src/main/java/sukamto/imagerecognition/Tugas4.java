package sukamto.imagerecognition;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

public class Tugas4 extends AppCompatActivity {
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    private static final int REQUEST_SELECT_IMAGE = 200;
    MainActivity main;
    ImageView image = null;
    TextView resultText, chainCodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tugas4);
        main = new MainActivity();
        image = (ImageView) findViewById(R.id.imageOriColor);
        resultText = findViewById(R.id.resultText);
        chainCodeText = findViewById(R.id.chainCodeText);
        Bitmap bitmap = main.getImageBitmap();
        if(bitmap != null){
            image.setImageBitmap(bitmap);
        }
    }

    public void addPicture(View view){
        final CharSequence[] options = { "Take Picture", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(Tugas4.this);
        builder.setTitle("Add Picture!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Picture"))
                {
                    takePicture();
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    choosePicture();
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void takePicture(){
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE
        );
        if(pictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pictureIntent,
                    REQUEST_CAPTURE_IMAGE);
        }
    }

    public void choosePicture(){
        try{
            if (ActivityCompat.checkSelfPermission(Tugas4.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Tugas4.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_SELECT_IMAGE);
            } else{
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_SELECT_IMAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAPTURE_IMAGE &&
                resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                image.setImageBitmap(imageBitmap);
                main.setBitmap(imageBitmap);
            }
        } else if (requestCode == REQUEST_SELECT_IMAGE) {
            Uri selectedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            Bitmap bmp = BitmapFactory.decodeFile(picturePath);
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            int newWidth = 450;
            int newHeight = 450;
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
//            matrix.postRotate(90);
            Bitmap imageBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
            Bitmap binaryBitmap = getBinaryImage(imageBitmap,128);
            image.setImageBitmap(binaryBitmap);
            main.setBitmap(binaryBitmap);
            String[] result = detectNumber2(binaryBitmap);
            resultText.setText(result[0]);
            chainCodeText.setText(result[1]);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case REQUEST_SELECT_IMAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, REQUEST_SELECT_IMAGE);
                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                }
                break;
        }
    }


    public Bitmap getBinaryImage(Bitmap bitmap, int threshold) {
        Bitmap result = bitmap.copy(bitmap.getConfig(), true);
        int[] color;

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                int pixel = bitmap.getPixel(i, j);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                int grayscale = (red + green + blue) / 3;

                if (grayscale < threshold) {
                    result.setPixel(i,j, Color.rgb(0,0,0));
                } else {
                    result.setPixel(i,j, Color.rgb(255,255,255));
                }
            }
        }
        return result;
    }

    public String[] detectNumber(Bitmap bitmap) {
        Bitmap image = bitmap.copy(bitmap.getConfig(), true);
        int[] color;
        String result = "";
        String chains = "";
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                int pixel = image.getPixel(i, j);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                int grayscale = (red + green + blue) / 3;

                if (grayscale == 0) {
                    String chain = getChainCode(image, i, j);
                    chains = chains + chain + "\n";
                    result = result + translate(chain);
                    Log.e("chain", chain);
                    Log.e("num", "" + translate(chain));
                    floodFill(image, i, j);
                }
            }
        }
        return new String[] {result, chains};
    }

    public String[] detectNumber2(Bitmap bitmap) {

        int[] color;
        String chain;

        Bitmap image = bitmap.copy(bitmap.getConfig(), true);
        StringBuilder result = new StringBuilder();
        StringBuilder chains = new StringBuilder();

        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                color = getPixelColor(image, i, j);

                if (color[3] == 0) {
                    chain = getChainCode(image, i, j);
                    chains.append(String.format("%s\n\n", chain));
                    result.append(String.format("%d | ", translate2(chain)));
                    floodFill(image, i, j);
                }
            }
        }

        return new String[]{result.toString(), chains.toString()};
    }

    private String getChainCode(Bitmap bitmap, int x, int y) {
        int a = x;
        int b = y;
        int[] next;
        int source = 6;
        String chain = "";

        do {
            next = getNextPixel(bitmap, a, b, source);
            a = next[0];
            b = next[1];
            source = (next[2] + 4) % 8;
            chain = chain + next[2];
        }
        while (!(a == x && b == y));

        return chain;
    }

    private int[] getNextPixel(Bitmap bitmap, int x, int y, int source) {
        int a, b, target = source;
        int[][] points = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};

        do {
            target = (target + 1) % 8;
            a = x + points[target][0];
            b = y + points[target][1];
        }
        while (getPixelColor(bitmap, a, b)[3] == 255);

        //Log.i("loc", String.format("%d %d %d", a, b, target));

        return new int[]{a, b, target};
    }

    private int translate(String chain) {
        double[][] ratio = {
                {0.250, 0.075, 0.098, 0.075, 0.250, 0.075, 0.098, 0.075},
                {0.329, 0.079, 0.074, 0.000, 0.361, 0.053, 0.095, 0.005},
                {0.108, 0.161, 0.172, 0.044, 0.134, 0.112, 0.243, 0.022},
                {0.143, 0.098, 0.158, 0.105, 0.132, 0.098, 0.169, 0.094},
                {0.186, 0.146, 0.090, 0.005, 0.328, 0.005, 0.232, 0.005},
                {0.161, 0.060, 0.218, 0.067, 0.147, 0.070, 0.211, 0.063},
                {0.189, 0.086, 0.133, 0.103, 0.163, 0.086, 0.159, 0.077},
                {0.211, 0.091, 0.201, 0.000, 0.201, 0.105, 0.183, 0.004},
                {0.175, 0.095, 0.132, 0.101, 0.164, 0.101, 0.132, 0.095},
                {0.168, 0.090, 0.147, 0.086, 0.181, 0.086, 0.142, 0.095}
        };
        double max = 0;
        double[] sum = new double[8];
        int number = 0;

        for (int i = 0; i < chain.length(); i++) {
            sum[Character.getNumericValue(chain.charAt(i))]++;
        }

        for (int i = 0; i < 8; i++) {
            sum[i] = sum[i] / chain.length();
        }

        for (int i = 0; i < 10; i++) {
            double res = 0;
            for (int j = 0; j < 8; j++) {
                res = res + ratio[i][j] * sum[j];
            }
            res = res / getVectorLength(ratio[i]) / getVectorLength(sum);
            if (res > max) {
                max = res;
                number = i;
            }
        }

        return number;
    }

    public int translate2(String chain) {

        ArrayList<Integer> count;
        String simpleChain = getSimplifiedChain(chain);

        if (simpleChain.equals("2460")) {
            count = getCountChain(chain);

            if((double)count.get(0) / count.get(1) <= 0.2){
                return 1;
            }
            else{
                return 0;
            }
        } else if (simpleChain.equals("246424602060")) {
            count = getCountChain(chain);

            if (count.get(1) > count.get(count.size() - 3)) {
                return 2;
            } else {
                return 5;
            }
        } else if (simpleChain.equals("246020602060")) {
            return 3;
        } else if (simpleChain.equals("2420246060")) {
            return 4;
        } else if (simpleChain.equals("24642460")) {
            return 6;
        } else if (simpleChain.equals("246060")) {
            return 7;
        } else if (simpleChain.equals("24602060")) {
            return 9;
        } else {
            return -1;
        }
    }

    public String getSimplifiedChain(String chain) {

        if (chain.length() < 2) {
            return chain;
        }

        char current;
        char last = chain.charAt(0);
        StringBuilder result = new StringBuilder();

        result.append(last);

        for (int i = 1; i < chain.length(); i++) {
            current = chain.charAt(i);
            if (current != last && Character.getNumericValue(current) % 2 == 0) {
                last = chain.charAt(i);
                result.append(last);
            }
        }

        return result.toString();
    }

    public ArrayList<Integer> getCountChain(String chain) {

        if (chain.length() < 2) {
            return new ArrayList<>();
        }

        ArrayList<Integer> list = new ArrayList<>();
        char current;
        char last = chain.charAt(0);
        int counter = 1;

        for (int i = 1; i < chain.length(); i++) {
            current = chain.charAt(i);
            if (Character.getNumericValue(current) % 2 == 0) {
                if (current != last) {
                    list.add(counter);
                    last = chain.charAt(i);
                    counter = 1;
                } else {
                    counter++;
                }
            }
        }

        list.add(counter);

        return list;
    }

    private double getVectorLength(double[] vector) {
        double sum = 0;

        for (int i = 0; i < 8; i++) {
            sum = sum + vector[i] * vector[i];
        }

        return Math.sqrt(sum);
    }

    private void floodFill(Bitmap bitmap, int x, int y) {
        int[] color = getPixelColor(bitmap, x, y);

        if (color[3] != 255) {
            bitmap.setPixel(x,y,Color.rgb(255,255,255));
            floodFill(bitmap, x - 1, y);
            floodFill(bitmap, x + 1, y);
            floodFill(bitmap, x, y - 1);
            floodFill(bitmap, x, y + 1);
        }
    }

    private int[] getPixelColor(Bitmap bitmap, int x, int y) {
        int pixel, red, green, blue, grayscale;

        pixel = bitmap.getPixel(x, y);
        red = Color.red(pixel);
        green = Color.green(pixel);
        blue = Color.blue(pixel);
        grayscale = (red + green + blue) / 3;

        return new int[]{red, green, blue, grayscale};
    }
}

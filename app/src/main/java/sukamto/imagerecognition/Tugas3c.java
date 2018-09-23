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
import android.view.View;
import android.widget.ImageView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.HashMap;

public class Tugas3c extends AppCompatActivity {

    public static HashMap<Integer,Integer> SMOOTH_RED = new HashMap<Integer,Integer>();
    public static HashMap<Integer,Integer> SMOOTH_GREEN = new HashMap<Integer,Integer>();
    public static HashMap<Integer,Integer> SMOOTH_BLUE = new HashMap<>();
    public static HashMap<Integer,Integer> SMOOTH_GRAY = new HashMap<>();
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    private static final int REQUEST_SELECT_IMAGE = 200;
    MainActivity main;
    ImageView image = null;
    ImageView smoothedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tugas3c);
        main = new MainActivity();
        image = (ImageView) findViewById(R.id.imageOriColor);
        smoothedImage = (ImageView) findViewById(R.id.imageResult);
        Bitmap bitmap = main.getImageBitmap();
        if(bitmap != null){
            image.setImageBitmap(bitmap);
        }
    }

    public void addPicture(View view){
        final CharSequence[] options = { "Take Picture", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(Tugas3c.this);
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
            if (ActivityCompat.checkSelfPermission(Tugas3c.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Tugas3c.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_SELECT_IMAGE);
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
            int newWidth = 640;
            int newHeight = 360;
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            matrix.postRotate(90);
            Bitmap imageBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
            image.setImageBitmap(imageBitmap);
            main.setBitmap(imageBitmap);
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

    public void getSmoothedImage(View view){
        imageSmoothing();
        GraphView grayGraph = (GraphView) findViewById(R.id.graphGrayscale);
        GraphView redGraph = (GraphView) findViewById(R.id.graphRed);
        GraphView greenGraph = (GraphView) findViewById(R.id.graphGreen);
        GraphView blueGraph = (GraphView) findViewById(R.id.graphBlue);
        grayGraph.removeAllSeries();
        redGraph.removeAllSeries();
        greenGraph.removeAllSeries();
        blueGraph.removeAllSeries();
        DataPoint[] redData = new DataPoint[256];
        DataPoint[] greenData = new DataPoint[256];
        DataPoint[] blueData = new DataPoint[256];
        DataPoint[] grayData = new DataPoint[256];
        for(int i=0;i<256;i++){
            redData[i] = new DataPoint(i,main.ARR_RED.get(i));
            greenData[i] = new DataPoint(i,main.ARR_GREEN.get(i));
            blueData[i] = new DataPoint(i,main.ARR_BLUE.get(i));
            grayData[i] = new DataPoint(i,main.ARR_GRAY.get(i));
        }
        LineGraphSeries<DataPoint> redPoint= new LineGraphSeries<>(redData);
        redPoint.setColor(Color.RED);
        LineGraphSeries<DataPoint> greenPoint= new LineGraphSeries<>(greenData);
        greenPoint.setColor(Color.GREEN);
        LineGraphSeries<DataPoint> bluePoint= new LineGraphSeries<>(blueData);
        bluePoint.setColor(Color.BLUE);
        LineGraphSeries<DataPoint> grayPoint= new LineGraphSeries<>(grayData);
        grayPoint.setColor(Color.GRAY);
        double highestRed = redPoint.getHighestValueY();
        double highestGreen = greenPoint.getHighestValueY();
        double highestBlue = bluePoint.getHighestValueY();
        double highestGray = grayPoint.getHighestValueY();
        grayGraph.getViewport().setMaxX(256);
        grayGraph.getViewport().setMaxY(highestGray);
        grayGraph.getViewport().setXAxisBoundsManual(true);
        grayGraph.getViewport().setYAxisBoundsManual(true);
        redGraph.getViewport().setMaxX(256);
        redGraph.getViewport().setMaxY(highestRed);
        redGraph.getViewport().setXAxisBoundsManual(true);
        redGraph.getViewport().setYAxisBoundsManual(true);
        greenGraph.getViewport().setMaxX(256);
        greenGraph.getViewport().setMaxY(highestGreen);
        greenGraph.getViewport().setXAxisBoundsManual(true);
        greenGraph.getViewport().setYAxisBoundsManual(true);
        blueGraph.getViewport().setMaxX(256);
        blueGraph.getViewport().setMaxY(highestBlue);
        blueGraph.getViewport().setXAxisBoundsManual(true);
        blueGraph.getViewport().setYAxisBoundsManual(true);
        grayGraph.setHorizontalScrollBarEnabled(true);
        redGraph.setHorizontalScrollBarEnabled(true);
        greenGraph.setHorizontalScrollBarEnabled(true);
        blueGraph.setHorizontalScrollBarEnabled(true);
        grayGraph.addSeries(grayPoint);
        redGraph.addSeries(redPoint);
        greenGraph.addSeries(greenPoint);
        blueGraph.addSeries(bluePoint);

        GraphView graySGraph = (GraphView) findViewById(R.id.smoothGray);
        GraphView redSGraph = (GraphView) findViewById(R.id.smoothRed);
        GraphView greenSGraph = (GraphView) findViewById(R.id.smoothGreen);
        GraphView blueSGraph = (GraphView) findViewById(R.id.smoothBlue);
        graySGraph.removeAllSeries();
        redSGraph.removeAllSeries();
        greenSGraph.removeAllSeries();
        blueSGraph.removeAllSeries();
        DataPoint[] redSData = new DataPoint[256];
        DataPoint[] greenSData = new DataPoint[256];
        DataPoint[] blueSData = new DataPoint[256];
        DataPoint[] graySData = new DataPoint[256];
        for(int i=0;i<256;i++){
            redSData[i] = new DataPoint(i,SMOOTH_RED.get(i));
            greenSData[i] = new DataPoint(i,SMOOTH_GREEN.get(i));
            blueSData[i] = new DataPoint(i,SMOOTH_BLUE.get(i));
            graySData[i] = new DataPoint(i,SMOOTH_GRAY.get(i));
        }
        LineGraphSeries<DataPoint> redSPoint= new LineGraphSeries<>(redSData);
        redSPoint.setColor(Color.RED);
        LineGraphSeries<DataPoint> greenSPoint= new LineGraphSeries<>(greenSData);
        greenSPoint.setColor(Color.GREEN);
        LineGraphSeries<DataPoint> blueSPoint= new LineGraphSeries<>(blueSData);
        blueSPoint.setColor(Color.BLUE);
        LineGraphSeries<DataPoint> graySPoint= new LineGraphSeries<>(graySData);
        graySPoint.setColor(Color.GRAY);
        double highestSRed = redSPoint.getHighestValueY();
        double highestSGreen = greenSPoint.getHighestValueY();
        double highestSBlue = blueSPoint.getHighestValueY();
        double highestSGray = graySPoint.getHighestValueY();
        graySGraph.getViewport().setMaxX(256);
        graySGraph.getViewport().setMaxY(highestSGray);
        graySGraph.getViewport().setXAxisBoundsManual(true);
        graySGraph.getViewport().setYAxisBoundsManual(true);
        redSGraph.getViewport().setMaxX(256);
        redSGraph.getViewport().setMaxY(highestSRed);
        redSGraph.getViewport().setXAxisBoundsManual(true);
        redSGraph.getViewport().setYAxisBoundsManual(true);
        greenSGraph.getViewport().setMaxX(256);
        greenSGraph.getViewport().setMaxY(highestSGreen);
        greenSGraph.getViewport().setXAxisBoundsManual(true);
        greenSGraph.getViewport().setYAxisBoundsManual(true);
        blueSGraph.getViewport().setMaxX(256);
        blueSGraph.getViewport().setMaxY(highestSBlue);
        blueSGraph.getViewport().setXAxisBoundsManual(true);
        blueSGraph.getViewport().setYAxisBoundsManual(true);
        graySGraph.setHorizontalScrollBarEnabled(true);
        redSGraph.setHorizontalScrollBarEnabled(true);
        greenSGraph.setHorizontalScrollBarEnabled(true);
        blueSGraph.setHorizontalScrollBarEnabled(true);
        graySGraph.addSeries(graySPoint);
        redSGraph.addSeries(redSPoint);
        greenSGraph.addSeries(greenSPoint);
        blueSGraph.addSeries(blueSPoint);
    }

    public void imageSmoothing(){
        for(int i=0;i<=255;i++){
            main.ARR_RED.put(i,0);
            main.ARR_GREEN.put(i,0);
            main.ARR_BLUE.put(i,0);
            main.ARR_GRAY.put(i,0);
            SMOOTH_RED.put(i,0);
            SMOOTH_GREEN.put(i,0);
            SMOOTH_BLUE.put(i,0);
            SMOOTH_GRAY.put(i,0);
        }
        Bitmap imageBitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                int[] pixs = new int[9];
                int sumR = 0;
                int sumG = 0;
                int sumB = 0;
                int count = 0;
                pixs[4] = imageBitmap.getPixel(j,i);
                int r4 = (pixs[4] >> 16) & 0xff;
                int g4 = (pixs[4] >> 8) & 0xff;
                int b4 = (pixs[4]) & 0xff;
                pixs[4] = 0xff000000 | (r4 << 16) | (g4 << 8) | b4;
                int gray = (r4+g4+b4)/3;

                if(i >= 1 && j >= 1){
                    pixs[0] = imageBitmap.getPixel(j-1,i-1);
                    int r0 = (pixs[0] >> 16) & 0xff;
                    int g0 = (pixs[0] >> 8) & 0xff;
                    int b0 = (pixs[0]) & 0xff;
                    pixs[0] = 0xff000000 | (r0 << 16) | (g0 << 8) | b0;
                    sumR += r0;
                    sumG += g0;
                    sumB += b0;
                    count++;
                } if(i >= 1 && j >= 0){
                    pixs[1] = imageBitmap.getPixel(j,i-1);
                    int r1 = (pixs[1] >> 16) & 0xff;
                    int g1 = (pixs[1] >> 8) & 0xff;
                    int b1 = (pixs[1]) & 0xff;
                    pixs[1] = 0xff000000 | (r1 << 16) | (g1 << 8) | b1;
                    sumR += r1;
                    sumG += g1;
                    sumB += b1;
                    count++;
                } if(i >= 1 && j <= width - 2){
                    pixs[2] = imageBitmap.getPixel(j+1,i-1);
                    int r2 = (pixs[2] >> 16) & 0xff;
                    int g2 = (pixs[2] >> 8) & 0xff;
                    int b2 = (pixs[2]) & 0xff;
                    pixs[2] = 0xff000000 | (r2 << 16) | (g2 << 8) | b2;
                    sumR += r2;
                    sumG += g2;
                    sumB += b2;
                    count++;
                } if(i >= 0 && j <= width - 2){
                    pixs[5] = imageBitmap.getPixel(j+1,i);
                    int r5 = (pixs[5] >> 16) & 0xff;
                    int g5 = (pixs[5] >> 8) & 0xff;
                    int b5 = (pixs[5]) & 0xff;
                    pixs[5] = 0xff000000 | (r5 << 16) | (g5 << 8) | b5;
                    sumR += r5;
                    sumG += g5;
                    sumB += b5;
                    count++;
                } if(i <= height - 2 && j <= width - 2){
                    pixs[8] = imageBitmap.getPixel(j+1,i+1);
                    int r8 = (pixs[8] >> 16) & 0xff;
                    int g8 = (pixs[8] >> 8) & 0xff;
                    int b8 = (pixs[8]) & 0xff;
                    pixs[8] = 0xff000000 | (r8 << 16) | (g8 << 8) | b8;
                    sumR += r8;
                    sumG += g8;
                    sumB += b8;
                    count++;
                } if(i <= height - 2 && j >= 0){
                    pixs[7] = imageBitmap.getPixel(j,i+1);
                    int r7 = (pixs[7] >> 16) & 0xff;
                    int g7 = (pixs[7] >> 8) & 0xff;
                    int b7 = (pixs[7]) & 0xff;
                    pixs[7] = 0xff000000 | (r7 << 16) | (g7 << 8) | b7;
                    sumR += r7;
                    sumG += g7;
                    sumB += b7;
                    count++;
                } if(i <= height - 2 && j >= 1){
                    pixs[6] = imageBitmap.getPixel(j-1,i+1);
                    int r6 = (pixs[6] >> 16) & 0xff;
                    int g6 = (pixs[6] >> 8) & 0xff;
                    int b6 = (pixs[6]) & 0xff;
                    pixs[6] = 0xff000000 | (r6 << 16) | (g6 << 8) | b6;
                    sumR += r6;
                    sumG += g6;
                    sumB += b6;
                    count++;
                } if(i >= 0 && j >= 1){
                    pixs[3] = imageBitmap.getPixel(j-1,i);
                    int r3 = (pixs[3] >> 16) & 0xff;
                    int g3 = (pixs[3] >> 8) & 0xff;
                    int b3 = (pixs[3]) & 0xff;
                    pixs[3] = 0xff000000 | (r3 << 16) | (g3 << 8) | b3;
                    sumR += r3;
                    sumG += g3;
                    sumB += b3;
                    count++;
                }
                int avgR = (int)Math.round((double)sumR/count);
                int avgG = (int)Math.round((double)sumG/count);
                int avgB = (int)Math.round((double)sumB/count);
                int avgGray = (avgR+avgG+avgB)/3;
                int rBefore = main.ARR_RED.get(r4);
                int gBefore = main.ARR_GREEN.get(g4);
                int bBefore = main.ARR_BLUE.get(b4);
                int grayBefore = main.ARR_GRAY.get(gray);

                main.ARR_RED.put(r4,rBefore+1);
                main.ARR_GREEN.put(g4,gBefore+1);
                main.ARR_BLUE.put(b4,bBefore+1);
                main.ARR_GRAY.put(gray,grayBefore+1);
                SMOOTH_RED.put(avgR,SMOOTH_RED.get(avgR)+1);
                SMOOTH_GREEN.put(avgR,SMOOTH_GREEN.get(avgG)+1);
                SMOOTH_BLUE.put(avgR,SMOOTH_BLUE.get(avgB)+1);
                SMOOTH_GRAY.put(avgGray,SMOOTH_GRAY.get(avgGray)+1);

                newBitmap.setPixel(j,i, Color.rgb(avgR, avgG, avgB));
            }
        }
        smoothedImage.setImageBitmap(newBitmap);
    }
}

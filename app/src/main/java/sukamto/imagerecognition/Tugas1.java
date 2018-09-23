package sukamto.imagerecognition;

import android.Manifest;
import android.content.Context;
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

import java.nio.file.Path;
import java.util.HashMap;

public class Tugas1 extends AppCompatActivity {
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    private static final int REQUEST_SELECT_IMAGE = 200;
    MainActivity main;
    ImageView image = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tugas1);
//        Bundle bundle = this.getIntent().getExtras();
//        int isOriNull = bundle.getInt("isOriNull");
//        if(isOriNull == 0){
//            byte[] byteArray = bundle.getByteArray("bitmap");
//            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//            if(bitmap != null){
//                image.setImageBitmap(bitmap);
//                main.setBitmap(bitmap);
//            }
//        }
        main = new MainActivity();
        image = (ImageView) findViewById(R.id.imageOriColor);
        Bitmap bitmap = main.getImageBitmap();
        if(bitmap != null){
            image.setImageBitmap(bitmap);
        }
    }

    public void addPicture(View view){
        final CharSequence[] options = { "Take Picture", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(Tugas1.this);
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
            if (ActivityCompat.checkSelfPermission(Tugas1.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Tugas1.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_SELECT_IMAGE);
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

    public void checkRGB(View view){
        main.countColor();
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
    }
}

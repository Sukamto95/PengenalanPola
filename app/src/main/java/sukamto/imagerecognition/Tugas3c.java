package sukamto.imagerecognition;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

import sukamto.imagerecognition.model.MainModel;

public class Tugas3c extends AppCompatActivity {

    ImageView image = null;
    ImageView smoothedImage = null;
    ImageView grayImage = null;
    ImageView smoothedGrayImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tugas3c);
        image = (ImageView) findViewById(R.id.imageOriColor);
        smoothedImage = (ImageView) findViewById(R.id.imageTransformColor);
        grayImage = (ImageView) findViewById(R.id.imageOriGrayscale);
        smoothedGrayImage = (ImageView) findViewById(R.id.imageTransformGrayscale);
        Bitmap bitmap = MainModel.getImageBitmap();
        if(bitmap != null){
            image.setImageBitmap(bitmap);
        }
        Bitmap grayBitmap = MainModel.getGrayBitmap();
        if(grayBitmap != null){
            grayImage.setImageBitmap(grayBitmap);
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
                    MainModel.REQUEST_CAPTURE_IMAGE);
        }
    }

    public void choosePicture(){
        try{
            if (ActivityCompat.checkSelfPermission(Tugas3c.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Tugas3c.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainModel.REQUEST_SELECT_IMAGE);
            } else{
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, MainModel.REQUEST_SELECT_IMAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainModel.REQUEST_CAPTURE_IMAGE &&
                resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                Bitmap imageBitmap = MainModel.getScaledBitmap(bmp,0);
                image.setImageBitmap(imageBitmap);
                MainModel.setBitmap(imageBitmap);
            }
        } else if (requestCode == MainModel.REQUEST_SELECT_IMAGE) {
            Uri selectedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            Bitmap bmp = BitmapFactory.decodeFile(picturePath);
            Bitmap imageBitmap = MainModel.getScaledBitmap(bmp,90);
            image.setImageBitmap(imageBitmap);
            MainModel.setBitmap(imageBitmap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case MainModel.REQUEST_SELECT_IMAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, MainModel.REQUEST_SELECT_IMAGE);
                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                }
                break;
        }
    }

    public void getSmoothedImage(View view){
        MainModel.countColor();
        grayImage.setImageBitmap(MainModel.getGrayBitmap());
        Bitmap[] bitmapResult = MainModel.getSmoothingImage();
        smoothedImage.setImageBitmap(bitmapResult[0]);
        smoothedGrayImage.setImageBitmap(bitmapResult[1]);

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
            redData[i] = new DataPoint(i,MainModel.ARR_RED[i]);
            greenData[i] = new DataPoint(i,MainModel.ARR_GREEN[i]);
            blueData[i] = new DataPoint(i,MainModel.ARR_BLUE[i]);
            grayData[i] = new DataPoint(i,MainModel.ARR_GRAY[i]);
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
            redSData[i] = new DataPoint(i,MainModel.smooth_red[i]);
            greenSData[i] = new DataPoint(i,MainModel.smooth_green[i]);
            blueSData[i] = new DataPoint(i,MainModel.smooth_blue[i]);
            graySData[i] = new DataPoint(i,MainModel.smooth_gray[i]);
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

}

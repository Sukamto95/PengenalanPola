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
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class Tugas3a extends AppCompatActivity {

    private static final int REQUEST_CAPTURE_IMAGE = 100;
    private static final int REQUEST_SELECT_IMAGE = 200;
    ImageView image = null;
    ImageView trImage = null;
    ImageView grayImage = null;
    ImageView trGrayImage = null;
    MainActivity main = null;
    SeekBar seekBar;
    TextView seekBarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tugas3a);
        main = new MainActivity();
        image = (ImageView) findViewById(R.id.imageOriColor);
        trImage = (ImageView) findViewById(R.id.imageTransformColor);
        grayImage = (ImageView) findViewById(R.id.imageOriGrayscale);
        trGrayImage = (ImageView) findViewById(R.id.imageTransformGrayscale);
        seekBar = findViewById(R.id.seekBarW);
        seekBarText = findViewById(R.id.seekBarText);
        Bitmap bitmap = main.getImageBitmap();
        if(bitmap != null){
            image.setImageBitmap(bitmap);
        }
        Bitmap gBitmap = main.getGrayBitmap();
        if(gBitmap != null){
            grayImage.setImageBitmap(gBitmap);
        }
        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // Write code to perform some action when progress is changed.
                    android.util.Log.e("Current SeekBar value ",progress+"");
                    double prog = progress;
                    seekBarText.setText("Current value is "+prog/10);
                    transformImage(prog/10);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // Write code to perform some action when touch is started.
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // Write code to perform some action when touch is stopped.
                }
            });
        }

    }

    public void addPicture(View view){
        final CharSequence[] options = { "Take Picture", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(Tugas3a.this);
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
            if (ActivityCompat.checkSelfPermission(Tugas3a.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Tugas3a.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_SELECT_IMAGE);
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
                double progress = seekBar.getProgress();
                transformImage(progress/10);
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
            double progress = seekBar.getProgress();
            transformImage(progress/10);
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

    public void transformImage(double w){
        main.countColor();
        main.ekualisasi(w);
        grayImage.setImageBitmap(main.getGrayBitmap());
        trImage.setImageBitmap(main.getTrBitmap());
        trGrayImage.setImageBitmap(main.getTrGrayBitmap());
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
        blueGraph.setHorizontalScrollBarEnabled(true);
        redGraph.setHorizontalScrollBarEnabled(true);
        greenGraph.setHorizontalScrollBarEnabled(true);
        blueGraph.setHorizontalScrollBarEnabled(true);
        grayGraph.addSeries(grayPoint);
        redGraph.addSeries(redPoint);
        greenGraph.addSeries(greenPoint);
        blueGraph.addSeries(bluePoint);

        GraphView ekualGray = (GraphView) findViewById(R.id.ekualGrayscale);
        GraphView ekualRed = (GraphView) findViewById(R.id.ekualRed);
        GraphView ekualGreen = (GraphView) findViewById(R.id.ekualGreen);
        GraphView ekualBlue = (GraphView) findViewById(R.id.ekualBlue);
        GraphView kumulatifGray = (GraphView) findViewById(R.id.kumulatifGrayscale);
        GraphView kumulatifRed = (GraphView) findViewById(R.id.kumulatifRed);
        GraphView kumulatifGreen = (GraphView) findViewById(R.id.kumulatifGreen);
        GraphView kumulatifBlue = (GraphView) findViewById(R.id.kumulatifBlue);
        ekualGray.removeAllSeries();
        ekualRed.removeAllSeries();
        ekualGreen.removeAllSeries();
        ekualBlue.removeAllSeries();
        kumulatifGray.removeAllSeries();
        kumulatifRed.removeAllSeries();
        kumulatifGreen.removeAllSeries();
        kumulatifBlue.removeAllSeries();
        DataPoint[] ekualGrayData = new DataPoint[256];
        DataPoint[] ekualRedData = new DataPoint[256];
        DataPoint[] ekualGreenData = new DataPoint[256];
        DataPoint[] ekualBlueData = new DataPoint[256];
        DataPoint[] kumulatifGrayData = new DataPoint[256];
        DataPoint[] kumulatifRedData = new DataPoint[256];
        DataPoint[] kumulatifGreenData = new DataPoint[256];
        DataPoint[] kumulatifBlueData = new DataPoint[256];
        for(int i=0;i<256;i++){
            ekualGrayData[i] = new DataPoint(i,main.EKUAL_GRAY.get(i));
            ekualRedData[i] = new DataPoint(i,main.EKUAL_RED.get(i));
            ekualGreenData[i] = new DataPoint(i,main.EKUAL_GREEN.get(i));
            ekualBlueData[i] = new DataPoint(i,main.EKUAL_BLUE.get(i));
            kumulatifGrayData[i] = new DataPoint(i,main.KUM_GRAY.get(i));
            kumulatifRedData[i] = new DataPoint(i,main.KUM_RED.get(i));
            kumulatifGreenData[i] = new DataPoint(i,main.KUM_GREEN.get(i));
            kumulatifBlueData[i] = new DataPoint(i,main.KUM_BLUE.get(i));
        }
        LineGraphSeries<DataPoint> ekualGrayPoint= new LineGraphSeries<>(ekualGrayData);
        LineGraphSeries<DataPoint> ekualRedPoint= new LineGraphSeries<>(ekualRedData);
        LineGraphSeries<DataPoint> ekualGreenPoint= new LineGraphSeries<>(ekualGreenData);
        LineGraphSeries<DataPoint> ekualBluePoint= new LineGraphSeries<>(ekualBlueData);
        LineGraphSeries<DataPoint> kumGrayPoint= new LineGraphSeries<>(kumulatifGrayData);
        LineGraphSeries<DataPoint> kumRedPoint= new LineGraphSeries<>(kumulatifRedData);
        LineGraphSeries<DataPoint> kumGreenPoint= new LineGraphSeries<>(kumulatifGreenData);
        LineGraphSeries<DataPoint> kumBluePoint= new LineGraphSeries<>(kumulatifBlueData);
        ekualGrayPoint.setColor(Color.GRAY);
        ekualRedPoint.setColor(Color.RED);
        ekualGreenPoint.setColor(Color.GREEN);
        ekualBluePoint.setColor(Color.BLUE);
        kumGrayPoint.setColor(Color.GRAY);
        kumRedPoint.setColor(Color.RED);
        kumGreenPoint.setColor(Color.GREEN);
        kumBluePoint.setColor(Color.BLUE);
        double highestEkualGray = ekualGrayPoint.getHighestValueY();
        ekualGray.getViewport().setMaxX(256);
        ekualGray.getViewport().setMaxY(highestEkualGray);
        ekualGray.getViewport().setXAxisBoundsManual(true);
        ekualGray.getViewport().setYAxisBoundsManual(true);
        double highestEkualRed = ekualRedPoint.getHighestValueY();
        ekualRed.getViewport().setMaxX(256);
        ekualRed.getViewport().setMaxY(highestEkualRed);
        ekualRed.getViewport().setXAxisBoundsManual(true);
        ekualRed.getViewport().setYAxisBoundsManual(true);
        double highestEkualGreen = ekualGreenPoint.getHighestValueY();
        ekualGreen.getViewport().setMaxX(256);
        ekualGreen.getViewport().setMaxY(highestEkualGreen);
        ekualGreen.getViewport().setXAxisBoundsManual(true);
        ekualGreen.getViewport().setYAxisBoundsManual(true);
        double highestEkualBlue = ekualBluePoint.getHighestValueY();
        ekualBlue.getViewport().setMaxX(256);
        ekualBlue.getViewport().setMaxY(highestEkualBlue);
        ekualBlue.getViewport().setXAxisBoundsManual(true);
        ekualBlue.getViewport().setYAxisBoundsManual(true);
        double highestKumulatifGray = kumGrayPoint.getHighestValueY();
        kumulatifGray.getViewport().setMaxX(256);
        kumulatifGray.getViewport().setMaxY(highestKumulatifGray);
        kumulatifGray.getViewport().setXAxisBoundsManual(true);
        kumulatifGray.getViewport().setYAxisBoundsManual(true);
        double highestKumulatifRed = kumRedPoint.getHighestValueY();
        kumulatifRed.getViewport().setMaxX(256);
        kumulatifRed.getViewport().setMaxY(highestKumulatifRed);
        kumulatifRed.getViewport().setXAxisBoundsManual(true);
        kumulatifRed.getViewport().setYAxisBoundsManual(true);
        double highestKumulatifGreen = kumGreenPoint.getHighestValueY();
        kumulatifGreen.getViewport().setMaxX(256);
        kumulatifGreen.getViewport().setMaxY(highestKumulatifGreen);
        kumulatifGreen.getViewport().setXAxisBoundsManual(true);
        kumulatifGreen.getViewport().setYAxisBoundsManual(true);
        double highestKumulatifBlue = kumBluePoint.getHighestValueY();
        kumulatifBlue.getViewport().setMaxX(256);
        kumulatifBlue.getViewport().setMaxY(highestKumulatifBlue);
        kumulatifBlue.getViewport().setXAxisBoundsManual(true);
        kumulatifBlue.getViewport().setYAxisBoundsManual(true);
        ekualGray.addSeries(ekualGrayPoint);
        ekualRed.addSeries(ekualRedPoint);
        ekualGreen.addSeries(ekualGreenPoint);
        ekualBlue.addSeries(ekualBluePoint);
        kumulatifGray.addSeries(kumGrayPoint);
        kumulatifRed.addSeries(kumRedPoint);
        kumulatifGreen.addSeries(kumGreenPoint);
        kumulatifBlue.addSeries(kumBluePoint);
    }
}

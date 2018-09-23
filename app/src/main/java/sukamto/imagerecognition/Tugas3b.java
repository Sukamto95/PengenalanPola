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
import android.widget.SeekBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.HashMap;

public class Tugas3b extends AppCompatActivity {

    public HashMap<Integer,Integer> arr_red = new HashMap<Integer,Integer>();
    public HashMap<Integer,Integer> arr_green = new HashMap<Integer,Integer>();
    public HashMap<Integer,Integer> arr_blue = new HashMap<Integer,Integer>();
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    private static final int REQUEST_SELECT_IMAGE = 200;
    MainActivity main;
    ImageView image = null;
    ImageView transformImage = null;
    SeekBar sbARed;
    SeekBar sbBRed;
    SeekBar sbCRed;
    TextView sbARedText;
    TextView sbBRedText;
    TextView sbCRedText;
    SeekBar sbAGreen;
    SeekBar sbBGreen;
    SeekBar sbCGreen;
    TextView sbAGreenText;
    TextView sbBGreenText;
    TextView sbCGreenText;
    SeekBar sbABlue;
    SeekBar sbBBlue;
    SeekBar sbCBlue;
    TextView sbABlueText;
    TextView sbBBlueText;
    TextView sbCBlueText;
    int progARed;
    int progBRed;
    int progCRed;
    int progAGreen;
    int progBGreen;
    int progCGreen;
    int progABlue;
    int progBBlue;
    int progCBlue;
    int nPixelRed;
    int nPixelGreen;
    int nPixelBlue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tugas3b);
        main = new MainActivity();
        image = (ImageView) findViewById(R.id.imageOriColor);
        transformImage = (ImageView) findViewById(R.id.imageTransformColor);
        sbARed = findViewById(R.id.sbARed);
        sbBRed = findViewById(R.id.sbBRed);
        sbCRed = findViewById(R.id.sbCRed);
        sbARedText = findViewById(R.id.sbARedText);
        sbBRedText = findViewById(R.id.sbBRedText);
        sbCRedText = findViewById(R.id.sbCRedText);
        progARed = sbARed.getProgress();
        progBRed = sbBRed.getProgress();
        progCRed = sbCRed.getProgress();

        sbAGreen = findViewById(R.id.sbAGreen);
        sbBGreen = findViewById(R.id.sbBGreen);
        sbCGreen = findViewById(R.id.sbCGreen);
        sbAGreenText = findViewById(R.id.sbAGreenText);
        sbBGreenText = findViewById(R.id.sbBGreenText);
        sbCGreenText = findViewById(R.id.sbCGreenText);
        progAGreen = sbAGreen.getProgress();
        progBGreen = sbBGreen.getProgress();
        progCGreen = sbCGreen.getProgress();

        sbABlue = findViewById(R.id.sbABlue);
        sbBBlue = findViewById(R.id.sbBBlue);
        sbCBlue = findViewById(R.id.sbCBlue);
        sbABlueText = findViewById(R.id.sbABlueText);
        sbBBlueText = findViewById(R.id.sbBBlueText);
        sbCBlueText = findViewById(R.id.sbCBlueText);
        progABlue = sbABlue.getProgress();
        progBBlue = sbBBlue.getProgress();
        progCBlue = sbCBlue.getProgress();

        Bitmap bitmap = main.getImageBitmap();
        if(bitmap != null){
            image.setImageBitmap(bitmap);
        }
        if (sbARed != null) {
            sbARed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    double prog = progress;
                    sbARedText.setText("A value is "+prog/10);
                    progARed = progress;
                    countRed();
                    ekualisasi();
                    setGraph();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
        if (sbBRed != null) {
            sbBRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    double prog = progress;
                    sbBRedText.setText("B value is "+prog/10);
                    progBRed = progress;
                    countRed();
                    ekualisasi();
                    setGraph();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
        if (sbCRed != null) {
            sbCRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    double prog = progress;
                    sbCRedText.setText("C value is "+prog/10);
                    progCRed = progress;
                    countRed();
                    ekualisasi();
                    setGraph();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }

        if (sbAGreen != null) {
            sbAGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    double prog = progress;
                    sbAGreenText.setText("A value is "+prog/10);
                    progAGreen = progress;
                    countGreen();
                    ekualisasi();
                    setGraph();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
        if (sbBGreen != null) {
            sbBGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    double prog = progress;
                    sbBGreenText.setText("B value is "+prog/10);
                    progBGreen = progress;
                    countGreen();
                    ekualisasi();
                    setGraph();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
        if (sbCGreen != null) {
            sbCGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    double prog = progress;
                    sbCGreenText.setText("C value is "+prog/10);
                    progCGreen = progress;
                    countGreen();
                    ekualisasi();
                    setGraph();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }

        if (sbABlue != null) {
            sbABlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    double prog = progress;
                    sbABlueText.setText("A value is "+prog/10);
                    progABlue = progress;
                    countBlue();
                    ekualisasi();
                    setGraph();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
        if (sbBBlue != null) {
            sbBBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    double prog = progress;
                    sbBBlueText.setText("B value is "+prog/10);
                    progBBlue = progress;
                    countBlue();
                    ekualisasi();
                    setGraph();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
        if (sbCBlue != null) {
            sbCBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    double prog = progress;
                    sbCBlueText.setText("C value is "+prog/10);
                    progCBlue = progress;
                    countBlue();
                    ekualisasi();
                    setGraph();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
    }

    public void addPicture(View view){
        final CharSequence[] options = { "Take Picture", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(Tugas3b.this);
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
            if (ActivityCompat.checkSelfPermission(Tugas3b.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Tugas3b.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_SELECT_IMAGE);
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
                countRed();
                countGreen();
                countBlue();
                ekualisasi();
                setGraph();
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
            countRed();
            countGreen();
            countBlue();
            ekualisasi();
            setGraph();
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

    public void setGraph(){
        GraphView redGraph = (GraphView) findViewById(R.id.graphRed);
        GraphView greenGraph = (GraphView) findViewById(R.id.graphGreen);
        GraphView blueGraph = (GraphView) findViewById(R.id.graphBlue);
        redGraph.removeAllSeries();
        greenGraph.removeAllSeries();
        blueGraph.removeAllSeries();
        DataPoint[] redData = new DataPoint[256];
        DataPoint[] greenData = new DataPoint[256];
        DataPoint[] blueData = new DataPoint[256];
        for(int i=0;i<256;i++){
            redData[i] = new DataPoint(i,arr_red.get(i));
            greenData[i] = new DataPoint(i,arr_green.get(i));
            blueData[i] = new DataPoint(i,arr_blue.get(i));
        }
        LineGraphSeries<DataPoint> redPoint= new LineGraphSeries<>(redData);
        redPoint.setColor(Color.RED);
        LineGraphSeries<DataPoint> greenPoint= new LineGraphSeries<>(greenData);
        greenPoint.setColor(Color.GREEN);
        LineGraphSeries<DataPoint> bluePoint= new LineGraphSeries<>(blueData);
        bluePoint.setColor(Color.BLUE);
        double highestRed = redPoint.getHighestValueY();
        double highestGreen = greenPoint.getHighestValueY();
        double highestBlue = bluePoint.getHighestValueY();
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
        redGraph.setHorizontalScrollBarEnabled(true);
        greenGraph.setHorizontalScrollBarEnabled(true);
        blueGraph.setHorizontalScrollBarEnabled(true);
        redGraph.addSeries(redPoint);
        greenGraph.addSeries(greenPoint);
        blueGraph.addSeries(bluePoint);

        GraphView ekualRed = (GraphView) findViewById(R.id.ekualRed);
        GraphView ekualGreen = (GraphView) findViewById(R.id.ekualGreen);
        GraphView ekualBlue = (GraphView) findViewById(R.id.ekualBlue);
        ekualRed.removeAllSeries();
        ekualGreen.removeAllSeries();
        ekualBlue.removeAllSeries();
        DataPoint[] ekualRedData = new DataPoint[256];
        DataPoint[] ekualGreenData = new DataPoint[256];
        DataPoint[] ekualBlueData = new DataPoint[256];
        for(int i=0;i<256;i++){
            ekualRedData[i] = new DataPoint(i,main.EKUAL_RED.get(i));
            ekualGreenData[i] = new DataPoint(i,main.EKUAL_GREEN.get(i));
            ekualBlueData[i] = new DataPoint(i,main.EKUAL_BLUE.get(i));
        }
        LineGraphSeries<DataPoint> ekualRedPoint= new LineGraphSeries<>(ekualRedData);
        LineGraphSeries<DataPoint> ekualGreenPoint= new LineGraphSeries<>(ekualGreenData);
        LineGraphSeries<DataPoint> ekualBluePoint= new LineGraphSeries<>(ekualBlueData);

        ekualRedPoint.setColor(Color.RED);
        ekualGreenPoint.setColor(Color.GREEN);
        ekualBluePoint.setColor(Color.BLUE);
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
        ekualRed.addSeries(ekualRedPoint);
        ekualGreen.addSeries(ekualGreenPoint);
        ekualBlue.addSeries(ekualBluePoint);
    }

    public void countRed(){
        for(int i=0;i<=255;i++){
            arr_red.put(i, 0);
        }
        nPixelRed = 0;
        int xar = 0;
        int yar = (500*sbARed.getProgress())/10;
        int xbr = (255*sbBRed.getProgress())/10;
        int ybr = 500;
        int xcr= 255;
        int ycr = (500*sbCRed.getProgress())/10;
        for(int x = 0 ; x < 256 ; x++){
            int y = 0;
            if(x < xbr){
                if(x == 0){
                    arr_red.put(x,yar);
                    nPixelRed += yar;
                } else{
                    y = yar + (((x - xar)*(ybr - yar))/(xbr - xar));
                    arr_red.put(x,y);
                    nPixelRed += y;
                }
            } else if(x == xbr){
                arr_red.put(x,ybr);
                nPixelRed += ybr;
            } else if(x > xbr){
                if(x == 255){
                    arr_red.put(x,ycr);
                    nPixelRed += ycr;
                } else{
                    y = ybr + (((x - xbr)*(ycr - ybr))/(xcr - xbr));
                    arr_red.put(x,y);
                    nPixelRed += y;
                }
            }
        }
    }

    public void countGreen(){
        for(int i=0;i<=255;i++){
            arr_green.put(i, 0);
        }
        nPixelGreen = 0;
        int xag = 0;
        int yag = (500*sbAGreen.getProgress())/10;
        int xbg = (255*sbBGreen.getProgress())/10;
        int ybg = 500;
        int xcg= 255;
        int ycg = (500*sbCGreen.getProgress())/10;
        for(int x = 0 ; x < 256 ; x++){
            int y = 0;
            if(x < xbg){
                if(x == 0){
                    arr_green.put(x,yag);
                    nPixelGreen += yag;
                } else{
                    y = yag + (((x - xag)*(ybg - yag))/(xbg - xag));
                    arr_green.put(x,y);
                    nPixelGreen += y;
                }
            } else if(x == xbg){
                arr_green.put(x,ybg);
                nPixelGreen += ybg;
            } else if(x > xbg){
                if(x == 255){
                    arr_green.put(x,ycg);
                    nPixelGreen += ycg;
                } else{
                    y = ybg + (((x - xbg)*(ycg - ybg))/(xcg - xbg));
                    arr_green.put(x,y);
                    nPixelGreen += y;
                }
            }
        }
    }

    public void countBlue(){
        for(int i=0;i<=255;i++){
            arr_blue.put(i, 0);
        }
        nPixelBlue = 0;
        int xab = 0;
        int yab = (500*sbABlue.getProgress())/10;
        int xbb = (255*sbBBlue.getProgress())/10;
        int ybb = 500;
        int xcb= 255;
        int ycb = (500*sbCBlue.getProgress())/10;
        for(int x = 0 ; x < 256 ; x++){
            int y = 0;
            if(x < xbb){
                if(x == 0){
                    arr_blue.put(x,yab);
                    nPixelBlue += yab;
                } else{
                    y = yab + (((x - xab)*(ybb - yab))/(xbb - xab));
                    arr_blue.put(x,y);
                    nPixelBlue += y;
                }
            } else if(x == xbb){
                arr_blue.put(x,ybb);
                nPixelBlue += ybb;
            } else if(x > xbb){
                if(x == 255){
                    arr_blue.put(x,ycb);
                    nPixelBlue += ycb;
                } else{
                    y = ybb + (((x - xbb)*(ycb - ybb))/(xcb - xbb));
                    arr_blue.put(x,y);
                    nPixelBlue += y;
                }
            }
        }
    }

    public void ekualisasi() {
        for (int i = 0; i <= 255; i++) {
            main.KUM_RED.put(i, (double) 0);
            main.KUM_GREEN.put(i, (double) 0);
            main.KUM_BLUE.put(i, (double) 0);
            main.KUM_GRAY.put(i, (double) 0);
            main.EKUAL_RED.put(i, 0);
            main.EKUAL_GREEN.put(i, 0);
            main.EKUAL_BLUE.put(i, 0);
            main.EKUAL_GRAY.put(i, 0);
        }
        Bitmap imageBitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();
        int[] pix = new int[width * height];
        int[] refRed = new int[256];
        int[] refGreen = new int[256];
        int[] refBlue = new int[256];
        imageBitmap.getPixels(pix, 0, width, 0, 0, width, height);
        main.recKumulatif(arr_red, arr_green, arr_blue,0, 1);
        for (int i = 0; i < 256; i++) {
            int kRed = (int) Math.round((main.KUM_RED.get(i) * (Math.pow(2, 8) - 1)) / (nPixelRed));
            int ekualRedBefore = main.EKUAL_RED.get(kRed);
            main.EKUAL_RED.put(kRed, arr_red.get(i) + ekualRedBefore);
            refRed[i] = kRed;

            int kGreen = (int) Math.round((main.KUM_GREEN.get(i) * (Math.pow(2, 8) - 1)) / (nPixelGreen));
            int ekualGreenBefore = main.EKUAL_GREEN.get(kGreen);
            main.EKUAL_GREEN.put(kGreen, arr_green.get(i) + ekualGreenBefore);
            refGreen[i] = kGreen;

            int kBlue = (int) Math.round((main.KUM_BLUE.get(i) * (Math.pow(2, 8) - 1)) / (nPixelBlue));
            int ekualBlueBefore = main.EKUAL_BLUE.get(kBlue);
            main.EKUAL_BLUE.put(kBlue, arr_blue.get(i) + ekualBlueBefore);
            refBlue[i] = kBlue;
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                int pixel = imageBitmap.getPixel(y, x);
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = (pixel) & 0xff;
                bitmap.setPixel(y, x, Color.rgb(refRed[r], refGreen[g], refBlue[b]));
            }
        }
        transformImage.setImageBitmap(bitmap);
    }
}

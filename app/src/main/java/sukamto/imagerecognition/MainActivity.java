package sukamto.imagerecognition;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public static HashMap<Integer,Integer> ARR_RED = new HashMap<Integer,Integer>();
    public static HashMap<Integer,Integer> ARR_GREEN = new HashMap<Integer,Integer>();
    public static HashMap<Integer,Integer> ARR_BLUE = new HashMap<Integer,Integer>();
    public static HashMap<Integer,Integer> ARR_GRAY = new HashMap<Integer,Integer>();
    public static HashMap<Integer,Double> KUM_RED = new HashMap<Integer,Double>();
    public static HashMap<Integer,Double> KUM_GREEN = new HashMap<Integer,Double>();
    public static HashMap<Integer,Double> KUM_BLUE = new HashMap<Integer,Double>();
    public static HashMap<Integer,Double> KUM_GRAY = new HashMap<Integer,Double>();
    public static HashMap<Integer,Integer> EKUAL_RED = new HashMap<Integer,Integer>();
    public static HashMap<Integer,Integer> EKUAL_GREEN = new HashMap<Integer,Integer>();
    public static HashMap<Integer,Integer> EKUAL_BLUE = new HashMap<>();
    public static HashMap<Integer,Integer> EKUAL_GRAY = new HashMap<Integer,Integer>();
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    private static final int REQUEST_SELECT_IMAGE = 200;
    public Bitmap imageBitmap = null;
    public Bitmap grayBitmap = null;
    public Bitmap trBitmap = null;
    public Bitmap trGrayBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void tugas1(View view){
        Intent tugas1 = new Intent(MainActivity.this,Tugas1.class);
//        if(imageBitmap != null){
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] byteArray = stream.toByteArray();
//            tugas1.putExtra("isOriNull",0);
//            tugas1.putExtra("bitmap", byteArray);
//        } else{
//            tugas1.putExtra("isOriNull", 1);
//        }
        startActivity(tugas1);
    }

    public void tugas2(View view){
        Intent tugas2 = new Intent(MainActivity.this,Tugas2.class);
        startActivity(tugas2);
    }

    public void tugas3a(View view){
        Intent tugas3a = new Intent(MainActivity.this,Tugas3a.class);
        startActivity(tugas3a);
    }

    public void tugas3b(View view){
        Intent tugas3b = new Intent(MainActivity.this,Tugas3b.class);
        startActivity(tugas3b);
    }

    public void tugas3c(View view){
        Intent tugas3c = new Intent(MainActivity.this,Tugas3c.class);
        startActivity(tugas3c);
    }

    public void tugas4(View view){
        Intent tugas4 = new Intent(MainActivity.this,Tugas4.class);
        startActivity(tugas4);
    }

    public void countColor(){
        for(int i=0;i<=255;i++){
            ARR_RED.put(i,0);
            ARR_GREEN.put(i,0);
            ARR_BLUE.put(i,0);
            ARR_GRAY.put(i,0);
        }
        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();
        android.util.Log.e("image width",width+"");
        android.util.Log.e("image height",height+"");
        int[] pix = new int[width * height];
        android.util.Log.e("pix length",pix.length+"");
        imageBitmap.getPixels(pix, 0, width, 0, 0, width, height);
        for (int i = 0; i < pix.length; i++) {
            int r = (pix[i] >> 16) & 0xff;
            int g = (pix[i] >> 8) & 0xff;
            int b = (pix[i]) & 0xff;
            int gray = (r+g+b)/3;

            int rBefore = ARR_RED.get(r);
            int gBefore = ARR_GREEN.get(g);
            int bBefore = ARR_BLUE.get(b);
            int grayBefore = ARR_GRAY.get(gray);

            ARR_RED.put(r,rBefore+1);
            ARR_GREEN.put(g,gBefore+1);
            ARR_BLUE.put(b,bBefore+1);
            ARR_GRAY.put(gray,grayBefore+1);

            pix[i] = 0xff000000 | (r << 16) | (g << 8) | b;
        }
    }

    public void ekualisasi(double w){
        for(int i=0;i<=255;i++){
            KUM_RED.put(i, (double)0);
            KUM_GREEN.put(i,(double)0);
            KUM_BLUE.put(i,(double)0);
            KUM_GRAY.put(i,(double)0);
            EKUAL_RED.put(i,0);
            EKUAL_GREEN.put(i,0);
            EKUAL_BLUE.put(i,0);
            EKUAL_GRAY.put(i,0);
        }
        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();
        android.util.Log.e("image width",width+"");
        android.util.Log.e("image height",height+"");
        int[] pix = new int[width * height];
        int[] refGray = new int[256];
        int[] refRed = new int[256];
        int[] refGreen = new int[256];
        int[] refBlue = new int[256];
        imageBitmap.getPixels(pix, 0, width, 0, 0, width, height);
        recKumulatif(0, w);
        for(int i = 0 ; i < 256 ; i++){
            int kGray = (int) Math.round((KUM_GRAY.get(i)*(Math.pow(2,8)-1))/(width*height));
            int ekualGrayBefore = EKUAL_GRAY.get(kGray);
            EKUAL_GRAY.put(kGray,ARR_GRAY.get(i)+ekualGrayBefore);
            refGray[i] = kGray;

            int kRed = (int) Math.round((KUM_RED.get(i)*(Math.pow(2,8)-1))/(width*height));
            int ekualRedBefore = EKUAL_RED.get(kRed);
            EKUAL_RED.put(kRed,ARR_RED.get(i)+ekualRedBefore);
            refRed[i] = kRed;

            int kGreen = (int) Math.round((KUM_GREEN.get(i)*(Math.pow(2,8)-1))/(width*height));
            int ekualGreenBefore = EKUAL_GREEN.get(kGreen);
            EKUAL_GREEN.put(kGreen,ARR_GREEN.get(i)+ekualGreenBefore);
            refGreen[i] = kGreen;

            int kBlue = (int) Math.round((KUM_BLUE.get(i)*(Math.pow(2,8)-1))/(width*height));
            int ekualBlueBefore = EKUAL_BLUE.get(kBlue);
            EKUAL_BLUE.put(kBlue,ARR_BLUE.get(i)+ekualBlueBefore);
            refBlue[i] = kBlue;
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Bitmap gBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Bitmap grBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                int pixel = imageBitmap.getPixel(y,x);
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = (pixel) & 0xff;
                int gray = (r+g+b)/3;
                bitmap.setPixel(y,x,Color.rgb(refRed[r], refGreen[g], refBlue[b]));
                gBitmap.setPixel(y,x,Color.rgb(gray, gray, gray));
                grBitmap.setPixel(y,x,Color.rgb(refGray[gray], refGray[gray], refGray[gray]));
            }
        }
        grayBitmap = gBitmap;
        trBitmap = bitmap;
        trGrayBitmap = grBitmap;
//        int[] grayPix = new int[width * height];
//        int[] trGrayPix = new int[width * height];
//        int[] trRedPix = new int[width * height];
//        int[] trGreenPix = new int[width * height];
//        int[] trBluePix = new int[width * height];
//        for (int i = 0; i < pix.length; i++) {
//            int r = (pix[i] >> 16) & 0xff;
//            int g = (pix[i] >> 8) & 0xff;
//            int b = (pix[i]) & 0xff;
//            int gray = (r+g+b)/3;
//            grayPix[i] = gray;
//            trGrayPix[i] = refGray[gray];
//            trRedPix[i] = refRed[r];
//            trGreenPix[i] = refGreen[g];
//            trBluePix[i] = refBlue[b];
//        }
//        dataToBitmap(grayPix,width,height);
//        dataToBitmap(trRedPix,trGreenPix,trBluePix,trGrayPix,width,height);
    }

    public void recKumulatif(int x, double w){
        if(x == 0){
            KUM_GRAY.put(x,(double)ARR_GRAY.get(x));
            KUM_RED.put(x,(double)ARR_RED.get(x));
            KUM_GREEN.put(x,(double)ARR_GREEN.get(x));
            KUM_BLUE.put(x,(double)ARR_BLUE.get(x));
            recKumulatif(x+1, w);
        } else if(x == 255){
            KUM_GRAY.put(x, Math.round(w*ARR_GRAY.get(x))+KUM_GRAY.get(x-1));
            KUM_RED.put(x, Math.round(w*ARR_RED.get(x))+KUM_RED.get(x-1));
            KUM_GREEN.put(x, Math.round(w*ARR_GREEN.get(x))+KUM_GREEN.get(x-1));
            KUM_BLUE.put(x, Math.round(w*ARR_BLUE.get(x))+KUM_BLUE.get(x-1));
            return;
        } else{
            KUM_GRAY.put(x, Math.round(w*ARR_GRAY.get(x))+KUM_GRAY.get(x-1));
            KUM_RED.put(x, Math.round(w*ARR_RED.get(x))+KUM_RED.get(x-1));
            KUM_GREEN.put(x, Math.round(w*ARR_GREEN.get(x))+KUM_GREEN.get(x-1));
            KUM_BLUE.put(x, Math.round(w*ARR_BLUE.get(x))+KUM_BLUE.get(x-1));
            recKumulatif(x+1, w);
        }
    }

    public void recKumulatif(HashMap<Integer,Integer> arr_red, HashMap<Integer,Integer> arr_green,
                             HashMap<Integer,Integer> arr_blue, int x, double w){
        if(x == 0){
            KUM_RED.put(x,(double)arr_red.get(x));
            KUM_GREEN.put(x,(double)arr_green.get(x));
            KUM_BLUE.put(x,(double)arr_blue.get(x));
            recKumulatif(arr_red, arr_green, arr_blue,x+1, w);
        } else if(x == 255){
            KUM_RED.put(x, Math.round(w*arr_red.get(x))+KUM_RED.get(x-1));
            KUM_GREEN.put(x, Math.round(w*arr_green.get(x))+KUM_GREEN.get(x-1));
            KUM_BLUE.put(x, Math.round(w*arr_blue.get(x))+KUM_BLUE.get(x-1));
            return;
        } else{
            KUM_RED.put(x, Math.round(w*arr_red.get(x))+KUM_RED.get(x-1));
            KUM_GREEN.put(x, Math.round(w*arr_green.get(x))+KUM_GREEN.get(x-1));
            KUM_BLUE.put(x, Math.round(w*arr_blue.get(x))+KUM_BLUE.get(x-1));
            recKumulatif(arr_red, arr_green, arr_blue,x+1, w);
        }
    }

//    public void dataToBitmap(int[] data, int width, int height) {
//        int index=0;
//        Bitmap gBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                index=y*width+x;
//                int c = data[index];
//                gBitmap.setPixel(x,y,Color.rgb(c, c, c));
//            }
//        }
//        grayBitmap = gBitmap;
//    }
//
//    public void dataToBitmap(int[] data, int[] data2, int[] data3, int[] dataGray, int width, int height) {
//        int index=0;
//        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                index=y*width+x;
//                int r = data[index];
//                int g = data2[index];
//                int b = data3[index];
//                int gray = dataGray[index];
//                bitmap.setPixel(x,y,Color.rgb(r, g, b));
//                grayBitmap.setPixel(x,y,Color.rgb(gray, gray, gray));
//            }
//        }
//        trBitmap = bitmap;
//        trGrayBitmap = grayBitmap;
//    }

    public void setBitmap(Bitmap bitmap){
        imageBitmap = bitmap;
    }

    public void setGrayBitmap(Bitmap bitmap){
        grayBitmap = bitmap;
    }

    public void setTrBitmap(Bitmap bitmap){
        trBitmap = bitmap;
    }

    public void setTrGrayBitmap(Bitmap bitmap){
        trGrayBitmap = bitmap;
    }

    public Bitmap getImageBitmap(){
        return imageBitmap;
    }

    public Bitmap getGrayBitmap(){
        return grayBitmap;
    }

    public Bitmap getTrBitmap(){
        return trBitmap;
    }

    public Bitmap getTrGrayBitmap(){
        return trGrayBitmap;
    }
}

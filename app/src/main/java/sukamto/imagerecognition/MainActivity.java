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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void tugas1(View view){
        Intent tugas1 = new Intent(MainActivity.this,Tugas1.class);
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

    public void tugas4b(View view){
        Intent tugas4b = new Intent(MainActivity.this,Tugas4b.class);
        startActivity(tugas4b);
    }

    public void tugas5a(View view){
        Intent tugas5a = new Intent(MainActivity.this,Tugas5a.class);
        startActivity(tugas5a);
    }

    public void tugas6(View view){
        Intent tugas6 = new Intent(MainActivity.this,Tugas6.class);
        startActivity(tugas6);
    }
}

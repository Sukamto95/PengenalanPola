package sukamto.imagerecognition;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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


import sukamto.imagerecognition.model.MainModel;

public class Tugas5a extends AppCompatActivity {
    ImageView image = null;
    ImageView skeletonImage = null;
    ImageView zhangSuenImage = null;
    //TextView resultText, skeletonText, zsCodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tugas5a);
        image = (ImageView) findViewById(R.id.imageOriColor);
        skeletonImage = (ImageView) findViewById(R.id.imageSkeleton);
        zhangSuenImage = (ImageView) findViewById(R.id.imageZhangSuen);
//        resultText = findViewById(R.id.resultText);
//        skeletonText = findViewById(R.id.skeletonText);
//        zsCodeText = findViewById(R.id.zsCodeText);
        Bitmap bitmap = MainModel.getImageBitmap();
        if(bitmap != null){
            image.setImageBitmap(bitmap);
        }

    }

    public void addPicture(View view){
        final CharSequence[] options = { "Take Picture", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(Tugas5a.this);
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
            if (ActivityCompat.checkSelfPermission(Tugas5a.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Tugas5a.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainModel.REQUEST_SELECT_IMAGE);
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
                Bitmap binaryBitmap = MainModel.getBinaryImage(imageBitmap,128);
                image.setImageBitmap(binaryBitmap);
                MainModel.setBitmap(binaryBitmap);
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
            Bitmap imageBitmap = MainModel.getScaledBitmap(bmp,0);
            Bitmap binaryBitmap = MainModel.getBinaryImage(imageBitmap,128);
            image.setImageBitmap(binaryBitmap);
            MainModel.setBitmap(binaryBitmap);
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

    public void manualThinning(View view){
        Bitmap binaryBitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        String[] result = MainModel.checkSkeletonManual(binaryBitmap);
        String skeletonCode = result[1];
        Bitmap bitmapResult = MainModel.getSkeletonBitmap(binaryBitmap,skeletonCode);
        skeletonImage.setImageBitmap(bitmapResult);
    }

    public void zhangSuenThinning(View view){
        Bitmap binaryBitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        Bitmap bitmapResult = MainModel.getZhangSuenBitmap(binaryBitmap);
        zhangSuenImage.setImageBitmap(bitmapResult);
    }
}

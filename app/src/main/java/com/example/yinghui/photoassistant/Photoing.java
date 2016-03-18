package com.example.yinghui.photoassistant;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Photoing extends AppCompatActivity implements View.OnClickListener {
    private Button addButton;
    private Button otherButton;
    private File tmpFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String sdStatus = Environment.getExternalStorageState();
        if(sdStatus.equals(Environment.MEDIA_MOUNTED)){
            try{
                File fileFolder = new File(Environment.getExternalStorageDirectory() + "/myImage/");
                if(!fileFolder.exists()) {
                    fileFolder.mkdir();
                }
                String tmpName = "tmpPhoto.bmp";
                tmpFile = new File(fileFolder, tmpName);
                Uri u = Uri.fromFile(tmpFile);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,u);
                startActivityForResult(intent, 1);
            } catch (ActivityNotFoundException e){
                Toast.makeText(getApplicationContext(), "没有找到存储目录", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(),"没有存储卡",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = null;
            try {
                Uri u = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(),
                        tmpFile.getAbsolutePath(),null,null));
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(u),null,options);
                //bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),u);
            } catch (IOException e){
                Toast.makeText(getApplicationContext(),"error: IOException",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            setContentView(R.layout.activity_photoing);
            addButton = (Button)findViewById(R.id.add_class);
            otherButton = (Button)findViewById(R.id.other_class);
            addButton.setOnClickListener(this);
            otherButton.setOnClickListener(this);

            ((ImageView) findViewById(R.id.view_photo)).setImageBitmap(bitmap);
        }
        else {
            Intent intent = new Intent(Photoing.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photoing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.add_class:
                addClass();
                break;
            case R.id.other_class:
                OtherPopWindow otherPopWindow = new OtherPopWindow(Photoing.this);
                otherPopWindow.showPopupWindow(findViewById(R.id.actionbar));
                break;
            default:
                break;
        }
    }

    public void addClass() {
        EditText editText = (EditText)findViewById(R.id.new_class);
        String str = editText.getText().toString();
        if(str != "") {
            String sdFile = Environment.getExternalStorageDirectory().getAbsolutePath();
            File filePath = new File(sdFile+"/myImage/"+str+"/");
            if(!filePath.exists()){
                filePath.mkdir();
            }
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 20;
            Bitmap src = BitmapFactory.decodeFile(tmpFile.getAbsolutePath(), options);

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date();
            String fileName = format.format(date) + ".jpg";
            tmpFile.renameTo(new File(filePath.getAbsolutePath()+"/"+fileName));

            File tmpBmpDir = new File(sdFile+"/myImageTmp/"+str+"/");
            if(!tmpBmpDir.exists()){
                tmpBmpDir.mkdir();
            }
            File tmpBmpFile = new File(tmpBmpDir,fileName);
            Log.e("TAG", tmpBmpFile.getAbsolutePath());
            try {
                FileOutputStream out = new FileOutputStream(tmpBmpFile);
                src.compress(Bitmap.CompressFormat.JPEG,100,out);
                out.flush();
                out.close();
            } catch (FileNotFoundException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
//            FileOutputStream b = null;
//            Bitmap bitmap = null;
//            try {
//                Uri u = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(),
//                        tmpFile.getAbsolutePath(), null, null));
//                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), u);
//                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
//                Date date = new Date();
//                String fileName = format.format(date) + ".jpg";
//                File photoFile = new File(filePath, fileName);
//                b = new FileOutputStream(photoFile);
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);
//                b.flush();
//                b.close();
//                Toast.makeText(getApplicationContext(), "已加入分类: "+str, Toast.LENGTH_SHORT).show();
//                bitmap.recycle();
//            } catch (FileNotFoundException e) {
//                Toast.makeText(getApplicationContext(),"error: FileNotFoundException",Toast.LENGTH_LONG).show();
//                e.printStackTrace();
//            } catch (IOException e){
//                Toast.makeText(getApplicationContext(),"error: IOException",Toast.LENGTH_LONG).show();
//                e.printStackTrace();
//            }
            this.finish();
            //Intent intent = new Intent(Photoing.this, MainActivity.class);
            //startActivity(intent);
        }
    }
}

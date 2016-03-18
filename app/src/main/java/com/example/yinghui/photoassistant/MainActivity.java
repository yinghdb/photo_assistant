package com.example.yinghui.photoassistant;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //int classNum; //分类的数目
    //File fileFolder;
    private static final String TAG = "Socket_Android";
    private ListView mListView;
    private ListViewAdapter mListViewAdapter;
    private ArrayList<ListViewData> mArrayList;
    private String IP;
    private int totalImage;
    private int uploadedImage;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        Button button1 = (Button)this.findViewById(R.id.button_taking_photo);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Photoing.class);
                startActivity(intent);
            }
        });
        Button button2 = (Button)this.findViewById(R.id.button_upload);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textDialog();
            }
        });

        init();

//        GridView gridView = (GridView)findViewById(R.id.gridview);
//        gridView.setAdapter(new ImageAdapter(this, 1));
//        //单击GridView元素的响应
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this,mThumbIds[position],Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        init();
    }

    private void textDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialogview, null);
        builder.setView(layout);
        builder.setTitle("请输入IP地址").setIcon(R.mipmap.ic_launcher);
        final EditText editText = (EditText) layout.findViewById(R.id.dialogtxt);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                IP = editText.getText().toString();
                progressDialog();
            }
        });

        builder.show();
    }

    private void progressDialog() {
        mProgress = new ProgressDialog(MainActivity.this);
        mProgress.setIcon(R.mipmap.ic_launcher);
        mProgress.setTitle("正在上传");
        mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgress.show();
        uploadedImage = 0;

        new Thread(new Runnable() {
            @Override
            public void run() {
                upload();
            }
        }).start();
    }

    private void upload() {
        String sdFile = Environment.getExternalStorageDirectory().toString();
        File fileFolder = new File(sdFile,"myImage");
        if(!fileFolder.exists()){
            fileFolder.mkdir();
        }
        String type = "";

        File[] fileList = fileFolder.listFiles();

        for(int i=0; i<fileList.length; i++){
            if(fileList[i].isDirectory()){
                type = fileList[i].getName();
                File[] imageList = fileList[i].listFiles();
                for(int j=0; j<imageList.length; j++){
                    if(imageList[j].getName().endsWith(".jpg") || imageList[j].getName().endsWith(".bmp")){
                        oneImage_upload(type, imageList[j]);
                        uploadedImage++;
                    }
                }
                mProgress.setProgress(uploadedImage*100/totalImage);
            }
        }

        mProgress.dismiss();


        /*setTitle("测试Socket连接");
        Socket socket = null;
        try {
            InetAddress serverAddr = InetAddress.getByName(IP);// TCPServer.SERVERIP
            Log.d("TCP", "C: Connecting...");

            // 应用Server的IP和端口建立Socket对象
            socket = new Socket(serverAddr, 51706);
            String message = "---Test_Socket_Android---";

            Log.d("TCP", "C: Sending: '" + message + "'");

            // 将信息通过这个对象来发送给Server
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())),
                    true);

            // 把用户输入的内容发送给server
            String toServer = "Hello World!";
            Log.d(TAG, "To server:'" + toServer + "'");
            out.println(toServer);
            out.flush();


            // 接收服务器信息
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            // 得到服务器信息
            String msg = in.readLine();
            Log.d(TAG, "From server:'" + msg + "'");
        } catch (UnknownHostException e) {
            Log.e(TAG, "unkown server!");
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        */
    }

    private void oneImage_upload(String type, File image) {
        Socket socket = null;
        DataOutputStream dos;
        FileInputStream fis;
        try {
            /* 指定Server的IP地址，此地址为局域网地址，如果是使用WIFI上网，则为PC机的WIFI IP地址
             * 在ipconfig查看到的IP地址如下：
             * Ethernet adapter 无线网络连接:
             * Connection-specific DNS Suffix  . : IP Address. . . . . . . . . . . . : 192.168.1.100
             */
            InetAddress serverAddr = InetAddress.getByName(IP);// TCPServer.SERVERIP
            Log.d("TCP", "C: Connecting...");

            // 应用Server的IP和端口建立Socket对象
            socket = new Socket(serverAddr, 51706);

            dos = new DataOutputStream(socket.getOutputStream());
            fis = new FileInputStream(image);

            dos.writeUTF(type);
            dos.flush();

            dos.writeUTF(image.getName());
            dos.flush();

            byte[] sendBytes = new byte[8*1024];
            int length;
            while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
                dos.write(sendBytes, 0, length);
                dos.flush();
            }
            dos.close();

            // 接收服务器信息
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            // 得到服务器信息
            Boolean res = Boolean.parseBoolean(in.readLine());
            if(!res) {
                Toast.makeText(getApplicationContext(),"上传失败",Toast.LENGTH_LONG).show();
            }

            fis.close();
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        mListView = (ListView) findViewById(R.id.listview);
        initDate();
        mListViewAdapter = new ListViewAdapter(this, mArrayList);
        mListView.setAdapter(mListViewAdapter);
    }

    private void initDate() {
        String sdFile = Environment.getExternalStorageDirectory().getAbsolutePath();
        File fileFolder = new File(sdFile + "/myImageTmp/");
        if(!fileFolder.exists()){
            fileFolder.mkdir();
        }
        File[] tmpFileList = fileFolder.listFiles();

        mArrayList = new ArrayList<>();
        ListViewData listViewData;
        int reqWidth, reqHeight;
        reqHeight = reqWidth = 250;
        totalImage = 0;

        for(int i=0; i<tmpFileList.length; i++) {
            if(tmpFileList[i].isDirectory() && tmpFileList[i].listFiles().length != 0) {
                listViewData = new ListViewData();
                listViewData.arrayListForGridView = new ArrayList<>();
                for(int j=0; j<tmpFileList[i].listFiles().length; j++){
                    listViewData.className = tmpFileList[i].getName();
                    File jpgFile = tmpFileList[i].listFiles()[j];
                    if(jpgFile.getName().endsWith(".bmp") || jpgFile.getName().endsWith(".jpg")){
                        GridViewData gridViewData = new GridViewData();
                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        Bitmap src = BitmapFactory.decodeFile(jpgFile.getAbsolutePath(),options);
                        Bitmap dst = createScaleBitmap(src, reqWidth, reqHeight);
                        gridViewData.bitmap = dst;
                        gridViewData.tmpFilePath=jpgFile;
                        gridViewData.filePath=new File(sdFile+"/myImage/"+tmpFileList[i].getName()
                                                    +"/"+jpgFile.getName());
                        listViewData.arrayListForGridView.add(gridViewData);
                        totalImage++;
                    }
                }
                mArrayList.add(listViewData);
            }
        }

    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height>reqHeight || width>reqWidth){
            final int halfHeight = height/2;
            final int halfWidth = width/2;
            while((halfHeight/inSampleSize)>reqHeight
                    &&(halfWidth/inSampleSize)>reqWidth) {
                inSampleSize *= 2;
            }
        }
        //Log.e("msg: ", "inSampleSize " + inSampleSize);
        return inSampleSize;
    }

    private Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight) {
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if(src != dst){
            src.recycle();
        }
        return dst;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}

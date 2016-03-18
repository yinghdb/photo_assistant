package com.example.yinghui.photoassistant;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yinghui on 15/12/2.
 */
public class OtherPopWindow extends PopupWindow {

    public OtherPopWindow(final Activity context) {

        int[] colorTable = {
                Color.rgb(23,50,7), Color.rgb(153,77,82), Color.rgb(217,116,43),
                Color.rgb(230,180,80), Color.rgb(217,230,195), Color.rgb(78,29,76),
                Color.rgb(119,52,96), Color.rgb(227,179,37), Color.rgb(20,0,28)
        };
        //获取屏幕大小，以合理设定 按钮 大小及位置
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int h = dm.heightPixels;
        int w = dm.widthPixels;
        //自定义layout组件
        RelativeLayout layout = new RelativeLayout(context);
        //layout.setBackgroundColor(Color.BLACK);
        //获取文件夹列表
        final String sdFile = Environment.getExternalStorageDirectory().getAbsolutePath();
        File fileFolder = new File(sdFile + "/myImageTmp/");
        File[] fileList = fileFolder.listFiles();
        int fileDirNum = 0;
        for(int i=0; i<fileList.length; i++){
            if(fileList[i].isDirectory())
                fileDirNum++;
        }
        //这里创建fileDirNum个按钮
        int btnCount = fileDirNum;
        int count = 0;
        Button btn[] = new Button[btnCount];
        for(int i=0; i<fileList.length; i++){
            if(fileList[i].isDirectory()) {
                btn[count] = new Button(context);
                btn[count].setId(2000 + count);
                btn[count].setText(fileList[i].getName());
                btn[count].setTextSize(15);
                btn[count].setTextColor(Color.WHITE);
                btn[count].setBackgroundColor(colorTable[count % 9]);
                //设置按钮的宽和高
                RelativeLayout.LayoutParams btParams = new RelativeLayout.LayoutParams((w - 10) / 2, 112);
                btParams.leftMargin = (w - 10) / 2;
                btParams.topMargin = 5 + 120 * count;
                layout.addView(btn[count], btParams);
                count ++;
            }
        }

        // 设置SelectPicPopupWindow的View
        this.setContentView(layout);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(5+120*btnCount);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果

        this.setAnimationStyle(R.style.AnimationPreview);

        //下面对按钮设置监听
        for(int k=0; k<btn.length; k++) {
            btn[k].setTag(btn[k].getText());  //为按钮设置一个标记，来传递信息
            btn[k].setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String str =(String)v.getTag(); //这里的str不能在外部定义，因为内部类的关系
                    File fileFolder = new File(Environment.getExternalStorageDirectory() + "/myImage/");
                    if(!fileFolder.exists()) {
                        fileFolder.mkdir();
                    }
                    String tmpName = "tmpPhoto.bmp";
                    File tmpFile = new File(fileFolder, tmpName);
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 20;
                    Bitmap src = BitmapFactory.decodeFile(tmpFile.getAbsolutePath(), options);
                    File filePath = new File(fileFolder, str);
                    if(!filePath.exists()){
                        filePath.mkdir();
                    }
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date date = new Date();
                    String fileName = format.format(date) + ".jpg";
                    tmpFile.renameTo(new File(filePath.getAbsolutePath() + "/" + fileName));

                    File tmpBmpDir = new File(sdFile+"/myImageTmp/"+str+"/");
                    if(!tmpBmpDir.exists()){
                        tmpBmpDir.mkdir();
                    }
                    File tmpBmpFile = new File(tmpBmpDir,fileName);
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

                    context.finish();
                    //Intent intent = new Intent(context, MainActivity.class);
                    //context.startActivity(intent);
                }
            });
        }
    }

    public void showPopupWindow(View parent){
        if(!isShowing()) {
            //int[] location = new int[2];
            //parent.getLocationOnScreen(location);
            //this.showAtLocation(parent, Gravity.NO_GRAVITY, 0,0);
            this.showAsDropDown(parent, parent.getLayoutParams().width/2, 18);
        } else {
            this.dismiss();
        }
    }

}

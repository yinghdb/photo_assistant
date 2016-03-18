package com.example.yinghui.photoassistant;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yinghui on 15/12/9.
 */
public class GridViewAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<GridViewData> mList;

    public GridViewAdapter(Context context, ArrayList<GridViewData> mList) {
        super();
        this.mContext = context;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
        //return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
        //return mThumbIds[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from
                    (this.mContext).inflate(R.layout.gridview_item, null, false);
            holder.imageView = (ImageView)convertView.findViewById(R.id.gridview_item_image);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        if(holder.imageView != null){
            holder.imageView.setImageBitmap(mList.get(position).bitmap);
            holder.imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,ViewPagerActivity.class);
                    Bundle bundle = new Bundle();
                    String[] file = new String[mList.size()];
                    for(int i=0; i<mList.size(); i++){
                        file[i] = "file://"+mList.get(i).filePath.getAbsolutePath();
                    }
                    bundle.putStringArray("fileList", file);
                    String[] tmpFile = new String[mList.size()];
                    for(int i=0; i<mList.size(); i++){
                        tmpFile[i] = "file://"+mList.get(i).tmpFilePath.getAbsolutePath();
                    }
                    bundle.putStringArray("tmpFileList", tmpFile);
                    bundle.putInt("pictureIndex", position);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);

                    //Toast.makeText(mContext,mList.get(position).filePath.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        return convertView;
/*
        ImageView imageView;
        if(convertView == null){
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(260, 260));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(1,1,1,1);
        }
        else {
            imageView = (ImageView)convertView;
        }
        int count=0;
        String jpgName="";
        File[] tmpList = mainFile.listFiles();
        for(int i=0; i<tmpList.length; i++){
            if(count == position){
                jpgName = tmpList[i].getName();
            }
            if(tmpList[i].getName().endsWith(".jpg")){
                count ++;
            }
        }
        //Log.e("ERR: ", jpgName);
        if(jpgName != "") {
            int reqWidth, reqHeight;
            reqHeight = reqWidth = 250;
            String pathName = mainFile.getAbsolutePath() + "/" + jpgName;
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pathName, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            Bitmap src = BitmapFactory.decodeFile(pathName,options);
            Bitmap dst = createScaleBitmap(src, reqWidth, reqHeight);

            imageView.setImageBitmap(dst);
        }
        return imageView;
        */
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

    private class ViewHolder {
        ImageView imageView;
    }
}

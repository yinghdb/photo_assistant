package com.example.yinghui.photoassistant;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;

public class ViewPagerActivity extends BaseActivity {

    private static final String STATE_POSITION = "STATE_POSITION";
    DisplayImageOptions options;
    ViewPager pager;
    String[] fileList;
    String[] tmpFileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        Bundle bundle = this.getIntent().getExtras();
        fileList = bundle.getStringArray("fileList");
        tmpFileList = bundle.getStringArray("tmpFileList");

        int pictureIndex = bundle.getInt("pictureIndex");

        // 如果之前有保存用户数据
        if (savedInstanceState != null) {
            pictureIndex = savedInstanceState.getInt(STATE_POSITION);
        }

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .resetViewBeforeLoading(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new ImagePagerAdapter(fileList));
        pager.setCurrentItem(pictureIndex);

        Button button1 = (Button)findViewById(R.id.button_delete);
        Button button2 = (Button)findViewById(R.id.button_delete_class);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = pager.getCurrentItem();
                File deleteFile = new File(fileList[position].substring(7));
                File typeFile = deleteFile.getParentFile();
                if (deleteFile.exists()) { // 判断文件是否存在
                    if (deleteFile.isFile()) { // 判断是否是文件
                        deleteFile.delete(); // delete()方法
                    }
                }
                if(typeFile.listFiles().length == 0){
                    typeFile.delete();
                }
                deleteFile = new File(tmpFileList[position].substring(7));
                typeFile = deleteFile.getParentFile();
                if (deleteFile.exists()) { // 判断文件是否存在
                    if (deleteFile.isFile()) { // 判断是否是文件
                        Toast.makeText(getApplicationContext(),"已删除",Toast.LENGTH_SHORT).show();
                        deleteFile.delete(); // delete()方法
                    }
                }
                if(typeFile.listFiles().length == 0){
                    typeFile.delete();
                }
                ViewPagerActivity.this.finish();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File deleteFile = new File(fileList[0].substring(7));
                File typeFile = deleteFile.getParentFile();
                File[] childFiles = typeFile.listFiles();
                for(int i=0; i<childFiles.length; i++){
                    childFiles[i].delete();
                }
                typeFile.delete();

                deleteFile = new File(tmpFileList[0].substring(7));
                typeFile = deleteFile.getParentFile();
                childFiles = typeFile.listFiles();
                for(int i=0; i<childFiles.length; i++){
                    childFiles[i].delete();
                }
                typeFile.delete();
                ViewPagerActivity.this.finish();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // 保存用户数据
        outState.putInt(STATE_POSITION, pager.getCurrentItem());
    }

    private class ImagePagerAdapter extends PagerAdapter {
        private String[] images;
        private LayoutInflater inflater;

        public ImagePagerAdapter(String[] images) {
            this.images=images;
            inflater = getLayoutInflater();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public void finishUpdate(View container) {
        }



        @Override
        public int getCount() {
            return images.length;
        }

        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
            final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

            imageLoader.init(ImageLoaderConfiguration.createDefault(ViewPagerActivity.this));
            imageLoader.displayImage(images[position], imageView, options, new SimpleImageLoadingListener() {

                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    spinner.setVisibility(View.VISIBLE);
                }
                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    String message = null;
                    switch (failReason.getType()) {     // 获取图片失败类型
                        case IO_ERROR:              // 文件I/O错误
                            message = "Input/Output error";
                            break;
                        case DECODING_ERROR:        // 解码错误
                            message = "Image can't be decoded";
                            break;
                        case NETWORK_DENIED:        // 网络延迟
                            message = "Downloads are denied";
                            break;
                        case OUT_OF_MEMORY:         // 内存不足
                            message = "Out Of Memory error";
                            break;
                        case UNKNOWN:               // 原因不明
                            message = "Unknown error";
                            break;
                    }
                    Toast.makeText(ViewPagerActivity.this, message, Toast.LENGTH_SHORT).show();

                    spinner.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    spinner.setVisibility(View.GONE);       // 不显示圆形进度条
                }
            });
            ((ViewPager) view).addView(imageLayout, 0);     // 将图片增加到ViewPager
            return imageLayout;
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View container) {
        }
    }
}

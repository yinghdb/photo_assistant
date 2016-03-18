package com.example.yinghui.photoassistant;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by yinghui on 15/12/9.
 */
public class ListViewAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ListViewData> mList;

    public ListViewAdapter(Context mContext, ArrayList<ListViewData> mList){
        super();
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView=LayoutInflater.from
                    (this.mContext).inflate(R.layout.listview_item,null,false);
            holder.textView = (TextView)convertView.findViewById(R.id.listview_item_textview);
            holder.gridView = (GridView)convertView.findViewById(R.id.listview_item_gridview);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }
        if(holder.textView != null){
            holder.textView.setText(mList.get(position).className);
        }
        if(holder.gridView != null){
            GridViewAdapter gridViewAdapter = new GridViewAdapter(mContext, mList.get(position).arrayListForGridView);
            holder.gridView.setAdapter(gridViewAdapter);
        }
        return convertView;
    }

    private class ViewHolder {
        TextView textView;
        GridView gridView;
    }
}

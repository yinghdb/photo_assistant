package com.example.yinghui.photoassistant;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

import java.util.jar.Attributes;

/**
 * Created by yinghui on 15/12/9.
 */
public class NoScrollGridView extends GridView {
    public NoScrollGridView(Context context) {
        super(context);
    }

    public NoScrollGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}

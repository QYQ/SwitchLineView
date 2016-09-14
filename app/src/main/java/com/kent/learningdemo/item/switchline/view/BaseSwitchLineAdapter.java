package com.kent.learningdemo.item.switchline.view;

import android.view.View;
import android.view.ViewGroup;


/**
 * Created by kent on 16/8/29.
 */
public abstract class BaseSwitchLineAdapter {

    private SwitchLineView.OnDataChangedListener mDataChangedListener = null;

    public void setOnDataChangedListener (SwitchLineView.OnDataChangedListener listener){
        this.mDataChangedListener = listener;
    }

    public SwitchLineView.OnDataChangedListener getOnDataChangedListener(){
        return  this.mDataChangedListener;
    }

    public abstract int getCount();

    /**
     * 获取每个 ItemView
     * @param position  位置下标
     * @param parent    父View，即 SwitchLineView
     * @return          子View实例
     */
    public abstract View getView(int position, View convertView, ViewGroup parent);

    public void notifyDataSetChanged(){
        if(this.mDataChangedListener != null){
            this.mDataChangedListener.onChanged();
        }
    }
}

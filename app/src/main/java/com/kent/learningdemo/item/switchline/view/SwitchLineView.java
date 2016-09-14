package com.kent.learningdemo.item.switchline.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.kent.learningdemo.R;

import java.util.ArrayList;

/**
 * 自动换行View
 * 使用注意：
 * 1.不支持子 View margging 属性,子 View 间距可用 divider 实现
 * 2.支持单行显示,超过显示范围的 item 不显示,默认为多行
 * 3.同一行内每个 item 的高度要相同,否则有可能出现行与行之间被覆盖显示
 * Created by kent on 16/8/29.
 */
public class SwitchLineView extends ViewGroup{

    public static final int ROW_COUNT_UNLITMIT = -1 ;   //不限制行数

    private int mHorizontalPadding = 0;      //分割线宽度
    private int mVerticalPadding = 0;        //分割线高度

    //行数
    private int mRowNum = 0;

    //最大限制行数
    private int mMaxRowCount = ROW_COUNT_UNLITMIT;

    private BaseSwitchLineAdapter mAdapter; //适配器

    private OnDataChangedListener mDataChangedListener = new OnDataChangedListener() {
        @Override
        public void onChanged() {
            post(new Runnable() {
                @Override
                public void run() {
                    refresh();
                }
            });
        }
    };

    private int mC = 0;
    private int lC = 0;

    //每个子 View 的位置缓存
    private ArrayList<LayoutCache> mLayoutCache = null;

    public SwitchLineView(Context context) {
        super(context);
    }

    public SwitchLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initStyle(attrs);
    }

    public SwitchLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStyle(attrs);
    }

    private void initStyle(AttributeSet attributeSet){
        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.SwitchLineView);
        if(typedArray.hasValue(R.styleable.SwitchLineView_maxRowCount)){
            this.mMaxRowCount = typedArray.getInt(R.styleable.SwitchLineView_maxRowCount, ROW_COUNT_UNLITMIT);
        }
        if(typedArray.hasValue(R.styleable.SwitchLineView_horizontalPadding)){
            this.mHorizontalPadding = typedArray.getDimensionPixelSize(R.styleable.SwitchLineView_horizontalPadding, 0);
        }
        if(typedArray.hasValue(R.styleable.SwitchLineView_verticalPadding)){
            this.mVerticalPadding = typedArray.getDimensionPixelSize(R.styleable.SwitchLineView_verticalPadding, 0);
        }
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        mC ++;
        Log.e("SwitchLineView", "onMeasure " + mC);
        //重置布局缓存,剔除失效缓存,防止失效缓存影响layout过程
        resetCache();
        int childCount = getChildCount();
        //父 View 建议的宽度
        int suggestedWidth = MeasureSpec.getSize(widthMeasureSpec);
        //可用宽度,去掉 padding 后,子 View 的可用宽度
        int availableWidth = suggestedWidth - getPaddingLeft() - getPaddingRight();
        int rowCount = 1;   //行数
        int currentTotalWidth = 0;  //在每一行上，测量到的所有子View的宽度和，在换行时此值置0
        int totalHeight = 0;

        //子View在当前行的下标，每次换行会置0
        int currentHorizonIndex = 0;
        for(int i = 0;i < childCount; i++){
            View childView = getChildAt(i);
            //测量每个子 View
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();

            if(currentHorizonIndex == 0){
                //当前行的第一个子 View
                currentTotalWidth += childWidth;
            }else {
                currentTotalWidth += mHorizontalPadding + childWidth;
            }

            if(rowCount == 1){
                //第一行
                totalHeight = childHeight;
            }
            //超过一行
            if(currentTotalWidth > availableWidth){
                rowCount ++;
                if(mMaxRowCount != ROW_COUNT_UNLITMIT && rowCount > mMaxRowCount){
                    //超出行数限制,剩下的item全部忽略
                    break;
                }
                if(rowCount == 1){
                    //第一行
                    totalHeight += childHeight;
                }else {
                    totalHeight += mVerticalPadding + childHeight;
                }
                currentTotalWidth = childWidth;
                currentHorizonIndex = 1;
            }else {
                currentHorizonIndex ++;
            }

            //缓存起来，在layout时直接用
            addCache(i, getPaddingLeft() + currentTotalWidth - childWidth,
                    getPaddingTop() + totalHeight - childHeight,
                    getPaddingLeft() + currentTotalWidth,
                    getPaddingTop() + totalHeight);

        }
        this.mRowNum = rowCount;
        //设置经过换行后的高度
        setMeasuredDimension(widthMeasureSpec, totalHeight + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        lC ++;
        Log.e("SwitchLineView", "onLayout " + lC);
        int childCount = getChildCount();
        for(int i = 0 ; i < childCount ; i++){
            View childView = getChildAt(i);
            LayoutCache layoutCache = this.mLayoutCache.get(i);
            if(layoutCache == null){
                continue;
            }
            childView.layout(layoutCache.left, layoutCache.top, layoutCache.right, layoutCache.bottom);
        }
    }

    private static final class LayoutCache{
        int left;
        int top;
        int right;
        int bottom;
    }

    /**
     * 设置最大行数
     * @param maxRowCount
     */
    public void setMaxRowCount(int maxRowCount){
        this.mMaxRowCount = maxRowCount;
        if(this.mRowNum == 0){
            //如果当前行数为0,则不刷新
            return;
        }
        refresh();
    }

    /**
     * 获取行数
     * @return
     */
    public int getLineNum(){
        return this.mRowNum;
    }

    public void setHorizontalPadding(int padding){
        this.mHorizontalPadding = padding;
    }

    public void setVerticalPadding(int padding){
        this.mVerticalPadding = padding;
    }

    private void addCache(int cacheIndex, int l, int t, int r, int b){
        LayoutCache cache = this.mLayoutCache.get(cacheIndex);
        if(cache == null){
            return;
        }
        cache.left = l;
        cache.top = t;
        cache.right = r;
        cache.bottom = b;
    }

    public void setAdapter(BaseSwitchLineAdapter adapter){
        if(mAdapter != null) {
            mAdapter = null;
            this.removeAllViews();
        }

        if(adapter == null) {
            throw new IllegalArgumentException("The adapter is null !!") ;
        }

        this.mAdapter = adapter;
        this.mAdapter.setOnDataChangedListener(this.mDataChangedListener);

        resetCache();
        initView();
    }

    /**
     * 刷新
     */
    private void refresh(){
        this.removeAllViews();
        resetCache();
        initView();
    }

    /**
     * 重置缓存
     */
    private void resetCache(){
        int childCount = this.mAdapter.getCount();
        if(this.mLayoutCache == null){
            this.mLayoutCache = new ArrayList<>();
        }else {
            this.mLayoutCache.clear();
        }
        //初始化布局缓存
        for(int i = 0 ; i < childCount ; i ++){
            this.mLayoutCache.add(new LayoutCache());
        }
    }

    /**
     * 添加子 View
     */
    private void initView(){
        int childCount = this.mAdapter.getCount();
        //添加子View
        for(int i = 0 ; i < childCount ; i ++){
            addView(this.mAdapter.getView(i, null, this));
        }
    }

    /**
     * 适配器数据变化监听器
     */
    public interface OnDataChangedListener{

        /**
         * 在Adapter中的数据实体发送改变时 被调用
         * @see BaseSwitchLineAdapter#notifyDataSetChanged()
         */
        void onChanged();
    }
}

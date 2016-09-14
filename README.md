# SwitchLineView

## 功能

* 一行显示不全，自动换行
* 支持设置最大行数（ xml 中或代码中）
* 支持设置水平及竖直间距

&emsp;效果如下图：


![](https://github.com/QYQ/SwitchLineView/blob/master/switchline.png)


## 使用

**第一步： 在 xml 布局中：**

```
<com.kent.learningdemo.item.switchline.view.SwitchLineView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/switch_line_view"
        app:verticalPadding="5dp"		
        app:horizontalPadding="5dp"
        app:maxRowCount="3"
        android:padding="10dp">

</com.kent.learningdemo.item.switchline.view.SwitchLineView>
```

&emsp;&emsp;其中:

* verticalPadding：行距
* horizontalPadding：水平间隔，即每个 Item 之间的间距
* maxRowCount：最大行数限制，默认为 -1，不限制行数

上述三个属性也都可以通过 SwtichLineView 的对应方法在代码中设置。

**第二步： 新建 Adapter 继承自 BaseSwitchLineAdapter。**

&emsp;&emsp;例如：

```
private class Adapter extends BaseSwitchLineAdapter{

        private ArrayList<String> mData = new ArrayList<>();

        private LayoutInflater mInflater;

        public Adapter(Context mContext) {
            this.mInflater = LayoutInflater.from(mContext);
        }

        public void setData(ArrayList<String> data){
            this.mData = data;
        }

        @Override
        public int getCount() {
            return mData == null? 0 : mData.size();
        }

        @Override
        public View getView(int position, View convert, ViewGroup parent) {
            View rootView = mInflater.inflate(R.layout.switch_line_item_layout, parent, false);
            TextView textView = (TextView) rootView.findViewById(R.id.text);
            textView.setText(mData.get(position));
            return rootView;
        }
    }
```

**第三步：为SwitchLineView 设置 Adapter**

&emsp;&emsp;代码如下：

```
final SwitchLineView switchLineView = (SwitchLineView) findViewById(R.id.switch_line_view);

final Adapter adapter = new Adapter(this);
adapter.setData(data);
switchLineView.setAdapter(adapter);
```

**其它：**

* BaseSwitchLineAdapter.notifyDataSetChanged()：刷新数据显示
* SwitchLineView.setMaxRowCount()：设置最大行数限制，默认值为SwitchLineView.ROW_COUNT_UNLITMIT（-1 无限制）
* SwitchLineView.setHorizontalPadding()：设置 item 之间的水平间距
* SwitchLineView.setVerticalPadding()：设置行距
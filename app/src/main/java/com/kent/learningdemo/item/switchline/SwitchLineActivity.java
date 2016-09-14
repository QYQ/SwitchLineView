package com.kent.learningdemo.item.switchline;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kent.learningdemo.R;
import com.kent.learningdemo.item.switchline.view.BaseSwitchLineAdapter;
import com.kent.learningdemo.item.switchline.view.SwitchLineView;

import java.util.ArrayList;

public class SwitchLineActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_line);

        final SwitchLineView switchLineView = (SwitchLineView) findViewById(R.id.switch_line_view);

        final Adapter adapter = new Adapter(this);

        final ArrayList<String> data = new ArrayList<>();
        for(int i = 0;i < 11;i++){
            data.add(" 【这是第 " + i +" 个item】");
        }

        adapter.setData(data);
        switchLineView.setAdapter(adapter);

        Button removeBtn = (Button) findViewById(R.id.remove_button);
        Button addBtn = (Button) findViewById(R.id.add_button);
        Button setRowBtn = (Button) findViewById(R.id.set_row_count);
        Button setUnlinitBtn = (Button) findViewById(R.id.set_unlimit);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.size() == 0){
                    Toast.makeText(SwitchLineActivity.this, "没有能移除的item了",Toast.LENGTH_SHORT).show();
                    return;
                }
                data.remove(data.size() - 1);
                adapter.setData(data);
                adapter.notifyDataSetChanged();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.add("这是新添加的item");
                adapter.setData(data);
                adapter.notifyDataSetChanged();
            }
        });

        setRowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchLineView.setMaxRowCount(1);
            }
        });

        setUnlinitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchLineView.setMaxRowCount(SwitchLineView.ROW_COUNT_UNLITMIT);
            }
        });

    }

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

}

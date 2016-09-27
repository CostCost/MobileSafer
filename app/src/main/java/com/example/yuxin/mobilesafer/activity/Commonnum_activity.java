package com.example.yuxin.mobilesafer.activity;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.example.yuxin.mobilesafer.R;
import com.example.yuxin.mobilesafer.dao.Commonnumdao;

import java.util.List;
import java.util.Map;

/**
 * =============================================================================
 * Copyright (c) 2016 yuxin. All rights reserved.
 * Packname com.example.yuxin.mobilesafer.activity
 * Created by yuxin.
 * Created time 2016/8/22 0022 上午 9:57.
 * Version   1.0;
 * Describe :常用号码查询
 * History:
 * ==============================================================================
 */
public class Commonnum_activity extends Activity {


    private ExpandableListView lv_expand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.commonnum_layout);
        //获取控件ExpandableListView
        lv_expand = (ExpandableListView) findViewById(R.id.lv_expand);
        //如果资源目录下数据库文件没有加载到手机中，那么执行拷贝工作
        if (!Commonnumdao.isExist(this)) {
            Commonnumdao.copyFileToFiles(this);
        }
        //获取groupData
        List<Map<String, String>> groupData = Commonnumdao.getGroupData(this);
        //获取childData
        List<List<Map<String, String>>> childData = Commonnumdao.getChildData(this);
        //编写一个简单的SimpleExpandableListAdapter适配器
        final SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                this,
                groupData,
                R.layout.simple_list_item_1,
                new String[]{"name"},
                new int[]{R.id.text1},
                childData,
                android.R.layout.simple_list_item_2,
                new String[]{"name", "number"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );

        lv_expand.setAdapter(adapter);

        lv_expand.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Map<String, String> child = (Map<String, String>) adapter.getChild(groupPosition, childPosition);
                String number = child.get("number");
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent(getApplicationContext(), Tools_activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
        return super.onKeyDown(keyCode, event);
    }
}

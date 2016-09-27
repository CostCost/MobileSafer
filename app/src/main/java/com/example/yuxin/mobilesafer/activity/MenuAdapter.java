package com.example.yuxin.mobilesafer.activity;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.yuxin.mobilesafer.R;
import com.example.yuxin.mobilesafer.domain.TrafficInfo;
import com.example.yuxin.mobilesafer.engine.TrafficInfo_Engine;

import java.util.List;

import static android.util.Log.i;

/**
 * Created by user on 2016/2/19.
 */
public class MenuAdapter extends BaseAdapter {
    private static final String TAG ="MenuAdapter";
    private Context mContext;
    private List<TrafficInfo> traffic;
    private LayoutInflater mInflater;
    private long pb_max;
    public MenuAdapter(Context context , List<TrafficInfo> traffic,long max){
        pb_max=max;
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.traffic = traffic;
    }
    @Override
    public int getCount() {
        return traffic.size();
    }

    @Override
    public Object getItem(int position) {
        return traffic.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.left_menu_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.iv_appicon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_appname = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_total = (TextView) convertView.findViewById(R.id.tv_total);
            viewHolder.pb_total = (ProgressBar) convertView.findViewById(R.id.pb_total);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TrafficInfo trafficInfo = traffic.get(position);
        viewHolder.iv_appicon.setImageDrawable(trafficInfo.getDrawable());
        viewHolder.tv_appname.setText(trafficInfo.getName());
        viewHolder.tv_total.setText(Formatter.formatFileSize(mContext,trafficInfo.getTotal()));
        viewHolder.pb_total.setMax((int) pb_max/1024);
        viewHolder.pb_total.setProgress((int) trafficInfo.getTotal()/1024);
        return convertView;
    }
    private static class ViewHolder{
        ImageView iv_appicon;
        TextView tv_appname;
        TextView tv_total;
        ProgressBar pb_total;
    }
}

package com.king.myweb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by 16230 on 2016/9/2.
 */

public class MyAdapter extends BaseExpandableListAdapter {
    private List<String> parent;
    private Map<String,List<String>> map;
    private Context context;
    public MyAdapter(Context context, List<String> parent, Map<String, List<String>> map) {
        this.parent = parent;
        this.map = map;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return parent.size();
    }

    @Override
    public int getChildrenCount(int i) {
        String key = parent.get(i);
        int size = map.get(key).size();
        return size;
    }

    @Override
    public Object getGroup(int i) {
        return parent.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        String key = parent.get(i);
        return (map.get(key).get(i1));
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if(view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_parent,null);
        }
        TextView tv = (TextView) view.findViewById(R.id.textView_parent);
        tv.setText(parent.get(i));
        tv.setPadding(40,40,40,40);
        tv.setTextSize(25);
        return tv;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        String key = parent.get(i);
        String info = map.get(key).get(i1);
        if(view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_children,null);
        }
        TextView tv = (TextView) view.findViewById(R.id.textView_children);
        tv.setText(info);
        tv.setPadding(30,30,30,30);
        tv.setTextSize(18);
        return tv;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}

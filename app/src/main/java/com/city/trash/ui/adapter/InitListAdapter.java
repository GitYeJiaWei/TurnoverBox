package com.city.trash.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.city.trash.R;
import com.city.trash.bean.EPC;

import java.util.ArrayList;
import java.util.List;

public class InitListAdapter extends BaseAdapter {
    //定义需要包装的JSONArray对象
    public List<EPC> mymodelList = new ArrayList<>();
    private Context context = null;
    private String size;
    //视图容器
    private LayoutInflater layoutInflater;

    public InitListAdapter(Context _context, String size) {
        this.context = _context;
        //创建视图容器并设置上下文
        this.layoutInflater = LayoutInflater.from(_context);
        this.size = size;
    }

    public void updateDatas(List<EPC> datalist) {
        if (datalist == null) {
            return;
        } else {
            mymodelList.clear();
            mymodelList.addAll(datalist);
            notifyDataSetChanged();
        }

    }

    /**
     * 清空列表的所有数据
     */
    public void clearData() {
        mymodelList.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return this.mymodelList.size();
    }

    @Override
    public Object getItem(int position) {
        if (getCount() > 0) {
            return this.mymodelList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItemView listItemView = null;
        if (convertView == null) {
            //获取list_item布局文件的视图
            convertView = layoutInflater.inflate(R.layout.list_item_init, null);
            //获取控件对象
            listItemView = new ListItemView();
            listItemView.id = (TextView) convertView.findViewById(R.id.tv_id);
            listItemView.user = (TextView) convertView.findViewById(R.id.tv_user);
            listItemView.num = (TextView) convertView.findViewById(R.id.tv_num);
            listItemView.name = (TextView) convertView.findViewById(R.id.tv_name);

            //设置控件集到convertView
            convertView.setTag(listItemView);
        } else {
            listItemView = (InitListAdapter.ListItemView) convertView.getTag();
        }

        final EPC m1 = (EPC) this.getItem(position);
        if (size.equals("pickResult")){
            listItemView.id.setText(m1.getData1());
            listItemView.user.setText(m1.getData2());
            listItemView.num.setText(m1.getData3());
            listItemView.name.setText(m1.getData4());
        }else {
            listItemView.id.setText(m1.getData1());
            listItemView.user.setText(m1.getData2());
            listItemView.num.setText(m1.getData3());
            listItemView.name.setText(m1.getData4());
        }

        return convertView;
    }

    /**
     * 使用一个类来保存Item中的元素
     * 自定义控件集合
     */
    public final class ListItemView {
        TextView id, user, num,name;
    }
}
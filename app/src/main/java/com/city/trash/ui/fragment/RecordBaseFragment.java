package com.city.trash.ui.fragment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.city.trash.bean.EpcInfo;
import com.city.trash.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public abstract class RecordBaseFragment<T extends BasePresenter> extends BaseFragment<T>
{

    protected MyAdapter adapter;
    protected ArrayList<EpcInfo> mEpcList = new ArrayList<EpcInfo>();
    protected ConcurrentHashMap<String, EpcInfo> epcToReadDataMap = new ConcurrentHashMap<String, EpcInfo>();

    public void myOnKeyDwon()
    {

    }

    public void myOnKeyUp()
    {

    }

    public void initPower()
    {

    }

    //各自的布局
    protected abstract View getItemView(int position, View convertView, ViewGroup parent, EpcInfo EpcInfo);

    public class MyAdapter extends BaseAdapter
    {
        private Context mContext;

        public ArrayList<EpcInfo> getDataList()
        {
            return mDataList;
        }

        private ArrayList<EpcInfo> mDataList;

        public MyAdapter(Context context)
        {
            mContext = context;
            mDataList = new ArrayList<EpcInfo>();
        }

        public void update(ArrayList<EpcInfo> dataList)
        {
            if (dataList.size() == 0)
            {
                return;
            }
            mDataList.clear();
            mDataList.addAll(dataList);
            sortDatas();
            notifyDataSetChanged();
        }

        public void clear()
        {
            mDataList.clear();
            notifyDataSetChanged();
        }

        private void sortDatas()
        {
        }

        @Override
        public int getCount()
        {
            return mDataList.size();
        }

        @Override
        public EpcInfo getItem(int position)
        {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            return getItemView(position, convertView, parent, getItem(position));
        }
    }

}

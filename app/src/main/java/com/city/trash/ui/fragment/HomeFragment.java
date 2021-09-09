package com.city.trash.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.city.trash.AppApplication;
import com.city.trash.R;
import com.city.trash.bean.BaseBean;
import com.city.trash.bean.FeeRule;
import com.city.trash.common.util.ACache;
import com.city.trash.di.component.AppComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 首页
 */
public class HomeFragment extends BaseFragment {

    @BindView(R.id.gridview)
    GridView gridview;
    private BaseBean<FeeRule> baseBean;
    private List<Map<String, Object>> dataList;
    private SimpleAdapter adapter;
    CallBackValue callBackValue;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public int setLayout() {
        return R.layout.home_layout;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {

    }

    /**
     * fragment与activity产生关联是  回调这个方法
     */
    @Override
    public void onAttach(Context context) {
        // TODO Auto-generated method stub
        super.onAttach(context);
        //当前fragment从activity重写了回调接口  得到接口的实例化对象
        callBackValue =(CallBackValue) getActivity();
    }

    private void initData() {
        dataList = new ArrayList<Map<String, Object>>();
        baseBean = (BaseBean<FeeRule>) ACache.get(AppApplication.getApplication()).getAsObject("feeRule");
        if (baseBean != null) {
            for (int i = 0; i < baseBean.getData().getPadMenus().size(); i++) {
                String id = baseBean.getData().getPadMenus().get(i).getId();
                Map<String, Object> map=new HashMap<String, Object>();
                if (id.equals("1")) {
                    map.put("img", R.mipmap.main01);
                    map.put("text","租赁");
                } else if (id.equals("2")) {
                    map.put("img", R.mipmap.main05);
                    map.put("text","退还");
                } else if (id.equals("3")) {
                    map.put("img", R.mipmap.main08);
                    map.put("text","报废登记");
                } else if (id.equals("4")) {
                    map.put("img", R.mipmap.main07);
                    map.put("text","扫码查询");
                } else if (id.equals("5")){
                    map.put("img", R.mipmap.main02);
                    map.put("text","绑定二维码");
                }
                dataList.add(map);
            }
        }

        for (int i = 0; i < 2; i++) {
            Map<String, Object> map=new HashMap<String, Object>();
            if (i==0){
                map.put("img", R.mipmap.main10);
                map.put("text","设置");
            }else {
                map.put("img", R.mipmap.main06);
                map.put("text","退出");
            }
            dataList.add(map);
        }

    }

    @Override
    public void init(View view) {
        initData();
        String[] from={"img","text"};

        int[] to={R.id.img,R.id.text};

        adapter=new SimpleAdapter(getContext(), dataList, R.layout.gridview_item, from, to);

        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                String strValue = dataList.get(arg2).get("text").toString();
                callBackValue.SendMessageValue(strValue);
            }
        });
    }

    @Override
    public void setBarCode(String barCode) {

    }

    //定义一个回调接口
    public interface CallBackValue{
        public void SendMessageValue(String strValue);
    }

}

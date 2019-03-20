package com.city.trash.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.city.trash.ui.activity.MainActivity;
import com.city.trash.ui.fragment.HomeFragment;
import com.city.trash.ui.fragment.LeaseFragment;
import com.city.trash.ui.fragment.ReturnFragment;
import com.city.trash.ui.fragment.SettingFragment;

/**
 * Created by YJW on 2018/1/2.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private final  int RAGER_COUNT =4;
    private HomeFragment myFragment1 =null;
    private LeaseFragment myFragment2 =null;
    private ReturnFragment myFragment3 =null;
    private SettingFragment myFragment4 =null;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        myFragment1 =new HomeFragment();
        myFragment2 =new LeaseFragment();
        myFragment3 =new ReturnFragment();
        myFragment4 =new SettingFragment();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment =null;
        switch (position){
            case  MainActivity.PAGE_ONE:
                fragment =myFragment1;
                break;
            case MainActivity.PAGE_TWO:
                fragment =myFragment2;
                break;
            case  MainActivity.PAGE_THREE:
                fragment =myFragment3;
                break;
            case MainActivity.PAGE_FOUR:
                fragment =myFragment4;
                break;
        }
        return  fragment;
    }

    //getCount():获得viewpager中有多少个view
    @Override
    public int getCount() {
        return RAGER_COUNT;
    }

    //instantiateItem(): ①将给定位置的view添加到ViewGroup(容器)中,创建并显示出来
    //②返回一个代表新增页面的Object(key),通常都是直接返回view本身就可以了,
    // 当然你也可以 自定义自己的key,但是key和每个view要一一对应的关系
    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    //destroyItem():移除一个给定位置的页面。适配器有责任从容器中删除这个视图。
    // 这是为了确保在finishUpdate(viewGroup)返回时视图能够被移除。
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println("position Destory" + position);
        super.destroyItem(container, position, object);
    }
}

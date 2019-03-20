package com.city.trash.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;


import com.city.trash.R;
import com.city.trash.di.component.AppComponent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 首页
 * */
public class HomeFragment extends BaseFragment
{


    public static HomeFragment newInstance()
    {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public int setLayout()
    {
        return R.layout.home_layout;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {

    }

    @Override
    public void init(View view)
    {
    }

    @Override
    public void setBarCode(String barCode)
    {

    }



}

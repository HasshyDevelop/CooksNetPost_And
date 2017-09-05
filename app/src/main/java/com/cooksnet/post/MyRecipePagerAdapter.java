package com.cooksnet.post;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by takana on 2016/07/14
 */
public class MyRecipePagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    public static final int PUBLISH = 0;
    public static final int DRAFT = 1;
    private Context context;



    public MyRecipePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }



    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case PUBLISH :
                return PublishFragment.newInstance();
            default:
                return DraftFragment.newInstance();
        }
    }



    @Override
    public int getCount() {
        return PAGE_COUNT;
    }



}

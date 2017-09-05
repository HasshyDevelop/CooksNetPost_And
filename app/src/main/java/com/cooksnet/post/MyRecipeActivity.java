package com.cooksnet.post;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class MyRecipeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipe);
//        optionListCreate();

        setTitle(R.string.menu_my_recipe);

        // タブ、ページャー生成
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        assert viewPager != null;
        viewPager.setAdapter(new MyRecipePagerAdapter(getSupportFragmentManager(), MyRecipeActivity.this));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(viewPager);

        // タブをカスタマイズ
        View view = View.inflate(this, R.layout.tab_my_recipe, null);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        TextView title = (TextView) view.findViewById(R.id.title);
        TabLayout.Tab tab = tabLayout.getTabAt(MyRecipePagerAdapter.PUBLISH);

        icon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.tab_publish, null));
        title.setText(getText(R.string.my_recipe_menu_publish));
        assert tab != null;
        tab.setCustomView(view);

        view = View.inflate(this, R.layout.tab_my_recipe, null);
        icon = (ImageView) view.findViewById(R.id.icon);

        title = (TextView) view.findViewById(R.id.title);
        tab = tabLayout.getTabAt(MyRecipePagerAdapter.DRAFT);

        icon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.tab_draft, null));
        title.setText(getText(R.string.my_recipe_menu_draft));
        assert tab != null;
        tab.setCustomView(view);

        // デフォルトのタブの設定
        String currentTab = getSharedPreferences(SHARED_NAME, MODE_PRIVATE)
                                .getString(PREF_CURRENT_TAB, TAB_PUBLISH);

        if (currentTab.equals(TAB_PUBLISH)) {
            viewPager.setCurrentItem(MyRecipePagerAdapter.PUBLISH);
        }
        else {
            viewPager.setCurrentItem(MyRecipePagerAdapter.DRAFT);
        }
    }
}

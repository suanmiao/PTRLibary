package me.suanmiao.ptrexample.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import me.suanmiao.ptrexample.R;
import me.suanmiao.ptrexample.ui.adapter.pager.FragmentPagerAdapter;
import me.suanmiao.ptrexample.ui.fragment.ContinuousPullingListFragment;
import me.suanmiao.ptrexample.ui.fragment.NormalListFragment;
import me.suanmiao.ptrexample.ui.fragment.RainDropListFragment;


public class MainActivity extends BaseToolbarActivity {

  public static final int INDEX_NORMAL = 0;
  public static final int INDEX_RAINDROP = 1;
  public static final int INDEX_CONTINUOUS = 2;
  @InjectView(R.id.ic_main_normal)
  ImageView icMainNormal;
  @InjectView(R.id.ic_main_raindrop)
  ImageView icMainRaindrop;
  @InjectView(R.id.ic_main_continuous)
  ImageView icMainContinuous;

  @InjectView(R.id.toolbar_base)
  Toolbar toolbarMain;
  @InjectView(R.id.viewpager_main)
  ViewPager viewpagerMain;

  @Override
  protected void afterInjected() {
    initWidgets();
  }

  @Override
  protected int getContentViewId() {
    return R.layout.activity_main;
  }

  private void initWidgets() {
    setupActionBar();
    icMainNormal.setSelected(true);

    List<Fragment> contentFragmentList = new ArrayList<Fragment>();
    contentFragmentList.add(new NormalListFragment());
    contentFragmentList.add(new RainDropListFragment());
    contentFragmentList.add(new ContinuousPullingListFragment());
    FragmentPagerAdapter fragmentPagerAdapter =
        new FragmentPagerAdapter(getSupportFragmentManager(), contentFragmentList);
    viewpagerMain.setAdapter(fragmentPagerAdapter);
    viewpagerMain.setOffscreenPageLimit(3);

    viewpagerMain.setOnPageChangeListener(pageChangeListener);

    icMainNormal.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        selectItem(INDEX_NORMAL);
      }
    });

    icMainRaindrop.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        selectItem(INDEX_RAINDROP);
      }
    });

    icMainContinuous.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        selectItem(INDEX_CONTINUOUS);
      }
    });
  }

  private void selectItem(int index) {
    switch (index) {
      case INDEX_NORMAL:
        icMainNormal.setSelected(true);
        icMainRaindrop.setSelected(false);
        icMainContinuous.setSelected(false);
        break;
      case INDEX_RAINDROP:
        icMainNormal.setSelected(false);
        icMainRaindrop.setSelected(true);
        icMainContinuous.setSelected(false);
        break;
      case INDEX_CONTINUOUS:
        icMainNormal.setSelected(false);
        icMainRaindrop.setSelected(false);
        icMainContinuous.setSelected(true);
        break;
    }
    if (viewpagerMain.getCurrentItem() != index) {
      viewpagerMain.setCurrentItem(index);
    }
  }

  private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
    @Override
    public void onPageScrolled(int i, float v, int i2) {}

    @Override
    public void onPageSelected(int i) {
      selectItem(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {}
  };

  private void setupActionBar() {
    setSupportActionBar(toolbarMain);
  }

}

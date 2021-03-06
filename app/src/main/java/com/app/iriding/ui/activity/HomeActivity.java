package com.app.iriding.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.iriding.R;
import com.app.iriding.util.SwitchJsonString;
import com.github.florent37.materialviewpager.MaterialViewPager;


/**
 * Created by 王海 on 2015/6/1.
 */
public class HomeActivity extends BaseActivity{
    private MaterialViewPager mViewPager;
    private Toolbar toolbar;
    private ImageView imageView;
    private ImageView navImage;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    @Override
    public void setContentView() {
        setContentView(R.layout.home_activity);
    }

    @Override
    public void findViews() {
        mViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);
        navigationView = (NavigationView) findViewById(R.id.draw_item);
        imageView = (ImageView) findViewById(R.id.logo_white);
        navImage = (ImageView) findViewById(R.id.nav_icon);
        Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.myuser);
        Bitmap dst;
        //将长方形图片裁剪成正方形图片
        if (src.getWidth() >= src.getHeight()){
            dst = Bitmap.createBitmap(src, src.getWidth()/2 - src.getHeight()/2, 0, src.getHeight(), src.getHeight()
            );
        }else{
            dst = Bitmap.createBitmap(src, 0, src.getHeight()/2 - src.getWidth()/2, src.getWidth(), src.getWidth()
            );
        }
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), dst);
        roundedBitmapDrawable.setCornerRadius(dst.getWidth() / 2);//设置圆角半径为正方形边长的一半
        roundedBitmapDrawable.setAntiAlias(true);
        imageView.setImageDrawable(roundedBitmapDrawable);
        navImage.setImageDrawable(roundedBitmapDrawable);
        toolbar = mViewPager.getToolbar();
        toolbar.setTitle(R.string.app_name);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            mDrawerLayout = (DrawerLayout)findViewById(R.id.draw_layout);
            mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,0,0);

            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setElevation(1);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayUseLogoEnabled(false);
                actionBar.setHomeButtonEnabled(true);
            }
            if (getSupportActionBar() != null) {
                getSupportActionBar().setElevation(1);
            }

            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayUseLogoEnabled(false);
                actionBar.setHomeButtonEnabled(true);
            }
        }
        mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            int oldPosition = -1;

            @Override
            public Fragment getItem(int position) {
                switch (position % 2) {
                    case 1:
                        return TravelRecyclerViewFragment.newInstance();
                    default:
                        return TravelScrollFragment.newInstance();
                }
            }

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                super.setPrimaryItem(container, position, object);
                if (position == oldPosition)
                    return;
                oldPosition = position;

                int color = 0;
                String imageUrl = "";
                switch (position % 2) {
                    case 0:
                        imageUrl = "http://pic1.ooopic.com/00/87/68/91b1OOOPIC4e.jpg";
                        color = getResources().getColor(R.color.ColorPrimary);
                        break;
                    case 1:
                        imageUrl = "http://img4q.duitang.com/uploads/item/201310/07/20131007102558_yYSNe.jpeg";
                        color = getResources().getColor(R.color.cyan);
                        break;
                }

                final int fadeDuration = 200;
                mViewPager.setImageUrl(imageUrl, fadeDuration);
                mViewPager.setColor(color, fadeDuration);
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position % 2) {
                    case 0:
                        return getString(R.string.tab_view1);
                    case 1:
                        return getString(R.string.tab_view2);
                }
                return "";
            }
        });
        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

        mViewPager.getViewPager().setCurrentItem(0);
    }

    @Override
    public void getData() {

    }

    // 抽屉菜单的选项
    @Override
    public void showContent() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_sub_item_2:
//                        mDrawerLayout.closeDrawers();// 关闭抽屉
                        Intent intent = new Intent(HomeActivity.this, RecordMoreActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        Log.e("Home", "onResume2");
        super.onResume();
        // 读取临时保存的数据
        SharedPreferences sharedPreferences = getSharedPreferences("lastData",MODE_PRIVATE);
        String mCurrentLantitude= sharedPreferences .getString("mCurrentLantitude","");
        String mCurrentLongitude= sharedPreferences .getString("mCurrentLongitude","");
        String maxLantitude= sharedPreferences .getString("maxLantitude","");
        String maxtLongitude= sharedPreferences .getString("maxtLongitude","");
        String minLantitude= sharedPreferences .getString("minLantitude","");
        String mintLongitude= sharedPreferences .getString("mintLongitude","");
        String distance= sharedPreferences .getString("distance","");
        float maxSpeed = sharedPreferences .getFloat("maxSpeed", 0);
        int totalTime = sharedPreferences .getInt("totalTime", 0);
        int restTime = sharedPreferences .getInt("restTime", 0);
        boolean statusRun = sharedPreferences .getBoolean("statusRun", false);
        String mCyclingPoints= sharedPreferences .getString("mCyclingPoints", "");
        Log.e("Home", mCurrentLantitude);
        Log.e("Home", mCurrentLongitude);
        Log.e("Home", maxLantitude);
        Log.e("Home", maxtLongitude);
        Log.e("Home", minLantitude);
        Log.e("Home", mintLongitude);
        Log.e("Home", distance);
        Log.e("Home", mCyclingPoints);
        Log.e("Home", maxSpeed+"");
        Log.e("Home", totalTime+"");
        Log.e("Home", restTime+"");
        Log.e("Home", statusRun+"");
        if (totalTime != 0){
            Log.e("Home", "上一次有数据的啦");
        }
    }

    @Override
    protected void onDestroy() {
        Log.e("Home", "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onPostResume() {
        Log.e("Home", "onPostResume");
        super.onPostResume();
    }

    @Override
    protected void onStop() {
        Log.e("Home", "onStop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.e("Home", "onPause");
        super.onPause();
    }
}

package com.hm.fragmentusedemo.adapter;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by chenchao on 16/9/30.
 * cc@cchao.org
 * viewpage循环adapter
 */
public class LoopPageAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

    private final String TAG = "LoopPageAdapter";

    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;
    private Fragment mCurrentPrimaryItem = null;

    private List<Fragment> fragmentList;

    private OnPageSelectd onPageSelectd;

    private static int fragmentSize = 1;

    private ViewPager viewPager;

    private int nowSelect;

    public LoopPageAdapter(ViewPager viewPager, FragmentManager fm, List<Fragment> fragmentList) {
        mFragmentManager = fm;
        this.fragmentList = fragmentList;
        fragmentSize = fragmentList.size();
        viewPager.setOnPageChangeListener(this);
        this.viewPager = viewPager;
        nowSelect = fragmentSize * 1000;
    }

    public void setOnPageSelectd(OnPageSelectd onPageSelectd) {
        this.onPageSelectd = onPageSelectd;
    }

    public Fragment getItem(int position) {
        return fragmentList.get(position % fragmentSize);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    /**
     * TODO setCurrentItem只能增大
     *
     * @param position
     */
    public void setCurrentItem(int position) {
        int index = position - (nowSelect % fragmentSize);
        if (index < 0) {
            index = fragmentSize + index;
        }
        viewPager.setCurrentItem(nowSelect + index, false);
    }

    @Override
    public void startUpdate(ViewGroup container) {
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        position = position % fragmentSize;
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        final long itemId = getItemId(position);

        String name = makeFragmentName(container.getId(), itemId);
        Fragment fragment = mFragmentManager.findFragmentByTag(name);
        if (fragment != null) {
            mCurTransaction.attach(fragment);
        } else {
            fragment = getItem(position);
            if (!fragment.isAdded()) {
                mCurTransaction.add(container.getId(), fragment, makeFragmentName(container.getId(), itemId));
            }
        }
        if (fragment != mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    public long getItemId(int position) {
        return position;
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        nowSelect = position;
        if (onPageSelectd != null) {
            onPageSelectd.onPageSelected(position % fragmentList.size());
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public interface OnPageSelectd {
        void onPageSelected(int position);
    }
}

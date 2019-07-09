package com.hu.tr1.Adapter;

import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.hu.tr1.ViewPagerOperator;

import java.security.Policy;
import java.security.acl.LastOwnerException;
import java.util.List;

public class ViewAdapter extends PagerAdapter {

    private final String TAG = "ViewAdapter";
    private List<View> datas;

    public ViewAdapter(List<View> list) {
        datas = list;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = datas.get(position);
        view.setTag(position);
        ViewPagerOperator.initRecyclerView(view, position);
        ViewPagerOperator.setPageRenewed(true, position);
        Log.d(TAG, "viewpage " + position);
        container.addView(view);
        return view;
    }

    /**
     * 有数据修改时，页面一定重绘
     * @param object
     * @return
     */
    @Override
    public int getItemPosition(@NonNull Object object) {
        int tag = (int) ((View) object).getTag();
        if (ViewPagerOperator.getPageRenewed(tag))
            return POSITION_UNCHANGED;
        else
            return POSITION_NONE;
    }
    //按钮下的线的动画


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        container.removeView(datas.get(position));
        Log.v(TAG, "destroy item " + position);
        container.removeView((View) object);
    }


}

package com.ly.wvp.widget.progress;

import android.view.View;

/**
 * Created by jiao on 2019/12/31.
 */
public abstract class OnSubsectionSeekBarChangeListener {
    /**
     * 进度条改变后的监听
     *
     * @param view     本view
     * @param progress 进度
     * @param fromUser 是否是用户改变
     */
    public void onProgressChanged(View view, int progress, boolean fromUser) {
    }

    /**
     * 触发开始的点击事件
     *
     * @param view 本view
     */
    public void onStartTrackingTouch(View view) {
    }

    /**
     * 触发结束后的点击事件
     *
     * @param view 本view
     */
    public  void onStopTrackingTouch(View view) {
    }

    public  void onKeyTouch(int person, float x) {
    }
}

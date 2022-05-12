package com.aoyihala.screenmediaplayer.util

import android.app.Activity
import android.app.Application
import android.view.SurfaceView
import android.widget.ImageView
import com.aoyihala.screenmediaplayer.R
import com.hjq.xtoast.XToast

/**
 * 作者:夏涛
 * 创建日期:2022/5/10 17:07
 * 描述:
 **/
object FloatUtil {

    /**
     * 初始化完成后返回给外部一个imageview
     */
    fun showFloatWindow(activity:Application):ImageView?{
        var imageView:ImageView?=null
        var pair:Pair<SurfaceView,ImageView>?=null
        XToast<XToast<*>>(activity).apply {
            setContentView(R.layout.info_view)
            // 设置成可拖拽的
            setDraggable()
            // 设置动画样式
            setAnimStyle(android.R.style.Animation_Translucent)
            // 设置外层是否能被触摸
            setOutsideTouchable(true)
            // 设置窗口背景阴影强度
            //setBackgroundDimAmount(0.5f)
            imageView = contentView.findViewById(R.id.img_float)
        }.show()
        return imageView
    }
}
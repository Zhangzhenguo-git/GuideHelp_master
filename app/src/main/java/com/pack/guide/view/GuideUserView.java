package com.pack.guide.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pack.guide.R;


public class GuideUserView extends RelativeLayout implements ViewTreeObserver.OnGlobalLayoutListener {
    private final static String TAG =GuideUserView.class.getSimpleName();
    private final OnClickListener mClickListener;
    private Context mContent;
    private final Paint mPaint = new Paint();
    private final RectF mRect = new RectF();
    private ViewEntity[] mViews; // 目标View、自定义布局等参数实体数组
    private boolean handleTouch = false;
    private TextView tvConfirm;

    /**
     * 显示半透明引导图（默认显示一次）
     * @param views 目标View和自定义布局等参数实体
     */
    public static void show(Context context, ViewEntity... views) {
        show(true, null, context, views);
    }

    /**
     * 显示半透明引导图（默认显示一次）
     * @param clickListener 点击监听
     * @param views         目标View和自定义布局等参数实体
     */
    public static void show(OnClickListener clickListener, Context context, ViewEntity... views) {
        show(true, clickListener, context, views);
    }

    /**
     * 显示半透明引导图
     * @param isOnlyShowOne 是否只显示一次
     * @param clickListener 点击监听
     * @param views         目标View和自定义布局等参数实体
     */
    public static void show(boolean isOnlyShowOne, OnClickListener clickListener, Context context, ViewEntity... views) {
        try {
            if (isOnlyShowOne) {
                ViewEntity ve = views[0];
                int targetViewID = ve.mTargetView != null ? ve.mTargetView.getId() : View.NO_ID;
                String viewID = targetViewID + "_" + ve.mCustomLayoutID;
                SharedPreferences sp = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
                if (sp.getBoolean(viewID, false))
                    return;
                sp.edit().putBoolean(viewID, true).apply();
            }
            new GuideUserView(context, clickListener, views);
        } catch (Throwable ignored) {
        }
    }

    private GuideUserView(Context context, OnClickListener clickListener, ViewEntity... views) {
        super(context);
        mContent = context;
        mClickListener = clickListener;
        mViews = views;
        mPaint.setAntiAlias(true);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT)); // 去除两次绘图的交集
        setBackgroundColor(Color.TRANSPARENT);
        getViewTreeObserver().addOnGlobalLayoutListener(this);
        ((FrameLayout) ((Activity) mContent).getWindow().getDecorView()).addView(this);

		 /*
			在Activity上面添加半透明View，有两种方法：
			方法一
			((FrameLayout) ((Activity) mContent).getWindow().getDecorView()).addView(this);
			((FrameLayout) ((Activity) mContent).getWindow().getDecorView()).removeView(this);
			使用DecorView().addView，半透明View会被PopupWindow，Dialog等弹窗遮挡！

			方法二
			WindowManager.LayoutParams params = new WindowManager.LayoutParams();
			params.format = PixelFormat.TRANSLUCENT;
			((Activity) mContent).getWindow().getWindowManager().addView(this, params);
			((Activity) mContent).getWindow().getWindowManager().removeView(this);
			该方法能覆盖PopupWindow，Dialog等弹窗，但有时获取目标View坐标偶尔会失效！
			获取目标View坐标方法：View.getLocationOnScreen, View.getLocationOnScreen, View.getGlobalVisibleRect, View.getLocalVisibleRect
		*/
    }

    @Override
    public void onGlobalLayout() {
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
        // 在目标View周围添加自定义布局
        for (final ViewEntity ve : mViews) {
            // 目标View中心坐标
            if (ve.mTargetView != null) {
                ve.targetW = ve.mTargetView.getWidth() / 2;
                ve.targetH = ve.mTargetView.getHeight() / 2;
                ve.mTargetView.getLocationOnScreen(ve.mCenter);
                ve.mCenter[0] += ve.targetW;
                ve.mCenter[1] += ve.targetH;
            }
            // 目标View方位
            final View view = LayoutInflater.from(getContext()).inflate(ve.mCustomLayoutID, this, false);
            LayoutParams params = (LayoutParams) view.getLayoutParams();
            tvConfirm=view.findViewById(R.id.viewGuide);
            if (ve.mDirection != null) {
                int width = getWidth();
                int height = getHeight();
                int left = ve.mCenter[0] - ve.targetW;
                int right = ve.mCenter[0] + ve.targetW;
                int top = ve.mCenter[1] - ve.targetH;
                int bottom = ve.mCenter[1] + ve.targetH;
                switch (ve.mDirection) {
                    case TOP:
                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        params.setMargins(left, 0, 0, height - top);
                        break;
                    case LEFT:
                        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        params.setMargins(0, top, width - left, 0);
                        break;
                    case BOTTOM:
                        params.setMargins(left, bottom, 0, 0);
                        break;
                    case RIGHT:
                        params.setMargins(right, top, 0, 0);
                        break;
                    case LEFT_TOP:
                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        params.setMargins(0, 0, width - left, height - top);
                        break;
                    case LEFT_BOTTOM:
                        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        params.setMargins(0, bottom, width - left, 0);
                        break;
                    case RIGHT_TOP:
                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        params.setMargins(right, 0, 0, height - top);
                        break;
                    case RIGHT_BOTTOM:
                        params.setMargins(right, bottom, 0, 0);
                        break;
                }
            } else {
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
            }
            addViewInLayout(view, -1, params, true); // 添加view, 不会重新布局
            tvConfirm.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((FrameLayout) ((Activity) mContent).getWindow().getDecorView()).removeView(view);
                }
            });
        }
        requestLayout(); // 统一重新布局
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG); // 新建图层
        canvas.drawColor(0x77000000); // 绘制半透明背景
        // 在目标View周围裁剪出高亮圆圈
        for (ViewEntity ve : mViews) {
            if (ve.targetW == 0 || ve.targetH == 0)
                return;
            if (ve.mShape == Shape.ELLIPSE) { // 椭圆
                mRect.left = ve.mCenter[0] - ve.targetW;
                mRect.top = ve.mCenter[1] - ve.targetH;
                mRect.right = ve.mCenter[0] + ve.targetW;
                mRect.bottom = ve.mCenter[1] + ve.targetH;
                canvas.drawOval(mRect, mPaint);
            } else if (ve.mShape == Shape.RECTANGULAR) { // 矩形
                mRect.left = ve.mCenter[0] - ve.targetW;
                mRect.top = ve.mCenter[1] - ve.targetH;
                mRect.right = ve.mCenter[0] + ve.targetW;
                mRect.bottom = ve.mCenter[1] + ve.targetH;
                canvas.drawRoundRect(mRect, 16, 16, mPaint);
            } else if (ve.mShape == Shape.CIRCULAR) { // 圆形
                canvas.drawCircle(ve.mCenter[0], ve.mCenter[1], ve.targetW, mPaint);
            }
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (!handleTouch) {
//            handleTouch = false;
//            if (mClickListener != null){
//
//            }
//        }
//        return true;
//    }

    /**
     * 目标View和自定义布局等参数实体
     */
    public static class ViewEntity {
        private View mTargetView; // 目标View
        private int[] mCenter = new int[2]; // 目标View中心坐标
        private Shape mShape; // 目标View高亮圆圈形状
        private int targetW = -1; // 目标View高亮宽半径
        private int targetH = -1; // 目标View高亮高半径

        private int mCustomLayoutID; // 自定义布局资源ID
        private Direction mDirection; // 自定义布局相对于目标View的方向
        private boolean intercept; // 自定义布局是否允许点击事件

        public ViewEntity(View targetView, int customLayoutID, Direction direction,boolean intercept) {
            this(targetView, null, Shape.RECTANGULAR, customLayoutID, direction,intercept);
        }

        public ViewEntity(View targetView, int[] targetSize, Shape shape, int customLayoutID, Direction direction,boolean intercept) {
            this.mTargetView = targetView;
            this.mShape = shape;
            this.mCustomLayoutID = customLayoutID;
            this.mDirection = direction;
            this.intercept = intercept;
            if (targetSize != null) {
                targetW = targetSize[0] / 2;
                targetH = targetSize[1] / 2;
            }
        }
    }

    public enum Direction { // 相对于目标View的方位
        LEFT, TOP, RIGHT, BOTTOM, LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM
    }

    public enum Shape { // 目标View的高亮圆圈形状: 圆形，椭圆，圆角矩形
        CIRCULAR, ELLIPSE, RECTANGULAR
    }
}

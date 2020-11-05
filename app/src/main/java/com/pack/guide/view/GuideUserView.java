package com.pack.guide.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pack.guide.R;
import com.pack.guide.listener.AnimationListenerAdapter;


public class GuideUserView extends RelativeLayout implements ViewTreeObserver.OnGlobalLayoutListener , View.OnClickListener {
    private final static String TAG = GuideUserView.class.getSimpleName();
    private OnClickListener mClickListener;
    private Context mContent;
    private Paint mPaint = new Paint();
    private RectF mRect = new RectF();
    //    屏幕触摸事件
    private boolean handleTouch = false;
    private TextView tvGuideMessage;
    private TextView tvConfirm;
    // 目标View
    private View mTargetView;
    // 目标View中心坐标
    private int[] mCenter = new int[2];
    // 目标View高亮圆圈形状
    private Shape mShape;
    // 目标View高亮宽半径
    private int targetW = -1;
    // 目标View高亮高半径
    private int targetH = -1;
    // 目标View圆角大小
    private int round;
    // 自定义布局资源ID
    private int mLayoutID;
    // 自定义布局相对于目标View的方向
    private Direction mDirection;
    //是否只显示一次,默认fase
    private boolean isOnlyShowOne = false;
    //    进入动画效果
    private Animation enterAnimation;
    //    退出动画效果
    private Animation exitAnimation;

    //    内矩形与当前布局的外边距
    private final static int MARGINBOX = 30;
    //    内矩形与外虚线矩形的内边距
    private final static int PADDINGBOX = 20;
    //    图文混排
    private SpannableString mSpannableName;
    //    蒙层点击回调
    private OnClickCallback mOnClickCallback;
    //    蒙层点击屏幕后退出
    private boolean mOnClickExit = false;

    /**
     * 显示半透明引导图（默认显示一次）
     */

    public GuideUserView(Context context) {
        super(context);
        this.mContent = context;
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
        // 目标View中心坐标
        if (mTargetView != null) {
            targetW = mTargetView.getWidth() / 2;
            targetH = mTargetView.getHeight() / 2;
            mTargetView.getLocationOnScreen(mCenter);
            mCenter[0] += targetW;
            mCenter[1] += targetH;
        }
        // 目标View方位
        final View view = LayoutInflater.from(mContent).inflate(mLayoutID, this, false);
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        tvGuideMessage = view.findViewById(R.id.tvGuideMessage);
        tvConfirm = view.findViewById(R.id.tvConfirm);
        tvConfirm.setOnClickListener(this);
        tvGuideMessage.setText(mSpannableName);
        if (mDirection != null) {
            int width = getWidth();
            int height = getHeight();

            int left = mCenter[0] - targetW;
            int right = mCenter[0] + targetW;
            int top = mCenter[1] - targetH;
            int bottom = mCenter[1] + targetH;
            switch (mDirection) {
                case TOP:
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    params.setMargins(left, 0, 0, height - top);
                    break;
                case LEFT:
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.setMargins(0, top, width - left, 0);
                    break;
                case BOTTOM:
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
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
        requestLayout(); // 统一重新布局
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG); // 新建图层
        canvas.drawColor(0x77000000); // 绘制半透明背景
        // 在目标View周围裁剪出高亮圆圈
        if (targetW == 0 || targetH == 0)
            return;
        switch (mShape) {
            // 椭圆
            case ELLIPSE:
                canvas.drawOval(getEllipse(), mPaint);
                break;
            // 矩形
            case RECTANGULAR:
                canvas.drawRoundRect(getRectangular(), round, round, mPaint);
                break;
            // 圆形
            case CIRCULAR:
                canvas.drawCircle(mCenter[0], mCenter[1], targetW, mPaint);
                break;
//                双层矩形框(实线和虚线)
            case ROUND_RECTANGLE_DASHGAP:
                canvas.drawRoundRect(getRectangular(), round, round, mPaint);
                canvas.drawRoundRect(getRectangularLine(), round, round, getPaintLine());
                break;
        }
    }
    /**
     * 绘制椭圆
     */
    private RectF getEllipse() {
        mRect = new RectF();
        mRect.left = mCenter[0] - targetW;
        mRect.top = mCenter[1] - targetH;
        mRect.right = mCenter[0] + targetW;
        mRect.bottom = mCenter[1] + targetH;
        return mRect;
    }

    /**
     * 绘制实线矩形
     *
     * @return
     */
    private RectF getRectangular() {
        mRect = new RectF();
//        mRect.left = mCenter[0] - targetW;
//        mRect.top = mCenter[1] - targetH;
//        mRect.right = mCenter[0] + targetW;
//        mRect.bottom = mCenter[1] + targetH;

        mRect.left = mCenter[0] - targetW + MARGINBOX + 20;
        mRect.top = mCenter[1] - targetH + MARGINBOX + 10;
        mRect.right = mCenter[0] + targetW - MARGINBOX - 20;
        mRect.bottom = mCenter[1] + targetH - MARGINBOX + 20;
        return mRect;
    }

    /**
     * 绘制虚线矩形
     *
     * @return
     */
    private RectF getRectangularLine() {
        mRect = new RectF();
        mRect.left = mCenter[0] - targetW + PADDINGBOX + 20;
        mRect.top = mCenter[1] - targetH + PADDINGBOX + 10;
        mRect.right = mCenter[0] + targetW - PADDINGBOX - 20;
        mRect.bottom = mCenter[1] + targetH - PADDINGBOX + 20;

        return mRect;
    }

    /**
     * 绘制虚线画笔
     *
     * @return
     */
    private Paint getPaintLine() {
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(getResources().getColor(R.color.white));
        mPaint.setStrokeWidth(5);
        mPaint.setPathEffect(new DashPathEffect(new float[]{15, 5}, 0));
        return mPaint;
    }

    /**
     * 显示半透明引导图
     */
    public void show() {
        try {
//            默认只显示一次
            if (isOnlyShowOne) {
                int targetViewID = mTargetView != null ? mTargetView.getId() : View.NO_ID;
                String viewID = targetViewID + "_" + mLayoutID;
                SharedPreferences sp = mContent.getSharedPreferences(TAG, Context.MODE_PRIVATE);
                if (sp.getBoolean(viewID, false))
                    return;
                sp.edit().putBoolean(viewID, true).apply();
            }
            addGuideView();
        } catch (Throwable ignored) {
        }
    }

    /**
     * 实例化画笔添加蒙层
     */
    public void addGuideView() {
        // 保留未被源像素覆盖的目标像素。*丢弃被源像素覆盖的目标像素。丢弃所有*源像素
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        setBackgroundColor(Color.TRANSPARENT);
        getViewTreeObserver().addOnGlobalLayoutListener(this);
        ((FrameLayout) ((Activity) mContent).getWindow().getDecorView()).addView(this);
    }

    /**
     * 动画进入效果
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (enterAnimation != null) {
            startAnimation(enterAnimation);
        }
    }
    /**
     * 蒙层点击事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mOnClickCallback != null) {
            if (!mOnClickExit) {
                removeGuideView();
                mOnClickCallback.onClickedGuideView();
            }
        }
        return true;
    }

    /**
     * 移除当前蒙层
     */
    public void removeGuideView() {
        Log.v(TAG, "remove");
        if (exitAnimation != null) {
            exitAnimation.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    mTargetView.getViewTreeObserver().removeOnGlobalLayoutListener(GuideUserView.this);
                    GuideUserView.this.removeAllViews();
                    ((FrameLayout) ((Activity) mContent).getWindow().getDecorView()).removeView(GuideUserView.this);
                }
            });
            startAnimation(exitAnimation);
        }
    }

    /**
     * 指定按钮点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvConfirm:
                if (mOnClickCallback != null) {
                    if (mOnClickExit) {
                        mOnClickCallback.onClickBtView();
                        System.out.println("执行12312312312312312");
                        removeGuideView();
                    }
                }
                break;
        }
    }

    /**
     * 目标View和自定义布局等参数实体
     */
    public static class Builder {
        GuideUserView guideUserView;

        public Builder(Context context) {
            if (guideUserView==null){
                guideUserView = new GuideUserView(context);
            }
            guideUserView.mContent = context;
        }

        /**
         * 设置高亮View
         *
         * @param mTargetView
         * @return
         */
        public Builder setTargetView(View mTargetView) {
            guideUserView.mTargetView = mTargetView;
            return this;
        }

        /**
         * 设置高亮View形状
         *
         * @param mShape
         * @return
         */
        public Builder setShapeType(Shape mShape) {
            guideUserView.mShape = mShape;
            return this;
        }

        /**
         * 设置自定义提示布局
         *
         * @param mLayoutID
         * @return
         */
        public Builder setLayoutID(int mLayoutID) {
            guideUserView.mLayoutID = mLayoutID;
            return this;
        }

        /**
         * 设置自定义提示布局位于高亮View的位置
         *
         * @param mDirection
         * @return
         */
        public Builder setDirection(Direction mDirection) {
            guideUserView.mDirection = mDirection;
            return this;
        }

        /**
         * 设置高亮view大小
         *
         * @param mTargetSize
         * @return
         */
        public Builder setTargetSize(int[] mTargetSize) {
            if (mTargetSize != null) {
                guideUserView.targetW = mTargetSize[0] / 2;
                guideUserView.targetH = mTargetSize[1] / 2;
            }
            return this;
        }

        /**
         * 圆角大小
         * @param round
         * @return
         */
        public Builder setRound(int round) {
            guideUserView.round = round;
            return this;
        }

        /**
         * 是否只显示一次
         */
        public Builder setIsOnlyShowOne(boolean isOnlyShowOne) {
            guideUserView.isOnlyShowOne = isOnlyShowOne;
            return this;
        }

        /**
         * 设置图文混排
         */
        public Builder setSpannableName(SpannableString spannableName) {
            guideUserView.mSpannableName = spannableName;
            return this;
        }

        public Builder setEnterAnimation(Animation enterAnimation) {
            guideUserView.enterAnimation = enterAnimation;
            return this;
        }

        public Builder setExitAnimation(Animation exitAnimation) {
            guideUserView.exitAnimation = exitAnimation;
            return this;
        }

        /**
         * 显示蒙层引导
         */
        public void show() {
            guideUserView.show();
        }

        /**
         * 按钮点击退出
         *
         * @param onclickExit
         * @return
         */
        public Builder setOnclickExit(boolean onclickExit) {
            guideUserView.mOnClickExit = onclickExit;
            return this;
        }

        /**
         * 蒙层点击事件回调
         *
         * @param callback
         * @return
         */
        public Builder setOnclickListener(OnClickCallback callback) {
            guideUserView.mOnClickCallback = callback;
            return this;
        }

        /**
         * 蒙层点击事件回调
         *
         * @param onClickListener
         * @return
         */
        public Builder setOnClickListener(OnClickListener onClickListener) {
            guideUserView.mClickListener = onClickListener;
            return this;
        }
    }

    public enum Direction { // 相对于目标View的方位
        LEFT, TOP, RIGHT, BOTTOM, LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM
    }

    public enum Shape { // 目标View的高亮圆圈形状: 圆形，椭圆，圆角矩形,虚线矩形
        CIRCULAR, ELLIPSE, RECTANGULAR, ROUND_RECTANGLE_DASHGAP
    }

    /**
     * GuideUserView点击事件回调
     */
    public interface OnClickCallback {
        void onClickedGuideView();
        void onClickBtView();
    }
}

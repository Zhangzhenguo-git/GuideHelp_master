package com.pack.guide.view;

import android.graphics.DashPathEffect;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.view.View;
public class HighLight {

    private View mHole;
    private Shape shape = Shape.RECTANGLE;
    /**
     * 圆角，仅当shape = Shape.ROUND_RECTANGLE才生效
     */
    private int round;
    /**
     * 高亮相对view的padding
     */
    private int padding;

    public static HighLight newInstance(View view) {
        return new HighLight(view);
    }

    private HighLight(View hole) {
        this.mHole = hole;
    }

    public HighLight setShape(Shape shape) {
        this.shape = shape;
        return this;
    }

    public HighLight setRound(int round) {
        this.round = round;
        return this;
    }

    public HighLight setPadding(int padding) {
        this.padding = padding;
        return this;
    }

    public int getPadding() {
        return padding;
    }

    public Shape getShape() {
        return shape;
    }

    public int getRound() {
        return round;
    }

    public int getRadius() {
        return mHole != null ? Math.max(mHole.getWidth() / 2, mHole.getHeight() / 2) : 0;
    }

    /**
     * 矩形上下左右角
     * @return
     */
    public RectF getRectF() {
        RectF rectF = new RectF();
        int[] location = new int[2];
        //        获取此View的坐标 x、y轴
        mHole.getLocationOnScreen(location);
        rectF.left = location[0] - padding;
        rectF.top = location[1] - padding;
        rectF.right = location[0] + mHole.getWidth() + padding * 2;
        rectF.bottom = location[1] + mHole.getHeight() + padding * 2;
        return rectF;
    }

    /**
     * 虚线矩形外边框
     * @return
     */
    public RectF getRectFExternal() {
        RectF rectF = new RectF();
        int[] location = new int[2];
        //        获取此View的坐标 x、y轴
        mHole.getLocationOnScreen(location);
        rectF.left = location[0] - padding-15;
        rectF.top = location[1] - padding-15;
        rectF.right = location[0] + mHole.getWidth() + padding * 2+15;
        rectF.bottom = location[1] + mHole.getHeight() + padding * 2+15;
        return rectF;
    }

    public DashPathEffect getEffect(){
        return new DashPathEffect(new float[]{15,5},0);
    }

    public enum Shape {
        CIRCLE,//圆形
        RECTANGLE, //矩形
        OVAL,//椭圆
        ROUND_RECTANGLE,//圆角矩形
        ROUND_RECTANGLE_DASHGAP;//圆角矩形（虚线）
    }

}
package com.pack.guide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.pack.guide.databinding.ActivityMainBinding;
import com.pack.guide.listener.OnGuideChangedListener;
import com.pack.guide.listener.OnLayoutInflatedListener;
import com.pack.guide.listener.OnPageChangedListener;
import com.pack.guide.view.Controller;
import com.pack.guide.view.GuidePage;
import com.pack.guide.view.HighLight;
import com.pack.guide.view.NewbieGuide;
import com.xuexiang.xui.widget.guidview.GuideCaseView;
import com.xuexiang.xui.widget.guidview.OnViewInflateListener;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main);
        new GuideCaseView.Builder(this)
                .focusBorderColor(getResources().getColor(R.color.colorAccent))
                .focusBorderSize(5)
                .focusOn(binding.tvGuideView)
                .focusRectAtPosition((int)(binding.tvGuideView.getX()),(int)binding.tvGuideView.getX(),binding.tvGuideView.getWidth(),binding.tvGuideView.getHeight())
                .customView(R.layout.guide_title_view, new OnViewInflateListener() {
                    @Override
                    public void onViewInflated(View view) {

                    }
                }).show();
    }

    public void showGuide() {
        Animation enterAnimation = new AlphaAnimation(0f, 1f);
        enterAnimation.setDuration(600);
        enterAnimation.setFillAfter(true);

        Animation exitAnimation = new AlphaAnimation(1f, 0f);
        exitAnimation.setDuration(100);
        exitAnimation.setFillAfter(true);

        //新增多页模式，即一个引导层显示多页引导内容
        NewbieGuide.with(this)
                .setLabel("page")//设置引导层标示区分不同引导层，必传！否则报错
                .setOnGuideChangedListener(new OnGuideChangedListener() {
                    @Override
                    public void onShowed(Controller controller) {
                        //引导层显示
                    }

                    @Override
                    public void onRemoved(Controller controller) {
                        //引导层消失（多页切换不会触发）
                    }
                })
                .setOnPageChangedListener(new OnPageChangedListener() {
                    @Override
                    public void onPageChanged(int page) {
                        //引导页切换，page为当前页位置，从0开始
                    }
                })
                .alwaysShow(false)//是否每次都显示引导层，默认false，只显示一次
                .addGuidePage(//添加一页引导页
                        GuidePage.newInstance()//创建一个实例
//                                设置要添加图层引导的View、圆角、最终样式
                                .addHighLight(binding.tvGuideView, 20,HighLight.Shape.ROUND_RECTANGLE_DASHGAP)
                                .setLayoutRes(R.layout.guide_title_view)//设置引导页布局
                                .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                                    @Override
                                    public void onLayoutInflated(View view) {
                                        //引导页布局填充后回调，用于初始化
                                        TextView tv = (TextView) view.findViewById(R.id.viewGuide);
                                        tv.setText("知道了");
                                    }
                                })
                                .setEnterAnimation(enterAnimation)//进入动画
                                .setExitAnimation(exitAnimation)//退出动画
                ).show();//显示引导层(至少需要一页引导页才能显示)
    }
}
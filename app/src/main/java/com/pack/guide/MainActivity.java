package com.pack.guide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.pack.guide.databinding.ActivityMainBinding;
import com.pack.guide.listener.OnGuideChangedListener;
import com.pack.guide.listener.OnLayoutInflatedListener;
import com.pack.guide.view.Controller;
import com.pack.guide.view.GuidePage;
import com.pack.guide.view.GuideUserView;
import com.pack.guide.view.HighLight;
import com.pack.guide.view.NewbieGuide;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main);


//        GuideUserView.show(false, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        Toast.makeText(MainActivity.this, "1111", Toast.LENGTH_SHORT).show();
//                    }
//                }, MainActivity.this,
//                new GuideUserView.ViewEntity(binding.tvGuideView, R.layout.guide_title_view, GuideUserView.Direction.BOTTOM,false),
//                new GuideUserView.ViewEntity(binding.tvGuideView1, R.layout.guide_title_view, GuideUserView.Direction.BOTTOM,false));

//        图文混排并水平居中
        String s="1、长按图标出现删除按钮  ，点击即可删除入口；\n2、删除后，点击加号＋可添加常用应用。";
        SpannableString spannableString=new SpannableString(s);
        ImageSpan imageSpan=new ImageSpan(this,R.mipmap.science_delete, ImageSpan.ALIGN_CENTER);
        spannableString.setSpan(imageSpan,12,13, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        new GuideUserView.Builder(this)
                .setTargetView(binding.tvGuideView)
                .setRound(20)
                .setTargetSize(null)
                .setShapeType(GuideUserView.Shape.ROUND_RECTANGLE_DASHGAP)
                .setDirection(GuideUserView.Direction.BOTTOM)
                .setLayoutID(R.layout.guide_title_view)
                .setSpannableName(spannableString)
                .setIntercpt(false)
                .setIsOnlyShowOne(false)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "11", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();

//        showGuide();
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
                .alwaysShow(true)//是否每次都显示引导层，默认false，只显示一次
                .addGuidePage(//添加一页引导页
                        GuidePage.newInstance()//创建一个实例
//                                设置要添加图层引导的View、圆角、最终样式
                                .addHighLight(binding.tvGuideView, 20,HighLight.Shape.ROUND_RECTANGLE_DASHGAP)
//                                .setLayoutRes(R.layout.guide_title_view)//设置引导页布局
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
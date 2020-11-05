package com.pack.guide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.pack.guide.databinding.ActivityMainBinding;
import com.pack.guide.view.GuideUserView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.tvGuideView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guide();
            }
        });
        guide();
    }


    private void guide() {
        //        图文混排并水平居中
        String s = "1、长按图标出现删除按钮  ，点击即可删除入口；\n2、删除后，点击加号＋可添加常用应用。";
        final SpannableString spannableString = new SpannableString(s);
        ImageSpan imageSpan = new ImageSpan(this, R.mipmap.science_delete);
        spannableString.setSpan(imageSpan, 12, 13, ImageSpan.ALIGN_CENTER);

        final Animation enterAnimation = new AlphaAnimation(0f, 1f);
        enterAnimation.setDuration(600);
        enterAnimation.setFillAfter(true);

        final Animation exitAnimation = new AlphaAnimation(1f, 0f);
        exitAnimation.setDuration(100);
        exitAnimation.setFillAfter(true);

        new GuideUserView.Builder(this)
                .setTargetView(binding.tvGuideView)
                .setRound(20)
                .setTargetSize(null)
                .setShapeType(GuideUserView.Shape.ROUND_RECTANGLE_DASHGAP)
                .setDirection(GuideUserView.Direction.BOTTOM)
                .setLayoutID(R.layout.guide_title_view)
                .setSpannableName(spannableString)
                .setEnterAnimation(enterAnimation)
                .setExitAnimation(exitAnimation)
                .setIsOnlyShowOne(true)
                .setOnclickExit(false)
                .setOnclickListener(new GuideUserView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        System.out.println("点击屏幕任意位置关闭");
                    }

                    @Override
                    public void onClickBtView() {
                        System.out.println("点击指定按钮关闭");
                        new GuideUserView.Builder(MainActivity.this)
                                .setTargetView(binding.tvGuideView1)
                                .setRound(20)
                                .setTargetSize(null)
                                .setShapeType(GuideUserView.Shape.ROUND_RECTANGLE_DASHGAP)
                                .setDirection(GuideUserView.Direction.BOTTOM)
                                .setLayoutID(R.layout.guide_title_view)
                                .setSpannableName(spannableString)
                                .setEnterAnimation(enterAnimation)
                                .setExitAnimation(exitAnimation)
                                .setIsOnlyShowOne(false)
                                .setOnclickExit(true)
                                .setOnclickListener(new GuideUserView.OnClickCallback() {
                                    @Override
                                    public void onClickedGuideView() {
                                        System.out.println("点击屏幕任意位置关闭");
                                    }

                                    @Override
                                    public void onClickBtView() {
                                        System.out.println("点击指定按钮关闭");
                                    }
                                })
                                .show();
                    }
                })
                .show();
    }

}
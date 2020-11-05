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

        binding.tvGuideViewBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guide(v,true,false,GuideUserView.Direction.BOTTOM);
            }
        });
        binding.tvGuideViewLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guide(v,true,false,GuideUserView.Direction.LEFT);
            }
        });
        binding.tvGuideViewRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guide(v,true,false,GuideUserView.Direction.RIGHT);
            }
        });
        binding.tvGuideViewTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guide(binding.tvGuideViewTop,true,false,GuideUserView.Direction.TOP);
            }
        });
        guide(binding.tvGuideViewBottom,true,false,GuideUserView.Direction.LEFT_BOTTOM);
    }

    /**
     * 显示蒙层
     * @param isNotRestartShow
     */
    private void guide(View view,final boolean isBtClick,final boolean isNotRestartShow,final GuideUserView.Direction type){
        //        图文混排并水平居中
//        String s = "欢迎使用欢迎使用欢迎使用欢迎使用欢迎使用";
//        final SpannableString spannableString = new SpannableString(s);
//        ImageSpan imageSpan = new ImageSpan(this, R.mipmap.delete);
//        spannableString.setSpan(imageSpan, 12, 13, ImageSpan.ALIGN_CENTER);

        final Animation enterAnimation = new AlphaAnimation(0f, 1f);
        enterAnimation.setDuration(600);
        enterAnimation.setFillAfter(true);

        final Animation exitAnimation = new AlphaAnimation(1f, 0f);
        exitAnimation.setDuration(100);
        exitAnimation.setFillAfter(true);

        new GuideUserView.Builder(this)
                .setTargetView(view)
                .setRound(20)
                .setTargetSize(null)
                .setShapeType(GuideUserView.Shape.RECTANGULAR)
                .setDirection(type)
                .setLayoutID(R.layout.test_guide_view)
//                .setSpannableName();
                .setEnterAnimation(enterAnimation)
                .setExitAnimation(exitAnimation)
                .setOnclickExit(isBtClick)
                .setIsOnlyShowOne(isNotRestartShow)
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

}
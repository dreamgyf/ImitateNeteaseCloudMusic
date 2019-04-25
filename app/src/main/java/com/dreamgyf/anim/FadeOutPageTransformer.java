package com.dreamgyf.anim;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

public class FadeOutPageTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(@NonNull View view, float position) {
        int pageWidth = view.getWidth();

        if (position < -1) {
            view.setAlpha(0);

        } else if (position <= 1) {
            if(position <= 0){
                view.setTranslationX(-pageWidth*position);
                if (position == -1)
                    view.setTranslationX(0);
            } else {
                view.setTranslationX(-pageWidth*position);
                if (position == 1)
                    view.setTranslationX(0);
            }
            view.setAlpha(1 - Math.abs(position));
        } else {
            view.setAlpha(0);
        }
    }
}

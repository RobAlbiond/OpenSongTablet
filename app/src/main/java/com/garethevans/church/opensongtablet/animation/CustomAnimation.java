package com.garethevans.church.opensongtablet.animation;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import com.garethevans.church.opensongtablet.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CustomAnimation {

    public void faderAnimation(final View v, int time, boolean fadeIn) {
        int start = 1;
        int end = 0;
        if (fadeIn) {
            start = 0;
            end = 1;
        }
        v.setAlpha(start);
        v.setVisibility(View.VISIBLE);
        Animation fader = new AlphaAnimation(start, end);

        if (fadeIn) {
            fader.setInterpolator(new DecelerateInterpolator()); //add this
        } else {
            fader.setInterpolator(new AccelerateInterpolator()); //add this
        }
        fader.setDuration(time);

        AnimationSet animation = new AnimationSet(false); //change to false
        animation.addAnimation(fader);
        v.setAnimation(animation);
        int finalEnd = end;
        v.postDelayed(() -> {
            if (finalEnd == 0) {
                v.setVisibility(View.GONE);
            }
        },time);
    }

    public void fadeActionButton(FloatingActionButton fab, float fadeTo) {
        new Handler().postDelayed(() -> {
            fab.setAlpha(1.0f);
            fab.animate().alpha(fadeTo).setDuration(800);
            },400);
    }

    public void pulse(Context c, View v) {
        v.startAnimation(AnimationUtils.loadAnimation(c, R.anim.pulse));
    }
}

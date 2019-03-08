package com.hu.tr_v1;

import android.animation.ObjectAnimator;
import android.util.Log;

public class AnimationOperator {

    private static final String TAG = "AnimationOperator";
    private static final int fadingDuration = 300;
    private static InfoPanel infoPanel;

    public void setInfoPanel(InfoPanel infoPanel) {
        this.infoPanel = infoPanel;
    }

    public static void infoPanelIn(SingleTask task){
        Log.d(TAG, "fading in panel");
        ObjectAnimator animator = ObjectAnimator.ofFloat(infoPanel,
                "alpha", 1f);
        animator.setDuration(fadingDuration);
        animator.start();

        infoPanel.setInit(task);
    }

    public static void infoPanelOut(){
        Log.d(TAG, "fading out info panel");
        ObjectAnimator animator = ObjectAnimator.ofFloat(infoPanel,
                "alpha", 0f);
        animator.setDuration(fadingDuration);
        animator.start();
    }

}

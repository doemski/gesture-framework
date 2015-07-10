package hs_mannheim.pattern_interaction_model.animation;

import android.view.View;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

/**
 * Scales a view with a bouncing effect, using the rebound spring framework.
 */
public class ScaleSpring {

    /**
     * Calling the constructor attaches to bounce effect to the view and starts it immediately.
     * @param view The view to attach to
     */
    public ScaleSpring(final View view) {
        SpringSystem springSystem = SpringSystem.create();
        Spring spring = springSystem.createSpring();
        spring.setSpringConfig(new SpringConfig(55, 3));
        spring.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring spring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float value = (float) spring.getCurrentValue();
                float scale = 1f - (value * 0.5f);
                view.setScaleX(scale);
                view.setScaleY(scale);
            }
        });

        spring.setEndValue(1);
    }
}

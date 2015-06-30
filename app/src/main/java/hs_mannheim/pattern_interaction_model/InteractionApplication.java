package hs_mannheim.pattern_interaction_model;

import android.app.Application;
import android.net.wifi.p2p.WifiP2pInfo;

import hs_mannheim.pattern_interaction_model.model.InteractionContext;

public class InteractionApplication extends Application {

    private InteractionContext mInteractionContext;

    public InteractionContext getInteractionContext() {
        return mInteractionContext;
    }

    public void setInteractionContext(InteractionContext mInteractionContext) {
        this.mInteractionContext = mInteractionContext;
    }
}

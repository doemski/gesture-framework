package hs_mannheim.pattern_interaction_model;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.Point;
import android.hardware.SensorManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.View;

import hs_mannheim.pattern_interaction_model.connection.PostOffice;
import hs_mannheim.pattern_interaction_model.connection.bluetooth.BluetoothChannel;
import hs_mannheim.pattern_interaction_model.connection.wifidirect.WifiDirectChannel;
import hs_mannheim.pattern_interaction_model.gesture.bump.BumpDetector;
import hs_mannheim.pattern_interaction_model.gesture.bump.Threshold;
import hs_mannheim.pattern_interaction_model.gesture.shake.ShakeDetector;
import hs_mannheim.pattern_interaction_model.gesture.stitch.StitchDetector;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeDetector;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeDirectionConstraint;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeDurationConstraint;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeEvent;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeOrientationConstraint;
import hs_mannheim.pattern_interaction_model.model.GestureDetector;
import hs_mannheim.pattern_interaction_model.model.IConnection;
import hs_mannheim.pattern_interaction_model.model.IViewContext;
import hs_mannheim.pattern_interaction_model.model.InteractionContext;
import hs_mannheim.pattern_interaction_model.model.Selection;

public class ConfigurationBuilder {

    private final Context mContext;
    private IViewContext mViewContext;
    private IConnection mConnection;
    private Selection mSelection;
    private GestureDetector mDetector;
    private PostOffice mPostOffice;

    public ConfigurationBuilder(Context context, IViewContext viewContext) {
        mContext = context;
        mViewContext = viewContext;
    }

    public ConfigurationBuilderC withBluetooth() {
        mConnection = new BluetoothChannel(BluetoothAdapter.getDefaultAdapter());
        mPostOffice = new PostOffice(mConnection);
        return new ConfigurationBuilderC();
    }

    public ConfigurationBuilderC withWifiDirect() {
        WifiP2pManager wifiP2pManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        WifiP2pManager.Channel channel = wifiP2pManager.initialize(mContext, mContext.getMainLooper(), null);
        mConnection = new WifiDirectChannel(wifiP2pManager, channel, mContext);
        mPostOffice = new PostOffice(mConnection);
        return new ConfigurationBuilderC();
    }

    public class ConfigurationBuilderC {
        public ConfigurationBuilderG shake() {
            mDetector = new ShakeDetector((SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE), mViewContext);
            return new ConfigurationBuilderG();
        }

        public ConfigurationBuilderG bump() {
            mDetector = new BumpDetector((SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE), Threshold.LOW, mViewContext);
            return new ConfigurationBuilderG();
        }

        public ConfigurationBuilderG swipe() {
            mDetector = createSwipeDetector(null);
            return new ConfigurationBuilderG();
        }

        public ConfigurationBuilderG stitch() {
            mDetector = createStitchDetector();
            return new ConfigurationBuilderG();
        }

        private StitchDetector createStitchDetector() {
            StitchDetector stitchDetector = new StitchDetector(mPostOffice, mViewContext);
            stitchDetector.addConstraint(new SwipeOrientationConstraint(SwipeEvent.Orientation.EAST));
            stitchDetector.addConstraint(new SwipeDurationConstraint(1000));
            return stitchDetector;

        }

        private SwipeDetector createSwipeDetector(SwipeDetector.SwipeEventListener listener) {
            return new SwipeDetector(mViewContext)
                    .addConstraint(new SwipeDirectionConstraint(SwipeEvent.Direction.HORIZONTAL))
                    .addConstraint(new SwipeDurationConstraint(250))
                    .addConstraint(new SwipeOrientationConstraint(SwipeEvent.Orientation.WEST))
                    .addSwipeListener(listener);
        }
    }

    public class ConfigurationBuilderG {

        public ConfigurationBuilderG() {
            mSelection = Selection.Empty;
        }

        public ConfigurationBuilderG select(Selection selection) {
            mSelection = selection;
            return this;
        }

        public void buildAndRegister() {
            InteractionContext interactionContext = new InteractionContext(mDetector, mSelection, mConnection, mPostOffice);
            ((InteractionApplication) mContext).setInteractionContext(interactionContext);
        }
    }
}
package hs_mannheim.gestureframework.model;

import android.view.View;

import java.util.ArrayList;

import hs_mannheim.gestureframework.gesture.swipe.SwipeDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeEvent;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;

public class GestureManager implements GestureDetector.GestureEventListener, SwipeDetector.SwipeEventListener{

    private GestureDetector mConnectDetector, mSelectDetector, mTransferDetector, mDisconnectDetector;

    //TODO: Make those Threadsafe
    private final ArrayList<IInteractionListener> mConnectListeners = new ArrayList<IInteractionListener>();
    private final ArrayList<IInteractionListener> mSelectListeners = new ArrayList<IInteractionListener>();
    private final ArrayList<IInteractionListener> mTransferListeners = new ArrayList<IInteractionListener>();
    private final ArrayList<IInteractionListener> mDisconnectListeners = new ArrayList<IInteractionListener>();

    private IViewContext mViewContext;

    /**
     *
     * @param connectDetector nullable
     * @param selectDetector nullable
     * @param transferDetector nullable
     * @param disconnectDetector nullable
     */
    public GestureManager(GestureDetector connectDetector, GestureDetector selectDetector, GestureDetector transferDetector, GestureDetector disconnectDetector) {
        mConnectDetector = (connectDetector == null) ? new VoidGestureDetector(mViewContext) : connectDetector;
        mSelectDetector = (selectDetector == null) ? new VoidGestureDetector(mViewContext) : selectDetector;
        mTransferDetector = (transferDetector == null) ? new VoidGestureDetector(mViewContext) : transferDetector;
        mDisconnectDetector = (disconnectDetector == null) ? new VoidGestureDetector(mViewContext) : disconnectDetector;

        mConnectDetector.registerGestureEventListener(this);
        mSelectDetector.registerGestureEventListener(this);
        mTransferDetector.registerGestureEventListener(this);
        mDisconnectDetector.registerGestureEventListener(this);
    }

    public void setViewContext(GestureContext gestureContext, IViewContext viewContext) {
        switch (gestureContext) {
            case CONNECT:
                mConnectDetector.setViewContext(viewContext);
            case SELECT:
                mSelectDetector.setViewContext(viewContext);
            case TRANSFER:
                mTransferDetector.setViewContext(viewContext);
            case DISCONNECT:
                mDisconnectDetector.setViewContext(viewContext);
        }
    }

    public void setViewContextAll(IViewContext viewContext){
        mConnectDetector.setViewContext(viewContext);
        mSelectDetector.setViewContext(viewContext);
        mTransferDetector.setViewContext(viewContext);
        mDisconnectDetector.setViewContext(viewContext);
    }

    /**
     *
     * @param gestureContext
     * @return The desired GestureDetector. CAN BE A VoidGestureDetector!
     */
    public GestureDetector getGestureDetector(GestureContext gestureContext){
        switch (gestureContext){
            case CONNECT:
                return mConnectDetector;
            case SELECT:
                return mSelectDetector;
            case TRANSFER:
                return mTransferDetector;
            case DISCONNECT:
                return mDisconnectDetector;
            default:
                return new VoidGestureDetector(mViewContext);
        }
    }

    public void setGestureDetector(GestureContext gestureContext, GestureDetector gestureDetector){
        switch (gestureContext) {
            case CONNECT:
                mConnectDetector = (gestureDetector == null) ? new VoidGestureDetector(mViewContext) : gestureDetector;
                mConnectDetector.registerGestureEventListener(this);
            case SELECT:
                mSelectDetector = (gestureDetector == null) ? new VoidGestureDetector(mViewContext) : gestureDetector;
                mSelectDetector.registerGestureEventListener(this);
            case TRANSFER:
                mTransferDetector = (gestureDetector == null) ? new VoidGestureDetector(mViewContext) : gestureDetector;
                mTransferDetector.registerGestureEventListener(this);
            case DISCONNECT:
                mDisconnectDetector = (gestureDetector == null) ? new VoidGestureDetector(mViewContext) : gestureDetector;
                mDisconnectDetector.registerGestureEventListener(this);
        }
    }

    //TODO: Threadsafe
    public void registerGestureEventListenerAll(IInteractionListener gestureListener){
        if (!mConnectListeners.contains(gestureListener)) {mConnectListeners.add(gestureListener);}
        if (!mSelectListeners.contains(gestureListener)) {mSelectListeners.add(gestureListener);}
        if (!mTransferListeners.contains(gestureListener)) {mTransferListeners.add(gestureListener);}
        if (!mDisconnectListeners.contains(gestureListener)) {mDisconnectListeners.add(gestureListener);}
    }

    //TODO: Threadsafe!
    public void registerGestureEventListener(GestureContext gestureContext, IInteractionListener gestureListener){
        switch (gestureContext) {
            case CONNECT:
                if (!mConnectListeners.contains(gestureListener)) {mConnectListeners.add(gestureListener);}
            case SELECT:
                if (!mSelectListeners.contains(gestureListener)) {mSelectListeners.add(gestureListener);}
            case TRANSFER:
                if (!mTransferListeners.contains(gestureListener)) {mTransferListeners.add(gestureListener);}
            case DISCONNECT:
                if (!mDisconnectListeners.contains(gestureListener)) {mDisconnectListeners.add(gestureListener);}
        }
    }

    public void unregisterGestureEventListener(GestureContext gestureContext, IInteractionListener gestureListener){
        switch (gestureContext) {
            case CONNECT:
                if (!mConnectListeners.contains(gestureListener)) {mConnectListeners.add(gestureListener);}
            case SELECT:
                if (!mSelectListeners.contains(gestureListener)) {mSelectListeners.add(gestureListener);}
            case TRANSFER:
                if (!mTransferListeners.contains(gestureListener)) {mTransferListeners.add(gestureListener);}
            case DISCONNECT:
                if (!mDisconnectListeners.contains(gestureListener)) {mDisconnectListeners.add(gestureListener);}
        }
    }

    @Override
    public void onGestureDetected(GestureDetector gestureDetector) {
        if(gestureDetector.equals(mConnectDetector)){
            for(IInteractionListener gestureListener : mConnectListeners){
                gestureListener.onConnect();
            }
        } else if(gestureDetector.equals(mSelectDetector)){
            for(IInteractionListener gestureListener : mSelectListeners){
                gestureListener.onSelect();
            }
        } else if(gestureDetector.equals(mTransferDetector)){
            for(IInteractionListener gestureListener : mTransferListeners){
                gestureListener.onTransfer();
            }
        } else if(gestureDetector.equals(mDisconnectDetector)){
            for(IInteractionListener gestureListener : mDisconnectListeners){
                gestureListener.onDisconnect();
            }
        }
    }

    @Override
    public void onSwipeDetected(SwipeDetector swipeDetector, SwipeEvent event) {

    }

    @Override
    public void onSwiping(SwipeDetector swipeDetector, TouchPoint touchPoint) {

    }

    @Override
    public void onSwipeStart(SwipeDetector swipeDetector, TouchPoint touchPoint, View view) {

    }

    @Override
    public void onSwipeEnd(SwipeDetector swipeDetector, TouchPoint touchPoint) {

    }

    public interface GestureListener {
        void onGestureDetected();
        void onSwipeDetected(SwipeEvent event);
        void onSwiping(TouchPoint touchPoint);
        void onSwipeStart(TouchPoint touchPoint, View view);
        void onSwipeEnd(TouchPoint touchPoint);
    }
}

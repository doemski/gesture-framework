package hs_mannheim.gestureframework.model;

import android.database.Observable;
import android.view.View;

import hs_mannheim.gestureframework.gesture.swipe.SwipeEvent;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;


public class InteractionContext extends Observable<AllEventsListener> implements IPacketReceiver, IInteractionListener {

    private final GestureManager mGestureManager;
    private final Selection mSelection;
    private final IConnection mConnection;
    private final IPostOffice mPostOffice;

    public InteractionContext(GestureManager gestureManager, Selection selection, IConnection connection, IPostOffice postOffice) {
        mGestureManager = gestureManager;
        mSelection = selection;
        mConnection = connection;
        mPostOffice = postOffice; /* only PostOffice talks to the connection */

        mGestureManager.registerGestureEventListenerAll(this);
    }

    public IPostOffice getPostOffice() {
        return mPostOffice;
    }

    public IConnection getConnection() {
        return this.mConnection;
    }

    public void updateSelection(Packet data) {
        mSelection.updateSelection(data);
    }

    public void updateViewContextAll(IViewContext viewContext) {
        mGestureManager.setViewContextAll(viewContext);
    }

    public void updateViewContext(GestureContext gestureContext, IViewContext viewContext) {
        mGestureManager.setViewContext(gestureContext, viewContext);
    }

    public GestureDetector getGestureDetector(GestureContext gestureContext){
        return this.mGestureManager.getGestureDetector(gestureContext);
    }

    private void notifyTransferStarted() {
        for (AllEventsListener listener : mObservers) {
            listener.onTransferStarted();
        }
    }

    @Override
    public void receive(Packet packet) {
        //todo: register to postoffice and distribute stuff
    }

    @Override
    public boolean accept(PacketType type) {
        return false;
    }

    public GestureManager getGestureManager() {
        return mGestureManager;
    }

    @Override
    public void onConnect() {
        notifyTransferStarted();
        mPostOffice.send(mSelection.getData());
    }

    @Override
    public void onSelect() {

    }

    @Override
    public void onTransfer() {

    }

    @Override
    public void onDisconnect() {

    }
}

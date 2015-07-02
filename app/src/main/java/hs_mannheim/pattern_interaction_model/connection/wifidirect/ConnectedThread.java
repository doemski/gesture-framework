package hs_mannheim.pattern_interaction_model.connection.wifidirect;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import hs_mannheim.pattern_interaction_model.model.Packet;

public class ConnectedThread extends Thread {
    private final String TAG = "[WifiP2P ConnectedThread]";

    private final Socket mSocket;
    private final WifiDirectChannel mChannel;

    private final InputStream mInStream;
    private final OutputStream mOutStream;
    private ObjectOutputStream mObjectOutputStream;

    public ConnectedThread(Socket socket, WifiDirectChannel channel) {
        mSocket = socket;
        mChannel = channel;

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Could not acquire streams from socket");
        }

        mInStream = tmpIn;
        mOutStream = tmpOut;

        mChannel.connected(this);
    }

    public void run() {
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(mInStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                //TODO: This still crashes!
                Packet data = (Packet) objectInputStream.readObject();
                mChannel.receive(data);

            } catch (IOException e) {
                Log.e(TAG, "IO Exception: " + e.getMessage());
                this.cancel();

                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();

                break;
            } catch (NullPointerException e) {
                Log.e(TAG, "boom");
            }
        }
    }

    /**
     * Write data to socket through output stream.
     *
     * @param packet to send
     */
    public void write(Packet packet) {
        try {
            if(mObjectOutputStream == null) {
                mObjectOutputStream = new ObjectOutputStream(mOutStream);
            }
            mObjectOutputStream.writeObject(packet);
        } catch (IOException e) {
            Log.e(TAG, "Error writing packet to stream");
        }
    }

    /**
     * Close the socket and notify the channel about it.
     */
    public void cancel() {
        try {
            mObjectOutputStream.close();
            mChannel.disconnected();
        } catch (IOException e) {
            Log.e(TAG, "Error closing client connection");
        }
    }
}
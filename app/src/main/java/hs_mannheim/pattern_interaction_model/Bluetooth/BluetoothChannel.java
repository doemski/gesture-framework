package hs_mannheim.pattern_interaction_model.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

import hs_mannheim.pattern_interaction_model.MainActivity;

public class BluetoothChannel {
    private final String TAG = "[BluetoothChannel]";
    private final UUID MY_UUID = UUID.fromString("0566981a-1c02-11e5-9a21-1697f925ec7b");
    private BluetoothDevice mConnectedDevice;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothListener mListener;

    private boolean isConnected = false;
    private ConnectedThread mConnectionThread;

    public BluetoothChannel(BluetoothAdapter bluetoothAdapter, BluetoothListener listener) {
        this.mBluetoothAdapter = bluetoothAdapter;
        mListener = listener;
    }

    public BluetoothDevice getConnectedDevice() {
        return this.isConnected() ? this.mConnectedDevice : null;
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    public boolean write(String message) {
        if(isConnected) {
            mConnectionThread.write(message.getBytes());
            return true;
        }

        return false;
    }

    public void connect(String address) {
        BluetoothDevice deviceToConnect = mBluetoothAdapter.getRemoteDevice(address);
        this.mConnectedDevice = deviceToConnect;

        Log.d(TAG, String.format("Device to connect to: %s", deviceToConnect));

        if (MainActivity.MODEL.equals("Nexus 4")) {
            Log.d(TAG, "Connecting as server.");
            new AcceptThread(this).start();
        } else {
            Log.d(TAG, "Connecting as client.");
            new ConnectThread(deviceToConnect, this).start();
        }
    }

    private void receive(String data) {
        Log.d(TAG, "Data received: " + data);
        this.mListener.onDataReceived(data);
    }

    private void connected(ConnectedThread connectionThread) {
        isConnected = true;
        this.mConnectionThread = connectionThread;
        mListener.onConnectionEstablished();
    }


    public void close() {
        this.mConnectionThread.cancel();
        this.mListener.onConnectionLost();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothChannel mChannel;

        public ConnectThread(BluetoothDevice device, BluetoothChannel channel) {
            BluetoothSocket tmp = null;
            mChannel = channel;

            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.d(TAG, "Could not connect");
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // block
                mmSocket.connect();
            } catch (IOException connectException) {
                Log.d(TAG, String.format("Could not connect: %s", connectException.getMessage()));

                this.cancel();

                return;
            }

            new ConnectedThread(mmSocket, mChannel).start();
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing socket");
            }
        }
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;
        private BluetoothChannel mChannel;

        public AcceptThread(BluetoothChannel channel) {
            this.mChannel = channel;

            BluetoothServerSocket tmp = null;
            try {
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("My App", MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Could not open Server Socket");
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket;

            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Error listening for incoming connections");
                    break;
                }

                if (socket != null) {
                    ConnectedThread connectedThread = new ConnectedThread(socket, mChannel);
                    connectedThread.start();

                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error connecting to client");
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing Server Socket");
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private BluetoothChannel mChannel;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket, BluetoothChannel channel) {
            mmSocket = socket;
            mChannel = channel;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Could not acquire streams from socket");
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

            mChannel.connected(this);
        }

        public void run() {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(mmInStream));

            while (true) {
                try {
                    mChannel.receive(stdIn.readLine());
                } catch (IOException e) {
                    Log.d(TAG, "IO Exception!");
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "Error sending data to remote device");
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing client connection");
            }
        }
    }
}

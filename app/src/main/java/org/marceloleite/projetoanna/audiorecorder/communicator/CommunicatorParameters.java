package org.marceloleite.projetoanna.audiorecorder.communicator;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

/**
 * The parameters required to construct a {@link Communicator} object.
 */
public class CommunicatorParameters {

    /**
     * The socket which represents the bluetooth connection with the audio recorder.
     */
    private final BluetoothSocket bluetoothSocket;

    /**
     * The application being executed.
     */
    private final AppCompatActivity appCompatActivity;

    /**
     * Constructor.
     *
     * @param bluetoothSocket   The socket which represents the bluetooth connection with the audio recorder.
     * @param appCompatActivity The appCompatActivity of the application being executed.
     */
    public CommunicatorParameters(BluetoothSocket bluetoothSocket, AppCompatActivity appCompatActivity) {
        this.bluetoothSocket = bluetoothSocket;
        this.appCompatActivity = appCompatActivity;
    }

    /**
     * Returns the socket which represents the bluetooth connection with the audio recorder.
     *
     * @return The socket which represents the bluetooth connection with the audio recorder.
     */
    BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    /**
     * Returns the appCompatActivity of the application being executed.
     *
     * @return The appCompatActivity of the application being executed.
     */
    AppCompatActivity getAppCompatActivity() {
        return appCompatActivity;
    }
}

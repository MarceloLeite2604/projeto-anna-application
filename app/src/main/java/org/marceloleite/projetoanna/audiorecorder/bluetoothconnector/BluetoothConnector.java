package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.connector.AsyncTaskConnectWithAudioRecorder;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.connector.ConnectWithAudioRecorderInterface;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.connector.ConnectWithAudioRecorderParameters;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.connector.ConnectWithAudioRecorderResult;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.Pairer;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.PairerInterface;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.PairerParameters;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.PairingResult;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.selectdevice.AlertDialogSelectBluetoothDevice;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.selectdevice.SelectBluetoothDeviceInterface;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.selectdevice.SelectBluetoothDeviceParameters;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.selectdevice.SelectBluetoothDeviceResult;
import org.marceloleite.projetoanna.utils.Log;

import java.io.IOException;
import java.util.Set;

/**
 * Establish the connection between the application and the audio recorder.
 */
public class BluetoothConnector implements PairerInterface, SelectBluetoothDeviceInterface, ConnectWithAudioRecorderInterface {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = BluetoothConnector.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The code used to identify the intent to request the bluetooth activation.
     */
    public static final int ENABLE_BLUETOOTH_REQUEST_CODE = 0x869a;

    /**
     * The objects which contains the bluetooth connection attempt parameters and the method to be executed after its conclusion.
     */
    private BluetoothConnectorInterface bluetoothConnectorInterface;

    /**
     * The parameters for bluetooth connection.
     */
    private BluetoothConnectorParameters bluetoothConnectorParameters;

    /**
     * The device's bluetooth adapter.
     */
    private BluetoothAdapter bluetoothAdapter;

    /**
     * The device selected to establish a bluetooth connection as the audio recorder.
     */
    private BluetoothDevice bluetoothDeviceAudioRecorder;

    /**
     * The bluetooth socket which represents the connection between this application and the audio recorder.
     */
    private BluetoothSocket bluetoothSocket;

    /**
     * Object constructor.
     *
     * @param bluetoothConnectorInterface The objects which contains the bluetooth connection attempt parameters and the method to be executed after its conclusion.
     */
    public BluetoothConnector(BluetoothConnectorInterface bluetoothConnectorInterface) throws IOException {
        this.bluetoothDeviceAudioRecorder = null;
        this.bluetoothSocket = null;
        this.bluetoothConnectorInterface = bluetoothConnectorInterface;
        this.bluetoothConnectorParameters = bluetoothConnectorInterface.getBluetoothConnectionParameters();
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            throw new IOException("This device does not have a bluetooth adapter.");
        }
    }

    /**
     * Returns the bluetooth device selected.
     *
     * @return The bluetooth device selected.
     */
    public BluetoothDevice getBluetoothDeviceAudioRecorder() {
        return bluetoothDeviceAudioRecorder;
    }

    /**
     * Returns the bluetooth socket which represents the connection between the application and the audio recorder.
     *
     * @return The bluetooth socket which represents the connection between the application and the audio recorder.
     */
    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    /**
     * Starts the connection process with the audio recorder.
     */
    public void startConnectionProcess() {
        if (isBluetoothAdapterActivated()) {
            checkDeviceToConnect();
        } else {
            requestBluetoothAdapterActivation();
        }
    }

    /**
     * Checks if the bluetooth adapter is activated.
     *
     * @return True if the bluetooth adapter is activated. False otherwise.
     */
    private boolean isBluetoothAdapterActivated() {
        return bluetoothAdapter.isEnabled();
    }

    /**
     * Requests the activation of the bluetooth adapter for Android.
     */
    private void requestBluetoothAdapterActivation() {
        Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        bluetoothConnectorParameters.getAppCompatActivity().startActivityForResult(enableBluetoothIntent, BluetoothConnector.ENABLE_BLUETOOTH_REQUEST_CODE);
    }

    /**
     * Receives the bluetooth adapter activation result from Android.
     *
     * @param resultCode The code returned from the bluetooth adapter activation request.
     */
    public void requestBluetoothAdapterActivationResult(int resultCode) {
        switch (resultCode) {
            case AppCompatActivity.RESULT_OK:
                checkDeviceToConnect();
                break;
            default:
                BluetoothConnectorResult bluetoothConnectorResult = new BluetoothConnectorResult(BluetoothConnectorReturnCodes.BLUETOOTH_ACTIVATION_DENIED, null);
                finishBluetoothConnectionProcess(bluetoothConnectorResult);
                break;
        }
    }

    /**
     * Checks if it is necessary for the user to select a bluetooth device to connect.
     */
    private void checkDeviceToConnect() {
        if (bluetoothDeviceAudioRecorder == null) {
            selectBluetoothDeviceToConnect();
        } else {
            connectWithAudioRecorder();
        }
    }

    /**
     * Open the alert dialog asking the user to start bluetooth discovering process.
     */
    private void selectBluetoothDeviceToConnect() {
        Set<BluetoothDevice> pairedBluetoothDevices = bluetoothAdapter.getBondedDevices();

        if (pairedBluetoothDevices.size() == 0) {
            new Pairer(this).startPairingProcess();
        } else {
            new AlertDialogSelectBluetoothDevice(this);
        }
    }

    @Override
    public void pairingResult(PairingResult pairingResult) {
        BluetoothConnectorResult bluetoothConnectorResult;
        switch (pairingResult.getReturnCode()) {
            case BluetoothConnectorReturnCodes.SUCCESS:
                bluetoothDeviceAudioRecorder = pairingResult.getBluetoothDevice();
                connectWithAudioRecorder();
                break;
            case BluetoothConnectorReturnCodes.PAIRING_FAILED:
                BluetoothDevice bluetoothDevice = pairingResult.getBluetoothDevice();
                /* TODO: Return the bluetoothDevice as a bluetooth connection result. */
                String device = bluetoothDevice.getName();
                if (device == null || device.isEmpty()) {
                    device = bluetoothDevice.getAddress();
                }
                Toast.makeText(bluetoothConnectorParameters.getAppCompatActivity(), "Failed to pair with device \"" + device + "\".", Toast.LENGTH_LONG).show();
                bluetoothConnectorResult = new BluetoothConnectorResult(BluetoothConnectorReturnCodes.PAIRING_FAILED, null);
                finishBluetoothConnectionProcess(bluetoothConnectorResult);
                break;
            case BluetoothConnectorReturnCodes.DISCOVERING_CANCELLED:
                bluetoothConnectorResult = new BluetoothConnectorResult(BluetoothConnectorReturnCodes.DISCOVERING_CANCELLED, null);
                finishBluetoothConnectionProcess(bluetoothConnectorResult);
                break;
            default:
                throw new RuntimeException("Unknown code returned from pairing process.");
        }
    }

    @Override
    public void bluetoothDeviceSelected(SelectBluetoothDeviceResult selectBluetoothDeviceResult) {
        switch (selectBluetoothDeviceResult.getReturnCode()) {
            case BluetoothConnectorReturnCodes.SUCCESS:
                bluetoothDeviceAudioRecorder = selectBluetoothDeviceResult.getBluetoothDevice();
                connectWithAudioRecorder();
                break;
            case BluetoothConnectorReturnCodes.DEVICE_SELECTION_CANCELLED:
                BluetoothConnectorResult bluetoothConnectorResult = new BluetoothConnectorResult(BluetoothConnectorReturnCodes.DEVICE_SELECTION_CANCELLED, null);
                finishBluetoothConnectionProcess(bluetoothConnectorResult);
                break;
            default:
                Log.e(LOG_TAG, "bluetoothDeviceSelected (217): Unknown code returned from bluetooth device selection.");
                throw new RuntimeException("Unknown code returned from bluetooth device selection.");
        }
    }


    /**
     * Starts the process which will attempt to connect with the bluetooth device selected.
     */
    private void connectWithAudioRecorder() {
        AsyncTaskConnectWithAudioRecorder asyncTaskConnectWithAudioRecorder = new AsyncTaskConnectWithAudioRecorder(this);
        asyncTaskConnectWithAudioRecorder.execute();
    }

    @Override
    public void connectWithAudioRecorderFinished(ConnectWithAudioRecorderResult connectWithAudioRecorderResult) {
        BluetoothConnectorResult bluetoothConnectorResult;
        String device;
        switch (connectWithAudioRecorderResult.getReturnCode()) {
            case BluetoothConnectorReturnCodes.SUCCESS:
                /* TODO: Create a new AudioRecord object and return it. */
                device = bluetoothDeviceAudioRecorder.getName();
                if (device == null || device.isEmpty()) {
                    device = bluetoothDeviceAudioRecorder.getAddress();
                }
                Toast.makeText(bluetoothConnectorParameters.getAppCompatActivity(), "Connected with audio recorder \"" + device + "\".", Toast.LENGTH_LONG).show();
                bluetoothConnectorResult = new BluetoothConnectorResult(BluetoothConnectorReturnCodes.SUCCESS, bluetoothSocket);
                break;
            case BluetoothConnectorReturnCodes.CONNECTION_FAILED:
                device = bluetoothDeviceAudioRecorder.getName();
                if (device == null || device.isEmpty()) {
                    device = bluetoothDeviceAudioRecorder.getAddress();
                }
                Toast.makeText(bluetoothConnectorParameters.getAppCompatActivity(), "Failed to connect with device \"" + device + "\".", Toast.LENGTH_LONG).show();
                bluetoothConnectorResult = new BluetoothConnectorResult(BluetoothConnectorReturnCodes.CONNECTION_FAILED, null);
                break;
            default:
                throw new RuntimeException("Unkonwn code returned from audio recorder connection attempt.");
        }
        finishBluetoothConnectionProcess(bluetoothConnectorResult);
    }

    private void finishBluetoothConnectionProcess(BluetoothConnectorResult bluetoothConnectorResult) {
        bluetoothConnectorInterface.bluetoothConnectionResult(bluetoothConnectorResult);
    }

    @Override
    public ConnectWithAudioRecorderParameters getConnectWithAudioRecorderParameters() {
        return new ConnectWithAudioRecorderParameters(bluetoothDeviceAudioRecorder, bluetoothConnectorParameters.getAppCompatActivity());
    }

    @Override
    public PairerParameters getPairerParameters() {
        return new PairerParameters(bluetoothConnectorParameters.getAppCompatActivity());
    }

    @Override
    public SelectBluetoothDeviceParameters getSelectBluetoothDeviceParameters() {
        return new SelectBluetoothDeviceParameters(bluetoothConnectorParameters.getAppCompatActivity());
    }

    /*@Override
    public void startDiscovery() {
        Pairer pairer = new Pairer(this);
        pairer.startBluetoothDeviceDiscovery();
    }*/
}

package org.marceloleite.projetoanna.bluetooth.connector;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.R;
import org.marceloleite.projetoanna.bluetooth.Bluetooth;

/**
 * Created by Marcelo Leite on 20/03/2017.
 */

public class AlertDialogConnectingToDevice extends AlertDialog {

    protected AlertDialogConnectingToDevice(AppCompatActivity appCompatActivity, BluetoothDevice bluetoothDevice) {
        super(appCompatActivity);
        setTitle("Connecting");
        setCancelable(true);
        LayoutInflater layoutInflater = appCompatActivity.getLayoutInflater();
        View bluetoothDeviceInfoView = layoutInflater.inflate(R.layout.bluetooth_device_connect, null);
        Bluetooth.fillBluetoothDeviceInformations(bluetoothDeviceInfoView, bluetoothDevice);
        setView(bluetoothDeviceInfoView);
    }

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        Log.d(MainActivity.LOG_TAG, "setOnCancelListener, 31: Cancelled.");
        super.setOnCancelListener(listener);
    }
}
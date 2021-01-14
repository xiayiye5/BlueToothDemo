package cn.xiayiye5.bluetoothdemo;

/**
 * @author : xiayiye5
 * @date : 2021/1/14 18:13
 * 类描述 :
 */

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 蓝牙连接线程
 */
public class ConnectBlueTask extends AsyncTask<BluetoothDevice, Integer, BluetoothSocket> {
    private BluetoothDevice bluetoothDevice;
    private ConnectBlueCallBack callBack;

    public ConnectBlueTask(ConnectBlueCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected BluetoothSocket doInBackground(BluetoothDevice... bluetoothDevices) {
        bluetoothDevice = bluetoothDevices[0];
        BluetoothSocket socket = null;
        try {
            String address = bluetoothDevice.getAddress();
            Log.d("蓝牙连接", "开始连接socket," + address);
            socket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            if (socket != null && !socket.isConnected()) {
                socket.connect();
                InputStream inputStream = socket.getInputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("蓝牙连接", "socket连接失败");
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
                Log.e("蓝牙连接", "socket关闭失败");
            }
        }
        return socket;
    }

    @Override
    protected void onPreExecute() {
        Log.d("蓝牙连接", "开始连接");
        if (callBack != null) {
            callBack.onStartConnect();
        }
    }

    @Override
    protected void onPostExecute(BluetoothSocket bluetoothSocket) {
        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
            Log.d("蓝牙连接", "连接成功");
            if (callBack != null) {
                callBack.onConnectSuccess(bluetoothDevice, bluetoothSocket);
            }
        } else {
            Log.d("蓝牙连接", "连接失败");
            if (callBack != null) {
                callBack.onConnectFail(bluetoothDevice, "连接失败");
            }
        }
    }
}

package cn.xiayiye5.bluetoothdemo;

/*
 * @author : xiayiye5
 * @date : 2021/1/14 18:13
 * 类描述 : 蓝牙连接失败参考帖子：https://www.cnblogs.com/zoro-zero/p/13390268.html
 * 在经典蓝牙连接时，经常出现“run: read failed, socket might closed or timeout, read ret: -1”
 * 主要原因是UUID的错误。
 * 非手机终端的UUID：00001101-0000-1000-8000-00805f9B34FB
 * 手机终端的UUID：00001105-0000-1000-8000-00805f9b34fb
 */

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
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
            //android 4.2以后方法
//            socket = (BluetoothSocket) bluetoothDevice.getClass().getDeclaredMethod("createRfcommSocket", new Class[]{int.class}).invoke(bluetoothDevice, 1);
            //Android 4.2以前方法
            socket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001105-0000-1000-8000-00805f9b34fb"));
            if (socket != null && !socket.isConnected()) {
                //开始连接
                socket.connect();
                try {
                    DataOutputStream mOut = new DataOutputStream(socket.getOutputStream());
                    //消息标记
                    mOut.writeInt(10098);
                    mOut.writeUTF("测试手机蓝牙");
                    mOut.flush();
                } catch (Throwable e) {
                    e.printStackTrace();
                    socket.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("蓝牙连接", "socket连接失败");
            try {
                socket.close();
            } catch (Exception e1) {
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

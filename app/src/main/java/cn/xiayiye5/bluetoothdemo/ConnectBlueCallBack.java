package cn.xiayiye5.bluetoothdemo;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * @author : xiayiye5
 * @date : 2021/1/14 18:15
 * 类描述 :
 */
public interface ConnectBlueCallBack {
    /**
     * 开始连接的回调方法
     */
    void onStartConnect();

    /**
     * 连接成功的回调
     *
     * @param bluetoothDevice 连接的设备
     * @param bluetoothSocket 蓝牙socket
     */
    void onConnectSuccess(BluetoothDevice bluetoothDevice, BluetoothSocket bluetoothSocket);

    /**
     * 连接失败
     *
     * @param bluetoothDevice 连接的设备
     * @param fail            失败
     */
    void onConnectFail(BluetoothDevice bluetoothDevice, String fail);
}

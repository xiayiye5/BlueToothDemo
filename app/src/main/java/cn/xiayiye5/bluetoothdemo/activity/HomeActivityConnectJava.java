package cn.xiayiye5.bluetoothdemo.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.xiayiye5.bluetoothdemo.ConnectBlueCallBack;
import cn.xiayiye5.bluetoothdemo.ConnectBlueTask;
import cn.xiayiye5.bluetoothdemo.R;
import cn.xiayiye5.bluetoothdemo.adapter.MyBlueListAdapterConnect;

/**
 * @author xiayiye5
 */
public class HomeActivityConnectJava extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, ConnectBlueCallBack {
    private ListView lvBlueList;
    private TextView tvStartScanner;
    private BluetoothAdapter defaultAdapter;
    private MyBlueListAdapterConnect myBlueListAdapter;
    private final List<BluetoothDevice> list = new ArrayList<>();
    private final List<BluetoothDevice> blueList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Java版本页面");
        setContentView(R.layout.activity_main_java);
        lvBlueList = findViewById(R.id.lvBlueList);
        tvStartScanner = findViewById(R.id.tv_start_scanner);
        //搜索开始的过滤器
        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        //搜索结束的过滤器
        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //寻找到设备的过滤器
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //绑定状态改变
        IntentFilter filter4 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //配对请求
        IntentFilter filter5 = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
        registerReceiver(mFindBlueToothReceiver, filter1);
        registerReceiver(mFindBlueToothReceiver, filter2);
        registerReceiver(mFindBlueToothReceiver, filter3);
        registerReceiver(mFindBlueToothReceiver, filter4);
        registerReceiver(mFindBlueToothReceiver, filter5);
        //初始化搜索蓝牙列表
        myBlueListAdapter = new MyBlueListAdapterConnect(this, list, blueList);
        lvBlueList.setAdapter(myBlueListAdapter);
        defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        //点击列表开始配对
        lvBlueList.setOnItemClickListener(this);
        lvBlueList.setOnItemLongClickListener(this);
        if (!defaultAdapter.isEnabled()) {
            //蓝牙设备未开启，自动开启蓝牙,此方法打开蓝牙后无回调
//            defaultAdapter.enable()
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 10010);
            return;
        }
        getBlueList();
    }

    /**
     * 获取已配过对的蓝牙列表
     */
    private void getBlueList() {
        Set<BluetoothDevice> bondedDevices = defaultAdapter.getBondedDevices();
        for (BluetoothDevice bondedDevice : bondedDevices) {
            Log.e("打印蓝牙", bondedDevice.getName() + "--" + bondedDevice.getAddress());
            list.add(bondedDevice);
        }
        myBlueListAdapter.notifyDataSetChanged();
    }


    public void getBlueToothList(View view) {
        if (view.getId() == R.id.tv_start_scanner) {
            //开始扫描蓝牙设备
            startScanner();
        }
    }

    private void startScanner() {
        //扫描蓝牙之前一定要申请ACCESS_FINE_LOCATION位置信息权限，否则Android6.0后会扫描不到蓝牙设备
        if (!checkPermission()) {
            return;
        }
        //搜索之前清空之前的记录
        blueList.clear();
        if (defaultAdapter.isDiscovering()) {
            defaultAdapter.cancelDiscovery();
        }
        defaultAdapter.startDiscovery();
        Log.e("打印线程", Thread.currentThread().getName());
        tvStartScanner.setText("开始搜索中………………");
    }


    /**
     * 广播接收器，当远程蓝牙设备被发现时，回调函数onReceiver()会被执行
     */
    private BroadcastReceiver mFindBlueToothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d("打印扫描", "开始扫描...");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d("打印扫描", "结束扫描...");
                tvStartScanner.setText("搜索完成...");
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                if (TextUtils.isEmpty(device.getAddress())) {
                    return;
                }
                blueList.add(device);
                Log.d("打印扫描", "发现第" + blueList.size() + "个蓝牙设备->" + device.getName() + "-" + device.getAddress());
                myBlueListAdapter.notifyDataSetChanged();
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                Log.d("打印扫描", device.getName() + "设备绑定状态改变...");
            }
        }
    };

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //检查是否已经获取到地理位置权限，如果没有!=，进行权限获取
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10086);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10086) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "已获得位置权限。", Toast.LENGTH_LONG).show();
                startScanner();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                ) {
                    Toast.makeText(this, "请授予位置权限！", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10010) {
            if (resultCode == Activity.RESULT_OK) {
                //可以获取列表操作等
                getBlueList();
            } else {
                Toast.makeText(this, "蓝牙没有开启", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除注册
        unregisterReceiver(mFindBlueToothReceiver);
        Log.e("destory", "解除注册");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //配对之前取消蓝牙扫描
        if (defaultAdapter.isDiscovering()) {
            defaultAdapter.cancelDiscovery();
        }
        if (position <= list.size()) {
            //上面布局已配对的蓝牙设备不让点击
            return;
        }
        BluetoothDevice blueDevice = blueList.get(position - list.size() - 1);
        if (blueDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
            //已配对
            Toast.makeText(this, "此设备已配对,无需再次配对！", Toast.LENGTH_LONG).show();
            //开始连接蓝牙设备
            connect(blueDevice, this);
        } else {
            //开始配对
            try {
                Method createBond = BluetoothDevice.class.getMethod("createBond");
                boolean isBond = (boolean) createBond.invoke(blueDevice);
                if (isBond) {
                    Toast.makeText(this, "配对成功！", Toast.LENGTH_LONG).show();
                    //开始连接蓝牙设备
                    connect(blueDevice, this);
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 连接 （在配对之后调用）
     *
     * @param device 设备
     */
    public void connect(BluetoothDevice device, ConnectBlueCallBack callBack) {
        //连接之前把扫描关闭
        if (defaultAdapter.isDiscovering()) {
            defaultAdapter.cancelDiscovery();
        }
        new ConnectBlueTask(callBack).execute(device);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //长按取消配对,跳转到系统蓝牙页面自行取消
        startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
        return false;
    }

    @Override
    public void onStartConnect() {
        Toast.makeText(this, "开始连接", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectSuccess(BluetoothDevice bluetoothDevice, BluetoothSocket bluetoothSocket) {
        Toast.makeText(this, "连接成功", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectFail(BluetoothDevice bluetoothDevice, String fail) {
        Toast.makeText(this, "连接失败", Toast.LENGTH_LONG).show();
    }
}

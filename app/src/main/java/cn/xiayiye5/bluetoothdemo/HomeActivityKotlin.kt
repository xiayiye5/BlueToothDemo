package cn.xiayiye5.bluetoothdemo

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.lang.reflect.InvocationTargetException
import kotlin.concurrent.thread


class HomeActivityKotlin : AppCompatActivity(), AdapterView.OnItemClickListener,
    AdapterView.OnItemLongClickListener, ConnectBlueCallBack {
    private lateinit var lvBlueList: ListView
    private lateinit var lvScanner: ListView
    private lateinit var tvStartScanner: TextView
    private lateinit var defaultAdapter: BluetoothAdapter
    private lateinit var myBlueListAdapter: MyBlueListAdapter
    private var list: MutableList<String?> = ArrayList()
    private var blueList: MutableList<BluetoothDevice?> = ArrayList()

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Kotlin版本页面"
        setContentView(R.layout.activity_main_java_and_kotlin)
        lvBlueList = findViewById(R.id.lvBlueList)
        lvScanner = findViewById(R.id.lv_scanner)
        tvStartScanner = findViewById(R.id.tv_start_scanner)
        //搜索开始的过滤器
        val filter1 = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        //搜索结束的过滤器
        val filter2 = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        //寻找到设备的过滤器
        val filter3 = IntentFilter(BluetoothDevice.ACTION_FOUND)
        //绑定状态改变
        val filter4 = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        //配对请求
        val filter5 = IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST)
        registerReceiver(mFindBlueToothReceiver, filter1)
        registerReceiver(mFindBlueToothReceiver, filter2)
        registerReceiver(mFindBlueToothReceiver, filter3)
        registerReceiver(mFindBlueToothReceiver, filter4)
        registerReceiver(mFindBlueToothReceiver, filter5)
        //初始化搜索蓝牙列表
        myBlueListAdapter = MyBlueListAdapter(this, blueList)
        lvScanner.adapter = myBlueListAdapter
        defaultAdapter = BluetoothAdapter.getDefaultAdapter()
        //点击列表开始配对
        lvScanner.onItemClickListener = this
        lvScanner.onItemLongClickListener = this
        if (!defaultAdapter.isEnabled) {
            //蓝牙设备未开启，自动开启蓝牙,此方法打开蓝牙后无回调
//            defaultAdapter.enable()
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, 10010)
            return
        }
        getBlueList()
    }

    /**
     * 获取已配过对的蓝牙列表
     */
    private fun getBlueList() {
        val bondedDevices = defaultAdapter.bondedDevices
        for (bondedDevice in bondedDevices) {
            Log.e("打印蓝牙", "${bondedDevice.name}-${bondedDevice.address}")
            list.add("${bondedDevice.name}-${bondedDevice.address}")
        }
        lvBlueList.adapter = MyAdapter(this, list)
    }

    fun getBlueToothList(view: View) {
        if (view.id == R.id.tv_start_scanner) {
            //开始扫描蓝牙设备
            startScanner()
        }
    }

    private fun startScanner() {
        //扫描蓝牙之前一定要申请ACCESS_FINE_LOCATION位置信息权限，否则Android6.0后会扫描不到蓝牙设备
        if (!checkPermission()) return
        thread {
            //搜索之前清空之前的记录
            blueList.clear()
            if (defaultAdapter.isDiscovering) {
                defaultAdapter.cancelDiscovery()
            }
            defaultAdapter.startDiscovery()
            Log.e("打印线程", Thread.currentThread().name)
        }
        tvStartScanner.text = "开始搜索中………………"
    }

    //广播接收器，当远程蓝牙设备被发现时，回调函数onReceiver()会被执行
    private val mFindBlueToothReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            when (action) {
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.d("打印扫描", "开始扫描...")
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.d("打印扫描", "结束扫描...")
                    tvStartScanner.text = "搜索完成..."
                }
                BluetoothDevice.ACTION_FOUND -> {
                    if (TextUtils.isEmpty(device?.address)) {
                        return
                    }
                    blueList.add(device)
                    Log.d("打印扫描", "发现第${blueList.size}个蓝牙设备->${device!!.name}-${device.address}")
                    myBlueListAdapter.notifyDataSetChanged()
                }
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    Log.d("打印扫描", "设备绑定状态改变...")
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //检查是否已经获取到地理位置权限，如果没有!=，进行权限获取
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    10086
                )
                return false
            } else {
                return true
            }
        } else {
            return true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10086) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "已获得位置权限。", Toast.LENGTH_LONG).show()
                startScanner()
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    Toast.makeText(this, "请授予位置权限！", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10010) {
            if (resultCode == Activity.RESULT_OK) {
                //可以获取列表操作等
                getBlueList()
            } else {
                Toast.makeText(this, "蓝牙没有开启", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //解除注册
        unregisterReceiver(mFindBlueToothReceiver)
        Log.e("destory", "解除注册")
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onItemClick(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ) {
        //配对之前取消蓝牙扫描
        if (defaultAdapter.isDiscovering) {
            defaultAdapter.cancelDiscovery()
        }
        val blueDevice = blueList[position]!!
        if (blueDevice.bondState == BluetoothDevice.BOND_BONDED) {
            //已配对
            Toast.makeText(this, "此设备已配对,无需再次配对！", Toast.LENGTH_LONG).show()
            //开始连接蓝牙设备
            connect(blueDevice, this)
        } else {
            //开始配对
            try {
                val createBond =
                    BluetoothDevice::class.java.getMethod("createBond")
                val isBond = createBond.invoke(blueDevice) as Boolean
                if (isBond) {
                    Toast.makeText(this, "配对成功！", Toast.LENGTH_LONG).show()
                    //开始连接蓝牙设备
                    connect(blueDevice, this)
                }
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 连接 （在配对之后调用）
     *
     * @param device 设备
     */
    fun connect(device: BluetoothDevice?, callBack: ConnectBlueCallBack?) {
        //连接之前把扫描关闭
        if (defaultAdapter.isDiscovering) {
            defaultAdapter.cancelDiscovery()
        }
        ConnectBlueTask(callBack).execute(device)
    }

    override fun onItemLongClick(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ): Boolean {
        //长按取消配对,跳转到系统蓝牙页面自行取消
        startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
        return false
    }

    override fun onStartConnect() {
        Toast.makeText(this, "开始连接", Toast.LENGTH_LONG).show()
    }

    override fun onConnectSuccess(
        bluetoothDevice: BluetoothDevice?,
        bluetoothSocket: BluetoothSocket?
    ) {
        Toast.makeText(this, "连接成功", Toast.LENGTH_LONG).show()
    }

    override fun onConnectFail(bluetoothDevice: BluetoothDevice?, fail: String?) {
        Toast.makeText(this, "连接失败", Toast.LENGTH_LONG).show()
    }
}
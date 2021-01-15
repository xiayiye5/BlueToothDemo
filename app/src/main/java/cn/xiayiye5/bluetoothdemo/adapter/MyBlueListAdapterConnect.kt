package cn.xiayiye5.bluetoothdemo.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import cn.xiayiye5.bluetoothdemo.R

/**
 * @author : xiayiye5
 * @date : 2021/1/14 15:19
 * 类描述 : 一个ListView实现联动效果
 */
class MyBlueListAdapterConnect(
    private val activity: Activity,
    private var list: MutableList<BluetoothDevice?>,
    var blueList: MutableList<BluetoothDevice?>
) :
    BaseAdapter() {
    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var inflate = View.inflate(activity, android.R.layout.simple_list_item_1, null)
        val text1 = inflate.findViewById<TextView>(android.R.id.text1)
        if (getItemViewType(position) == 111) {
            val bluetoothDevice = list[position]
            if (TextUtils.isEmpty(bluetoothDevice?.name)) {
                text1.text = "${bluetoothDevice?.address}(${bluetoothDevice?.address})"
            } else {
                text1.text = "${bluetoothDevice?.name}(${bluetoothDevice?.address})"
            }
        } else if (getItemViewType(position) == 222) {
            inflate = View.inflate(activity, R.layout.item_scanner_layout, null)
            val tvItemScanner = inflate.findViewById<TextView>(R.id.tv_item_scanner)
            tvItemScanner.text = "扫描到的设备"
        } else {
            //position - list.size - 1是为了防止越界
            val bluetoothDevice = blueList[position - list.size - 1]
            if (TextUtils.isEmpty(bluetoothDevice?.name)) {
                text1.text = "${bluetoothDevice?.address}(${bluetoothDevice?.address})"
            } else {
                text1.text = "${bluetoothDevice?.name}(${bluetoothDevice?.address})"
            }
        }
        return inflate
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            //返回一个textView布局
            position < list.size -> {
                111
            }
            //返回一个textView布局
            position == list.size -> {
                222
            }
            //返回扫描到的列表
            else -> 333
        }
    }

    override fun getItem(position: Int): Any = 0

    override fun getItemId(position: Int): Long = 0

    override fun getCount(): Int = list.size + blueList.size + 1
}
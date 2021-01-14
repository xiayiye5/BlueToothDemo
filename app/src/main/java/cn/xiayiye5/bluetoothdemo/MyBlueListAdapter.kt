package cn.xiayiye5.bluetoothdemo

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

/**
 * @author : xiayiye5
 * @date : 2021/1/14 15:19
 * 类描述 :
 */
class MyBlueListAdapter(private val activity: Activity, var list: MutableList<String?>) :
    BaseAdapter() {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflate = View.inflate(activity, android.R.layout.simple_list_item_1, null)
        val text1 = inflate.findViewById<TextView>(android.R.id.text1)
        text1.text = list[position]
        return inflate
    }

    override fun getItem(position: Int): Any = 0

    override fun getItemId(position: Int): Long = 0

    override fun getCount(): Int = list.size
}
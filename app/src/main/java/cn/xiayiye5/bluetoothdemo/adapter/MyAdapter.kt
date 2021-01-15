package cn.xiayiye5.bluetoothdemo.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

/**
 * @author : xiayiye5
 * @date : 2021/1/14 14:14
 * 类描述 :
 */
class MyAdapter(private val activity: Activity, var list: MutableList<String?>) : BaseAdapter() {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflate = View.inflate(activity, android.R.layout.simple_list_item_1, null)
        val text1 = inflate.findViewById<TextView>(android.R.id.text1)
        text1.text = list[position]
        return inflate


//        val inflate = View.inflate(activity, R.layout.blue_list, null)
//        val text1 = inflate.findViewById<TextView>(R.id.tv_blue_name)
//        val tvScanner = inflate.findViewById<TextView>(R.id.tv_scanner)
//        text1.text = list[position]
//        if (position == list.size - 1) {
//            tvScanner.visibility = View.VISIBLE
//        } else {
//            tvScanner.visibility = View.GONE
//        }
//        return inflate
    }

    override fun getItem(position: Int): Any = 0

    override fun getItemId(position: Int): Long = 0

    override fun getCount(): Int = list.size

}
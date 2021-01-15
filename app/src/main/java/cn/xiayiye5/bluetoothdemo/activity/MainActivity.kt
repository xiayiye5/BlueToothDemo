package cn.xiayiye5.bluetoothdemo.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import cn.xiayiye5.bluetoothdemo.R
import cn.xiayiye5.bluetoothdemo.utils.TimerDownUtils

/**
 * @author : xiayiye5
 * @date : 2021/1/14 16:27
 * 类描述 :
 */
public class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //当前页面定时任务
        TimerDownUtils.getInstance().isFinish(this)
    }


    fun goJavaPages(view: View?) {
        startActivity(Intent(this, HomeActivityConnectJava::class.java))
    }

    fun goJavaPage(view: View?) {
        startActivity(Intent(this, HomeActivityJava::class.java))
    }

    fun goKotlinPage(view: View?) {
        startActivity(Intent(this, HomeActivityKotlin::class.java))
    }
}
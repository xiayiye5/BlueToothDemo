package cn.xiayiye5.bluetoothdemo.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import cn.xiayiye5.bluetoothdemo.R

/**
 * @author : xiayiye5
 * @date : 2021/1/14 16:27
 * 类描述 :
 */
public class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    fun goJavaPage(view: View?) {
        startActivity(Intent(this, HomeActivityJava::class.java))
    }

    fun goKotlinPage(view: View?) {
        startActivity(Intent(this, HomeActivityKotlin::class.java))
    }
}
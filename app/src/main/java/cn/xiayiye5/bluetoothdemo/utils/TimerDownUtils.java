package cn.xiayiye5.bluetoothdemo.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author : xiayiye5
 * @date : 2021/1/15 16:58
 * 类描述 : 实现定时任务的工具类
 */
public class TimerDownUtils {
    private WeakReference<Activity> activityWeakReference;
    private static final TimerDownUtils TIME_UNIT = new TimerDownUtils();
    private Application currentApplication;

    private TimerDownUtils() {
    }

    public static TimerDownUtils getInstance() {
        return TIME_UNIT;
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String time = simpleDateFormat.format(new Date());
            Log.e("打印当前时间", time);
            //下面的17:11:00可以自己改
            if ("17:11:00".equals(time)) {
                //定时时间到！
                Toast.makeText(activityWeakReference.get(), "定时时间到！", Toast.LENGTH_LONG).show();
                removeCallbacksAndMessages(null);
                return;
            }
            sendEmptyMessageDelayed(0, 500);
        }
    };

    public void isFinish(Activity mainActivity) {
        activityWeakReference = new WeakReference<>(mainActivity);
        //每隔500毫秒刷新一次数据
        handler.sendEmptyMessageDelayed(0, 500);
    }

    /**
     * 获取全局的application
     *
     * @return 返回application
     */
    @SuppressLint("PrivateApi")
    public Application getNewApplication() {
        try {
            if (currentApplication == null) {
                currentApplication = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null, (Object[]) null);
            }
            return currentApplication;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.name.rmedal.widget.stepcount;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.veni.tools.LogUtils;
import com.veni.tools.util.JsonUtils;
import com.veni.tools.util.SPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by finnfu on 16/9/27.
 */

/*
 * 后台计步的service
 * */

public class StepService extends Service {
    private String TAG = StepService.class.getSimpleName();
    private final IBinder mBinder = new StepBinder();
    private UpdateUiCallBack mCallback;
    private Sensor mSensor;
    private SensorManager mSensorManager;
    private StepCount mStepCount;
    private StepDetector mStepDetector;
    private PowerManager.WakeLock wakeLock;
    private List<String> patrol_ids;
    public static final String SP_STEPS = "steps";
    private static final String SP_STEPSLIST = "steps_list";

    private final static int GRAY_SERVICE_ID = 1001;
    private int last_steps = 0;

    private StepValuePassListener mValuePassListener = new StepValuePassListener() {
        @Override
        public void stepChanged(int steps) {
            LogUtils.eTag("StepService", "steps==" + steps);
            LogUtils.eTag("StepService", "last_steps==" + last_steps);
            for (String patrol_id : patrol_ids) {
                int stepcount = (int) SPUtils.get(StepService.this, SP_STEPS + patrol_id, 0);
                SPUtils.put(StepService.this, SP_STEPS + patrol_id, (stepcount + steps - last_steps));
            }
            last_steps = steps;
            if (mCallback != null) {
                mCallback.updateUi();
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    @SuppressLint("InvalidWakeLockTag")
    public void onCreate() {
        super.onCreate();
        this.wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(1, "StepService");
        this.wakeLock.acquire();
        this.patrol_ids = JsonUtils.parseArray(
                (String) SPUtils.get(StepService.this, SP_STEPSLIST, ""), String.class);
        if (this.patrol_ids == null) {
            this.patrol_ids = new ArrayList<>();
        }
        this.mStepDetector = new StepDetector();
        this.mSensorManager = ((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        this.mSensor = this.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mSensorManager.registerListener(this.mStepDetector, this.mSensor, SensorManager.SENSOR_DELAY_UI);
        this.mStepCount = new StepCount();
        this.mStepCount.initListener(this.mValuePassListener);
        this.mStepDetector.initListener(this.mStepCount);
    }


    public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2) {
        LogUtils.eTag(TAG, "start");
        /*
         * 灰色保活,使服务成为无通知栏显示的前台服务
         * */
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(0,getNotification(this));
        } else {
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, getNotification(this));
        }
        last_steps = 0;
        return START_STICKY;
    }


    public void onDestroy() {
        this.mSensorManager.unregisterListener(this.mStepDetector);
        this.wakeLock.release();
        LogUtils.eTag(TAG, "stop");
        super.onDestroy();
    }

    public void registerCallback(UpdateUiCallBack paramICallback) {
        this.mCallback = paramICallback;
    }

    //重置StepCount
    public void resetValues(String patrol_id) {
        if(getPositionAtList(patrol_id)==-1){
            this.patrol_ids.add(patrol_id);
        }
        this.mStepCount.setSteps(0);
        SPUtils.put(StepService.this, SP_STEPSLIST, JsonUtils.toJson(this.patrol_ids));
    }

    private int getPositionAtList(String patrol_id) {
        for (int i = 0; i < patrol_ids.size(); i++) {
            String id = patrol_ids.get(i);
            if (id.equals(patrol_id)) {
                return i;
            }
        }
        return -1;
    }

    //重置StepCount
    public void removeValues(String patrol_id) {
        if (this.patrol_ids != null) {
           int position= getPositionAtList(patrol_id);
           if(position!=-1){
               patrol_ids.remove(position);
               SPUtils.remove(StepService.this, SP_STEPS + patrol_id);
           }
        }
        LogUtils.eTag(TAG, "patrol_ids==" + JsonUtils.toJson(patrol_ids));
        SPUtils.put(StepService.this, SP_STEPSLIST, JsonUtils.toJson(patrol_ids));
    }

    public boolean onUnbind(Intent paramIntent) {
        return super.onUnbind(paramIntent);
    }

    public class StepBinder extends Binder {
        public StepService getService() {
            return StepService.this;
        }
    }

    private static final String notificationId = "steps_channelId";
    private static final String notificationName = "steps_channelName";


    private static Notification getNotification(Context context) {
        Notification.Builder builder = new Notification.Builder(context);
        //设置Notification的ChannelID,否则不能正常显示
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(notificationId);
        }
        Notification notification = builder.build();
        return notification;
    }

    public static class GrayInnerService extends Service {
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            NotificationManager  notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //创建NotificationChannel
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel channel = new NotificationChannel(notificationId, notificationName, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
            startForeground(GRAY_SERVICE_ID,getNotification(this));

            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

}

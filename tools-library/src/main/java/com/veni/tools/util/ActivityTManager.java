package com.veni.tools.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Stack;

public class ActivityTManager {
    private Stack<Activity> activityStack;
    private volatile static ActivityTManager instance;

    private ActivityTManager() {

    }

    /**
     * 单一实例
     */
    public static ActivityTManager get() {
        if (instance == null) {
            synchronized (ActivityTManager.class) {
                if (instance == null) {
                    instance = new ActivityTManager();
                    instance.activityStack = new Stack<>();
                }
            }
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void add(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**
     * 移除指定的Activity
     */
    public void remove(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity = null;
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        try {
            for (Activity activity : activityStack) {
                if (activity.getClass() == cls) {
                    finishActivity(activity);
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishExceptActivity(Class<?> cls) {
        try {
            for (Activity activity : activityStack) {
                if (activity.getClass() != cls) {
                    finishActivity(activity);
                }
            }
        } catch (Exception ignored) {
        }
    }

    public Activity getActivityByClassName(Class<?> cls) {
        Activity targetactivity = null;
        try {
            for (Activity activity : activityStack) {
                if (activity.getClass() == cls) {
                    targetactivity = activity;
                }
            }
        } catch (Exception ignored) {
        }
        return targetactivity;
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 返回到指定的activity
     *
     * @param cls
     */
    public void returnToActivity(Class<?> cls) {
        while (activityStack.size() != 0)
            if (activityStack.peek().getClass() == cls) {
                break;
            } else {
                finishActivity(activityStack.peek());
            }
    }


    /**
     * 是否已经打开指定的activity
     *
     * @param cls
     * @return
     */
    public boolean isOpenActivity(Class<?> cls) {
        if (activityStack != null) {
            for (int i = 0, size = activityStack.size(); i < size; i++) {
                if (cls == activityStack.peek().getClass()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 退出应用程序
     *
     * @param context      上下文
     * @param isBackground 是否开开启后台运行
     */
    public void AppExit(Context context, Boolean isBackground) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            if (activityMgr != null && isBackground) {
                activityMgr.restartPackage(context.getPackageName());
            }
        } catch (Exception ignored) {
        } finally {
            // 注意，如果您有后台程序运行，请不要支持此句子
            if (!isBackground) {
                ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE); //获取应用程序管理器
                manager.killBackgroundProcesses(context.getPackageName()); //强制结束当前应用程序
//                System.exit(0);
            }
        }
    }
}

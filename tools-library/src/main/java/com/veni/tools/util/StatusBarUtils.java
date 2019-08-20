package com.veni.tools.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.FloatRange;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.veni.tools.LogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * 状态栏透明
 * Created by Kkan on 2018/10/26.
 * 沉浸工具
 * 导航沉浸
 * 底部导航适配 全面屏适配
 *  手机有限 小米9  华为mate VIVO NEX 测试通过
 *
 */

@SuppressWarnings("unused")
public class StatusBarUtils {
    private int DEFAULT_COLOR = 0;
    private float DEFAULT_ALPHA = 0;//Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 0.2f : 0.3f;

    private Window window;
    private ViewGroup mViewObserved;//跟布局视图
    private View mNavBarTintView;//底部导航栏
    private int usableHeightPrevious;//视图变化前的可用高度

    //<editor-fold desc="沉侵">
    public void immersive(Activity activity) {
        immersive(activity, DEFAULT_COLOR, DEFAULT_ALPHA);
    }

    public void immersive(Activity activity, int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        immersive(activity.getWindow(), color, alpha);
    }

    public void immersive(Activity activity, int color) {
        immersive(activity.getWindow(), color, 1f);
    }

    public void immersive(Window window) {
        immersive(window, DEFAULT_COLOR, DEFAULT_ALPHA);
    }

    public void immersive(Window window, int color) {
        immersive(window, color, 1f);
    }

    public void immersive(Window window, int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        this.window = window;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(mixtureColor(color, alpha));

            int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;//去掉信息栏
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;//让应用的主体内容占用系统状态栏的空间
            window.getDecorView().setSystemUiVisibility(systemUiVisibility);

            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            //把导航栏颜色设置透明
//            window.setStatusBarColor(Color.TRANSPARENT);
            //把底部导航栏颜色设置灰色
//            window.setNavigationBarColor(Color.DKGRAY);
            assistNavBar();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setTranslucentView((ViewGroup) window.getDecorView(), color, alpha);
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        }

//        在代码中设置 顶部导航栏会有一个阴影遮罩背景色
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        //把导航栏颜色设置透明
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
    }
    //</editor-fold>

    //<editor-fold desc="DarkMode">
    public void darkMode(Activity activity, boolean dark) {
        if (isFlyme4Later()) {
            darkModeForFlyme4(activity.getWindow(), dark);
        } else if (isMIUI6Later()) {
            darkModeForMIUI6(activity.getWindow(), dark);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            darkModeForM(activity.getWindow(), dark);
        }
    }

    /**
     * 设置状态栏darkMode,字体颜色及icon变黑(目前支持MIUI6以上,Flyme4以上,Android M以上)
     */
    public void darkMode(Activity activity) {
        darkMode(activity.getWindow(), DEFAULT_COLOR, DEFAULT_ALPHA);
    }

    public void darkMode(Activity activity, int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        darkMode(activity.getWindow(), color, alpha);
    }

    /**
     * 设置状态栏darkMode,字体颜色及icon变黑(目前支持MIUI6以上,Flyme4以上,Android M以上)
     */
    public void darkMode(Window window, int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        if (isFlyme4Later()) {
            darkModeForFlyme4(window, true);
            immersive(window, color, alpha);
        } else if (isMIUI6Later()) {
            darkModeForMIUI6(window, true);
            immersive(window, color, alpha);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            darkModeForM(window, true);
            immersive(window, color, alpha);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setTranslucentView((ViewGroup) window.getDecorView(), color, alpha);
        } else {
            immersive(window, color, alpha);
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }

//        setTranslucentView((ViewGroup) window.getDecorView(), color, alpha);
    }


    /**
     * 增加View的paddingTop,增加的值为状态栏高度
     */
    public void setPadding(Context context, View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context),
                    view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    /**
     * 增加View的paddingTop,增加的值为状态栏高度 (智能判断，并设置高度)
     */
    public void setPaddingSmart(Context context, View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp != null && lp.height > 0) {
                lp.height += getStatusBarHeight(context);//增高
            }
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context),
                    view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    /**
     * 增加View的高度以及paddingTop,增加的值为状态栏高度.一般是在沉浸式全屏给ToolBar用的
     */
    public void setHeightAndPadding(Context context, View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            lp.height += getStatusBarHeight(context);//增高
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context),
                    view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    /**
     * 增加View上边距（MarginTop）一般是给高度为 WARP_CONTENT 的小控件用的
     */
    public void setMargin(Context context, View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) lp).topMargin += getStatusBarHeight(context);//增高
            }
            view.setLayoutParams(lp);
        }
    }

    public void clearMargin(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) lp).topMargin = 0;
            }
            view.setLayoutParams(lp);
        }
    }
    //------------------------->

    /**
     * android 6.0设置字体颜色
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private void darkModeForM(Window window, boolean dark) {
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(Color.TRANSPARENT);

        int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
        if (dark) {
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else {
            systemUiVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        window.getDecorView().setSystemUiVisibility(systemUiVisibility);
    }

    /**
     * 设置Flyme4+的darkMode,darkMode时候字体颜色及icon变黑
     * http://open-wiki.flyme.cn/index.php?title=Flyme%E7%B3%BB%E7%BB%9FAPI
     */
    private void darkModeForFlyme4(Window window, boolean dark) {
        if (window != null) {
            try {
                WindowManager.LayoutParams e = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(e);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }

                meizuFlags.setInt(e, value);
                window.setAttributes(e);
            } catch (Exception var8) {
                LogUtils.eTag("StatusBar", "darkIcon: failed");
            }
        }
    }

    /**
     * 设置MIUI6+的状态栏是否为darkMode,darkMode时候字体颜色及icon变黑
     * http://dev.xiaomi.com/doc/p=4769/
     */
    private void darkModeForMIUI6(Window window, boolean darkmode) {
        Class<? extends Window> clazz = window.getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(window, darkmode ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建假的透明栏
     */
    private void setTranslucentView(ViewGroup container, int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int mixtureColor = mixtureColor(color, alpha);
            View translucentView = container.findViewById(android.R.id.custom);
            if (translucentView == null && mixtureColor != 0) {
                translucentView = new View(container.getContext());
                translucentView.setId(android.R.id.custom);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(container.getContext()));
                container.addView(translucentView, lp);
            }
            if (translucentView != null) {
                translucentView.setBackgroundColor(mixtureColor);
            }
        }
    }

    private int mixtureColor(int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        int a = (color & 0xff000000) == 0 ? 0xff : color >>> 24;
        return (color & 0x00ffffff) | (((int) (a * alpha)) << 24);
    }

    //</editor-fold>
//底部导航栏
    private void assistNavBar() {
        mViewObserved = (ViewGroup) window.getDecorView();
        //给View添加全局的布局监听器
        mViewObserved.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                //只需要获取一次高度，获取后移除监听器
                mViewObserved.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                resetLayoutByUsableHeight(window);
            }
        });
    }

    public void setGonemNavBarTintView(boolean enabled) {
        if (window != null && mNavBarTintView != null) {
            if (getHasVirtualKey(window) > 0) {
                mNavBarTintView.setVisibility(enabled ? View.VISIBLE : View.GONE);
            }
        }
    }

    private void resetLayoutByUsableHeight(Window window/*Activity activity*/) {
        WindowManager wm = (WindowManager) window.getContext().getSystemService(Context.WINDOW_SERVICE);
//        WindowManager wm = window.getWindowManager();
        int height = wm.getDefaultDisplay().getHeight();


        //比较布局变化前后的View的可用高度
        if (height != usableHeightPrevious) {
            int navigationBarHeight = getHasVirtualKey(window);

            LinearLayout.LayoutParams parentparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            LinearLayout linearLayout = new LinearLayout(window.getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(parentparams);


            ViewGroup decorChild = (ViewGroup) mViewObserved.getChildAt(0);
            decorChild.setBackgroundColor(Color.TRANSPARENT);

            mViewObserved.removeView(decorChild);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getRealHeight(window), 1);
            LinearLayout childlinearLayout = new LinearLayout(window.getContext());
            childlinearLayout.setOrientation(LinearLayout.VERTICAL);
            childlinearLayout.setLayoutParams(params);
            childlinearLayout.addView(decorChild);
            linearLayout.addView(childlinearLayout);

            mNavBarTintView = new View(window.getContext());
            FrameLayout.LayoutParams navparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, navigationBarHeight);
            params.gravity = Gravity.BOTTOM;
            mNavBarTintView.setLayoutParams(navparams);
            mNavBarTintView.setBackgroundColor(Color.DKGRAY);

            mNavBarTintView.setVisibility((navigationBarHeight > 0) ? View.VISIBLE : View.GONE);

            linearLayout.addView(mNavBarTintView);

            mViewObserved.addView(linearLayout);

            mViewObserved.requestLayout();//请求重新布局
            usableHeightPrevious = height;
        }
    }

    /**
     * dpi 通过反射，获取包含虚拟键的整体屏幕高度
     * height 获取屏幕尺寸，但是不包括虚拟功能高度
     *
     * @return
     */
    public static int getHasVirtualKey(Window window) {
        if (isShowNavBar(window)) {
            int dpi = 0;
            Display display = window.getWindowManager().getDefaultDisplay();
            DisplayMetrics dm = new DisplayMetrics();
            @SuppressWarnings("rawtypes")
            Class c;
            try {
                c = Class.forName("android.view.Display");
                @SuppressWarnings("unchecked")
                Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
                method.invoke(display, dm);
                dpi = dm.heightPixels;
            } catch (Exception e) {
                e.printStackTrace();
            }
            int height = window.getWindowManager().getDefaultDisplay().getHeight();

            int navigationBarHeight = dpi - height;
            int navigation_bar_height = getSizeByReflection(window.getContext(),"navigation_bar_height");
            return navigationBarHeight>navigation_bar_height?navigation_bar_height:navigationBarHeight;

        }
        return 0;
    }
    public static int getSizeByReflection(Context context, String field) {
        int size = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField(field).get(object).toString());
            size = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }
    /**
     * 判断是否显示了导航栏
     * (说明这里的context 一定要是activity的context 否则类型转换失败)
     *
     * @param window
     * @return
     */
    public static boolean isShowNavBar(Window window) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

            //是否开启全面屏手势开关 0 关闭  1 开启
            int val = Settings.Global.getInt(window.getContext().getContentResolver(), getDeviceNavigation(), 0);
            if (val!= 0) {
                return false;
            }
        }

       /* if (isMIUI6Later()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                //true 是手势，默认是 false
                boolean fsg_nav_bar = (Settings.Global.getInt(window.getContext().getContentResolver(), "force_fsg_nav_bar", 0) != 0);
                if (fsg_nav_bar) {
                    return false;
                }
            }
        }*/
        if (null == window) {
            return false;
        }

        /*
         * 获取应用区域高度
         */
        Rect outRect1 = new Rect();
        try {
            window.getDecorView().getWindowVisibleDisplayFrame(outRect1);
        } catch (ClassCastException e) {
            e.printStackTrace();
            return false;
        }
        int activityHeight = outRect1.height();
        /*
         * 获取状态栏高度
         */
        int statuBarHeight = getStatusBarHeight(window.getContext());
        /*
         * 屏幕物理高度 减去 状态栏高度
         */
        int remainHeight = getRealHeight(window) - statuBarHeight;

        /*
         * 剩余高度跟应用区域高度相等 说明导航栏没有显示 否则相反
         */
        return activityHeight != remainHeight;
    }

    private static String getDeviceNavigation() {
        String manufacturer = Build.MANUFACTURER;
        if(DataUtils.isEmpty(manufacturer)) return "navigationbar_is_min";

        if (manufacturer.equalsIgnoreCase("HUAWEI")) {
            return "navigationbar_is_min";
        } else if (manufacturer.equalsIgnoreCase("XIAOMI")) {
            return "force_fsg_nav_bar";
        } else if (manufacturer.equalsIgnoreCase("VIVO")) {
            return "navigation_gesture_on";
        } else if (manufacturer.equalsIgnoreCase("OPPO")) {
            return "navigation_gesture_on";
        } else {
            return "navigationbar_is_min";
        }
    }
    /**
     * 获取真实屏幕高度
     *
     * @return
     */
    public static int getRealHeight(Window window) {
//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager wm = window.getWindowManager();
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.y;
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 24;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = context.getResources().getDimensionPixelSize(resId);
        } else {
            result = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    result, Resources.getSystem().getDisplayMetrics());
        }
        return result;
    }

    /**
     * 判断是否Flyme4以上
     */
    public static boolean isFlyme4Later() {
        return Build.FINGERPRINT.contains("Flyme_OS_4")
                || Build.VERSION.INCREMENTAL.contains("Flyme_OS_4")
                || Pattern.compile("Flyme OS [4|5]", Pattern.CASE_INSENSITIVE).matcher(Build.DISPLAY).find();
    }

    /**
     * 判断是否为MIUI6以上
     */
    public static boolean isMIUI6Later() {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method mtd = clz.getMethod("get", String.class);
            String val = (String) mtd.invoke(null, "ro.miui.ui.version.name");
            val = val.replaceAll("[vV]", "");
            int version = Integer.parseInt(val);
            return version >= 6;
        } catch (Exception e) {
            return false;
        }
    }
}

package com.veni.tools.baserx;

import com.veni.tools.LogUtils;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 作者：kkan on 2017/01/30
 * 当前类注释:
 * 用于管理单个presenter的RxBus的事件和Rxjava相关代码的生命周期处理
 * //监听菜单显示或隐藏
 * mRxManager.post(AppConstant.MENU_SHOW_HIDE,true);
 * mRxManager.on(AppConstant.MENU_SHOW_HIDE, new Consumer<Boolean>() {
 *
 * @Override public void accept(Boolean hideOrShow) {
 * }
 * });
 */
public class RxManager {
    public RxBus mRxBus = RxBus.getInstance();
    //管理rxbus订阅
    private Map<String, Observable<?>> mObservables = new HashMap<>();
    private Map<String, Disposable> disposables = new HashMap<>();
    /*管理Observables 和 Subscribers订阅*/
    private CompositeDisposable mCompositeSubscription = new CompositeDisposable();

    /**
     * RxBus注入监听
     *
     * @param eventName
     * @param action1
     */
    public <T> void on(String eventName, Consumer<T> action1) {
        Observable<T> mObservable = mRxBus.register(eventName);
        mObservables.put(eventName, mObservable);
        Disposable disposable = mObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(action1, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
        disposables.put(eventName, disposable);
        /*订阅管理*/
       add(disposable);
    }

    /**
     * 单纯的Observables 和 Subscribers管理
     * Observable操作符
     * just()：创建一个直接发射数据的Observable
     * from()：从一个数组或列表中转换成Observable
     * create()：创建一个Observable
     * defer()：当订阅者订阅时才创建Observable，为每一个订阅创建一个新的Observable
     * range()：创建一个指定范围的序列Observable
     * interval()：创建一个按照规定时间间隔发射数据的Observable
     * timer()：延时发射数据的Observable
     * empty()：直接完成的Observable
     * error()：直接发射错误的Observable
     * never()：不发射数据的Observable
     * ---------------------
     * 原文：https://blog.csdn.net/u013318615/article/details/82390992
     *
     * @param d Observable
     */
    public void add(Disposable d) {
        /*订阅管理*/
        mCompositeSubscription.add(d);
    }

    /**
     * 单个presenter生命周期结束，取消订阅和所有rxbus观察
     */
    public void clear(String eventName) {
        Disposable disposable =  disposables.get(eventName);
        if(disposable!=null){
            mCompositeSubscription.remove(disposable);
            Observable observable=  mObservables.get(eventName);
            if(observable!=null){
                mRxBus.unregister(eventName,observable);// 移除rxbus观察
            }
        }
        LogUtils.eTag("RxManager","clear---"+eventName);
    }
    /**
     * 单个presenter生命周期结束，取消订阅和所有rxbus观察
     */
    public void clear() {
        mCompositeSubscription.dispose();// 取消所有订阅
        for (Map.Entry<String, Observable<?>> entry : mObservables.entrySet()) {
            mRxBus.unregister(entry.getKey(), entry.getValue());// 移除rxbus观察
        }
    }

    //发送rxbus
    public void post(Object tag, Object content) {
        mRxBus.post(tag, content);
    }
}

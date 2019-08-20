package com.name.rmedal.ui.personal.contract;


import com.name.rmedal.modelbean.CheckVersionBean;
import com.veni.tools.base.mvp.BasePresenter;
import com.veni.tools.base.mvp.BaseView;

import java.io.File;


/**
 * 作者：Administrator on 2017/12/04 10:36
 * 当前类注释:
 * MVP契约类
 */
public interface PersonalContract {

    /**
     * 向 页面 返回数据
     *  页面中实现这个  View 接口
     */
    interface View extends BaseView {
        //返回的数据
        void returnVersionData(CheckVersionBean data);

        void onStartDownload(long length);

        void onDownLoadProgress(int progress);

        void onDownLoadCompleted(File file);

        void onDownLoadError(String msg);
    }
    /**
     * 发起请求
     * 继承这个抽象类
     *   调用Model获取网络数据，用View中的接口  更新界面
     */
    abstract static class Presenter extends BasePresenter<View> {
        //发起请求

        public abstract void checkVersion(String version);

        public abstract void download(String url);
    }

}

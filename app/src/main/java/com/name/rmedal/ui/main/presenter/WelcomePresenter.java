package com.name.rmedal.ui.main.presenter;


import com.name.rmedal.BuildConfig;
import com.name.rmedal.api.HttpManager;
import com.name.rmedal.api.HttpRespose;
import com.name.rmedal.api.RxSubscriber;
import com.name.rmedal.modelbean.CheckVersionBean;
import com.name.rmedal.tools.AppTools;
import com.name.rmedal.ui.AppConstant;
import com.name.rmedal.ui.main.contract.WelcomeContract;
import com.veni.tools.baserx.DownloadListener;
import com.veni.tools.baserx.RxSchedulers;
import com.veni.tools.util.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * 作者：Administrator on 2017/12/04 10:36
 * 当前类注释:
 * 欢迎页的Presenter
 */
public class WelcomePresenter extends WelcomeContract.Presenter {

    @Override
    public void checkVersion(String version) {
        //正式调试
       /* //请求参数
        HashMap<String, String> param = new HashMap<>();
        param.put("version", version);
        HttpManager.getInstance().getOkHttpUrlService().checkVersion(param)
                .compose(RxSchedulers.<HttpRespose<CheckVersionBean>>io_main()).subscribe(new RxSubscriber<CheckVersionBean>() {
            @Override
            public void _onNext(CheckVersionBean data) {
                mView.returnVersionData(data);
            }

            @Override
            public void onErrorSuccess(int code, String message, boolean issuccess) {
                mView.onErrorSuccess(code, message, issuccess, false);
            }
        });*/
        //测试数据
        AppTools.createObservable(CheckVersionBean.class)
                .compose(RxSchedulers.<HttpRespose<CheckVersionBean>>io_main())
                .doOnNext(new Consumer<HttpRespose<CheckVersionBean>>() {
                    @Override
                    public void accept(HttpRespose<CheckVersionBean> httpRespose) throws Exception {
                        CheckVersionBean versionBean = new CheckVersionBean();
                        versionBean.setAppDownUrl(AppConstant.download_url);
                        versionBean.setIsNeedUpdate("1");
                        versionBean.setDesc("1.&nbsp;qqqqqqqqqq<br/>" +
                                "2.&nbsp;wwwwwwwww<br/>" +
                                "3.&nbsp;eeeeee");
                        versionBean.setNewVersion(BuildConfig.VERSION_NAME + "1");
                        httpRespose.setResult(versionBean);
                    }
                })
                .subscribe(new RxSubscriber<CheckVersionBean>(this) {
                    @Override
                    public void _onNext(CheckVersionBean data) {
                        mView.returnVersionData(data);
                    }

                    @Override
                    public void onErrorSuccess(int code, String message, boolean issuccess) {
                        mView.onErrorSuccess(code, message, issuccess, false);
                    }
                });
    }

    @Override
    public void download(final String url) {
        String baseurl = AppTools.getSubUrl(url);
        String filePath = FileUtils.getRootPath() + File.separator + "/downlaod/";
        String fileName = url.substring(url.lastIndexOf('/') + 1);
        final File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final File file = new File(filePath, fileName);

        HttpManager.getInstance().getDownloadUrlService(baseurl, new DownloadListener() {
            @Override
            public void onStartDownload(long length) {
                mView.onStartDownload(length);
            }

            @Override
            public void onProgress(int progress) {
                mView.onDownLoadProgress(progress);
            }
        }).download(url).compose(RxSchedulers.<ResponseBody>io_main())
                .map(new Function<ResponseBody, InputStream>() {
                    @Override
                    public InputStream apply(ResponseBody responseBody) throws Exception {
                        return responseBody.byteStream();
                    }
                }).observeOn(Schedulers.computation()) // 用于计算任务
                .doOnNext(new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream inputStream) throws Exception {
                        writeFile(inputStream, file);
                    }
                })
                .subscribe(new Observer<InputStream>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(InputStream inputStream) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onDownLoadError(e.toString());
                    }

                    @Override
                    public void onComplete() {
                        mView.onDownLoadCompleted(file);
                    }
                });
    }
/*  FileOutputStream fos = null;
                        long total = 0;
                        long responseLength = 0;
                        try {
                            fos = new FileOutputStream(file);
                            byte[] b = new byte[1024];
                            int len;
                            int progress = 0;
                            int lastProgress = 0;
                            while ((len = inputStream.read(b)) != -1) {
                                fos.write(b, 0, len);
                                total += len;
                                lastProgress = progress;
                                progress = (int) (total );
                                if (progress > 0 && progress != lastProgress) {
                                    mView.onDownLoadProgress(progress);
                                }
                            }
                            mView.onDownLoadCompleted(file);
                            inputStream.close();
                            fos.close();
                        } catch (FileNotFoundException e) {
                            mView.onDownLoadError("FileNotFoundException");
                        } catch (IOException e) {
                            mView.onDownLoadError("IOException");
                        }*/

    /**
     * 将输入流写入文件
     */
    private void writeFile(InputStream inputString, File file) {

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int len;
            while ((len = inputString.read(b)) != -1) {
                fos.write(b, 0, len);
            }
            inputString.close();
            fos.close();
        } catch (FileNotFoundException e) {
            mView.onDownLoadError("FileNotFoundException");
        } catch (IOException e) {
            mView.onDownLoadError("IOException");
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (inputString != null) {
                    inputString.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}

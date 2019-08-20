package com.veni.tools.baserx;

public interface DownloadListener {
    void onStartDownload(long length);
    void onProgress(int progress);
}

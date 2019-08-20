package com.name.rmedal.modelbean;

public class CheckVersionBean{

    /**
     * isNeedUpdate :是否需要升级更新 0-无需更新 1-需要升级更新
     * appDownUrl :最新版app安装包的下载地址，地址为全路径地址，直接访问该地址进行下载安装包
     * desc :最新安装包的升级更新内容描述
     * newVersion :最新版本号
     */
    private String isNeedUpdate;
    private String appDownUrl;
    private String desc;
    private String newVersion;
    public String getIsNeedUpdate() {
        return isNeedUpdate;
    }

    public void setIsNeedUpdate(String isNeedUpdate) {
        this.isNeedUpdate = isNeedUpdate;
    }

    public String getAppDownUrl() {
        return appDownUrl;
    }

    public void setAppDownUrl(String appDownUrl) {
        this.appDownUrl = appDownUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }
}

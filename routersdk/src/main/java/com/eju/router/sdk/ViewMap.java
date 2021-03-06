package com.eju.router.sdk;

/**
 * Created by Sidney on 2016/11/25.
 */
 class ViewMap {

    //下载链接
    public String downloadUrl;

    //模块包md5
    public String md5;

    //模块包版本
    public String version;

    ViewMap(String downloadUrl, String md5, String version) {
        this.downloadUrl = downloadUrl;
        this.md5 = md5;
        this.version = version;
    }

    @Override
    public String toString() {
        return "ViewMap{" +
                "downloadUrl='" + downloadUrl + '\'' +
                ", md5='" + md5 + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}

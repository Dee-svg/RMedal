package com.name.rmedal.modelbean;


/**
 * 作者：kkan on 2018/02/26
 * 当前类注释:
 * 首页轮播
 */
public class BannerBean{
    private String image_url;
    private String image_describe;

    public BannerBean() {
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getImage_describe() {
        return image_describe==null?"":image_describe;
    }

    public void setImage_describe(String image_describe) {
        this.image_describe = image_describe;
    }

}

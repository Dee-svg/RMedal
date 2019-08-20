package com.name.rmedal.modelbean;

import com.name.rmedal.ui.bigimage.BigImageBean;

/**
 * 卡片数据装载对象
 *
 * @author xmuSistone
 */
public class CardDataItem extends BigImageBean {
    private int likeNum;
    private int imageNum;

    public CardDataItem(String image_url, String image_describe, String image_view_title) {
        super(image_url, image_describe, image_view_title);
    }

    public CardDataItem(String image_url, String image_describe) {
        super(image_url, image_describe);
    }


    public int getLikeNum() {
        return (int) (Math.random() * 10);
    }


    public int getImageNum() {
        return (int) (Math.random() * 6);
    }
}

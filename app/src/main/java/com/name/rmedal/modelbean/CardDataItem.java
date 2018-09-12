package com.name.rmedal.modelbean;

/**
 * 卡片数据装载对象
 *
 * @author xmuSistone
 */
public class CardDataItem {
    private String imagePath;
    private String userName;
    private int likeNum;
    private int imageNum;

    public CardDataItem(String imagePath, String userName) {
        this.imagePath = imagePath;
        this.userName = userName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getLikeNum() {
        return (int) (Math.random() * 10);
    }


    public int getImageNum() {
        return (int) (Math.random() * 6);
    }
}

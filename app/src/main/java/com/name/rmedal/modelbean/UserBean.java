package com.name.rmedal.modelbean;

import com.name.rmedal.ui.AppConstant;

/**
 * 作者：kkan on 2017/12/04 10:36
 * 当前类注释:
 * 用户,根据实际情况来
 */
public class UserBean {
    /*
     * @Entity：告诉GreenDao该对象为实体，只有被@Entity注释的Bean类才能被dao类操作
     * @Id：对象的Id，使用Long类型作为EntityId，否则会报错。
     * (autoincrement = true)表示主键会自增，如果false就会使用旧值
     * @Property：可以自定义字段名，注意外键不能使用该属性
     * @NotNull：属性不能为空
     * @Transient：使用该注释的属性不会被存入数据库的字段中
     * @Unique：该属性值必须在数据库中是唯一值
     * @Generated：编译后自动生成的构造函数、方法等的注释，提示构造函数、方法等不能被修改
     */
    private String userId;// 用户ID
    private String userName;// 用户名
    private String phone;// 电话
    private int sex;// 性别
    private String customerImg;//用户头像
    private String realName;//真实名字

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName == null ? "Veni" : userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getCustomerImg() {
        if (customerImg == null) {
            return AppConstant.img_head;
        }
        return customerImg;
    }

    public void setCustomerImg(String customerImg) {
        this.customerImg = customerImg;
    }

    public String getRealName() {
        if (realName == null) {
            return "@KKan";
        }
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

}

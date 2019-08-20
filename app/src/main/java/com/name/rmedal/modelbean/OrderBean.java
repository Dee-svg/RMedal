package com.name.rmedal.modelbean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;

/**
 * 作者：kkan on 2017/12/04 10:36
 * 当前类注释:
 */
@Entity
public class OrderBean {
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

    public static final int TYPE_CART = 0x01; //表示是购物车列表
    public static final int TYPE_LOVE = 0x02; //表示为收藏列表

    //不能用int （ID 表示标识主键 且主键不能用int autoincrement = true 表示主键会自增）
    @Id(autoincrement = true)
    private Long id;
    @Unique
    private String name;//商品名称 (unique 表示该属性必须在数据库中是唯一的值)
    @Property(nameInDb = "price")
    private String price;//商品价格(可以自定义字段名，注意外键不能使用该属性)
    private int sell_num;//已售数量
    private String image_url; //图标url
    private String address; //商家地址
    private int type; //商品列表分类

    @Generated(hash = 1145462163)
    public OrderBean(Long id, String name, String price, int sell_num,
            String image_url, String address, int type) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.sell_num = sell_num;
        this.image_url = image_url;
        this.address = address;
        this.type = type;
    }

    @Generated(hash = 1725534308)
    public OrderBean() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getSell_num() {
        return sell_num;
    }

    public void setSell_num(int sell_num) {
        this.sell_num = sell_num;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

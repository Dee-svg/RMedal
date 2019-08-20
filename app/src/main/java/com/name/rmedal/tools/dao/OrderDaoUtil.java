package com.name.rmedal.tools.dao;

import android.content.Context;

import com.name.rmedal.BuildConfig;
import com.name.rmedal.modelbean.DaoSession;
import com.name.rmedal.modelbean.OrderBean;
import com.name.rmedal.modelbean.OrderBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * 作者：kkan on 2019/05/05 10:36
 * 当前类注释:
 *  * 使用GreenDao 实现简单的增删改查，下面是基本方法
 *  * 增加单个数据
 *  * getShopDao().insert(shop);
 *  * getShopDao().insertOrReplace(shop);
 *  * 增加多个数据
 *  * getShopDao().insertInTx(shopList);
 *  * getShopDao().insertOrReplaceInTx(shopList);
 *  * 查询全部
 *  * List< Shop> list = getShopDao().loadAll();
 *  * List< Shop> list = getShopDao().queryBuilder().list();
 *  * 查询附加单个条件
 *  * .where()
 *  * .whereOr()
 *  * 查询附加多个条件
 *  * .where(, , ,)
 *  * .whereOr(, , ,)
 *  * 查询附加排序
 *  * .orderDesc()
 *  * .orderAsc()
 *  * 查询限制当页个数
 *  * .limit()
 *  * 查询总个数
 *  * .count()
 *  * 修改单个数据
 *  * getShopDao().update(shop);
 *  * 修改多个数据
 *  * getShopDao().updateInTx(shopList);
 *  * 删除单个数据
 *  * getTABUserDao().delete(user);
 *  * 删除多个数据
 *  * getUserDao().deleteInTx(userList);
 *  * 删除数据ByKey
 *  * getTABUserDao().deleteByKey();
 *
 */
public class OrderDaoUtil {
    private DaoManager manager;
    private OrderBeanDao beanDao;
    private DaoSession daoSession;

    public OrderDaoUtil(Context context) {
        manager = DaoManager.getInstance();
        manager.init(context);
        manager.setDebug(BuildConfig.DEBUG);

        daoSession = manager.getDaoSession();
        beanDao = daoSession.getOrderBeanDao();
    }

    /**
     * 添加数据，如果有重复则覆盖
     */
    public void insertOrderBean(OrderBean orderBean) {
        beanDao.insertOrReplace(orderBean);
    }

    /**
     * 添加多条数据，需要开辟新的线程
     */
    public void insertMultOrderBean(final List<OrderBean> orderBeans) {
        daoSession.runInTx(new Runnable() {
            @Override
            public void run() {
                for (com.name.rmedal.modelbean.OrderBean OrderBean : orderBeans) {
                    insertOrderBean(OrderBean);
                }
            }
        });
    }


    /**
     * 删除数据
     */
    public void deleteOrderBean(OrderBean orderBean) {
        beanDao.delete(orderBean);
    }

    public void deleteUserBean(long key) {
        beanDao.deleteByKey(key);
    }

    /**
     * 删除全部数据
     */
    public void deleteAll() {
        beanDao.deleteAll();
    }

    /**
     * 更新数据
     */
    public void updateOrderBean(OrderBean orderBean) {
        beanDao.update(orderBean);
    }

    /**
     * 按照主键返回单条数据
     */
    public OrderBean listOneOrderBean(long key) {
        return beanDao.load(key);
    }

    /**
     * 根据指定条件查询数据
     */
    public List<OrderBean> queryOrderBean(String username) {
        //查询构建器
        QueryBuilder<OrderBean> builder = beanDao.queryBuilder();
        return builder
                .where(OrderBeanDao.Properties.Type.eq(OrderBean.TYPE_CART)).list();
    }

    /**
     * 查询全部数据
     */
    public List<OrderBean> queryAll() {
//        return manager.getDaoSession().loadAll(OrderBean.class);
        return beanDao.loadAll();
    }
}

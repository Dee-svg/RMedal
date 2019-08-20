package com.name.rmedal.tools.dao;

import android.content.Context;

import com.name.rmedal.BuildConfig;
import com.name.rmedal.modelbean.DaoSession;
import com.name.rmedal.modelbean.UserBean;
import com.name.rmedal.modelbean.UserBeanDao;

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
public class UserDaoUtil {
    private DaoManager manager;
    private UserBeanDao beanDao;
    private DaoSession daoSession;

    public UserDaoUtil(Context context) {
        manager = DaoManager.getInstance();
        manager.init(context);
        manager.setDebug(BuildConfig.DEBUG);

        daoSession = manager.getDaoSession();
        beanDao = daoSession.getUserBeanDao();
    }

    /**
     * 添加数据，如果有重复则覆盖
     */
    public void insertUserBean(UserBean userBean) {
        beanDao.insertOrReplace(userBean);
    }

    /**
     * 添加多条数据，需要开辟新的线程
     */
    public void insertMultUserBean(final List<UserBean> userBeans) {
        daoSession.runInTx(new Runnable() {
            @Override
            public void run() {
                for (UserBean userBean : userBeans) {
                    insertUserBean(userBean);
                }
            }
        });
    }


    /**
     * 删除数据
     */
    public void deleteUserBean(UserBean userBean) {
        beanDao.delete(userBean);
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
    public void updateUserBean(UserBean userBean) {
        beanDao.update(userBean);
    }

    /**
     * 按照主键返回单条数据
     */
    public UserBean listOneUserBean(long key) {
        return beanDao.load(key);
    }

    /**
     * 根据指定条件查询数据
     */
    public List<UserBean> queryUserBeanByPhone(String userphone) {
        //查询构建器
        QueryBuilder<UserBean> builder = beanDao.queryBuilder();
        return builder
                .where(UserBeanDao.Properties.Phone.like(userphone)).list();
    }
    public List<UserBean> queryUserBeanByUserId(String userid) {
        //查询构建器
        QueryBuilder<UserBean> builder = beanDao.queryBuilder();
        return builder
                .where(UserBeanDao.Properties.UserId.like(userid)).list();
    }

    /**
     * 查询全部数据
     */
    public List<UserBean> queryAll() {
//        return manager.getDaoSession().loadAll(UserBean.class);
        return beanDao.loadAll();
    }
}

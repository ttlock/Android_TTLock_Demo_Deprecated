package com.example.ttlock.dao;

import com.example.ttlock.MyApplication;
import com.example.ttlock.model.DaoSession;
import com.example.ttlock.model.Key;
import com.example.ttlock.model.KeyDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by TTLock on 2016/9/12 0012.
 */
public class DbService {
    private static boolean DBG = true;
    private static DaoSession daoSession = MyApplication.getDaoSession();
    private static KeyDao keyDao = daoSession.getKeyDao();
    private static QueryBuilder queryBuilder = keyDao.queryBuilder();

    /**
     * 根据accessToken获取钥匙
     * @param accessToken
     * @return
     */
    public static List<Key> getKeysByAccessToken(String accessToken) {
        queryBuilder = keyDao.queryBuilder();
       return queryBuilder.where(KeyDao.Properties.AccessToken.eq(accessToken)).list();
    }

    /**
     * 根据accessToken和lockmac获取钥匙
     * @param accessToken
     * @param lockmac
     * @return
     */
    public static Key getKeyByAccessTokenAndLockmac(String accessToken, String lockmac) {
        queryBuilder = keyDao.queryBuilder();
       List<Key> keys = queryBuilder.where(KeyDao.Properties.AccessToken.eq(accessToken),
               KeyDao.Properties.LockMac.eq(lockmac)).list();
        if(keys.size() > 0) {
//            LogUtil.d("keys.size():" + keys.size(), DBG);
//            LogUtil.d("key:" + keys.get(0).toString(), DBG);
            return keys.get(0);
        } else return null;
    }

    /**
     * 保存key
     * @param key
     */
    public static void saveKey(Key key) {
        keyDao.save(key);
    }

    /**
     * 删除钥匙
     * @param key
     */
    public static void deleteKey(Key key) {
        keyDao.delete(key);
    }

    /**
     * 保存钥匙列表
     */
    public static void saveKeyList(List<Key> keys) {
        keyDao.saveInTx(keys);
    }

    /**
     * 清空所有钥匙
     */
    public static void deleteAllKey() {
        keyDao.deleteAll();
    }

    /**
     * 更新key
     * @param key
     */
    public static void updateKey(Key key) {
        keyDao.update(key);
    }
}

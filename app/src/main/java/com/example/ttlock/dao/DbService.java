package com.example.ttlock.dao;

import com.example.ttlock.MyApplication;
import com.example.ttlock.dao.gen.DaoSession;
import com.example.ttlock.dao.gen.KeyDao;
import com.example.ttlock.model.Key;

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

//    /**
//     * get keys by accessToken
//     * @param accessToken
//     * @return
//     */
//    public static List<Key> getKeysByAccessToken(String accessToken) {
//        queryBuilder = keyDao.queryBuilder();
//       return queryBuilder.where(KeyDao.Properties.AccessToken.eq(accessToken)).list();
//    }

    /**
     * get key by accessToken and lockmac
     * @param lockmac
     * @return
     */
    public static Key getKeyByLockmac(String lockmac) {
        queryBuilder = keyDao.queryBuilder();
        try {
            List<Key> keys = queryBuilder.where(KeyDao.Properties.LockMac.eq(lockmac)).list();
            if(keys.size() > 0) {
//            LogUtil.d("keys.size():" + keys.size(), DBG);
//            LogUtil.d("key:" + keys.get(0).toString(), DBG);
                return keys.get(0);
            } else return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * save key
     * @param key
     */
    public static void saveKey(Key key) {
        keyDao.save(key);
    }

    /**
     * delete key
     * @param key
     */
    public static void deleteKey(Key key) {
        keyDao.delete(key);
    }

    /**
     * save key list
     */
    public static void saveKeyList(List<Key> keys) {
        keyDao.saveInTx(keys);
    }

    /**
     * clear all keys
     */
    public static void deleteAllKey() {
        keyDao.deleteAll();
    }

    /**
     * update key
     * @param key
     */
    public static void updateKey(Key key) {
        keyDao.update(key);
    }
}

package cn.sun.miniredis.server.core;

import cn.sun.miniredis.database.RedisDB;
import cn.sun.miniredis.datastructure.RedisBytes;
import cn.sun.miniredis.datastructure.RedisData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RedisCoreImpl implements RedisCore {

    private final List<RedisDB> databases;
    private final int dbNum;
    private int currentDBIndex;

    public RedisCoreImpl(int dbNum) {
        this.dbNum = dbNum;
        this.databases = new ArrayList<>(dbNum);
        for (int i = 0; i < dbNum; i ++) {
            databases.add(new RedisDB(i));
        }
        this.currentDBIndex = 0;
    }

    @Override
    public Set<RedisBytes> keys() {
        RedisDB db = databases.get(getCurrentDBIndex());
        return db.keys();
    }

    @Override
    public void put(RedisBytes key, RedisData value) {
        RedisDB db = databases.get(getCurrentDBIndex());
        db.put(key, value);
    }

    @Override
    public RedisData get(RedisBytes key) {
        RedisDB db = databases.get(getCurrentDBIndex());
        if (db.exists(key)) {
            return db.get(key);
        }
        return null;
    }

    @Override
    public void remove(RedisBytes key) {
    }

    @Override
    public void selectDB(int dbIndex) {
        if (dbIndex >= 0 && dbIndex < dbNum) {
            this.currentDBIndex = dbIndex;
        } else {
            throw new IllegalArgumentException(("db index out of range"));
        }
    }

    @Override
    public int getDBNum() {
        return dbNum;
    }

    @Override
    public void setDBNum(int dbNum) {

    }

    @Override
    public int getCurrentDBIndex() {
        return currentDBIndex;
    }
}

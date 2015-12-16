package org.framework.basic.dao;

import org.apache.ibatis.annotations.*;
import org.framework.basic.entity.BaseEntity;
import org.framework.basic.mybatis.MyBatisProvider;

import java.util.List;

/**
 * Created by snow on 2015/7/25.
 */

public abstract interface BaseDao<T extends BaseEntity> {
    @SelectProvider(type = MyBatisProvider.class, method = "getAll")
    @Options(flushCache = false,useCache = true)
    @ResultMap("getMap")
    public List<T> getAll();

    @SelectProvider(type = MyBatisProvider.class, method = "getById")
    @Options(flushCache = false,useCache = true)
    @ResultMap("getMap")
    public T getById(String id);

    @InsertProvider(type = MyBatisProvider.class, method = "insert")
    @Options(keyProperty = "id",flushCache = true)
    public int insert(T t);

    @InsertProvider(type = MyBatisProvider.class, method = "insertBatch")
    @Options(keyProperty = "id",flushCache = true)
    public int insertBatch(@Param("list")List<T> list);

    @UpdateProvider(type = MyBatisProvider.class, method = "update")
    @Options(flushCache = true)
    public int update(T t);

    @DeleteProvider(type = MyBatisProvider.class, method = "deleteById")
    @Options(flushCache = true)
    public int deleteById(String id);

    @DeleteProvider(type = MyBatisProvider.class, method = "deleteByPrimaryKey")
    @Options(flushCache = true)
    public int deleteByPrimaryKey(T t);

    @DeleteProvider(type = MyBatisProvider.class,method = "deleteAll")
    @Options(flushCache = true)
    public int deleteAll();

}

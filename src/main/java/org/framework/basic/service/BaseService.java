package org.framework.basic.service;

import org.framework.basic.entity.BaseEntity;
import org.framework.basic.system.BaseException;

import java.util.List;

/**
 * Created by snow on 2015/8/20.
 */
public interface BaseService<T extends BaseEntity> {
    public List<T> getAll();

    public T getById(String id);

    public void insert(T t) throws BaseException;

    public void insert(List<T> list) throws BaseException;

    public void deleteById(String id) throws BaseException;

    public void deleteById(List<String> list) throws BaseException;

    public void delete(T t) throws BaseException;

    public void delete(List<T> list) throws BaseException;

    public void deleteAll() throws BaseException;

    public void update(T t) throws BaseException;

    public void update(List<T> list) throws BaseException;
}

package org.framework.basic.service.impl;

import org.framework.basic.dao.BaseDao;
import org.framework.basic.entity.BaseEntity;
import org.framework.basic.service.BaseService;
import org.framework.basic.system.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by snow on 2015/8/20.
 */
public class BaseServiceImpl<T extends BaseEntity> implements BaseService<T> {
	
	@Autowired
    private BaseDao<T> baseDao;

    public List<T> getAll() {
        return baseDao.getAll();
    }

    public T getById(String id) {
        return baseDao.getById(id);
    }

    @Transactional(readOnly = false)
    public void insert(T t) throws BaseException {
        if (baseDao.insert(t) != 1) {
            throw new BaseException();
        }
    }

    @Transactional(readOnly = false)
    public void insert(List<T> list) throws BaseException {
        for (T t : list) {
            insert(t);
        }
    }

    @Transactional(readOnly = false)
    public void deleteById(String id) throws BaseException {
        if (baseDao.deleteById(id) != 1) {
            throw new BaseException();
        }
    }

    @Transactional(readOnly = false)
    public void deleteById(List<String> list) throws BaseException {
        for (String id : list) {
            deleteById(id);
        }
    }

    @Transactional(readOnly = false)
    public void delete(T t) throws BaseException {
        if (baseDao.deleteByPrimaryKey(t) != 1) {
            throw new BaseException();
        }
    }

    @Transactional(readOnly = false)
    public void delete(List<T> list) throws BaseException {
        for (T t : list) {
            delete(t);
        }
    }

    @Transactional(readOnly = false)
    public void deleteAll() throws BaseException {
        baseDao.deleteAll();
    }

    @Transactional(readOnly = false)
    public void update(T t) throws BaseException {
        if (baseDao.update(t) != 1) {
            throw new BaseException();
        }
    }

    @Transactional(readOnly = false)
    public void update(List<T> list) throws BaseException {
        for (T t : list) {
            update(t);
        }
    }
}

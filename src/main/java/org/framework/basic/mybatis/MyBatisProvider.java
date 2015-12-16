package org.framework.basic.mybatis;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;
import org.framework.basic.entity.BaseEntity;
import org.framework.common.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by snow on 2015/8/19.
 * 封装通用的增删改查方法
 */
public class MyBatisProvider<T extends BaseEntity> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private String tableName;
    private Class<?> modelClass;
    private static ThreadLocal<Class<?>> threadModelClass = new ThreadLocal<Class<?>>();

    private void initFromThreadLocal() {
        modelClass = MyBatisProvider.threadModelClass.get();
        tableName = modelClass.getAnnotation(Table.class).name();
        MyBatisProvider.threadModelClass.remove();
    }

    public static void setModelClass(Class<?> modelClass) {
        MyBatisProvider.threadModelClass.set(modelClass);
    }

    /**
     * 查询所有
     * @return
     */
    public String getAll() {
        initFromThreadLocal();
        SQL sql = SELECT_FROM();
        sql = ORDER(null, sql);
        return sql.toString();
    }

    /**
     * 根据主键ID查询
     * @return
     */
    public String getById() {
        initFromThreadLocal();
        SQL sql = SELECT_FROM().WHERE("ID = #{id}");
        return sql.toString();
    }

    /**
     * 插入一条记录
     * @param t
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchFieldException
     */
    public String insert(final T t) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        initFromThreadLocal();
        // 设置默认值
        Date now = Calendar.getInstance().getTime();
        if (StringUtils.isBlank(t.getId())) {
            t.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        if(t.getCreateTime()==null){
            t.setCreateTime(now);
            t.setUpdateTime(now);
        }

        if (com.alibaba.druid.util.StringUtils.isEmpty(t.getCreateName())) {
            t.setCreateName("system");
            t.setUpdateName("system");
        }


        SQL sql = new SQL() {
            {
                INSERT_INTO(tableName);

                Map<String, Property> properties = ModelUtils.getProperties(t, ColumnTarget.INSERT);
                for (Property property : properties.values()) {
                    // 过滤不允许更新的字段
                    if (property.isId() || property.isNullValue(t)) {
                        continue;
                    }
                    VALUES(property.getColumnName(), "#{" + property.getName() + "}");
                }
            }
        };

        return sql.toString();
    }

    /**
     * 批量新增
     * @param param
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchFieldException
     */
    public String insertBatch(final Map<String,List<T>> param) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        initFromThreadLocal();
        // 设置默认值
        Date now = Calendar.getInstance().getTime();
        final List<T> list = param.get("list");
//		logger.debug("list info:{}",list);
        for(Object obj : list) {
//			logger.debug("obj info:{}",obj);
            T t = (T) obj;
            if (StringUtils.isBlank(t.getId())) {
                t.setId(UUID.randomUUID().toString().replace("-", ""));
            }
            if (t.getCreateTime() == null) {
                t.setCreateTime(now);
                t.setUpdateTime(now);
            }
            if (com.alibaba.druid.util.StringUtils.isEmpty(t.getCreateName())) {
                t.setCreateName("system");
                t.setUpdateName("system");
            }



        }
        T cla = list.get(0);
        final Map<String, Property> properties = ModelUtils.getProperties(cla, null);
        SQL sql = new SQL() {
            {
                INSERT_INTO(tableName);
                for (Property property : properties.values()) {
                    VALUES(property.getColumnName(), "#{" + property.getName() + "}");
                }

            }
        };


        Class classType = cla.getClass();
        Method[] methods = classType.getMethods();


        Map<String,Method> methodMap = new HashMap<String,Method>();
        for(Method method :methods){
//			logger.debug("method name:{}",method.getName());
            methodMap.put(method.getName(),method);
        }
//		Field[] declaredFields=classType.getDeclaredFields();

        Map<String,Class> typeMap = new HashMap<String,Class>();
        Map<String,Field> fieldMap = new HashMap<String,Field>();
        for(Class<?> clazz = classType ; clazz != Object.class ; clazz = clazz.getSuperclass()) {
            try {
                Field[] declaredFields=clazz.getDeclaredFields();
                for(Field field:declaredFields){
                    if(!fieldMap.containsKey(field.getName())){
                        fieldMap.put(field.getName(),field);
                        typeMap.put(field.getName(),field.getType());
                    }
                }
            } catch (Exception e) {
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
            }
        }
        String sqlStr = sql.toString();
        int index = StringUtils.lastIndexOf(sqlStr, "(");
        String prefixSql = StringUtils.substring(sqlStr,0,index);
        StringBuffer sqlBuffer = new StringBuffer(prefixSql);
        for(int i =0;i<list.size();i++) {
            T t = list.get(i);
            if(i==0){
                sqlBuffer.append("(");
            }else{
                sqlBuffer.append(",(");
            }

            Iterator<Property> iterator = properties.values().iterator();
            while (iterator.hasNext()) {
                Property property = iterator.next();

                String fieldName=property.getName();
                String methodName = null;
                if(fieldName.length() == 1) {
                    methodName = fieldName.substring(0, 1).toUpperCase();
                } else {
                    methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
                }
                methodName = "get" + methodName;
                Method method = methodMap.get(methodName);
                method.setAccessible(true);
                Object val = method.invoke(t);
                Class type =typeMap.get(fieldName);
//				logger.debug("========insertBatch field name:{},type:{},value:{}", fieldName, type, val);

                if (type.isEnum()) {
                    sqlBuffer.append("'");
                    if(val!=null) {
                        Enum<?> e = (Enum<?>) val;
                        sqlBuffer.append(e.name());
                    }
                    sqlBuffer.append("'");
                } else if (Date.class.equals(type)) {
                    sqlBuffer.append("'");
                    if(val!=null) {
                        Date date = (Date) val;
                        sqlBuffer.append(DateUtils.convert(date));
                    }else{
                        sqlBuffer.append("0000-00-00 00:00;00");
                    }
                    sqlBuffer.append("'");
                } else {
                    sqlBuffer.append("'");
                    if(val!=null) {
                        sqlBuffer.append(val.toString());
                    }
                    sqlBuffer.append("'");
                }
                if(iterator.hasNext()){
                    sqlBuffer.append(",");
                }
            }

            sqlBuffer.append(")");
        }
        return sqlBuffer.toString();
    }

    /**
     * 更新
     * @param t
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public String update(final T t) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        initFromThreadLocal();
        // 设置默认值
        t.setUpdateTime(Calendar.getInstance().getTime());

        // 过滤不允许更新的字段
        t.setCreateTime(null);
        t.setCreateName(null);
        SQL sql = new SQL() {
            {
                UPDATE(tableName);

                String className = StringUtils.split(modelClass.getName(), "$")[0];
                try {
                    Map<String, Property> properties = ModelUtils.getProperties(Class.forName(className), ColumnTarget.UPDATE);

                    for (Property property : properties.values()) {
                        // 过滤不允许更新的字段
                        if (property.isId() || property.isNullValue(t)) {
                            continue;
                        }

                        SET(property.getColumnName() + " = #{" + property.getName() + "}");
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                WHERE("ID = #{id}");
            }
        };
        return sql.toString();
    }

    public String deleteById(String id) {
        initFromThreadLocal();

        SQL sql = new SQL() {
            {
                DELETE_FROM(tableName);
                WHERE("ID = #{id}");
            }
        };
        return sql.toString();
    }

    public String deleteByPrimaryKey(final T t) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException{
        initFromThreadLocal();

        String sql = new SQL() {
            {
                boolean flg = false;
                DELETE_FROM(tableName);
                Map<String, Property> properties = ModelUtils.getProperties(t, null);
                for (Property property : properties.values()) {
                    if(t.getClass().getDeclaredField(property.getName()).getAnnotation(javax.persistence.Id.class)!=null){
                        WHERE(property.getColumnName() + " = #{" + property.getName() + "}");
                        flg = true;
                    }
                }
                if(!flg){
                    throw new UnsupportedOperationException("please set jpa @Id to entity field");
                }
            }
        }.toString();
        logger.debug("deleteByPrimaryKey sql:{}"+sql);
        //TODO
        return sql;
    }

    public String deleteAll() {
        initFromThreadLocal();
        SQL sql = new SQL() {
            {
                DELETE_FROM(tableName);
            }
        };
        return sql.toString();
    }

    private SQL SELECT_FROM() {
        final Map<String, Property> columns = ModelUtils.getProperties(modelClass, ColumnTarget.SELECT);
        return new SQL() {
            {
                for (Property property : columns.values()) {
                    SELECT(property.getColumnName());
                }
                FROM(tableName);
            }
        };
    }

    private SQL ORDER(List<Sort> sortList, SQL sql) {
        Map<String, Property> properties = ModelUtils.getProperties(modelClass, ColumnTarget.ORDER);
        for (Property property : properties.values()) {
            sql.ORDER_BY(property.getOrder());
        }
        if (sortList != null) {
            for (Sort sort : sortList) {
                sql.ORDER_BY(sort.getProperty() + " " + sort.getDirection());
            }
        }
        return sql;
    }
}

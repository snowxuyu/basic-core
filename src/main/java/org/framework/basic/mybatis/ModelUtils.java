package org.framework.basic.mybatis;

import org.framework.basic.entity.BaseEntity;
import org.framework.common.util.ReflectUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by snow on 2015/8/19.
 */
public class ModelUtils {
    public static Map<String, Property> getProperties(BaseEntity object, ColumnTarget columnTarget) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Class<?> modelClass = object.getClass();
        Map<String, Property> properties = getProperties(modelClass, columnTarget);
        Map<String, Property> results = new HashMap<String, Property>(properties.size());
        for (Map.Entry<String, Property> propertyEntry : properties.entrySet()) {
            Property property = propertyEntry.getValue();
            if (columnTarget == ColumnTarget.INSERT || columnTarget == ColumnTarget.UPDATE || columnTarget == ColumnTarget.WHERE) {
                if (property.isNullValue(object)) { // 空值忽略
                    continue;
                }
            }

            results.put(propertyEntry.getKey(), property);
        }

        return results;
    }

    /**
     *
     * @param columnTarget 允许为null
     * @return
     */
    public static Map<String, Property> getProperties(Class<?> modelClass, ColumnTarget columnTarget) {
        PropertyDescriptor[] propDescriptors = ReflectUtils.getPropertyDescriptors(modelClass);
        Map<String, Property> properties = new HashMap<String, Property>(propDescriptors.length);
        for (PropertyDescriptor propertyDescriptor : propDescriptors) {
            Property property = new Property(modelClass, propertyDescriptor);
            if (property.isTransient()) {
                continue;
            }
            if (property.isUnableForColumnTarget(columnTarget)) {
                continue;
            }
            if (columnTarget == ColumnTarget.INSERT || columnTarget == ColumnTarget.UPDATE || columnTarget == ColumnTarget.WHERE) {
                if (property.isId()) { // ID忽略
                    continue;
                }
            }
            if (columnTarget == ColumnTarget.ORDER) {
                if (!property.isOrderColumn()) { // 仅保留ordercolumn
                    continue;
                }
            }

            properties.put(property.getName(), property);
        }
        return properties;
    }
}

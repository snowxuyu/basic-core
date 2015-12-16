package org.framework.basic.entity;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by snow on 2015/8/1.
 */
@Data
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = -3440718127562204854L;
	public static final String CREATE_DATE_PROPERTY_NAME = "createTime";// "创建日期"属性名称
    public static final String MODIFY_DATE_PROPERTY_NAME = "updateTime";// "修改日期"属性名称

    @Id
    private String id;

    private String createName;

    private Date createTime;

    private String updateName;

    private  Date updateTime;

	public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BaseEntity other = (BaseEntity) obj;
        if (id == null || other.getId() == null) {
            return false;
        } else {
            return (id.equals(other.getId()));
        }
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
}

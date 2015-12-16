package org.framework.basic.mybatis;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by snow on 2015/8/20.
 */
@Data
public class Sort implements Serializable {

    private static final long serialVersionUID = -8455714603523183477L;
    private String property;
    private String direction;

}

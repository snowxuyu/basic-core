package org.framework.basic.system;

import lombok.Data;

import java.util.List;

/**分页公式  (currentPage-1)*pageSize, pageSize
 * Created by snow on 2015/7/24.
 * 统一使用分页插件 这个类不再使用
 */
@Data
public class Page<T> {
    /**
     * 分页公式： （（当前页数-1）x 分页大小）， 分页大小
     */

    private List<T> datas;  //page存放的数据

    private Integer pageSize; //分页的大小

    private Integer currentPage; //当前页

    private long totalRecord; //总共有多少条记录

}

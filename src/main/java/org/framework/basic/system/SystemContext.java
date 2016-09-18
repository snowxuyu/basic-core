package org.framework.basic.system;

/**
 * Created by snow on 2015/7/24.
 * 用了传递分页数据 排序数据
 * 统一使用分页插件 这个类不再使用
 */
public class SystemContext {

    public static ThreadLocal<Integer> pageSize = new ThreadLocal<Integer>(); //分页大小

    public static ThreadLocal<Integer> currentPage = new ThreadLocal<Integer>(); //当前页

    public static ThreadLocal<String> sort = new ThreadLocal<String>(); // 排序方式  升序还是降序

    public static ThreadLocal<String> orders = new ThreadLocal<String>();  //排序字段

    public static ThreadLocal<String> realPath = new ThreadLocal<String>();  //系统路径



    public static Integer getPageSize() {
        return (Integer)pageSize.get();
    }

    public static void setPageSize(Integer _pageSize) {
        pageSize.set(_pageSize);
    }

    public static void removePageSize() {
        pageSize.remove();
    }

    public static Integer getCurrentPage() {
        return (Integer)currentPage.get();
    }

    public static void setCurrentPage(Integer _currentPage) {
        currentPage.set(_currentPage);
    }

    public static void removeCurrentPage() {
        currentPage.remove();
    }

    public static String getSort() {
        return (String)sort.get();
    }

    public static void setSort(String _sort) {
        sort.set(_sort);
    }

    public static void removeSort() {
        sort.remove();
    }

    public static String getOrders () {
        return (String) orders.get();
    }

    public static void setOrders(String _orders) {
        orders.set(_orders);
    }

    public static void removeOrders() {
        orders.remove();
    }

    public static String getRealPath() {
        return (String) realPath.get();
    }

    public static void setRealPath(String _realPath) {
        realPath.set(_realPath);
    }

    public static void removeRealPath() {
        realPath.remove();
    }

}

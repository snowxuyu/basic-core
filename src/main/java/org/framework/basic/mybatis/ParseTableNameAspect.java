package org.framework.basic.mybatis;


import org.apache.ibatis.binding.MapperProxy;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.framework.basic.entity.BaseEntity;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * Created by snow on 2015/8/20.
 * mybatis的一个切面
 */
@Aspect
@Component
public class ParseTableNameAspect {

    @Around("execution(* org.framework.basic.dao.BaseDao.*(..))")
    public Object invoke(ProceedingJoinPoint proceedingJoinPoint)
            throws Throwable {
        parseTableName(proceedingJoinPoint);

        return proceedingJoinPoint.proceed();
    }

    private void parseTableName(ProceedingJoinPoint proceedingJoinPoint)
            throws NoSuchFieldException, IllegalAccessException,
            ClassNotFoundException {
        // 将modelClass添加到线程变量
        if(proceedingJoinPoint.getArgs()!=null
                &&proceedingJoinPoint.getArgs().length>0
                &&proceedingJoinPoint.getArgs()[0] instanceof BaseEntity){
            MyBatisProvider.setModelClass(proceedingJoinPoint.getArgs()[0].getClass());
        }else{
            // 获取代理目标对象
            Field h = Proxy.class.getDeclaredField("h");
            h.setAccessible(true);
            Object proxyTarget = h.get(proceedingJoinPoint.getTarget());
            // 获取dao类
            Field mapperInterface = MapperProxy.class
                    .getDeclaredField("mapperInterface");
            mapperInterface.setAccessible(true);
            Class<?> cl = (Class<?>) mapperInterface.get(proxyTarget);

            // 获取model类
            String modelName = cl.getName().replace(".dao.", ".entity.").replace("Dao", "");
            MyBatisProvider.setModelClass(Class.forName(modelName));
        }
    }
}

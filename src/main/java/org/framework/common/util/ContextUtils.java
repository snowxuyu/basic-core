package org.framework.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by snow on 2015/12/16.
 */
 @Component
public class ContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static final Logger logger = LoggerFactory.getLogger(ContextUtils.class);

    public static ApplicationContext getApplicationContext() {
        synchronized (ContextUtils.class) {
            while (applicationContext == null) {
                try {
                    ContextUtils.class.wait(60000);
                    if (applicationContext == null) {
                        logger.warn("Have been waiting for ApplicationContext to be set for 1 minute", new Exception());
                    }
                } catch (InterruptedException ex) {
                    logger.debug("getApplicationContext, wait interrupted");
                }
            }
            return applicationContext;
        }
    }

    public static Object getBean(Class<?> beanType) {
        return getApplicationContext().getBean(beanType);
    }

    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException{
        synchronized (ContextUtils.class) {
            ContextUtils.applicationContext = applicationContext;
            ContextUtils.class.notifyAll();
        }
    }
}

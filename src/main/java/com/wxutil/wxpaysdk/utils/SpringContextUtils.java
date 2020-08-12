package com.wxutil.wxpaysdk.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Administrator
 */
public class SpringContextUtils implements ApplicationContextAware {

    private static ApplicationContext context;


    @Override
    public void setApplicationContext(ApplicationContext context)
            throws BeansException {

        SpringContextUtils.context = context;

    }


    public static ApplicationContext getContext() {

        return context;

    }

    public static Object getBean(String beanName) {

        return context.getBean(beanName);

    }


}

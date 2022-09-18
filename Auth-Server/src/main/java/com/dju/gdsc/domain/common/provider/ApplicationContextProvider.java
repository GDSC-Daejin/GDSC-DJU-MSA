package com.dju.gdsc.domain.common.provider;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext ctx;
    public void setApplicationContext(@NotNull ApplicationContext ctx) throws BeansException {
        ApplicationContextProvider.ctx = ctx;
    }
    public static ApplicationContext getApplicationContext() {
        return ctx;
    }




}

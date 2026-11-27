package com.mycompany.pencatatanbahan.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Pemuat Spring container (menggantikan HibernateUtil lama).
 * SessionFactory sekarang dibuat & dikelola Spring lewat applicationContext.xml.
 */
public class SpringUtil {

    private static ApplicationContext context;

    private SpringUtil() {
    }

    public static synchronized ApplicationContext getContext() {
        if (context == null) {
            context = new ClassPathXmlApplicationContext("applicationContext.xml");
        }
        return context;
    }

    public static <T> T getBean(Class<T> type) {
        return getContext().getBean(type);
    }
}

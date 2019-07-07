package com.TyxApp.bangumi.util;


public class ExceptionUtil {
    public static void checkNull(Object o, String exceptionmes) {
        if (o == null) {
            throw new NullPointerException(exceptionmes);
        }
    }

}

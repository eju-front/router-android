package com.eju.router.sdk;

import java.lang.reflect.*;
import java.lang.reflect.Method;

/**
 * class description
 *
 * @author tangqianwei
 */
class RouterBridge<T> {

    private T instance;

    RouterBridge(T instance) {
        this.instance = instance;
    }

    void execute(String funcName, String jsonParams) {
        if(null == instance) return;

        Class<?> clazz = instance.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if(funcName.equalsIgnoreCase(method.getName())) {
                try {
                    method.invoke(instance, jsonParams);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        // TODO error
    }
}

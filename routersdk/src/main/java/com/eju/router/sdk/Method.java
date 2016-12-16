package com.eju.router.sdk;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Joe on 2016/11/25.
 * Email lovejjfg@gmail.com
 */
@IntDef(flag = true, value = {
        EjuRequest.METHOD_GET,
        EjuRequest.METHOD_POST,
        EjuRequest.METHOD_PUT,
        EjuRequest.METHOD_DELETE
})
@Retention(RetentionPolicy.SOURCE)
/*package*/ @interface Method {
}

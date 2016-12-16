package com.eju.router.sdk;

import android.net.Uri;

import com.eju.router.sdk.exception.EjuParamException;

import java.lang.reflect.ParameterizedType;


/**
 * Parameter converter
 *
 * @author  tangqianwei
 */
/*package*/ abstract class ParamConverter<Param> {

    /**
     * @param key url key, such as 'aaa' in this url: http://example.com?aaa=1     * @param param
     * @param param param converted to url
     * @param encode whether url should be encoded
     * @return url
     * @throws EjuParamException if error in converting process
     */
    String toUrl(String key, Param param, boolean encode) throws EjuParamException {
        String part = toUrl(param);
        return key + "=" + (encode ? Uri.encode(part) : part);
    }

    /**
     * convert param to url
     *
     * @param param param converted to url
     * @return url representation
     * @throws EjuParamException if error in converting process
     */
    protected abstract String toUrl(Param param) throws EjuParamException;

    /**
     * convert params to type {@code Param}
     *
     * @param query url to be converted
     * @return if {@code Param} could be converted, returns instance.
     * Otherwise, returns {@code null}
     * @throws EjuParamException if error in converting process
     */
    protected abstract Param fromUrl(String query) throws EjuParamException;

    @Override
    public String toString() {
        Class<?> clazz = getClass();

        Class<?>[] classes = ClassUtils.getParameterizeClassActualClasses(clazz);
        if(null != classes) {
            StringBuilder builder = new StringBuilder();
            for(Class<?> cls : classes) {
                builder.append(cls.getName()).append(',');
            }

            return builder.deleteCharAt(builder.length()-1).toString();
        }
        return clazz.getName();
    }
}

package com.eju.router.sdk;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.eju.router.sdk.exception.EjuParamException;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Parameter adapter
 *
 * @author tangqianwei
 */
/*package*/ class ParamAdapter {

    private static final ParamConverter<String> STRING_PARAM_CONVERTER =
            new ParamConverter<String>() {
                @Override
                public String toUrl(String value) {
                    return value;
                }

                @Override
                public String fromUrl(String url) {
                    if (url.startsWith("{")) return null;
                    return url;
                }
            };

    private static final ParamConverter<List> LIST_PARAM_CONVERTER =
            new ParamConverter<List>() {
                @Override
                public String toUrl(
                        String key, List params, boolean encode) throws EjuParamException {
                    if (0 == params.size()) {
                        return null;
                    }

                    // same object in one list
                    ParamConverter<Object> converter = getParamConverter(params.get(0).getClass());

                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < params.size(); i++) {
                        String url = converter.toUrl(key + "[]", params.get(i), encode);
                        if (null != url) {
                            builder.append(url);
                            builder.append("&");
                        }
                    }

                    builder.deleteCharAt(builder.length() - 1);
                    return builder.toString();
                }

                @Override
                protected String toUrl(List list) throws EjuParamException {
                    return null;
                }

                @Override
                public List fromUrl(String url) throws EjuParamException {
                    return null;
                }
            };

    private static final ParamConverter<Integer> INTEGER_PARAM_CONVERTER =
            new PrimitiveParamConverter<Integer>() {
                @Override
                public Integer fromUrl(String query) throws EjuParamException {
                    final Integer obj;
                    try {
                        obj = Integer.valueOf(query);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                    return obj;
                }
            };

    private static final ParamConverter<Boolean> BOOLEAN_PARAM_CONVERTER =
            new PrimitiveParamConverter<Boolean>() {
                @Override
                public Boolean fromUrl(String query) throws EjuParamException {
                    if (query.equalsIgnoreCase("true")) {
                        return true;
                    } else if (query.equalsIgnoreCase("false")) {
                        return false;
                    } else {
                        return null;
                    }
                }
            };

    private static final ParamConverter<Float> FLOAT_PARAM_CONVERTER =
            new PrimitiveParamConverter<Float>() {
                @Override
                public Float fromUrl(String query) throws EjuParamException {
                    //use double to instead float
                    //try {
                    //    return Float.valueOf(query);
                    //} catch (NumberFormatException e) {
                        return null;
                    //}
                }
            };

    private static final ParamConverter<Double> DOUBLE_PARAM_CONVERTER =
            new PrimitiveParamConverter<Double>() {
                @Override
                public Double fromUrl(String query) throws EjuParamException {
                    try {
                        return Double.valueOf(query);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            };

    private static final ParamConverter<Character> CHARACTER_PARAM_CONVERTER =
            new PrimitiveParamConverter<Character>() {
                @Override
                public Character fromUrl(String query) throws EjuParamException {
                    if (1 != query.length()) {
                        return null;
                    }
                    return query.charAt(0);
                }
            };

    private static final ParamConverter<Long> LONG_PARAM_CONVERTER =
            new PrimitiveParamConverter<Long>() {
                @Override
                public Long fromUrl(String query) throws EjuParamException {
                    try {
                        return Long.valueOf(query);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            };

    private static final ParamConverter<Short> SHORT_PARAM_CONVERTER =
            new PrimitiveParamConverter<Short>() {
                @Override
                public Short fromUrl(String query) throws EjuParamException {
                    // use int to instead short
                    //try {
                    //    return Short.valueOf(query);
                    //} catch (NumberFormatException e) {
                        return null;
                    //}
                }
            };

    private static final ParamConverter<Byte> BYTE_PARAM_CONVERTER =
            new PrimitiveParamConverter<Byte>() {
                @Override
                public Byte fromUrl(String query) throws EjuParamException {
                    // use int to instead byte
                    //try {
                    //    return Byte.valueOf(query);
                    //} catch (NumberFormatException e) {
                        return null;
                    //}
                }
            };

    private static final LinkedHashMap<String, ParamConverter<?>> mConverters =
            new LinkedHashMap<>();

    private Map<String, Object> mParams = new HashMap<>();

    /**
     * !!!converters's order do not change
     */
    /*pakage*/ ParamAdapter() {
        addParamConverter(Boolean.class, BOOLEAN_PARAM_CONVERTER);
        addParamConverter(Character.class, CHARACTER_PARAM_CONVERTER);
        addParamConverter(Byte.class, BYTE_PARAM_CONVERTER);
        addParamConverter(Short.class, SHORT_PARAM_CONVERTER);
        addParamConverter(Integer.class, INTEGER_PARAM_CONVERTER);
        addParamConverter(Long.class, LONG_PARAM_CONVERTER);
        addParamConverter(Float.class, FLOAT_PARAM_CONVERTER);
        addParamConverter(Double.class, DOUBLE_PARAM_CONVERTER);
        addParamConverter(String.class, STRING_PARAM_CONVERTER);
        addParamConverter(List.class, LIST_PARAM_CONVERTER);
    }

    /**
     * add parameter converter for specific class
     *
     * @param clazz class to add
     * @param converter converter with {@code clazz}
     * @param <T> class template
     */
    /*package*/ static <T> void addParamConverter(@NonNull Class<T> clazz, ParamConverter<T> converter) {
        mConverters.put(filter(clazz), converter);
    }

    /**
     * get parameter converter for specific class
     *
     * @param clazz specific class
     * @param <T> class template
     * @return equivalent converter
     */
    /*package*/ static <T> ParamConverter<T> getParamConverter(@NonNull Class<?> clazz) {
        ClassUtils.TreeClass tree = ClassUtils.getClassHierarchyTree(clazz);
        if(null == tree) {
            String msg = String.format("cannot get class[%s] hierarchy tree!", clazz.getName());
            throw new IllegalArgumentException(msg);
        }

        String classIdentify = filter(tree.getRepresentationClass());
        ParamConverter<?> converter = mConverters.get(classIdentify);
        if (null != converter) {
            //noinspection unchecked
            return (ParamConverter<T>) converter;
        }

        Iterator<ClassUtils.TreeClass> iterator = tree.iterator();
        while (true) {
            if (!(iterator.hasNext())) break;

            classIdentify = filter(iterator.next().getRepresentationClass());
            converter = mConverters.get(classIdentify);
            if (null != converter) {
                //noinspection unchecked
                return (ParamConverter<T>) converter;
            }
        }

        String msg = "no such converter for class [" + clazz.getName() + "]";
        throw new IllegalArgumentException(msg);
    }

    /**
     * add default parameter class {@code WrapperObjectParamConverter} with specific class\
     *
     * @param clazz specific class
     * @param <T> class template
     */
    /*package*/ static <T> void addParamConverter(@NonNull final Class<T> clazz) {
        addParamConverter(clazz, new WrapperObjectParamConverter<T>(clazz));
    }

    /**
     * fill current parameter map with {@code Bundle}
     *
     * @param bundle source parameters
     */
    /*package*/ void setParam(Bundle bundle) {
        clearParams();

        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            mParams.put(key, bundle.get(key));
        }
    }

    /**
     * fill current parameter map with {@code Map<?, ?>}
     *
     * @param params source parameters
     */
    /*package*/ void setParam(Map<String, Object> params) {
        clearParams();

        mParams.putAll(params);
    }

    /**
     * turn current parameter map to url represented by {@code String}
     *
     * @return map string representation
     * @throws EjuParamException error in turn
     */
    /*package*/ String toURL() throws EjuParamException {
        final StringBuilder builder = new StringBuilder();

        try {
            MapUtil.foreach(mParams, new MapUtil.Consumer<String, Object>() {
                @Override
                public boolean consume(String key, Object value) throws Exception {
//                    try {
                        ParamConverter<Object> converter = getParamConverter(value.getClass());

                        // TODO ignore the case that url do not need encode
                        String url = converter.toUrl(key, value, true);
                        if (null != url) {
                            builder.append(url);
                            builder.append("&");
                        }
//                    } catch (EjuParamException ex) {
//                        EjuLog.e(ex.getMessage());
//                    }
                    return false;
                }
            });
        } catch (Exception e) {
            throw new EjuParamException(e);
        }

        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    /**
     * turn current parameter map to {@code Bundle} representation
     *
     * @return map bundle representation
     */
    /*package*/ Bundle toBundle() throws EjuParamException {
        final Bundle bundle = new Bundle();
        try {
            MapUtil.foreach(mParams, new MapUtil.Consumer<String, Object>() {
                @Override
                public boolean consume(String key, Object value) {
                    putObjectInBundle(bundle, key, value);
                    return false;
                }
            });
        } catch (Exception e) {
            throw new EjuParamException(e);
        }
        return bundle;
    }

    /**
     * decode the given url to {@code Bundle} representation
     * @param url url to decode
     * @return url Bundle representation
     */
    /*package*/ Bundle fromUrl(String url) {
        if (!url.endsWith("&")) {
            url = url.concat("&");
        }

        return decode(url);
    }

    /**
     * @see com.eju.router.sdk.ParamAdapter#fromUrl(String)
     */
    private Bundle decode(String url) {
        final Bundle bundle = new Bundle();

        int count = 0;
        boolean isArray = false;
        try {
            String key = null;
            Map<String, ArrayList<? extends Serializable>> lists = null;
            for (int i = 0; i < url.length(); i++) {
                char ch = url.charAt(i);
                switch (ch) {
                    case '=': {
                        int start = i - count;
                        int end = i;

                        if (']' == url.charAt(i - 1)) {
                            isArray = true;
                            end -= 2;
                        } else {
                            isArray = false;
                        }

                        key = url.substring(start, end);
                        count = 0;
                        continue;
                    }
                    case '&': {
                        if (null == key) {
                            throw new EjuParamException("no key for parameter");
                        }

                        final int start = i - count;
                        final String query = url.substring(start, i);
                        final Object object[] = {null};
                        try {
                            MapUtil.foreach(mConverters,
                                    new MapUtil.Consumer<String, ParamConverter<?>>() {
                                        @Override
                                        public boolean consume(
                                                String key, ParamConverter<?> value) throws Exception {
                                            object[0] = value.fromUrl(query);
                                            return null != object[0];
                                        }
                                    });
                        } catch(Exception e) {
                            throw new EjuParamException(e);
                        }
                        if (null == object[0]) {
                            throw new EjuParamException("no convert for [" + query + "]");
                        }

                        if (!isArray) {
                            putObjectInBundle(bundle, key, object[0]);
                        } else {
                            if (null == lists) {
                                lists = new HashMap<>();
                            }
                            putValueInContainer(lists, key, (Serializable) object[0]);
                        }

                        count = 0;
                        continue;
                    }
                    default:
                        ++count;
                        break;
                }
            }

            if (null != lists) {
                try {
                    MapUtil.foreach(
                            lists,
                            new MapUtil.Consumer<String, ArrayList<? extends Serializable>>() {
                                @Override
                                public boolean consume(
                                        String key, ArrayList<? extends Serializable> value) {
                                    bundle.putSerializable(key, value);
                                    return false;
                                }
                            });
                } catch (Exception e) {
                    throw new EjuParamException(e);
                }
            }

        } catch (EjuParamException e) {
            EjuLog.e(e.getMessage());
        }

        return bundle;
    }

    /**
     * filter all primitive class
     * @param clazz class
     * @return class name
     */
    private static String filter(Class clazz) {
        if (int.class == clazz) {
            clazz = Integer.class;
        } else if (boolean.class == clazz) {
            clazz = Boolean.class;
        } else if (float.class == clazz) {
            clazz = Float.class;
        } else if (double.class == clazz) {
            clazz = Double.class;
        } else if (char.class == clazz) {
            clazz = Character.class;
        } else if (long.class == clazz) {
            clazz = Long.class;
        } else if (short.class == clazz) {
            clazz = Short.class;
        } else if (byte.class == clazz) {
            clazz = Byte.class;
        }

        return clazz.getName();
    }

    private <T extends Serializable> void putValueInContainer(
            Map<String, ArrayList<? extends Serializable>> lists,
            String key, T object) throws EjuParamException {
        ArrayList<T> list;
        try {
            ArrayList<? extends Serializable> tmp = lists.get(key);
            if (null == tmp) {
                tmp = new ArrayList<>();
                lists.put(key, tmp);
            }

            //noinspection unchecked
            list = (ArrayList<T>) tmp;
            list.add(object);
        } catch (ClassCastException ex) {
            throw new EjuParamException("wrong param type for key[" + key + "]");
        }
    }

    private void putObjectInBundle(Bundle bundle, String key, Object object) {
        if (object instanceof Integer) {
            bundle.putInt(key, (int) object);
        } else if (object instanceof String) {
            bundle.putString(key, (String) object);
        } else if (object instanceof Boolean) {
            bundle.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            bundle.putFloat(key, (Float) object);
        } else if (object instanceof Double) {
            bundle.putDouble(key, (Double) object);
        } else if (object instanceof Character) {
            bundle.putChar(key, (Character) object);
        } else if (object instanceof Long) {
            bundle.putLong(key, (Long) object);
        } else if (object instanceof Short) {
            bundle.putShort(key, (Short) object);
        } else if (object instanceof Byte) {
            bundle.putByte(key, (Byte) object);
        } else if (object instanceof Serializable) {
            bundle.putSerializable(key, (Serializable) object);
        }
    }

    private void clearParams() {
        mParams.clear();
    }

    /**
     * primitive parameter converter superclass
     * @param <T> class template
     */
    private static abstract class PrimitiveParamConverter<T> extends ParamConverter<T> {
        @Override
        public String toUrl(T t) throws EjuParamException {
            return t.toString();
        }
    }

    /**
     * wrapper object parameter converter superclass
     * @param <T> class template
     */
    private static class WrapperObjectParamConverter<T> extends ParamConverter<T> {

        private static Gson _gson = new Gson();
        private Class<?> _type;

        WrapperObjectParamConverter(Class<?> type) {
            this._type = type;
        }

        @Override
        public String toUrl(T o) throws EjuParamException {
            return _gson.toJson(o);
        }

        @Override
        public T fromUrl(String query) throws EjuParamException {
            return (T) _gson.fromJson(Uri.decode(query), _type);
        }

        @Override
        public String toString() {
            return _type.getName();
        }
    }
}

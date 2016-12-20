package com.eju.router.sdk;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;

/**
 * class utils
 *
 * @author tangqianwei
 */

/*package*/ class ClassUtils {

    /**
     * tree representation of class hierarchy
     * as follow:
     *
     * A    B               C
     * ^    ^               ^
     * |    |               |
     * ------(implements)   |(extends)
     *    |                 |
     *    -------------------
     *              |
     *            this
     */
    @SuppressWarnings("unused")
    /*package*/ static class TreeClass implements Iterable{

        private TreeClass[] _interfaces;
        private TreeClass _superClass;

        private Class<?> _class;

        TreeClass(Class<?> cls) {
            this._class = cls;
        }

        TreeClass[] getInterfaces() {
            return _interfaces;
        }

        void setInterfaces(TreeClass[] interfaces) {
            this._interfaces = interfaces;
        }

        TreeClass getSuperClasses() {
            return _superClass;
        }

        void setSuperClasses(TreeClass superClasses) {
            this._superClass = superClasses;
        }

        Class<?> getRepresentationClass() {
            return _class;
        }

        /**
         * get superclasses and superinterfaces count
         * @return sum
         */
        int getCount() {
            int count = 0;

            if(null != _superClass) {
                count++;
            }

            if(null != _interfaces) {
                count += _interfaces.length;
            }

            return count;
        }

        @Override
        public String toString() {
            return _class.getName();
        }

        @Override
        public Iterator<TreeClass> iterator() {
            return new TreeClassIterator();
        }

        private final class TreeClassIterator implements Iterator<TreeClass> {

            private int _all = TreeClass.this.getCount();
            private int _cursor = 0;

            @Override
            public boolean hasNext() {
                return _cursor < _all;
            }

            @Override
            public TreeClass next() {
                TreeClass[] interfaces = TreeClass.this._interfaces;
                if(null != interfaces) {
                    final int length = interfaces.length;
                    if (0 < length && _cursor < length) {
                        return interfaces[_cursor++];
                    }
                }

                _cursor++;
                return TreeClass.this._superClass;
            }
        }
    }

    /**
     * get {@code TreeClass} representation of the specific class
     * @param clazz the specific class
     * @return {@code TreeClass} representation
     */
    /*package*/ static TreeClass getClassHierarchyTree(Class<?> clazz) {
        if(null == clazz) {
            return null;
        }

        TreeClass tree = new TreeClass(clazz);

        Type superClassType = clazz.getGenericSuperclass();
        tree.setSuperClasses(
                getClassHierarchyTree(getClassFromType(superClassType)));

        Type[] interfacesTypes = clazz.getGenericInterfaces();
        final int length = interfacesTypes.length;

        TreeClass[] trees;
        if(0 == length) {
            trees = null;
        } else {
            trees = new TreeClass[length];
            for (int i = 0; i < length; i++) {
                trees[i] = getClassHierarchyTree(getClassFromType(interfacesTypes[i]));
            }
        }
        tree.setInterfaces(trees);

        return tree;
    }

    /**
     * get parameterize type of the specific class
     *
     * !!! there is limitation in this method at current ver,
     * this method could only get the superclass's parameterize type of the given class,
     * if the superclass is not parameterize type, then it will return {@code null}.
     *
     * @param clazz the specific class
     * @return parameterize type
     */
    /*package*/ static Class<?>[] getParameterizeClassActualClasses(Class<?> clazz) {
        Type type = clazz.getGenericSuperclass();
        if(type instanceof Class<?>) {
            return null;
        } else if(type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();

            final int length = types.length;
            if(0 >= length) {
                return null;
            }

            Class<?>[] ret = new Class[types.length];
            for(int i = 0; i < length; i++) {
                Class<?> cls;
                try {
                    cls = (Class<?>)types[i];
                } catch(ClassCastException e) {
                    continue;
                }

                ret[i] = cls;
            }

            return ret;
        } else if(type instanceof GenericArrayType) {
            return null;
        } else {
            return null;
        }
    }

    private static Class<?> getClassFromType(Type type) {
        Class<?> clazz;
        if(null == type) {
            clazz = null;
        } else if(type instanceof Class<?>) {
            clazz = (Class<?>) type;
        } else if(type instanceof ParameterizedType) {
            clazz = (Class<?>) ((ParameterizedType) type).getRawType();
        } else {
            clazz = Object.class;
        }
        return clazz;
    }
}

package com.eju.router.sdk;

import java.util.List;


/**
 * Created by SidneyXu on 2016/11/30.
 */

/**
 * init the {@link Router} with this ,you can add {@link EjuRequest} to check  update your local ViewMap.
 * <p>and you can set the default ViewMapVersion with {@link Option#defaultVersion}.
 * <p>and also you can add  {@link Option#nativeRouteSchema}.
 */
public class Option {

    public List<String> nativeRouteSchema;
    public EjuRequest request;
    public String defaultVersion;
}

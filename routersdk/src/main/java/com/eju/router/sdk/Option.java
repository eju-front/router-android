package com.eju.router.sdk;

import java.util.List;


/**
 * init the {@link Router} with this,
 * you can:
 * <p>add {@link EjuRequest} to check or update your local ViewMap.
 * <p>set the default ViewMapVersion with {@link Option#defaultVersion}.
 * <p>add {@link Option#nativeRouteSchema}.
 *
 * @author SidneyXu
 */
public class Option {

    public List<String> nativeRouteSchema;
    public EjuRequest request;
    public String defaultVersion;
}

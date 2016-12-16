package com.eju.router.sdk;

import android.app.Application;

import org.junit.After;
import org.junit.Before;
import org.robolectric.RuntimeEnvironment;

public class BaseTest {

    protected Application application;

    @Before
    public void setUp() {
        application = RuntimeEnvironment.application;
    }

    @After
    public void tearDown() {
    }

}

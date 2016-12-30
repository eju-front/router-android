package com.eju.router.sdk;

import android.app.Fragment;

import com.eju.router.sdk.exception.EjuException;
import com.eju.router.sdk.resource.TargetFragment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author Sidney
 */
@RunWith(BaseTestRunner.class)
@Config(sdk = 23, manifest = Config.NONE)
public class RouterTest extends BaseTest {

    private Router router;

    @Override
    public void setUp() {
        super.setUp();
        router = Mockito.spy(Router.class);
        Mockito.doNothing().when(router).prepareRemoteData(application);
        router.initialize(application);

        ViewMapManager mapManager = new ViewMapManager(application);
        mapManager.getViewMapLocal(application.getFilesDir().getPath());
        router.setMapManager(mapManager);
    }

    @Test
    public void testGetInstanceThenReturnSameObject() {
        Router router = Router.getInstance();
        assertThat(router).isNotNull();
        assertThat(Router.getInstance()).isEqualTo(router);
    }

    @Test
    public void testInitializeWithNullPointThenReturnException() {
        Router router = new Router();
        try {
            router.initialize(null);
            fail("Initialize should failed due to pass null!");
        } catch (NullPointerException e) {
            assertThat(e).isNotNull();
        }
    }

    @Test
    public void testHandle404WithExceptionHandler() {
        ExceptionHandler handler = new ExceptionHandler() {
            @Override
            public void handle(EjuException e) {
                assertThat(e).isNotNull();
                assertThat(e.getCode()).isEqualTo(EjuException.RESOURCE_NOT_FOUND);
            }
        };
        router.register(handler);
        router.route(application, "404", ViewMapInfo.TYPE_NATIVE, null);
        router.unregister(handler);
    }

    @Test
    public void testRegisterExceptionHandler() {
        final AtomicInteger count = new AtomicInteger(0);
        ExceptionHandler handler1 = new ExceptionHandler() {
            @Override
            public void handle(EjuException e) {
                count.incrementAndGet();
            }
        };
        ExceptionHandler handler2 = new ExceptionHandler() {
            @Override
            public void handle(EjuException e) {
                count.incrementAndGet();
            }
        };
        router.register(handler1);
        router.register(handler2);
        router.route(application, "404", ViewMapInfo.TYPE_NATIVE, null);
        assertThat(count.get()).isEqualTo(2);

        router.unregister(handler1);
        router.route(application, "404", ViewMapInfo.TYPE_NATIVE, null);
        assertThat(count.get()).isEqualTo(3);

        router.unregisterAll();
        router.route(application, "404", ViewMapInfo.TYPE_NATIVE, null);
        assertThat(count.get()).isEqualTo(3);
    }

    @Test
    public void testBroadcastExceptionToMultipleHandler() {
        final String errorMessage = "dummy";
        final AtomicInteger count = new AtomicInteger(0);
        ExceptionHandler handler1 = new ExceptionHandler() {
            @Override
            public void handle(EjuException e) {
                assertThat(e).hasMessage(errorMessage);
                count.incrementAndGet();
            }
        };
        ExceptionHandler handler2 = new ExceptionHandler() {
            @Override
            public void handle(EjuException e) {
                assertThat(e).hasMessage(errorMessage);
                count.incrementAndGet();
            }
        };
        router.register(handler1);
        router.register(handler2);
        router.broadcastException(new EjuException(errorMessage));
        assertThat(count.get()).isEqualTo(2);

        router.unregister(handler1);
        router.broadcastException(new EjuException(errorMessage));
        assertThat(count.get()).isEqualTo(3);

        router.unregisterAll();
        router.broadcastException(new EjuException(errorMessage));
        assertThat(count.get()).isEqualTo(3);
    }

    @Test
    public void testDefaultNativeSchema() {
        Option option = new Option();
        Router router = new Router();
        router.initialize(application, option);
        boolean isTrue = router.isNativeRouteSchema("eju://foobar");
        assertThat(isTrue).isTrue();
        assertThat(router.isNativeRouteSchema("http://foobar")).isFalse();
        assertThat(router.isNativeRouteSchema("https://foobar")).isFalse();
    }

    @Test
    public void testCustomNativeSchema() {
        Option option = new Option();
        option.nativeRouteSchema = Arrays.asList("x", "y", "z");
        Router router = new Router();
        router.initialize(application, option);
        boolean isFalse = router.isNativeRouteSchema("eju://foobar");
        assertThat(isFalse).isFalse();
        assertThat(router.isNativeRouteSchema("x://foobar")).isTrue();
        assertThat(router.isNativeRouteSchema("y://foobar")).isTrue();
        assertThat(router.isNativeRouteSchema("z://foobar")).isTrue();
        assertThat(router.isNativeRouteSchema("http://foobar")).isFalse();
        assertThat(router.isNativeRouteSchema("https://foobar")).isFalse();
    }

    @Test
    public void testFindFragmentById() {
        Fragment fragment = router.findFragmentById(application, "fragment", null);
        assertThat(fragment).isNotNull().isInstanceOf(TargetFragment.class);
    }

    @Test
    public void testFindNotExistFragmentThenReturnNull() {
        Fragment fragment = router.findFragmentById(application, "foobar", null);
        assertThat(fragment).isNull();
    }
}

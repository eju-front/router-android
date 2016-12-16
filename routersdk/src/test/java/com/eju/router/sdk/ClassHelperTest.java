package com.eju.router.sdk;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * ClassUtils Test
 *
 * @author tangqianwei
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23, manifest = Config.NONE)
public class ClassHelperTest extends BaseTest {

    @Test
    public void testGenerateTree() {
        ClassUtils.TreeClass tree = ClassUtils.getClassHierarchyTree(ArrayList.class);

        Iterator<ClassUtils.TreeClass> iterator = tree.iterator();
        while(true) {
            if(!(iterator.hasNext())) break;

            System.out.println(iterator.next());
        }
    }
}

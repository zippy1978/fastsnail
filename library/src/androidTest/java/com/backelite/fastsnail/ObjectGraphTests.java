package com.backelite.fastsnail;

import android.test.AndroidTestCase;
import com.backelite.fastsnail.async.*;

import junit.framework.TestCase;

public class ObjectGraphTests extends AndroidTestCase {

    public void testGetComponentSingleton() {

        ObjectGraph.getInstance().loadConfiguration("{\n" +
                "    \"components\": {\n" +
                "        \"asyncManager\": {\n" +
                "            \"className\": \"com.backelite.fastsnail.async.AsyncManager\",\n" +
                "            \"singleton\": true\n" +
                "        }\n" +
                "    }\n" +
                "}", this.getContext());

        Object component = ObjectGraph.getInstance().getComponent("asyncManager");

        assertNotNull(component);

        int hashCode1 = component.hashCode();

        component = ObjectGraph.getInstance().getComponent("asyncManager");

        assertNotNull(component);

        int hashCode2 = component.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    public void testgetComponentPrototype() {

        ObjectGraph.getInstance().loadConfiguration("{\n" +
                "    \"components\": {\n" +
                "        \"asyncManager\": {\n" +
                "            \"className\": \"com.backelite.fastsnail.async.AsyncManager\",\n" +
                "            \"singleton\": false\n" +
                "        }\n" +
                "    }\n" +
                "}", this.getContext());

        Object component = ObjectGraph.getInstance().getComponent("asyncManager");

        assertNotNull(component);

        int hashCode1 = component.hashCode();

        component = ObjectGraph.getInstance().getComponent("asyncManager");

        assertNotNull(component);

        int hashCode2 = component.hashCode();

        assertNotSame(hashCode1, hashCode2);
    }

    public void testAddComponent() {

        ObjectGraph.getInstance().addComponent("asyncManager", AsyncManager.class, true);

        Object component = ObjectGraph.getInstance().getComponent("asyncManager");

        assertNotNull(component);
    }
}
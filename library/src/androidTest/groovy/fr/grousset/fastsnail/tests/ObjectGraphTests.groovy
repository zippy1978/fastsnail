package fr.grousset.fastsnail.tests

import android.test.AndroidTestCase
import fr.grousset.fastsnail.ObjectGraph
import fr.grousset.fastsnail.async.AsyncManager
import fr.grousset.fastsnail.tests.fixtures.ContextAwareFixture

public class ObjectGraphTests extends AndroidTestCase {

    public void testGetComponentSingleton() {

        ObjectGraph.instance.loadConfiguration("""
        {
            "components": {
                "asyncManager": {
                    "className": "fr.grousset.fastsnail.async.AsyncManager",
                    "singleton": true
                }
            }
        }
        """, this.context)


        Object component = ObjectGraph.instance.getComponent("asyncManager")

        assert component != null

        int hashCode1 = component.hashCode()

        component = ObjectGraph.instance.getComponent("asyncManager")

        assert component != null

        int hashCode2 = component.hashCode()

        assert hashCode1 == hashCode2
    }

    public void testGetComponentPrototype() {

        ObjectGraph.instance.loadConfiguration("""
        {
            "components": {
                "asyncManager": {
                    "className": "fr.grousset.fastsnail.async.AsyncManager",
                    "singleton": false
                }
            }
        }
        """, this.context)


        Object component = ObjectGraph.instance.getComponent("asyncManager")

        assert component != null

        int hashCode1 = component.hashCode()

        component = ObjectGraph.instance.getComponent("asyncManager")

        assert component != null

        int hashCode2 = component.hashCode()

        assert hashCode1 != hashCode2
    }

    public void testGetComponentContextAware() {

        ObjectGraph.instance.loadConfiguration("""
        {
            "components": {
                "contextAwareFixture": {
                    "className": "fr.grousset.fastsnail.tests.fixtures.ContextAwareFixture",
                    "singleton": true
                }
            }
        }
        """, this.context)

        ContextAwareFixture component = ObjectGraph.instance.getComponent("contextAwareFixture") as ContextAwareFixture

        assert component != null
        assert component.context == this.context
    }

    public void testAddComponent() {

        ObjectGraph.instance.addComponent("asyncManager", AsyncManager.class, true)

        Object component = ObjectGraph.instance.getComponent("asyncManager")

        assert component != null
    }
}
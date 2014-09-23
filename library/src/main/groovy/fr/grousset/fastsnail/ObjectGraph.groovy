package fr.grousset.fastsnail

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import groovy.transform.CompileStatic

/**
 * Singleton object managing the object graph.
 * @author gi.grousset@gmail.com
 */
@Singleton
@CompileStatic
public class ObjectGraph {

    private static final String TAG = 'ObjectGraph'

    private Context context
    private Map config = [components: [:]]
    private Map singletons = [:]

    /**
     * Add component to the object graph
     * @param name Name
     * @param clazz Class
     * @param singleton Is it a singleton ?
     */
    public void addComponent(String name, Class clazz, boolean singleton) {

        (config['components'] as Map).put(name, ['className': clazz.name, 'singleton': singleton])
    }

    /**
     * Return a component given it's name.
     * @param name Name of the component (as define in the configuration block)
     * @return Component, or null if the component dies not exist
     */
    public Object getComponent(String name) {

        def instance = null

        if (!config) {
            Log.e(TAG, 'Configuration not loaded !')
        } else {
            if (config['components']) {
                def info = config['components'][name]

                if (info) {
                    instance = this.getComponentInstance(name, info)
                }

            } else {
                Log.e(TAG, 'Unable to find components section in the configuration file')
            }
        }

        return instance
    }

    /**
     * In charge of providing a component instance according to the configuration settings.
     * @param name Name of the component
     * @param info Info map for the component
     * @return
     */
    private Object getComponentInstance(String name, def info) {

        // Singleton ?
        if (info['singleton']) {
            def singletonInstance = singletons[name]
            if (!singletonInstance) {
                synchronized (this) {
                    singletonInstance = Class.forName(info['className'] as String)?.newInstance()
                    singletons.put(name, singletonInstance)
                }
            }

            this.injectContext(singletonInstance)

            return singletonInstance

        } else {
            // Prototype
            def prototype =  Class.forName(info['className'] as String)?.newInstance()

            this.injectContext(prototype)

            return prototype
        }

    }

    /**
     * Inject application context into a component if it implements interface ContextAware
     * @param component Component
     */
    private void injectContext(Object component) {

        if (component instanceof ContextAware) {
            ContextAware contextAware = component as ContextAware
            contextAware.context = context
        }
    }

    /**
     * Load graph configuration from raw resource.
     * @param configResId Resource id of the configuration script
     * @param context Context
     */
    public void loadConfiguration(int configResId, Context context) {

        loadConfiguration(context.resources.openRawResource(configResId).text, context)
    }

    /**
     * Load graph configuration from JSON String.
     * @param config JSON String
     * @param context Context
     */
    public void loadConfiguration(String config, Context context) {

        this.context = context

        Gson gson = new Gson()
        this.config = gson.fromJson(config, HashMap.class)
    }

    /**
     * Shortcut to access getComponent method from a static context.
     * @param name Name of the component (as define in the configuration block)
     * @return Component, or null if the component dies not exist
     */
    public static Object injectComponent(String name) {
        return ObjectGraph.instance.getComponent(name)
    }

    /**
     * Shortcut to access loadConfiguration method from a static context.
     * @param configResId Resource id of the configuration script
     * @param context Context
     */
    public static void load(int configResId, Context context) {
        ObjectGraph.instance.loadConfiguration(configResId, context)
    }
}
package fr.grousset.fastsnail

import android.content.Context

/**
 * Interface to implement in order to inject application context into a component.
 */
public interface ContextAware {

    public void setContext(Context context)

}
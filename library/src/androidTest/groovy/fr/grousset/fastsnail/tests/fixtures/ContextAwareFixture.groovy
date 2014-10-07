package fr.grousset.fastsnail.tests.fixtures

import android.content.Context
import fr.grousset.fastsnail.ContextAware

public class ContextAwareFixture implements ContextAware {

    Context context

    @Override
    void setContext(Context context) {
        this.context = context
    }
}
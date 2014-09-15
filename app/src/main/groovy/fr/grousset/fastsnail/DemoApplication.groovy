package fr.grousset.fastsnail.demo

import android.app.Application
import fr.grousset.fastsnail.ObjectGraph

public class DemoApplication extends Application {

    @Override
    public void onCreate() {

        // Load graph
        ObjectGraph.load(R.raw.config, this)

    }

}
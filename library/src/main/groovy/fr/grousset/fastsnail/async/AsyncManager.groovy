package fr.grousset.fastsnail.async

import groovy.transform.CompileStatic
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func0
import rx.schedulers.Schedulers
import rx.util.async.Async

@CompileStatic
/**
 * Manager used to handle asynchronous operations.
 * @author gi.grousset@gmail.com
 */
public class AsyncManager {

    /**
     * Run async closure.
     * Compatible with UI events, as observation is performed on main thread.
     * @param closure Closure to run
     * @return Observable
     */
    public Observable runAsync(Closure closure) {

        return Async.start(new Func0() {
            @Override
            Object call() {
                return closure.call()
            }
        }, Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    }

}
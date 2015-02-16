package it.liqid.instamaterial;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by niccolo on 11/01/15.
 */
public class InstaMaterialApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}

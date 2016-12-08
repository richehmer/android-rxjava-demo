package com.ehmer.usa;

import android.app.Application;
import android.content.Context;


public class UsaApplication extends Application {

    private ApplicationComponent component;


    public static UsaApplication get(Context ctx) {
        //throws RuntimeException if this class isn't named in AndroidManifest.xml
        return (UsaApplication) ctx.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildComponentAndInject();

    }

    public void buildComponentAndInject() {
        component = ApplicationComponent.Initializer.init(this);
        component.inject(this);
    }

    public ApplicationComponent component() {
        return component;
    }


}

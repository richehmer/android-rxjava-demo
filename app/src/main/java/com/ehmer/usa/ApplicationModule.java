package com.ehmer.usa;

import android.app.Application;

import com.ehmer.usa.constitution.ConstitutionService;
import com.ehmer.usa.constitution.ConstitutionServiceImpl;
import com.ehmer.usa.login.RatificationContract;
import com.ehmer.usa.login.RatificationPresenter;
import com.ehmer.usa.messaging.ConstitutionalMessageService;
import com.ehmer.usa.messaging.MessageServiceImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
/**
 * To get the most out of compile-time validation, create a module that includes all of your
 * application's modules. The annotation processor will detect problems across the modules and
 * report them.
 * http://square.github.io/dagger/
 */
public class ApplicationModule {
    private final UsaApplication app;

    public ApplicationModule(UsaApplication app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return app;
    }

    @Provides
    RatificationContract.UserActionListener provideRatificationPresenter(RatificationPresenter presenter) {
        return presenter;
    }

    @Provides
    @Singleton
    ConstitutionService getConstitutionProvider(ConstitutionServiceImpl provider) {
        return provider;
    }

    @Provides
    @Singleton
    Gson provideGsonInstance() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    @Provides
    @Singleton
    ConstitutionalMessageService provideMEssageService(MessageServiceImpl impl) {
        return impl;
    }
}

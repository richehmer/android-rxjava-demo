package com.ehmer.usa;

import com.ehmer.usa.persistance.PersistenceModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        PersistenceModule.class})
public interface ApplicationComponent extends ApplicationComponentGraph {

    /**
     * An initializer that creates the graph from an application.
     */
    final class Initializer {
        private Initializer() {
        }

        static ApplicationComponent init(UsaApplication app) {
            return DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(app))
                    .build();
        }
    }


}
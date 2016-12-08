package com.ehmer.usa.persistance;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.ehmer.usa.constitution.UsConstitution;
import com.ehmer.usa.persistance.prefs.StringPreference;
import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module()
public final class PersistenceModule {

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application app) {
        return app.getSharedPreferences("use.prefs", Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    @ConstitutionPref
    StringPreference provideConstitutionPref(SharedPreferences preferences, Gson gson) {
        String defaultValue = gson.toJson(new UsConstitution(false));
        return new StringPreference(preferences, "constitution", defaultValue);
    }

}

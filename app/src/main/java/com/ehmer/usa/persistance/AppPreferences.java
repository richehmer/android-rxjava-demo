package com.ehmer.usa.persistance;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Preferences for app that should be persisted via Android backup mechanism.
 */
@Qualifier
@Retention(RUNTIME)
public @interface AppPreferences {
}

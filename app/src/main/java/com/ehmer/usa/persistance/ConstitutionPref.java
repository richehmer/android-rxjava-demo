package com.ehmer.usa.persistance;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Preferences to store definition of Constitution
 */
@Qualifier
@Retention(RUNTIME)
public @interface ConstitutionPref {
}

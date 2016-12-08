package com.ehmer.usa.constitution;

import rx.Observable;

public interface ConstitutionService {

    /**
     * Get {@link Observable} that emits at least one {@link Constitution} and any changes
     * without completing.
     */
    Observable<UsConstitution> getConstitution();

    /**
     * Signs the constitution, result will be emitted at {@link #getConstitution()}. Please
     * call from background thred.
     *
     * @return {@link Observable} that emits {@code true} or {@code false} depending on success
     * and then completes.
     * @param constitution
     */
    Observable<Boolean> signConstitution(UsConstitution constitution);

    void deleteConstitution();
}

package com.ehmer.usa.constitution;

import android.support.annotation.NonNull;

import com.ehmer.usa.persistance.ConstitutionPref;
import com.ehmer.usa.persistance.prefs.StringPreference;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;

import static com.ehmer.usa.util.LogUtils.LOGD;
import static com.ehmer.usa.util.LogUtils.makeLogTag;

public class ConstitutionServiceImpl implements ConstitutionService {

    private static final String TAG = makeLogTag(ConstitutionServiceImpl.class);
    private final BehaviorSubject<UsConstitution> constitutionSubject;

    @Inject
    ConstitutionServiceImpl(final Gson gson,
                            @ConstitutionPref final StringPreference cachedConstitution) {

        // initialize constitution from a cached value
        final String json = cachedConstitution.get();
        LOGD(TAG, json);
        UsConstitution cached = gson.fromJson(json, UsConstitution.class);
        constitutionSubject = BehaviorSubject.create(cached);
        constitutionSubject.skip(1)
                .subscribe(new Action1<Constitution>() {
                    @Override
                    public void call(Constitution constitution) {
                        //save all subsequent updates to the constitution
                        final String newConstitution = gson.toJson(constitution);
                        cachedConstitution.set(newConstitution);
                    }
                });

    }

    @Override
    public Observable<UsConstitution> getConstitution() {
        return constitutionSubject.asObservable();
    }

    @Override
    public Observable<Boolean> signConstitution(@NonNull final UsConstitution constitution) {
        if (constitution.isRatified()) {
            return Observable.just(true)
                    .delay(500, TimeUnit.MILLISECONDS)
                    .doOnNext(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            constitutionSubject.onNext(constitution);
                        }
                    });
        } else {
            return Observable.just(false)
                    .delay(500, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void deleteConstitution() {
        constitutionSubject.onNext(new UsConstitution(false));
    }
}

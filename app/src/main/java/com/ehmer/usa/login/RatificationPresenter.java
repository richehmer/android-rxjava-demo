package com.ehmer.usa.login;

import com.ehmer.usa.constitution.Constitution;
import com.ehmer.usa.constitution.ConstitutionService;
import com.ehmer.usa.constitution.UsConstitution;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class RatificationPresenter implements RatificationContract.UserActionListener {

    private final ConstitutionService constitutionService;
    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private Subscription ratificationSubscription;
    private final RatificationContract.View view;
    private final Scheduler viewScheduler;

    RatificationPresenter(ConstitutionService constitutionService, RatificationContract.View view, Scheduler viewScheduler) {
        this.constitutionService = constitutionService;
        this.view = view;
        this.viewScheduler = viewScheduler;
    }

    @Override
    public void create() {


        // listen for a ratified constitution
        subscriptions.add(constitutionService.getConstitution()
                .filter(new Func1<Constitution, Boolean>() {
                    @Override
                    public Boolean call(Constitution constitution) {
                        return constitution.isRatified();
                    }
                })
                .subscribe(new Action1<Constitution>() {
                    @Override
                    public void call(Constitution ratifiedConstitution) {
                        view.showRatificationSuccessScreen();
                    }
                }));


        subscriptions.add(view.ratificationCount()
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer count) {
                        view.setHintVisibility(count == 0);
                    }
                })
                .map(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer count) {
                        return count >= 9;
                    }
                })
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean canSignConstitution) {
                        view.setSignatureButtonEnabled(canSignConstitution);
                    }
                }));

    }

    @Override
    public void destroy() {
        subscriptions.clear();
        clearRatificationSubscription();

    }

    private void clearRatificationSubscription() {
        if (ratificationSubscription != null && !ratificationSubscription.isUnsubscribed()) {
            ratificationSubscription.unsubscribe();
        }
    }

    @Override
    public void ratify(UsConstitution constitution) {

        // typical structure of 'login' request
        clearRatificationSubscription();
        view.setSignatureInProgress(true);
        ratificationSubscription = constitutionService.signConstitution(constitution)
                .filter(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean success) {
                        return !success;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(viewScheduler)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean ratified) {
                        // only called if the signature failed
                        view.showRatificationFailedMessage();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //error during signature
                        view.setSignatureInProgress(false);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        //completion of stream without error
                        view.setSignatureInProgress(false);
                    }
                });

    }
}

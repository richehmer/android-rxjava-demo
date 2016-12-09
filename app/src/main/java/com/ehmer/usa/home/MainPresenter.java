package com.ehmer.usa.home;

import com.ehmer.usa.constitution.Constitution;
import com.ehmer.usa.constitution.ConstitutionService;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Ehmer, R.G. on 12/7/16.
 */

public class MainPresenter implements MainContract.UserActionListener {

    private final MainContract.View view;
    private final ConstitutionService constitutionService;
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    public MainPresenter(MainContract.View view, ConstitutionService constitutionService) {
        this.view = view;
        this.constitutionService = constitutionService;
    }

    @Override
    public void create() {

        // show ratification screen if unratified
        subscriptions.add(constitutionService.getConstitution()
                .subscribe(new Action1<Constitution>() {
                    @Override
                    public void call(Constitution constitution) {
                        if (!constitution.isRatified()) {
                            view.displayRatificationActivity();
                        }
                    }
                }));


    }

    @Override
    public void destroy() {
        subscriptions.clear();
    }

    @Override
    public void requestJudicialBranch() {
        view.showJudicial();
    }

    @Override
    public void requestExecutiveBranch() {
        view.showExecutive();
    }

    @Override
    public void requestLegislativeBranch() {
        view.showLegislative();
    }

    @Override
    public void requestMediaBranch() {
        view.showMedia();
    }

    @Override
    public void unratifyConstitution() {
        constitutionService.deleteConstitution();
    }

}

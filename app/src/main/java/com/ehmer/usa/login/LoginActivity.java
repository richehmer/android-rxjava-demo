package com.ehmer.usa.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.ehmer.usa.R;
import com.ehmer.usa.RxUsaApplication;
import com.ehmer.usa.constitution.ConstitutionService;
import com.ehmer.usa.constitution.UsConstitution;
import com.ehmer.usa.databinding.ActivityLoginBinding;
import com.jakewharton.rxbinding.widget.RxCompoundButton;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func6;
import rx.functions.Func7;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements RatificationContract.View {


    // UI references.
    private RatificationContract.UserActionListener mPresenter;

    @Inject
    ConstitutionService constitutionService;

    ActivityLoginBinding bind;
    private Snackbar ratificationHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxUsaApplication.get(this).component().inject(this);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_login);

        mPresenter = new RatificationPresenter(constitutionService, this,
                AndroidSchedulers.mainThread());

        bind.signRatification.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.ratify(new UsConstitution(true));
            }
        });

        mPresenter.create();
    }

    @Override
    public Observable<Integer> ratificationCount() {

        //the combine latest operator accepts a maxumum of 16 observables, so we must split
        //it into 2 observables
        final Observable<Integer> observableGroup1 = Observable.combineLatest(
                RxCompoundButton.checkedChanges(bind.ratifyDelaware).compose(boolToInt),
                RxCompoundButton.checkedChanges(bind.ratifyPennsylvania).compose(boolToInt),
                RxCompoundButton.checkedChanges(bind.ratifyConnecticut).compose(boolToInt),
                RxCompoundButton.checkedChanges(bind.ratifyGeorgia).compose(boolToInt),
                RxCompoundButton.checkedChanges(bind.ratifyMaryland).compose(boolToInt),
                RxCompoundButton.checkedChanges(bind.ratifyMassachusetts).compose(boolToInt),
                RxCompoundButton.checkedChanges(bind.ratifyNewHampshire).compose(boolToInt),
                new Func7<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer int1, Integer int2, Integer int3, Integer int4, Integer int5, Integer int6, Integer int7) {
                        return int1 + int2 + int3 + int4 + int5 + int6 + int7;
                    }
                });

        final Observable<Integer> observableGroup2 = Observable.combineLatest(
                RxCompoundButton.checkedChanges(bind.ratifyNewJersey).compose(boolToInt),
                RxCompoundButton.checkedChanges(bind.ratifyNewYork).compose(boolToInt),
                RxCompoundButton.checkedChanges(bind.ratifyNorthCarolina).compose(boolToInt),
                RxCompoundButton.checkedChanges(bind.ratifyRhodeIsland).compose(boolToInt),
                RxCompoundButton.checkedChanges(bind.ratifySouthCarolina).compose(boolToInt),
                RxCompoundButton.checkedChanges(bind.ratifyVirginia).compose(boolToInt),
                new Func6<Integer, Integer, Integer, Integer, Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer int1, Integer int2, Integer int3, Integer int4, Integer int5, Integer int6) {
                        return int1 + int2 + int3 + int4 + int5 + int6;
                    }
                });

        return Observable.combineLatest(observableGroup1, observableGroup2, new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) {
                return integer + integer2;
            }
        });

    }

    private final Observable.Transformer<Boolean, Integer> boolToInt = new Observable.Transformer<Boolean, Integer>() {
        @Override
        public Observable<Integer> call(Observable<Boolean> booleanObservable) {
            return booleanObservable.distinctUntilChanged()
                    .map(new Func1<Boolean, Integer>() {
                        @Override
                        public Integer call(Boolean aBoolean) {
                            return aBoolean ? 1 : 0;
                        }
                    });
        }
    };


    @Override
    public void setHintVisibility(boolean visible) {
        if (visible && (ratificationHint == null || !ratificationHint.isShown())) {
            ratificationHint = Snackbar.make(bind.loginForm, getString(R.string.ratification_hint), Snackbar.LENGTH_INDEFINITE);
            ratificationHint.show();
        } else if (!visible && (ratificationHint != null && ratificationHint.isShown())) {
            ratificationHint.dismiss();
        }
    }

    @Override
    public void setSignatureButtonEnabled(boolean enabled) {
        bind.signRatification.setEnabled(enabled);
    }

    @Override
    public void setSignatureInProgress(final boolean show) {
// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        final ScrollView loginForm = bind.loginForm;
        loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        loginForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        final ProgressBar loginProgress = bind.loginProgress;
        loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        loginProgress.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void showRatificationSuccessScreen() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void showRatificationFailedMessage() {
        Toast.makeText(this, "Ratification Failed!", Toast.LENGTH_LONG).show();
    }
}


package com.ehmer.usa.login;

import com.ehmer.usa.constitution.Constitution;
import com.ehmer.usa.constitution.UsConstitution;

import rx.Observable;

public interface RatificationContract {

    interface View {

        /**
         * Get an {@link Observable} that emits count of colonies that have ratified the
         * constitution.
         */
        Observable<Integer> ratificationCount();

        void setHintVisibility(boolean visible);

        void setSignatureButtonEnabled(boolean enabled);

        void setSignatureInProgress(boolean inProgress);

        void showRatificationSuccessScreen();

        void showRatificationFailedMessage();

    }

    interface UserActionListener {

        void create();

        void destroy();

        void ratify(UsConstitution constitution);
    }
}

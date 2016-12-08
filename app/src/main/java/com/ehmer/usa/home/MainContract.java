package com.ehmer.usa.home;

/**
 * the primary screen of the application
 * Created by Ehmer, R.G. on 12/7/16.
 */

public interface MainContract {

    interface View {
        void showLegislative();

        void showJudicial();

        void showExecutive();

        void showMedia();

        void displayRatificationActivity();

    }

    interface UserActionListener {

        void create();

        void destroy();

        void requestJudicialBranch();

        void requestExecutiveBranch();

        void requestLegislativeBranch();

        void requestMediaBranch();

        void unratifyConstitution();

    }
}

package com.ehmer.usa;

import com.ehmer.usa.branches.JudicialFragment;
import com.ehmer.usa.home.MainActivity;
import com.ehmer.usa.branches.ExecutiveFragment;
import com.ehmer.usa.branches.LegislativeFragment;
import com.ehmer.usa.login.LoginActivity;

/**
 * This interface is edited every time you want to perform dependency injection in a new class.
 */
public interface ApplicationComponentGraph {

    void inject(UsaApplication app);

    void inject(LoginActivity activity);

    void inject(MainActivity activity);

    void inject(LegislativeFragment fragment);

    void inject(ExecutiveFragment fragment);

    void inject(JudicialFragment fragment);
}

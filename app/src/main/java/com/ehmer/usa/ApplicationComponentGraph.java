package com.ehmer.usa;

import com.ehmer.usa.home.MainActivity;
import com.ehmer.usa.branches.ExecutiveFragment;
import com.ehmer.usa.branches.LegislativeFragment;
import com.ehmer.usa.login.LoginActivity;

public interface ApplicationComponentGraph {

    void inject(UsaApplication app);

    void inject(LoginActivity activity);

    void inject(MainActivity activity);

    void inject(LegislativeFragment fragment);

    void inject(ExecutiveFragment fragment);
}

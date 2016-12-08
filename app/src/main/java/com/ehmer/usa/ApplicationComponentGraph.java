package com.ehmer.usa;

import com.ehmer.usa.home.MainActivity;
import com.ehmer.usa.legislative.LegislativeFragment;
import com.ehmer.usa.login.LoginActivity;

public interface ApplicationComponentGraph {

    void inject(UsaApplication app);

    void inject(LoginActivity activity);

    void inject(MainActivity activity);

    void inject(LegislativeFragment fragment);
}

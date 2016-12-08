package com.ehmer.usa;

import com.ehmer.usa.home.MainActivity;
import com.ehmer.usa.login.LoginActivity;

public interface ApplicationComponentGraph {

    void inject(RxUsaApplication app);

    void inject(LoginActivity activity);

    void inject(MainActivity activity);
}

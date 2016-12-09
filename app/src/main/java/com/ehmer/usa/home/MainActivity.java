package com.ehmer.usa.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ehmer.usa.R;
import com.ehmer.usa.UsaApplication;
import com.ehmer.usa.constitution.ConstitutionService;
import com.ehmer.usa.branches.ExecutiveFragment;
import com.ehmer.usa.branches.LegislativeFragment;
import com.ehmer.usa.login.LoginActivity;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainContract.View {

    private static final int RC_RATIFY_CONSTITUTION = 1;

    MainContract.UserActionListener mPresenter;

    @Inject
    ConstitutionService constitutionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UsaApplication.get(this).component().inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_legistative:
                                mPresenter.requestLegislativeBranch();
                                break;
                            case R.id.action_judicial:
                                mPresenter.requestJudicialBranch();
                                break;
                            case R.id.action_executive:
                                mPresenter.requestExecutiveBranch();
                                break;
//                            case R.id.action_media:
//                                mPresenter.requestMediaBranch();
//                                break;
                        }
                        return true;
                    }
                });

        mPresenter = new MainPresenter(this, constitutionService);
        mPresenter.create();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mPresenter.requestLegislativeBranch();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            // Handle the unratify action
            mPresenter.unratifyConstitution();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void showLegislative() {
        setDisplayedFragment(new LegislativeFragment());
    }

    private void setDisplayedFragment(Fragment fragment) {
        final FragmentManager fm = getSupportFragmentManager();
        // pending transactions exist if activity started from a notification
        fm.executePendingTransactions();

        final String tag = "current.fragment";
        Fragment current = fm.findFragmentByTag(tag);
        if (current != null) {
            fm.beginTransaction()
                    .remove(current)
                    .add(R.id.content_main, fragment, tag)
                    .commit();
        } else {
            fm.beginTransaction()
                    .add(R.id.content_main, fragment, tag)
                    .commit();
        }
    }

    @Override
    public void showJudicial() {

    }

    @Override
    public void showExecutive() {
        setDisplayedFragment(new ExecutiveFragment());
    }

    @Override
    public void showMedia() {

    }

    @Override
    public void displayRatificationActivity() {
        // problem to solve: what if the ratification activity is already being displayed?
        // answers:
        // 1. keep track of whether the activity has been started or not in an instance variable
        //      that is persisted across configuration changes
        // 2. Make the
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(i, RC_RATIFY_CONSTITUTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_RATIFY_CONSTITUTION && resultCode != Activity.RESULT_OK) {
            finish();
        }
    }
}

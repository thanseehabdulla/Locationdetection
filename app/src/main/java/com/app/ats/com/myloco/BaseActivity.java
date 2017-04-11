package com.app.ats.com.myloco;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;




public abstract class BaseActivity extends AppCompatActivity {

    //permission initialization for maashmallow
    public static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final String[] CAMERA_PERMS = {
            Manifest.permission.CAMERA
    };
    public static final String[] CONTACTS_PERMS = {
            Manifest.permission.READ_CONTACTS
    };
    public static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    public static final String[] WRITE_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final int INITIAL_REQUEST = 1337;
    public static final int CAMERA_REQUEST = INITIAL_REQUEST + 1;
    public static final int CONTACTS_REQUEST = INITIAL_REQUEST + 2;
    public static final int LOCATION_REQUEST = INITIAL_REQUEST + 3;
    public static final int WRITE_REQUEST = INITIAL_REQUEST + 4;

    //intent filter declares what an activity or service can do and what types of broadcasts a receiver can handle and sync adapter sync until its finished


    public View layoutView;
    public View mProgressView;
    public BaseFragment baseFragment;
    ProgressDialog progress;


    private BroadcastReceiver syncBroadcastReceiver = new BroadcastReceiver() {

        @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onReceive(Context context, Intent intent) {
            intent.describeContents();
            Boolean refreshCheck = intent.getBooleanExtra("REFRESH_TOKEN_VALID", true);
            if (!refreshCheck) {
                showPasswordDialog();
            }
            if (progress != null)
                progress.dismiss();

        }

    };


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void showPasswordDialog() {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();




    }






    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void callApiFragment() {
        FragmentManager manager = getFragmentManager();
        baseFragment = (BaseFragment) manager.findFragmentByTag("api");
        if (baseFragment == null) {
            FragmentTransaction transaction = manager.beginTransaction();
            baseFragment = new BaseFragment();
            transaction.add(baseFragment, "api");
            transaction.commit();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callApiFragment();


        if (Build.VERSION.SDK_INT >= 21) {
            setupWindowAnimations(buildExplodeTransition(true), buildExplodeTransition(false));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !canAccessLocation()) {
            this.requestPermissions(BaseActivity.INITIAL_PERMS, BaseActivity.INITIAL_REQUEST);
        }
    }

    private boolean canAccessLocation() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    protected boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //return(PackageManager.PERMISSION_GRANTED==this.checkSelfPermission(perm));
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || (PackageManager.PERMISSION_GRANTED == this.checkSelfPermission(perm));
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();

    }
    public void onProgressStart() {

    }


    public void onProgressFinish() {
        progress.dismiss();
    }


    public void showProgressDialog(String title, String message) {
        progress = new ProgressDialog(this);
        progress.setMessage(message);
        if (null != null)
            progress.setTitle(null);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();
    }

    public void dismissProgressDialog() {
        if (progress != null)
            progress.dismiss();

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2 && mProgressView != null && layoutView != null) {
            int shortAnimTime = 10;

            layoutView.setVisibility(show ? View.GONE : View.VISIBLE);
            layoutView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    layoutView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            assert mProgressView != null;
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            layoutView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    public void showAlertDialog(String title, String message) {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertBuilder.show();
    }






    /**
     * Setup window emter exit transitions
     */

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void setupWindowAnimations(Transition enter, Transition exit) {

        getWindow().setEnterTransition(enter);
        getWindow().setReturnTransition(exit);
;

    }

    /**
     * Slide transition effect
     *
     *
     */

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    Transition buildSlideTransition() {
        Transition transition = new Slide();

        return transition;
    }

    /**
     * Explode Transition effect
     *
     *
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    Transition buildExplodeTransition(Boolean delay) {
        Transition transition = new Explode();

        return transition;
    }

    /**
     * Explode Transition effect
     *
     *
     *
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    Transition buildFadeTransition(boolean delay) {
        Transition transition = new Fade();

        return transition;
    }





    public void processCameraRequest() {

    }

    public abstract void processWriteRequest();
}















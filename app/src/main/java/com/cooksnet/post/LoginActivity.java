package com.cooksnet.post;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

import com.cooksnet.obj.Profile;
import com.cooksnet.util.CooksNetWebAccess;

public class LoginActivity extends BaseActivity implements LoginEventListener {

    public final static String DEMO_NICKNAME = "demo_user-p";
    public final static String DEMO_PASSWORD = "YHuffZSdU8";

    private ProgressDialogFragment dialog;
    private Handler handler;

    private boolean isDemo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.login);

        handler = new Handler();

        class MyFilter implements InputFilter {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.toString().matches("^[a-zA-Z0-9 -/:-@\\[-\\`\\{-\\~]+$")) {
                    return source;
                } else {
                    return "";
                }
            }
        }
        InputFilter[] filters = {new MyFilter()};
        EditText nickname = ((EditText) findViewById(R.id.nickname));
        nickname.setFilters(filters);

        ((Button) findViewById(R.id.login)).setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                final String nickname = ((EditText) findViewById(R.id.nickname)).getText().toString();
                final String password = ((EditText) findViewById(R.id.password)).getText().toString();
                if ("".equals(nickname.trim()) || "".equals(password.trim())) {
//                    new AlertDialog.Builder(LoginActivity.this).setTitle(getText(R.string.dialog_login_failed_title))
//                            .setMessage(getText(R.string.dialog_login_failed_message))
//                            .setPositiveButton(R.string.dialog_ok, null).create().show();
                    AlertDialogFragment.newInstance(getString(R.string.dialog_login_failed_title), getString(R.string.dialog_login_failed_message), "")
                            .show(getSupportFragmentManager(), "NoneTitle");
                    return;
                }

//				dialog = ProgressDialog
//						.show(LoginActivity.this, "", getText(R.string.dialog_progress).toString(), true);
                dialog = ProgressDialogFragment.newInstance("", getString(R.string.dialog_progress));
                dialog.show(getSupportFragmentManager(), "CreateThread");

                new LoginThread(LoginActivity.this, nickname, password).execute("");
            }

        });

        ((Button) findViewById(R.id.signup)).setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Uri uri = Uri.parse(CooksNetWebAccess.URL_SIGN_UP);
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            }

        });

        SharedPreferences pref = getSharedPreferences(BaseActivity.SHARED_NAME, MODE_PRIVATE);
        ((EditText) findViewById(R.id.nickname)).setText(pref.getString("nickname", ""));
        ((EditText) findViewById(R.id.password)).setText(pref.getString("password", ""));
    }

    public void doAdditionalTask() {
        // ExtrasData.contacts = new
        // AndroidDBAccess(getContentResolver()).loadContactsOrderName();
    }

    public void loginSuccessed(final LoginEvent le) {
        SharedPreferences pref = getSharedPreferences(BaseActivity.SHARED_NAME, MODE_PRIVATE);
        Editor e = pref.edit();
        e.putString("nickname", le.getNickname());
        e.putString("password", le.getPassword());
        e.commit();

        handler.post(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
//				Toast.makeText(LoginActivity.this, getText(R.string.login_toast_success), Toast.LENGTH_LONG).show();
                SharedPreferences pref = getSharedPreferences(BaseActivity.SHARED_NAME, MODE_PRIVATE);
                Editor e = pref.edit();
                // TODO demo
                if (!isDemo) {
                    e.putBoolean("fromLogin", true);
                }
                e.putString("currentTab", "publish");
                e.commit();
                Intent i = new Intent(getApplication(), MyRecipeActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void loginFailed(final LoginEvent le) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                if (!le.isSuccessNetwork()) {
                    if (null == le.getErrorCode() || "".equals(le.getErrorCode())) {
//                        new AlertDialog.Builder(LoginActivity.this)
//                                .setTitle(getText(R.string.dialog_network_error_title))
//                                .setMessage(getText(R.string.dialog_network_error_message))
//                                .setPositiveButton(R.string.dialog_ok, null).create().show();
                        AlertDialogFragment.newInstance(getString(R.string.dialog_network_error_title), getString(R.string.dialog_network_error_message), "")
                                .show(getSupportFragmentManager(), "NoneTitle");
                    } else {
//                        new AlertDialog.Builder(LoginActivity.this)
//                                .setTitle(getText(R.string.dialog_http_error_title))
//                                .setMessage(
//                                        getText(R.string.dialog_input_error_message_unexpected_err) + "\n"
//                                                + "Error Code " + le.getErrorCode())
//                                .setPositiveButton(R.string.dialog_ok, null).create().show();
                        AlertDialogFragment.newInstance(getString(R.string.dialog_http_error_title), getText(R.string.dialog_input_error_message_unexpected_err) + "\n"
                                + "Error Code " + le.getErrorCode(), "")
                                .show(getSupportFragmentManager(), "NoneTitle");
                    }
                } else {
                    if (Profile.LOGIN_NOT_PROFILED.equals(le.getProfile().loginState)) {
//                        new AlertDialog.Builder(LoginActivity.this)
//                                .setMessage(getText(R.string.dialog_login_not_profiled_message))
//                                .setPositiveButton(R.string.dialog_ok, null).create().show();
                        AlertDialogFragment.newInstance("", getString(R.string.dialog_login_not_profiled_message), "")
                                .show(getSupportFragmentManager(), "NoneTitle");
                    } else {
//                        new AlertDialog.Builder(LoginActivity.this)
//                                .setTitle(getText(R.string.dialog_login_failed_title))
//                                .setMessage(getText(R.string.dialog_login_failed_message))
//                                .setPositiveButton(R.string.dialog_ok, null).create().show();
                        AlertDialogFragment.newInstance(getString(R.string.dialog_login_failed_title), getString(R.string.dialog_login_failed_message), "")
                                .show(getSupportFragmentManager(), "NoneTitle");
                    }
                }
            }
        });
    }

}
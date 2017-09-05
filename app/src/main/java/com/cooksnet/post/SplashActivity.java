package com.cooksnet.post;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.cooksnet.util.ImageLoadThread;

public class SplashActivity extends AppCompatActivity implements LoginEventListener {

    private Handler handler;

    boolean isConnected;

    protected String getExtention() {
        return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);

        handler = new Handler();

        Display display = ((WindowManager) SplashActivity.this.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        ImageView splash = (ImageView) findViewById(R.id.splash);
        BitmapDrawable bd = (BitmapDrawable) splash.getDrawable();
        Bitmap source = bd.getBitmap();
//        int w = display.getWidth();
        Point size = new Point();
        display.getSize(size);
        int w = size.x;
        int h = size.y;
        Bitmap dest = resizeBitmap(source, w, h, w, h, w, h);
        splash.setImageBitmap(dest);

        ImageLoadThread.displayWidth = size.x;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        if (network == null || !network.isAvailable() || !network.isConnectedOrConnecting()) {
//            AlertDialog dialog = new AlertDialog.Builder(SplashActivity.this)
//                    .setTitle(getText(R.string.dialog_network_error_title))
//                    .setMessage(getText(R.string.dialog_network_error_message))
//                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int whichButton) {
//                            SplashActivity.this.finish();
//                        }
//                    }).create();
//            dialog.show();
            AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(getString(R.string.dialog_network_error_title),
                    getString(R.string.dialog_network_error_message), "", "", 1, null, null);
            alertDialogFragment.setListener(new AlertDialogListener() {
                @Override
                public void doPositiveClick(int id) {
                    finish();
                }

                @Override
                public void doNegativeClick(int id) {
                }
            });
            alertDialogFragment.show(getSupportFragmentManager(), "NoneTitle");
        } else {
            isConnected = true;
        }

        SharedPreferences pref = getSharedPreferences(BaseActivity.SHARED_NAME, MODE_PRIVATE);
        boolean save = pref.getBoolean("save", true);
        String nickname = pref.getString("nickname", "");

        // TODO demo
        if (save /* && !LoginActivity.DEMO_NICKNAME.equals(nickname) */) {
            Editor e = pref.edit();
            e.putBoolean("save", true);
            e.commit();
            String password = pref.getString("password", "");

            if (isConnected) {
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (isConnected) {
//							dialog = ProgressDialog.show(SplashActivity.this, "", getText(R.string.dialog_progress).toString(),
//									true);
                        }
                    }
                }, 500);
            }

            new LoginThread(this, nickname, password).execute("");
        } else {
            Editor e = pref.edit();
            e.putString("nickname", "");
            e.putString("password", "");
            e.commit();
            if (isConnected) {
                startActivity(new Intent(getApplication(), LoginActivity.class));
                finish();
            }
        }
    }

    public void doAdditionalTask() {
    }

    public void loginSuccessed(LoginEvent le) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isConnected) {
//					while (null == dialog) {
//					}
//					dialog.dismiss();

                    SharedPreferences pref = getSharedPreferences(BaseActivity.SHARED_NAME, MODE_PRIVATE);
                    Editor e = pref.edit();
                    e.putString("currentTab", "publish");
                    e.commit();

                    startActivity(new Intent(getApplication(), MyRecipeActivity.class));
                    finish();
                }
            }
        });
    }

    public void loginFailed(final LoginEvent le) {
        if (!le.isSuccessNetwork()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isConnected) {
//						while (null == dialog) {
//						}
//						dialog.dismiss();
//                        AlertDialog dialog = null;
                        if (null == le.getErrorCode() || "".equals(le.getErrorCode())) {
//                            dialog = new AlertDialog.Builder(SplashActivity.this)
//                                    .setTitle(getText(R.string.dialog_network_error_title))
//                                    .setMessage(getText(R.string.dialog_network_error_message))
//                                    .setPositiveButton(R.string.dialog_ok, null).create();
                            AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(getString(R.string.dialog_network_error_title),
                                    getString(R.string.dialog_network_error_message), "", "", 1, null, null);
                            alertDialogFragment.setListener(new AlertDialogListener() {
                                @Override
                                public void doPositiveClick(int id) {
                                    finish();
                                }

                                @Override
                                public void doNegativeClick(int id) {
                                }
                            });
                            alertDialogFragment.show(getSupportFragmentManager(), "NoneTitle");
                        } else {
//                            dialog = new AlertDialog.Builder(SplashActivity.this)
//                                    .setTitle(getText(R.string.dialog_http_error_title))
//                                    .setMessage(
//                                            getText(R.string.dialog_input_error_message_unexpected_err) + "\n"
//                                                    + "Error Code " + le.getErrorCode())
//                                    .setPositiveButton(R.string.dialog_ok, null).create();
                            AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(getString(R.string.dialog_http_error_title),
                                    getText(R.string.dialog_input_error_message_unexpected_err) + "\n"
                                            + "Error Code " + le.getErrorCode(), "", "", 1, null, null);
                            alertDialogFragment.setListener(new AlertDialogListener() {
                                @Override
                                public void doPositiveClick(int id) {
                                    finish();
                                }

                                @Override
                                public void doNegativeClick(int id) {
                                }
                            });
                            alertDialogFragment.show(getSupportFragmentManager(), "NoneTitle");
                        }
//                        dialog.setOnDismissListener(new OnDismissListener() {
//                            public void onDismiss(DialogInterface di) {
//                                finish();
//                            }
//                        });
//                        dialog.show();
                    }
                }
            });
            return;
        }
        startActivity(new Intent(getApplication(), LoginActivity.class));
        finish();
    }

    public Bitmap resizeBitmap(Bitmap src, int vw, int vh, int hw, int hh, int dw, int dh) {
        if (vw < dw || vh < dh || hw < dw || hh < dh) {
            throw new IllegalArgumentException("vw < dw || vh < dh || hw < dw || hh < dh");
        }
        int destWidth = 0;
        int destHeight = 0;
        // if (src.getWidth() < src.getHeight()) {
        // destHeight = vh;
        // int hoseiW = (vh * src.getWidth()) / src.getHeight();
        // if (hoseiW >= vw) {
        // vw = hoseiW;
        // }
        // destWidth = vw;
        // } else if (src.getWidth() > src.getHeight()) {
        destWidth = hw;
        int hoseiH = (hw * src.getHeight()) / src.getWidth();
        if (hoseiH >= hh) {
            hh = hoseiH;
        }
        destHeight = hh;
        // } else {
        // int mw = vw > hw ? vw : hw;
        // int mh = vh > hh ? vh : hh;
        // destWidth = mw;
        // destHeight = mh;
        // }
        Matrix matrix = new Matrix();
        float widthScale = ((float) destWidth) / ((float) src.getWidth());
        float heightScale = ((float) destHeight) / ((float) src.getHeight());
        // Log.d("scale", "w" + widthScale + "h" + heightScale);
        matrix.postScale(widthScale, heightScale);
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        // Log.d("src", "w" + src.getWidth() + "h" + src.getHeight());
        if (dw == dst.getWidth() && dh == dst.getHeight() || dw == 0 || dh == 0) {
            // Log.d("dst", "w" + dst.getWidth() + "h" + dst.getHeight());
            return dst;
        }
        if (dst.getWidth() != dst.getHeight()) {
            int cutW = (dst.getWidth() - dw) / 2;
            int cutH = (dst.getHeight() - dh) / 2;
            // Log.d("cut", "w" + cutW + "h" + cutH);
            dst = Bitmap.createBitmap(dst, cutW, cutH, dst.getWidth() - (cutW * 2), dst.getHeight() - (cutH * 2));
            // Log.d("cutdst", "w" + dst.getWidth() + "h" + dst.getHeight());
        }
        if (dst.getWidth() != dw || dst.getHeight() != dh) {
            dst = Bitmap.createBitmap(dst, 0, 0, dw, dh);
        }
        // Log.d("dst", "w" + dst.getWidth() + "h" + dst.getHeight());
        return dst;
    }

}

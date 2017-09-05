package com.cooksnet.post;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by takana on 2016/04/26
 */
public class ProgressDialogFragment extends DialogFragment {
    private static final String TITLE = "title";
    private static final String MESSAGE = "message";

    private static ProgressDialog progressDialog = null;

    public static ProgressDialogFragment newInstance(String title, String message) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();

        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(MESSAGE, message);
        fragment.setArguments(args);
        fragment.setCancelable(false);

        return fragment;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (progressDialog == null) {
            String title = getArguments().getString(TITLE);
            String message = getArguments().getString(MESSAGE);

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        return progressDialog;
    }



    @Override
    public Dialog getDialog() {
        return progressDialog;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        progressDialog = null;
    }
}

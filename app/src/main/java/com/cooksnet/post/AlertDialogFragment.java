package com.cooksnet.post;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

/**
 * Created by takana on 2016/04/27
 * インスタンスは AlertDialogFragment.newInstance() で生成
 */
public class AlertDialogFragment extends DialogFragment {
    private static final String TITLE = "title";
    private static final String MESSAGE = "message";
    private static final String POSITIVE_BTN = "positiveBtn";
    private static final String NEGATIVE_BTN = "negativeBtn";
    private static final String TYPE = "type";
    private static final String ID = "id";

    public static final int BTN_ONE = 1;
    public static final int BTN_TWO = 2;

    private AlertDialogListener listener;

    /**
     * staticファクトリメソッド
     * @param positiveBtn positiveButton のテキストを指定　""(空文字)の場合は "OK"
     * @param negativeBtn negativeButton のテキストを指定　""(空文字)の場合は "Cancel"
     * @param type 表示する Button の数 1:positiveButton のみ 2:positive & negativeButton
     * @param target 呼び出し元が Fragment の場合は呼び出し元の Fragment を設定、その他は null
     * @param id リスナーが複数箇所から呼び出される場合、条件分岐に使用 必要ない場合はnullを指定 nullの場合は1となります
     */
    public static AlertDialogFragment newInstance(String title, String message, String positiveBtn, String negativeBtn, int type, Fragment target, Integer id) {
        AlertDialogFragment fragment = new AlertDialogFragment();

        if (target != null) {
            fragment.setTargetFragment(target, 0);
        }

        if (null == id) {
            id = 1;
        }

        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(MESSAGE, message);
        args.putString(POSITIVE_BTN, positiveBtn);
        args.putString(NEGATIVE_BTN, negativeBtn);
        args.putInt(TYPE, type);
        args.putInt(ID, id);
        fragment.setArguments(args);
        fragment.setCancelable(false);

        return fragment;
    }


    /**
     * staticファクトリメソッド
     * ボタン1つ、リスナーなし
     * @param positiveBtn positiveButton のテキストを指定　""(空文字)の場合は "OK"
     */
    public static AlertDialogFragment newInstance(String title, String message, String positiveBtn){
        return newInstance(title, message, positiveBtn, "", BTN_ONE, null, null);
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = args.getString(TITLE);
        String message = args.getString(MESSAGE);
        String positiveBtn = args.getString(POSITIVE_BTN);
        String negativeBtn = args.getString(NEGATIVE_BTN);
        int type = args.getInt(TYPE);

        if ("".equals(positiveBtn)){
            positiveBtn = getString(R.string.dialog_ok);
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveBtn, onClickListener());

        if (type == BTN_TWO) {
            if ("".equals(negativeBtn)){
                negativeBtn = getString(R.string.dialog_cancel);
            }

            dialog.setNegativeButton(negativeBtn, onClickListener());
        }

        return dialog.create();
    }

    public void setListener(AlertDialogListener listener) {
        this.listener = listener;
    }

    private OnClickListener onClickListener() {
        return new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    int id = getArguments().getInt(ID);

                    if (which == DialogInterface.BUTTON_POSITIVE){
                        listener.doPositiveClick(id);
                    } else {
                        listener.doNegativeClick(id);
                    }
                }

                dismiss();
            }
        };
    }
}

package com.cooksnet.post;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EditTextDialogFragment extends DialogFragment {
    private static final String TITLE = "title";
    private static final String MESSAGE = "message";
    private static final String POSITIVE_BTN = "positiveBtn";
    private static final String NEGATIVE_BTN = "negativeBtn";
    private static final String TYPE = "type";
    private static final String ID = "id";

    public static final int BTN_ONE = 1;
    public static final int BTN_TWO = 2;

    private EditText messageEE;
    private TextView messageE;
    private InputFilter inputFilter;
    private TextWatcher textWatcher;
    private AlertDialogListener listener;

    /**
     * staticファクトリメソッド
     *
     * @param positiveBtn positiveButton のテキストを指定　""(空文字)の場合は "OK"
     * @param negativeBtn negativeButton のテキストを指定　""(空文字)の場合は "Cancel"
     * @param type        表示する Bu tton の数 1:positiveButton のみ 2:positive & negativeButton
     * @param target      呼び出し元が Fragment の場合は呼び出し元の Fragment を設定、その他は null
     * @param id          リスナーが複数箇所から呼び出される場合、条件分岐に使用 必要ない場合はnullを指定 nullの場合は1となります
     */
    public static EditTextDialogFragment newInstance(String title, String message, String positiveBtn, String negativeBtn, int type, Fragment target, Integer id) {
        EditTextDialogFragment fragment = new EditTextDialogFragment();

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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = args.getString(TITLE);
        String message = args.getString(MESSAGE);
        String positiveBtn = args.getString(POSITIVE_BTN);
        String negativeBtn = args.getString(NEGATIVE_BTN);
        int type = args.getInt(TYPE);

        if ("".equals(positiveBtn)) {
            positiveBtn = getString(R.string.dialog_ok);
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveBtn, onClickListener());

        if (type == BTN_TWO) {
            if ("".equals(negativeBtn)) {
                negativeBtn = getString(R.string.dialog_cancel);
            }

            dialog.setNegativeButton(negativeBtn, onClickListener());
        }

        final LinearLayout messageView = (LinearLayout) View.inflate(getContext(), R.layout.dialog_message, null);
        messageEE = (EditText) messageView.findViewById(R.id.message);
        messageEE.setText(messageE.getText().toString().trim());
        messageEE.setFilters(new InputFilter[]{inputFilter});
        messageEE.addTextChangedListener(textWatcher);
        messageEE.requestFocus();
        dialog.setView(messageView);

        return dialog.create();
    }

    public void setListener(AlertDialogListener listener) {
        this.listener = listener;
    }

    public void setTextView(TextView messageE) {
        this.messageE = messageE;
    }

    public void setInputFilter(InputFilter inputFilter) {
        this.inputFilter = inputFilter;
    }

    public void setTextWatcher(TextWatcher textWatcher) {
        this.textWatcher = textWatcher;
    }

    public EditText getEditText() {
        return this.messageEE;
    }

    private DialogInterface.OnClickListener onClickListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    int id = getArguments().getInt(ID);

                    if (which == DialogInterface.BUTTON_POSITIVE) {
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

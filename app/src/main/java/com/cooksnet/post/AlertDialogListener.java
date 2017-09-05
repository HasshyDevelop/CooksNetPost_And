package com.cooksnet.post;

import java.util.EventListener;

/**
 * Created by takana on 2016/06/07
 * int id は複数呼び出しがある場合に条件分岐として仕様
 */
public interface AlertDialogListener extends EventListener {
    void doPositiveClick(int id);
    void doNegativeClick(int id);
}

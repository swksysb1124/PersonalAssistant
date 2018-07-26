package com.weikai.personalassistant.view;

import android.database.Cursor;

/**
 * Created by suweikai on 2018/7/25.
 */

public interface ContactView {
    void updateCursorAdapter(Cursor cursor);
    void updateButtonState(Cursor cursor);
    void updateName(String name);
    void updatePhone(String phone);
    void updateEmail(String email);
    String getName();
    String getPhone();
    String getEmail();
    void enableActionCallButton(boolean enabled);
    void enableActionEmailButton(boolean enabled);
    void enableUpdateButton(boolean enabled);
    void enableDeleteButton(boolean enabled);
    void showToast(String message);
}

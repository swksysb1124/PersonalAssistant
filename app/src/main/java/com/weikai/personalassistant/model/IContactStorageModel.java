package com.weikai.personalassistant.model;

import android.database.Cursor;
import android.net.Uri;

import java.util.List;

/**
 * Created by suweikai on 2018/7/24.
 */

public interface IContactStorageModel {
    void add(String name, String phone, String email);
    void update(int id, String name, String phone, String email);
    void query(OnCursorUpdateCallback onCursorUpdateCallback);
    void readAll(OnAllDataUpdateCallback callback);
    void delete(int id);
    void moveToPosition(int position);

    String getContactFieldAtCurrentPosition(String field);
    int getContactCount();
    Uri getContactInfo(int type);
    Cursor getCursor();
    public interface OnAllDataUpdateCallback {
        void onUpdate(List<Contact> contacts);
    }
    interface OnCursorUpdateCallback {
        void onUpdate(Cursor cursor);
    }
    interface Contact {
        String getName();
        String getPhone();
        String getEmail();
    }
}

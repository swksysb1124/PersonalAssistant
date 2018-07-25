package com.weikai.personalassistant.presenter;

import android.content.Context;

import com.weikai.personalassistant.ContactView;
import com.weikai.personalassistant.model.IContactStorageModel;
import com.weikai.personalassistant.model.SQLiteModel;

/**
 * Created by suweikai on 2018/7/25.
 */

public class ContactPresenter {

    private ContactView mContactView;
    private IContactStorageModel mContactStorageModel;

    public ContactPresenter(ContactView contactView, Context context) {
        mContactView = contactView;
        mContactStorageModel = new SQLiteModel(context);
    }

    public void queryData() {

    }


    public void release() {
        mContactView = null;
        mContactStorageModel = null;
    }

}

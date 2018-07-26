package com.weikai.personalassistant.presenter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.weikai.personalassistant.view.ContactView;
import com.weikai.personalassistant.R;
import com.weikai.personalassistant.model.IContactStorageModel;
import com.weikai.personalassistant.model.SQLiteModel;

/**
 * Created by suweikai on 2018/7/25.
 */

public class ContactPresenter {

    private static final String TAG = ContactPresenter.class.getSimpleName();
    private ContactView mContactView;
    private Context mContext;
    private IContactStorageModel mContactStorageModel;

    public ContactPresenter(ContactView contactView, Context context) {
        mContactView = contactView;
        mContext = context;
        mContactStorageModel = new SQLiteModel(mContext);
    }

    public void addData(String name, String phone,String email){
        mContactStorageModel.add(name, phone, email);
    }

    public void updateData(String name, String phone, String email, int id){
        mContactStorageModel.update(id, name, phone, email);
    }

    public void queryData() {
        mContactStorageModel.query(new IContactStorageModel.OnCursorUpdateCallback() {
            @Override
            public void onUpdate(Cursor cursor) {
                mContactView.updateCursorAdapter(cursor);
                mContactView.updateButtonState(cursor);
            }
        });
    }

    public Cursor getCursor() {
        return mContactStorageModel.getCursor();
    }

    public void selectData(int position) {
        mContactStorageModel.moveToPosition(position);
        mContactView.updateName(mContactStorageModel.getContactFieldAtCurrentPosition("name"));
        mContactView.updatePhone(mContactStorageModel.getContactFieldAtCurrentPosition("phone"));
        mContactView.updateEmail(mContactStorageModel.getContactFieldAtCurrentPosition("email"));
        mContactView.enableActionCallButton(true);
        mContactView.enableActionEmailButton(true);
        mContactView.enableUpdateButton(true);
        mContactView.enableDeleteButton(true);
    }

    public void insertOrUpdate(View v){
        String nameStr = mContactView.getName();
        String phoneStr = mContactView.getPhone();
        String emailStr = mContactView.getEmail();
        if(nameStr.length()==0 ||
                phoneStr.length()==0 ||
                emailStr.length()==0)return;
        if(v.getId() == R.id.btn_update) {
            updateData(nameStr, phoneStr, emailStr, mContactStorageModel.getCursor().getInt(0));
        }else {
            addData(nameStr, phoneStr, emailStr);
        }
        queryData();
        mContactView.updateEmail("");
        mContactView.updateName("");
        mContactView.updatePhone("");
    }

    public void delete(){
        mContactStorageModel.delete(mContactStorageModel.getCursor().getInt(0));
        queryData();
        mContactView.updateEmail("");
        mContactView.updateName("");
        mContactView.updatePhone("");
    }

    public void call(){
        String uri ="tel: " + mContactStorageModel.getContactFieldAtCurrentPosition("phone");
        log("uri = "+uri);
        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        mContext.startActivity(it);
    }

    public void mail(){
        String uri ="mailto: " + mContactStorageModel.getContactFieldAtCurrentPosition("email");
        log("uri = "+uri);
        Intent it = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
        mContext.startActivity(it);
    }

    public void release() {
        mContactView = null;
        mContactStorageModel = null;
    }

    private void log(String message){
        Log.d(TAG, message);
    }
}

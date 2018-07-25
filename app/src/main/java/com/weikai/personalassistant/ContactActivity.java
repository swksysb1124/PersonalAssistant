package com.weikai.personalassistant;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;


public class ContactActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener{

    private static final String TAG = ContactActivity.class.getSimpleName();
    private IContactStorageModel contactStorageModel;
    private SimpleCursorAdapter cursorAdapter;
    private EditText edtName, edtPhone, edtEmail;	//用來輸入姓名，電話，Email欄位
    private Button btnInsert, btnUpdate, btnDelete;
    private ImageButton ibtnActionCall;
    private ImageButton ibtnActionEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initModel();
        initCursorAdapter();
        initContactListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryData();
    }

    private void initViews() {
        edtName = (EditText) findViewById(R.id.edt_name);
        edtPhone = (EditText) findViewById(R.id.edt_phone);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        btnInsert = (Button) findViewById(R.id.btn_insert);
        btnUpdate = (Button) findViewById(R.id.btn_update);
        btnDelete = (Button) findViewById(R.id.btn_delete);
        ibtnActionCall = (ImageButton) findViewById(R.id.ibtn_action_call);
        ibtnActionEmail = (ImageButton) findViewById(R.id.ibtn_action_email);
    }

    private void initModel() {
        contactStorageModel = new ContactStorageModel(this);
    }

    private void initCursorAdapter() {
        cursorAdapter = new SimpleCursorAdapter(this,
                R.layout.contact_list_item,      // 自訂的layout
                contactStorageModel.getCursor(),			                 // Cursor物件
                ContactStorageModel.TABLE_FIELD_NAMES,				             // 欄位名稱陣列
                new int[] {R.id.txt_name, R.id.txt_phone, R.id.txt_email},	// TextView 資源ID
                0);
    }

    private void initContactListView() {
        ListView lvContact = (ListView)findViewById(R.id.lv);
        lvContact.setAdapter(cursorAdapter);						// 設定Adapter
        lvContact.setOnItemClickListener(this);			// 設定按下事件的監聽器
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addData(String name, String phone,String email){
        contactStorageModel.add(name, phone, email);
    }

    private void updateData(String name, String phone, String email, int id){
        contactStorageModel.update(id, name, phone, email);
    }

    private void queryData() {
        contactStorageModel.query(new IContactStorageModel.OnCursorUpdateCallback() {
            @Override
            public void onUpdate(Cursor cursor) {
                updateCursorAdapter(cursor);
                updateButtonState(cursor);
            }
        });
    }

    private void updateCursorAdapter(Cursor cursor) {
        if(cursor == null) return;
        if(cursorAdapter != null) {
            cursorAdapter.changeCursor(cursor);                    // 更改Adapter的Cursor
        }
    }

    private void updateButtonState(Cursor cursor) {
        if(cursor == null) return;
        if(cursor.getCount() == 0) {
            ibtnActionCall.setEnabled(false);
            ibtnActionEmail.setEnabled(false);
        }
        if (cursor.getCount() == ContactStorageModel.DATA_COUNT_MAX) {                // 已達上限，停用Insert Button
            btnInsert.setEnabled(false);
        } else {
            btnInsert.setEnabled(true);
        }
        btnUpdate.setEnabled(false);                        // 停用Update Button, 待使用者選取item後自啟用
        btnDelete.setEnabled(false);                        // 停用Delete Button, 待使用者選取item後自啟用
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v,
                            int position, long id){
        contactStorageModel.moveToPosition(position);
        edtName.setText(contactStorageModel.getContactFieldAtCurrentPosition("name"));
        edtPhone.setText(contactStorageModel.getContactFieldAtCurrentPosition("phone"));
        edtEmail.setText(contactStorageModel.getContactFieldAtCurrentPosition("email"));
        ibtnActionCall.setEnabled(true);
        ibtnActionEmail.setEnabled(true);
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    public void insertOrUpdate(View v){
        String nameStr = edtName.getText().toString().trim();
        String phoneStr = edtPhone.getText().toString().trim();
        String emailStr = edtEmail.getText().toString().trim();
        if(nameStr.length()==0 ||
                phoneStr.length()==0 ||
                emailStr.length()==0)return;
        if(v.getId() == R.id.btn_update) {
            updateData(nameStr, phoneStr, emailStr, contactStorageModel.getCursor().getInt(0));
        }else {
            addData(nameStr, phoneStr, emailStr);
        }
        queryData();
    }

    public void delete(View v){
        contactStorageModel.delete(contactStorageModel.getCursor().getInt(0));
        queryData();
    }

    public void call(View v){
        String uri ="tel: " + contactStorageModel.getContactFieldAtCurrentPosition("phone");
        log("uri = "+uri);
        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(it);
    }

    public void mail(View v){
        String uri ="mailto: " + contactStorageModel.getContactFieldAtCurrentPosition("email");
        log("uri = "+uri);
        Intent it = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
        startActivity(it);
    }

    private void log(String message){
        Log.d(TAG, message);
    }

}
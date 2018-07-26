package com.weikai.personalassistant.view;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.weikai.personalassistant.R;
import com.weikai.personalassistant.model.SQLiteModel;
import com.weikai.personalassistant.presenter.ContactPresenter;


public class ContactActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, ContactView{

    private ContactPresenter contactPresenter;
    private SimpleCursorAdapter cursorAdapter;
    private EditText edtName, edtPhone, edtEmail;	//用來輸入姓名，電話，Email欄位
    private Button btnInsert, btnUpdate, btnDelete;
    private ImageButton ibtnActionCall;
    private ImageButton ibtnActionEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contactPresenter = new ContactPresenter(this, this);
        initViews();
        initCursorAdapter();
        initContactListView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        contactPresenter.release();
        contactPresenter = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        contactPresenter.queryData();
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


    private void initCursorAdapter() {
        cursorAdapter = new SimpleCursorAdapter(this,
                R.layout.contact_list_item,                                     // 自訂的layout
                contactPresenter.getCursor(),			                    // Cursor物件
                SQLiteModel.TABLE_FIELD_NAMES,				                    // 欄位名稱陣列
                new int[] {R.id.txt_name, R.id.txt_phone, R.id.txt_email},	    // TextView 資源ID
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

    @Override
    public void updateCursorAdapter(Cursor cursor) {
        if(cursor == null) return;
        if(cursorAdapter != null) {
            cursorAdapter.changeCursor(cursor);                    // 更改Adapter的Cursor
        }
    }

    @Override
    public void updateButtonState(Cursor cursor) {
        if(cursor == null) return;
        if(cursor.getCount() == 0) {
            ibtnActionCall.setEnabled(false);
            ibtnActionEmail.setEnabled(false);
        }
        if (cursor.getCount() == SQLiteModel.DATA_COUNT_MAX) {                // 已達上限，停用Insert Button
            btnInsert.setEnabled(false);
        } else {
            btnInsert.setEnabled(true);
        }
        btnUpdate.setEnabled(false);                        // 停用Update Button, 待使用者選取item後自啟用
        btnDelete.setEnabled(false);                        // 停用Delete Button, 待使用者選取item後自啟用
    }

    @Override
    public void updateName(String name) {
        edtName.setText(name);
    }

    @Override
    public void updatePhone(String phone) {
        edtPhone.setText(phone);
    }

    @Override
    public void updateEmail(String email) {
        edtEmail.setText(email);
    }

    @Override
    public String getName() {
        return edtName.getText().toString().trim();
    }

    @Override
    public String getPhone() {
        return edtPhone.getText().toString().trim();
    }

    @Override
    public String getEmail() {
        return edtEmail.getText().toString().trim();
    }

    @Override
    public void enableActionCallButton(boolean enabled) {
        ibtnActionCall.setEnabled(enabled);
    }

    @Override
    public void enableActionEmailButton(boolean enabled) {
        ibtnActionEmail.setEnabled(enabled);
    }

    @Override
    public void enableUpdateButton(boolean enabled) {
        btnUpdate.setEnabled(enabled);
    }

    @Override
    public void enableDeleteButton(boolean enabled) {
        btnDelete.setEnabled(enabled);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v,
                            int position, long id){
        contactPresenter.selectData(position);
    }

    public void insertOrUpdate(View v){
        contactPresenter.insertOrUpdate(v);
    }

    public void delete(View v){
        contactPresenter.delete();
    }

    public void call(View v){
        contactPresenter.call();
    }

    public void mail(View v){
        contactPresenter.mail();
    }

}
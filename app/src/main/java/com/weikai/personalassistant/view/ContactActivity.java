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
import android.widget.Toast;

import com.weikai.personalassistant.R;
import com.weikai.personalassistant.model.SQLiteModel;
import com.weikai.personalassistant.presenter.ContactPresenter;


public class ContactActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, ContactView{

    private ContactPresenter mContactPresenter;
    private SimpleCursorAdapter mCursorAdapter;
    private EditText edtName, edtPhone, edtEmail;	//用來輸入姓名，電話，Email欄位
    private Button btnInsert, btnUpdate, btnDelete;
    private ImageButton ibtnActionCall;
    private ImageButton ibtnActionEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContactPresenter = new ContactPresenter(this, this);
        initViews();
        initCursorAdapter();
        initContactListView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContactPresenter.release();
        mContactPresenter = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mContactPresenter.queryData();
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
        mCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.contact_list_item,                                     // 自訂的layout
                mContactPresenter.getCursor(),			                    // Cursor物件
                SQLiteModel.TABLE_FIELD_NAMES,				                    // 欄位名稱陣列
                new int[] {R.id.txt_name, R.id.txt_phone, R.id.txt_email},	    // TextView 資源ID
                0);
    }

    private void initContactListView() {
        ListView lvContact = (ListView)findViewById(R.id.lv);
        lvContact.setAdapter(mCursorAdapter);						// 設定Adapter
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
        if(mCursorAdapter != null) {
            mCursorAdapter.changeCursor(cursor);                    // 更改Adapter的Cursor
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
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v,
                            int position, long id){
        mContactPresenter.selectData(position);
    }

    public void insertOrUpdate(View v){
        mContactPresenter.insertOrUpdate(v);
    }

    public void delete(View v){
        mContactPresenter.delete();
    }

    public void call(View v){
        mContactPresenter.call();
    }

    public void mail(View v){
        mContactPresenter.mail();
    }

}
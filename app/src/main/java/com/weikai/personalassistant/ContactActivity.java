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
import android.widget.ListView;


public class ContactActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener{

    private static final String TAG = ContactActivity.class.getSimpleName();
    private static final String DB_NAME = "HotlinDB";	// 數據庫名稱
    private static final String TB_NAME = "hotlist";	// 數據表名稱
    private static final int DATA_COUNT_MAX = 8;	    // 通訊數據筆數上限
    private static final String[] TABLE_FIELD_NAMES = new String[]{"name","phone","email"}; //欄位名稱

    private SQLiteDatabase db;
    private Cursor cursor;							//存放查詢結果的Cursor物件. Cursor is a pointer pointing to the data in the database
    private SimpleCursorAdapter cursorAdapter;
    private EditText edtName, edtPhone, edtEmail;	//用來輸入姓名，電話，Email欄位
    private Button btnInsert, btnUpdate, btnDelete;
    private ListView lvContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initDatabase();
        createTable();
        queryData();
        initCursorAdapter();
        initContactListView();
        requery();
    }

    private void initViews() {
        edtName = (EditText) findViewById(R.id.edt_name);
        edtPhone = (EditText) findViewById(R.id.edt_phone);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        btnInsert = (Button) findViewById(R.id.btn_insert);
        btnUpdate = (Button) findViewById(R.id.btn_update);
        btnDelete = (Button) findViewById(R.id.btn_delete);
    }

    private void initDatabase() {
        db = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
    }

    private void createTable() {
        if(db == null) {
            Log.e(TAG, "database not initialized yet");
            throw new IllegalStateException("database not initialized yet");
        }
        String createTable = "CREATE TABLE IF NOT EXISTS " + TB_NAME +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR(32), " +
                "phone VARCHAR(16), " +
                "email VARCHAR(64))";
        db.execSQL(createTable);// create a table
    }

    private void queryData() {
        if(db == null) {
            Log.e(TAG, "database not initialized yet");
            throw new IllegalStateException("database not initialized yet");
        }
        cursor = db.rawQuery("SELECT * FROM " + TB_NAME, null );// query data from database
        //若是空的則寫入測試程式
        if(cursor.getCount()==0){
            addData("蘇暐凱","02-29136322","swksysb1124@gmail.com");
            addData("林香君","02-12345678","sysb1125@gmail.com");
        }
    }

    private void initCursorAdapter() {
        if(cursor == null) {
            notifyIllegalStateError("cursor not initialized yet");
            return;
        }
        cursorAdapter = new SimpleCursorAdapter(this,
                R.layout.contact_list_item,      // 自訂的layout
                cursor,			                 // Cursor物件
                TABLE_FIELD_NAMES,				             // 欄位名稱陣列
                new int[] {R.id.txt_name, R.id.txt_phone, R.id.txt_email},	// TextView 資源ID
                0);
    }

    private void initContactListView() {
        if(cursorAdapter == null) {
            notifyIllegalStateError("cursorAdapter not initialized yet");
            return;
        }
        lvContact = (ListView)findViewById(R.id.lv);
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
        if(db == null) {
            Log.e(TAG, "database not initialized yet");
            throw new IllegalStateException("database not initialized yet");
        }
        ContentValues cv = new ContentValues(3);
        cv.put(TABLE_FIELD_NAMES[0], name);
        cv.put(TABLE_FIELD_NAMES[1], phone);
        cv.put(TABLE_FIELD_NAMES[2], email);
        db.insert(TB_NAME, null, cv);
    }

    private void updateData(String name, String phone, String email, int id){
        if(db == null) {
            Log.e(TAG, "database not initialized yet");
            throw new IllegalStateException("database not initialized yet");
        }
        ContentValues cv = new ContentValues(3);
        cv.put(TABLE_FIELD_NAMES[0], name);
        cv.put(TABLE_FIELD_NAMES[1], phone);
        cv.put(TABLE_FIELD_NAMES[2], email);

		/* 更新_id所指的紀錄  */
        db.update(	TB_NAME, 	// 需更新的 表
                cv, 		// 含資料的ContentValues 物件
                "_id="+id, 	// 更新的條件式
                null);		// 不使用
    }

    private void requery(){
        if(db == null) {
            Log.e(TAG, "database not initialized yet");
            throw new IllegalStateException("database not initialized yet");
        }
        if(cursor == null) {
            notifyIllegalStateError("cursor not initialized yet");
            return;
        }
        cursor = db.rawQuery("SELECT * FROM " + TB_NAME ,null);
        cursorAdapter.changeCursor(cursor);					// 更改Adapter的Cursor
        if(cursor.getCount()== DATA_COUNT_MAX){				// 已達上限，停用Insert Button
            btnInsert.setEnabled(false);
        }else{
            btnInsert.setEnabled(true);
        }
        btnUpdate.setEnabled(false);					    // 停用Update Button, 待使用者選取item後自啟用
        btnDelete.setEnabled(false);					    // 停用Delete Button, 待使用者選取item後自啟用
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v,
                            int position, long id){
        if(cursor == null) {
            notifyIllegalStateError("cursor not initialized yet");
            return;
        }
        cursor.moveToPosition(position);
        edtName.setText(cursor.getString(cursor.getColumnIndex(TABLE_FIELD_NAMES[0])));
        edtPhone.setText(cursor.getString(cursor.getColumnIndex(TABLE_FIELD_NAMES[1])));
        edtEmail.setText(cursor.getString(cursor.getColumnIndex(TABLE_FIELD_NAMES[2])));
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    public void insertOrUpdate(View v){
        if(cursor == null) {
            notifyIllegalStateError("cursor not initialized yet");
            return;
        }
        String nameStr = edtName.getText().toString().trim();
        String phoneStr = edtPhone.getText().toString().trim();
        String emailStr = edtEmail.getText().toString().trim();
        if(nameStr.length()==0 ||
                phoneStr.length()==0 ||
                emailStr.length()==0)return;
        if(v.getId()==R.id.btn_update)
            updateData(nameStr,phoneStr,emailStr, cursor.getInt(0));
        else
            addData(nameStr,phoneStr,emailStr);
        requery();
    }

    public void delete(View v){
        if(db == null) {
            Log.e(TAG, "database not initialized yet");
            throw new IllegalStateException("database not initialized yet");
        }
        if(cursor == null) {
            notifyIllegalStateError("cursor not initialized yet");
            return;
        }
        db.delete(TB_NAME, "_id="+ cursor.getInt(0), null);
        requery();
    }

    public void call(View v){
        if(cursor == null) {
            notifyIllegalStateError("cursor not initialized yet");
            return;
        }
        String uri="tel: " + cursor.getString(
                cursor.getColumnIndex(TABLE_FIELD_NAMES[1]));
        Intent it = new Intent(Intent.ACTION_VIEW,Uri.parse(uri));
        startActivity(it);
    }

    public void mail(View v){
        if(cursor == null) {
            notifyIllegalStateError("cursor not initialized yet");
            return;
        }
        String uri="mailto: " + cursor.getString(
                cursor.getColumnIndex(TABLE_FIELD_NAMES[2]));
        Intent it = new Intent(Intent.ACTION_SENDTO,Uri.parse(uri));
        startActivity(it);
    }

    private void notifyIllegalStateError(String error) {
        String errorMesssage;
        errorMesssage = (error == null)?"Some object not initialized yet":error;
        Log.e(TAG, errorMesssage);
        throw new IllegalStateException(errorMesssage);
    }

}

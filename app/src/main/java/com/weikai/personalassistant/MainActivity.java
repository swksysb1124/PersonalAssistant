package com.weikai.personalassistant;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity
        implements AdapterView.OnItemClickListener{
    static final String DB_NAME = "HotlinDB";	//庫名稱
    static final String TB_NAME = "hotlist";	//表名稱
    static final int MAX=8;						//通訊資料筆數上限
    static final String[] FROM = new String[]{"name","phone","email"}; //欄位名稱字串陣列
    SQLiteDatabase db;
    Cursor cur;							//存放查詢結果的Cursor物件. Cursor is a pointer pointing to the data in the database
    SimpleCursorAdapter adapter;
    EditText etName,etPhone,etEmail;	//用來輸入姓名，電話，Email欄位
    Button btInsert,btUpdate, btDelete;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //取得畫面的元件
        etName = (EditText) findViewById(R.id.etName);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etEmail = (EditText) findViewById(R.id.etEmail);
        btInsert = (Button) findViewById(R.id.btInsert);
        btUpdate = (Button) findViewById(R.id.btUpdate);
        btDelete = (Button) findViewById(R.id.btDelete);
        //開啟或建立庫
        db = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        //建立表
        String createTable = "CREATE TABLE IF NOT EXISTS " + TB_NAME +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR(32), " +
                "phone VARCHAR(16), " +
                "email VARCHAR(64))";
        db.execSQL(createTable);// create a table

        cur = db.rawQuery("SELECT * FROM " + TB_NAME, null );// query date from database

        //若是空的則寫入測試程式
        if(cur.getCount()==0){
            addData("蘇暐凱","02-29136322","swksysb1124@gmail.com");
            addData("林香君","02-12345678","sysb1125@gmail.com");
        }
        //建立Adapter 物件
        adapter = new SimpleCursorAdapter(this,
                R.layout.item, cur,			// 自訂的layout, Cursor物件
                FROM,						// 欄位名稱陣列
                new int[] {R.id.name,R.id.phone,R.id.email},	// TextView 資源ID
                0);
        lv = (ListView)findViewById(R.id.lv);
        lv.setAdapter(adapter);						// 設定Adapter
        lv.setOnItemClickListener(this);			// 設定按下事件的監聽器
        requery();
    }

    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
    //    // Inflate the menu; this adds items to the action bar if it is present.
    //    getMenuInflater().inflate(R.menu.main, menu);
    //    return true;
    //}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addData(String name, String phone,String email){
        ContentValues cv = new ContentValues(3);
        cv.put(FROM[0], name);
        cv.put(FROM[1], phone);
        cv.put(FROM[2], email);
        db.insert(TB_NAME, null, cv);
    }

    private void update(String name, String phone, String email, int id){
        ContentValues cv = new ContentValues(3);
        cv.put(FROM[0], name);
        cv.put(FROM[1], phone);
        cv.put(FROM[2], email);

		/* 更新_id所指的紀錄  */
        db.update(	TB_NAME, 	// 需更新的 表
                cv, 		// 含資料的ContentValues 物件
                "_id="+id, 	// 更新的條件式
                null);		// 不使用
    }

    private void requery(){
        cur = db.rawQuery("SELECT * FROM " + TB_NAME ,null);
        adapter.changeCursor(cur);					// 更改Adapter的Cursor
        if(cur.getCount()== MAX){					// 已達上限，停用Insert Button
            btInsert.setEnabled(false);
        }else{
            btInsert.setEnabled(true);
        }
        btUpdate.setEnabled(false);					// 停用Update Button, 待使用者選取item後自啟用
        btDelete.setEnabled(false);					// 停用Delete Button, 待使用者選取item後自啟用
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v,
                            int position, long id){
        cur.moveToPosition(position);
        etName.setText(cur.getString(
                cur.getColumnIndex(FROM[0])));
        etPhone.setText(cur.getString(
                cur.getColumnIndex(FROM[1])));
        etEmail.setText(cur.getString(
                cur.getColumnIndex(FROM[2])));
        btUpdate.setEnabled(true);
        btDelete.setEnabled(true);
    }

    public void InsertOrUpdate(View v){
        String nameStr = etName.getText().toString().trim();
        String phoneStr = etPhone.getText().toString().trim();
        String emailStr = etEmail.getText().toString().trim();
        if(nameStr.length()==0 ||
                phoneStr.length()==0 ||
                emailStr.length()==0)return;
        if(v.getId()==R.id.btUpdate)
            update(nameStr,phoneStr,emailStr,cur.getInt(0));
        else
            addData(nameStr,phoneStr,emailStr);
        requery();
    }
    public void delete(View v){
        db.delete(TB_NAME, "_id="+cur.getInt(0), null);
        requery();
    }
    public void call(View v){
        String uri="tel: " + cur.getString(
                cur.getColumnIndex(FROM[1]));
        Intent it = new Intent(Intent.ACTION_VIEW,Uri.parse(uri));
        startActivity(it);
    }
    public void mail(View v){
        String uri="mailto: " + cur.getString(
                cur.getColumnIndex(FROM[2]));
        Intent it = new Intent(Intent.ACTION_SENDTO,Uri.parse(uri));
        startActivity(it);
    }


}

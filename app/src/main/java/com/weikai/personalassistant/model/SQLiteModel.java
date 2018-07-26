package com.weikai.personalassistant.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by suweikai on 2018/7/24.
 */

public class SQLiteModel implements IContactStorageModel {

    private static final String TAG = SQLiteModel.class.getSimpleName();
    private static final String DATABASE_NAME = "HotlinDB";	// 數據庫名稱
    private static final String TABLE_NAME = "hotlist";	// 數據表名稱
    public static final int DATA_COUNT_MAX = 8;	    // 通訊數據筆數上限
    public static final int INFO_TYPE_CALL = 0;
    public static final int INFO_TYPE_EMAIL = 1;
    public static final String[] TABLE_FIELD_NAMES = new String[]{"name","phone","email"}; //欄位名稱

    private Context mContext;
    private SQLiteDatabase mDatabase;
    private Cursor mCursor;

    public SQLiteModel(Context context) {
        mContext = context.getApplicationContext();
        openOrCreateDatabase();
        openOrCreateTable();
    }

    private void openOrCreateDatabase() {
        if(mDatabase == null) {
            mDatabase = mContext.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        }
    }

    private void openOrCreateTable() {
        if(mDatabase == null) {
            Log.e(TAG, "database not initialized yet");
            throw new IllegalStateException("database not initialized yet");
        }
        String createTableSql =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR(32), " +
                "phone VARCHAR(16), " +
                "email VARCHAR(64))";
        mDatabase.execSQL(createTableSql);// create a table
    }

    @Override
    public void add(String name, String phone, String email) {
        if(mDatabase == null) {
            Log.e(TAG, "database not initialized yet");
            throw new IllegalStateException("database not initialized yet");
        }
        ContentValues cv = new ContentValues(3);
        cv.put(TABLE_FIELD_NAMES[0], name);
        cv.put(TABLE_FIELD_NAMES[1], phone);
        cv.put(TABLE_FIELD_NAMES[2], email);
        mDatabase.insert(TABLE_NAME, null, cv);
    }

    @Override
    public void update(int id, String name, String phone, String email) {
        if(mDatabase == null) {
            Log.e(TAG, "database not initialized yet");
            throw new IllegalStateException("database not initialized yet");
        }
        ContentValues cv = new ContentValues(3);
        cv.put(TABLE_FIELD_NAMES[0], name);
        cv.put(TABLE_FIELD_NAMES[1], phone);
        cv.put(TABLE_FIELD_NAMES[2], email);

		/* 更新_id所指的紀錄  */
        mDatabase.update(
                TABLE_NAME,	// 需更新的 表
                cv, 		// 含資料的ContentValues 物件
                "_id="+id, 	// 更新的條件式
                null);		// 不使用
    }

    @Override
    public void query(OnCursorUpdateCallback onCursorUpdateCallback) {
        if(mDatabase == null) {
            Log.e(TAG, "database not initialized yet");
            throw new IllegalStateException("database not initialized yet");
        }
        mCursor = mDatabase.rawQuery("SELECT * FROM " +TABLE_NAME,null);
        if(onCursorUpdateCallback != null) {
            onCursorUpdateCallback.onUpdate(mCursor);
        }
    }

    @Override
    public void readAll(OnAllDataUpdateCallback callback) {
        // To be implemented
    }

    @Override
    public void delete(int id) {
        if(mDatabase == null) {
            Log.e(TAG, "database not initialized yet");
            throw new IllegalStateException("database not initialized yet");
        }
        if(mCursor == null) {
            notifyIllegalStateError("cursor not initialized yet");
            return;
        }
        mDatabase.delete(TABLE_NAME, "_id="+ mCursor.getInt(0), null);
    }

    @Override
    public int getContactCount() {
        int count = 0;
        if(mCursor!=null){
            count = mCursor.getCount();
        }
        return count;
    }

    @Override
    public Uri getContactInfo(int infoType) {
        if(mCursor == null) {
            notifyIllegalStateError("cursor not initialized yet");
            return null;
        }
        String resultUri = null;
        switch (infoType) {
            case INFO_TYPE_CALL:
                resultUri = "tel: " + mCursor.getString(mCursor.getColumnIndex(TABLE_FIELD_NAMES[1]));
                break;

            case INFO_TYPE_EMAIL:
                resultUri ="mailto: " + mCursor.getString(mCursor.getColumnIndex(TABLE_FIELD_NAMES[2]));
                break;

            default:
                break;
        }
        return Uri.parse(resultUri);
    }

    @Override
    public void moveToPosition(int position) {
        if(mCursor == null) {
            notifyIllegalStateError("cursor not initialized yet");
            return;
        }
        mCursor.moveToPosition(position);
    }

    @Override
    public String getContactFieldAtCurrentPosition(String field) {
        if(mCursor == null) {
            notifyIllegalStateError("cursor not initialized yet");
            return null;
        }
        int columnIndex = mCursor.getColumnIndex(field);
        if(columnIndex != -1){
            return mCursor.getString(columnIndex);
        }
        return "No Data";

    }

    @Override
    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public void release() {
        if(mCursor != null) {
            mCursor.close();
        }
        if(mDatabase != null) {
            mDatabase.close();
        }
    }

    private void notifyIllegalStateError(String error) {
        String errorMessage;
        errorMessage = (error == null)?"Some object not initialized yet":error;
        Log.e(TAG, errorMessage);
        throw new IllegalStateException(errorMessage);
    }

}

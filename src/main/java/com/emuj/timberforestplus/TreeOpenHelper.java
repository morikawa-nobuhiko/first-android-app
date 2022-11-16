package com.emuj.timberforestplus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TreeOpenHelper extends SQLiteOpenHelper {

    // データーベースのバージョン
    private static final int DATABASE_VERSION = 12;

    // データーベース名
    private static final String DATABASE_NAME = "tree.db";
    private static final String TABLE_NAME = "treeTable";
    private static final String _ID = "_id";
    private static final String COLUMN_NAME_OWNERID = "ownerid";
    private static final String COLUMN_NAME_NAME = "treename";
    private static final String COLUMN_NAME_SIZE = "treesize";
    private static final String COLUMN_NAME_REMARK = "treeremark";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    _ID + " INTEGER," +
                    COLUMN_NAME_OWNERID + " INTEGER," +
                    COLUMN_NAME_NAME + " TEXT," +
                    COLUMN_NAME_SIZE + " TEXT," +
                    COLUMN_NAME_REMARK + " TEXT," +
                    "PRIMARY KEY (_id, ownerid))";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    TreeOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // テーブル作成
        // SQLiteファイルがなければSQLiteファイルが作成される
        db.execSQL(
                SQL_CREATE_ENTRIES
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // アップデートの判別
        db.execSQL(
                SQL_DELETE_ENTRIES
        );
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
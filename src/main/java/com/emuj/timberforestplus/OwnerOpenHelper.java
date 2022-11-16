package com.emuj.timberforestplus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OwnerOpenHelper extends SQLiteOpenHelper {

    // データーベースのバージョン
    private static final int DATABASE_VERSION = 2;

    // データーベース名
    private static final String DATABASE_NAME = "owner.db";
    private static final String TABLE_NAME = "ownerTable";
    private static final String _ID = "_id";
    private static final String COLUMN_NAME_NAME = "ownername";
    private static final String COLUMN_NAME_SNO = "surveyno";
    private static final String COLUMN_NAME_YMD = "surveyymd";
    private static final String COLUMN_NAME_PREF = "pref";
    private static final String COLUMN_NAME_CITY = "city";
    private static final String COLUMN_NAME_OAZA = "oaza";
    private static final String COLUMN_NAME_AZA = "aza";
    private static final String COLUMN_NAME_TIBAN = "tiban";
    private static final String COLUMN_NAME_TIMOKU = "timoku";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_NAME + " TEXT," +
                    COLUMN_NAME_SNO + " TEXT," +
                    COLUMN_NAME_YMD + " TEXT," +
                    COLUMN_NAME_PREF + " TEXT," +
                    COLUMN_NAME_CITY + " TEXT," +
                    COLUMN_NAME_OAZA + " TEXT," +
                    COLUMN_NAME_AZA + " TEXT," +
                    COLUMN_NAME_TIBAN + " TEXT," +
                    COLUMN_NAME_TIMOKU + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    OwnerOpenHelper(Context context) {
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
package com.emuj.timberforestplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TreeListOpenHelper extends SQLiteOpenHelper {

    // データーベースのバージョン
    private static final int DATABASE_VERSION = 5;

    // データーベース名
    private static final String DATABASE_NAME = "treelist.db";
    private static final String TABLE_NAME = "treelistTable";
    private static final String _ID = "_id";
    private static final String COLUMN_NAME_NAME = "treename";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_NAME + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    TreeListOpenHelper(Context context) {
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
        addDefaultTrees(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // デフォルトの樹種一覧を追加
    private void addDefaultTrees(SQLiteDatabase db) {

        String trees[] = {"スギ", "ヒノキ", "マツ", "カエデ", "クワ", "サクラ", "ナラ", "シデ", "ミズキ"};

        try {
            db.beginTransaction();
            for (int i = 0; i <= trees.length - 1; i++) {
                ContentValues values = new ContentValues();
                values.put("treename", trees[i]);
                // DB追加
                db.insert(TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            // トランザクション終了
            db.endTransaction();
        }
    }

    // 樹種一覧を返す
    public String[] getTreeList(SQLiteDatabase db) {

        Cursor c;
        c = db.query(
                TABLE_NAME,
                new String[]{"_id", "treename"},
                null,
                null,
                null,
                null,
                null
        );
        c.moveToFirst();
        String trees[] = new String[c.getCount()];
        for (int i = 0; i <= trees.length - 1; i++) {
            String id = c.getString(0);
            String tn = c.getString(1);
            trees[i] = tn;
            c.moveToNext();
        }
        c.close();
        return trees;

    }

}

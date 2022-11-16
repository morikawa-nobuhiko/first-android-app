package com.emuj.timberforestplus;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class TreeListActivity extends AppCompatActivity {

    // DB
    private TreeListOpenHelper helper;
    private SQLiteDatabase db;
    private String[] treeNames;
    Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treelist);

        // 一覧をDBから読み込む
        readTreeList();

    }

    // メニュー
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //main.xmlの内容を読み込む
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.treelist, menu);
        return true;
    }

    // オプションメニューのアイテムが選択されたときに呼び出されるメソッド
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.treelist_menu_add:
                // 樹種名入力
                showTreeNameDialog();
                break;
            case R.id.treelist_menu_delete:
                deleteTree();
                readTreeList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // DB読み込み
    private void readTreeList() {

        if (helper == null) {
            helper = new TreeListOpenHelper(getApplicationContext());
        }
        if (db == null) {
            db = helper.getReadableDatabase();
        }

        c = db.query(
                "treelistTable",
                new String[]{"_id", "treename"},
                null,
                null,
                null,
                null,
                null
        );

        // カーソルを先頭に
        c.moveToFirst();

        // 樹種一覧表示
        treeNames = helper.getTreeList(db);
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, treeNames);
        ListView lv = findViewById(R.id.lvTreeList);
        lv.setAdapter(adapter);

    }

    // 樹種名入力ダイアログ
    public void showTreeNameDialog() {

        //テキスト入力を受け付けるビューを作成します。
        final EditText editView = new EditText(TreeListActivity.this);
        new AlertDialog.Builder(TreeListActivity.this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(R.string.treelistTitle)
                .setView(editView)
                .setPositiveButton(R.string.contTreeOK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // 樹種を追加
                        String tn = editView.getText().toString();
                        ContentValues values = new ContentValues();
                        values.put("treename", tn);
                        // DB追加
                        db.insert("treelistTable", null, values);
                        // 一覧をDBから読み込む
                        readTreeList();
                    }
                })
                .setNegativeButton(R.string.contTreeCancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();

    }

    // 樹種削除
    private void deleteTree() {

        // 選択状態を取得する
        ListView lv = findViewById(R.id.lvTreeList);
        SparseBooleanArray checked = lv.getCheckedItemPositions();

        // 削除処理
        for (int i = 0; i < treeNames.length; i++) {
            if (checked.get(i)) {
                String whereClause = "treename = " + "'" + treeNames[i] + "'";
                db.delete("treelistTable", whereClause, null);
            }
        }

    }

    // 最初のアクティビティに戻る
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(TreeListActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    // 破棄
    @Override
    protected void onDestroy() {
        super.onDestroy();
        c.close();
        db.close();
    }

}

package com.emuj.timberforestplus;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {

    // DB
    private OwnerOpenHelper helper;
    private SQLiteDatabase db;
    Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // HINT
        String msg = "編集は長押しします。";
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

        // リストビュー取得
        ListView lv = findViewById(R.id.lvOwner);

        // 所有者一覧をDBから読み込む
        readOwnerData();

        //リスト項目が選択された時のイベントを追加
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 選択された項目(ID)
                TextView tv1 = view.findViewById(R.id._id);
                TextView tv2 = view.findViewById(R.id.owner_name);
                TextView tv3 = view.findViewById(R.id.address);
                String _id = tv1.getText().toString();
                String ownername = tv2.getText().toString();
                String address = tv3.getText().toString();
                // インテントのインスタンス
                Intent intent = new Intent(getApplicationContext(), TreeActivity.class);
                // インテントにセット
                intent.putExtra("ownerid", _id);
                intent.putExtra("ownername", ownername);
                intent.putExtra("address", address);
                // インテントを表示
                startActivity(intent);
                // アクティビティを裏側に移す
                finish();
            }
        });

        //リスト項目が長押しされた時のイベントを追加
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 選択された項目(ID)
                TextView textView = view.findViewById(R.id._id);
                String _id = textView.getText().toString();
                // 第2引数は適宜
                Intent intent = new Intent(getApplicationContext(), OwnerActivity.class);
                // インテントにセット
                intent.putExtra("_id", _id);
                startActivity(intent);
                return false;
            }
        });
    }

    // メニュー
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //main.xmlの内容を読み込む
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    // オプションメニューのアイテムが選択されたときに呼び出されるメソッド
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_newbutton:
                // 新規作成
                // インテントのインスタンス
                Intent intent = new Intent(getApplicationContext(), OwnerActivity.class);
                // インテントにセット
                intent.putExtra("_id", "");
                // インテントの表示
                startActivity(intent);
                // アクティビティを閉じる
                finish();
                break;
            case R.id.main_menu_listtree:
                // インテントのインスタンス
                Intent intentTreeList = new Intent(getApplicationContext(), TreeListActivity.class);
                // インテントを表示
                startActivity(intentTreeList);
                // アクティビティを裏側に移す
                finish();
                break;
            case R.id.main_menu_samplebutton:
                // インスタンス生成
                if (helper == null) {
                    helper = new OwnerOpenHelper(getApplicationContext());
                }
                if (db == null) {
                    db = helper.getReadableDatabase();
                }
                ContentValues values = new ContentValues();
                values.put("ownername", "都市 太郎");
                values.put("surveyno", "1");
                values.put("surveyymd", "");
                values.put("pref", "福島県");
                values.put("city", "郡山市");
                values.put("oaza", "");
                values.put("aza", "不動前");
                values.put("tiban", "1-1");
                values.put("timoku", "");
                db.insert("ownerTable", null, values);
                // 所有者一覧をDBから読み込む
                readOwnerData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // DB読み込み
    private void readOwnerData() {

        if (helper == null) {
            helper = new OwnerOpenHelper(getApplicationContext());
        }
        if (db == null) {
            db = helper.getReadableDatabase();
        }

        c = db.query(
                "ownerTable",
                new String[]{"_id", "surveyno", "ownername", "tiban"},
                null,
                null,
                null,
                null,
                null
        );

        // カーソルを先頭に
        c.moveToFirst();

        //表示するカラム名
        String[] from = {"_id", "surveyno", "ownername", "tiban"};
        //バインドするViewリソース
        int[] to = {R.id._id, R.id.owner_surveyNo, R.id.owner_name, R.id.address};
        //adapter生成
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.owner_listviewlayout, c, from, to, 0);
        //bindして表示
        ListView lv = findViewById(R.id.lvOwner);
        lv.setAdapter(adapter);

    }

    // 破棄
    @Override
    protected void onDestroy() {
        super.onDestroy();
        c.close();
        db.close();
    }

}

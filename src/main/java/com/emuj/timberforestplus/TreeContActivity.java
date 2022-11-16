package com.emuj.timberforestplus;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TreeContActivity extends AppCompatActivity {

    private TreeOpenHelper helper;
    private SQLiteDatabase db;
    Cursor c;
    String ownerid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_cont);

        // オブジェクト取得
        TextView tv1 = findViewById(R.id.treeContNo);
        TextView tv2 = findViewById(R.id.treeContName);
        TextView tv3 = findViewById(R.id.treeContSize);
        TextView tv4 = findViewById(R.id.treeContRemark);
        Button updButton = findViewById(R.id.contTreeUpdateButton);
        Button delButton = findViewById(R.id.contTreeDeleteButton);

        // 遷移データ取得
        Intent intent = getIntent();
        ownerid = intent.getStringExtra("ownerid");
        String treeNo = intent.getStringExtra("treeNo");
        String treeName = intent.getStringExtra("treeName");
        String treeSize = intent.getStringExtra("treeSize");
        String treeRemark = intent.getStringExtra("treeRemark");

        // _idの空き番号を取得する
        Integer emptyid = GetEmptyTreeId();

        // NOが空の時は1をセット
        if (treeNo == null) {
            tv1.setText(emptyid.toString());
            updButton.setEnabled(false);
            delButton.setEnabled(false);
        } else {
            // データ反映
            tv1.setText(treeNo);
            tv2.setText(treeName);
            tv3.setText(treeSize);
            tv4.setText(treeRemark);
            updButton.setEnabled(true);
            delButton.setEnabled(true);
        }

        // 読み取り専用（フォーカスが移らないようにする）
        // tv1.setFocusable(false);

    }

    // 空き番号を返す
    private int GetEmptyTreeId() {

        // インスタンス生成
        if(helper == null){
            helper = new TreeOpenHelper(getApplicationContext());
        }
        if(db == null){
            db = helper.getReadableDatabase();
        }

        String whereClause = "ownerid = " + ownerid ;
        c = db.query(
                "treeTable",
                new String[] { "_id" },
                whereClause,
                null,
                null,
                null,
                "_id DESC"
        );
        c.moveToFirst();

        Integer eid = 1;
        for (int i = 0; i < c.getCount(); i++) {
            eid = c.getInt(0) + 1;
            break;
        }
        return eid;

    }

    // 樹種一覧ボタン
    public void treeList(View view) {

//        Intent intent = new Intent(this,TreeListActivity.class);
//        // 遷移先から返却されてくる際の識別コード
//        int requestCode = 1001;
//        // 返却値を考慮したActivityの起動を行う
//        startActivityForResult( intent, requestCode);

        showTreeDialog(TreeContActivity.this);

    }

    // 樹種選択ダイアログ
    private void showTreeDialog(Context context){

        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);

        // リストビュー
        ListView lv = new ListView(context);
        final ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        adapter.add("スギ");
        adapter.add("ヒノキ");
        adapter.add("マツ");
        adapter.add("カエデ");
        adapter.add("クワ");
        adapter.add("サクラ");
        adapter.add("ナラ");
        adapter.add("シデ");
        adapter.add("ミズキ");
        lv.setAdapter(adapter);
        lv.setAdapter(adapter);
        ll.addView(lv);

        // ダイアログ
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(ll);
        dialog.setTitle("樹種選択");

        // 選択時
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> items, View view, int position, long id) {
                String treeName = adapter.getItem(position);
                TextView textView =  findViewById(R.id.treeContName);
                textView.setText(treeName);
                dialog.dismiss();
            }
        });

        // サイズ
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dialogWidth = (int) (metrics.widthPixels * 0.3);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = dialogWidth;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

    }

    // サイズ一覧ボタン
    public void treeSize(View view) {

        showSizeDialog(TreeContActivity.this);

    }

    // サイズ選択ポップアップウィンドウ
    private void showSizeDialog(Context context){

        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);

        // リストビュー
        ListView lv = new ListView(context);
        final ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        for (int i=1; i<=100; i++) {
            adapter.add(Integer.toString(i));
        }
        lv.setAdapter(adapter);
        lv.setAdapter(adapter);
        ll.addView(lv);

        // ダイアログ
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(ll);
        dialog.setTitle("サイズ変更");

        // 選択時
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> items, View view, int position, long id) {
                String treesize = adapter.getItem(position);
                // オブジェクト取得
                TextView textView =  findViewById(R.id.treeContSize);
                textView.setText(treesize);
                dialog.dismiss();
            }
        });

        // サイズ
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dialogWidth = (int) (metrics.widthPixels * 0.3);
        int dialogHeight = (int) (metrics.heightPixels * 0.8);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = dialogWidth;
        lp.height = dialogHeight;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

    }

    // 遷移先からの返却値
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        // startActivityForResult()の際に指定した識別コードとの比較
//        if( requestCode == 1001 ){
//            // 返却結果ステータスとの比較
//            if( resultCode == Activity.RESULT_OK ){
//                // 返却されてきたintentから値を取り出す
//                String tn = data.getStringExtra( "treeName" );
//                // オブジェクト取得
//                TextView textView =  findViewById(R.id.treeContName);
//                textView.setText(tn);
//            }
//        }
//    }

    // 更新ボタン
    public void contTreeUpdate(View view) {

        // インスタンス生成
        if(helper == null){
            helper = new TreeOpenHelper(getApplicationContext());
        }
        if(db == null){
            db = helper.getReadableDatabase();
        }

        // ビュー取得
        TextView tv1 = findViewById(R.id.treeContNo);
        TextView tv2 = findViewById(R.id.treeContName);
        TextView tv3 = findViewById(R.id.treeContSize);
        TextView tv4 = findViewById(R.id.treeContRemark);

        // 値を取得する
        String tree_no = tv1.getText().toString();
        String tree_name = tv2.getText().toString();
        String tree_size = tv3.getText().toString();
        String tree_remark = tv4.getText().toString();
        ContentValues cv = new ContentValues();
        cv.put("treename", tree_name);
        cv.put("treesize", tree_size);
        cv.put("treeremark", tree_remark);

        String whereClause = "ownerid = " + ownerid +  " AND _id = " + tree_no ;

        try {
            db.update("treeTable", cv, whereClause, null);
            // インテント
            Intent intent = new Intent(getApplicationContext(), TreeActivity.class);
            // インテントにセット
            intent.putExtra("ownerid", ownerid);
            // インテントの表示
            startActivity(intent);
            // アクティビティを閉じる
            finish();
        } finally {
            db.close();
        }

    }

    // 削除ボタン
    public void contTreeDelete(View view) {

        // インスタンス生成
        if (helper == null) {
            helper = new TreeOpenHelper(getApplicationContext());
        }
        if (db == null) {
            db = helper.getWritableDatabase();
        }

        // ビュー取得
        TextView tv1 = findViewById(R.id.treeContNo);
        // 値を取得する
        String tree_no = tv1.getText().toString();

        try {
            String whereClause = "ownerid = " + ownerid + " AND _id = " + tree_no ;
            db.delete("treeTable", whereClause, null);
        } finally {
//            db.close();
        }

        // 連番の更新
        try {
            db.beginTransaction();
            String sql = "update treeTable set _id = (select count(*) from treeTable b where treeTable.rowid >= b.rowid )";
            db.execSQL(sql);
            db.setTransactionSuccessful();
        }
        catch (Exception e){
            Toast.makeText(TreeContActivity.this, "削除に失敗しました。", Toast.LENGTH_LONG).show();
        } finally {
            // トランザクション終了
            db.endTransaction();
        }

        // インテント
        Intent intent = new Intent(getApplicationContext(), TreeActivity.class);
        // インテントにセット
        intent.putExtra("ownerid", ownerid);
        // インテントの表示
        startActivity(intent);
        // アクティビティを閉じる
        finish();

    }

    // Cancelボタン
    public void contTreeCancel(View view) {
        // インテントのインスタンス
        Intent intent = new Intent(getApplicationContext(), TreeActivity.class);
        // インテントにセット
        intent.putExtra("ownerid", ownerid);
        // インテントの表示
        startActivity(intent);
        // アクティビティを閉じる
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        c.close();
        db.close();
    }

}

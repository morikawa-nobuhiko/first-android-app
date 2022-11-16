package com.emuj.timberforestplus;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class OwnerActivity extends AppCompatActivity {

    // DB
    private OwnerOpenHelper ownerHelper;
    private TreeOpenHelper treeHelper;
    private SQLiteDatabase ownerTable;
    private SQLiteDatabase treeTable;
    String ownerid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);

        // ID取得
        Intent intent = getIntent();
        ownerid = intent.getStringExtra("_id");

        // DB
        if(ownerHelper == null){
            ownerHelper = new OwnerOpenHelper(getApplicationContext());
        }
        if(ownerTable == null){
            ownerTable = ownerHelper.getReadableDatabase();
        }
        if(treeHelper == null){
            treeHelper = new TreeOpenHelper(getApplicationContext());
        }
        if(treeTable == null){
            treeTable = treeHelper.getReadableDatabase();
        }

        // ビュー取得
        TextView tv1 = findViewById(R.id.owner_surveyNo);
        TextView tv2 = findViewById(R.id.owner_ymd);
        TextView tv3 = findViewById(R.id.owner_pref);
        TextView tv4 = findViewById(R.id.owner_city);
        TextView tv5 = findViewById(R.id.owner_oaza);
        TextView tv6 = findViewById(R.id.owner_aza);
        TextView tv7 = findViewById(R.id.owner_tiban);
        TextView tv8 = findViewById(R.id.owner_timoku);
        TextView tv9 = findViewById(R.id.owner_name);

        if (ownerid.equals("")) {
            tv1.setText("");
            tv2.setText("");
            tv3.setText("");
            tv4.setText("");
            tv5.setText("");
            tv6.setText("");
            tv7.setText("");
            tv8.setText("");
            tv9.setText("");
        }else{
            // 検索
            String whereClause = "_id = " + ownerid ;
            Cursor cursor = ownerTable.query(
                    "ownerTable",
                    new String[] { "_id", "surveyno", "surveyymd", "pref", "city", "oaza", "aza", "tiban", "timoku", "ownername" },
                    whereClause,
                    null,
                    null,
                    null,
                    null
            );

            // カーソルを先頭に
            boolean isEof = cursor.moveToFirst();

            while (isEof) {
                tv1.setText(cursor.getString(cursor.getColumnIndex("surveyno")));
                tv2.setText(cursor.getString(cursor.getColumnIndex("surveyymd")));
                tv3.setText(cursor.getString(cursor.getColumnIndex("pref")));
                tv4.setText(cursor.getString(cursor.getColumnIndex("city")));
                tv5.setText(cursor.getString(cursor.getColumnIndex("oaza")));
                tv6.setText(cursor.getString(cursor.getColumnIndex("aza")));
                tv7.setText(cursor.getString(cursor.getColumnIndex("tiban")));
                tv8.setText(cursor.getString(cursor.getColumnIndex("timoku")));
                tv9.setText(cursor.getString(cursor.getColumnIndex("ownername")));
                // 次の行が存在するか確認
                isEof = cursor.moveToNext();
                break;
            }

            // 閉じる
            cursor.close();
        }
    }

    // OKボタン（DB登録 / 更新）
    public void ownerOK(View view) {

        // インスタンス生成
        if(ownerHelper == null){
            ownerHelper = new OwnerOpenHelper(getApplicationContext());
        }
        if(ownerTable == null){
            ownerTable = ownerHelper.getReadableDatabase();
        }

        TextView textView1 = findViewById(R.id.owner_surveyNo);
        TextView textView2 = findViewById(R.id.owner_ymd);
        TextView textView3 = findViewById(R.id.owner_pref);
        TextView textView4 = findViewById(R.id.owner_city);
        TextView textView5 = findViewById(R.id.owner_oaza);
        TextView textView6 = findViewById(R.id.owner_aza);
        TextView textView7 = findViewById(R.id.owner_tiban);
        TextView textView8 = findViewById(R.id.owner_timoku);
        TextView textView9 = findViewById(R.id.owner_name);

        String ownr_sn = textView1.getText().toString();
        String ownr_ymd = textView2.getText().toString();
        String ownr_pref = textView3.getText().toString();
        String ownr_city = textView4.getText().toString();
        String ownr_oaza = textView5.getText().toString();
        String ownr_aza = textView6.getText().toString();
        String ownr_tiban = textView7.getText().toString();
        String ownr_timoku = textView8.getText().toString();
        String ownr_name = textView9.getText().toString();

        ContentValues values = new ContentValues();
        values.put("ownername", ownr_name);
        values.put("surveyno", ownr_sn);
        values.put("surveyymd", ownr_ymd);
        values.put("pref", ownr_pref);
        values.put("city", ownr_city);
        values.put("oaza", ownr_oaza);
        values.put("aza", ownr_aza);
        values.put("tiban", ownr_tiban);
        values.put("timoku", ownr_timoku);

        if (ownerid.equals("")) {
            ownerTable.insert("ownerTable", null, values);
        } else {
            String whereClause = "_id = " + ownerid ;
            ownerTable.update("ownerTable", values, whereClause, null);
        }

        // 親画面に戻る
        Intent intent = new Intent(OwnerActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    // Cancelボタン
    public void ownerCancel(View view) {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    // コピーボタン
    public void ownerCopy(View view) {

        // ビュー取得
        TextView tv1 = findViewById(R.id.owner_surveyNo);
        TextView tv2 = findViewById(R.id.owner_ymd);
        TextView tv3 = findViewById(R.id.owner_pref);
        TextView tv4 = findViewById(R.id.owner_city);
        TextView tv5 = findViewById(R.id.owner_oaza);
        TextView tv6 = findViewById(R.id.owner_aza);
        TextView tv7 = findViewById(R.id.owner_tiban);
        TextView tv8 = findViewById(R.id.owner_timoku);
        TextView tv9 = findViewById(R.id.owner_name);

        String ownr_sn = tv1.getText().toString();
        String ownr_ymd = tv2.getText().toString();
        String ownr_pref = tv3.getText().toString();
        String ownr_city = tv4.getText().toString();
        String ownr_oaza = tv5.getText().toString();
        String ownr_aza = tv6.getText().toString();
        String ownr_tiban = tv7.getText().toString();
        String ownr_timoku = tv8.getText().toString();
        String ownr_name = tv9.getText().toString();

        ContentValues values = new ContentValues();
        values.put("ownername", ownr_name);
        values.put("surveyno", ownr_sn);
        values.put("surveyymd", ownr_ymd);
        values.put("pref", ownr_pref);
        values.put("city", ownr_city);
        values.put("oaza", ownr_oaza);
        values.put("aza", ownr_aza);
        values.put("tiban", ownr_tiban);
        values.put("timoku", ownr_timoku);
        ownerTable.insert("ownerTable", null, values);

        // 親画面に戻る
        Intent intent = new Intent(OwnerActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    // 削除ボタン
    public void ownerDelete(View view) {

        final AlertDialog.Builder exportBuilder = new AlertDialog.Builder(this);
        exportBuilder.setMessage("削除します。よろしいですか？")
                .setTitle("案件の削除")
                // OKボタン
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Boolean treeOK = false;
                        // 樹種一覧の削除
                        treeTable.beginTransaction();
                        try {
                            // 削除
                            String whereClause = "ownerid = " + ownerid;
                            treeTable.delete("treeTable", whereClause, null);
                            //トランザクション成功を宣言。
                            treeTable.setTransactionSuccessful();
                            treeOK = true;
                        }
                        catch (Exception e){
                            treeOK = false;
//                            Toast.makeText(OwnerActivity.this, "削除に失敗しました。", Toast.LENGTH_LONG).show();
                        } finally {
                            // トランザクション終了
                            treeTable.endTransaction();
                        }
                        if (treeOK){
                            // 案件の削除
                            ownerTable.beginTransaction();
                            try {
                                // 削除
                                String whereClause = "_id = " + ownerid;
                                ownerTable.delete("ownerTable", whereClause, null);
                                //トランザクション成功を宣言。
                                ownerTable.setTransactionSuccessful();
                            } finally {
                                // トランザクション終了
                                ownerTable.endTransaction();
                            }
                            // 親画面に戻る
                            dialog.dismiss();
                            Intent intent = new Intent(OwnerActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                // キャンセルボタン
                exportBuilder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        exportBuilder.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ownerTable.close();
        treeTable.close();
    }

}

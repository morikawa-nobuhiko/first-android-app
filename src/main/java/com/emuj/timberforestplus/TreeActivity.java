package com.emuj.timberforestplus;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Random;

public class TreeActivity extends AppCompatActivity {

    private TreeOpenHelper helper;
    private SQLiteDatabase db;
    private TreeListOpenHelper treeListHelper;
    private SQLiteDatabase treeListDB;
    private String[] treeNames;
    private final int REQUEST_PERMISSION = 1000;
    Cursor c;
    String ownerid;
    String ownername;
    String address;
    String popupTreeNo;
    String popupTreeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree);

        // 遷移データ取得
        Intent intent = getIntent();
        ownerid = intent.getStringExtra("ownerid");
        ownername = intent.getStringExtra("ownername");
        address = intent.getStringExtra("address");

//        Toast.makeText(TreeActivity.this, ownername + address, Toast.LENGTH_LONG).show();

        // 樹種一覧をDBから読み込む
        readTreeData();

        // 樹種一覧表示
        if (treeListHelper == null) {
            treeListHelper = new TreeListOpenHelper(getApplicationContext());
        }
        if (treeListDB == null) {
            treeListDB = treeListHelper.getReadableDatabase();
        }
        treeNames = treeListHelper.getTreeList(treeListDB);
        treeListDB.close();

        // リストビュー取得
        ListView lv = findViewById(R.id.lvTree);

        //リスト項目が選択された時のイベントを追加
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // 選択された項目(ID)
                TextView tv1 = view.findViewById(R.id.treeNo);
                TextView tv2 = view.findViewById(R.id.treeName);
                popupTreeNo = tv1.getText().toString();
                popupTreeName = tv2.getText().toString();
//                showTreeDialog();
                showSizeDialog(TreeActivity.this);
            }
        });

        //リスト項目が長押しされた時のイベントを追加
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 選択された項目(ID)
                TextView tv1 = view.findViewById(R.id.treeNo);
                TextView tv2 = view.findViewById(R.id.treeName);
                TextView tv3 = view.findViewById(R.id.treeSize);
                TextView tv4 = view.findViewById(R.id.treeRemark);
                String treeNo = tv1.getText().toString();
                String treeName = tv2.getText().toString();
                String treeSize = tv3.getText().toString();
                String treeRemark = tv4.getText().toString();
                // インテントのインスタンス
                Intent intent = new Intent(getApplicationContext(), TreeContActivity.class);
                // インテントにセットし遷移
                intent.putExtra("ownerid", ownerid);
                intent.putExtra("treeNo", treeNo);
                intent.putExtra("treeName", treeName);
                intent.putExtra("treeSize", treeSize);
                intent.putExtra("treeRemark", treeRemark);
                startActivity(intent);
                // アクティビティを閉じる
                finish();
                return false;
            }
        });

    }

    // メニュー
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // tree.xmlの内容を読み込む
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tree, menu);
        return true;
    }

    // オプションメニューのアイテムが選択されたときに呼び出されるメソッド
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ListView lv = findViewById(R.id.lvTree);
        switch (item.getItemId()) {
            case R.id.tree_menu_export:
                final AlertDialog.Builder exportBuilder = new AlertDialog.Builder(this);
                exportBuilder.setMessage("CSV形式で保存します。よろしいですか？")
                        .setTitle("樹種一覧出力")
                        // OKボタン
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                backupDatabaseCSV("tfp" + ownerid + ownername + address);
                                dialog.dismiss();
                            }
                        });
                // キャンセルボタン
                exportBuilder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                exportBuilder.show();
                break;

            case R.id.tree_menu_newbutton:
                // 樹種選択
                showTreeDialog(this);
                break;
            case R.id.tree_menu_first:
                // 最初
                lv.setSelection(0);
                break;
            case R.id.tree_menu_last:
                // 最後
                lv.setSelection(lv.getCount());
                break;
            case R.id.tree_menu_addmanytree:

                final int treeTestCount = 1000;
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(treeTestCount + "件追加しますか？\n")
                        .setTitle("デバッグ")
                        .setPositiveButton("消去して追加", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // トランザクション開始を宣言
                                db.beginTransaction();
                                try {
                                    // 削除
                                    String whereClause = "ownerid = " + ownerid;
                                    db.delete("treeTable", whereClause, null);
                                    // 追加
                                    Random r = new Random();
                                    for (int i = 1; i <= treeTestCount; i++) {
                                        int n = r.nextInt(30) + 1;
                                        addTree("サクラ" + i, Integer.toString(n));
                                    }
                                    //トランザクション成功を宣言。
                                    db.setTransactionSuccessful();
                                } finally {
                                    // トランザクション終了
                                    db.endTransaction();
                                    // 樹種一覧をDBから読み込む
                                    readTreeData();
                                    dialog.dismiss();
                                }
                            }
                        });

                // ボタン作成
                builder.setNegativeButton("そのまま追加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // トランザクション開始を宣言
                        db.beginTransaction();
                        try {
                            // 追加
                            Random r = new Random();
                            for (int i = 1; i <= treeTestCount; i++) {
                                int n = r.nextInt(30) + 1;
                                addTree("サクラ" + i, Integer.toString(n));
                            }
                            //トランザクション成功を宣言。
                            db.setTransactionSuccessful();
                        } finally {
                            // トランザクション終了
                            db.endTransaction();
                            // 樹種一覧をDBから読み込む
                            readTreeData();
                            dialog.dismiss();
                        }
                    }
                });

                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // DB読み込み
    private void readTreeData() {

        if (helper == null) {
            helper = new TreeOpenHelper(getApplicationContext());
        }
        if (db == null) {
            db = helper.getReadableDatabase();
        }

        // 選択条件
        String whereClause = "ownerid = " + ownerid;

        c = db.query(
                "treeTable",
                new String[]{"_id", "treename", "treesize", "treeremark"},
                whereClause,
                null,
                null,
                null,
                null
        );

        // カーソルを先頭に
        c.moveToFirst();

        //表示するカラム名
        String[] from = {"_id", "treename", "treesize", "treeremark"};
        //バインドするViewリソース
        int[] to = {R.id.treeNo, R.id.treeName, R.id.treeSize, R.id.treeRemark};
        //adapter生成
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.tree_listviewlayout, c, from, to, 0);
        //bindして表示
        ListView lv = findViewById(R.id.lvTree);
        lv.setAdapter(adapter);

    }

    // 樹種選択ダイアログ
    private void showTreeDialog(Context context) {

        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);

        // リストビュー
        ListView lv = new ListView(context);
        final ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        for (int i = 0; i <= treeNames.length - 1; i++) {
            adapter.add(treeNames[i]);
        }
//        adapter.add("スギ");
//        adapter.add("ヒノキ");
//        adapter.add("マツ");
//        adapter.add("カエデ");
//        adapter.add("クワ");
//        adapter.add("サクラ");
//        adapter.add("ナラ");
//        adapter.add("シデ");
//        adapter.add("ミズキ");
        lv.setAdapter(adapter);
        ll.addView(lv);

        // ダイアログ
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(ll);
        dialog.setTitle("樹種選択");

        // 選択時
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> items, View view, int position, long id) {
//                Toast.makeText(TreeActivity.this, adapter.getItem(position), Toast.LENGTH_LONG).show();
                popupTreeName = adapter.getItem(position);
                dialog.dismiss();
                showSizeDialog(TreeActivity.this);
            }
        });

        // サイズ
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dialogWidth = (int) (metrics.widthPixels * 0.4);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = dialogWidth;
//        lp.gravity = Gravity.TOP | Gravity.RIGHT;
//        lp.y = 100;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

    }

    // サイズ選択ポップアップウィンドウ
    private void showSizeDialog(Context context) {

        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);

        // リストビュー
        ListView lv = new ListView(context);
        final ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        for (int i = 1; i <= 100; i++) {
            adapter.add(Integer.toString(i));
        }
        lv.setAdapter(adapter);
        ll.addView(lv);

        // ダイアログ
        Dialog dialog = new Dialog(context);
        dialog.setContentView(ll);
        dialog.setTitle(popupTreeName);

        // 選択時
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> items, View view, int position, long id) {
                if (0 < position) {
                    String treesize = adapter.getItem(position);
                    // 行追加
                    if (popupTreeName != null) {
                        addTree(popupTreeName, treesize);
                    } else {
                        addTree("", "");
                    }
                    // 樹種一覧をDBから読み込む
                    readTreeData();
                    // リストビューの一番したにスクロール
                    ListView lv = findViewById(R.id.lvTree);
                    lv.setSelection(lv.getCount());
                }
            }
        });

        //dialogに閉じるイベントをセット
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                showTreeDialog(TreeActivity.this);
            }
        });

        // サイズ
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dialogWidth = (int) (metrics.widthPixels * 0.4);
        int dialogHeight = (int) (metrics.heightPixels * 0.8);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = dialogWidth;
        lp.height = dialogHeight;
        dialog.getWindow().setAttributes(lp);
        dialog.setTitle(popupTreeName);
        dialog.show();

    }

    // 行の追加
    private String addTree(String treename, String treesize) {

        // 選択条件
        String whereClause = "ownerid = " + ownerid;
        c = db.query(
                "treeTable",
                new String[]{"ownerid", "_id"},
                whereClause,
                null,
                null,
                null,
                null
        );

        // _idの空き番号を取得する
        String emptyid = getEmptyTreeId();

        // 樹種を追加
        ContentValues values = new ContentValues();
        values.put("ownerid", ownerid);
        values.put("_id", emptyid);
        values.put("treename", treename);
        values.put("treesize", treesize);
        values.put("treeremark", "");

        // DB追加
        db.insert("treeTable", null, values);

        return emptyid;

    }

    // 全樹種の削除
    private void deleteAllTree() {

        // トランザクション開始を宣言
        db.beginTransaction();

        try {
            // 削除
            String whereClause = "ownerid = " + ownerid;
            db.delete("treeTable", whereClause, null);
            //トランザクション成功を宣言。
            db.setTransactionSuccessful();
        } finally {
            // トランザクション終了
            db.endTransaction();
        }

    }

    // 空き番号を返す
    private String getEmptyTreeId() {

        // インスタンス生成
        if (helper == null) {
            helper = new TreeOpenHelper(getApplicationContext());
        }
        if (db == null) {
            db = helper.getReadableDatabase();
        }

        String whereClause = "ownerid = " + ownerid;
        c = db.query(
                "treeTable",
                new String[]{"_id"},
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
        return eid.toString();

    }

    // 最初のアクティビティに戻る
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(TreeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    // CSV出力
    private Boolean backupDatabaseCSV(String outFileName) {

        // Android 6, API 23以上でパーミッシンの確認
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission() == false) {
                return false;
            }
        }

        // ファイルパスの取得
        int inc = 1;
        String ext = "txt";
        String filename = outFileName;
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/Download/" + outFileName + "(" + inc + ")." + ext;

        // ファイル名の取得
        File fp = new File(filePath);
        boolean found = fp.exists();
        if (found) {
            while (found == true) {
                inc += 1;
                filePath = Environment.getExternalStorageDirectory().getPath() + "/Download/" + outFileName + "(" + inc + ")." + ext;
                fp = new File(filePath);
                found = fp.exists();
            }
        }
        filePath = Environment.getExternalStorageDirectory().getPath() + "/Download/" + outFileName + "(" + inc + ")." + ext;

        // 現在ストレージが書き込みできるかチェック
        if (isExternalStorageWritable()) {
            File file = new File(filePath);
            try (FileOutputStream fileOutputStream = new FileOutputStream(file, true);
//                 OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
                 OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "SHIFT-JIS");
                 BufferedWriter bw = new BufferedWriter(outputStreamWriter);
            ) {
                // 検索
                String whereClause = "ownerid = " + ownerid;
                c = db.query(
                        "treeTable",
                        new String[]{"_id", "treename", "treesize", "treeremark"},
                        whereClause,
                        null,
                        null,
                        null,
                        null
                );
                // カーソルを先頭に
                c.moveToFirst();
                // 書き込み
                String csvValues = "";
                if (c != null) {
                    do {
                        csvValues = c.getString(0)
                                + ",";
                        csvValues += c.getString(1)
                                + ",";
                        csvValues += c.getString(2)
                                + ",";
                        csvValues += c.getString(3)
                                + "\n";
                        bw.write(csvValues);
                    } while (c.moveToNext());
//                    c.close();
                }
                bw.flush();
                Toast.makeText(TreeActivity.this, filePath + "に出力しました。", Toast.LENGTH_LONG).show();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    // permissionの確認
    public Boolean checkPermission() {
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        // 拒否していた場合
        else {
            requestLocationPermission();
            return false;
        }
    }

    // 許可を求める
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(TreeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);

        } else {
            Toast toast = Toast.makeText(this, "アプリ実行に許可が必要です", Toast.LENGTH_SHORT);
            toast.show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, REQUEST_PERMISSION);
        }
    }

    // 結果の受け取り
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_PERMISSION) {
//            // 使用が許可された
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                setUpReadWriteExternalStorage();
//            } else {
//                // それでも拒否された時の対応
//                Toast toast = Toast.makeText(this, "何もできません", Toast.LENGTH_SHORT);
//                toast.show();
//            }
//        }
//    }

    // 破棄
    @Override
    protected void onDestroy() {
        super.onDestroy();
        c.close();
        db.close();
    }

}

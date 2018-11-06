package jp.techacademy.mitsuru.yamaura.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.MediaStore;
//import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Timer mTimer;

    //  タイマー用の時間のための変数
    double mTimerSec = 0.0f;

    Handler mHandler = new Handler();

    //  メンバ変数でcursor1を宣言し、ここにGetContentInfo()内でcursorの内容を代入する
    Cursor cursor1 = null;

    Button mNextButton;
    Button mStartButton;
    Button mPrevButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNextButton = (Button) findViewById(R.id.next_button);
        mStartButton = (Button) findViewById(R.id.start_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);

        //  Andorid6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //  パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //  許可されている。呼び出し
                getContentsInfo();

            } else {
                //  許可されていないので許可ダイアログを表示する
                Toast.makeText(this, "パーミッションを許可してください", Toast.LENGTH_SHORT).show();
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            //  Andorid5.0以下
        } else {
            getContentsInfo();
        }

        //  再生と停止の処理
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  タイマーのカウントが0なら
                if (mTimer == null) {

                    //  タイマーの作成
                    mTimer = new Timer();

                    //  タイマースタート
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mTimerSec += 0.1;

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mStartButton.setText("停止");
                                    mNextButton.setEnabled(false);
                                    mPrevButton.setEnabled(false);

                                    //  タイマーが２秒を超えたら
                                    if (mTimerSec > 2.0f) {

                                        //  カーソルの位置を１つ進める関数を実行する
                                        if (cursor1.moveToNext()) {

                                            //  indexからIDを取得し、そのIDから画像のURIを取得する
                                            int fieldIndex = cursor1.getColumnIndex(MediaStore.Images.Media._ID);
                                            Long id = cursor1.getLong(fieldIndex);
                                            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                                            ImageView imageView = (ImageView) findViewById(R.id.imageView);
                                            imageView.setImageURI(imageUri);

                                            mTimerSec = 0;

                                        } else {
                                            //  もしも最後の画像まで戻ったら、カーソルを最初に戻す関数を実行する
                                            cursor1.moveToFirst();

                                            //  indexからIDを取得し、そのIDから画像のURIを取得する
                                            //  中身はmoveToStart内のものと同じ
                                            int fieldIndex = cursor1.getColumnIndex(MediaStore.Images.Media._ID);
                                            Long id = cursor1.getLong(fieldIndex);
                                            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                                            ImageView imageView = (ImageView) findViewById(R.id.imageView);
                                            imageView.setImageURI(imageUri);

                                            mTimerSec = 0;
                                        }
                                    }
                                }
                            });
                        }
                        //  +0.1秒後に+0.1する
                    }, 100, 100);

                } else {
                    mTimer.cancel();
                    mTimer = null;
                    mStartButton.setText("再生");
                    mNextButton.setEnabled(true);
                    mPrevButton.setEnabled(true);
                }
                
            }
        });

        //  Next（進む）ボタンを押した際の処理
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  カーソルの位置を１つ進める関数を実行する
                if(cursor1.moveToNext()){

                    //  indexからIDを取得し、そのIDから画像のURIを取得する
                    int fieldIndex = cursor1.getColumnIndex(MediaStore.Images.Media._ID);
                    Long id = cursor1.getLong(fieldIndex);
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                    imageView.setImageURI(imageUri);
                } else {
                    //  もしも最後の画像まで戻ったら、カーソルを最初に戻す関数を実行する
                    //  これを入れないとfalseが戻ってしまい、エラーになる
                    cursor1.moveToFirst();

                    //  indexからIDを取得し、そのIDから画像のURIを取得する
                    //  中身はmoveToStart内のものと同じ
                    int fieldIndex = cursor1.getColumnIndex(MediaStore.Images.Media._ID);
                    Long id = cursor1.getLong(fieldIndex);
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                    imageView.setImageURI(imageUri);
                }
            }
        });

        //  Previous（戻る）ボタンを押した際の処理
        mPrevButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  カーソルの位置を１つ戻す関数を実行する
                        if(cursor1.moveToPrevious()){

                            //  indexからIDを取得し、そのIDから画像のURIを取得する
                            //  中身はmoveToStart内のものと同じ
                            int fieldIndex = cursor1.getColumnIndex(MediaStore.Images.Media._ID);
                            Long id = cursor1.getLong(fieldIndex);
                            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                            //  画像を表示する
                            ImageView imageView = (ImageView) findViewById(R.id.imageView);
                            imageView.setImageURI(imageUri);

                            //  もしも最初の画像まで戻ったら、カーソルを最後に戻す関数を実行する
                        } else {
                            //  メソッドを実行
                            cursor1.moveToLast();

                            //  indexからIDを取得し、そのIDから画像のURIを取得する
                            //  中身はmoveToStart内のものと同じ
                            int fieldIndex = cursor1.getColumnIndex(MediaStore.Images.Media._ID);
                            Long id = cursor1.getLong(fieldIndex);
                            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                            ImageView imageView = (ImageView) findViewById(R.id.imageView);
                            imageView.setImageURI(imageUri);
                        }
                    }
        });
    }



    //  パーミッションの選択結果の受け取りメソッド
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult){
        switch (requestCode){
            case PERMISSIONS_REQUEST_CODE:
                if(grantResult[0] == PackageManager.PERMISSION_GRANTED){
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }

    //  画像の取得処理のメソッド。最初だけ扱う関数
    private void getContentsInfo() {

        ContentResolver resolver = getContentResolver();

        //  cursorではなく、メンバで宣言したcursor1に代入する。以下のcursorもcursor1に変更
        //  このcursor1がカーソルの位置になる
        cursor1 = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,//  データの種類
                null,    //  項目（null=全項目）
                null,     //  フィルタ条件（null=フィルタなし）
                null,  //  フィルタ用パラメータ
                null     //  ソート（null=ソートなし）
        );

        //  一番最初の画像にカーソルを置く関数を実行
        if (cursor1.moveToFirst()) {
                //  indexからIDを取得し、そのIDから画像のURIを取得する
                int fieldIndex = cursor1.getColumnIndex(MediaStore.Images.Media._ID);
                Long id = cursor1.getLong(fieldIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageURI(imageUri);
        }
        //cursor.close();
    }
}

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

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    //Timer mTimer;
    double mTimerSec = 0.0f;

    //Handler mHandler = new Handler;

    Cursor cursor1 = null;

    Button mNextButton;
    Button mStartButton;
    Button mPrevButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(this);

        mStartButton = (Button) findViewById(R.id.start_button);
        mStartButton.setOnClickListener(this);

        mPrevButton = (Button) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(this);


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

        @Override
        public void onClick (View v){
            //  タップをした時の処理。ボタンで分岐
            if (cursor1 == null) {
                //  画像の情報を取得する
                ContentResolver resolver = getContentResolver();
                Cursor cursor = resolver.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,//  データの種類
                        null,    //  項目（null=全項目）
                        null,     //  フィルタ条件（null=フィルタなし）
                        null,  //  フィルタ用パラメータ
                        null     //  ソート（null=ソートなし）
                );
            }
            if (v.getID() == R.id.mStartButton) {

                //  Timerを使用して画像を時間で再生する。
                //  また、その間は戻ると進むをタップできないようにする
                getContentsInfo();

            } else if (v.getID() == R.id.mNextButton) {
                //if (cursor1.moveToNext()) {
                    //  画像を取得する
                //}

            } else if (v.getID() == R.id.mPrevButton) {
                //if (cursor1.moveToPrevious()) {
                    //  前の画像を取得する
                //}
            }
        }
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

    //  画像の取得処理のメソッド
    private void getContentsInfo() {

        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,//  データの種類
                null,    //  項目（null=全項目）
                null,     //  フィルタ条件（null=フィルタなし）
                null,  //  フィルタ用パラメータ
                null     //  ソート（null=ソートなし）
        );


        if (cursor.moveToFirst()) {
                //  indexからIDを取得し、そのIDから画像のURIを取得する
                int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                Long id = cursor.getLong(fieldIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageURI(imageUri);
        }
        cursor.close();
    }
}

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

import java.Timer;
import java.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    //Timer mtimer;
    double mTimerSec = 0.0f;

    //Handler mHandler = new Handler;

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
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //  パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //  許可されている。呼び出し
                getContentsInfo();

            } else {
                //  許可されていないので許可ダイアログを表示する
                Toast.makeText(this,"パーミッションを許可してください", Toast.LENGTH_SHORT).show();
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
        //  Andorid5.0以下
        }else{
            getContentsInfo();
        }
    }

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

    private void getContentsInfo(){

        //  画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,//  データの種類
                null,
                null,
                null,
                null
        );

        if(cursor.moveToFirst()) {
            //  indexからIDを取得し、そのIDから画像のURIを取得する
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageURI(imageUri);
        } while(cursor.moveToNext());

    }
    cursor.close();
}

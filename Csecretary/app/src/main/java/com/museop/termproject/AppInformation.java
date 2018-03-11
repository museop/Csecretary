package com.museop.termproject;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
/*
 * 앱 정보를 res/raw/appinfo.txt로 부터 가져와 보여준다.
 */
public class AppInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_information);

        TextView textView = (TextView) findViewById( R.id.app_into_tv );

        String memoData = getStringOfTextfile( R.raw.appinfo );
        textView.setText( memoData );

    }

    public String getStringOfTextfile(int resourceID) {
        InputStream inputStream = getResources().openRawResource(resourceID);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
            return new String(byteArrayOutputStream.toByteArray(),"UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void onClick(View v) {
        Uri uri = Uri.parse("mailto:kms920806@naver.com");
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        startActivity(it);
    }
}

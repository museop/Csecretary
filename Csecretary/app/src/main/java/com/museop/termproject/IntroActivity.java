package com.museop.termproject;

        import android.app.Activity;
        import android.content.Intent;
        import android.os.Handler;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
/*
 * 애플리케이션의 시작 시 잠시 나타나는 액티비티이다.
 */
public class IntroActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent( IntroActivity.this, MainActivity.class );
                startActivity( intent );
                finish();
            }
        }, 2000);
    }
}

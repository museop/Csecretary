package com.museop.termproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/*
 * 서버와의 연결을 통해 원하는 게시판들의 업데이트가 발생했는지 확인한다.
 * 업데이트가 되었을 경우, 관련 DB들을 업데이트하고 사용자에게 알림을 보낸다.
 */
public class AlarmReceiver extends BroadcastReceiver {

    // 알림 매니저, 알림 작업
    private NotificationManager notificationManager;
    private Notification.Builder notification;

    // PendingIntent
    private PendingIntent pendingIntent;

    // 비동기 Task
    private JSONReceiveTask jsonReceiveTask = null;

    // DB연동을 위한 BoardDBManager 매니저
    public BoardDBManager mDbManager = null;

    // 프레퍼런스
    public SharedPreferences mPref = null;

    // Key (DB column 및 프레퍼런스 연동)
    public String[] keys = null;

    // Context
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("AlarmReceiver", "onReceive()");

        // 관련 리소스들을 초기화한다.
        this.context = context;

        notification = new Notification.Builder( context );
        notificationManager = (NotificationManager) context.getSystemService( Context.NOTIFICATION_SERVICE );

        mDbManager = BoardDBManager.getInstance(context);

        mPref = PreferenceManager.getDefaultSharedPreferences(context);
        keys = context.getResources().getStringArray(R.array.key_list);

        // 서버와의 통신은 비동기 처리한다.
        jsonReceiveTask = new JSONReceiveTask();
        jsonReceiveTask.execute( "ToServer" );
    }

    // 서버와 통신할 AsyncTask
    private class JSONReceiveTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            // HttpURLConnection을 이용하여 서버와 POST방식으로 연동한다.
            HttpURLConnection connection;
            OutputStream outputStream;
            InputStream inputStream;
            ByteArrayOutputStream byteArrayOutputStream;

            String response = null;
            try {
                // update json 문자열을 받을 서버 URL
                URL url = new URL("http://52.78.20.89/csecretary/updateview");

                // 서버와 연결한다.
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(30000);
                connection.setReadTimeout(30000);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Cache-Control", "no-cache");
                connection.setRequestProperty("Accept", "application/json");

                outputStream = connection.getOutputStream();
                outputStream.write(params[0].getBytes());   // "ToServer"
                outputStream.flush();

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream = connection.getInputStream();
                    byteArrayOutputStream = new ByteArrayOutputStream();

                    byte[] byteBuffer = new byte[1024];
                    byte[] byteData;

                    // 서버로부터 json 문자열을 받는다.
                    int length;
                    while ((length = inputStream.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                        byteArrayOutputStream.write(byteBuffer, 0, length);
                    }
                    byteData = byteArrayOutputStream.toByteArray();
                    response = new String(byteData);

                }
            } catch (Exception e) {
                Log.d("AlarmReceiver", "doInBackground()");
            }

            // 받은 문자열을 onPostExecute로 전달한다.
            return response;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            if (jsonString != null) {
                try {
                    // 업데이트를 체크한다.
                    updateCheck( jsonString );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // jsonString이 null일 경우 서버와의 통신에 문제가 있음.
                Toast.makeText(context, "[CSECRETARY] 서버와의 연결에 문제가 있습니다.", Toast.LENGTH_LONG).show();
            }

        }

        // 실인수로 받은 jsonString과 프레퍼런스 정보, DB 정보를 기반으로 업데이트가 발생했는지 체크한다.
        private void updateCheck(String jsonString) throws JSONException {

            // 받은 문자열로부터 JSON 객체를 생성한다.
            JSONObject jObject = new JSONObject( jsonString );

            for (int i = 0; i < keys.length; i++) {

                // 설정창에서 체크한 프레퍼런스들만 업데이트 날짜를 비교한다.
                if ( mPref.getBoolean( keys[i], false ) ) {
                    String[] dbColumns = new String[] { "_id", "key", "subject", "professor", "last_update", "type" };

                    // DB에 있는 업데이트 날짜를 구한다. (id, subject, professor, type 정보도 같이 구한다)
                    int dbUpdate = 99999999;
                    int id = -1;                 // professor, type의 초기화
                    String subject = "NONE";
                    String professor = "NONE";
                    int type = -1;
                    Cursor c = mDbManager.query( dbColumns, "key=?", new String[] { keys[i] }, null, null, null );
                    if ( c != null && c.getCount() != 0 ) {
                        c.moveToNext();
                        id = c.getInt(0);
                        subject = c.getString(2);
                        professor = c.getString(3);
                        dbUpdate = Integer.parseInt( c.getString(4) );
                        type = c.getInt(5);
                    }

                    // 서버가 보낸 업데이트 날짜를 구한다.
                    int serverUpdate = Integer.parseInt( jObject.getString( keys[i] ) );

                    // 업데이트가 되었다면 알림 메시지 전송 및 DB 갱신
                    if ( dbUpdate < serverUpdate ) {
                        Log.d("AlarmReceiver", "업데이트 발생");

                        // 알림을 준다.
                        notifyBoardUpdate(professor, subject, id, type);

                        // TagDB 갱신
                        updateTagDB(professor);

                        // BoardDB 갱신
                        ContentValues updateRowValue = new ContentValues();
                        updateRowValue.put( "last_update", String.valueOf( serverUpdate ) );
                        mDbManager.update( updateRowValue, "key='" + keys[i] + "'", null );

                    }
                }
            }

        }

        // TagDB에서 해당 태그(professor)
        private void updateTagDB(String professor) {
            TagDBManager tagDBManager = TagDBManager.getInstance( context );

            ContentValues updateRowValue = new ContentValues();
            updateRowValue.put( "isUpdate", "new!" );
            tagDBManager.update( updateRowValue, "Tag='" + professor + "'", null );
        }

        // 인수로 받은 정보들로 알림을 보낸다.
        private void notifyBoardUpdate(String professor, String subject, int id, int type) {
            // 알림 내용 클릭 시 이동할 액티비티를 위한 인텐트
            Intent intent = new Intent();
            intent.setClass(context, NoticeBoardActivity.class);
            intent.putExtra("TAG", professor);
            intent.putExtra("SUBJECT", subject);
            intent.putExtra("TYPE", type);

            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );

            notification.setSmallIcon(R.drawable.ic_cse);
            notification.setContentTitle("[CSECRETARY] 가 알려드립니다!");
            notification.setContentText("'" + subject + "' 게시판이 업데이트 되었습니다.");
            notification.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
            notification.setContentIntent(pendingIntent);
            notification.setAutoCancel(true);

            // 알림을 보낸다.
            notificationManager.notify(7777 + id, notification.build());
        }
    }
}

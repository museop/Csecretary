package com.museop.termproject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
/*
 * 각 태그별 통합 게시판을 제공한다.
 */
public class NoticeBoardActivity extends AppCompatActivity {

    private String[]                boardList       = null; // 스피너에 담길 게시판 항목들
    private String[]                urlList         = null; // 파싱할 주소들
    private String[]                selectorList    = null; // 게시글 태그의 선택자들
    private int[]                   boardTypeList   = null; // 게시글 타입

    private ArrayAdapter<String>    spAdapter       = null; // 스피너에 설정할 어댑터
    private Spinner                 mSpinner        = null; // 스피너

    private ArrayList<ParsingData>  mData           = null; // 어댑터에 저장할 목록
    private ListView                mListView       = null; // 리스트뷰
    private ParsingAdapter          mAdapter        = null; // 리스트뷰에 설정할 어댑터

    private ProgressDialog          progressDialog  = null; // 진행바
    private int                     currentType     = 0;    // 게시판 타입

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);

        // 데이터 설정을 위해 인텐트를 받는다.
        Intent intent = getIntent();
        String receivedTag = intent.getStringExtra("TAG");
        String receivedSubject = intent.getStringExtra("SUBJECT");
        int receivedType = intent.getIntExtra("TYPE", -1);

        // TagDB에서 해당 태그를 업데이트한다(new! 표시 제거)
        updateTagDB(receivedTag);

        // Actionbar title을 태그명으로 설정한다.
        if (receivedTag != null) {
            getSupportActionBar().setTitle(receivedTag);
        }

        // 스피너 관련 리소스를 설정한다.
        spinnerSetting(receivedType, receivedSubject);

        // 리스트뷰 관련 리소스를 설정한다.
        listViewSetting();

    }

    // 파싱 작업을 비동기 처리할 AsyncTask
    private class ParsingAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // ProgressDialog 시작
            progressDialog = ProgressDialog.show(NoticeBoardActivity.this, "게시글 가져오기", "게시글을 가져오는 중입니다.", true, false);
        }

        @Override
        protected Void doInBackground(String... parsingInfo) {

            // 게시판을 선택할 때마다 초기화
            mData.clear();
            try {
                Document document = Jsoup.connect(parsingInfo[0]).timeout(20000).get(); // parsingInfo[0]: 해당 게시판 전체 URL
                Elements contents = document.select(parsingInfo[1]);                    // parsingInfo[1]: 게시글 선택자

                String title;
                String link;
                for (Element e : contents) {
                    title = e.text();
                    // 타입 별 처리 작업
                    switch (currentType) {
                        case 1:
                        case 4:
                        case 5:
                        case 10:
                            link = e.attr("href");
                            break;
                        default:
                            link = parsingInfo[0];
                    }

                    ParsingData data = new ParsingData(title, link);
                    mData.add(data);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // ListView 갱신
            mAdapter.notifyDataSetChanged();
            // ProgressDialog 끝
            progressDialog.dismiss();
        }
    }

    // 네트워크 연결 여부를 확인한다.
    private Boolean isNetWorkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isMobileAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable();
        boolean isMobileConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        boolean isWifiAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
        boolean isWifiConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

        if ((isWifiAvailable && isWifiConnect) || (isMobileAvailable && isMobileConnect)) {
            return true;
        } else {
            return false;
        }
    }

    // 스피너를 설정한다.
    private void spinnerSetting(int type, String subject) {
        String[] totalBoards    = getResources().getStringArray( R.array.board_list );      // 총 게시판 목록
        String[] totalUrls      = getResources().getStringArray( R.array.url_list );        // 총 URL 목록
        String[] totalSelectors = getResources().getStringArray( R.array.selector_list );   // 총 선택자 목록
        int[] totalTypes        = getResources().getIntArray( R.array.type_list );          // 총 게시판 타입 목록

        // type이 -1이면 나의 설정(프레퍼런스)을 기반으로 스피너 데이터를 설정한다.
        if (type == -1) {
            String[] keys = getResources().getStringArray( R.array.key_list );  // 프레퍼런스 키 목록
            SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);

            // cnt : 설정창에서 체크한 프레퍼런스들의 개수
            int cnt = 0;
            for (int i = 0; i < keys.length; i++) {
                if ( mPref.getBoolean( keys[i], false ) ) {
                    cnt++;
                }
            }

            boardList = new String[cnt];
            urlList = new String[cnt];
            selectorList = new String[cnt];
            boardTypeList = new int[cnt];

            int k = 0;
            for (int i = 0; i < keys.length; i++) {
                // 프레퍼런스 값이 true인 것만 넣는다.
                if ( mPref.getBoolean( keys[i], false ) ) {
                    boardList[k] = totalBoards[i];
                    urlList[k] = totalUrls[i];
                    selectorList[k] = totalSelectors[i];
                    boardTypeList[k++] = totalTypes[i];
                }
            }

        } else {
            // cnt : totalTypes 중 값이 type인 것들의 개수
            int cnt = 0;
            for (int i = 0; i < totalTypes.length; i++) {
                if (totalTypes[i] == type) {
                    cnt++;
                }
            }

            boardList = new String[cnt];
            urlList = new String[cnt];
            selectorList = new String[cnt];
            boardTypeList = new int[cnt];

            int k = 0;
            for (int i = 0; i < totalBoards.length; i++) {
                // 타입이 같은 것들만 넣는다.
                if (totalTypes[i] == type) {
                    boardList[k] = totalBoards[i];
                    urlList[k] = totalUrls[i];
                    selectorList[k] = totalSelectors[i];
                    boardTypeList[k++] = totalTypes[i];
                }
            }
        }

        mSpinner = (Spinner) findViewById(R.id.spinner);

        // 스피너에 사용할 어댑터 생성 및 준비된 데이터를 설정한다.
        spAdapter = new ArrayAdapter<>(this, R.layout.spinner_textview, boardList);

        // 스피너에 어댑터를 설정한다.
        mSpinner.setAdapter(spAdapter);

        // 스피너 첫 아이템을 선택한다.
        int startSpinnerItemNumber = getSpinnerItemNumber( subject );
        mSpinner.setSelection(startSpinnerItemNumber);

        // 스피너 아이템 선택시 실행될 로직 선언
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // 인터넷 연결 확인
                if (isNetWorkAvailable()) {
                    // 파싱을 ParsingAsyncTask로 UI스레드와 별도로 처리
                    ParsingAsyncTask parsingAsyncTask = new ParsingAsyncTask();
                    currentType = boardTypeList[position];
                    parsingAsyncTask.execute(urlList[position], selectorList[position]);

                } else {
                    Toast.makeText(NoticeBoardActivity.this, "인터넷 연결에 문제가 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // empty
            }
        });
    }

    // 리스트뷰를 설정한다.
    private void listViewSetting() {
        // 리스트뷰에 설정할 어댑터를 생성 및 데이터 설정
        mData = new ArrayList<>();
        mAdapter = new ParsingAdapter(this, mData);
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter);

        // 리스트 아이템 클릭시 실행될 로직 선언
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // 클릭한 포지션의 데이터를 가져온다.
                ParsingData data = mData.get(position);

                // 가져온 데이터 중 링크 부분만 적출해낸다.
                String link = data.link;

                // 해당 링크로 이동
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
            }
        });
    }

    // TagDB에서 해당 Tag의 isUpdate 항목을 지운다. (" ")
    private void updateTagDB(String tag) {
        TagDBManager tagDBManager = TagDBManager.getInstance( this );

        ContentValues updateRowValue = new ContentValues();
        updateRowValue.put( "isUpdate", " " );
        tagDBManager.update( updateRowValue, "Tag='" + tag + "'", null );
    }

    // 현재 스피너 항목 중 subject의 인덱스를 반환한다. (없으면 0 반환)
    private int getSpinnerItemNumber(String subject) {
        int ret = 0;
        for (int i = 0; i < boardList.length; i++) {
            if (boardList[i].equals(subject)) {
                ret = i;
                break;
            }
        }
        return ret;
    }
}
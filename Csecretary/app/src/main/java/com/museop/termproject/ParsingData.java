package com.museop.termproject;
/*
 * 파싱 관련 정보를 저장한다.
 */
public class ParsingData {

    String title;   // 글 제목
    String link;    // 링크

    ParsingData(String title, String link) {
        this.title  = title;
        this.link   = link;
    }
}

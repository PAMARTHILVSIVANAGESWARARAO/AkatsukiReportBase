package com.akatsuki_news_application.AkatsukiReportBase.source.dto;

public class NewsRequest {

    private String akatsukiMemberName;

    public NewsRequest() {
    }

    public NewsRequest(String akatsukiMemberName) {
        this.akatsukiMemberName = akatsukiMemberName;
    }

    public String getAkatsukiMemberName() {
        return akatsukiMemberName;
    }

    public void setAkatsukiMemberName(String akatsukiMemberName) {
        this.akatsukiMemberName = akatsukiMemberName;
    }
}


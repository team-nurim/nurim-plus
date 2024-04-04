package org.nurim.nurim.domain.entity;

import java.util.List;

public class PolicyData {
    private int page;
    private int perPage;
    private int totalCount;
    private int currentCount;
    private int matchCount;
    private List<UddiData> data;

    // 각 필드에 대한 getter 메서드
    public int getPage() {
        return page;
    }

    public int getPerPage() {
        return perPage;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public int getMatchCount() {
        return matchCount;
    }

    // 각 필드에 대한 setter 메서드 (생략 가능)
    public void setPage(int page) {
        this.page = page;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }

    // 생성자, getter 및 setter 메서드
    public List<UddiData> getData() {
        return data;
    }

    public void setData(List<UddiData> data) {
        this.data = data;
    }
}
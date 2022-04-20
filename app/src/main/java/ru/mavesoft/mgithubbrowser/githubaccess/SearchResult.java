package ru.mavesoft.mgithubbrowser.githubaccess;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResult {

    @SerializedName("total_count")
    private int totalCount;

    @SerializedName("items")
    private List<Repository> repositories;

    public int getTotalCount() {
        return totalCount;
    }

    public List<Repository> getRepositories() {
        return repositories;
    }
}

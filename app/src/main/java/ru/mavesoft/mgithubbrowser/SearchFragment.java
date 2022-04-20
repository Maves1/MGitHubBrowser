package ru.mavesoft.mgithubbrowser;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.mavesoft.mgithubbrowser.githubaccess.GitHubAPI;
import ru.mavesoft.mgithubbrowser.githubaccess.Repository;
import ru.mavesoft.mgithubbrowser.githubaccess.SearchResult;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    EditText editTextRequest;
    Button btnSearch;

    Retrofit retrofit;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View searchFragment = inflater.inflate(R.layout.fragment_search, container, false);

        editTextRequest = searchFragment.findViewById(R.id.editTextRequest);
        btnSearch = searchFragment.findViewById(R.id.btnSearch);

        RepoAdapter repoAdapter = new RepoAdapter();
        RecyclerView recViewSearchResults = searchFragment.findViewById(R.id.recViewSearchResults);

        recViewSearchResults.setLayoutManager(new LinearLayoutManager(searchFragment.getContext()));
        recViewSearchResults.setAdapter(repoAdapter);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String request = editTextRequest.getText().toString();
                if (request.length() > 0) {
                    try {
                        getAndShowSearchResults(request, repoAdapter);
                    } catch (Exception ex) {
                        Toast.makeText(searchFragment.getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(searchFragment.getContext(), "Enter your search request", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return searchFragment;
    }

    private void getAndShowSearchResults(String request, RepoAdapter repoAdapter) {
        int page = 1;
        int per_page = 10;

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GitHubAPI gitHubAPI = retrofit.create(GitHubAPI.class);
        Call<SearchResult> searchResults = gitHubAPI.searchForRepositories(request, page, per_page);
//        Call<List<Repository>> searchResults = gitHubAPI.listReposNoAuth("Maves1");
        searchResults.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                SearchResult results = response.body();
                Log.d("SearchResponse", response.toString());

                repoAdapter.setDataList(results.getRepositories());
                repoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {

            }
        });
    }
}
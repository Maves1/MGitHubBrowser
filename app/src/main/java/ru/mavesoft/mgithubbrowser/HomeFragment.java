package ru.mavesoft.mgithubbrowser;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.mavesoft.mgithubbrowser.auth.Auth;
import ru.mavesoft.mgithubbrowser.githubaccess.GitHubAPI;
import ru.mavesoft.mgithubbrowser.githubaccess.Repository;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    SwipeRefreshLayout swipeRefreshLayout;
    RepoAdapter repoAdapter;
    RecyclerView recViewRepos;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

        View homeFragment = inflater.inflate(R.layout.fragment_home, container, false);

        repoAdapter = new RepoAdapter();
        recViewRepos = homeFragment.findViewById(R.id.recViewSearchResults);
        swipeRefreshLayout = homeFragment.findViewById(R.id.swipeRefreshHome);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);

        recViewRepos.setLayoutManager(new LinearLayoutManager(homeFragment.getContext()));
        recViewRepos.setAdapter(repoAdapter);

        try {
            updateRepositoryView(repoAdapter);
        } catch (Exception ex) {
            Log.d("RepositoryUpdate", ex.getMessage());
        }

        return homeFragment;
    }

    private void updateRepositoryView(RepoAdapter repoAdapter) {


        Retrofit retrofit;

        GitHubAPI service;
        Call<List<Repository>> repoCall;

        if (Auth.getInstance(getContext()).getUser() != null) {
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
            builder.readTimeout(10, TimeUnit.SECONDS);
            builder.connectTimeout(5, TimeUnit.SECONDS);

            builder.addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .addHeader("Authorization", "token "
                                + Auth.getInstance(getContext()).getUser().getToken())
                        .build();
                return chain.proceed(request);
            });
            OkHttpClient client = builder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.github.com/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            service = retrofit.create(GitHubAPI.class);

            repoCall = service.listReposAuth(Auth.getInstance(getContext()).getUser().getToken());
        } else {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.github.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(GitHubAPI.class);

            repoCall = service.listReposNoAuth("Maves1");
        }

        repoCall.enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
                List<Repository> repos = response.body();
                Log.d("Response", response.toString());

                repoAdapter.getLocalRepositories().clear();
                repoAdapter.getLocalRepositories().addAll(repos);
                repoAdapter.notifyDataSetChanged();

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        updateRepositoryView(repoAdapter);
    }
}
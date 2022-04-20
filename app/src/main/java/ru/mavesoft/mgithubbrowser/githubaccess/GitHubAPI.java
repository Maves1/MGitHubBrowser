package ru.mavesoft.mgithubbrowser.githubaccess;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.mavesoft.mgithubbrowser.auth.User;

public interface GitHubAPI {

    @Headers("Accept: application/json")
    @POST("login/oauth/access_token")
    @FormUrlEncoded
    Call<AccessToken> getAccessToken(
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("code") String code
    );

    @GET("users/{user}/repos")
    Call<List<Repository>> listReposNoAuth(@Path("user") String user);

    @GET("user/repos")
    Call<List<Repository>> listReposAuth(@Query("access_token") String access_token);

    @Headers("Accept: application/json")
    @GET("user")
    Call<User> getUser(@Query("access_token") String access_token);

    @GET("search/repositories")
    Call<SearchResult> searchForRepositories(@Query("q") String request,
                                                 @Query("page") int page,
                                                 @Query("per_page") int perPage);

}

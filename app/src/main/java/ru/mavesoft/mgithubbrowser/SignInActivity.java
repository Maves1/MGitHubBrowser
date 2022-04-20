package ru.mavesoft.mgithubbrowser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.mavesoft.mgithubbrowser.auth.User;
import ru.mavesoft.mgithubbrowser.githubaccess.AccessToken;
import ru.mavesoft.mgithubbrowser.githubaccess.GitHubAPI;
import ru.mavesoft.mgithubbrowser.auth.Auth;

public class SignInActivity extends AppCompatActivity {

    private final String clientID = "0c5504aded8e77f9df81";
    private final String clientSecret = "91349deaf7b269f7ae0314589c682d8a311ce908";
    private String callbackUri;

    private Retrofit retrofit;
    private String apiBaseUrl = "https://api.github.com/";
    private String accessTkBaseUrl = "https://github.com/";

    private String accessToken = "";

    Button btnSignIn;
    TextView tvSkipSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        if (isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnSignIn = findViewById(R.id.btnSignIn);
        tvSkipSignIn = findViewById(R.id.tvSkipSignIn);

        callbackUri = getResources().getString(R.string.callback_scheme) + "://" +
                getResources().getString(R.string.callback_host);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String baseAuthUri = "https://github.com/login/oauth/authorize";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseAuthUri +
                        "?client_id=" + clientID +
                        "&redirect_uri=" +
                        callbackUri));
                startActivity(intent);
            }
        });

        tvSkipSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri cbUri = getIntent().getData();

        Log.d("Test", "Before uri");
        if (cbUri != null && cbUri.toString().startsWith(callbackUri)) {
            Log.d("Test", "uri is correct");
            String code = cbUri.getQueryParameter("code");

            retrofit = new Retrofit.Builder()
                    .baseUrl(accessTkBaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            GitHubAPI gitHubAPI = retrofit.create(GitHubAPI.class);
            Call<AccessToken> accessTokenCall = gitHubAPI.getAccessToken(
                    clientID, clientSecret, code
            );

            accessTokenCall.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                    accessToken = response.body().getAccessToken();

                    Auth.getInstance(getApplicationContext()).createAndSaveUser(accessToken);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Toast.makeText(SignInActivity.this, "Couldn't sign in!", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private boolean isLoggedIn() {
        return Auth.getInstance(getApplicationContext()).getUser() != null;
    }
}
package com.example.galilea.gitimporter.processing;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.galilea.gitimporter.R;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedByteArray;

/**
 * Created by galilea on 18.04.2015.
 */
public class GithubService extends IntentService {

    public static final String GITHUB = "https://api.github.com";
    private String owner;
    private String repo;

    public String name = "github";
    final String success = "OK!";
    final String action = "com.example.galilea.action.SUCCESS";

    Response githubResponse;
    RestAdapter restAdapter;
    Converter converter;

    List<CommitPOJOContainer> commits;
    Type listType = new TypeToken<List<CommitPOJOContainer>>(){}.getType();


    private final IBinder myBinder = new LocalBinder();

    public GithubService() {
        super(String.valueOf(R.string.app_name));
    }

    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    private void get() {

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(GITHUB)
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        // Create an instance of our GitHub API interface.
        GitHub github = restAdapter.create(GitHub.class);

        // Fetch and print a list of the contributors to this library.

        githubResponse = github.getResponse(owner, repo);
        JsonParser parser = new JsonParser();
        String res = new String(((TypedByteArray)githubResponse.getBody()).getBytes());
        Log.v("BODY:", res);
        JsonArray o = (JsonArray) parser.parse(res);
        commits = new Gson().fromJson(o, listType);

        for(CommitPOJOContainer c: commits){
            c.setOwner(owner);
            c.setRepo(repo);
        }


        Intent respond = new Intent();
        respond.setAction(action);
        respond.putExtra("result", success);
        sendBroadcast(respond);
    }

    public List<CommitPOJOContainer> returnCommits(){
        return commits;
    }

    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        owner = intent.getStringExtra("owner");
        repo = intent.getStringExtra("repo");
        get();

    }

    public class LocalBinder extends Binder {
        public GithubService getService() {
            return GithubService.this;
        }
    }
}

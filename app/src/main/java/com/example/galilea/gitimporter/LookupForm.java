package com.example.galilea.gitimporter;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.example.galilea.gitimporter.database.CommitsProvider;
import com.example.galilea.gitimporter.processing.CommitPOJOContainer;
import com.example.galilea.gitimporter.processing.GithubService;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@SuppressLint("NewApi")
@EActivity(R.layout.activity_lookup_form)
public class LookupForm extends ActionBarActivity {

    boolean bound = false;
    GithubService github;
    Intent intent;

    String owner;
    String repo;

    ServiceConnection connect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GithubService.LocalBinder binder = (GithubService.LocalBinder) service;
            github = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    final Uri COMMITS_URI = Uri
            .parse("content://com.example.galilea.provider.Commits/commits");


    @Background
    @Receiver(actions = "com.example.galilea.action.SUCCESS")
    public void onSuccess() {
        List<CommitPOJOContainer> com = github.returnCommits();
        ContentResolver rslvr = getContentResolver();
        for(CommitPOJOContainer c: com)
        rslvr.insert(COMMITS_URI, CommitsProvider.prepare(c));

        startActivity(new Intent(getApplicationContext(), CommitListActivity_.class));

    }

    @ViewById(R.id.owner)
    AutoCompleteTextView mOwnerView;

    @ViewById(R.id.repo)
    EditText mRepoView;

    @ViewById(R.id.login_progress)
    View mProgressView;

    @ViewById(R.id.login_form)
    View mLoginFormView;

    @ViewById(R.id.search_button)
    Button mSearchButton;

    @Click(R.id.search_button)
    public void onClick(View view) {
        search();

    }

    public void search() {

        // Reset errors.
        mOwnerView.setError(null);
        mRepoView.setError(null);

        // Store values at the time of the login attempt.
        owner = mOwnerView.getText().toString();
        repo = mRepoView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (!TextUtils.isEmpty(repo) && !isRepoValid(repo)) {
            mRepoView.setError(getString(R.string.error_invalid_password));
            focusView = mRepoView;
            cancel = true;
        }

        if (TextUtils.isEmpty(owner)) {
            mOwnerView.setError(getString(R.string.error_field_required));
            focusView = mOwnerView;
            cancel = true;
        } else if (!isOwnerValid(owner)) {
            mOwnerView.setError(getString(R.string.error_invalid_owner));
            focusView = mOwnerView;
            cancel = true;
        }

        if (cancel) {

            focusView.requestFocus();
        } else {

            intent = new Intent(this, GithubService.class);
            intent.putExtra("owner", owner);
            intent.putExtra("repo", repo);
            startService(intent);
            bindService(intent, connect, BIND_AUTO_CREATE);
            mSearchButton.setEnabled(false);

        }
    }

    private boolean isOwnerValid(String owner) {
        //TODO: Replace this with your own logic
        return true;
    }

    private boolean isRepoValid(String repo) {
        //TODO: Replace this with your own logic
        return true;
    }


    @Override
    protected void onDestroy() {
        unbindService(connect);
        stopService(intent);
        super.onDestroy();
    }
}




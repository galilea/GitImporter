package com.example.galilea.gitimporter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.galilea.gitimporter.database.CommitsProvider;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@EActivity(R.layout.activity_commit_list)
public class CommitListActivity extends ActionBarActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    @ViewById(R.id.recyclerView)
    RecyclerView commitsList;
    RecyclerView.LayoutManager manager;

    @AfterViews
    public void initList(){
        getLoaderManager().initLoader(1, null, this);
        String[] projection = {
                Fields.COMMIT_ID,
                Fields.COMMIT_OWNER,
                Fields.COMMIT_MESSAGE,
                Fields.COMMIT_HASH,
                Fields.COMMIT_DATE};

        manager = new LinearLayoutManager(this);
        commitsList.setLayoutManager(manager);

        commitsAdapter = new CommitListAdapter(this,getContentResolver()
                .query(CommitsProvider.COMMITS_CONTENT_URI, projection, null, null, null) );
        commitsList.setAdapter(commitsAdapter);

    }

    CommitListAdapter commitsAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_commit_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                Fields.COMMIT_ID,
                Fields.COMMIT_OWNER,
                Fields.COMMIT_MESSAGE,
                Fields.COMMIT_HASH,
                Fields.COMMIT_DATE};
        CursorLoader cursorLoader = new CursorLoader(this,
                CommitsProvider.COMMITS_CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        commitsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        commitsAdapter.swapCursor(null);
    }
}

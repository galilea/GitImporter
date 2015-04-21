package com.example.galilea.gitimporter.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.galilea.gitimporter.processing.CommitPOJOContainer;

import org.androidannotations.annotations.EProvider;

/**
 * Created by galilea on 18.04.2015.
 */

@EProvider
public class CommitsProvider extends ContentProvider {

    DBHelper dbHelper;
    SQLiteDatabase db;

    private static final int DB_VERSION = 5;

    private String DB_NAME = "commits.db";

    static final String AUTHORITY = "com.example.galilea.provider.Commits";

    static final String COMMITS_PATH = "commits";

    // Общий Uri
    public static final Uri COMMITS_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + COMMITS_PATH);

    private static final String COMMIT_TABLE = "commits_list";

    //Id, хэш, сообщение, дата, владелец, репозиторий
    static final String COMMIT_ID = "_id";
    static final String COMMIT_HASH = "hash";
    static final String COMMIT_MESSAGE = "message";
    static final String COMMIT_DATE = "date";
    static final String COMMIT_OWNER = "owner";
    static final String COMMIT_REPO = "repo";

    // набор строк
    static final String CONTACT_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + COMMITS_PATH;

    // одна строка
    static final String CONTACT_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + COMMITS_PATH;

    //// UriMatcher
    // общий Uri
    static final int URI_CONTACTS = 1;

    // Uri с указанным ID
    static final int URI_CONTACTS_ID = 2;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, COMMITS_PATH, URI_CONTACTS);
        uriMatcher.addURI(AUTHORITY, COMMITS_PATH + "/#", URI_CONTACTS_ID);
    }

    private static final String DB_CREATE = "create table " + COMMIT_TABLE + "("
            + COMMIT_ID + " integer primary key autoincrement, "
            + COMMIT_OWNER + " text, " + COMMIT_REPO + " text,"
            + COMMIT_DATE + " integer," + COMMIT_MESSAGE + " text,"
            + COMMIT_HASH + " text" +");";


    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
            switch (uriMatcher.match(uri)) {
                case URI_CONTACTS:

                    if (TextUtils.isEmpty(sortOrder)) {
                        sortOrder = COMMIT_ID + " ASC";
                    }
                    break;

                case URI_CONTACTS_ID: // Uri с ID
                    String id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        selection = COMMIT_ID + " = " + id;
                    } else {
                        selection = selection + " AND " + COMMIT_ID + " = " + id;
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Wrong URI: " + uri);
            }
            db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(COMMIT_TABLE, projection, selection,
                    selectionArgs, null, null, sortOrder);
            cursor.setNotificationUri(getContext().getContentResolver(),
                    COMMITS_CONTENT_URI);
            return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != URI_CONTACTS)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(COMMIT_TABLE, null, values);
        Uri resultUri = ContentUris.withAppendedId(COMMITS_CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    public static ContentValues prepare(CommitPOJOContainer cm){
        ContentValues values = new ContentValues();
        values.put(COMMIT_OWNER, cm.getOwner());
        values.put(COMMIT_REPO, cm.getRepo());
        values.put(COMMIT_DATE, Integer.getInteger(cm.getCommit().getAuthor().getDate()));
        values.put(COMMIT_MESSAGE, cm.getCommit().getMessage());
        values.put(COMMIT_HASH, cm.getSha());
        return values;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        switch (uriMatcher.match(uri)) {
            case URI_CONTACTS:
                break;
            case URI_CONTACTS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = COMMIT_ID + " = " + id;
                } else {
                    selection = selection + " AND " + COMMIT_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(COMMIT_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_CONTACTS:
                break;
            case URI_CONTACTS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = COMMIT_ID + " = " + id;
                } else {
                    selection = selection + " AND " + COMMIT_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.update(COMMIT_TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    public class DBHelper extends SQLiteOpenHelper {

        Context con;

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            con = context;
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
            ContentValues values = new ContentValues();
            values.put(COMMIT_OWNER, "blabla");
            values.put(COMMIT_REPO, "blablabla");
            values.put(COMMIT_DATE, "1212");
            values.put(COMMIT_MESSAGE, "pffffff");
            values.put(COMMIT_HASH, "bu");
            db.insert(COMMIT_TABLE, null, values);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS commits_list");
            onCreate(db);
        }
    }
}

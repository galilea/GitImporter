package com.example.galilea.gitimporter;

import android.database.Cursor;

import com.example.galilea.gitimporter.processing.CommitPOJOContainer;

/**
 * Created by galilea on 21.04.2015.
 */
public class SimpleCommit {

    public String author;
    public String message;
    public Integer date;
    public String hash;

    SimpleCommit(CommitPOJOContainer container){
        author = container.getCommit().getAuthor().getName();
        date = Integer.getInteger(container.getCommit().getAuthor().getDate());
        message = container.getCommit().getMessage();
        hash =  container.getSha();

    }

    SimpleCommit(String a, String m, String d, String h){
        author = a;
        message = m;
        date = Integer.getInteger(d);
        hash = h;
    }

    public static SimpleCommit fromCursor(Cursor cursor) {
        SimpleCommit smp = new SimpleCommit(
                cursor.getString(cursor.getColumnIndex("owner")),
                cursor.getString(cursor.getColumnIndex("message")),
                cursor.getString(cursor.getColumnIndex("date")),
                cursor.getString(cursor.getColumnIndex("hash"))
        );

        return smp;
    }

}

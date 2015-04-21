package com.example.galilea.gitimporter;

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

}

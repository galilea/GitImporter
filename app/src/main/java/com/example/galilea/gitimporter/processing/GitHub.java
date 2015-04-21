package com.example.galilea.gitimporter.processing;


import java.util.List;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by galilea on 18.04.2015.
 */
public interface GitHub {

    @GET("/repos/{owner}/{repo}/commits")
    List<CommitPOJOContainer> commits(
            @Path("owner") String owner,
            @Path("repo") String repo
    );

    @GET("/repos/{owner}/{repo}/commits")
    Response getResponse(
            @Path("owner") String owner,
            @Path("repo") String repo
    );
}
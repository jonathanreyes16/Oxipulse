package com.example.oxipulse.classes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("/eval/{oxi}_{hr}")
    Call<EvalResponse> getEval();

    @POST("/upload")
    Call<EvalResponse> postEvalCsv();



}

package com.example.oxipulse.classes;

import android.text.Editable;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @GET("/eval/{oxi}_{hr}")
    Call<EvalResponse> getEval(
            @Field("oxi")String oxi,
            @Field("hr") String hr
    );
    @FormUrlEncoded
    @POST("/upload")
    Call<EvalResponse> postEvalCsv();



}

package com.example.oxipulse.classes;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("eval")
    Call<EvalResponse> getEval();

}

package com.example.oxipulse.api;

import com.example.oxipulse.model.EvalResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    //Se definen las rutas del api
    //usando el metodo GET ingresamos a la ruta especificado,
    // {oxi} y {hr} son las variables para cambiar la ruta de forma dinamica
    @GET("eval/{oxi}_{hr}")
    Call<EvalResponse> getEval(
            @Path("oxi")String oxi,
            @Path("hr") String hr
    );

    //Usando POST enviaremos Datos (TODO)

    @Multipart
    @POST("upload")
    Call<EvalResponse> postEvalCsv(
            @Part("file") RequestBody file
            //@Part("file") MultipartBody.Part file
            );
    //Call<MyResponse> uploadImage(@Part("image\"; filename=\"myfile.jpg\" ") RequestBody file, @Part("desc") RequestBody desc);

}

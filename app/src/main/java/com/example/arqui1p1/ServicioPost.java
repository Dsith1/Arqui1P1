package com.example.arqui1p1;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Multipart;
import retrofit2.http.Query;

public interface ServicioPost {

    String ruta_api="http://192.168.0.15:5555/prueba/";


    @POST(ruta_api)
    @FormUrlEncoded
    Call<RequestBody>  uploadFile(
            @Field("archivo") String base64,
            @Field("nombre") String nombre
            );

}

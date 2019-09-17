package com.example.arqui1p1;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface IGaleria {

    String ruta_api="http://192.168.0.19:5555/";


    @GET(ruta_api+"min/")
    Call<String>  minimo(

    );

    @GET(ruta_api+"ver/{id}")
    Call<String>  siguiente(
            @Path("id") String id
    );



}

package com.example.arqui1p1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Galeria extends AppCompatActivity {
    int actual;
    int max;
    String base64;
    ImageView imagen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);

        Inicial();

        imagen =(ImageView)findViewById(R.id.imageView);

        Button btnAnterior = (Button) findViewById(R.id.Anterior);
        btnAnterior.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {

                                           if(actual!=1){
                                               actual--;
                                               Dibujar();
                                           }else{
                                               Toast.makeText(getApplicationContext(), "No Existe imagen anterior", Toast.LENGTH_SHORT).show();
                                           }

                                       }
                                   }
        );

        Button btnSiguiente = (Button) findViewById(R.id.Siguiente);
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {

                                               if(actual<max){
                                                   actual++;
                                                   Dibujar();
                                               }else{
                                                   Toast.makeText(getApplicationContext(), "Usted se encuentra en la ultima imagen", Toast.LENGTH_SHORT).show();
                                               }

                                           }
                                       }
        );

        Button btnimprimir = (Button) findViewById(R.id.Imprimir);
        btnimprimir.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {

                                            Imprimir();

                                           }
                                       }
        );


    }


    private void Inicial(){

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IGaleria.ruta_api)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        IGaleria postApi= retrofit.create(IGaleria.class);


        Call<String> call = postApi.minimo();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String prueba=response.body();


                actual= Integer.parseInt(prueba.substring(6,prueba.length()-1));

                Log.d("good", "goog");
                Log.d("goog","prueba:"+prueba.substring(6,prueba.length()-1));
                Dibujar();



            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("fail", "fail");
                Log.d("fail",t.getMessage());
            }
        });
    }


    private void Dibujar(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IGaleria.ruta_api)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IGaleria postApi= retrofit.create(IGaleria.class);



        Call<String> call = postApi.siguiente(String.valueOf(actual));

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String prueba=response.body();


                 base64= prueba.substring(1,prueba.length()-1);

                 String[] p=base64.split("\n");


                Log.d("good", "goog Dibujo");
                Log.d("good1", prueba);
                Log.d("good2", p[0]);



                byte[] decodedString = Base64.decode(base64, Base64.NO_WRAP);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                String ruta=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();

                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Pruebas/Visor.png");

                if (!file.exists()) {
                    try {
                        file.createNewFile();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                FileOutputStream fos = null;
                try {
                    if (base64 != null) {
                        fos = new FileOutputStream(ruta+"/Pruebas/Visor.png");
                        fos.write(decodedString);
                        fos.flush();
                        fos.close();
                    }

                } catch (Exception e) {

                } finally {
                    if (fos != null) {
                        fos = null;
                    }
                }


                imagen.setImageBitmap(decodedByte);
                imagen.invalidate();
                Maximo();





            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("fail", "fail");
                Log.d("fail",t.getMessage());
            }
        });
    }

    private void Maximo(){

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IGaleria.ruta_api)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        IGaleria postApi= retrofit.create(IGaleria.class);


        Call<String> call = postApi.maximo();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String prueba=response.body();


                max= Integer.parseInt(prueba.substring(6,prueba.length()-1));

                Log.d("good", "goog");
                Log.d("goog","prueba:"+prueba.substring(6,prueba.length()-1));




            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("fail", "fail");
                Log.d("fail",t.getMessage());
            }
        });
    }

    private void Imprimir(){

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IGaleria.ruta_api)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        IGaleria postApi= retrofit.create(IGaleria.class);


        Call<String> call = postApi.Mostrar(String.valueOf(actual));

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String prueba=response.body();

                Log.d("good", "goog");
                Log.d("goog","muestreo");
                Dibujar();



            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("fail", "fail");
                Log.d("fail",t.getMessage());
            }
        });
    }
}

package com.example.arqui1p1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.Button;
import android.widget.Toast;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;


public class Lienzo extends Activity {

    Dibujo dibujo;
    String nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lienzo);
        dibujo = findViewById(R.id.draw);
        Intent intent = getIntent();
        nombre = intent.getStringExtra("Nombre");

        Button btnEnviar = findViewById(R.id.Enviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {


                                             File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Pruebas");
                                             boolean success = false;
                                             if (!folder.exists()) {
                                                 success = folder.mkdirs();
                                             }

                                             System.out.println(success + "folder");

                                             File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Pruebas/" + nombre + ".png");

                                             if (!file.exists()) {
                                                 try {
                                                     success = file.createNewFile();

                                                 } catch (IOException e) {
                                                     e.printStackTrace();
                                                 }
                                             }

                                             System.out.println(success + "file");


                                             FileOutputStream ostream = null;
                                             try {
                                                 ostream = new FileOutputStream(file);

                                                 System.out.println(ostream);
                                                 View targetView = dibujo;

                                                 // myDrawView.setDrawingCacheEnabled(true);
                                                 //   Bitmap save = Bitmap.createBitmap(myDrawView.getDrawingCache());
                                                 //   myDrawView.setDrawingCacheEnabled(false);
                                                 // copy this bitmap otherwise distroying the cache will destroy
                                                 // the bitmap for the referencing drawable and you'll not
                                                 // get the captured view
                                                 //   Bitmap save = b1.copy(Bitmap.Config.ARGB_8888, false);
                                                 //BitmapDrawable d = new BitmapDrawable(b);
                                                 //canvasView.setBackgroundDrawable(d);
                                                 //   myDrawView.destroyDrawingCache();
                                                 // Bitmap save = myDrawView.getBitmapFromMemCache("0");
                                                 // myDrawView.setDrawingCacheEnabled(true);
                                                 //Bitmap save = myDrawView.getDrawingCache(false);
                                                 Bitmap well = dibujo.getBitmap();
                                                 Bitmap save = Bitmap.createBitmap(320, 480, Config.ARGB_8888);
                                                 Paint paint = new Paint();
                                                 paint.setColor(Color.WHITE);
                                                 Canvas now = new Canvas(save);
                                                 now.drawRect(new Rect(0, 0, 320, 480), paint);
                                                 now.drawBitmap(well, new Rect(0, 0, well.getWidth(), well.getHeight()), new Rect(0, 0, 320, 480), null);

                                                 // Canvas now = new Canvas(save);
                                                 //myDrawView.layout(0, 0, 100, 100);
                                                 //myDrawView.draw(now);
                                                 if (save == null) {
                                                     System.out.println("NULL bitmap save\n");
                                                 }
                                                 save.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                                                 Envio(file.getAbsolutePath());
                                                 Toast.makeText(getApplicationContext(), "Imagen Guardada y Enviada", Toast.LENGTH_SHORT).show();
                                                 finish();
                                                 //bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                                                 //ostream.flush();
                                                 //ostream.close();
                                             } catch (NullPointerException e) {
                                                 e.printStackTrace();
                                                 Toast.makeText(getApplicationContext(), "Null error", Toast.LENGTH_SHORT).show();
                                             } catch (FileNotFoundException e) {
                                                 e.printStackTrace();
                                                 Toast.makeText(getApplicationContext(), "File error", Toast.LENGTH_SHORT).show();
                                             } catch (IOException e) {
                                                 e.printStackTrace();
                                                 Toast.makeText(getApplicationContext(), "IO error", Toast.LENGTH_SHORT).show();
                                             }

                                         }

                                     }
        );

        Button btnBorrar = (Button) findViewById(R.id.Borrar);
        btnBorrar.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             dibujo.clear();

                                         }
                                     }
        );

        Button btnRojo = (Button) findViewById(R.id.Rojo);
        btnRojo.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           dibujo.SetColor(1);

                                       }
                                   }
        );

        Button btnNegro = (Button) findViewById(R.id.Negro);
        btnNegro.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dibujo.SetColor(2);

                                        }
                                    }
        );

        Button btnAzul = (Button) findViewById(R.id.Azul);
        btnAzul.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           dibujo.SetColor(3);

                                       }
                                   }
        );


    }

    private void Envio(String ruta){

        File imgF = new File(ruta);


        Retrofit retrofit = new Builder()
                .baseUrl(ServicioPost.ruta_api)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicioPost postApi= retrofit.create(ServicioPost.class);

        String archivo=convertImageToStringForServer(dibujo.getBitmap());
        Call<RequestBody> call = postApi.uploadFile(archivo,nombre);

        call.enqueue(new Callback<RequestBody>() {
            @Override
            public void onResponse(Call<RequestBody> call, Response<RequestBody> response) {
                response.body();
                Log.d("good", "good");
                Log.d("goog",response.raw().toString());

            }
            @Override
            public void onFailure(Call<RequestBody> call, Throwable t) {
                Log.d("fail", "fail");
                Log.d("fail",t.getMessage());
            }
        });

    }


    public static String convertImageToStringForServer(Bitmap imageBitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if(imageBitmap != null) {
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 250, 250, false);
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 1, stream);
            byte[] byteArray = stream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.NO_WRAP );
        }else{
            return null;
        }
    }
}

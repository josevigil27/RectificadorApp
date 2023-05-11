package com.example.rectificadorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button btnScan, btnQuit;
    EditText textProducto, textDescripcion, textLinea, textPrecio;
    ImageView imageView;

    ProgressDialog progressDialog;
    RequestQueue requestQueue;

    String HttpUri = "http://192.168.0.107/ApiRectificadorApp/assets/php/obtenerproductos.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            btnScan = (Button) findViewById(R.id.btnscan);
            btnQuit = (Button) findViewById(R.id.btnquit);

            textProducto = (EditText) findViewById(R.id.editTextProducto);
            textDescripcion = (EditText) findViewById(R.id.editTextDescripcio);
            textLinea = (EditText) findViewById(R.id.editTextLinea);
            textPrecio = (EditText) findViewById(R.id.editTextPrecio);
            imageView = (ImageView) findViewById(R.id.imageView);

            // inicializamos requestQueue
            requestQueue = Volley.newRequestQueue(this);

            // inicializamos el progress bar
            progressDialog = new ProgressDialog(this);

            textProducto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if(compruebaConexion(getApplicationContext()) == true) {
                            ObtenerProductos();
                        }else{
                            Toast.makeText(getApplicationContext(),"Compruebe su conexión a internet",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            btnScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(compruebaConexion(getApplicationContext()) == true) {
                        btnScan.setClickable(false);
                        ObtenerProductos();
                        btnScan.setClickable(true);
                    }else{
                        Toast.makeText(getApplicationContext(),"Compruebe su conexión a internet",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnQuit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishAndRemoveTask();
                }
            });

        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void ObtenerProductos() {
        try {

            progressDialog.setMessage("Cargando Productos... ");
            progressDialog.show();

            StringRequest stringRequestObtenerProductos = new StringRequest(Request.Method.POST, HttpUri,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String serverResponse) {
                            // recibimos la respuesta del web services
                            try {
                                JSONObject jsonObject = new JSONObject(serverResponse);
                                JSONArray objectJsonArrayTablaProductos = jsonObject.getJSONArray("tablaproductos");

                                String opproducto="",opdescripcio="",oplinea="",opprecio="",imageString="";
                                byte [] imageBytes;
                                Bitmap objectBitmap=null;
                                for (int i = 0; i < objectJsonArrayTablaProductos.length(); i++) {
                                    JSONObject objectJsonProductos = objectJsonArrayTablaProductos.getJSONObject(i);
                                    opproducto = objectJsonProductos.getString("PRODUCTO");
                                    opdescripcio = objectJsonProductos.getString("DESCRIPCIO");
                                    oplinea = objectJsonProductos.getString("LINEA");
                                    opprecio = objectJsonProductos.getString("PRECIO");

                                    imageString  = objectJsonProductos.getString("IMAGEN");
                                    imageBytes= Base64.decode(imageString,Base64.DEFAULT);
                                    objectBitmap = BitmapFactory.decodeByteArray(imageBytes, 0 , imageBytes.length);
                                }

                                String mensajeApi = jsonObject.getString("mensajeobtenerproductos");
                                textDescripcion.setText(opdescripcio);
                                textLinea.setText(oplinea);
                                textPrecio.setText("L." + String.format(opprecio, "%.2f"));
                                imageView.setImageBitmap(objectBitmap);

                                progressDialog.dismiss();

                            } catch (JSONException ex) {
                                ex.printStackTrace();
                                progressDialog.dismiss();
                                btnScan.setClickable(true);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // si hay algun error por parte de la libreria Voley
                    progressDialog.dismiss();
                    // mostramos el error de la libreria
                    Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                }
            }) {
                // el primer paso es enviar los datos al web services, con sus respectivos parametros
                // se hace un mapeo de un arreglo de 2 dimesiones
                protected Map<String, String> getParams(){
                    Map<String, String> parametros = new HashMap<>();
                    // parametros que enviaremos al web service
                    parametros.put("producto", textProducto.getText().toString());

                    return parametros;
                }
            };

            requestQueue.add(stringRequestObtenerProductos);


        } catch (WindowManager.BadTokenException e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            btnScan.setClickable(true);
        }
    }

    public static boolean compruebaConexion(Context context)
    {
        boolean connected = false;
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();

        for (int i = 0; i < redes.length; i++) {
            // Si alguna red tiene conexión, se devuelve true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }
}
package com.example.studenteasy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner spinner = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.courses,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        url=getUrl(text);


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public String getUrl(String course){

        String link="";
        switch(course){

            case"MEDICINA": link="https://corsi.unibo.it/magistralecu/MedicinaChirurgia/orario-lezioni/@@orario_reale_json?";
                break;
            case"INGEGNERIA": link="https://corsi.unibo.it/laurea/ElettronicaTelecomunicazioni/orario-lezioni/@@orario_reale_json?anno=3&curricula=995-000";
                break;
            case"FILOSOFIA": link="https://corsi.unibo.it/laurea/Filosofia/orario-lezioni/@@orario_reale_json?anno=1&curricula=A85-000";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + course);
        }
        return link;
    }

    public void StartMenu(View view) {
        //check se url vuoto, senno manda openrequest()
        if (url.isEmpty()) {
            Toast.makeText(this, "devi scegliere", Toast.LENGTH_SHORT).show();
        } else openRequest();

    }

    public void openRequest() {

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            saveJson(response);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        start();

                        }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorVolley();
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void saveJson(String http) throws IOException {
        FileOutputStream outputStream;
        String filename = "Myfile2.txt";
        outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
        outputStream.write(http.getBytes());
        outputStream.close();
    }

    public void errorVolley() {
        Toast.makeText(this, "controlla connessione", Toast.LENGTH_SHORT).show();
    }

    public void start()  {
        Intent intent=new Intent(this,MenuActivity.class);
        startActivity(intent);
    }

}




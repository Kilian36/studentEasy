package com.example.studenteasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MenuActivity extends AppCompatActivity {

    JSONArray one_date_lessons;
   //creo il calendario
    private CalendarView n_c_v;
    private String contenuto;
    //creo un oggetto Linear Layout
    private LinearLayout lin;
    private boolean ok=false;
    //al posto d fare array di text view faccio append e poi ripulisco
    // la text view ogni volta che cambio giorno
    TextView tv;
    String data;
    int time_start;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        tv=(TextView) findViewById(R.id.textView);


        //Carico il file con le informazioni su tutte le lezioni
        try {
            contenuto = loadfile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //associo il calendario
        n_c_v=(CalendarView) findViewById(R.id.calendarView);
        //Creo il metodo che si attiva quando seleziono una data nel calendario

        n_c_v.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                 data=take_string_from_int(year,month,dayOfMonth);
                int N;
                //adesso che ho ottenuto la stringa data chiamo la funzione takeObj
                try {
                    one_date_lessons=takeObjects(contenuto,data);
                     ok=true;


                       JSONObject elemento=one_date_lessons.getJSONObject(0);
                       String dato= elemento.getString("start");
                       String start_time = dato.substring(11, 13);
                       time_start = Integer.parseInt(start_time);
                       //Toast.makeText(getApplicationContext(), start_time, Toast.LENGTH_SHORT).show();

                    //voglio sapere quante lezioni ci sono oggi
                    N=one_date_lessons.length();
                    //chiamo il metodo che si occupa di settare le views
                    setviews(N);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        put_alarm(data,time_start,00);

    }


    public String loadfile() throws FileNotFoundException {
        String contents = "";
        String filename = "Myfile2.txt";
        Context context = getApplicationContext();
        FileInputStream fis = context.openFileInput(filename);
        InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);

        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
// Error occurred when opening raw file for reading.
        } finally {
            contents = stringBuilder.toString();

        }
        return contents;
    }


    JSONArray takeObjects(String contenuto, String date) throws JSONException {
        //prende input data e stringa json
        //confrota date degli eggetti lezione con data input e prende oggetti con data corrispondente

        int i;
        CharSequence start = "";

        JSONArray Arr = new JSONArray(contenuto);
        JSONArray Obj_selected = new JSONArray();

        for (i = 0; i < Arr.length(); i++) {

            JSONObject Obj = Arr.getJSONObject(i);
            String dateObj = Obj.getString("start");
            start = dateObj.substring(0, 10);


            if (date.contentEquals(start)) {

                for (int j = 0; j < 4 && j <= Arr.length(); j++) {

                    Obj = Arr.getJSONObject(j + i);
                    dateObj = Obj.getString("start");
                    start = dateObj.substring(0, 10);

                    if (date.contentEquals(start)) {
                        Obj_selected.put(Arr.getJSONObject(i + j));
                    } else break;

                }
                break;
            }

        }
        //Obj_selected è array oggetti json con tutte le lezioni di date


        return Obj_selected;
    }


    public static Intent put_alarm(String date,int hour,int minute) {

        //Creo un arraylist con i giorni della settimana in cui voglio che la sveglia sia messa
        final ArrayList<Integer> days = new ArrayList<>();
        days.add(Calendar.DATE);

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        intent.putExtra(AlarmClock.EXTRA_DAYS, days);

        return intent;
    }
    private String take_string_from_int(int year,int month,int day) {
        //selezionata la data chiamo il metodo takeObj
        //prima creo l'oggetto calendario
        Calendar c = Calendar.getInstance();
        c.set(year,month,day);
        Date date=c.getTime();
        @SuppressLint("SimpleDateFormat") DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        //lo converto nella stringa data voluta
        return df.format(date);
    }

   //Metodo set views da rivedere
    @SuppressLint("SetTextI18n")
    private void setviews(int N) throws JSONException {
        tv.setText(" ");
        if(N==0) {
            tv.setText("Non ci sono lezioni per la data selezionata");
        }
        else {
            //lezioni=new TextView[N];
            for (int i = 0; i < N; i++) {

                //Estraggo le informazioni che mi servono dalla variabile globale
                JSONObject lez_temp=one_date_lessons.getJSONObject(i);
                String titolo=lez_temp.getString("title");
                String prof=lez_temp.getString("docente");
                String time=lez_temp.getString("time");

                tv.append(titolo+ " Svolta dal professor   "+prof + " seguendo l'orario "+time+"\n");
            }
        }
    }
}
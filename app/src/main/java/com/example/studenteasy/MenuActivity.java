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
   //Oggeto calendario che uso per scegliere i giorni in cui voglio vedere le lezioni
    private CalendarView n_c_v;
    //Stringa con il Json array completo
    private String contenuto;

    private boolean ok=false;
    //al posto d fare array di text view faccio append e poi ripulisco
    // la text view ogni volta che cambio giorno
    TextView tv;
    String data;
    int time_start;
    //Intent per la sveglia
    Intent alarm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        tv=(TextView) findViewById(R.id.textView);


        /*Carico il file con il Json array in una stringa che ho definito come variabile globale
        Cosi posso accederla da ogni metodo
        */
        try {
            contenuto = loadfile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //inizializzo il calendario
        n_c_v=(CalendarView) findViewById(R.id.calendarView);
        //Creo il metodo che si attiva quando seleziono una data nel calendario
        n_c_v.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                 data=take_string_from_int(year,month,dayOfMonth);
               //creo un intero che mi permetta di sapere quante lezioni ho in un singolo giorno
                int N;
                //adesso che ho ottenuto la stringa data chiamo la funzione takeObj
                try {
                    one_date_lessons=takeObjects(contenuto,data);
                    ok=true;


                       JSONObject elemento=one_date_lessons.getJSONObject(0);
                       String dato= elemento.getString("start");
                       String start_time = dato.substring(11, 13);
                       time_start = Integer.parseInt(start_time);

                    //voglio sapere quante lezioni ci sono oggi
                     N=one_date_lessons.length();
                    //chiamo il metodo che si occupa di settare la view con le informazioni utili
                    setviews(N);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public String loadfile() throws FileNotFoundException {

        /*
        Questo metodo mette in una stringa il contenuto del file
        salvato nell'internal storage dell'app
         */
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
        } finally {
            contents = stringBuilder.toString();

        }
        return contents;
    }

    JSONArray takeObjects(String contenuto, String date) throws JSONException {
        /*prende input data (In formato yyyy-MM-dd) e stringa json completa
        confronta date degli oggetti lezione con la stringa date e
         prende gli oggetti con data corrispondente.
        */
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

    public void put_tomorrow_alarm(View view) throws JSONException {
        /*Questo metodo prende la data di domani, guarda se domani  c'è lezione
        controlla l'orario quando c'è e mette una sveglia due ore prima
        */
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        String date=take_string_from_int(year,month,day);

        //date è la data di domani in formato yyyy-MM-dd
        JSONArray tomorrow_array=takeObjects(contenuto,date);
        //guardo se domani c'è lezione
        if(tomorrow_array.length()==0) {
            Toast.makeText(this, "Non hai lezione domani, Buon riposo!!", Toast.LENGTH_LONG).show();
        }
        //Se c'è metto la sveglia due ore prima
        else {
            JSONObject first = tomorrow_array.getJSONObject(0);
            String ora_inizio= first.getString("time").substring(0,2);
            String minuto_inizio= first.getString("time").substring(3,5);
            Toast.makeText(this, ora_inizio, Toast.LENGTH_SHORT).show();
            int ora=Integer.parseInt(ora_inizio);
            int minuti=Integer.parseInt(minuto_inizio);
            put_alarm(ora-2,minuti);
        }
    }

    public void put_alarm(int hour, int minute) {

        //Questo metodo mette la sveglia per la giornata di domani

        alarm = new Intent(AlarmClock.ACTION_SET_ALARM);
        alarm.putExtra(AlarmClock.EXTRA_HOUR, hour);
        alarm.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        startActivity(alarm);
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

    private void setviews(int N) throws JSONException {
        if(N==0) {
            tv.append("Non ci sono lezioni per la data selezionata");
        }
        else {
            for (int i = 0; i < N; i++) {
                /*Estraggo le informazioni che voglio mostrare dalla variabile globale
                che contiene le lezioni nel giorno specifico
                 */
                JSONObject lez_temp=one_date_lessons.getJSONObject(i);
                String titolo=lez_temp.getString("title");
                String prof=lez_temp.getString("docente");
                String time=lez_temp.getString("time");
                tv.append(titolo+"\n"+prof +"\n"+time+"\n");
            }
        }
    }

    public void go_back(View view){
        //Creo un intent esplicito che mi faccia ritornare direttamente nella prima activity
        Intent come_back=new Intent(this,MainActivity.class);
        come_back.putExtra("Test",false);
        startActivity(come_back);
    }
}
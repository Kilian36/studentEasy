package com.example.studenteasy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
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
import java.util.ArrayList;
import java.util.Calendar;

public class MenuActivity extends AppCompatActivity {

    private TextView helloTextView;
    JSONArray one_date_lessons;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        try {

            String contenuto = loadfile();
            one_date_lessons = takeObjects(contenuto, "2021-12-14");

        } catch (FileNotFoundException | JSONException e) {
            e.printStackTrace();
        }


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

                //helloTextView.setText(title);

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


    public static Intent put_alarm() {
        int hour = 8;
        int minute = 0;
        //Creo un arraylist con i giorni della settimana in cui voglio che la sveglia sia messa

        final ArrayList<Integer> days = new ArrayList<>();
        days.add(Calendar.DATE);
        days.add(Calendar.DATE + 1);
        days.add(Calendar.DATE + 2);

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        intent.putExtra(AlarmClock.EXTRA_DAYS, days);

        return intent;
    }
}
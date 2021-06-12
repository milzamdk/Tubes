package com.example.tubes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tubes.adapter.MateriAdapter;
import com.example.tubes.data.MateriData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class Materi extends AppCompatActivity {
    private RecyclerView rV_materi;
    private List<MateriData> listmateri;
    private MateriAdapter adaptermateri;

    //JSON URL =    https://run.mocky.io/v3/9169d365-ef98-45cf-a397-f3ca4b187939
    //or            http://192.168.137.1/database/get_data.php
    //or            http://192.168.40.183/wpu-rest-server/api/materi
    private static String JSON_URL = "http://192.168.40.183/wpu-rest-server/api/materi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materi);
        rV_materi = findViewById(R.id.rv_list_materi);
        listmateri = new ArrayList<>();


        GetData getData = new GetData();
        getData.execute();
    }

    public class GetData extends AsyncTask<String, String, String> {
        ProgressDialog pb = new ProgressDialog(Materi.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setMessage("Loading...");
            pb.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String current = "";
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(JSON_URL);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream is = urlConnection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);

                    int data = isr.read();
                    while (data != -1) {
                        current += (char) data;
                        data = isr.read();
                    }
                    return current;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return current;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    MateriData materiData = new MateriData();
                    materiData.setJudul(jsonObject1.getString("judul_materi"));
                    materiData.setSubjudul(jsonObject1.getString("submateri"));
                    materiData.setDeskripsi(jsonObject1.getString("isi_materi"));
                    listmateri.add(materiData);
                    pb.dismiss();

                }
            } catch (JSONException e) {
                Toast.makeText(Materi.this, e.toString(), Toast.LENGTH_LONG).show();
            }
            PutDataIntoRecyclerView (listmateri);
        }
    }

    private void PutDataIntoRecyclerView(List<MateriData> listmateri) {
        adaptermateri = new MateriAdapter(listmateri, this);
        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rV_materi.setLayoutManager(lm);
        rV_materi.setHasFixedSize(true);
        rV_materi.setAdapter(adaptermateri);
    }

}
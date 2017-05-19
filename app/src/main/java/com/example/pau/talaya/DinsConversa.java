package com.example.pau.talaya;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

import static com.example.pau.talaya.LoginActivity.usuariActiu;


public class DinsConversa extends AppCompatActivity {
    public Cursor cursor,comprovar;
    public SimpleCursorAdapter adapterdreta;
    static AdapterChat ad;
    private AsyncHttpClient clientMissatge;
    private DataBaseManager d;
    private TextView txtMissatge;
    private String[] from;
    private int[] to;
    static int usuarirep;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dins_conversa);

        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#4C9141"));

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_flecha_back));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Missatges");
        final Bundle dades = this.getIntent().getExtras();
        usuarirep = dades.getInt("idrep");

        TextView missatgedreta = (TextView) findViewById(R.id.msg_text_message);
        TextView horamissatge = (TextView) findViewById(R.id.msg_text_time_message);

        txtMissatge = (TextView) findViewById(R.id.missatge);

        //LinearLayout layout = (LinearLayout) findViewById(R.id.msg_bubble_background_textesquerra);

        final ImageButton enviarmissatge = (ImageButton) findViewById(R.id.enviarmissatge);
        d = new DataBaseManager(this);
        from = new String[]{OpenHelper.M_Missatge, OpenHelper.M_Data,OpenHelper.M_Perfil, OpenHelper.M_Missatge, OpenHelper.M_Data};
        to = new int[]{R.id.msg_text_message, R.id.msg_text_time_message, R.id.perfilmissatge, R.id.msg_text_time_messageesquerra, R.id.msg_text_messageesquerra};

        cursor = d.getMissatges(1,2);

        mostraListdreta(cursor, from, to);
        //
        // d.insertarusuari('1',"asa@gmail.com", "Albert", "1234", "ajaj", '2', "123");
        //String date = (String) android.text.format.DateFormat.format("yyyy-MM-dd kk:mm:ss", new java.util.Date());
        //d.insertarmissatge( date,"dreta",2,1);


        enviarmissatge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!txtMissatge.getText().toString().equals("")){

                    String date = (String) DateFormat.format("yyyy-MM-dd kk:mm:ss", new Date());
                    String miss = txtMissatge.getText().toString();

                    d.insertarmissatge(date, miss, 1, 2);

                    cursor = d.getMissatges(1,2);
                    mostraListdreta(cursor, from, to);

                    txtMissatge.setText("");

                    apiPost(miss,date);

                    getMissatge();

                }

            }
        });
    }
    public void mostraListdreta(Cursor cursor, String[] from, int[] to) {

        ListView llista = (ListView)findViewById(R.id.listConversa);

        adapterdreta = new AdapterChat(this, R.layout.bombolladreta, cursor, from, to, 1);
        llista.setAdapter(adapterdreta);

        llista.setSelection(llista.getAdapter().getCount()-1);


    }

    private void apiPost(String missatge, String data) {

        HttpResponse response;
        HttpClient client = new DefaultHttpClient();
        String url = "http://talaiaapi.azurewebsites.net/api/missatge";
        HttpPost post = new HttpPost(url);

        JSONObject miss = new JSONObject();


        try {
            miss.put("Text",missatge);
            miss.put("Data",data);
            ///////////////////////////////////////////////////////////////
            ///AQUI!!
            ///////////////////////////////
            miss.put("FKUsuariEnvia",usuariActiu.getIdUsuari());
            miss.put("FKUsuariRep",351);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String strUser = miss.toString();

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }

        post.setEntity(new StringEntity(strUser, "UTF-8"));
        post.setHeader("Content-Type", "application/json");

        try {
            response = client.execute(post);
            String sresponse = response.getEntity().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private void getMissatge(){

        String url = " http://talaiaapi.azurewebsites.net/api/missatge";

        clientMissatge = new AsyncHttpClient();
        clientMissatge.setMaxRetriesAndTimeout(0,10000);

        clientMissatge.get(DinsConversa.this, url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                JSONArray jsonArray = null;
                JSONObject missatge = null;
                String str = new String(responseBody);

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                String dateString,miss;

                String oldFormat = "MM/dd/yyyy HH:mm:ss";
                String newFormat = "dd/MM/yyyy";

                SimpleDateFormat sdf1 = new SimpleDateFormat(oldFormat);
                SimpleDateFormat sdf2 = new SimpleDateFormat(newFormat);


                try {

                    jsonArray = new JSONArray(str);

                    for (int i = 0; i < jsonArray.length();i++){

                        missatge = jsonArray.getJSONObject(i);

                        if (missatge.getInt("FKUsuariRep") == usuariActiu.getIdUsuari()){

                            miss = missatge.getString("Text");
                            dateString = (String) missatge.get("Data");

                            dateString = sdf2.format(sdf1.parse(dateString));





                            d.insertarmissatge(miss,dateString,usuariActiu.getIdUsuari(),usuarirep );
                        }



                    }

                }catch (JSONException e) {

                    e.printStackTrace();

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {


            }
        });

    }
}

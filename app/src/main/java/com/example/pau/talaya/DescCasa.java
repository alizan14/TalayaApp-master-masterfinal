package com.example.pau.talaya;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

import static com.example.pau.talaya.LoginActivity.usuariActiu;
import static com.example.pau.talaya.home.CasaList;


/**
 * Created by Pau on 28/4/17.
 */

public class DescCasa extends AppCompatActivity{


    private AsyncHttpClient clientUsuari;
    private ProgressDialog progress;
    private DatePickerDialog CalendarPicker;
    private Calendar dateCalendar = Calendar.getInstance();
    private int indexCasa;

    final Context context = this;
    private DataBaseManager d;
    private Date dateE, dateS;

    String DiaE, DiaS;

    int Dies, Persones;

    private View view;
    private String poblacio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.desc_casa);

        String capacitat, preu, nom, id;

        view = getWindow().getDecorView().getRootView();

        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#4C9141"));

        final Bundle b = getIntent().getExtras();

        FloatingActionButton crearconversa = (FloatingActionButton) findViewById(R.id.missatgeButton);

        TextView txtNom = (TextView)findViewById(R.id.textNom);
        TextView txtPob = (TextView)findViewById(R.id.textPob);
        TextView txtPreu = (TextView)findViewById(R.id.textPreu);
        TextView txtComarca = (TextView)findViewById(R.id.textComarca);
        TextView txtCapacitat = (TextView)findViewById(R.id.textCapacitat);
        TextView txtRating = (TextView)findViewById(R.id.textPuntuacio);

        TextView txtDesc = (TextView)findViewById(R.id.txtDesc);

        ImageView billarImg = (ImageView)findViewById(R.id.image1);
        ImageView campFutImg = (ImageView)findViewById(R.id.image2);
        ImageView campTenImg = (ImageView)findViewById(R.id.image3);
        ImageView internetImg = (ImageView)findViewById(R.id.image4);
        ImageView piscinaImg = (ImageView)findViewById(R.id.image5);
        ImageView projectorImg = (ImageView)findViewById(R.id.image6);
        ImageView salaImg = (ImageView)findViewById(R.id.image7);
        ImageView pingpongImg = (ImageView)findViewById(R.id.image8);

        Button reserva = (Button)findViewById(R.id.btnReserva);

        id = b.getString("id");

        for (int i = 0; i < CasaList.size();i++){

            assert id != null;
            if (id.equals(String.valueOf(CasaList.get(i).getIdCasa()))){

                indexCasa = i;

            }
        }

        nom = CasaList.get(indexCasa).getNom() + ",";
        poblacio = CasaList.get(indexCasa).getPoblacio() +",";
        preu = CasaList.get(indexCasa).getPreuBasic() + " €/nit (consultar)";
        capacitat = CasaList.get(indexCasa).getCapacitat() + " persones -";

        txtNom.setText(nom);
        txtPob.setText(poblacio);
        txtComarca.setText(CasaList.get(indexCasa).getComarca());
        txtPreu.setText(preu);
        txtCapacitat.setText(capacitat);

        txtRating.setText(String.valueOf(CasaList.get(indexCasa).getMitjana()));

        RelativeLayout layoutDesc = (RelativeLayout)findViewById(R.id.descripcio);
        LinearLayout noData = (LinearLayout)findViewById(R.id.noData);

        txtDesc.setText(CasaList.get(indexCasa).getDescripcio());

        layoutDesc.bringToFront();

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_flecha_back));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(CasaList.get(indexCasa).getNom());

        RatingBar avg =(RatingBar)findViewById(R.id.avgRating);

        avg.setRating((float) CasaList.get(indexCasa).getMitjana());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPageAndroid);
        AdapterImatges adapterView = new AdapterImatges(this);
        mViewPager.setAdapter(adapterView);

        final FloatingActionButton favorits = (FloatingActionButton)findViewById(R.id.favButton);

        final FloatingActionButton misstge = (FloatingActionButton)findViewById(R.id.missatgeButton);

        misstge.setImageResource(R.drawable.misstage);

        if (CasaList.get(indexCasa).isFavorits()){
            favorits.setImageResource(R.drawable.star_selected);
        }else {
            favorits.setImageResource(R.drawable.star_unselected);
        }

        favorits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CasaList.get(indexCasa).isFavorits()){

                    favorits.setImageResource(R.drawable.star_unselected);
                    CasaList.get(indexCasa).setFavorits(false);

                }else {

                    favorits.setImageResource(R.drawable.star_selected);
                    CasaList.get(indexCasa).setFavorits(true);

                    posaMarcador(String.valueOf(CasaList.get(indexCasa).getIdCasa()));

                }
            }
        });

        if (CasaList.get(indexCasa).getBillar() == 0){
            billarImg.setVisibility(View.GONE);
        }
        if (CasaList.get(indexCasa).getCampFutbol() == 0){
            campFutImg.setVisibility(View.GONE);
        }
        if (CasaList.get(indexCasa).getCampTenis() == 0){
            campTenImg.setVisibility(View.GONE);
        }
        if (CasaList.get(indexCasa).getInternet() == 0){
            internetImg.setVisibility(View.GONE);
        }
        if (CasaList.get(indexCasa).getPiscina() == 0){
            piscinaImg.setVisibility(View.GONE);
        }
        if (CasaList.get(indexCasa).getProjector() == 0){
            projectorImg.setVisibility(View.GONE);
        }
        if (CasaList.get(indexCasa).getSalaComuna() == 0){
            salaImg.setVisibility(View.GONE);
        }
        if (CasaList.get(indexCasa).getTenisTaula() == 0) {
            pingpongImg.setVisibility(View.GONE);
        }

        if (CasaList.get(indexCasa).getBillar() == 0 && CasaList.get(indexCasa).getCampFutbol() == 0 && CasaList.get(indexCasa).getCampTenis() == 0 && CasaList.get(indexCasa).getInternet() == 0 && CasaList.get(indexCasa).getPiscina() == 0 && CasaList.get(indexCasa).getProjector() == 0 && CasaList.get(indexCasa).getSalaComuna() == 0 && CasaList.get(indexCasa).getTenisTaula() == 0){

            noData.setVisibility(View.VISIBLE);
        }
        crearconversa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();

                bundle.putString("idrep", String.valueOf(CasaList.get(indexCasa).getFK_Propietari()));


                Intent intent = new Intent(view.getContext(), DinsConversa.class);
                intent.putExtras(bundle);

                d = new DataBaseManager(context);
                d.crearconversa(usuariActiu.getIdUsuari(),CasaList.get(indexCasa).getIdCasa(),CasaList.get(indexCasa).getNom());

                startActivity(intent);

            }
        });
        reserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom);
                dialog.setTitle("Reserva");
                // set the custom dialog components - text, image and button

                final EditText txtDies = (EditText)dialog.findViewById(R.id.editDia);
                final EditText txtDiaE = (EditText)dialog.findViewById(R.id.editDiaE);
                final EditText txtDiaS = (EditText)dialog.findViewById(R.id.editDiaS);
                final EditText txtPersones = (EditText)dialog.findViewById(R.id.editPersones);

                ImageView DataEntrada = (ImageView)dialog.findViewById(R.id.imageEntrada);
                ImageView DataSortida = (ImageView)dialog.findViewById(R.id.imageSortida);

                DataEntrada.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CalendarPicker = new DatePickerDialog(DescCasa.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                java.util.Calendar dateCalendar = java.util.Calendar.getInstance();
                                dateCalendar.set(java.util.Calendar.YEAR, year);
                                dateCalendar.set(java.util.Calendar.MONTH, monthOfYear);
                                dateCalendar.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth);

                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                                dateE = dateCalendar.getTime();

                                String dateString;
                                dateString = formatter.format(dateE);
                                txtDiaE.setText(dateString);
                                DiaE = (String) DateFormat.format("yyyy-MM-dd kk:mm:ss", dateE);

                            }
                        }, dateCalendar.get(java.util.Calendar.YEAR), dateCalendar.get(java.util.Calendar.MONTH), dateCalendar.get(java.util.Calendar.DAY_OF_MONTH));

                        CalendarPicker.show();

                    }
                });

                DataSortida.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CalendarPicker = new DatePickerDialog(DescCasa.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                java.util.Calendar dateCalendar = java.util.Calendar.getInstance();
                                dateCalendar.set(java.util.Calendar.YEAR, year);
                                dateCalendar.set(java.util.Calendar.MONTH, monthOfYear);
                                dateCalendar.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth);

                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                                dateS = dateCalendar.getTime();

                                String dateString;
                                dateString = formatter.format(dateS);
                                txtDiaS.setText(dateString);

                                DiaS = (String) DateFormat.format("yyyy-MM-dd kk:mm:ss", dateS);

                            }
                        }, dateCalendar.get(java.util.Calendar.YEAR), dateCalendar.get(java.util.Calendar.MONTH), dateCalendar.get(java.util.Calendar.DAY_OF_MONTH));

                        CalendarPicker.show();

                    }
                });



                Button dialogButton = (Button) dialog.findViewById(R.id.buttonAccepta);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Dies = Integer.parseInt(txtDies.getText().toString());
                        Persones = Integer.parseInt(txtPersones.getText().toString());
                        postReserva(Dies,DiaE,DiaS,Persones);
                    }
                });

                dialog.show();
            }
        });

    }

    private void postReserva(int Dies, String DiaE, String DiaS, int Persones) {

        HttpResponse response;
        HttpClient client = new DefaultHttpClient();
        String url = "http://talaiaapi.azurewebsites.net/api/reserva";
        HttpPost post = new HttpPost(url);

        JSONObject user = new JSONObject();

        try {
            user.put("Preu",0);
            user.put("Dies",Dies);
            user.put("Persones",Persones);
            user.put("DataEntrada",DiaE);
            user.put("DataSortida",DiaS);
            user.put("Estat","Pendent");
            user.put("FKUsuari",usuariActiu.getIdUsuari());
            user.put("FKCasa",CasaList.get(indexCasa).getIdCasa());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String strUser = user.toString();

        post.setEntity(new StringEntity(strUser, "UTF-8"));
        post.setHeader("Content-Type", "application/json");

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }

        try {
            response = client.execute(post);
            String sresponse = response.getEntity().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void posaMarcador(String idCasa) {

        HttpResponse response;
        HttpClient client = new DefaultHttpClient();
        String url = "http://talaiaapi.azurewebsites.net/api/marcador/"+usuariActiu.getIdUsuari();
        HttpPost post = new HttpPost(url);

        JSONObject favorit = new JSONObject();


        try {
            favorit.put("FKUsuari",usuariActiu.getIdUsuari());
            favorit.put("FKCasa",idCasa);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String strUser = favorit.toString();

        post.setEntity(new StringEntity(strUser, "UTF-8"));
        post.setHeader("Content-Type", "application/json");

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }

        try {
            response = client.execute(post);
            String sresponse = response.getEntity().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public void treuMarcador(final View view){

        final String url = "http://talaiaapi.azurewebsites.net/api/marcador/"+usuariActiu.getIdUsuari();

        final View view2 = view;

        clientUsuari = new AsyncHttpClient();
        clientUsuari.setMaxRetriesAndTimeout(0,10000);

        clientUsuari.get(DescCasa.this, url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                JSONObject casa = null;
                String str = new String(responseBody);

                try {

                    casa = new JSONObject(str);

                }catch (JSONException e) {

                    e.printStackTrace();

                }
                progress.dismiss();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                Snackbar.make(view, "Error de conexió", Snackbar.LENGTH_LONG)
                        .show();

                progress.dismiss();

            }
        });

    }

}

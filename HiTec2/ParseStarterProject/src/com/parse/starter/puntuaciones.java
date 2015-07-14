package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class puntuaciones extends Activity implements AdapterView.OnItemSelectedListener{
    Spinner spinnerEdificio;
    Spinner spinnerColor;
    Spinner spinnerScore;
    boolean listo = false, defaults=false;
    int punct;
    String edificio, color, estacion, score, equipo;
    String edificioDefault,colorDefault,scoreDefault;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puntuaciones);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        spinnerEdificio = (Spinner)findViewById(R.id.spinnerEdificio);


        ArrayAdapter adapterEdificio =ArrayAdapter.createFromResource(this,R.array.Edificios,android.R.layout.simple_spinner_item);
        spinnerEdificio.setAdapter(adapterEdificio);
        spinnerEdificio.setOnItemSelectedListener(this);




        spinnerScore = (Spinner)findViewById(R.id.spinnerScore);


        ArrayAdapter adapterScore =ArrayAdapter.createFromResource(this,R.array.Score,android.R.layout.simple_spinner_item);
        spinnerScore.setAdapter(adapterScore);
        spinnerScore.setOnItemSelectedListener(this);

        spinnerColor = (Spinner)findViewById(R.id.spinnerColor);
        ArrayAdapter adapterColor = ArrayAdapter.createFromResource(this,R.array.Colores,android.R.layout.simple_spinner_item);
        spinnerColor.setAdapter(adapterColor);
        spinnerColor.setOnItemSelectedListener(this);

        edificioDefault = adapterEdificio.getItem(0).toString();
        colorDefault = adapterColor.getItem(0).toString();
        scoreDefault = adapterScore.getItem(0).toString();
    }
    public void onClickSubmit(View v){
        Button button = (Button) v;
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            estacion = currentUser.get("Estacion").toString();

        } else {
            // show the signup or login screen
        }
        //Toast.makeText(this, estacion, Toast.LENGTH_SHORT).show();

        punct= Integer.parseInt(score);
        //button.setText(score);


        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Puntuacionesv2");
        query.whereEqualTo("Estaciones", estacion);
        final ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Puntuacionesv2");

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> equipoList, ParseException e) {
                if (e == null) {

                    equipo = edificio + color;
                    query.whereExists(equipo);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> equipoList, ParseException e) {
                            if (e == null) {
                                ParseObject object = equipoList.get(0);


                                object.put(equipo, score);
                                object.saveInBackground();

                                success();


                            } else {
                                fail();
                            }
                        }
                    });


                    //success();


                } else {
                    Log.d("Equipos", "Error: " + e.getMessage());
                }
            }
        });


    }
    public void success()
    {
        Toast.makeText(this," Puntuacion guardada exitosamente ", Toast.LENGTH_SHORT).show();


    }
    public void fail()
    {
        Toast.makeText(this," Error, favor de verificar datos ", Toast.LENGTH_SHORT).show();


    }
    public  void Logout(View v){
        ParseUser.logOut();
        puntuaciones.this.startActivity(new Intent(puntuaciones.this, ParseStarterProjectActivity.class));
    }
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView myText=(TextView) view;
        int counter=0;

		/*if(id==R.id.spinner1)
		{
			counter+=1;
			if(counter>0)
			{
				spinner1.setEnabled(false);
			}
		}*/
        edificio = spinnerEdificio.getSelectedItem().toString();

        if(edificio.equals("INGENIERIA"))
            edificio="EI";

        if(edificio.equals("HUMANIDADES"))
            edificio="ENH";


        //estacion = spinner1.getSelectedItem().toString();
        score = spinnerScore.getSelectedItem().toString();
        color = spinnerColor.getSelectedItem().toString();

        if(edificio.equals(edificioDefault) && color.equals(colorDefault) && score.equals(scoreDefault))
            defaults = true;
        else
            defaults = false;

        if(defaults==false)
            Toast.makeText(this," Seleccionaste "+myText.getText(), Toast.LENGTH_SHORT).show();
    }

    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_puntuaciones, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

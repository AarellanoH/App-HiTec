package com.parse.starter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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


public class puntosBonus extends Activity implements AdapterView.OnItemSelectedListener{

    Spinner spinnerEdificio;
    Spinner spinnerColor;
    EditText textScore;
    int scoreTotal = 0;

    boolean listo = false, defaults=true, error=false, assignAll=false;
    int punct;
    String edificio, color, estacion, score, equipo;
    int score_CIT, score_PIT, score_ENH,score_EI;

    String edificioDefault,colorDefault,scoreDefault;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puntos_bonus);
        this.setTitle("Puntos Bonus");

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        spinnerEdificio = (Spinner)findViewById(R.id.spinnerEdificio);


        ArrayAdapter adapterEdificio =ArrayAdapter.createFromResource(this,R.array.Edificios,android.R.layout.simple_spinner_item);
        spinnerEdificio.setAdapter(adapterEdificio);
        spinnerEdificio.setOnItemSelectedListener(this);




        textScore = (EditText)findViewById(R.id.textScore);




        spinnerColor = (Spinner)findViewById(R.id.spinnerColor);
        ArrayAdapter adapterColor = ArrayAdapter.createFromResource(this, R.array.Colores, android.R.layout.simple_spinner_item);
        spinnerColor.setAdapter(adapterColor);
        spinnerColor.setOnItemSelectedListener(this);

        edificioDefault = adapterEdificio.getItem(0).toString();
        colorDefault = adapterColor.getItem(0).toString();

    }
    public void onClickSubmit(View v) {
        Button button = (Button) v;
        error=true;
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            estacion = currentUser.get("Estacion").toString();

        } else {
            // show the signup or login screen
        }

        try {
            score = textScore.getText().toString();
            punct = Integer.parseInt(score);
            error = false;
        } catch (Exception e) {
            fail();
        }

        //button.setText(scoreNoValido);

        if(defaults==false&&error==false)
            try{
                guardarScore(edificio,color,score);
            }
            catch (Exception e)
            {
                fail();
            }

        else
            fail();
    }


    public void guardarScore(String miEdificio, final String miColor, final String myScore){
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Puntuacionesv2");
        query.whereEqualTo("Estaciones", estacion);
        if(miEdificio.equals("TODOS")){
            assignAll=true;
            query.whereExists("CIT"+miColor);
            Log.d("Equipos", "Equipo" + miColor + " Score:" + scoreTotal);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> equipoList, ParseException e) {
                    if (e == null) {
                        Log.d("Equipos", "Entramos" );
                        ParseObject object = equipoList.get(0);
                        String temp_CIT = object.get("CIT"+miColor).toString();
                        String temp_EI = object.get("EI"+miColor).toString();
                        String temp_ENH = object.get("ENH"+miColor).toString();
                        String temp_PIT = object.get("PIT"+miColor).toString();
                        try{
                            punct = Integer.parseInt(temp_CIT);
                            punct = Integer.parseInt(temp_EI);
                            punct = Integer.parseInt(temp_ENH);
                            punct = Integer.parseInt(temp_PIT);
                        }
                        catch (Exception exc){
                            temp_CIT = "0";
                            temp_EI = "0";
                            temp_ENH = "0";
                            temp_PIT = "0";
                        }
                        score_CIT = Integer.parseInt(temp_CIT) + Integer.parseInt(myScore);
                        score_EI = Integer.parseInt(temp_EI) + Integer.parseInt(myScore);
                        score_ENH = Integer.parseInt(temp_ENH) + Integer.parseInt(myScore);
                        score_PIT = Integer.parseInt(temp_PIT) + Integer.parseInt(myScore);
                        object.put("CIT"+miColor, Integer.toString(score_CIT));
                        object.put("EI"+miColor, Integer.toString(score_EI));
                        object.put("ENH"+miColor, Integer.toString(score_ENH));
                        object.put("PIT"+miColor, Integer.toString(score_PIT));

                        object.saveEventually();

                        success();
                    /*
                    while (count <= 4) {
                        count++;
                        guardarScore(listaEdificios[count], miColor, myScore);
                    }
                    */


                    } else {
                        fail();
                    }
                }
            });
        }else {
            equipo = miEdificio + miColor;
            query.whereExists(equipo);
            Log.d("Equipos", "Equipo" + equipo + " Score:" + score);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> equipoList, ParseException e) {
                    if (e == null) {
                        ParseObject object = equipoList.get(0);
                        String tempScore = object.get(equipo).toString();
                        try {
                            punct = Integer.parseInt(tempScore);
                        } catch (Exception exc) {
                            tempScore = "0";
                        }

                        scoreTotal = Integer.parseInt(tempScore) + Integer.parseInt(myScore);
                        object.put(equipo, Integer.toString(scoreTotal));

                        object.saveEventually();

                        success();
                    /*
                    while (count <= 4) {
                        count++;
                        guardarScore(listaEdificios[count], miColor, myScore);
                    }
                    */


                    } else {
                        //Log.d("Equipos", "Excepcion" + e.getMessage());
                        fail();
                    }
                }
            });
        }
    }

    public void success()
    {
        if(edificio.equals("EI"))
            edificio = "INGENIERIA";
        if(edificio.equals("ENH"))
            edificio = "HUMANIDADES";
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Exito");
        if(assignAll==true){
            alert.setMessage("Puntuacion para equipos " + color +  " guardada exitosamente. \nBonus dado: " + score + "" +
                    "\nBonus total CIT: " + score_CIT +
                    "\nBonus total INGENIERIA: " + score_EI+
                    "\nBonus total HUMANIDADES: " + score_ENH+
                    "\nBonus total PIT: " + score_PIT);
            assignAll=false;

        }else
        alert.setMessage("Puntuacion de equipo " + edificio + " " + color + " guardada exitosamente. \nBonus dado: " + score + "\nBonus total: " + scoreTotal);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                borrarSeleccion();
            }
        });
        alert.show();



    }

    public void borrarSeleccion(){
        spinnerColor.setSelection(0);
        spinnerEdificio.setSelection(0);
        textScore.setText("");
    }

    public void fail()
    {
        Toast.makeText(this," Error, favor de verificar los datos ", Toast.LENGTH_SHORT).show();

    }

    public  void Logout(View v){
        ParseUser.logOut();
        puntosBonus.this.startActivity(new Intent(puntosBonus.this, ParseStarterProjectActivity.class));
    }

    public void Logout(){
        ParseUser.logOut();
        puntosBonus.this.startActivity(new Intent(puntosBonus.this, ParseStarterProjectActivity.class));
    }

    public void onClickCancel(View view){
        borrarSeleccion();
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

        color = spinnerColor.getSelectedItem().toString();

        if(!(edificio.equals(edificioDefault)) && !(color.equals(colorDefault)))
            defaults = false;
        else
            defaults = true;
    }

    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void sumarScores(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_puntos_bonus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_logout){
            Logout();
        }

        return super.onOptionsItemSelected(item);
    }
}

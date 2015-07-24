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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    RadioGroup groupTermino;
    RadioGroup groupParticipacion;
    RadioGroup groupCompromiso;

    Spinner spinnerEdificio;
    Spinner spinnerColor;
    boolean listo = false, defaults=true, error=false, assignAll=false;
    int punct;
    String edificio, color, estacion, equipo;
    String edificioDefault,colorDefault,scoreDefault;
    int count = 0;
    String[] listaEdificios = {"CIT","EI","ENH","PIT"};
    int scoreTermino,scoreParticipacion,scoreCompromiso,scoreTotal,score_CIT,score_PIT,score_EI,score_ENH ;
    boolean terminoDeafult=true, participacionDefault=true,compromisoDefault=true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puntuaciones);
        this.setTitle(ParseUser.getCurrentUser().get("Estacion").toString());

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        spinnerEdificio = (Spinner)findViewById(R.id.spinnerEdificio);


        ArrayAdapter adapterEdificio =ArrayAdapter.createFromResource(this,R.array.Edificios,android.R.layout.simple_spinner_item);
        spinnerEdificio.setAdapter(adapterEdificio);
        spinnerEdificio.setOnItemSelectedListener(this);

        groupTermino = (RadioGroup)findViewById(R.id.groupTermino);
        groupParticipacion = (RadioGroup)findViewById(R.id.groupParticipacion);
        groupCompromiso = (RadioGroup)findViewById(R.id.groupCompromiso);


        spinnerColor = (Spinner)findViewById(R.id.spinnerColor);
        ArrayAdapter adapterColor = ArrayAdapter.createFromResource(this,R.array.Colores,android.R.layout.simple_spinner_item);
        spinnerColor.setAdapter(adapterColor);
        spinnerColor.setOnItemSelectedListener(this);

        edificioDefault = adapterEdificio.getItem(0).toString();
        colorDefault = adapterColor.getItem(0).toString();
    }

    public void onClickCancel(View v){
        borrarSeleccion();
    }

    public void onClickSubmit(View v) {
        Button button = (Button) v;
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            estacion = currentUser.get("Estacion").toString();

        } else {
            // show the signup or login screen
        }


        if((terminoDeafult==false) && (participacionDefault==false) && (compromisoDefault==false) && (defaults==false)){
            sumarScores();
            guardarScore(edificio,color,Integer.toString(scoreTotal));
        }
        else{
            fail();
        }

        /*
        if (scoreNoValido != scoreDefault) {

            if(edificio.equals("TODOS")){
                edificio = "CIT";
                guardarScore("CIT",color,scoreNoValido);
                Log.d("Equipos", "Equipo antes de metodo" + equipo + " Score:" + scoreNoValido);

                edificio = "EI";
                guardarScore("EI",color,scoreNoValido);
                Log.d("Equipos", "Equipo antes de metodo" + equipo + " Score:" + scoreNoValido);

                edificio = "ENH";
                guardarScore("ENH",color,scoreNoValido);
                Log.d("Equipos", "Equipo antes de metodo" + equipo + " Score:" + scoreNoValido);

                edificio = "PIT";
                guardarScore("PIT", color, scoreNoValido);
                Log.d("Equipos", "Equipo antes de metodo" + equipo + " Score:" + scoreNoValido);

                //clearSelection();
            }

            else{
                guardarScore(edificio,color,scoreNoValido);
                clearSelection();
            }
        }
        */
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
            alert.setMessage("Puntuacion para equipos " + color +  " guardada exitosamente. \nBonus dado: " + scoreTotal + "" +
                    "\nPuntos totales CIT: " + score_CIT +
                    "\nPuntos totales INGENIERIA: " + score_EI+
                    "\nPuntos totales HUMANIDADES: " + score_ENH+
                    "\nPuntos totales PIT: " + score_PIT);
            assignAll=false;

        }else
        alert.setMessage("Puntuacion de equipo " + edificio + " " + color + " guardada exitosamente. \nScore: " + scoreTotal);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                borrarSeleccion();
            }
        });
        alert.show();


    }

    public void fail()
    {
        Toast.makeText(this," Error, favor de verificar los datos ", Toast.LENGTH_SHORT).show();
    }

    public void guardarScore(String miEdificio, final String miColor, final String myScore){
        final ParseQuery query = ParseQuery.getQuery("Puntuacionesv2");
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
            Log.d("Equipos", "Equipo" + equipo + " Score:" + scoreTotal);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> equipoList, ParseException e) {
                    if (e == null) {
                        ParseObject object = equipoList.get(0);
                        object.put(equipo, myScore);

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
        }
    }

    public void onRadioButtonClicked(View view) {

        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            //Los botones de haber terminado
            case R.id.rdbTermSi:
                if (checked){
                    scoreTermino = 15;
                    terminoDeafult = false;
                }break;
            case R.id.rdbTermNo:
                if (checked){
                    scoreTermino = 0;
                    terminoDeafult = false;
                }break;

            //Los botones de calificacion en participacion
            case R.id.rdbPart0:
                if (checked){
                    scoreParticipacion = 0;
                    participacionDefault = false;
                }break;
            case R.id.rdbPart5:
                if (checked){
                    scoreParticipacion = 5;
                    participacionDefault = false;
                }break;
            case R.id.rdbPart10:
                if (checked){
                    scoreParticipacion = 10;
                    participacionDefault = false;
                }break;
            case R.id.rdbPart15:
                if (checked){
                    scoreParticipacion = 15;
                    participacionDefault = false;
                }break;

            //Los botones de calificacion en compromiso
            case R.id.rdbComp0:
                if (checked){
                    scoreCompromiso = 0;
                    compromisoDefault = false;
                }break;
            case R.id.rdbComp5:
                if (checked){
                    scoreCompromiso = 5;
                    compromisoDefault = false;
                }break;
            case R.id.rdbComp10:
                if (checked){
                    scoreCompromiso = 10;
                    compromisoDefault = false;
                }break;
            case R.id.rdbComp15:
                if (checked){
                    scoreCompromiso = 15;
                    compromisoDefault = false;
                }break;

        }
    }

    public void sumarScores(){
        scoreTotal = scoreTermino + scoreParticipacion + scoreCompromiso;
    }

    public void borrarSeleccion(){
        groupTermino.clearCheck();
        groupParticipacion.clearCheck();
        groupCompromiso.clearCheck();
        participacionDefault=true;
        compromisoDefault=true;
        terminoDeafult=true;

        spinnerColor.setSelection(0);
        spinnerEdificio.setSelection(0);
    }

    public  void Logout(View v){
        ParseUser.logOut();
        puntuaciones.this.startActivity(new Intent(puntuaciones.this, ParseStarterProjectActivity.class));
    }

    public void Logout(){
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
        color = spinnerColor.getSelectedItem().toString();

        if(!(edificio.equals(edificioDefault)) && !(color.equals(colorDefault)))
            defaults = false;
        else
            defaults = true;

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

        if(id == R.id.action_logout){
            Logout();
        }

        return super.onOptionsItemSelected(item);
    }
}

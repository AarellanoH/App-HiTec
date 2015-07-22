package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ParseStarterProjectActivity extends Activity implements AdapterView.OnItemSelectedListener {
	/** Called when the activity is first created. */
	String estacion;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		/*ParseObject testObject = new ParseObject("TestObject");
		testObject.put("foo", "bar");
		testObject.saveInBackground();*/

		ParseAnalytics.trackAppOpenedInBackground(getIntent());


	}

	@Override
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

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
	public void success(ParseUser user)
	{
		estacion= user.get("Estacion").toString();
		Toast.makeText(this," Login succeeded as " + estacion, Toast.LENGTH_SHORT).show();
	}
	public void failure()
	{
		Toast.makeText(this," Wrong user or password ", Toast.LENGTH_SHORT).show();
	}

	public void LogIn(View view) {
		EditText username= (EditText)findViewById(R.id.username);
		EditText password= (EditText)findViewById(R.id.password);
		String usern= username.getText().toString();
		String pass= password.getText().toString();
		//Toast.makeText(this," You Selected "+usern+pass, Toast.LENGTH_SHORT).show();

		ParseUser.logInInBackground(usern, pass, new LogInCallback() {
			public void done(ParseUser user, ParseException e) {
				if (user != null) {
					success(user);
					String usuario = user.get("Estacion").toString();
					if(usuario.equals("Bonus")){
						ParseStarterProjectActivity.this.startActivity(new Intent(ParseStarterProjectActivity.this,puntosBonus.class));

					}
					else {
						ParseStarterProjectActivity.this.startActivity(new Intent(ParseStarterProjectActivity.this, puntuaciones.class));
					}

				} else {
					failure();
				}
			}
		});



	}
}


package cl.trusteeapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import cl.trusteeapp.R;
import cl.trusteeapp.activities.config.FormularioRegistroUsuario;
import cl.trusteeapp.activities.system.SetupTrustee;

public class ActividadInicial extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.actividad_inicial);
	}

	@Override
	protected void onResume() {
		Intent intent;
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		String value = prefs.getString(FormularioRegistroUsuario.KEY_EMAIL, "");
		if ("".equals(value)) {
			intent = new Intent(this, FormularioRegistroUsuario.class);
		} else {
			intent = new Intent(this, SetupTrustee.class);
		}
		startActivity(intent);
		finish();
		super.onResume();
	}

}

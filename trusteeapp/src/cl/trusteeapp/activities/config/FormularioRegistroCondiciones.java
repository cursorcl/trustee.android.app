package cl.trusteeapp.activities.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;
import cl.trusteeapp.R;
import cl.trusteeapp.activities.ABaseActivity;
import cl.trusteeapp.enums.EPreferences;

public class FormularioRegistroCondiciones extends ABaseActivity {

	private ToggleButton checkValidar;
	private ToggleButton checkHistorial;
	
	private SharedPreferences preferences;
	private ToggleButton checkRegistrarLlamadas;
	
	private TextView txtValidaciones;
	private TextView txtColaboraciones;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.formulario_configuracion_condiciones);
		String name = getResources().getString(R.string.PREFERENCES);
		
		txtValidaciones = (TextView) findViewById(R.id.txtValidaciones);
		txtColaboraciones = (TextView) findViewById(R.id.txtColaboraciones);
		
		preferences = getSharedPreferences(name ,
				Context.MODE_APPEND);
		checkValidar = (ToggleButton) findViewById(R.id.toggleValidar);
		checkValidar.setChecked(true);
		checkValidar.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SharedPreferences.Editor editor = preferences.edit();
				editor.putBoolean(EPreferences.VALIDATE.toString(), isChecked);
				editor.commit();
			}
		});

		checkHistorial = (ToggleButton) findViewById(R.id.toggleGuardar);
		checkHistorial
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						SharedPreferences.Editor editor = preferences.edit();
						editor.putBoolean(EPreferences.HISTORY.toString(),
								isChecked);
						editor.commit();
					}
				});

		checkRegistrarLlamadas = (ToggleButton) findViewById(R.id.btnRegisterCalls);
		checkRegistrarLlamadas
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						SharedPreferences.Editor editor = preferences.edit();
						editor.putBoolean(
								EPreferences.REGISTER_CALLS.toString(),
								isChecked);
						editor.commit();
					}
				});
		initialize();
	}

	private void initialize() {
		checkValidar.setChecked(preferences.getBoolean(
				EPreferences.VALIDATE.toString(), true));

		checkHistorial.setChecked(preferences.getBoolean(
				EPreferences.HISTORY.toString(), true));
		checkRegistrarLlamadas.setChecked(preferences.getBoolean(
				EPreferences.REGISTER_CALLS.toString(), true));
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	    int nroColaboraciones = preferences.getInt(EPreferences.NRO_TEMPORAL_SEND.toString(), 0);
	    txtColaboraciones.setText("Colaboraciones:" + Integer.toString(nroColaboraciones));
	    int nroValidaciones = preferences.getInt(EPreferences.NRO_VALIDATION.toString(), 0);
	    txtValidaciones.setText("Validaciones:" + Integer.toString(nroValidaciones));
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		initialize();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initialize();
	}

}

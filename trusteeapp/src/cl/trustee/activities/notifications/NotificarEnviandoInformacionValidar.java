/**
 * 
 */
package cl.trustee.activities.notifications;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import cl.trustee.R;
import cl.trustee.activities.config.FormularioRegistroUsuario;
import cl.trustee.enums.EAttributes;
import cl.trustee.enums.EBroadcast;
import cl.trustee.view.Register;

/**
 * @author cursor
 */
public class NotificarEnviandoInformacionValidar extends Activity {

	private BroadcastReceiver receiver;
	final Timer t = new Timer();
	private TextView txtNroEntrante;
	private TextView txtNroPropio;
	private TextView txtEmailNotifica;
	private TextView txtTimer;
	private int seconds;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		setContentView(R.layout.notificar_enviando_informacion_validar);

		txtNroEntrante = (TextView) findViewById(R.id.txtNroEntrante);
		txtNroPropio = (TextView) findViewById(R.id.txtNroPropio);
		txtEmailNotifica = (TextView) findViewById(R.id.txtEmailNotifica);

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		String email = preferences.getString(
				FormularioRegistroUsuario.KEY_EMAIL, "");
		String ownPhone = preferences.getString(
				FormularioRegistroUsuario.KEY_PHONE, "");

		Bundle b = getIntent().getExtras();
		String incomming = (String) b.get(EAttributes.PHONE.name());

		txtNroEntrante.setText("Nº Entrante:" + incomming);
		txtNroPropio.setText("Nº Propio:" + ownPhone);
		txtEmailNotifica.setText("Usuario:" + email);

		txtTimer = (TextView) findViewById(R.id.txtTimer);

		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				t.cancel();
				finish();
				Intent local = new Intent();
				local.setAction(EBroadcast.CANCEL.toString());
				sendBroadcast(local);
			}
		});
		inscribe();
		seconds = 6;
		txtTimer.setText(String.format("%d", seconds));
		t.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						txtTimer.setText(String.format("%d", seconds));
					}
				});
				seconds--;
				if (seconds == 0) {
					t.cancel();

					NotificarEnviandoInformacionValidar.this.finish();
					Intent local = new Intent();
					local.setAction(EBroadcast.ACCEPT.toString());
					sendBroadcast(local);
				}

			}
		},0,  1000);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		t.cancel();
		this.finish();
		Intent local = new Intent();
		local.setAction(EBroadcast.ACCEPT.toString());
		sendBroadcast(local);
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(this.receiver);
	}

	private void inscribe() {
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				NotificarEnviandoInformacionValidar.this.finish();
				t.cancel();
				Intent local = new Intent();
				local.setAction(EBroadcast.ACCEPT.toString());
				sendBroadcast(local);
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(EBroadcast.END.toString());
		registerReceiver(receiver, filter);
	}
}

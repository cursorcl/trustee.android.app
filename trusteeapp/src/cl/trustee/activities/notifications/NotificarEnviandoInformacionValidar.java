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
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import cl.trustee.R;
import cl.trustee.enums.EBroadcast;

/**
 * @author cursor
 */
public class NotificarEnviandoInformacionValidar extends Activity {

	private BroadcastReceiver receiver;
	final Timer t = new Timer();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		setContentView(R.layout.notificar_enviando_informacion_validar);

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
		
		
        t.schedule(new TimerTask() {
            public void run() {
                t.cancel();
                NotificarEnviandoInformacionValidar.this.finish();
        		Intent local = new Intent();
        		local.setAction(EBroadcast.ACCEPT.toString());
        		sendBroadcast(local);
            }
        }, 2000); 
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

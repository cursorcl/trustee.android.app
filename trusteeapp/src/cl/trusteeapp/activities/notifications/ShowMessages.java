/**
 * 
 */
package cl.trusteeapp.activities.notifications;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import cl.trusteeapp.R;
import cl.trusteeapp.enums.EAttributes;
import cl.trusteeapp.enums.EBroadcast;
import cl.trusteeapp.enums.EPreferences;
import cl.trusteeapp.enums.EStatusConection;
import cl.trusteeapp.utils.DownloadImageTask;
import cl.trusteeapp.view.Register;

/**
 * @author cursor
 */
public class ShowMessages extends Activity {

	private BroadcastReceiver receiver;
	private TextView txtNumber;
	private TextView txtName;
	private ImageView image;
	private ImageView imageLogo;
  private Register register;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		setContentView(R.layout.show_message);

		txtNumber = (TextView) findViewById(R.id.textNumber);
		txtName = (TextView) findViewById(R.id.textDescription);
		image = (ImageView) findViewById(R.id.imageStatus);
		imageLogo = (ImageView) findViewById(R.id.imgLogo);

		Bundle b = getIntent().getExtras();

		register = (Register) b.get(EAttributes.REGISTER.name());

		if (register.getImage() != null) {
			imageLogo.setTag(register.getImage());
			(new DownloadImageTask()).execute(imageLogo);
		}

		String number = register.getNumber();
		Integer iconId = register.getStatus().getResource();
		String txtDescription = register.getDescription();
		if (txtDescription == null) {
			txtDescription = "Número desconocido";
		}
		txtNumber.setText("Nº:" + number);
		txtName.setText(txtDescription);
		image.setImageDrawable(getResources().getDrawable(iconId));

		sendNotification();
		inscribe();

	}

	private void sendNotification() {

		String title = "TrusteeApp";
		String subject = txtNumber.getText().toString();
		;
		String body = txtName.getText().toString();
		NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notify = new Notification(
				R.drawable.logo, title,
				System.currentTimeMillis());
		PendingIntent pending = PendingIntent.getActivity(
				getApplicationContext(), 0, new Intent(), 0);
		notify.setLatestEventInfo(getApplicationContext(), subject, body,
				pending);
		NM.notify(0, notify);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.finish();
		return true;
	}

	public void finish() {
	    if(register != null && register.getStatus().equals(EStatusConection.SAFE))
	    {
	      notificarDecimaValidacion();
	    }
		super.finish();
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(this.receiver);
	}

	private void inscribe() {
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				finish();
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(EBroadcast.END.toString());
		registerReceiver(receiver, filter);
	}
	
	private void notificarDecimaValidacion() {
	    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

	    int nroValidations = preferences.getInt(EPreferences.NRO_VALIDATION.toString(), 0);
	    nroValidations = nroValidations + 1;

	    SharedPreferences.Editor editor = preferences.edit();
	    editor.putInt(EPreferences.NRO_VALIDATION.toString(), nroValidations);
	    editor.commit();
	    if (nroValidations % 10 == 0) {
	      Intent mIntent = new Intent(this, NotificarDiezValidaciones.class);
	      mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	      mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	      mIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

	      Bundle b = new Bundle();
	      b.putSerializable("NUMERO", nroValidations);
	      mIntent.putExtras(b);
	      this.startActivity(mIntent);
	    }
	  }
}

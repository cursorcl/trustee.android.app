package cl.trustee.activities.formularios;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import cl.trustee.R;
import cl.trustee.activities.ABaseActivity;
import cl.trustee.activities.config.FormularioRegistroUsuario;
import cl.trustee.activities.notifications.NotificarDiezValidaciones;
import cl.trustee.rest.RestClient;

/**
 * @author cursor
 */
public class CalificaContenidoServicio extends ABaseActivity {

	public final static String SEND_FEEDBACK = "send_feedback";
	private RatingBar rating;
	private EditText comment;
	private EditText edtCalificaServicioEmail;
	private Button btnCalificaSend;
	private String email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.califica_contenido_servicio);

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		email = preferences.getString(FormularioRegistroUsuario.KEY_EMAIL, "");
		edtCalificaServicioEmail = (EditText) findViewById(R.id.edtCalificaServicioEmail);
		edtCalificaServicioEmail.setText(email);

		rating = (RatingBar) findViewById(R.id.rating);
		comment = (EditText) findViewById(R.id.comment);
		btnCalificaSend = (Button) findViewById(R.id.btnCalificaSend);
		btnCalificaSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FeedbackTask task = new FeedbackTask();
				task.execute();
				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(CalificaContenidoServicio.this);
				int sendFeedback = preferences.getInt(SEND_FEEDBACK, 0);
				Editor editor = preferences.edit();
				editor.putInt(SEND_FEEDBACK, ++sendFeedback);
				editor.commit();
				if (sendFeedback % 10 == 0) {
					Intent mIntent = new Intent(CalificaContenidoServicio.this,
							NotificarDiezValidaciones.class);
					mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

					Bundle b = new Bundle();
					b.putSerializable("NUMERO", Integer.valueOf(sendFeedback));
					mIntent.putExtras(b);
					CalificaContenidoServicio.this.startActivity(mIntent);
				}
				CalificaContenidoServicio.this.finish();
			}
		});
	}

	private class FeedbackTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			RestClient client = new RestClient();
			List<NameValuePair> pairValues = new ArrayList<NameValuePair>();
			pairValues.add(new BasicNameValuePair("calificacion", String
					.valueOf((int) rating.getRating())));
			pairValues.add(new BasicNameValuePair("comentario", comment
					.getText().toString()));

			email = "";
			if (edtCalificaServicioEmail.getText() != null) {
				email = edtCalificaServicioEmail.getText().toString();
			}
			pairValues.add(new BasicNameValuePair("email", email));

			String url = "http://www.trusteeapp.com/trustee/index.php/opinion/respuesta";
			client.register(url, pairValues);
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result)
				Toast.makeText(CalificaContenidoServicio.this,
						"Gracias por enviar calificación.", Toast.LENGTH_SHORT)
						.show();
		}

		@Override
		protected void onCancelled() {
			Toast.makeText(CalificaContenidoServicio.this, "Tarea cancelada!",
					Toast.LENGTH_SHORT).show();
		}
	}
}

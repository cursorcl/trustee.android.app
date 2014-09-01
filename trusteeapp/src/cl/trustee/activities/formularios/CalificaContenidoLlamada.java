package cl.trustee.activities.formularios;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import cl.trustee.R;
import cl.trustee.activities.ABaseActivity;
import cl.trustee.activities.config.FormularioRegistroUsuario;
import cl.trustee.rest.RestClient;

/**
 * Esta clase permite el ingreso de la informacion posterior a una llamada respondida y validada.
 * 
 * @author cursor
 * 
 */
public class CalificaContenidoLlamada extends ABaseActivity {

  public final static String SEND_FEEDBACK = "send_feedback";
  private RatingBar rating;
  private EditText comment;
  private CheckBox reciveMail;
  private Button btnCalificaSend;
  private String selfPhoneNumber;
  private String email;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.califica_contenido_llamada);

    Bundle b = getIntent().getExtras();
    selfPhoneNumber = (String) b.get("NUMERO");

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    email = preferences.getString(FormularioRegistroUsuario.KEY_EMAIL, "");



    rating = (RatingBar) findViewById(R.id.rating);
    comment = (EditText) findViewById(R.id.comment);
    reciveMail = (CheckBox) findViewById(R.id.reciveMail);
    btnCalificaSend = (Button) findViewById(R.id.btnCalificaSend);
    btnCalificaSend.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        FeedbackTask task = new FeedbackTask();
        task.execute();
        CalificaContenidoLlamada.this.finish();
      }
    });
  }

  private class FeedbackTask extends AsyncTask<Void, Void, Boolean> {
    @Override
    protected Boolean doInBackground(Void... params) {
      RestClient client = new RestClient();
      List<NameValuePair> pairValues = new ArrayList<NameValuePair>();
      pairValues.add(new BasicNameValuePair("numero", selfPhoneNumber));
      pairValues.add(new BasicNameValuePair("calificacion",
          String.valueOf((int) rating.getRating())));
      pairValues.add(new BasicNameValuePair("comentario", comment.getText().toString()));
      pairValues.add(new BasicNameValuePair("recibir", reciveMail.isChecked() ? "1" : "0"));
      pairValues.add(new BasicNameValuePair("email", email));

      String url = "http://www.trusteeapp.com/trustee/index.php/fbk/respuesta";
      client.register(url, pairValues);
      return true;
    }



    @Override
    protected void onPostExecute(Boolean result) {
      if (result)
        Toast.makeText(CalificaContenidoLlamada.this, "Gracias por enviar calificación.",
            Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCancelled() {
      Toast.makeText(CalificaContenidoLlamada.this, "Tarea cancelada!", Toast.LENGTH_SHORT).show();
    }
  }


}

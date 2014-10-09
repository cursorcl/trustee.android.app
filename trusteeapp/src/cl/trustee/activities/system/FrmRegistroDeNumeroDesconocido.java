package cl.trustee.activities.system;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import cl.trustee.R;
import cl.trustee.activities.config.FormularioRegistroUsuario;
import cl.trustee.activities.notifications.NotificarDiezColaboraciones;
import cl.trustee.enums.EPreferences;
import cl.trustee.rest.RestClient;
import cl.trustee.rest.RestClient.TrusteeResponse;

public class FrmRegistroDeNumeroDesconocido extends Activity implements OnClickListener {

  private RadioGroup radioEmpresa;
  private EditText edtNombre;
  private EditText edtRegistroTemporalEmail;
  private TextView txtNumero;
  private Button btnCancelar;
  private Button btnRegistrar;
  private String email;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

    setContentView(R.layout.inform_calls);

    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
    final TextView myTitleText = (TextView) findViewById(R.id.title);
    if (myTitleText != null) {
      myTitleText.setText("Enviar info. para identificación");
    }

    SharedPreferences lpreferences = PreferenceManager.getDefaultSharedPreferences(this);
    email = lpreferences.getString(FormularioRegistroUsuario.KEY_EMAIL, "");

    edtRegistroTemporalEmail = (EditText) findViewById(R.id.edtRegistroTemporalEmail);
    edtRegistroTemporalEmail.setText(email);

    Bundle b = getIntent().getExtras();
    String numero = (String) b.get("NUMERO");

    radioEmpresa = (RadioGroup) findViewById(R.id.rgEmpresa);
    edtNombre = (EditText) findViewById(R.id.edtNombre);
    txtNumero = (TextView) findViewById(R.id.txtNumero);
    txtNumero.setText(numero);
    btnCancelar = (Button) findViewById(R.id.btnCancelar);
    btnCancelar.setOnClickListener(this);
    btnRegistrar = (Button) findViewById(R.id.btnRegistrarItem);
    btnRegistrar.setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {
    if (view == btnCancelar) {
      this.finish();
    } else if (view == btnRegistrar) {

      RestClient restClient = new RestClient();
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("nombre", edtNombre.getText().toString()));
      params.add(new BasicNameValuePair("numero", txtNumero.getText().toString()));
      email = "";
      if (edtRegistroTemporalEmail.getText() != null) {
        email = edtRegistroTemporalEmail.getText().toString();
      }
      if (email != null && !"".equals(email.trim())) {
        params.add(new BasicNameValuePair("email", email));
      }
      RadioButton selectRadio = (RadioButton) findViewById(radioEmpresa.getCheckedRadioButtonId());

      String selection = selectRadio.getText().toString();
      String empresa =
          selection.equalsIgnoreCase("EMPRESA") ? "1"
              : (selection.equalsIgnoreCase("PERSONA") ? "0" : "2");
      params.add(new BasicNameValuePair("empresa", empresa));

      String url = "http://www.trusteeapp.com/trustee/index.php/api/registrotemporal";
      TrusteeResponse response = restClient.executePost(url, params);
      if (response != null && response.httpResponse != null) {
        int statusCode = response.httpResponse.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
          Toast toast = Toast.makeText(this, "Se ha registrado el número", Toast.LENGTH_SHORT);
          toast.show();
        } else if (statusCode == HttpStatus.SC_FORBIDDEN || statusCode == HttpStatus.SC_BAD_REQUEST
            || statusCode == HttpStatus.SC_UNAUTHORIZED) {
          Toast toast = Toast.makeText(this, "No se ha registrado el número", Toast.LENGTH_SHORT);
          toast.show();
        } else {
          Toast toast = Toast.makeText(this, "No se ha registrado el número", Toast.LENGTH_SHORT);
          toast.show();
        }
      }
      notificarDiezColaboraciones();
      this.finish();
    }

  }

  /**
   * Metodo que abrirá dialogo para indicar que se han realizado multiplo de 10 colaboraciones.
   * 
   * @param context El contexto en el que se esta ejecutando.
   */
  private void notificarDiezColaboraciones() {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    int nroTemporalSend = preferences.getInt(EPreferences.NRO_TEMPORAL_SEND.toString(), 0);
    nroTemporalSend = nroTemporalSend + 1;

    SharedPreferences.Editor editor = preferences.edit();
    editor.putInt(EPreferences.NRO_TEMPORAL_SEND.toString(), nroTemporalSend);
    editor.commit();

    if (nroTemporalSend % 10 == 0) {
      Intent mIntent =
          new Intent(FrmRegistroDeNumeroDesconocido.this, NotificarDiezColaboraciones.class);
      mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
      mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      mIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

      Bundle b = new Bundle();
      b.putSerializable("NUMERO", Integer.valueOf(nroTemporalSend));
      mIntent.putExtras(b);
      FrmRegistroDeNumeroDesconocido.this.startActivity(mIntent);
    }
  }
}

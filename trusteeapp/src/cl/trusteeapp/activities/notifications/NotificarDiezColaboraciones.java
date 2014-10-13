package cl.trusteeapp.activities.notifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import cl.trusteeapp.R;

/**
 * Esta clase permite el ingreso de la informacion posterior a una llamada respondida y validada.
 * 
 * @author cursor
 * 
 */
public class NotificarDiezColaboraciones extends Activity {

  public final static String SEND_FEEDBACK = "send_feedback";
  private TextView txtNro;
  private Button btnCompartirColaboracion;
  private Button btnCancelarDiezColaboraciones;
  private Integer nroLlamadas;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle b = getIntent().getExtras();
    nroLlamadas = (Integer) b.get("NUMERO");
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    setContentView(R.layout.notificar_diez_colaboraciones);
    txtNro = (TextView) findViewById(R.id.txtNro);
    txtNro.setText(nroLlamadas.toString());

    btnCompartirColaboracion = (Button) findViewById(R.id.btnSiDiezValidaciones);

    btnCompartirColaboracion.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        recommend();
        NotificarDiezColaboraciones.this.finish();
      }
    });

    btnCancelarDiezColaboraciones = (Button) findViewById(R.id.btnNoDiezValidaciones);
    btnCancelarDiezColaboraciones.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        NotificarDiezColaboraciones.this.finish();
      }
    });
  }

  private void recommend() {
    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
    sharingIntent.setType("text/plain");
    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "TrusteeApp");
    sharingIntent
        .putExtra(
            android.content.Intent.EXTRA_TEXT,
            "Yo uso TrusteeApp y combato el #SPAM móvil únete y habla más seguro! @TrusteeApp - Descarga GRATIS www.trusteeapp.com");
    sharingIntent.putExtra(Intent.EXTRA_TITLE, "TrusteeApp http://www.trusteeapp.com");
    startActivity(Intent.createChooser(sharingIntent, "Share via"));
  }
}

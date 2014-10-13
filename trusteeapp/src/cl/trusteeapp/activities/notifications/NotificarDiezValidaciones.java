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
import cl.trusteeapp.activities.formularios.CalificaContenidoServicio;

/**
 * Esta clase permite el ingreso de la informacion posterior a una llamada respondida y validada.
 * 
 * @author cursor
 * 
 */
public class NotificarDiezValidaciones extends Activity {

  public final static String SEND_FEEDBACK = "send_feedback";
  private TextView txtNro;
  private Button btnSiDiezValidaciones;
  private Button btnNoDiezValidaciones;
  private Integer nroLlamadas;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN); 
    setContentView(R.layout.notificar_diez_validaciones);

    Bundle b = getIntent().getExtras();
    nroLlamadas = (Integer) b.get("NUMERO");

    txtNro = (TextView) findViewById(R.id.txtNro);
    txtNro.setText(nroLlamadas.toString());
    btnSiDiezValidaciones = (Button)findViewById(R.id.btnSiDiezValidaciones); 
    btnSiDiezValidaciones.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        fillForm();
        NotificarDiezValidaciones.this.finish();
      }
    });
    btnNoDiezValidaciones = (Button)findViewById(R.id.btnNoDiezValidaciones); 
    btnNoDiezValidaciones.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        NotificarDiezValidaciones.this.finish();
      }
    });
  }
  private void fillForm() {
    //Aca debo abrir el formulario para llenar la info.
    Intent mIntent = new Intent(this, CalificaContenidoServicio.class);
    this.startActivity(mIntent);
  }
}

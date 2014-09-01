package cl.trustee.activities.notifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import cl.trustee.R;
import cl.trustee.activities.formularios.CalificaContenidoLlamada;

public class ConsultaEnviarCalificacionContenidoLlamada extends Activity implements OnClickListener {

  private Button btnCancel;
  private Button btnRegistrar;
  private String numero;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);

    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);


    Bundle b = getIntent().getExtras();
    numero = (String) b.get("NUMERO");

    setContentView(R.layout.consulta_enviar_calificacion_contenido_llamada);

    btnCancel = (Button) findViewById(R.id.btnNoConsultaContenidoLlamada);
    btnRegistrar = (Button) findViewById(R.id.btnRegistrarConfirm);
    btnRegistrar.setOnClickListener(this);
    btnCancel.setOnClickListener(this);

  }

  @Override
  public void onClick(View paramView) {
    if (paramView == btnRegistrar) {
      Intent intentConfig = new Intent(this, CalificaContenidoLlamada.class);
      Bundle b = new Bundle();
      b.putSerializable("NUMERO", numero);
      intentConfig.putExtras(b);
      this.startActivity(intentConfig);

      this.finish();
    } else if (paramView == btnCancel) {
      this.finish();
    }

  }

}

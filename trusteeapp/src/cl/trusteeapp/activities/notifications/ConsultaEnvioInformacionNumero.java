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
import cl.trusteeapp.activities.system.FrmRegistroDeNumeroDesconocido;

public class ConsultaEnvioInformacionNumero extends Activity implements OnClickListener {

	private Button btnCancel;
	private Button btnRegistrar;
	private TextView txtNumero;
	private String numero;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		Bundle b = getIntent().getExtras();
		numero = (String) b.get("NUMERO");

		setContentView(R.layout.consulta_envio_informacion_numero);

		btnCancel = (Button) findViewById(R.id.btnNoConsultaContenidoLlamada);
		btnRegistrar = (Button) findViewById(R.id.btnRegistrarConfirm);
		txtNumero = (TextView) findViewById(R.id.txtNumeroConfirm);
		txtNumero.setText(numero);
		btnRegistrar.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

	}

	@Override
	public void onClick(View paramView) {
		if (paramView == btnRegistrar) {
			Intent intentConfig = new Intent(this, FrmRegistroDeNumeroDesconocido.class);
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

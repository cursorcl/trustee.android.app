package cl.trustee.activities.system;

import java.util.Date;

import android.content.Context;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cl.trustee.R;
import cl.trustee.view.Register;

/**
 * Utilizado como entrada en cada elemento de la lista de llamadas.
 * 
 * @author cursor
 * 
 */
public class RegisterView extends LinearLayout {

	private ImageView imageStatus;
	private TextView textNumber;
	private TextView textDescription;
	private TextView textDate;

	public RegisterView(Context context) {
		super(context);
		inflate(context, R.layout.message, this);
		imageStatus = (ImageView) findViewById(R.id.imageStatus);
		textNumber = (TextView) findViewById(R.id.textNumber);
		textDescription = (TextView) findViewById(R.id.textDescription);
		textDate = (TextView) findViewById(R.id.textDate);
	}

	/**
	 * Este método nos permitirá asignar los valores a los diferentes
	 * componentes gráficos según el objeto que queramos ver.
	 * 
	 * @param rectangulo
	 */
	public void setRectangulo(Register register) {
		textNumber.setText("" + register.getNumber());
		textDescription.setText("" + register.getDescription());
		imageStatus.setImageResource(register.getStatus().getResource());
		Date date = new Date(register.getDate());
		textDate.setText(DateFormat.format("dd-MM-yyyy hh:mm:ss", date));
	}

}

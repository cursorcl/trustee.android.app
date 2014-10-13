package cl.trusteeapp.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cl.trusteeapp.R;
import cl.trusteeapp.activities.formularios.CalificaContenidoLlamada;
import cl.trusteeapp.activities.system.FrmRegistroDeNumeroDesconocido;
import cl.trusteeapp.enums.EStatusConection;

public class RegisterAdapter extends ArrayAdapter<Register> {

	private List<Register> items;
	private Calendar calendar;
	private Context context;
	private SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy HH:mm:ss",
			Locale.US);

	public RegisterAdapter(Context context, int textViewResourceId,
			List<Register> items) {
		super(context, textViewResourceId, items);
		this.context = context;

		this.items = items;
		if (this.items != null && this.items.isEmpty()) {
			Register register = new Register();
			register.setNumber(null);
			register.setDescription("No hay registro de llamadas");
			this.items.add(register);
		}
		calendar = Calendar.getInstance();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = vi.inflate(R.layout.message, parent, false);

		final Register register = items.get(position);
		if (register != null) {
			TextView txtNumber = (TextView) view.findViewById(R.id.textNumber);
			TextView txtDescr = (TextView) view
					.findViewById(R.id.textDescription);
			ImageView image = (ImageView) view.findViewById(R.id.imageStatus);
			TextView textDate = (TextView) view.findViewById(R.id.textDate);
			Button btnRegistrarItem = (Button) view
					.findViewById(R.id.btnRegistrarItem);
			btnRegistrarItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Bundle b = new Bundle();
					b.putSerializable("NUMERO", register.getNumber());

					if (register.getStatus().equals(EStatusConection.UNSAFE)) {
						
						Intent intentConfig = new Intent(context,
								FrmRegistroDeNumeroDesconocido.class);
						intentConfig.putExtras(b);
						context.startActivity(intentConfig);
					} else {
						Intent intentConfig = new Intent(context,
								CalificaContenidoLlamada.class);
						intentConfig.putExtras(b);
						context.startActivity(intentConfig);
					}
				}
			});
			// Solo se ve para llamadas no seguras.
			boolean visible = register.getStatus().equals(
					EStatusConection.UNSAFE);
			visible = visible
					|| (register.getStatus().equals(EStatusConection.SAFE));
			if (!visible) {
				btnRegistrarItem.setVisibility(View.GONE);
			}
			txtDescr.setText(register.getDescription());
			if (register.getNumber() != null) {
				txtNumber.setText(register.getNumber());
				image.setImageResource(register.getStatus().getResource());
				calendar.setTimeInMillis(register.getDate());
				textDate.setText(format.format(calendar.getTime()));
			} else {
				txtNumber.setText("");
				image.setImageResource(register.getStatus().getResource());
				textDate.setText("");
			}
		}
		return view;
	}

}

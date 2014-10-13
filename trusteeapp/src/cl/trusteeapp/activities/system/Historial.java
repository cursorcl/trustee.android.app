/**
 * 
 */
package cl.trusteeapp.activities.system;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.ListView;
import cl.trusteeapp.R;
import cl.trusteeapp.activities.ABaseActivity;
import cl.trusteeapp.database.DBTrustee;
import cl.trusteeapp.view.Register;
import cl.trusteeapp.view.RegisterAdapter;

/**
 * @author cursor
 * 
 */
public class Historial extends ABaseActivity {

	private List<Register> listRegister = new ArrayList<Register>();
	private RegisterAdapter adapter;
	private ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_calls);
		list = (ListView)findViewById(R.id.logcallList);
		listRegister = DBTrustee.getInstance(this.getApplicationContext()).getListLog();
		adapter = new RegisterAdapter(this, R.layout.message, listRegister);
		list.setAdapter(adapter);
	}
}

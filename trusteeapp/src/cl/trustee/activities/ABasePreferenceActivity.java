/**
 * 
 */
package cl.trustee.activities;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import cl.trustee.R;
import cl.trustee.R.anim;
import cl.trustee.R.array;
import cl.trustee.R.id;
import cl.trustee.R.layout;
import cl.trustee.activities.config.FormularioRegistroUsuario;
import cl.trustee.activities.config.FormularioRegistroCondiciones;
import cl.trustee.activities.system.About;
import cl.trustee.activities.system.Historial;
import cl.trustee.database.DBTrustee;

/**
 * @author cursor
 * 
 */
public class ABasePreferenceActivity extends PreferenceActivity implements OnItemClickListener, OnClickListener {
	private PopupWindow popupMessage;
	private ImageView imgMenu;
	protected View layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.menu_layout, null);
		ListView listViewMenu = (ListView) layout.findViewById(R.id.listViewMenu);
		listViewMenu.setOnItemClickListener(this);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.menu_items,
				R.layout.list_view_menu_ly);
		listViewMenu.setAdapter(adapter);

		imgMenu = (ImageView) findViewById(R.id.header);
		imgMenu.setOnClickListener(this);

	}

	private void recommend() {

		String twitter = "com.twitter.android";
		Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(twitter);
		if (intent != null) {
			// The application exists
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.setPackage(twitter);

			shareIntent.putExtra(android.content.Intent.EXTRA_TITLE, "http://www.trusteeapp.com");
			shareIntent
					.putExtra(Intent.EXTRA_TEXT,
							"TrusteeApp Validador de llamadas, habla con mayor seguridad y evita los fraudes! Descarga GRATIS www.trusteeapp.com");
			startActivity(shareIntent);
		} else {
			// The application does not exist
			// Open GooglePlay or use the default system picker
		}

		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("plain/text");
		sharingIntent
				.putExtra(android.content.Intent.EXTRA_TEXT,
						"TrusteeApp Validador de llamadas, habla con mayor seguridad y evita los fraudes! Descarga GRATIS www.trusteeapp.com");
		startActivity(Intent.createChooser(sharingIntent, "http://www.trusteeapp.com"));

	}

	public boolean confirm() {
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle("Eliminar");
		dialog.setMessage("Seguro de borrar la historia?");
		dialog.setCancelable(false);
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Aceptar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int buttonId) {
				DBTrustee.getInstance(getApplicationContext()).clearLog();
			}
		});
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int buttonId) {
			}
		});
		dialog.setIcon(android.R.drawable.ic_dialog_alert);
		dialog.show();
		return true;
	}

	@Override
	public void onClick(View arg0) {
		try {
			if (popupMessage == null) {
				popupMessage = new PopupWindow(layout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				popupMessage.setBackgroundDrawable(new BitmapDrawable());
				
				popupMessage.setTouchable(true);
				popupMessage.setFocusable(true);
				popupMessage.setOutsideTouchable(true);
				popupMessage.update(0, 0, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				popupMessage.setAnimationStyle(R.anim.fade);
			}
			popupMessage.showAsDropDown(ABasePreferenceActivity.this.imgMenu, 0, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		popupMessage.dismiss();
		switch (position) {
		case 0: // Configuracion
			Intent intentConfig = new Intent(ABasePreferenceActivity.this, FormularioRegistroCondiciones.class);
			startActivity(intentConfig);
			break;
		case 1: // Registrar
			Intent intentRegister = new Intent(ABasePreferenceActivity.this, FormularioRegistroUsuario.class);
			startActivity(intentRegister);
			break;
		case 2: // Ver Historial
			Intent intentHistorial = new Intent(ABasePreferenceActivity.this, Historial.class);
			startActivity(intentHistorial);
			break;
		case 3: // Borrar historial
			confirm();
			break;
		case 4: // Recomendar
			recommend();
			break;
		case 5: // Como funciona
			Intent intentAbout = new Intent(ABasePreferenceActivity.this, About.class);
			startActivity(intentAbout);
			break;
		case 6: // Terminos y condiciones
			String strURL = "http://www.trusteeapp.com/trustee/index.php/site/page/view/condiciones";
			Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(strURL));
			startActivity(myIntent);
			break;

		}
	}

//	private void publishFeedDialog() {
//
//		Bundle params = new Bundle();
//		params.putString("name", "TrusteeApp");
//		params.putString("caption", "TrusteeApp Validador de llamadas");
//		params.putString("description",
//				"TrusteeApp Validador de llamadas, habla con mayor seguridad y evita los fraudes � descarga GRATIS www.trusteeapp.com");
//		params.putString("link", "http://www.trusteeapp.com");
////		params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");
//
//		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(PhotoViewer.this, Session.getActiveSession(), params))
//				.setOnCompleteListener(new OnCompleteListener() {
//
//					@Override
//					public void onComplete(Bundle values, FacebookException error) {
//
//						if (error == null) {
//							// When the story is posted, echo the success
//							// and the post Id.
//							final String postId = values.getString("post_id");
//							if (postId != null) {
//								// Toast.makeText(PhotoViewer.this,"Posted story, id: "+postId,Toast.LENGTH_SHORT).show();
//								Toast.makeText(getApplicationContext(), "Publish Successfully!", Toast.LENGTH_SHORT)
//										.show();
//							} else {
//								// User clicked the Cancel button
//								Toast.makeText(getApplicationContext(), "Publish cancelled", Toast.LENGTH_SHORT).show();
//							}
//						} else if (error instanceof FacebookOperationCanceledException) {
//							// User clicked the "x" button
//							Toast.makeText(getApplicationContext(), "Publish cancelled", Toast.LENGTH_SHORT).show();
//						} else {
//							// Generic, ex: network error
//							Toast.makeText(getApplicationContext(), "Error posting story", Toast.LENGTH_SHORT).show();
//						}
//					}
//
//				}).build();
//		feedDialog.show();
//	}
}

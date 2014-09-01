/**
 * 
 */
package cl.trustee.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import cl.trustee.activities.config.FormularioRegistroCondiciones;
import cl.trustee.activities.config.FormularioRegistroUsuario;
import cl.trustee.activities.formularios.CalificaContenidoServicio;
import cl.trustee.activities.system.About;
import cl.trustee.activities.system.Historial;
import cl.trustee.database.DBTrustee;

/**
 * @author cursor
 * 
 */
public class ABaseActivity extends Activity implements OnItemClickListener, OnClickListener {
  private PopupWindow popupMessage;
  private ImageView imgMenu;
  protected View layout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

    setContentView(R.layout.activity_setup_trustee);

    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

    LayoutInflater inflater =
        (LayoutInflater) getBaseContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    layout = inflater.inflate(R.layout.menu_layout, null);
    ListView listViewMenu = (ListView) layout.findViewById(R.id.listViewMenu);
    listViewMenu.setOnItemClickListener(this);
    ArrayAdapter<CharSequence> adapter =
        ArrayAdapter.createFromResource(getBaseContext(), R.array.menu_items,
            R.layout.list_view_menu_ly);
    listViewMenu.setAdapter(adapter);

    imgMenu = (ImageView) findViewById(R.id.header);
    imgMenu.setOnClickListener(this);

  }

  private void recommend() {
    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
    sharingIntent.setType("text/plain");
    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "TrusteeApp");
    sharingIntent
        .putExtra(
            android.content.Intent.EXTRA_TEXT,
            "TrusteeApp Validador de llamadas, conoce quien te llama y habla con mayor seguridad @TrusteeApp - Descarga GRATIS www.trusteeapp.com");
    sharingIntent.putExtra(Intent.EXTRA_TITLE, "TrusteeApp http://www.trusteeapp.com");
    startActivity(Intent.createChooser(sharingIntent, "Share via"));
  }

  public boolean confirm() {
    AlertDialog dialog = new AlertDialog.Builder(this).create();
    dialog.setTitle("Eliminar");
    dialog.setMessage("Seguro de borrar la historia?");
    dialog.setCancelable(false);
    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Aceptar",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int buttonId) {
            DBTrustee.getInstance(getApplicationContext()).clearLog();
          }
        });
    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int buttonId) {}
        });
    dialog.setIcon(android.R.drawable.ic_dialog_alert);
    dialog.show();
    return true;
  }

  @Override
  public void onClick(View arg0) {
    try {
      if (popupMessage == null) {
        popupMessage =
            new PopupWindow(layout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        popupMessage.setBackgroundDrawable(new BitmapDrawable());

        popupMessage.setTouchable(true);
        popupMessage.setFocusable(true);
        popupMessage.setOutsideTouchable(true);
        popupMessage.update(0, 0, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        popupMessage.setAnimationStyle(R.anim.fade);
      }
      popupMessage.showAsDropDown(ABaseActivity.this.imgMenu, 0, 0);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Override
  public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
    popupMessage.dismiss();
    switch (position) {
      case 0: // Configuracion
        Intent intentConfig = new Intent(ABaseActivity.this, FormularioRegistroCondiciones.class);
        startActivity(intentConfig);
        break;
      case 1: // Registrar
        Intent intentRegister = new Intent(ABaseActivity.this, FormularioRegistroUsuario.class);
        startActivity(intentRegister);
        break;
      case 2: // Ver Historial
        Intent intentHistorial = new Intent(ABaseActivity.this, Historial.class);
        startActivity(intentHistorial);
        break;
      case 3: // Borrar historial
        confirm();
        break;
      case 4: // Recomendar
        recommend();
        break;
      case 5: // Enviar Comentarios (Calificar servicio).
        Intent intentEnviarComentario =
            new Intent(ABaseActivity.this, CalificaContenidoServicio.class);
        startActivity(intentEnviarComentario);
        break;
      case 6: // Como funciona
        Intent intentAbout = new Intent(ABaseActivity.this, About.class);
        startActivity(intentAbout);
        break;
      case 7: // Terminos y condiciones
        String strURL = "http://www.trusteeapp.com/trustee/index.php/site/page/view/condiciones";
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(strURL));
        startActivity(myIntent);
        break;

    }
  }

}

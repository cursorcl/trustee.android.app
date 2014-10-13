package cl.trusteeapp.activities.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.Toast;
import cl.trusteeapp.R;
import cl.trusteeapp.activities.ABasePreferenceActivity;
import cl.trusteeapp.activities.system.SetupTrustee;
import cl.trusteeapp.rest.RestClient;

public class FormularioRegistroUsuario extends ABasePreferenceActivity implements
    OnSharedPreferenceChangeListener {

  public final static String KEY_NAME = "pref_name";
  public final static String KEY_EMAIL = "pref_email";
  public final static String KEY_PHONE = "pref_phone";

  private EditTextPreference name;
  private EditTextPreference phone;
  private EditTextPreference email;
  private ImageButton imgRegistrar;
  private CheckBox chkAceptar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.formulario_registro_usuario);

    WebView webView = (WebView) findViewById(R.id.webView1);
    webView.loadData(getString(R.string.welcome), "text/html", "utf-8");
    webView.setWebViewClient(new WebViewClient() {
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url != null && url.startsWith("http://")) {
          view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
          return true;
        } else {
          return false;
        }
      }
    });
    addPreferencesFromResource(R.xml.preferences);
    name = (EditTextPreference) findPreference(KEY_NAME);
    phone = (EditTextPreference) findPreference(KEY_PHONE);
    email = (EditTextPreference) findPreference(KEY_EMAIL);
    chkAceptar = (CheckBox) findViewById(R.id.chkAceptar);
    chkAceptar.setOnCheckedChangeListener(new OnCheckedChangeListener() {

      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        imgRegistrar.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
      }
    });
    imgRegistrar = (ImageButton) findViewById(R.id.imgSend);
    imgRegistrar.setVisibility(View.INVISIBLE);
    imgRegistrar.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        if (isValidEmail(email.getText())) {

          RestClient client = new RestClient();
          List<NameValuePair> params = new ArrayList<NameValuePair>();
          params.add(new BasicNameValuePair("email", email.getText()));
          params.add(new BasicNameValuePair("name", name.getText()));
          params.add(new BasicNameValuePair("number", phone.getText()));
          // params.add(new BasicNameValuePair("os", getSOVersion()));
          // params.add(new BasicNameValuePair("device", getDeviceName()));

          Toast toast =
              Toast.makeText(FormularioRegistroUsuario.this, "Gracias por activar TrusteeApp.",
                  Toast.LENGTH_SHORT);
          toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
          toast.show();

          String url = "http://www.trusteeapp.com/trustee/index.php/api/personas";
          client.register(url, params);

          Intent intent = new Intent(FormularioRegistroUsuario.this, SetupTrustee.class);
          startActivity(intent);
        } else {
          notifyErrorMailError(email.getText());
        }
      }
    });

  }

  @Override
  protected void onResume() {
    super.onResume();

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

    name.setSummary("Nombre:" + preferences.getString(KEY_NAME, ""));
    email.setSummary("Correo electrónico:" + preferences.getString(KEY_EMAIL, ""));
    phone.setSummary("Teléfono:" + preferences.getString(KEY_PHONE, ""));
    preferences.registerOnSharedPreferenceChangeListener(this);

  }

  private void notifyErrorMailError(String text) {
    Toast toast =
        Toast.makeText(this, String.format("Correo electrónico %s inválido.", text),
            Toast.LENGTH_SHORT);
    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    toast.show();
  }

  public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
    name.setSummary("Nombre:" + preferences.getString(KEY_NAME, ""));
    email.setSummary("Correo electrónico:" + preferences.getString(KEY_EMAIL, ""));
    phone.setSummary("Teléfono:" + preferences.getString(KEY_PHONE, ""));
  }

  public final static boolean isValidEmail(CharSequence target) {
    if (target == null) {
      return false;
    } else {
      return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
  }

  public String getDeviceName() {
    String manufacturer = Build.MANUFACTURER;
    String model = Build.MODEL;
    if (model.startsWith(manufacturer)) {
      return capitalize(model);
    } else {
      return capitalize(manufacturer) + " " + model;
    }
  }

  private String capitalize(String s) {
    if (s == null || s.length() == 0) {
      return "";
    }
    char first = s.charAt(0);
    if (Character.isUpperCase(first)) {
      return s;
    } else {
      return Character.toUpperCase(first) + s.substring(1);
    }
  }

  private String getSOVersion() {
    // String strVersion ="";
    // int version = android.os.Build.VERSION.SDK_INT;
    // switch (version)
    // {
    // case 8:
    // //Android 2.2---
    // strVersion = "Android 2.2";
    // break;
    // case 9:
    // //Android 2.3.1---
    // strVersion = "Android 2.3.1";
    // break;
    // case 10:
    // break;
    // case 11:
    // //Android 3.0---
    // strVersion = "Android 3.0";
    // break;
    // case 14:
    // //Android 4.0---
    // strVersion = "Android 4.0";
    // break;
    // }
    // return strVersion;
    //
    return Build.VERSION.RELEASE;

  }
}

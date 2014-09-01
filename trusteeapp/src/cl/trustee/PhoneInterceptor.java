package cl.trustee;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import cl.trustee.activities.config.FormularioRegistroUsuario;
import cl.trustee.activities.notifications.ConsultaEnviarCalificacionContenidoLlamada;
import cl.trustee.activities.notifications.ConsultaEnvioInformacionNumero;
import cl.trustee.activities.notifications.NotificarEnviandoInformacionValidar;
import cl.trustee.activities.notifications.ShowMessages;
import cl.trustee.database.DBTrustee;
import cl.trustee.enums.EAttributes;
import cl.trustee.enums.EBroadcast;
import cl.trustee.enums.EPreferences;
import cl.trustee.enums.EStatusConection;
import cl.trustee.rest.RestClient;
import cl.trustee.rest.RestClient.TrusteeResponse;
import cl.trustee.view.Register;

/**
 * @author cursor
 */
public class PhoneInterceptor extends BroadcastReceiver {

  private static long timeStarted = -1L; // IMPORTANT!

  private boolean validate = true;

  private boolean history = true;
  private static boolean registerCalls = true;

  private String email = "";

  private String selfPhoneNumber = null;

  private RestClient restClient = new RestClient();

  private static String incommingPhoneNumber;

  private static Register register = new Register();

  private static Long idRegistro;


  private static boolean answered;

  @Override
  public void onReceive(final Context context, final Intent intent) {

    if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
      processCall(context, intent);
    }
    if (intent.getAction().equals(EBroadcast.ACCEPT.toString())) {

      processAccept(context, intent);

    }
  }

  private String getSelfPhoneNumber(final Context context) {
    if (selfPhoneNumber == null) {

      SharedPreferences lpreferences = PreferenceManager.getDefaultSharedPreferences(context);

      TelephonyManager mTelephonyManager;
      mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      selfPhoneNumber = mTelephonyManager.getLine1Number();
      if (selfPhoneNumber == null || "".equals(selfPhoneNumber.trim())) {
        selfPhoneNumber = lpreferences.getString(FormularioRegistroUsuario.KEY_PHONE, "");
      }
    }
    return selfPhoneNumber;
  }

  private void processAccept(final Context context, Intent intent) {
    String name = context.getResources().getString(R.string.PREFERENCES);
    SharedPreferences preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);

    validate = preferences.getBoolean(EPreferences.VALIDATE.toString(), true);
    history = preferences.getBoolean(EPreferences.HISTORY.toString(), true);
    registerCalls = preferences.getBoolean(EPreferences.REGISTER_CALLS.toString(), true);

    SharedPreferences lpreferences = PreferenceManager.getDefaultSharedPreferences(context);
    email = lpreferences.getString(FormularioRegistroUsuario.KEY_EMAIL, "");

    selfPhoneNumber = getSelfPhoneNumber(context);
    
    if(incommingPhoneNumber == null || incommingPhoneNumber.trim().equals(""))
    {
      PhoneInterceptor.register.setNumber("=======1");
    }
    
    PhoneInterceptor.idRegistro = findOnNetPut(context, incommingPhoneNumber);
    if (history) {
      DBTrustee.getInstance(context.getApplicationContext()).addToLog(register);
    }

    Runnable r = new Runnable() {
      @Override
      public void run() {
        PhoneInterceptor.register.setNumber(new String(incommingPhoneNumber));
        Intent newIntent = new Intent(context, ShowMessages.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        newIntent.addCategory(Intent.CATEGORY_HOME);
        newIntent.putExtras(newIntent);
        Bundle b = new Bundle();
        b.putSerializable(EAttributes.REGISTER.name(), register);
        newIntent.putExtras(b);
        context.startActivity(newIntent);
      }
    };
    new Handler().postDelayed(r, 200);
  }

  private void processCall(final Context context, final Intent intent) {

    if (validate) {
      String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
      if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
        Bundle extras = intent.getExtras();
        incommingPhoneNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
        PhoneInterceptor.register.setNumber(new String(incommingPhoneNumber));
        PhoneInterceptor.register.setDescription("--");
        PhoneInterceptor.register.setStatus(EStatusConection.NO_VALITED);

        timeStarted = System.currentTimeMillis();
        PhoneInterceptor.register.setDate(System.currentTimeMillis());
        PhoneInterceptor.register.setImage(null);
        answered = false;
        if (!findOnContacts(context, incommingPhoneNumber)) {
          Runnable r = new Runnable() {
            @Override
            public void run() {
              Intent mIntent = new Intent(context, NotificarEnviandoInformacionValidar.class);
              mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
              mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              mIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
              mIntent.putExtras(mIntent);
              context.startActivity(mIntent);
            }
          };
          new Handler().postDelayed(r, 200);
        } else {
          Runnable r = new Runnable() {
            @Override
            public void run() {
              Intent newIntent = new Intent(context, ShowMessages.class);
              newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
              newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              newIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
              newIntent.addCategory(Intent.CATEGORY_HOME);
              
              
              Bundle bundle = new Bundle();
              bundle.putSerializable(EAttributes.REGISTER.name(), PhoneInterceptor.register);
              newIntent.putExtras(bundle);
              context.startActivity(newIntent);
              if (history) {

                DBTrustee.getInstance(context.getApplicationContext()).addToLog(
                    PhoneInterceptor.register);
              }
            }
          };
          new Handler().postDelayed(r, 200);
        }

      } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) && timeStarted != -1L) {
        timeStarted = System.currentTimeMillis();
        answered = true;
        Intent local = new Intent();
        local.setAction(EBroadcast.END.toString());
        context.sendBroadcast(local);

      }
      // call was ended
      else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
        if (answered) {
          timeStarted = System.currentTimeMillis() - timeStarted;
        } else {
          timeStarted = 0;
        }
        updateTimeOfRegister(context);
        if (register.getStatus().equals(EStatusConection.UNSAFE) && registerCalls) {
          consultaAgregaEmpresa(context);
        } else if (register.getStatus().equals(EStatusConection.SAFE) && timeStarted > 0) {
          consultaFeedback(context);

        }
        timeStarted = -1L;
        answered = false;
        Intent local = new Intent();
        local.setAction(EBroadcast.END.toString());
        context.sendBroadcast(local);
      }
    }

  }

  private void consultaAgregaEmpresa(final Context context) {
    Runnable r = new Runnable() {
      @Override
      public void run() {
        Intent mIntent = new Intent(context, ConsultaEnvioInformacionNumero.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        Bundle b = new Bundle();
        b.putSerializable("NUMERO", register.getNumber());
        mIntent.putExtras(b);
        context.startActivity(mIntent);
      }
    };
    new Handler().postDelayed(r, 200);
  }

  private void consultaFeedback(final Context context) {
    Runnable r = new Runnable() {
      @Override
      public void run() {
        Intent mIntent = new Intent(context, ConsultaEnviarCalificacionContenidoLlamada.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        Bundle b = new Bundle();
        b.putSerializable("NUMERO", register.getNumber());
        mIntent.putExtras(b);
        context.startActivity(mIntent);
      }
    };
    new Handler().postDelayed(r, 200);
  }

  protected void updateTimeOfRegister(Context context) {
    EStatusConection status = PhoneInterceptor.register.getStatus();
    if (status.equals(EStatusConection.SAFE) || status.equals(EStatusConection.UNSAFE)) {
      int iAnswered = answered ? 1 : 0;

      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("idRegistro", String.valueOf(idRegistro.longValue())));
      params.add(new BasicNameValuePair("duration", String.valueOf(timeStarted)));
      params.add(new BasicNameValuePair("answered", String.valueOf(iAnswered)));

      String url = "http://www.trusteeapp.com/trustee/index.php/api/update/empresas";
      TrusteeResponse response = restClient.executePost(url, params);
      if (response != null && response.httpResponse != null) {
        int statusCode = response.httpResponse.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
          // Se grabo con exito.
        } else if (statusCode == HttpStatus.SC_FORBIDDEN || statusCode == HttpStatus.SC_BAD_REQUEST
            || statusCode == HttpStatus.SC_UNAUTHORIZED) {
          // No se pudo grabar
        } else {
          // No se pudo graba
        }
      }
    }
  }

  private Long findOnNetPut(Context context, String number) {
    Long founded = -1L;
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("id", selfPhoneNumber));
    params.add(new BasicNameValuePair("number", new String(number)));
    params.add(new BasicNameValuePair("email", email));

    String url = "http://www.trusteeapp.com/trustee/index.php/api/empresas";
    TrusteeResponse response = restClient.executePost(url, params);
    if (response != null && response.httpResponse != null) {
      int statusCode = response.httpResponse.getStatusLine().getStatusCode();
      if (statusCode == HttpStatus.SC_OK) {
        response.clientePago = 0;
        try {
          response.text = (String) response.json.get("descripcion");
          if (response.json.get("register") != null) {
            String value = (String) response.json.get("register");
            response.idRegister = Long.parseLong(value);
          }
          if (response.json.get("isclient") != null) {
            String value = (String) response.json.get("isclient");
            response.clientePago = Integer.parseInt(value);
          }

          PhoneInterceptor.register.setDescription(response.text);
          if (response.clientePago == 1) {
            PhoneInterceptor.register.setStatus(EStatusConection.SAFE);
          } else {
            PhoneInterceptor.register.setStatus(EStatusConection.SAFE_NO_VERIFICATED);
          }
          PhoneInterceptor.register.setImage(null);
          if (response.json.get("image") != null) {
            String value = (String) response.json.get("image");
            response.image = value;
            PhoneInterceptor.register.setImage("http://www.trusteeapp.com" + value);
          }
          founded = response.idRegister;

        } catch (JSONException e) {
          PhoneInterceptor.register.setDescription("Problemas de red");
          PhoneInterceptor.register.setStatus(EStatusConection.NO_CONECTION);
        }

      } else if (statusCode == HttpStatus.SC_FORBIDDEN || statusCode == HttpStatus.SC_BAD_REQUEST
          || statusCode == HttpStatus.SC_UNAUTHORIZED) {
        PhoneInterceptor.register.setDescription("Sin conexión");
        PhoneInterceptor.register.setStatus(EStatusConection.NO_CONECTION);
      } else {
        PhoneInterceptor.register.setDescription("Número desconocido");
        PhoneInterceptor.register.setStatus(EStatusConection.UNSAFE);
      }
    } else {
      PhoneInterceptor.register.setDescription("Sin conexión");
      PhoneInterceptor.register.setStatus(EStatusConection.NO_CONECTION);
    }
    return founded;
  }

  private boolean findOnContacts(Context context, String number) {

    boolean exist = false;
    String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME};
    Uri contactUri =
        Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

    Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);

    if (cursor.moveToFirst()) {

      String name =
          cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
      PhoneInterceptor.register.setDescription(name);
      PhoneInterceptor.register.setStatus(EStatusConection.NO_VALITED);
      exist = true;
    }

    return exist;
  }


}

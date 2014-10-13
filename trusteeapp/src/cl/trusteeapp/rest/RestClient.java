package cl.trusteeapp.rest;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class RestClient {

	private static final String TAG = "RESTCLIENT";

	public final static class TrusteeResponse {
		public int clientePago = 0;
		public String text = "";
		public Long idRegister = -1L;
		public String image = null;
		public HttpResponse httpResponse = null;
		public JSONObject json;

	}

	public enum RequestMethod {
		GET, POST
	}

	public int responseCode = 0;

	public String message;

	public void register(String url, List<NameValuePair> params) {
		HttpPost request = new HttpPost(url);
		if (params != null)
			try {
				request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				executeRequest(request);
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, e.toString());
			}
	}

	public TrusteeResponse executePost(String url, List<NameValuePair> params) {
		TrusteeResponse response = null;
		HttpPost request = new HttpPost(url);
		if (params != null) {
			try {
				UrlEncodedFormEntity encoded = new UrlEncodedFormEntity(params,
						HTTP.UTF_8);
				request.setEntity(encoded);
				response = executeRequest(request);
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, e.toString());
			}
		}
		return response;
	}

	public TrusteeResponse executeGET(String url) throws Exception {
		// add parameters
		HttpGet request = new HttpGet(url);
		TrusteeResponse response = executeRequest(request);
		return response;
	}

	private TrusteeResponse executeRequest(HttpUriRequest request) {

		HttpParams my_httpParams = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(my_httpParams, 30000);
		HttpConnectionParams.setSoTimeout(my_httpParams, 25000);
		HttpClient client = new DefaultHttpClient(my_httpParams);
		HttpResponse httpResponse;
		TrusteeResponse response = null;
		try {
			httpResponse = client.execute(request);
			response = new TrusteeResponse();
			response.httpResponse = httpResponse;

			int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.i(TAG, "StatusCode=" + statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = response.httpResponse.getEntity();
				if (entity != null) {
					String sEntity;
					try {
						sEntity = EntityUtils.toString(entity);
						entity.consumeContent();
						if (!"".equals(sEntity)) {
							response.json = new JSONObject(sEntity.trim());
						}

					} catch (JSONException ex) {
						response.text = "JSON Exception";
						response.idRegister = -1L;
						Log.e(TAG, ex.toString());
					}
				}
			}

			client.getConnectionManager().shutdown();
		} catch (ConnectTimeoutException e) {
			Log.e(TAG, e.toString());
		} catch (SocketTimeoutException e) {
			Log.e(TAG, e.toString());
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return response;
	}

}

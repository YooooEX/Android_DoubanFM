package com.schutzstaffel.fmplayer.utils;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.schutzstaffel.fmplayer.MainActivity;

public class UserActions {
	private static Context mContext;
	private static boolean isIntl = false;
	public static boolean isLogin = false;

	public static void intl(Context context) {
		mContext = context;
		isIntl = true;

	}

	/**
	 * Login to server
	 */
	public static void Login() {
		if (isIntl) {
			StringRequest loginInfos = new StringRequest(Method.POST,
					ClientAPIs.API_LOGIN_URL, new Response.Listener<String>() {

						@Override
						public void onResponse(String response) {
							// Log.i(TAG, response);
							try {
								JSONObject jObj = new JSONObject(response);
								ClientAPIs.setid(jObj
										.getString(ClientAPIs.API_USER_ID));
								ClientAPIs.setname(jObj
										.getString(ClientAPIs.API_USERNAME));
								ClientAPIs.settoken(ClientAPIs.API_TOKEN);
								ClientAPIs.setexpire(ClientAPIs.API_EXPIRE);
								Toast.makeText(mContext,
										"ª∂”≠ªÿ¿¥," + ClientAPIs.getname(),
										Toast.LENGTH_LONG).show();
								isLogin = true;
							} catch (JSONException e) {
								e.printStackTrace();
								Log.e(ClientAPIs.TAG, response);
							}
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Log.e(ClientAPIs.TAG,
									"Login err:" + error.getMessage());
						}
					}) {
				// Set Params
				@Override
				protected HashMap<String, String> getParams()
						throws AuthFailureError {
					HashMap<String, String> hashMap = new HashMap<String, String>();
					hashMap.put(ClientAPIs.API_APP_NAME,
							ClientAPIs.APP_NAME_VALUE);
					hashMap.put(ClientAPIs.API_VERSION,
							ClientAPIs.VERSION_VALUE);
					hashMap.put("email", "yourmail");
					hashMap.put("password", "yourpass");
					return hashMap;
				}

				// Set Headers
				public Map<String, String> getHeaders() throws AuthFailureError {
					HashMap<String, String> headers = new HashMap<String, String>();
					headers.put("Accept", "application/json");
					headers.put("Content-Type",
							"application/x-www-form-urlencoded");
					return headers;
				}

			};
			MainActivity.mQueue.add(loginInfos);
		}

	}

}

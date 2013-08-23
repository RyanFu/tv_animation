package com.jumplife.tvanimation.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jumplife.tvanimation.entity.Animate;
import com.jumplife.tvanimation.sqlitehelper.SQLiteTvAnimationHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TvAnimationAPI {

	private String urlAddress;
	private HttpURLConnection connection;
	private String requestedMethod;
	private int connectionTimeout;
	private int readTimeout;
	private boolean usercaches;
	private boolean doInput;
	private boolean doOutput;
	
	public static final String TAG = "USADRAMA_API";
	public static final boolean DEBUG = true;
	
	public TvAnimationAPI(String urlAddress, int connectionTimeout, int readTimeout) {
		this.urlAddress = new String(urlAddress + "/");
		this.connectionTimeout = connectionTimeout;
		this.readTimeout = readTimeout;
		this.usercaches = false;
		this.doInput = true;
		this.doOutput = true;
	}
	
	public TvAnimationAPI(String urlAddress) {
		this(new String(urlAddress), 5000, 5000);
	}
	
	public TvAnimationAPI() {
		this(new String("http://106.186.21.179:81"));
	}
	
	public int connect(String requestedMethod, String apiPath) {
		int status = -1;
		try {
			URL url = new URL(urlAddress + apiPath);
			
			if(DEBUG)
				Log.d(TAG, "URL: " + url.toString());
			connection = (HttpURLConnection) url.openConnection();
					
			connection.setRequestMethod(requestedMethod);
			connection.setReadTimeout(this.readTimeout);
			connection.setConnectTimeout(this.connectionTimeout);
			connection.setUseCaches(this.usercaches);
			connection.setDoInput(this.doInput);
			connection.setDoOutput(this.doOutput);
			connection.setRequestProperty("Content-Type",  "application/json;charset=utf-8");
			
			connection.connect();

		} 
		catch (MalformedURLException e1) {
			e1.printStackTrace();
			return status;
		}
		catch (IOException e) {
			e.printStackTrace();
			return status;
		}
		
		return status;
	}
	
	public void disconnect()
	{
		connection.disconnect();
	}
	
	public ArrayList<Animate> getTvAnimationsIdViewsEps(){
		ArrayList<Animate> tvAnimations = new ArrayList<Animate>(100);
		String message = getMessageFromServer("GET", "api/v1/dramas.json", null);
		if(message == null) {
			return null;
		}
		else {			
			try {
				JSONArray tvAnimationsArray = new JSONArray(message.toString());
				for(int i=0; i<tvAnimationsArray.length(); i++) {
					JSONObject tvAnimationObject = tvAnimationsArray.getJSONObject(i);
					Animate tmp = new Animate();
					tmp.setId(tvAnimationObject.getInt("id"));
					tmp.setViews(tvAnimationObject.getInt("views"));
					tmp.setEpsNumStr(tvAnimationObject.getString("eps_num_str"));
					tvAnimations.add(tmp);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return tvAnimations;
	}
	
	public void AddTvAnimationsFromInfo(SQLiteTvAnimationHelper instance, SQLiteDatabase db, String idlst) {
		Log.d(TAG, "id list : " + idlst);
		String message = getMessageFromServer("GET", "api/v1/dramas/dramas_info.json?dramas_id=" + idlst, null);
		
		if(message != null) {
			try {
				JSONArray tvAnimationArray;		
				tvAnimationArray = new JSONArray(message.toString());
				ArrayList<Animate> tvAnimations = new ArrayList<Animate>();
				for (int i = 0; i < tvAnimationArray.length() ; i++) {
					JSONObject tvAnimationJson = tvAnimationArray.getJSONObject(i);
					Animate tvAnimation = TvAnimationJsonToClass(tvAnimationJson);
					tvAnimations.add(tvAnimation);
				}
				if(tvAnimations != null && tvAnimations.size() > 0) {
					Log.d(TAG, "Insert New Animate.");
					instance.insertTvAnimations(db, tvAnimations);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Animate getTvAnimationEpsNumViews(int tvAnimationId, Animate tvAnimation){
		String message = getMessageFromServer("GET", "api/v1/dramas/new_dramas_info.json?dramas_id=" + tvAnimationId, null);
		if(message == null) {
			return null;
		}
		else {			
			try {
				JSONArray tvAnimationArray = new JSONArray(message.toString());
				for(int i=0; i<tvAnimationArray.length(); i++) {
					JSONObject tvAnimationObject = tvAnimationArray.getJSONObject(i);
					tvAnimation.setEpsNumStr(tvAnimationObject.getString("eps_num_str"));
					tvAnimation.setViews(tvAnimationObject.getInt("views"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return tvAnimation;
			}
		}
		return tvAnimation;
	}
	
	public String getVideoLink(int tvAnimationId, int epsNum){
		String link = null;
		String message = getMessageFromServer("GET", "/api/v1/eps/find_by_drama_and_ep_num.json?drama_id=" 
																					+ tvAnimationId + "&num=" + epsNum, null);
		if(message == null) {
			return null;
		}
		else {			
			try {
				JSONArray linkArray = new JSONArray(message.toString());
				for(int i=0; i<linkArray.length(); i++) {
					link = linkArray.getString(i);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return link;
			}
		}
		return link;
	}
	
	public boolean updateViews(int TvAnimationId) {
		boolean result = false;

		try{
			DefaultHttpClient httpClient = new DefaultHttpClient();
			String url = urlAddress + "api/v1/dramas/" + TvAnimationId + ".json";						
			if(DEBUG)
				Log.d(TAG, "URL : " + url);
			
			HttpPut httpPut = new HttpPut(url);
			HttpResponse response = httpClient.execute(httpPut);
			
			StatusLine statusLine =  response.getStatusLine();
			if (statusLine.getStatusCode() == 200){
				result = true;
			}
		} 
	    catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;
		} 
		catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;
		} 
		catch (IOException e){
			e.printStackTrace();
			return result;
		}	
		return result;
	}
	
	public boolean report(int tvAnimationId, int chapterNum) {
		boolean result = false;
		String message = getMessageFromServer("GET", "api/v1/eps/update_ep_error.json?drama_id=" + tvAnimationId + "&num=" + chapterNum, null);
		if(message == null) {
			return result;
		} else {
			try {
				JSONObject object = new JSONObject(message.toString());
				if(object.getString("message").equals("success"))
					result = true;
			} catch (JSONException e){
				e.printStackTrace();
				return result;
			}	
		}
		return result;
	}
	
	public String getTvAnimationsHistory(){
		String history = "";
		String message = getMessageFromServer("GET", "api/v1/drama_history.json", null);
		if(message == null) {
			return null;
		}
		else {			
			try {
				JSONArray tvAnimationsArray = new JSONArray(message.toString());
				for(int i=0; i<tvAnimationsArray.length(); i++) {
					JSONObject tvAnimationObject = tvAnimationsArray.getJSONObject(i);
					history = history 
							+ "<b>" + tvAnimationObject.getString("release_date") + "</b>" 
							+ "<p>" + tvAnimationObject.getString("tvAnimations_str") + "</p><br><br><hr>";
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return history;
	}
	
	public String getMessageFromServer(String requestMethod, String apiPath, JSONObject json) {
		URL url;
		try {
			url = new URL(this.urlAddress +  apiPath);
			if(DEBUG)
				Log.d(TAG, "URL: " + url);
				
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(requestMethod);
			
			connection.setRequestProperty("Content-Type",  "application/json;charset=utf-8");
			if(requestMethod.equalsIgnoreCase("POST"))
				connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.connect();
			
			
			if(requestMethod.equalsIgnoreCase("POST")) {
				OutputStream outputStream;
				
				outputStream = connection.getOutputStream();
				if(json != null)
					outputStream.write(json.toString().getBytes());
				outputStream.flush();
				outputStream.close();
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder lines = new StringBuilder();
			String tempStr;
			
			while ((tempStr = reader.readLine()) != null) {
	            lines = lines.append(tempStr);
	        }
			if(DEBUG)
				Log.d(TAG, lines.toString());
			
			reader.close();
			connection.disconnect();
			
			return lines.toString();
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} 
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Animate TvAnimationJsonToClass (JSONObject tvAnimationJson) {
		Animate tvAnimation = null;
		
		if(tvAnimationJson == null) {
			return null;
		}
		else {
			try {
				String season = "";
				String poster = "";
				
				if(!tvAnimationJson.getString("season").equalsIgnoreCase("null"))
					season = tvAnimationJson.getString("season");
				if(!tvAnimationJson.getString("poster").equalsIgnoreCase("null"))
					poster = tvAnimationJson.getString("poster");
				
				tvAnimation = new Animate(tvAnimationJson.getInt("id"), tvAnimationJson.getString("name"), season, 
						poster, tvAnimationJson.getString("introduction"), "", 0, tvAnimationJson.getInt("type_id"));				 
			} 
			catch (JSONException e) {
				e.printStackTrace();
				return tvAnimation;
			}	
		}
		return tvAnimation;
	}
	
	public String[] getPromotion() {
				
		String message = getMessageFromServer("GET", "api/promotion.json", null);
		String[] tmp = new String[5];
				
		if(message == null) {
			return null;
		}
		try{
			JSONObject responseJson = new JSONObject(message);
			
			tmp[0] = (responseJson.getString("picture_link"));
			tmp[1] = (responseJson.getString("link"));
			tmp[2] = (responseJson.getString("tilte"));
			tmp[3] = (responseJson.getString("description"));
			tmp[4] = (responseJson.getString("version"));
		} 
		catch (JSONException e){
			e.printStackTrace();
			return null;
		}
		
		return tmp;
	}
	
	public boolean postGcm(String regId, Context context) {
		boolean result = false;
		
		try{
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(urlAddress + "api/v1/devices?registration_id="	+ regId + "&device_id=");
			HttpResponse response = httpClient.execute(httpPost);

			StatusLine statusLine =  response.getStatusLine();
			if (statusLine.getStatusCode() == 200){
				result = true;
			}
		} 
	    catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;
		} 
		catch (ClientProtocolException e) {
			e.printStackTrace();
			return result;
		} 
		catch (IOException e){
			e.printStackTrace();
			return result;
		}	
		return result;
	}
	
	public void getVersionCode(int[] mVersionCode, String[] msg){
		String message = getMessageFromServer("GET", "api/version_check.json", null);
		if(message == null) {
			return;
		}
		else {			
			try {
				JSONObject jsonObject =  new JSONObject(message.toString());
				mVersionCode[0] = jsonObject.getInt("version_code");
				msg[0] = jsonObject.getString("message");		
			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
	public void getServerStatus(int[] serverCode, String[] msg){
		String message = getMessageFromServer("GET", "api/sever_check.json", null);
		if(message == null) {
			return;
		}
		else {			
			try {
				JSONObject jsonObject =  new JSONObject(message.toString());
				serverCode[0] = jsonObject.getInt("sever_state");
				msg[0] = jsonObject.getString("message");		
			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
	public String getUrlAddress() {
		return urlAddress;
	}
	public void setUrlAddress(String urlAddress) {
		this.urlAddress = urlAddress;
	}
	public HttpURLConnection getConnection() {
		return connection;
	}
	public void setConnection(HttpURLConnection connection) {
		this.connection = connection;
	}
	public String getRequestedMethod() {
		return requestedMethod;
	}
	public void setRequestedMethod(String requestedMethod) {
		this.requestedMethod = requestedMethod;
	}
	public int getConnectionTimeout() {
		return connectionTimeout;
	}
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	public int getReadTimeout() {
		return readTimeout;
	}
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	public boolean isUsercaches() {
		return usercaches;
	}
	public void setUsercaches(boolean usercaches) {
		this.usercaches = usercaches;
	}
	public boolean isDoInput() {
		return doInput;
	}
	public void setDoInput(boolean doInput) {
		this.doInput = doInput;
	}
	public boolean isDoOutput() {
		return doOutput;
	}
	public void setDoOutput(boolean doOutput) {
		this.doOutput = doOutput;
	}
}

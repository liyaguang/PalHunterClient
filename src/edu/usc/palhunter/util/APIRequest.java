package edu.usc.palhunter.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.net.Uri.Builder;
import android.os.AsyncTask;
import edu.usc.palhunter.config.Config;

public class APIRequest {
  public static interface APICallback {
    void process(String result);
  }

  public static void get(String api, JSONObject params, APICallback callback) {

    String addr = String.format("%s/%s", Config.getApiBaseAddr(), api);
    // addr = "http://www.google.com/";
    Builder builder = Uri.parse(addr).buildUpon();
    Iterator<String> iterator = params.keys();
    while (iterator.hasNext()) {
      String key = iterator.next();
      String value = "";
      try {
        value = params.getString(key);
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      builder.appendQueryParameter(key, value);
    }
    addr = builder.build().toString();
    HttpGet httpGet = new HttpGet(addr);
    new RequestTask(httpGet, callback).execute();
  }

  public static void post(String api, JSONObject params, APICallback callback) {
    String addr = String.format("%s/%s", Config.getApiBaseAddr(), api);
    HttpPost httpPost = new HttpPost(addr);
    List<NameValuePair> param = new ArrayList<NameValuePair>();
    Iterator<String> iterator = params.keys();
    try {
      while (iterator.hasNext()) {
        String key = iterator.next();
        String value = "";
        value = params.get(key).toString();
        param.add(new BasicNameValuePair(key, value));
      }
      httpPost.setEntity(new UrlEncodedFormEntity(param));
      new RequestTask(httpPost, callback).execute();
    } catch (JSONException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  private static class RequestTask extends AsyncTask<Void, Void, String> {
    HttpUriRequest request;
    APICallback callback;

    public RequestTask(HttpUriRequest request, APICallback callback) {
      this.request = request;
      this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... paramVarArgs) {
      // TODO Auto-generated method stub
      // TODO Auto-generated method stub
      String result = "";
      HttpClient client = new DefaultHttpClient();
      try {
        HttpResponse response = client.execute(request);
        result = Utility.streamToString(response.getEntity().getContent());
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return result;
    }

    @Override
    protected void onPostExecute(String result) {
      // TODO Auto-generated method stub
      if (callback != null) {

        this.callback.process(result);
      }
    }
  }
}

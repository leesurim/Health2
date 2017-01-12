package com.example.user.health;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class async extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... arg) {
        // TODO Auto-generated method stub
        String response = null;
        OutputStreamWriter out = null;
        BufferedWriter writer = null;

        ArrayList<NameValuePair> post = new ArrayList<NameValuePair>();
        post.add(new BasicNameValuePair("datetime",arg[0]));
        post.add(new BasicNameValuePair("record",arg[1]));

        try{
            URL u = new URL("http://gorapaduk.dothome.co.kr/project/health.php");
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("POST");

            conn.setDoOutput(true);

            out = new OutputStreamWriter(conn.getOutputStream(),"UTF-8");

            writer = new BufferedWriter(out);
            writer.write(getQuery(post));
            writer.flush();
            writer.close();

            //�����κ��� ������ ����
            response = conn.getResponseMessage();
            Log.d("RESPONSE", "The response is: " + response);

        }catch(MalformedURLException ex){
            ex.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return response;
    }


    //��׶��忡�� �۾��� ���
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        if(result != null){
            Log.d("ASYNC","result="+result);
        }
    }

    //NameValuePair�ȿ� �ִ� Ű,�� ���� UTF-8�� ��ȯ��Ŵ
    private String getQuery(ArrayList<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params){
            if(first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(),"UTF-8"));
        }
        return result.toString();
    }
}

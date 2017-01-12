package com.example.user.health;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Main4Activity extends AppCompatActivity {

    String myJSON;
    private View chart;
    LinearLayout resultLayout;

    private static final String TAG_RESULTS="result";
    private static final String TAG_ID = "idx";
    private static final String TAG_NAME = "datetime";
    private static final String TAG_ADD ="record";

    JSONArray peoples = null;

    ArrayList<HashMap<String, String>> personList;

    ListView list;
    public static final String TAG = "main";
    private final String URL_POST = "http://gorapaduk.dothome.co.kr/project/health3.php";
    AsyncHttpClient client;
    hellohttp hellohttpResponse;

    public String MillToDate(){
        long mills = System.currentTimeMillis();
        String pattern = "yyyyMMdd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String date = (String) formatter.format(new Timestamp(mills));
        return date;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        list = (ListView) findViewById(R.id.listView);
        personList = new ArrayList<HashMap<String,String>>();
        resultLayout = (LinearLayout) findViewById(R.id.chart);

        String date1 = MillToDate();
        int numInt = Integer.parseInt(date1);
        //
        try {
            // Start
            client = new AsyncHttpClient();
            hellohttpResponse = new hellohttp();
            RequestParams params2 = new RequestParams();
            int num1 = numInt - 7;
            int num2 = numInt - 6;
            int num3 = numInt - 5;
            int num4 = numInt - 4;
            int num5 = numInt - 3;
            int num6 = numInt - 2;
            int num7 = numInt - 1;

            Log.d("main", "num1 : " +num1);


            String Snum1 = String.valueOf(num1);
            String Snum2 = String.valueOf(num2);
            String Snum3 = String.valueOf(num3);
            String Snum4 = String.valueOf(num4);
            String Snum5 = String.valueOf(num5);
            String Snum6 = String.valueOf(num6);
            String Snum7 = String.valueOf(num7);
            Log.d("main", Snum1);

            params2.put("id1", Snum1);
            params2.put("id2", Snum2);
            params2.put("id3", Snum3);
            params2.put("id4", Snum4);
            params2.put("id5", Snum5);
            params2.put("id6", Snum6);
            params2.put("id7", Snum7);

            client.post(URL_POST, params2, hellohttpResponse);
            Log.d(TAG, "Hello");
            // End
        } catch (Exception ex) {
            Log.d(TAG,"Catch Err : " + ex);
            ex.printStackTrace();
        }
    }


    protected int[] showList(){
        int temp[]  = new int[7];
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            Log.d("main","Test : " + peoples);
            for(int i=0;i<peoples.length();i++){
                JSONObject c = peoples.getJSONObject(i);
                Log.d("main","Test : " + c);
                String idx = c.getString(TAG_ID);
                String datetime = c.getString(TAG_NAME);
                String record = c.getString(TAG_ADD);
                HashMap<String,String> persons = new HashMap<String,String>();
                int torecord = Integer.parseInt(record);
                temp[i] = torecord;
                persons.put(TAG_ID,idx);
                persons.put(TAG_NAME,datetime);
                persons.put(TAG_ADD,record);
                personList.add(persons);

            }

            ListAdapter adapter = new SimpleAdapter(
                    Main4Activity.this, personList, R.layout.list_item,
                    new String[]{TAG_ID,TAG_NAME,TAG_ADD},
                    new int[]{R.id.id, R.id.time, R.id.calory}
            );

            list.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }

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

    private void createChart() {
        Log.d("main","chartstart");

        int[] x = { 0, 1, 2, 3, 4, 5 ,6};
        int y[]= showList();
        String date1 = MillToDate();
        int numInt = Integer.parseInt(date1);
        int num1 = numInt - 7;
        int num2 = numInt - 6;
        int num3 = numInt - 5;
        int num4 = numInt - 4;
        int num5 = numInt - 3;
        int num6 = numInt - 2;
        int num7 = numInt - 1;
        String Snum1 = String.valueOf(num1);
        String Snum2 = String.valueOf(num2);
        String Snum3 = String.valueOf(num3);
        String Snum4 = String.valueOf(num4);
        String Snum5 = String.valueOf(num5);
        String Snum6 = String.valueOf(num6);
        String Snum7 = String.valueOf(num7);

        String[] _friends = new String[] { Snum1, Snum2, Snum3, Snum4, Snum5, Snum6, Snum7};
        XYSeries expenseSeries = new XYSeries("Height");
        // Adding data to Height Series
        for (int k = 0; k < y.length; k++) {
            expenseSeries.add(k, y[k]);
        }
        // Creating a dataset to hold height series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        // Adding Height Series to dataset
        dataset.addSeries(expenseSeries);

        // Creating XYSeriesRenderer to customize expenseSeries
        XYSeriesRenderer heightRenderer = new XYSeriesRenderer();
        heightRenderer.setColor(Color.BLUE);
        heightRenderer.setFillPoints(true);
        heightRenderer.setDisplayChartValues(true);

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setXLabels(0);
        renderer.setChartTitle("Record chart ");
        renderer.setXTitle("State");
        renderer.setYTitle("Quantity");

        /***
         * Customizing graphs
         */
        // setting text size of the title
        renderer.setChartTitleTextSize(50);
        // setting text size of the axis title
        renderer.setAxisTitleTextSize(30);
        // setting text size of the graph lable
        renderer.setLabelsTextSize(30);
        // setting zoom buttons visiblity
        renderer.setZoomButtonsVisible(true);
        // setting pan enablity which uses graph to move on both axis
        renderer.setPanEnabled(true, true);
        // setting click false on graph
        renderer.setClickEnabled(false);
        // setting zoom to false on both axis
        renderer.setZoomEnabled(true, true);
        // setting lines to display on y axis
        renderer.setShowGridY(true);
        // setting lines to display on x axis
        renderer.setShowGridX(true);
        // setting legend to fit the screen size
        renderer.setFitLegend(true);
        // setting displaying line on grid
        renderer.setShowGrid(false);
        // Setting background color of the graph to transparent
        renderer.setBackgroundColor(Color.TRANSPARENT);
        // Setting margin color of the graph to transparent
        renderer.setMarginsColor(getResources().getColor(android.R.color.transparent));
        renderer.setApplyBackgroundColor(true);
        renderer.setScale(1f);
        // setting x axis point size
        renderer.setBarSpacing(0.5f);
        renderer.setZoomRate(1.0f);
        renderer.setPointSize(4f);
        renderer.setXLabelsAlign(Paint.Align.LEFT);
        renderer.setYLabelsAlign(Paint.Align.LEFT);
        // setting the margin size for the graph in the order top, left, bottom,
        // right
        renderer.setMargins(new int[] { 0,10,0,10});

        for (int j = 0; j < y.length; j++) {
            renderer.addXTextLabel(j, _friends[j]);
            renderer.setXLabelsPadding(6);
            //renderer.setYLabelsPadding(10);
        }

        // Adding heightRender to multipleRenderer
        // Note: The order of adding dataseries to dataset and renderers to
        // multipleRenderer
        // should be same
        renderer.addSeriesRenderer(heightRenderer);

        // this part is used to display graph on the xml
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.chart);
        // remove any views before u paint the chart
        chartContainer.removeAllViews();
        //drawing bar chart
        chart = ChartFactory.getBarChartView(Main4Activity.this, dataset, renderer, BarChart.Type.DEFAULT);
        // adding the view to the linearlayout
        chartContainer.addView(chart);

    }


    public class hellohttp extends AsyncHttpResponseHandler {

        ProgressDialog dialog;

        /**통신 시작시에 실행된다.*/
        @Override
        public void onStart() {
            /*
            dialog = new ProgressDialog(MyReceiver.this);
            dialog.setMessage("잠시만 기다려주세요...");
            dialog.setCancelable(false);
            dialog.show();
            */
        }

        /**통신 접속 실패시 호출된다.
         * @param stateCode     상태코드
         * @param header        HTTP Header
         * @param body          HTTP body
         * @param error         에러정보 객체
         */

        @Override
        public void onFailure(int stateCode, Header[] header, byte[] body, Throwable error) {
            /*
            String errMsg = "State Code :" + stateCode + "\n";
            errMsg += "Error Message :" + error.getMessage();
            textView.setText(errMsg);
            */
            Log.d(TAG,"Fail : " + error);
        }

        /**통신 접속 성공시 호출된다.
         * @param stateCode     상태코드. (정상결과일 경우 200)
         * @param header        HTTP Header
         * @param body          HTTP Body (브라우저에 보여지는 내용)
         */
        @Override
        public void onSuccess(int stateCode, Header[] header, byte[] body) {
            try {
                String result = new String(body, "UTF-8");
                Log.d(TAG,"Success! : " + result);
                myJSON = result;
                showList();
                createChart();
                Log.d("main","listView");
            } catch (UnsupportedEncodingException e) {
                Log.d(TAG,"Suc-Err : " + e);
                e.printStackTrace();
            }

        }

        /**성공, 실패 여부에 상관 없이 통신이 종료되면 실행된다.*/
        @Override
        public void onFinish() {
            //dialog.dismiss();
            //dialog = null;
        }
    } //end class
}
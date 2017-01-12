package com.example.user.health;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class Main3Activity extends AppCompatActivity implements SensorEventListener {

    TextView myOutput;
    TextView myRec;
    Button myBtnStart;
    Button myBtnRec;
    EditText edit;
    private View chart;
    private Button btnChart;

    private int[] _x = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
    private String[] _friends = new String[] { "Slow", "Nolmal", "Fast", "Running", "Up", "Down"};

    final static int Init =0;
    final static int Run =1;
    final static int Pause =2;

    int cur_Status = Init; //현재의 상태를 저장할변수를 초기화함.
    int myCount=1;
    long myBaseTime;
    long myPauseTime;

    private int[] temp = new int[6];
    private double x = SensorManager.DATA_X;
    private double y = SensorManager.DATA_Y;
    private double z = SensorManager.DATA_Z;

    ArrayList<Double> V_x = new ArrayList();
    ArrayList<Double> V_y = new ArrayList();
    ArrayList<Double> V_z = new ArrayList();

    private long lastTime;

    ArrayList<String> result = new ArrayList();

    private double Acc_mean_X;
    private double Acc_mean_Y;
    private double Acc_mean_Z;
    private double Acc_max_X;
    private double Acc_max_Y;
    private double Acc_max_Z;
    private double Acc_min_X;
    private double Acc_min_Y;
    private double Acc_min_Z;

    private SensorManager mSensorM;
    private Sensor accelsensor;
    LinearLayout resultLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        myOutput = (TextView) findViewById(R.id.time_out);
        myRec = (TextView) findViewById(R.id.record);
        myBtnStart = (Button) findViewById(R.id.btn_start);
        myBtnRec = (Button) findViewById(R.id.btn_rec);
        edit = (EditText) findViewById(R.id.editText2);
        resultLayout = (LinearLayout) findViewById(R.id.chart);
        mSensorM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelsensor = mSensorM.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        //click
    }

  /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
   Code == KeyEvent.KEYCODE_BACK )
            { {
        if ( event.getAction() == KeyEvent.ACTION_DOWN )
        {
            if ( key
            }
            if ( keyCode == KeyEvent.KEYCODE_HOME )
            {
            }
        }
        return super.onKeyDown(keyCode, event);
    }*/

    public void myOnClick(View v){
        switch(v.getId()){

            case R.id.btn_start: //시작버튼을 클릭했을때 현재 상태값에 따라 다른 동작을 할수있게끔 구현.
                switch(cur_Status){
                    case Init:
                        myBaseTime = SystemClock.elapsedRealtime();
                        System.out.println(myBaseTime);
                        //myTimer이라는 핸들러를 빈 메세지를 보내서 호출
                        myTimer.sendEmptyMessage(0);
                        myBtnStart.setText("멈춤"); //버튼의 문자"시작"을 "멈춤"으로 변경
                        myBtnRec.setEnabled(true); //기록버튼 활성
                        cur_Status = Run; //현재상태를 런상태로 변경
                        //mSensorM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                        //accelsensor = mSensorM.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
                        //mSensorM.registerListener(Main3Activity.this, accelsensor, SensorManager.SENSOR_DELAY_NORMAL);
                        break;
                    case Run:
                        onPause();
                        Log.d("cal", "0");
                        temp = Count();
                        //OpenChart();
                        Log.d("cal", "1");
                        edit.setText(Calculate()) ;
                        String calory = Calculate();
                        Log.d("cal", "2");
                        new async().execute(MillToDate(), calory);
                        Log.d("cal", "2");
                        createChart();
                        //for(int k=0;k<result.size();k++)
                        //Log.d("count",result.get(k)+"");
                        myTimer.removeMessages(0); //핸들러 메세지 제거
                        myPauseTime = SystemClock.elapsedRealtime();
                        myBtnStart.setText("시작");
                        myBtnRec.setText("리셋");
                        cur_Status = Pause;
                        break;
                    case Pause:
                        long now = SystemClock.elapsedRealtime();
                        myTimer.sendEmptyMessage(0);
                        myBaseTime += (now- myPauseTime);
                        myBtnStart.setText("멈춤");
                        myBtnRec.setText("기록");
                        cur_Status = Run;
                        break;
                }
                break;
            case R.id.btn_rec:
                switch(cur_Status){
                    case Run:
                        String str = myRec.getText().toString();
                        str +=  String.format("%d. %s\n",myCount,getTimeOut());
                        myRec.setText(str);
                        myCount++; //카운트 증가
                      //Calculate();
                        break;
                    case Pause:
                        //핸들러를 멈춤
                        //onPause();
                        myTimer.removeMessages(0);

                        myBtnStart.setText("시작");
                        myBtnRec.setText("기록");
                        myOutput.setText("00:00:00");

                        cur_Status = Init;
                        myCount = 1;
                        myRec.setText("");
                        myBtnRec.setEnabled(false);
                        break;
                }
                break;

        }
    }

    Handler myTimer = new Handler(){
        public void handleMessage(Message msg){
            myOutput.setText(getTimeOut());

            //sendEmptyMessage 는 비어있는 메세지를 Handler 에게 전송하는겁니다.
            myTimer.sendEmptyMessage(0);
        }
    };
    //현재시간을 계속 구해서 출력하는 메소드
    String getTimeOut(){
        long now = SystemClock.elapsedRealtime(); //애플리케이션이 실행되고나서 실제로 경과된 시간(??)^^;
        long outTime = now - myBaseTime;
        String easy_outTime = String.format("%02d:%02d:%02d", outTime/1000 / 60, (outTime/1000)%60,(outTime%1000)/10);
        return easy_outTime;

    }

    //YYYY-MM-dd HH:mm:ss
    public String MillToDate(){
        long mills = System.currentTimeMillis();
        String pattern = "yyyyMMdd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String date = (String) formatter.format(new Timestamp(mills));
        return date;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION){

            long currentTime = System.currentTimeMillis();
            Log.d("Main","7");
            long gabOfTime = (currentTime - lastTime);
            Log.d("Main","8");

            x = event.values[SensorManager.DATA_X];
            y = event.values[SensorManager.DATA_Y];
            z = event.values[SensorManager.DATA_Z];

            V_x.add(x);
            V_y.add(y);
            V_z.add(z);

            if (gabOfTime > 2000) {
                Log.d("Main","9");
                lastTime = currentTime;
                Log.d("Main","9.5");

                Acc_max_X = Collections.max(V_x);
                Acc_min_X = Collections.min(V_x);
                Acc_max_Y = Collections.max(V_y);
                Acc_min_Y = Collections.min(V_y);
                Acc_max_Z = Collections.max(V_z);
                Acc_min_Z = Collections.min(V_z);

                if (V_x == null || V_x.isEmpty()){
                    Acc_mean_X = 0.0;
                }
                long sum = 0;
                int n = V_x.size();
                for (int i = 0; i < n; i++) {
                    sum += V_x.get(i);
                    Acc_mean_X = ((double) sum) / n;
                }

                if (V_y == null || V_y.isEmpty()){
                    Acc_mean_Y = 0.0;
                }
                long sum2 = 0;
                int n2 = V_x.size();
                for (int i = 0; i < n2; i++) {
                    sum2 += V_y.get(i);
                    Acc_mean_Y = ((double) sum2) / n2;
                }

                if (V_z == null || V_z.isEmpty()){
                    Acc_mean_Z = 0.0;
                }
                long sum3 = 0;
                int n3 = V_x.size();
                for (int i = 0; i < n3; i++) {
                    sum3 += V_z.get(i);
                    Acc_mean_Z = ((double) sum3) / n3;
                }

                Log.d("Main","10");
                if(Acc_min_Y < -4.27){
                    Log.d("Main","10.1");
                    if(Acc_max_Y < 7.89){
                        if(Acc_min_Z < -5.62){
                            if(Acc_max_X < 3.38){
                                if(Acc_mean_X < -0.34){result.add("D");}
                                if(Acc_mean_X >= 0.34){result.add("S"); }
                            }
                            if(Acc_max_X >= 3.38){
                                if(Acc_min_Y < -5.75){
                                    if(Acc_mean_X < 0.79){
                                        if(Acc_min_Z < -7.54){
                                            if(Acc_mean_X < 0.25){
                                                if(Acc_mean_Y < -0.89){result.add("F");}
                                                if(Acc_mean_Y >= -0.89){result.add("F");}
                                            }
                                            if(Acc_mean_X >= 0.25){result.add("N");}
                                        }
                                        if(Acc_min_Z >= -7.54){result.add("F"); }
                                    }
                                    if(Acc_mean_X >= 0.79){
                                        if(Acc_max_X < 6.48){result.add("N"); }
                                        if(Acc_max_X >= 6.48){
                                            if(Acc_min_Y < -6.27){
                                                if(Acc_max_Z < 5.49){result.add("F"); }
                                                if(Acc_max_Z >= 5.49){
                                                    if(Acc_mean_Y < -1.11){result.add("R"); }
                                                    if(Acc_mean_Y >= -1.11){result.add("N"); }
                                                }
                                            }
                                            if(Acc_min_Y >= -6.27){
                                                if(Acc_min_Z < -7.55){result.add("N"); }
                                                if(Acc_min_Z >= -7.55){
                                                    if(Acc_min_X < -3.84){result.add("N"); }
                                                    if(Acc_min_X >= -3.84){result.add("D"); }
                                                }
                                            }
                                        }
                                    }
                                }
                                if(Acc_min_Y >= -5.75){
                                    if(Acc_min_Z < 5.96){result.add("N"); }
                                    if(Acc_min_Z >= -5.96){
                                        if(Acc_mean_X < 1.2){result.add("F"); }
                                        if(Acc_mean_X >= 1.2){result.add("N"); }
                                    }
                                }
                            }
                        }
                        Log.d("Main","11");
                        if(Acc_min_Z >= -5.62){
                            Log.d("Main","10.2");
                            if(Acc_max_X < 4.91){
                                if(Acc_mean_X < 0.31){
                                    if(Acc_min_Y < -5.25){
                                        if(Acc_max_Y < 2.41){result.add("S"); }
                                        if(Acc_max_Y >= 2.41){result.add("D"); }
                                    }
                                    if(Acc_min_Y >= -5.25){result.add("S"); }
                                }
                                if(Acc_mean_X >= 0.31){
                                    if(Acc_mean_Z < 0){
                                        if(Acc_max_Y < 3.75){result.add("S"); }
                                        if(Acc_max_Y >= 3.75){
                                            if(Acc_mean_X < 0.4){result.add("R"); }
                                            if(Acc_mean_X >= 0.4){result.add("F");
                                            }
                                            if(Acc_mean_Z < 0){
                                                if(Acc_max_Y < 3.75){result.add("S"); }
                                                if(Acc_max_Y >= 3.75){
                                                    if(Acc_mean_X < 0.4){result.add("R"); }
                                                    if(Acc_mean_X >= 0.4){result.add("F"); }
                                                }
                                            }
                                            if(Acc_mean_Z >= 0){
                                                if(Acc_max_X < 2.79){result.add("S"); }
                                                if(Acc_max_X >= 2.79){result.add("F"); }
                                            }
                                        }
                                    }
                                    if(Acc_max_X >= 4.91){
                                        if(Acc_min_Y < -5.49){
                                            if(Acc_max_Z < 4.61){result.add("R"); }
                                            if(Acc_max_Z >= 4.61){
                                                if(Acc_max_Y < 5.08){result.add("R"); }
                                                if(Acc_max_Y >= 5.08){result.add("F"); }
                                            }
                                        }
                                        if(Acc_min_Y >= -5.49){
                                            if(Acc_min_Z < -4.35){
                                                if(Acc_mean_X < 0.55){result.add("D");}
                                                if(Acc_mean_X >= 0.55){result.add("N");}
                                            }
                                            if(Acc_min_Z >= -4.35){
                                                if(Acc_min_X < -3.46){
                                                    if(Acc_min_Y < -4.62){result.add("N"); }
                                                    if(Acc_min_Y >= -4.62){result.add("R"); }
                                                }
                                                if(Acc_min_X >= -3.46){
                                                    if(Acc_mean_X < 1.15){result.add("F"); }
                                                    if(Acc_mean_X >= 1.15){result.add("R"); }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if(Acc_max_Y >= 7.89){
                                Log.d("Main","10.3");
                                if(Acc_min_Y < -8.41){
                                    if(Acc_max_Y < 30.67){
                                        if(Acc_max_X < 4.35){
                                            if(Acc_max_Y < 11.47){
                                                if(Acc_max_X < 4.26){
                                                    if(Acc_min_X < -6.14){
                                                        if(Acc_mean_Y < -0.69){result.add("R"); }
                                                        if(Acc_mean_Y >= -0.69){
                                                            if(Acc_mean_X < -2.6){result.add("F"); }
                                                            if(Acc_mean_X >= -2.6){result.add("F"); }
                                                        }
                                                    }
                                                    if(Acc_min_X >= -6.14){result.add("R"); }
                                                }
                                                if(Acc_max_X >= 4.26){result.add("F"); }
                                            }
                                            if(Acc_max_Y >= 11.47){result.add("R"); }
                                        }
                                        if(Acc_max_X >= 4.35){
                                            if(Acc_mean_X < 0.72){
                                                Log.d("Main","12.1");
                                                if(Acc_max_Y < 11.2){
                                                    if(Acc_mean_Z < -0.07){result.add("R"); }
                                                    Log.d("Main","12.2");
                                                    if(Acc_mean_Z >= -0.07){
                                                        Log.d("Main","12.3");
                                                        if(Acc_min_Z < -5.63){
                                                            Log.d("Main","12.4");
                                                            if(Acc_mean_X < 0.02){result.add("F"); }
                                                            Log.d("Main","12.5");
                                                            if(Acc_mean_X >= 0.02){result.add("F"); }
                                                        }
                                                        if(Acc_min_Z >= -5.63){
                                                            if(Acc_mean_X < -1.03){result.add("R"); }
                                                            if(Acc_mean_X >= -1.03){result.add("R"); }
                                                        }
                                                    }
                                                }
                                                if(Acc_max_Y >= 11.2){
                                                    if(Acc_mean_X < -1.73){
                                                        if(Acc_mean_Y < -0.58){result.add("F"); }
                                                        if(Acc_mean_Y >= -0.58)result.add("R"); }
                                                }
                                                if(Acc_mean_X >= -1.73)result.add("R"); }
                                        }
                                    }
                                    if(Acc_mean_X >= 0.72){result.add("R"); }
                                }
                            }
                            if(Acc_max_Y >= 30.67){result.add("S"); }
                        }
                        Log.d("Main","12");
                        if(Acc_min_Y >= -8.41){
                            if(Acc_min_Z < -8.82){
                                if(Acc_mean_Y < 0.03){
                                    if(Acc_mean_X < 1.17){result.add("D"); }
                                    if(Acc_mean_X >= 1.17){result.add("F"); }
                                }
                                if(Acc_mean_Y >= 0.03){
                                    if(Acc_max_Z < 14.48){result.add("N"); }
                                    if(Acc_max_Z >= 14.48){result.add("R"); }
                                }
                            }
                            if(Acc_min_Z >+ -8.82){
                                if(Acc_min_X < -6.2){
                                    if(Acc_mean_Y < -0.03){
                                        if(Acc_mean_X < 0.92){result.add("D"); }
                                        if(Acc_mean_X >= 0.92){result.add("N"); }
                                    }
                                    if(Acc_mean_Y >= -0.03){
                                        if(Acc_mean_Z < -0.27){result.add("F"); }
                                        if(Acc_mean_Z >= -0.27)
                                            if(Acc_mean_Y < 2.73){
                                                if(Acc_max_Y < 9.15){result.add("R"); }
                                                if(Acc_max_Y >= 9.15){result.add("F"); }
                                            }
                                        if(Acc_mean_Y >= 2.73){result.add("R");}
                                    }
                                }
                                if(Acc_min_X >= -6.2){
                                    if(Acc_min_Z < -7.46){
                                        if(Acc_mean_Y < 0.88){
                                            if(Acc_mean_X < 0.89){result.add("F"); }
                                            if(Acc_mean_X >= 0.89){
                                                if(Acc_mean_Y < -0.11){result.add("F"); }
                                                if(Acc_mean_Y >= -0.11){result.add("N"); }
                                            }
                                        }
                                        if(Acc_mean_Y >= 0.88){result.add("N"); }
                                    }
                                    if(Acc_min_Z >= -7.46){result.add("F"); }
                                }
                            }
                        }
                    }
                }
                if(Acc_min_Y >= -4.27){
                    Log.d("Main","15.");
                    if(Acc_mean_X < 0.15){
                        if(Acc_max_Y < 2.43){
                            if(Acc_max_Z < 0.86){
                                if(Acc_mean_Y < 0.24){result.add("U"); }
                                if(Acc_mean_Y >= 0.24){result.add("S"); }
                            }
                            if(Acc_max_Z >= 0.86){
                                if(Acc_mean_Y < -0.09){
                                    if(Acc_min_Y < -1.83){
                                        if(Acc_mean_X < -0.44){
                                            if(Acc_mean_X < -0.56){result.add("S"); }
                                            if(Acc_mean_X >= -0.56){result.add("S"); }
                                        }
                                        if(Acc_mean_X >= -0.44){result.add("S"); }
                                    }
                                    if(Acc_min_Y >= -1.83){result.add("D"); }
                                }
                                if(Acc_mean_Y >= -0.09){result.add("S"); }
                            }
                        }
                        if(Acc_max_Y >= 2.43){
                            if(Acc_mean_X < -0.35){
                                if(Acc_mean_Z < -0.1){
                                    if(Acc_max_Z < 0.85){result.add("S"); }
                                    if(Acc_max_Z >= 0.85){
                                        if(Acc_min_Z < -4.12){
                                            if(Acc_min_Z < -4.7){result.add("D"); }
                                            if(Acc_min_Z >= -4.7){
                                                if(Acc_max_Y < 5.07){result.add("D"); }
                                                if(Acc_max_Y >= 5.07){result.add("S"); }
                                            }
                                        }
                                        if(Acc_min_Z >= -4.12){result.add("D"); }
                                    }
                                }
                                if(Acc_mean_Z >= -0.1){
                                    if(Acc_max_X < 4.1){
                                        if(Acc_mean_X < -0.55){
                                            if(Acc_mean_Z < -0.38){result.add("D"); }
                                            if(Acc_mean_Z >= 0.38){result.add("S"); }
                                        }
                                        if(Acc_mean_X < -0.55){result.add("S"); }
                                    }
                                    if(Acc_max_X >= 4.1){result.add("D"); }
                                }
                            }
                            if(Acc_mean_X >= 0.35){
                                if(Acc_mean_Z < -1.27){
                                    if(Acc_mean_Y < 0.57){result.add("U"); }
                                    if(Acc_mean_Y >= 0.57){result.add("N"); }
                                }
                                if(Acc_mean_Z >= -1.27){
                                    if(Acc_mean_Y < 0.03){
                                        if(Acc_min_X < -1.97){
                                            if(Acc_max_X < 3.47){result.add("S"); }
                                            if(Acc_max_X >= 3.47){
                                                if(Acc_mean_Y < -0.34){result.add("U"); }
                                                if(Acc_mean_Y >= -0.34){result.add("D"); }
                                            }
                                        }
                                        if(Acc_min_X >= -1.97){
                                            if(Acc_mean_X < 0.08){result.add("U"); }
                                            if(Acc_mean_X >= 0.08){
                                                if(Acc_mean_Z < -0.53){result.add("U");}
                                                if(Acc_mean_Z >= -0.53){result.add("D"); }
                                            }
                                        }
                                    }
                                    if(Acc_mean_Y > 0.03){
                                        if(Acc_mean_Z < -0.39){
                                            if(Acc_min_Y < -1.94){result.add("D"); }
                                            if(Acc_min_Y >= -1.94){result.add("U"); }
                                        }
                                        if(Acc_mean_Z >= -0.39){
                                            if(Acc_mean_Z < -0.37){result.add("S"); }
                                            if(Acc_mean_Z >= -0.37){
                                                if(Acc_min_Y < -2.87){
                                                    if(Acc_mean_X < -0.06){result.add("S"); }
                                                    if(Acc_mean_X >= -0.06){result.add("D"); }
                                                }
                                                if(Acc_min_Y >= -2.87){result.add("D"); }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(Acc_mean_X >= 0.15){
                        Log.d("Main","15.2");
                        if(Acc_max_Y < 2.47){
                            if(Acc_max_Y < 1.99){
                                if(Acc_max_X < 1.56){result.add("U"); }
                                if(Acc_max_X >= 1.56){
                                    if(Acc_mean_X < 0.17){
                                        if(Acc_mean_X < 0.17){result.add("S"); }
                                        if(Acc_mean_X >= 0.17){result.add("U"); }
                                    }
                                    if(Acc_mean_X >= 0.17){result.add("S"); }
                                }
                            }
                            if(Acc_max_Y >= 1.99){
                                if(Acc_min_X < -1.11){
                                    if(Acc_min_X < -2.01){result.add("S"); }
                                    if(Acc_min_X >= -2.01){
                                        if(Acc_mean_Z < -0.42){result.add("U");}
                                        if(Acc_mean_Z >= -0.42){
                                            if(Acc_max_X < 3.82){result.add("S"); }
                                            if(Acc_max_X >= 3.82){
                                                if(Acc_mean_X < 0.37){result.add("S"); }
                                                if(Acc_mean_X >= 0.37){result.add("U"); }
                                            }
                                        }
                                    }
                                }
                                if(Acc_min_X >= -1.11){
                                    if(Acc_min_X < -0.89){result.add("U"); }
                                    if(Acc_min_X >= -0.89){
                                        if(Acc_mean_X < 0.42){result.add("U"); }
                                        if(Acc_mean_X >= 0.42){result.add("U"); }
                                    }
                                }
                            }
                        }
                        if(Acc_max_Y >= 2.47){
                            if(Acc_min_X < -2.54){
                                if(Acc_min_Y < -0.92){
                                    if(Acc_mean_X < 0.77){
                                        if(Acc_max_Y > 2.51){result.add("N"); }
                                        if(Acc_max_Y >= 2.51){
                                            if(Acc_min_X < -4.21){
                                                if(Acc_mean_Y < 1.17){result.add("D"); }
                                                if(Acc_mean_Y >= 1.17){result.add("N"); }
                                            }
                                            if(Acc_min_X >= -4.21){result.add("S"); }
                                        }
                                    }
                                    if(Acc_mean_X >= 0.77){
                                        if(Acc_mean_Y < 0.37){result.add("N"); }
                                        if(Acc_mean_Y >= 0.37){
                                            if(Acc_mean_X < 1.04){result.add("F"); }
                                            if(Acc_mean_X >= 1.04){result.add("N");}
                                        }
                                    }
                                }
                                if(Acc_min_Y >= -0.92){
                                    if(Acc_mean_X < 0.64){result.add("R"); }
                                    if(Acc_mean_X >= 0.64){result.add("U"); }
                                }
                            }
                            if(Acc_min_X >= -2.54){
                                if(Acc_max_Y < 4.06){
                                    if(Acc_mean_Z < -0.19){result.add("U"); }
                                    if(Acc_mean_Z >= -0.19){
                                        if(Acc_min_Z < -2.23){
                                            if(Acc_min_X < -1.24){result.add("S"); }
                                            if(Acc_min_X >= -1.24){result.add("F"); }
                                        }
                                        if(Acc_min_Z >= -2.23){result.add("F"); }
                                    }
                                }
                                if(Acc_max_Y >= 4.06){
                                    if(Acc_min_Z < -4.88){
                                        if(Acc_max_X < 4.06){result.add("D"); }
                                        if(Acc_max_X >= 4.06){result.add("N"); }
                                    }
                                    if(Acc_min_Z >= -4.88){
                                        if(Acc_mean_Z < 0.46){result.add("S"); }
                                        if(Acc_mean_Z >= 0.46){result.add("R"); }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Log.d("Main","13");

    }

    public int[] Count(){
        int i;
        int temp[]  = new int[6];

        int countN=0;
        int countS=0;
        int countD=0;
        int countU=0;
        int countR=0;
        int countF=0;
        Log.d("surim",result.size()+"");
        for(i=0; i<result.size(); i++){
            Log.d("Yorim: ",result.get(i)+"");

        }
        for(i=0; i<result.size(); i++){
            if(result.get(i) == "S"){
                countS++;
            }
        }
        temp[0] = countS;

        for(i=0; i<result.size(); i++){
            if(result.get(i) == "N"){
                countN++;
            }
        }
        temp[1] = countN;

        for(i=0; i<result.size(); i++){
            if(result.get(i) == "F" ){
                countF++;
            }
        }
        temp[2] = countF;

        for(i=0; i<result.size(); i++){
            if(result.get(i) == "R"){
                countR++;
            }
        }
        temp[3] = countR;

        for(i=0; i<result.size(); i++){
            if(result.get(i) == "U"){
                countU++;
            }
        }
        temp[4] = countU;

        for(i=0; i<result.size(); i++){
            if(result.get(i) == "D"){
                countD++;
            }
        }
        temp[5] = countD;

        Log.d("Count", "count");

        return temp;
    }

    private String Calculate(){
        int cal1;
        int cal2;
        int cal3;
        int cal4;
        int cal5;
        int cal6;

        double sum1=0;
        double sum2=0;
        double sum3=0;
        double sum4=0;
        double sum5=0;
        double sum6=0;

        double result = 0.0;

        temp = Count();
        if(temp[0] >= 30){
            cal1 = temp[0]/2;
            sum1 = cal1*1.5;
        }
        if(temp[1] >= 30){
            cal2 = temp[1]/2;
            sum2 = cal2*3.3;
        }
        if(temp[2] >= 30){
            cal3 = temp[2]/2;
            sum3 = cal3*4;
        }
        if(temp[3] >= 30){
            cal4 = temp[3]/2;
            sum4 = cal4*8;
        }
        if(temp[4] >= 30){
            cal5 = temp[4]/2;
            sum5 = cal5*7.3;
        }
        if(temp[5] >= 30){
            cal6 = temp[5]/2;
            sum6 = cal6*4.4;
        }
        result = (sum1 + sum2 + sum3 + sum4 + sum5+ sum6)*0.1 ;
        String s = Double.toString(result);
        Log.d("cal","cal "+ s);
        return s+"kcal";
    }
    private void createChart() {

        int[] x = { 0, 1, 2, 3, 4, 5 };
        String[] _friends = new String[] { "Slow", "Nolmal", "Fast", "Running", "Up", "Down"};
        temp = Count();
        // Creating an XYSeries for Height
        XYSeries expenseSeries = new XYSeries("Height");
        // Adding data to Height Series
        for (int i = 0; i < temp.length; i++) {
            expenseSeries.add(i, temp[i]);
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
        renderer.setXLabels(10);
        renderer.setChartTitle("classification chart ");
        renderer.setXTitle("State");
        renderer.setYTitle("Quantity");

        /***
         * Customizing graphs
         */
        // setting text size of the title
        renderer.setChartTitleTextSize(30);
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

        for (int i = 0; i < x.length; i++) {
            renderer.addXTextLabel(i, _friends[i]);
            renderer.setXLabelsPadding(3);
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
        chart = ChartFactory.getBarChartView(Main3Activity.this, dataset, renderer, BarChart.Type.DEFAULT);
        // adding the view to the linearlayout
        chartContainer.removeAllViews();
        chartContainer.addView(chart);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume(){
        super.onResume();
        mSensorM.registerListener(this, accelsensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorM.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}

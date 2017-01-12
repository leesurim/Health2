package com.example.user.health;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class FirstTab extends Activity {
    private ListView _listview;
    private String[] items = {"날씨", "START", "RECORD"};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_tab);

        //Intent intent = getIntent();
        //String name;
        //name = intent.getExtras().getString("name");

        //TextView text = (TextView) findViewById(R.id.textView1);
        //text.setText("name" + "님 반갑습니다");

        ImageView image =(ImageView)this.findViewById(R.id.imageView);
        image.setImageResource(R.drawable.ghghhealth);

        _listview = (ListView)findViewById(R.id.mainmenulist);

        _listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));
        _listview.setOnItemClickListener(onItemClickListener);
    }


//아래가 리스너로 리스트 항목 클릭시 나타나는 액티비트들을 호출한다.

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id)
        {

            if(position == 0){
                Intent intent = new Intent(FirstTab.this, WT.class);
                startActivity(intent);
            }
            if(position == 1){
                Intent intent = new Intent(FirstTab.this, Main3Activity.class);
                startActivity(intent);
            }
            if(position == 2){
                Intent intent = new Intent(FirstTab.this, Main4Activity.class);
                startActivity(intent);
            }
        }
    };

}
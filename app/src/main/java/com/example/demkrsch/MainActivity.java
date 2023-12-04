package com.example.demkrsch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
public class MainActivity extends AppCompatActivity {
    private Button view_tab, conf, kill;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DBHelper helper = new DBHelper(MainActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addListenerOnButton();


    }
    public void addListenerOnButton (){
        Button view_tab = findViewById(R.id.viewtables);
        Button conf = findViewById(R.id.conf);
        Button kill = findViewById(R.id.kill);
        view_tab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this,
                                MainActivity2.class);
                        startActivity(intent);
                    }
                }
        );
        conf.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this,
                                Configuration.class);
                        startActivity(intent);


                    }
                }
        );
        kill.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finishAffinity();
                    }
                }
        );
    }
}
package com.example.demkrsch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class MainActivity2 extends AppCompatActivity {
    private Button back,moth,cpu,gpu,punit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        addListenerOnButton();
    }

    public void addListenerOnButton() {
        Button back = findViewById(R.id.backtab);
        Button moth = findViewById(R.id.moth);
        Button cpu = findViewById(R.id.cpu);
        Button gpu = findViewById(R.id.gpu);
        Button punit = findViewById(R.id.punit);
        back.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity2.this,
                                MainActivity.class);
                        startActivity(intent);
                    }
                }
        );
        moth.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity2.this,
                                Motherboard.class);
                        startActivity(intent);
                    }
                }
        );
        cpu.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity2.this,
                                Cpu.class);
                        startActivity(intent);
                    }
                }
        );
        gpu.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity2.this,
                                Gpu.class);
                        startActivity(intent);
                    }
                }
        );
        punit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity2.this,
                                Punit.class);
                        startActivity(intent);
                    }
                }
        );
    }
}
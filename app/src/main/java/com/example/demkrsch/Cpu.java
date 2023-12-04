package com.example.demkrsch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class Cpu extends AppCompatActivity {
    private Button backcpu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu);
        addListenerOnButton();
    }

    public void addListenerOnButton() {
        Button backcpu = findViewById(R.id.backcpu);
        backcpu.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Cpu.this,
                                MainActivity2.class);
                        startActivity(intent);
                    }
                }
        );
    }
}
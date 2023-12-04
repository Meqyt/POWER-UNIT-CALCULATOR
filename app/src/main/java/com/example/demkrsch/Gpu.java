package com.example.demkrsch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class Gpu extends AppCompatActivity {
    private Button backgpu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpu);
        addListenerOnButton();
    }

    public void addListenerOnButton() {
        Button backgpu = findViewById(R.id.backgpu);
        backgpu.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Gpu.this,
                                MainActivity2.class);
                        startActivity(intent);
                    }
                }
        );
    }
}
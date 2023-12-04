package com.example.demkrsch;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Configuration extends AppCompatActivity {

    private Spinner spinner1, spinner2, spinner3;
    private TextView textViewTdp, textViewPunit;
    private Button back;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        dbHelper = new DBHelper(this);

        spinner1 = findViewById(R.id.spin1);
        spinner2 = findViewById(R.id.spin2);
        spinner3 = findViewById(R.id.spin3);
        textViewTdp = findViewById(R.id.textViewTdp);
        textViewPunit = findViewById(R.id.textViewPunit);
        Button sumButton = findViewById(R.id.sum);
        addListenerOnButton();

        try {
            setupSpinner(spinner1, dbHelper.getCpuNames(), new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    try {
                        SpinnerItem selectedCpuItem = (SpinnerItem) spinner1.getSelectedItem();
                        int cpuId = selectedCpuItem.getId();
                        ArrayList<SpinnerItem> mb = dbHelper.getMotherboardNamesByCpuId(cpuId);
                        try {
                            setupSpinner(spinner2, mb, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // Используем значение из параметра метода onItemSelected
                        String selectedGpu = parentView.getItemAtPosition(position).toString();
                        updateTdpTextView(selectedCpuItem.getName(), selectedGpu);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Ничего не делаем при отсутствии выбора
                }
            });

            // Вызываем метод populateSpinner
            populateSpinner();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод populateSpinner теперь является частью класса Configuration, а не внутри onCreate
    private void populateSpinner() {
        ArrayList<SpinnerItem> gpuItems = dbHelper.getGpuNames();
        setupSpinner(spinner3, gpuItems, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // При выборе элемента Spinner3 обновляем TDP
                SpinnerItem selectedCpuItem = (SpinnerItem) spinner1.getSelectedItem();
                updateTdpTextView(selectedCpuItem.getName(), parentView.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Ничего не делаем при отсутствии выбора
            }
        });
    }

    private void setupSpinner(Spinner spinner, ArrayList<SpinnerItem> data, AdapterView.OnItemSelectedListener listener) {
        if (spinner != null) {
            ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            if (listener != null) {
                spinner.setOnItemSelectedListener(listener);
            }
        }
    }

    public void addListenerOnButton() {
        Button back = findViewById(R.id.backconf);
        back.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Configuration.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }

    private void updateTdpTextView(String selectedCpu, String selectedGpu) {
        // Ваш код обновления TDP
        try {
            int cpuTdp = dbHelper.getCpuTdp(selectedCpu);
            int gpuTdp = dbHelper.getGpuTdp(selectedGpu);
            int totalTdp = cpuTdp + gpuTdp + 30;
            textViewTdp.setText("Total TDP: " + totalTdp + " Watts");

            // Ваш остальной код...

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

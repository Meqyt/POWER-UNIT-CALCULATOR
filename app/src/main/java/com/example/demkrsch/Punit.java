package com.example.demkrsch;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Punit extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextView mView;
    private Button delpunit, backpunit, addpunit, changepunit;
    private Spinner spinnerPunit;
    private SQLiteDatabase db;
    private ArrayAdapter<String> adapter;
    private int selectedPowerUnitId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punit);

        spinnerPunit = findViewById(R.id.spinpunit);
        mView = findViewById(R.id.textView5);
        delpunit = findViewById(R.id.delpunit);
        backpunit = findViewById(R.id.backpunit);
        addpunit = findViewById(R.id.addpunit);
        changepunit = findViewById(R.id.chengepunit);

        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT name FROM power_unit", null);
        List<String> names = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex("name"));
                names.add(id);
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPunit.setAdapter(adapter);

        spinnerPunit.setOnItemSelectedListener(this);
        addListenerOnButton();
    }

    private void deleteDataFromDatabase(String name) {
        db.delete("power_unit", "name=?", new String[]{name});
        refreshSpinnerData();
    }

    private void refreshSpinnerData() {
        Cursor cursor = db.rawQuery("SELECT name FROM power_unit", null);
        List<String> names = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                names.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPunit.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedPowerUnit = parent.getItemAtPosition(position).toString();

        Cursor cursorPunit = db.rawQuery("SELECT * FROM power_unit WHERE name = ?", new String[]{selectedPowerUnit});
        if (cursorPunit.moveToFirst()) {
            String name = cursorPunit.getString(cursorPunit.getColumnIndex("name"));
            String manufacturer = cursorPunit.getString(cursorPunit.getColumnIndex("manufacturer"));
            String APFC = cursorPunit.getString(cursorPunit.getColumnIndex("APFC"));
            String certificate = cursorPunit.getString(cursorPunit.getColumnIndex("certificate"));
            int output_power = cursorPunit.getInt(cursorPunit.getColumnIndex("output_power"));

            // Выводим информацию в textView4
            mView.setText("Название: " + name + "\nПроизводитель: " + manufacturer + "\nAPFC: " + APFC
                    + "\nСертификат 80PLUS: " + certificate + "\nМощность: " + output_power + " Watt");
        }
        cursorPunit.close();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        mView.setText("nothing");
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить запись");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_punit, null);
        builder.setView(view);

        final EditText nameEditText = view.findViewById(R.id.editTextName);
        final EditText manufacturerEditText = view.findViewById(R.id.editTextManufacturer);
        final EditText apfcEditText = view.findViewById(R.id.editTextAPFC);
        final EditText certificateEditText = view.findViewById(R.id.editTextCertificate);
        final EditText powerEditText = view.findViewById(R.id.editTextPower);

        builder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBHelper helper = new DBHelper(Punit.this);
                String name = nameEditText.getText().toString();
                String manufacturer = manufacturerEditText.getText().toString();
                String APFC = apfcEditText.getText().toString();
                String certificate = certificateEditText.getText().toString();
                String power = powerEditText.getText().toString();
                int pow = Integer.parseInt(power);




                helper.insertPUNIT(name, manufacturer, APFC, certificate,pow);
                refreshSpinnerData();
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        refreshSpinnerData();
    }

    public void showUpdateDialog(String selectedPowerUnit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Изменить запись");

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_punit, null);
        builder.setView(view);

        final EditText nameEditText = view.findViewById(R.id.editTextName);
        final EditText manufacturerEditText = view.findViewById(R.id.editTextManufacturer);
        final EditText apfcEditText = view.findViewById(R.id.editTextAPFC);
        final EditText certificateEditText = view.findViewById(R.id.editTextCertificate);
        final EditText powerEditText = view.findViewById(R.id.editTextPower);

        Cursor cursorPowerUnit = db.rawQuery("SELECT * FROM power_unit WHERE name = ?", new String[]{selectedPowerUnit});
        if (cursorPowerUnit.moveToFirst()) {
            nameEditText.setText(cursorPowerUnit.getString(cursorPowerUnit.getColumnIndex("name")));
            manufacturerEditText.setText(cursorPowerUnit.getString(cursorPowerUnit.getColumnIndex("manufacturer")));
            apfcEditText.setText(cursorPowerUnit.getString(cursorPowerUnit.getColumnIndex("APFC")));
            certificateEditText.setText(cursorPowerUnit.getString(cursorPowerUnit.getColumnIndex("certificate")));
            powerEditText.setText(cursorPowerUnit.getString(cursorPowerUnit.getColumnIndex("output_power")));
        }
        cursorPowerUnit.close();

        builder.setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = nameEditText.getText().toString();
                String manufacturer = manufacturerEditText.getText().toString();
                String APFC = apfcEditText.getText().toString();
                String certificate = certificateEditText.getText().toString();
                String power = powerEditText.getText().toString();
                int pow = Integer.parseInt(power);

                updatePowerUnit(name, manufacturer, APFC, certificate,pow);
                refreshSpinnerData();
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        refreshSpinnerData();
    }

    private void updatePowerUnit(String name, String manufacturer, String APFC, String certificate, int output_power) {
        ContentValues puValues = new ContentValues();
        puValues.put("name", name);
        puValues.put("manufacturer", manufacturer);
        puValues.put("APFC", APFC);
        puValues.put("certificate", certificate);
        puValues.put("output_power", output_power);

        db.beginTransaction();

        try {
            db.update("power_unit", puValues, "name=?", new String[]{name});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        refreshSpinnerData();
    }

    public void addListenerOnButton() {
        delpunit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedName = spinnerPunit.getSelectedItem().toString();
                deleteDataFromDatabase(selectedName);
            }
        });

        backpunit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Punit.this, MainActivity2.class);
                startActivity(intent);
            }
        });

        addpunit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });
        changepunit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedPowerUnit = spinnerPunit.getSelectedItem().toString();
                showUpdateDialog(selectedPowerUnit);
            }
        });
    }
}

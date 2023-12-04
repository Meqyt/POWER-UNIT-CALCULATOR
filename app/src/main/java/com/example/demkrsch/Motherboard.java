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

public class Motherboard extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextView mView;
    private Button delmoth, backmoth, addmoth,editmoth;
    private Spinner spinnerSocket;
    private SQLiteDatabase db;
    private ArrayAdapter<String> adapter;
    private int selectedSocketId;
    private int selectedMotherboardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motherboard);

        spinnerSocket = findViewById(R.id.spinmoth);
        mView = findViewById(R.id.textView4);
        delmoth = findViewById(R.id.delmoth);
        backmoth = findViewById(R.id.backmoth);
        addmoth = findViewById(R.id.addmoth);
        editmoth = findViewById(R.id.editmoth);

        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT name FROM motherboard", null);
        List<String> names = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex("name"));
                names.add(id);
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSocket.setAdapter(adapter);

        spinnerSocket.setOnItemSelectedListener(this);
        addListenerOnButton();
    }

    private void deleteDataFromDatabase(String name) {
        db.delete("motherboard", "name=?", new String[]{name});
        refreshSpinnerData();
    }

    private void refreshSpinnerData() {
        Cursor cursor = db.rawQuery("SELECT name FROM motherboard", null);
        List<String> names = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                names.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSocket.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedMotherboard = parent.getItemAtPosition(position).toString();

        Cursor cursorMotherboard = db.rawQuery("SELECT * FROM motherboard WHERE name = ?", new String[]{selectedMotherboard});
        if (cursorMotherboard.moveToFirst()) {
            String name = cursorMotherboard.getString(cursorMotherboard.getColumnIndex("name"));
            String manufacturer = cursorMotherboard.getString(cursorMotherboard.getColumnIndex("manufacturer"));
            String chipset = cursorMotherboard.getString(cursorMotherboard.getColumnIndex("chipset"));
            String formFactor = cursorMotherboard.getString(cursorMotherboard.getColumnIndex("form_factor"));
            int sockId = cursorMotherboard.getInt(cursorMotherboard.getColumnIndex("sockid"));

            Cursor cursorSocket = db.rawQuery("SELECT name FROM socket WHERE _id = ?", new String[]{String.valueOf(sockId)});
            String socketName = "";
            if (cursorSocket.moveToFirst()) {
                socketName = cursorSocket.getString(cursorSocket.getColumnIndex("name"));
            }
            cursorSocket.close();

            // Выводим информацию в textView4
            mView.setText("Название: " + name + "\nПроизводитель: " + manufacturer + "\nЧипсет: " + chipset
                    + "\nФорм-Фактор: " + formFactor + "\nСокет: " + socketName);
        }
        cursorMotherboard.close();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        mView.setText("nothing");
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить запись");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_motherboard, null);
        builder.setView(view);

        final EditText nameEditText = view.findViewById(R.id.editTextName);
        final EditText manufacturerEditText = view.findViewById(R.id.editTextManufacturer);
        final EditText chipsetEditText = view.findViewById(R.id.editTextChipset);
        final EditText formFactorEditText = view.findViewById(R.id.editTextFormFactor);

        // Создаем новый Spinner для выбора сокета в диалоговом окне
        Spinner spinnerDialog = view.findViewById(R.id.spine);
        Cursor cursorSocket = db.rawQuery("SELECT name FROM socket", null);
        List<String> socketNames = new ArrayList<>();
        if (cursorSocket.moveToFirst()) {
            do {
                String socketName = cursorSocket.getString(cursorSocket.getColumnIndex("name"));
                socketNames.add(socketName);
            } while (cursorSocket.moveToNext());
        }
        cursorSocket.close();

        ArrayAdapter<String> socketAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, socketNames);
        socketAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDialog.setAdapter(socketAdapter);

        builder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBHelper helper = new DBHelper(Motherboard.this);
                String name = nameEditText.getText().toString();
                String manufacturer = manufacturerEditText.getText().toString();
                String chipset = chipsetEditText.getText().toString();
                String formFactor = formFactorEditText.getText().toString();

                String selectedSocketDialog = spinnerDialog.getSelectedItem().toString();

                Cursor cursorSocketDialog = db.rawQuery("SELECT _id FROM socket WHERE name = ?", new String[]{selectedSocketDialog});
                if (cursorSocketDialog.moveToFirst()) {
                    selectedSocketId = cursorSocketDialog.getInt(cursorSocketDialog.getColumnIndex("_id"));
                }
                cursorSocketDialog.close();

               helper.insertMotherboard(name, manufacturer, chipset, formFactor, selectedSocketId);
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

    public void showUpdateDialog(String selectedMotherboard) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Изменить запись");

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_motherboard, null);
        builder.setView(view);

        final EditText nameEditText = view.findViewById(R.id.editTextName);
        final EditText manufacturerEditText = view.findViewById(R.id.editTextManufacturer);
        final EditText chipsetEditText = view.findViewById(R.id.editTextChipset);
        final EditText formFactorEditText = view.findViewById(R.id.editTextFormFactor);
        final Spinner spinnerDialog = view.findViewById(R.id.spine);

        Cursor cursorMotherboard = db.rawQuery("SELECT * FROM motherboard WHERE name = ?", new String[]{selectedMotherboard});
        if (cursorMotherboard.moveToFirst()) {
            nameEditText.setText(cursorMotherboard.getString(cursorMotherboard.getColumnIndex("name")));
            manufacturerEditText.setText(cursorMotherboard.getString(cursorMotherboard.getColumnIndex("manufacturer")));
            chipsetEditText.setText(cursorMotherboard.getString(cursorMotherboard.getColumnIndex("chipset")));
            formFactorEditText.setText(cursorMotherboard.getString(cursorMotherboard.getColumnIndex("form_factor")));

            // Получаем ID сокета текущей материнской платы
            int selectedSocketId = cursorMotherboard.getInt(cursorMotherboard.getColumnIndex("sockid"));

            // Получаем все сокеты из базы данных
            Cursor cursorSocket = db.rawQuery("SELECT * FROM socket", null);
            List<String> socketNames = new ArrayList<>();
            final List<Integer> socketIds = new ArrayList<>();

            if (cursorSocket.moveToFirst()) {
                do {
                    String socketName = cursorSocket.getString(cursorSocket.getColumnIndex("name"));
                    int socketId = cursorSocket.getInt(cursorSocket.getColumnIndex("_id"));
                    socketNames.add(socketName);
                    socketIds.add(socketId);
                } while (cursorSocket.moveToNext());
            }
            cursorSocket.close();

            ArrayAdapter<String> socketAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, socketNames);
            socketAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDialog.setAdapter(socketAdapter);

            // Устанавливаем выбранный сокет в Spinner
            int selectedSocketIndex = socketIds.indexOf(selectedSocketId);
            spinnerDialog.setSelection(selectedSocketIndex);
        }
        cursorMotherboard.close();

        builder.setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = nameEditText.getText().toString();
                String manufacturer = manufacturerEditText.getText().toString();
                String chipset = chipsetEditText.getText().toString();
                String formFactor = formFactorEditText.getText().toString();

                // Получаем выбранный сокет из нового Spinner в диалоговом окне
                String selectedSocketDialog = spinnerDialog.getSelectedItem().toString();

                // Получаем ID выбранного сокета
                Cursor cursorSocketDialog = db.rawQuery("SELECT _id FROM socket WHERE name = ?", new String[]{selectedSocketDialog});
                int selectedSocketId = -1;
                if (cursorSocketDialog.moveToFirst()) {
                    selectedSocketId = cursorSocketDialog.getInt(cursorSocketDialog.getColumnIndex("_id"));
                }
                cursorSocketDialog.close();

                // Обновляем данные в базе данных
                updateMotherboard(name, manufacturer, chipset, formFactor, selectedSocketId);
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

    private void updateMotherboard(String name, String manufacturer, String chipset, String formFactor, int socketId) {
        ContentValues valuesup = new ContentValues();
        valuesup.put("name", name);
        valuesup.put("manufacturer", manufacturer);
        valuesup.put("chipset", chipset);
        valuesup.put("form_factor", formFactor);
        valuesup.put("sockid", socketId);

        db.beginTransaction();

        try {
            db.update("motherboard", valuesup, "name=?", new String[]{name});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        refreshSpinnerData();
    }

    public void addListenerOnButton() {
        delmoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedName = spinnerSocket.getSelectedItem().toString();
                deleteDataFromDatabase(selectedName);
            }
        });

        backmoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Motherboard.this, MainActivity2.class);
                startActivity(intent);
            }
        });

        addmoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });
        editmoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedMotherboard = spinnerSocket.getSelectedItem().toString();
                showUpdateDialog(selectedMotherboard);
            }
        });
    }
}

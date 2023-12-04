package com.example.demkrsch;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    static SQLiteDatabase db;
    Context context;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CPP";
    private static final String SOCKET = "socket";
    private static final String MOTHERBOARD = "motherboard";
    private static final String CPU = "cpu";
    private static final String GPU = "gpu";
    private static final String POWER_UNIT = "power_unit";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + SOCKET + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL);");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + MOTHERBOARD + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, chipset TEXT NOT NULL, manufacturer TEXT, form_factor TEXT, sockid INTEGER references socket(_id));");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CPU + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, manufacturer TEXT NOT NULL, cores INTEGER NOT NULL, threads INTEGER NOT NULL, frequency INT NOT NULL, tdp INTEGER NOT NULL,sockid references socket (_id));");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GPU + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, manufacturer TEXT NOT NULL, vendor TEXT, frequency INTEGER NOT NULL, vram INTEGER NOT NULL, tdp INTEGER NOT NULL);");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + POWER_UNIT + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, manufacturer TEXT NOT NULL, APFC TEXT NOT NULL, certificate TEXT ,output_power INTEGER NOT NULL);");
        db.execSQL("insert into socket(name)values('lga1151'),('lga1151v2'),('lga1200'),('lga1700'),('AM3+'),('AM4'),('AM5')");
        db.execSQL("insert into motherboard(name,chipset,manufacturer,form_factor,sockid)values('Asus Prime H410m-k','H410','ASUS','mATX',3)");
        db.execSQL("insert into gpu(name,manufacturer,vendor,frequency,vram,tdp) values('MSI VENTUS GTX 1660 super','NVIDIA','MSI',1678,6,120),('Palit GeForce RTX 4090 GameRock OC','NVIDIA','PALIT',2235,24,450)");
        db.execSQL("insert into power_unit(name,manufacturer,APFC,certificate,output_power) values('DEEPCOOL PF600','DEEPCOOL','+','+',600),('Cougar STX 700W','Cougar','+','+',700)");
        db.execSQL("insert into cpu(name,manufacturer,cores,threads,frequency,tdp,sockid) values('DEEPCOOL PF600','DEEPCOOL','+','+',600),('Cougar STX 700W','Cougar','+','+',700)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ POWER_UNIT);
        db.execSQL("DROP TABLE IF EXISTS "+ GPU);
        db.execSQL("DROP TABLE IF EXISTS "+ CPU);
        db.execSQL("DROP TABLE IF EXISTS "+ MOTHERBOARD);
        db.execSQL("DROP TABLE IF EXISTS "+ SOCKET);
        this.onCreate(db);


    }

    public void insertSocket(String name) {
        db = this.getWritableDatabase();
        ContentValues socketValues = new ContentValues();
        socketValues.put("name", name);
        db.insert(SOCKET, null, socketValues);

    }

    public void insertMotherboard(String name, String manufacturer, String chipset, String form_factor, int sockid) {
        db = this.getWritableDatabase();
        ContentValues mothValues = new ContentValues();
        mothValues.put("name", name);
        mothValues.put("chipset", chipset);
        mothValues.put("manufacturer", manufacturer);
        mothValues.put("form_factor", form_factor);
        mothValues.put("sockid", sockid);
        db.insert(MOTHERBOARD, null, mothValues);

    }

    public void insertCPU(String name, String manufacturer, int cores, int threads, int frequency, int tdp, int sockid) {
        db = this.getWritableDatabase();
        ContentValues cpuValues = new ContentValues();
        cpuValues.put("name", name);
        cpuValues.put("manufacturer", manufacturer);
        cpuValues.put("cores", cores);
        cpuValues.put("threads", threads);
        cpuValues.put("frequency", frequency);
        cpuValues.put("tdp", tdp);
        cpuValues.put("sockid", sockid);
        db.insert(CPU, null, cpuValues);

    }

    public void insertGPU(String name, String manufacturer, String vendor, int vram, int frequency, int tdp) {
        db = this.getWritableDatabase();
        ContentValues gpuValues = new ContentValues();
        gpuValues.put("name", name);
        gpuValues.put("manufacturer", manufacturer);
        gpuValues.put("vendor", vendor);
        gpuValues.put("frequency", frequency);
        gpuValues.put("vram", vram);
        gpuValues.put("tdp", tdp);
        db.insert(GPU, null, gpuValues);

    }

    public void insertPUNIT(String name, String manufacturer, String APFC, String certificate, int output_power) {
        db = this.getWritableDatabase();
        ContentValues puValues = new ContentValues();
        puValues.put("name", name);
        puValues.put("manufacturer", manufacturer);
        puValues.put("APFC", APFC);
        puValues.put("certificate", certificate);
        puValues.put("output_power", output_power);
        db.insert(POWER_UNIT, null, puValues);

    }

    public ArrayList<SpinnerItem> getCpuNames() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id, name FROM cpu", null);
        String[] names = new String[cursor.getCount()];
        int i = 0;
        ArrayList<SpinnerItem> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            list.add(new SpinnerItem(cursor.getInt(0),cursor.getString(1)));
        }
        cursor.close();
        return list;
    }


    public ArrayList<SpinnerItem> getMotherboardNamesByCpuId(int cpuId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT motherboard._id,motherboard.name FROM cpu as c " +
                "join motherboard on c.sockid = motherboard.sockid WHERE c._id=?", new String[]{String.valueOf(cpuId)});
        String[] names = new String[cursor.getCount()];

        ArrayList<SpinnerItem> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            list.add(new SpinnerItem(cursor.getInt(0),cursor.getString(1)));
        }
        cursor.close();
        return list;
    }

    public int getCpuTdp(String cpuName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT tdp FROM cpu WHERE name=?", new String[]{cpuName});
        int tdp = 0;
        if (cursor.moveToFirst()) {
            tdp = cursor.getInt(0);
        }
        cursor.close();
        return tdp;
    }

    public ArrayList<SpinnerItem> getGpuNames() {
        ArrayList<SpinnerItem> gpuNamesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"_id", "name"};
        Cursor cursor = db.query("gpu", projection, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                SpinnerItem spinnerItem = new SpinnerItem(id, name);
                gpuNamesList.add(spinnerItem);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return gpuNamesList;
    }

    public int getGpuTdp(String gpuName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT tdp FROM gpu WHERE name=?", new String[]{gpuName});
        int tdp = 0;
        if (cursor.moveToFirst()) {
            tdp = cursor.getInt(0);
        }
        cursor.close();
        return tdp;
    }

}




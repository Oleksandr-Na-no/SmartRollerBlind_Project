package com.example.smart_roller_blind;

import static com.example.smart_roller_blind.MainActivity.dataBaseHelper;
import static com.example.smart_roller_blind.RecyclerViewAdapter.ubd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {


    public static final String CUSTOMER_TABLE = "CUSTOMER_TABLE";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_IS_OPEN = "IS_OPEN";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_POSITION = "POSITION";
    public static final String COLUMN_SUB_DEVISE = "SUB_DEVISE";
    public static final String COLUMN_SPEED_TOP = "SPEED_TOP";
    public static final String COLUMN_SPEED_DOWN = "SPEED_DOWN";
    public static final String COLUMN_ACTIVE = "ACTIVE";
    public static final String COLUMN_LENGTH = "LENGTH";

    public static final String COLUMN_ACTIVE_CHECK = "ACTIVE_CHECK";
    public static final String COLUMN_IMAGE = "IMAGE";
    public static final String COLUMN_AUTO_ON = "AUTO_ON";
    public static final String COLUMN_AUTO_THRESHOLD = "AUTO_THRESHOLD";
    public static final String COLUMN_SENSOR_NOW = "SENSOR_NOW";
    public static final String COLUMN_ALARM = "ALARM";

    public DataBaseHelper(@Nullable Context context){
        super(context, "Object.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableStatement = "CREATE TABLE " + CUSTOMER_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_IS_OPEN + " BOOL, "
                + COLUMN_POSITION + " INT, "

                + COLUMN_SUB_DEVISE + " TEXT, "
                + COLUMN_SPEED_TOP + " INT, "
                + COLUMN_SPEED_DOWN + " INT, "
                + COLUMN_ACTIVE + " BOOL, "
                + COLUMN_LENGTH + " INT, "

                + COLUMN_ACTIVE_CHECK + " BOOL, "
                + COLUMN_IMAGE + " TEXT, "
                + COLUMN_AUTO_ON + " BOOL, "
                + COLUMN_AUTO_THRESHOLD + " INT, "
                + COLUMN_SENSOR_NOW + " INT, "
                + COLUMN_ALARM + " TEXT)";

//                String createTableStatement = "CREATE TABLE " + CUSTOMER_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT, " + COLUMN_IS_OPEN + " BOOL, " + COLUMN_POSITION + " INT, " + COLUMN_SUB_DEVISE + " TEXT)";
//        String createTableStatement = "CREATE TABLE " + CUSTOMER_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT, " + COLUMN_IS_OPEN + " BOOL, " + COLUMN_POSITION + " INT, " + COLUMN_SUB_DEVISE + " TEXT, " + COLUMN_SPEED_TOP + " INT, " + COLUMN_SPEED_DOWN + " INT, " + COLUMN_ACTIVE + " BOOL, " + COLUMN_LENGTH + " INT)";
//        String createTableStatement = "CREATE TABLE " + CUSTOMER_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT, " + COLUMN_IS_OPEN + " BOOL)";

        db.execSQL(createTableStatement);
//        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public boolean addOne(Object_model object_model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, object_model.getName());
        cv.put(COLUMN_IS_OPEN, object_model.getisOpen());
        cv.put(COLUMN_POSITION, object_model.getPosition());
        cv.put(COLUMN_SUB_DEVISE, object_model.getSup_devise());
        cv.put(COLUMN_SPEED_TOP, object_model.getSpeed_up());
        cv.put(COLUMN_SPEED_DOWN, object_model.getSpeed_down());
        cv.put(COLUMN_ACTIVE, object_model.getActive());
        cv.put(COLUMN_LENGTH, object_model.getLength());

        cv.put(COLUMN_ACTIVE_CHECK, object_model.getActive_check());
        cv.put(COLUMN_IMAGE, object_model.getImage());
        cv.put(COLUMN_AUTO_ON, object_model.getAuto_on());
        cv.put(COLUMN_AUTO_THRESHOLD, object_model.getAuto_threshold());
        cv.put(COLUMN_SENSOR_NOW, object_model.getSensor_now());
        cv.put(COLUMN_ALARM, object_model.getAlarmStr());

        long insert = db.insert(CUSTOMER_TABLE, null, cv);
//        db.close();
        if (insert == -1) {
            return false;
        }
        else {
            return true;
        }
    }

    public boolean deleteOne(Object_model object_model) {
// find customerModel in the database. if it found, delete it and return true.
// if it is not found, return false
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + CUSTOMER_TABLE + " WHERE " + COLUMN_ID + "=" + object_model.getId();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            cursor.close();
//            db.close();
            return true;
        } else {
            cursor.close();
//            db.close();
            return false;
        }

    }

    public void update_one(Object_model object_model){
            Update_one asyncTask = new Update_one();
            asyncTask.execute(object_model);
    }


    private class Update_one extends AsyncTask<Object_model, String, Boolean> {
        @Override
        protected Boolean doInBackground(Object_model... strings) {
                Object_model object_model = strings[0];
                SQLiteDatabase db = DataBaseHelper.this.getWritableDatabase();
                ContentValues cv = new ContentValues();

                cv.put(COLUMN_NAME, object_model.getName());
                cv.put(COLUMN_IS_OPEN, object_model.getisOpen());
                cv.put(COLUMN_POSITION, object_model.getPosition());
                cv.put(COLUMN_SUB_DEVISE, object_model.getSup_devise());
                cv.put(COLUMN_SPEED_TOP, object_model.getSpeed_up());
                cv.put(COLUMN_SPEED_DOWN, object_model.getSpeed_down());
                cv.put(COLUMN_ACTIVE, object_model.getActive());
                cv.put(COLUMN_LENGTH, object_model.getLength());

                cv.put(COLUMN_ACTIVE_CHECK, object_model.getActive_check());
                cv.put(COLUMN_IMAGE, object_model.getImage());
                cv.put(COLUMN_AUTO_ON, object_model.getAuto_on());
                cv.put(COLUMN_AUTO_THRESHOLD, object_model.getAuto_threshold());
                cv.put(COLUMN_SENSOR_NOW, object_model.getSensor_now());

                cv.put(COLUMN_ALARM, object_model.getAlarmStr());

                long insert = db.update(CUSTOMER_TABLE, cv, "id = ?", new String[]{String.valueOf(object_model.getId())});
//        db.close();
                if (insert == -1) {
                    return false;
                } else {
                    return true;
                }
        }
        @Override
        protected void onPostExecute(Boolean bull){
            Log.e("Update: ","Update_one");
            ubd = true;
        }
    }


    public boolean deleteOne_id(int id) {
// find customerModel in the database. if it found, delete it and return true.
// if it is not found, return false
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + CUSTOMER_TABLE + " WHERE " + COLUMN_ID + "=" + id;
        Cursor cursor = db. rawQuery(queryString, null);

        if (cursor.moveToFirst()){
            cursor.close();
//            db.close();
            return true;
        }
        else {
            cursor.close();
//            db.close();
            return false;
        }
    }
    public Object_model getOne(int i){
        String queryString = "SELECT * FROM " + CUSTOMER_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);
        cursor.moveToPosition(i);
        int ID = cursor.getInt(1);
        String name = cursor.getString(1);
        boolean isOpen = cursor.getInt(2) == 1;
        int position = cursor.getInt(3);
        String sup_devise = cursor.getString(4);
        int speed_up = cursor.getInt(5);
        int speed_down = cursor.getInt(6);
        boolean active = cursor.getInt(7) == 1;
        int length = cursor.getInt(8);

        boolean active_check = cursor.getInt(9) == 1;
        String image = cursor.getString(10);
        boolean auto_on = cursor.getInt(11) == 1;
        int auto_threshold = cursor.getInt(12);
        int sensor_now = cursor.getInt(13);
        String alarms = cursor.getString(14);

        return new Object_model(name, isOpen, position, ID, sup_devise, speed_up, speed_down ,active, length, active_check, image, auto_on, auto_threshold, sensor_now, alarms);
    }

    public List<Object_model> getEveryone() {
        List<Object_model> returnList = new ArrayList<>();
// get data from the database
        String queryString = "SELECT * FROM " + CUSTOMER_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);
        if (cursor.moveToFirst()) {
// loop through the cursor (result set) and create new customer objects. Put them into the return lis
            do {
                int ID = cursor.getInt(0);
                String name = cursor.getString(1);
                boolean isOpen = cursor.getInt(2) == 1;
                int position = cursor.getInt(3);
                String sup_devise = cursor.getString(4);
                int speed_up = cursor.getInt(5);
                int speed_down = cursor.getInt(6);
                boolean active = cursor.getInt(7) == 1;
                int length = cursor.getInt(8);

                boolean active_check = cursor.getInt(9) == 1;
                String image = cursor.getString(10);
                boolean auto_on = cursor.getInt(11) == 1;
                int auto_threshold = cursor.getInt(12);
                int sensor_now = cursor.getInt(13);
                String alarms = cursor.getString(14);

                Object_model newCustomer = new Object_model(name, isOpen, position, ID, sup_devise, speed_up, speed_down ,active, length, active_check, image, auto_on, auto_threshold, sensor_now, alarms);///llll
//                Object_model newCustomer = new Object_model(name, isOpen, position, ID);

                returnList.add(newCustomer);
            } while (cursor.moveToNext());
        } else {
            // failure. do not add anything to the list.
        }
// close both the cursor and the db when done.
        cursor.close();
//        db.close();
        return returnList;
    }
}

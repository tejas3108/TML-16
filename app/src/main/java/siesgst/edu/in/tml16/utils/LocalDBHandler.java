package siesgst.edu.in.tml16.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by vishal on 1/1/16.
 */
public class LocalDBHandler extends SQLiteOpenHelper {

    final String U_KEY = "uID";
    final String U_NAME = "uName";
    final String U_EMAIL = "uEmail";
    final String U_PHONE = "uPhone";
    final String YEAR = "Year";
    final String BRANCH = "Branch";
    final String DIVISION = "Division";
    final String COLLEGE = "College";
    final String U_CREATED = "uCreated";
    final String U_MODIFIED = "uModified";

    final String E_KEY = "eID";
    final String E_NAME = "eName";
    final String E_DAY = "eDay";
    final String E_VENUE = "eVenue";
    final String E_CATEGORY = "eCategory";
    final String E_SUBCATEGORY = "eSubCategory";
    final String E_DETAIL = "eDetails";
    final String EVENT_HEAD_1 = "eHead1";
    final String EVENT_HEAD_2 = "eHead2";
    final String E_PHONE_1 = "ePhone1";
    final String E_PHONE_2 = "ePhone2";
    final String E_CREATED = "eCreated";
    final String E_MODIFIED = "eModified";

    final String F_MESSAGE = "fMessage";
    final String F_PICTURE = "fPicture";
    final String F_LINK = "fLink";
    final String F_LIKES = "fLikes";
    final String F_COMMENTS = "fComments";

    final String S_NAME = "sName";
    final String S_PATH = "sPath";
    final String S_LINK = "sLink";

    final String PAYMENT_STATUS = "upayment_status";

    final int DB_VERSION = 1;

    final String USER_TABLE_NAME = "user_table";
    final String EVENT_TABLE_NAME = "event_table";
    final String REG_TABLE_NAME = "reg_table";
    final String FB_DATA_TABLE = "fb_data";
    final String REG_EVENT_DATA_TABLE = "reg_events_data";
    final String SPONSORS_EVENTS = "sponsors";

    final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS user_table(uID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "uName VARCHAR DEFAULT NULL," +
            "uEmail VARCHAR DEFAULT NULL," +
            "uPhone VARCHAR DEFAULT NULL," +
            "Year VARCHAR DEFAULT NULL," +
            "Branch VARCHAR DEFAULT NULL," +
            "Division VARCHAR DEFAULT NULL," +
            "College VARCHAR DEFAULT NULL," +
            "uCreated VARCHAR DEFAULT NULL," +
            "uModified VARCHAR DEFAULT NULL)";

    final String CREATE_EVENT_TABLE = "CREATE TABLE IF NOT EXISTS event_table(eID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "eName VARCHAR DEFAULT NULL," +
            "eDay VARCHAR DEFAULT NULL," +
            "eVenue VARCHAR DEFAULT NULL," +
            "eCategory VARCHAR DEFAULT NULL," +
            "eSubCategory VARCHAR DEFAULT NULL," +
            "eDetails VARCHAR DEFAULT NULL," +
            "eHead1 VARCHAR DEFAULT NULL," +
            "ePhone1 VARCHAR DEFAULT NULL," +
            "eHead2 VARCHAR DEFAULT NULL," +
            "ePhone2 VARCHAR DEFAULT NULL," +
            "eCreated VARCHAR DEFAULT NULL," +
            "eModified VARCHAR DEFAULT NULL)";

    final String CREATE_REG_TABLE = "CREATE TABLE IF NOT EXISTS reg_table(uID VARCHAR DEFAULT NULL," +
            "eID VARCHAR DEFAULT NULL," +
            "pStatus VARCHAR DEFAULT NULL)";

    final String CREATE_FB_DATA_TABLE = "CREATE TABLE IF NOT EXISTS fb_data(fID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "fMessage VARCHAR DEFAULT NULL," +
            "fPicture VARCHAR DEFAULT NULL," +
            "fLink VARCHAR DEFAULT NULL," +
            "fLikes VARCHAR DEFAULT NULL," +
            "fComments VARCHAR DEFAULT NULL," +
            "fNext VARCHAR DEFAULT NULL)";

    final String CREATE_REG_EVENT_TABLE = "CREATE TABLE IF NOT EXISTS reg_events_data(eID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "eName VARCHAR DEFAULT NULL," +
            "upayment_status VARCHAR DEFAULT NULL)";

    final String CREATE_SPONSOR_TABLE = "CREATE TABLE IF NOT EXISTS sponsors(sID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "sName VARCHAR DEFAULT NULL," +
            "sPath VARCHAR DEFAULT NULL," +
            "sLink VARCHAR DEFAULT NULL)";


    public LocalDBHandler(Context context) {
        super(context, "tml_event_details", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_REG_EVENT_TABLE);
        database.execSQL(CREATE_USER_TABLE);
        database.execSQL(CREATE_EVENT_TABLE);
        database.execSQL(CREATE_REG_TABLE);
        database.execSQL(CREATE_FB_DATA_TABLE);
        database.execSQL(CREATE_SPONSOR_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void wapasTableBana() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + FB_DATA_TABLE);
        db.execSQL(CREATE_FB_DATA_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE_NAME);
        db.execSQL(CREATE_EVENT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + REG_TABLE_NAME);
        db.execSQL(CREATE_REG_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + REG_EVENT_DATA_TABLE);
        db.execSQL(CREATE_REG_EVENT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SPONSORS_EVENTS);
        db.execSQL(CREATE_SPONSOR_TABLE);
        db.close();
    }

    public void dropFBTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + FB_DATA_TABLE);
        db.execSQL(CREATE_FB_DATA_TABLE);
        db.close();
    }

    public void dropEventsTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE_NAME);
        db.execSQL(CREATE_EVENT_TABLE);
        db.close();
    }

    public void dropRegEventTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + REG_EVENT_DATA_TABLE);
        db.execSQL(CREATE_REG_EVENT_TABLE);
        db.close();
    }

    public void dropSponsorsTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + SPONSORS_EVENTS);
        db.execSQL(CREATE_SPONSOR_TABLE);
        db.close();
    }

    public boolean doesExists()///TO CHECK IF DB EXISTS
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(USER_TABLE_NAME, new String[]{U_NAME}, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            Log.d("TML", "DATABASE ENTRIES: " + cursor.getCount());
            cursor.close();
            db.close();
            return true;
        }
        Log.d("TML", "NO DATABASE;WILL CREATE ACCORDINGLY");
        db.close();
        return false;
    }

    public void insertUserData(String[] data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(U_NAME, data[0]);
        values.put(U_EMAIL, data[1]);
        values.put(U_PHONE, data[2]);
        values.put(YEAR, data[3]);
        values.put(BRANCH, data[4]);
        values.put(DIVISION, data[5]);
        values.put(COLLEGE, data[6]);
        values.put(U_CREATED, data[7]);
        values.put(U_MODIFIED, data[8]);
        db.insert(USER_TABLE_NAME, null, values);
        db.close();
    }

    public void insertEventData(String[] data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(E_NAME, data[0]);
        values.put(E_DAY, data[1]);
        values.put(E_VENUE, data[2]);
        values.put(E_CATEGORY, data[3]);
        values.put(E_SUBCATEGORY, data[4]);
        values.put(E_DETAIL, data[5]);
        values.put(EVENT_HEAD_1, data[6]);
        values.put(E_PHONE_1, data[7]);
        values.put(EVENT_HEAD_2, data[8]);
        values.put(E_PHONE_2, data[9]);
        values.put(E_CREATED, data[10]);
        values.put(E_MODIFIED, data[11]);
        db.insert(EVENT_TABLE_NAME, null, values);
        db.close();
    }

    public void insertRegData(String[] data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(U_KEY, data[0]);
        values.put(E_KEY, data[1]);
        values.put(PAYMENT_STATUS, data[2]);
        db.insert(REG_TABLE_NAME, null, values);
        db.close();
    }

    public void insertFBData(String[] data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(F_MESSAGE, data[0]);
        values.put(F_PICTURE, data[1]);
        values.put(F_LINK, data[2]);
        values.put(F_LIKES, data[3]);
        values.put(F_COMMENTS, data[4]);
        db.insert(FB_DATA_TABLE, null, values);
        db.close();
    }

    public void insertRegEvents(String[] data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(E_NAME, data[0]);
        values.put(PAYMENT_STATUS, data[1]);
        db.insert(REG_EVENT_DATA_TABLE, null, values);
        db.close();
    }

    public void insertSponsorData(String[] data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(S_NAME, data[0]);
        values.put(S_PATH, data[1]);
        values.put(S_LINK, data[2]);
        db.insert(SPONSORS_EVENTS, null, values);
        db.close();
    }

    /*public void insertNextValue(String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(F_NEXT, value);
        db.insert(FB_DATA_TABLE, null, values);
        db.close();
    }*/

    public ArrayList<String> getEventNamesAndDay(String category, String subCategory) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        if (subCategory != null) {
            cursor = db.query(EVENT_TABLE_NAME, new String[]{E_NAME, E_DAY}, E_CATEGORY + "='" + category + "' and " + E_SUBCATEGORY + "='" + subCategory + "'", null, null, null, null, null);
        } else {
            cursor = db.query(EVENT_TABLE_NAME, new String[]{E_NAME, E_DAY}, E_CATEGORY + "='" + category + "'", null, null, null, null, null);
        }
        ArrayList<String> arrayList = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            arrayList.add(cursor.getString(cursor.getColumnIndex(E_NAME)));
            arrayList.add(cursor.getString(cursor.getColumnIndex(E_DAY)));
        }
        cursor.close();
        db.close();

        return arrayList;
    }

    public ArrayList<String> getAllEventNames() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(EVENT_TABLE_NAME, new String[]{E_NAME}, null, null, null, null, null, null);
        ArrayList<String> arrayList = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            arrayList.add(cursor.getString(cursor.getColumnIndex(E_NAME)));
        }
        cursor.close();
        db.close();

        return arrayList;
    }

    public ArrayList<String> getEventKaSabKuch(String eventName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(EVENT_TABLE_NAME, new String[]{E_DETAIL, E_DAY, E_VENUE, EVENT_HEAD_1, EVENT_HEAD_2, E_PHONE_1, E_PHONE_2}, E_NAME + "='" + eventName + "'", null, null, null, null);
        ArrayList<String> arrayList = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            arrayList.add(cursor.getString(cursor.getColumnIndex(E_DETAIL)));
            arrayList.add(cursor.getString(cursor.getColumnIndex(E_DAY)));
            arrayList.add(cursor.getString(cursor.getColumnIndex(E_VENUE)));
            arrayList.add(cursor.getString(cursor.getColumnIndex(EVENT_HEAD_1)));
            arrayList.add(cursor.getString(cursor.getColumnIndex(EVENT_HEAD_2)));
            arrayList.add(cursor.getString(cursor.getColumnIndex(E_PHONE_1)));
            arrayList.add(cursor.getString(cursor.getColumnIndex(E_PHONE_2)));
        }
        cursor.close();
        db.close();

        return arrayList;
    }

    public ArrayList<String> getFBData() {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.query(FB_DATA_TABLE, new String[]{F_MESSAGE, F_PICTURE, F_LINK, F_LIKES, F_COMMENTS}, null, null, null, null, null);
            ArrayList<String> arrayList = new ArrayList<>();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                arrayList.add(cursor.getString(cursor.getColumnIndex(F_MESSAGE)));
                arrayList.add(cursor.getString(cursor.getColumnIndex(F_PICTURE)));
                arrayList.add(cursor.getString(cursor.getColumnIndex(F_LINK)));
                arrayList.add(cursor.getString(cursor.getColumnIndex(F_LIKES)));
                arrayList.add(cursor.getString(cursor.getColumnIndex(F_COMMENTS)));
            }
            cursor.close();
            db.close();
        return arrayList;
    }

    public ArrayList<String> getRegEventData() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(REG_EVENT_DATA_TABLE, new String[]{E_NAME, PAYMENT_STATUS}, null, null, null, null, null);
        ArrayList<String> arrayList = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            arrayList.add(cursor.getString(cursor.getColumnIndex(E_NAME)));
            arrayList.add(cursor.getString(cursor.getColumnIndex(PAYMENT_STATUS)));
        }
        cursor.close();
        db.close();

        return arrayList;
    }

    public ArrayList<String> getSponsorDetails() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(SPONSORS_EVENTS, new String[]{"*"}, null, null, null, null, null);
        ArrayList<String> arrayList = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            arrayList.add(cursor.getString(cursor.getColumnIndex(S_NAME)));
            arrayList.add(cursor.getString(cursor.getColumnIndex(S_PATH)));
            arrayList.add(cursor.getString(cursor.getColumnIndex(S_LINK)));
        }
        cursor.close();
        db.close();

        return arrayList;
    }

    /*public ArrayList<String> getSubCategoryDetails(String subCategory) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(EVENT_TABLE_NAME, new String[]{E_NAME, E_DETAIL, E_DAY, E_VENUE, EVENT_HEAD_1, EVENT_HEAD_2, E_PHONE_1, E_PHONE_2}, E_SUBCATEGORY + "='" + subCategory + "'", null, null, null, null);
        ArrayList<String> arrayList = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            arrayList.add(cursor.getString(cursor.getColumnIndex(E_NAME)));
            arrayList.add(cursor.getString(cursor.getColumnIndex(E_DETAIL)));
            arrayList.add(cursor.getString(cursor.getColumnIndex(E_DAY)));
            arrayList.add(cursor.getString(cursor.getColumnIndex(E_VENUE)));
            arrayList.add(cursor.getString(cursor.getColumnIndex(EVENT_HEAD_1)));
            arrayList.add(cursor.getString(cursor.getColumnIndex(EVENT_HEAD_2)));
            arrayList.add(cursor.getString(cursor.getColumnIndex(E_PHONE_1)));
            arrayList.add(cursor.getString(cursor.getColumnIndex(E_PHONE_2)));
        }
        cursor.close();
        db.close();

        return arrayList;
    }*/

    public int getDBVersion() {
        return DB_VERSION;
    }
}
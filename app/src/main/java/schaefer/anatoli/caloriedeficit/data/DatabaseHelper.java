package schaefer.anatoli.caloriedeficit.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import schaefer.anatoli.caloriedeficit.model.DayEntry;
import schaefer.anatoli.caloriedeficit.model.EntryItem;
import schaefer.anatoli.caloriedeficit.model.PersonalData;
import schaefer.anatoli.caloriedeficit.model.SportEntryItem;
import schaefer.anatoli.caloriedeficit.util.PersonalDataConfiguration;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "DatabaseHelper";

    //DB
    private static final String DATABASE_NAME = "db_caloriedeficit";
    private static final int DATABASE_VERSION = 1;



    //TABLE PERSONALDATA
    private static final String TABLE_PERSONALDATA = "PERSONALDATA";
    private static final String KEY_ID = "id";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_BODYSIZE = "bodysize";
    public static final String KEY_WEIGHT = "weight";
    public static final String KEY_AGE = "age";
    public static final String KEY_PALACTIVITY = "palactivity";
    public static final String KEY_FIRSTIMAGE = "firstimage";
    public static final String KEY_DATE = "date";

    //TABLE ENTRYITEM
    public static final String TABLE_ENTRYITEM = "ENTRYITEM";
    public static final String KEY_LABEL = "label";
   // public static final String KEY_QUANTITY = "quantity";
    public static final String KEY_CALORIES = "calories";
    public static final String KEY_FK_ENTRYITEM_DAYENTRY = "fk_dayentry";

    //TABLE DAYENTRY
    public static final String TABLE_DAYENTRY = "DAYENTRY";
    public static final String KEY_DAY_DEFICIT = "daydeficit";
    public static final String KEY_SUM_ATE = "sumate";
    public static final String KEY_SUM_SPORT = "sumsport";
    public static final String KEY_PERFORMANCE_VALUE = "perfvalue";
    private static final String KEY_DAYIMAGE = "dayimage";


    //TABLE SPORTENTRYITEM
    public static final String TABLE_SPORTENTRYITEM = "SPORTENTRYITEM";
    public static final String KEY_FK_SPORTENTRYITEM_DAYENTRY = "fk_dayentryid";



    //CREATE TABLE STATMENTS
    //create table PERSONALDATA (id, gender, bodySize, weight, age, palActivity, firstImage, date)
    // int id, String gender, int bodySize, int weight, int age, float palActivity, byte[] firstImage, Date date
    private static final String CREATE_PERSONALDATA_TABLE = "CREATE TABLE " + TABLE_PERSONALDATA + " ("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_GENDER + " TEXT,"
            + KEY_BODYSIZE + " INTEGER,"
            + KEY_WEIGHT + " INTEGER,"
            + KEY_AGE + " INTEGER,"
            + KEY_PALACTIVITY + " REAL,"
            + KEY_FIRSTIMAGE + " BLOB,"
            + KEY_DATE + " TEXT" + ")";


    //create table DAYENTRY
    private static final String CREATE_DAYENTRY_TABLE = "CREATE TABLE " + TABLE_DAYENTRY + " ("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_DATE + " TEXT,"
            + KEY_WEIGHT + " REAL,"
            + KEY_DAY_DEFICIT + " INTEGER,"
            + KEY_DAYIMAGE + " BLOB,"
            + KEY_PERFORMANCE_VALUE + " INTEGER,"
            + KEY_SUM_ATE + " INTEGER,"
            + KEY_SUM_SPORT + " INTEGER"
            + ")";



    //create table ENTRYITEM (id INTEGER PRIMARY KEY, label TEXT, quantity INTEGER, calories INTEGER, fk_dayentry INTEGER)
    private static final String CREATE_ENTRYITEM_TABLE = "CREATE TABLE " + TABLE_ENTRYITEM + " ("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_LABEL + " TEXT,"
            // + KEY_QUANTITY + " INTEGER,"
            + KEY_CALORIES + " INTEGER,"
            //             + KEY_DATE + " TEXT,"
            + KEY_FK_ENTRYITEM_DAYENTRY + " INTEGER,"
            + " FOREIGN KEY (" + KEY_FK_ENTRYITEM_DAYENTRY + ") REFERENCES " + TABLE_DAYENTRY + " (" + KEY_ID + ")"
            + ")";


    //create table SPORTENTRYITEM
    private static final String CREATE_SPORTENTRYITEM_TABLE = "CREATE TABLE " + TABLE_SPORTENTRYITEM + " ("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_LABEL + " TEXT,"
            + KEY_CALORIES + " INTEGER,"
            + KEY_FK_SPORTENTRYITEM_DAYENTRY + " INTEGER,"
            + " FOREIGN KEY (" + KEY_FK_SPORTENTRYITEM_DAYENTRY + ") REFERENCES " + TABLE_DAYENTRY + " (" + KEY_ID + ")"
            + ")";



    //Database update MIGRATION !
   // private PersonalData tmpPersonalData;
   // private ArrayList<DayEntry> tmpDayEntries;
   // private ArrayList<EntryItem> tmpEntryItems;
   // private ArrayList<SportEntryItem> tmpSportEntryItems;

   // boolean isDataTemporarlySaved = false;

    private static DatabaseHelper instance;

    private DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHelper getInstance(Context context){
        if(instance == null){
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQL - Structured Query Language

        //db erzeugen
        db.execSQL(CREATE_PERSONALDATA_TABLE);
        db.execSQL(CREATE_DAYENTRY_TABLE);
        db.execSQL(CREATE_ENTRYITEM_TABLE);
        db.execSQL(CREATE_SPORTENTRYITEM_TABLE);

       // if(isDataTemporarlySaved) {
       //     fillTempDataToNewDB();
       // }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


      //  if (newVersion > oldVersion) {
      //      db.execSQL("ALTER TABLE " + TABLE_DAYENTRY + " ADD COLUMN " + KEY_SUM_SPORT +  "INTEGER DEFAULT 0");
      //      this.isDataTemporarlySaved = saveDataTemporarily();
      //  }


        String DROP_TABLE_PERSONALDATA = "DROP TABLE IF EXISTS " + TABLE_PERSONALDATA;
        db.execSQL(DROP_TABLE_PERSONALDATA);

        String DROP_TABLE_ENTRYITEM = "DROP TABLE IF EXISTS " + TABLE_DAYENTRY;
        db.execSQL(DROP_TABLE_ENTRYITEM);

        String DROP_TABLE_DAYENTRY = "DROP TABLE IF EXISTS " + TABLE_ENTRYITEM;
        db.execSQL(DROP_TABLE_DAYENTRY);

        String DROP_TABLE_SPORTENTRYITEM = "DROP TABLE IF EXISTS " + TABLE_SPORTENTRYITEM;
        db.execSQL(DROP_TABLE_SPORTENTRYITEM);

        //danach neu erzeugen
        onCreate(db);
    }


  /*  //daten vorrübergehend speichern
    private boolean saveDataTemporarily(){
        boolean hasWorked = false;

        try {
            tmpPersonalData = getPersonalData(PersonalDataConfiguration.PERSONALDATA_ID);
            tmpDayEntries = getAllDayEntriesSortedByDate();
            tmpEntryItems = getAllEntryItems();
            tmpSportEntryItems = getAllSportEntryItems();

            hasWorked = true;
        }catch (Exception e){
            e.printStackTrace();
            hasWorked = false;
        }

        Log.d(TAG,"saveDataTemporarily");
        return  hasWorked;
    }

    //nach erzeugung der neuen DB daten wieder reinschreiben
    private  void fillTempDataToNewDB(){
        addPersonalData(tmpPersonalData);

        for(DayEntry de: tmpDayEntries){
            addDayEntry(de);
        }

        for(EntryItem eItem: tmpEntryItems){
            addEntryItem(eItem);
        }

        for(SportEntryItem sEntryItem: tmpSportEntryItems){
            addSportEntryItem(sEntryItem);
        }

        Log.d(TAG,"fillTempDataToNewDB");

    }

   */





    //CRUD = create, read, update, delete-------------------------------------------------------------------

    //add addDayEntry
    public int addDayEntry(DayEntry dayEntry){
        //etwas der db hinzufuegen >> writableDB
        SQLiteDatabase db = this.getWritableDatabase();

        //hilfsklasse um daten in db zu schieben
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_DAY_DEFICIT, dayEntry.getDayDeficit());
        contentValues.put(KEY_DAYIMAGE,  dayEntry.getDayImage() );
        contentValues.put(KEY_DATE, dayEntry.getDate() );
        contentValues.put(KEY_PERFORMANCE_VALUE,  dayEntry.getPerformanceValue());
        contentValues.put(KEY_SUM_ATE, dayEntry.getSumAte() );
        contentValues.put(KEY_WEIGHT, dayEntry.getWeight());
        //contentValues.put( , dayEntry.getEntryItems());
        contentValues.put(KEY_SUM_SPORT, dayEntry.getSumSport() );

        //in db schreiben
        int val = (int) db.insert(TABLE_DAYENTRY,null, contentValues);
       // Log.d(TAG, "addDayEntry: " + val);
        return val;

    }


    //update DayEntry
    public int updateDayEntry(DayEntry dayEntry){
        SQLiteDatabase db = this.getWritableDatabase();

        //hilfsklasse um daten in db zu schieben
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_DAY_DEFICIT, dayEntry.getDayDeficit());
        contentValues.put(KEY_DAYIMAGE,  dayEntry.getDayImage() );
        contentValues.put(KEY_DATE, dayEntry.getDate() );
        contentValues.put(KEY_PERFORMANCE_VALUE,  dayEntry.getPerformanceValue());
        contentValues.put(KEY_SUM_ATE, dayEntry.getSumAte() );
        contentValues.put(KEY_WEIGHT, dayEntry.getWeight());
        contentValues.put(KEY_SUM_SPORT, dayEntry.getSumSport() );

        //update the row
      //
        int val = db.update(TABLE_DAYENTRY, contentValues, KEY_ID + "=?", new String[]{String.valueOf(dayEntry.getId())});
       // Log.d(TAG, "updateDayEntry " + val);
        return val;
    }

    //delete DayEntry
    public int deleteDayEntry(int dayEntryId){
        SQLiteDatabase db = this.getWritableDatabase();

        int val = db.delete(TABLE_DAYENTRY, KEY_ID + "=?", new String[]{String.valueOf(dayEntryId)});
       // Log.d(TAG, "deleteDayEntry: " + val);
        return val;
    }


    //get/read DAYENTRY
    public DayEntry getDayEntryById(int dayEntryId){
        SQLiteDatabase db = this.getReadableDatabase();

        //cursors to iterate over the table
        Cursor cursor = db.query(TABLE_DAYENTRY, new String[]{KEY_ID, KEY_DATE, KEY_WEIGHT, KEY_DAY_DEFICIT, KEY_DAYIMAGE, KEY_PERFORMANCE_VALUE, KEY_SUM_ATE, KEY_SUM_SPORT},
                KEY_ID + "=?",
                new String[]{String.valueOf(dayEntryId)},
                null, null, null);

        if(cursor != null){
            cursor.moveToFirst();

            DayEntry dayEntry= new DayEntry();
            dayEntry.setId(cursor.getInt(0));
            dayEntry.setDate(cursor.getString(1));
            dayEntry.setWeight(cursor.getFloat(2));
            dayEntry.setDayDeficit(cursor.getInt(3));
            dayEntry.setDayImage(cursor.getBlob(4));
            dayEntry.setPerformanceValue(cursor.getInt(5));
            dayEntry.setSumAte(cursor.getInt(6));
            dayEntry.setSumSport(cursor.getInt(7));

            cursor.close();
            return dayEntry;
        }else {
            //cursor ist hier null also kein close aufruf !
            return null;
        }
    }

    //get/read DAYENTRY
    public DayEntry getDayEntryByDate(String date){
        SQLiteDatabase db = this.getReadableDatabase();

        //cursors to iterate over the table
        Cursor cursor = db.query(TABLE_DAYENTRY, new String[]{KEY_ID, KEY_DATE, KEY_WEIGHT, KEY_DAY_DEFICIT, KEY_DAYIMAGE, KEY_PERFORMANCE_VALUE, KEY_SUM_ATE, KEY_SUM_SPORT},
                KEY_DATE + "=?",
                new String[]{String.valueOf(date)},
                null, null, null);

        if(cursor != null){
            cursor.moveToFirst();

            DayEntry dayEntry= new DayEntry();
            dayEntry.setId(cursor.getInt(0));
            dayEntry.setDate(cursor.getString(1));
            dayEntry.setWeight(cursor.getFloat(2));
            dayEntry.setDayDeficit(cursor.getInt(3));
            dayEntry.setDayImage(cursor.getBlob(4));
            dayEntry.setPerformanceValue(cursor.getInt(5));
            dayEntry.setSumAte(cursor.getInt(6));
            dayEntry.setSumSport(cursor.getInt(7));

            cursor.close();
            return dayEntry;
        }else {
            //cursor ist hier null also kein close aufruf !
            return null;
        }
    }


    //get/read ALL DAYENTRYs
    public ArrayList<DayEntry> getAllDayEntriesSortedByDate(){
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<DayEntry> dayEntries = new ArrayList<>();

        //cursors to iterate over the table
        Cursor cursor = db.query(TABLE_DAYENTRY, new String[]{KEY_ID, KEY_DATE, KEY_WEIGHT, KEY_DAY_DEFICIT, KEY_DAYIMAGE, KEY_PERFORMANCE_VALUE, KEY_SUM_ATE, KEY_SUM_SPORT},
                null,
                null,
                null,
                null,
                KEY_DATE); //orderBY

        try {
            while (cursor.moveToNext()) {
                DayEntry dayEntry  = new DayEntry();
                dayEntry.setId(cursor.getInt(0));
                dayEntry.setDate(cursor.getString(1));
                dayEntry.setWeight(cursor.getFloat(2));
                dayEntry.setDayDeficit(cursor.getInt(3));
                dayEntry.setDayImage(cursor.getBlob(4));
                dayEntry.setPerformanceValue(cursor.getInt(5));
                dayEntry.setSumAte(cursor.getInt(6));
                dayEntry.setSumSport(cursor.getInt(7));
                dayEntries.add(dayEntry);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            cursor.close();

        }



            return dayEntries;

    }


    //add addEntryItem --------------------------------------------------------------------------------------------------
    public int addEntryItem(EntryItem entryItem){
        //etwas der db hinzufuegen >> writableDB
        SQLiteDatabase db = this.getWritableDatabase();

        //hilfsklasse um daten in db zu schieben
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_LABEL, entryItem.getLabel());
        contentValues.put(KEY_CALORIES, entryItem.getCalories() );
        contentValues.put(KEY_FK_ENTRYITEM_DAYENTRY, entryItem.getDayEntryId_FK() );
        //contentValues.put(KEY_DATE, entryItem.getDate() );

        //in db schreiben
        int val = (int) db.insert(TABLE_ENTRYITEM,null, contentValues);
        //Log.d(TAG, "addEntryItem: " + val);
        return val;

    }

    //update EntryItem
    public int updateEntryItem(EntryItem entryItem){
        SQLiteDatabase db = this.getWritableDatabase();

        //hilfsklasse um daten in db zu schieben
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_LABEL, entryItem.getLabel());
        contentValues.put(KEY_CALORIES, entryItem.getCalories());
        contentValues.put(KEY_FK_ENTRYITEM_DAYENTRY, entryItem.getDayEntryId_FK() );
        //contentValues.put(KEY_DATE, entryItem.getDate() );


        //update the row
        //update ENTRYITEM values (label, calories, date) where id = 21
        int val = db.update(TABLE_ENTRYITEM, contentValues, KEY_ID + "=?", new String[]{String.valueOf(entryItem.getId())});
       // Log.d(TAG, "updateEntryItem: " + val);
        return val;
    }

    //delete EntryItem
    public int deleteEntryItem(int entryItemId){
        SQLiteDatabase db = this.getWritableDatabase();

        int val = -1;
        try {
             val = db.delete(TABLE_ENTRYITEM, KEY_ID + "=?", new String[]{String.valueOf(entryItemId)});

        }catch (Exception e){
            e.printStackTrace();
        }

       // Log.d(TAG, "delteEntryItem: " + val);
        return val;
    }

    //get/read ENTRYITEM
    public EntryItem getEntryItemById(int entryItemId){
        SQLiteDatabase db = this.getReadableDatabase();

        //cursors to iterate over the table
        //SELECT id, label, calories, fk_dayentryId from ENTRYITEM where id = entryItemId
        Cursor cursor = db.query(TABLE_ENTRYITEM, new String[]{KEY_ID, KEY_LABEL,KEY_CALORIES,
                //        KEY_DATE
                KEY_FK_ENTRYITEM_DAYENTRY
                },
                KEY_ID + "=?",
                new String[]{String.valueOf(entryItemId)},
                null, null, null);

        if(cursor != null){
            cursor.moveToFirst();

            EntryItem entryItem = new EntryItem();
            entryItem.setId(cursor.getInt(0));
            entryItem.setLabel(cursor.getString(1));
            entryItem.setCalories(cursor.getInt(2));
            entryItem.setDayEntryId_FK(cursor.getInt(3));



            cursor.close();
            return entryItem;
        }else {
            //cursor ist hier null also kein close aufruf !
            return null;
        }
    }



    //get ENTRYITEMs
    public ArrayList<EntryItem> getAllEntryItemsByDayEntryId(int dayEntryId){
        ArrayList<EntryItem> entryItemsAL = new ArrayList<>();

        Cursor cursor = this.getReadableDatabase().query(TABLE_ENTRYITEM, new String[]{KEY_ID, KEY_LABEL, KEY_CALORIES, KEY_FK_ENTRYITEM_DAYENTRY},
                KEY_FK_ENTRYITEM_DAYENTRY + "=?",new String[]{String.valueOf(dayEntryId)}, null, null, null);

        try{
            while (cursor.moveToNext()){
                EntryItem entryItem = new EntryItem();
                entryItem.setId(cursor.getInt(0));
                entryItem.setLabel(cursor.getString(1));
                entryItem.setCalories(cursor.getInt(2));
                entryItem.setDayEntryId_FK(cursor.getInt(3));
                entryItemsAL.add(entryItem);
            }

           // Log.d(TAG, "" + entryItemsAL.size());
        }catch (Exception e){
               e.printStackTrace();
        }finally {
               cursor.close();
        }

        return entryItemsAL;
    }


    /*
    //get ALL ENTRYITEMs
    public ArrayList<EntryItem> getAllEntryItems(){
        ArrayList<EntryItem> entryItemsAL = new ArrayList<>();

        Cursor cursor = this.getReadableDatabase().query(TABLE_ENTRYITEM, new String[]{KEY_ID, KEY_LABEL, KEY_CALORIES, KEY_FK_ENTRYITEM_DAYENTRY},
               null ,null, null, null, null);

        try{

            while (cursor.moveToNext()){
                EntryItem entryItem = new EntryItem();
                entryItem.setId(cursor.getInt(0));
                entryItem.setLabel(cursor.getString(1));
                entryItem.setCalories(cursor.getInt(2));
                entryItem.setDayEntryId_FK(cursor.getInt(3));
                entryItemsAL.add(entryItem);
            }


            Log.d(TAG, "" + entryItemsAL.size());
            return entryItemsAL;
        }catch (Exception e){
            e.printStackTrace();
            //cursor ist hier null also kein close aufruf !

        }finally {
            cursor.close();
        }

        return entryItemsAL;

    }

     */


    /*
    //get ENTRYITEMs
    public ArrayList<EntryItem> getAllEntryItemsFromThatDay(String date){
        ArrayList<EntryItem> entryItemsAL = new ArrayList<>();

        Cursor cursor = this.getReadableDatabase().query(TABLE_ENTRYITEM, new String[]{KEY_ID, KEY_LABEL, KEY_CALORIES, KEY_DATE},
                KEY_DATE + "=?",new String[]{date}, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();

            EntryItem entryItem = new EntryItem(cursor.getString(1), cursor.getInt(2), cursor.getString(3));
            entryItem.setId(cursor.getInt(0));
            entryItemsAL.add(entryItem);

            while (cursor.moveToNext()){
                entryItem = new EntryItem(cursor.getString(1), cursor.getInt(2), cursor.getString(3));
                entryItem.setId(cursor.getInt(0));
                entryItemsAL.add(entryItem);
            }

            cursor.close();
            Log.d("DatabaseHelper", "" + entryItemsAL.size());
            return entryItemsAL;
        }else {
            //cursor ist hier null also kein close aufruf !
            return null;
        }

    }

     */

    //---------------------------------------------------------------------------------------------------------------------------
    //add addSportEntryItem
    public int addSportEntryItem(SportEntryItem sportEntryItem){
        //etwas der db hinzufuegen >> writableDB
        SQLiteDatabase db = this.getWritableDatabase();

        //hilfsklasse um daten in db zu schieben
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_LABEL, sportEntryItem.getLabel());
        contentValues.put(KEY_CALORIES, sportEntryItem.getCalories() );
        contentValues.put(KEY_FK_SPORTENTRYITEM_DAYENTRY, sportEntryItem.getDayEntryId() );


        //in db schreiben
        int val = (int) db.insert(TABLE_SPORTENTRYITEM,null, contentValues);
        //Log.d(TAG, "addSportEntryItem: " + val);
        return val;

    }

    //update SportEntryItem
    public int updateSportEntryItem(SportEntryItem sportEntryItem){
        SQLiteDatabase db = this.getWritableDatabase();

        //hilfsklasse um daten in db zu schieben
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_LABEL, sportEntryItem.getLabel());
        contentValues.put(KEY_CALORIES, sportEntryItem.getCalories());
        contentValues.put(KEY_FK_SPORTENTRYITEM_DAYENTRY, sportEntryItem.getDayEntryId() );


        //update the row
        int val = db.update(TABLE_SPORTENTRYITEM, contentValues, KEY_ID + "=?", new String[]{String.valueOf(sportEntryItem.getId())});
       // Log.d(TAG, "updateSportEntryItem: " + val);
        return val;
    }

    //delete SportEntryItem
    public int deleteSportEntryItem(int sportEntryItemId){
        SQLiteDatabase db = this.getWritableDatabase();

        int val = 1;
        try {
            val = db.delete(TABLE_SPORTENTRYITEM, KEY_ID + "=?", new String[]{String.valueOf(sportEntryItemId)});
        }catch (Exception e){
            e.printStackTrace();
        }
        //Log.d(TAG, "deleteSportEntryItemID: " + val);
        return val;
    }

    //get/read SPORTENTRYITEM
    public SportEntryItem getSportEntryItemById(int sportEntryItemId){
        SQLiteDatabase db = this.getReadableDatabase();

        //cursors to iterate over the table
        Cursor cursor = db.query(TABLE_SPORTENTRYITEM, new String[]{KEY_ID, KEY_LABEL,KEY_CALORIES,
                        //        KEY_DATE
                        KEY_FK_SPORTENTRYITEM_DAYENTRY
                },
                KEY_ID + "=?",
                new String[]{String.valueOf(sportEntryItemId)},
                null, null, null);

        if(cursor != null){
            cursor.moveToFirst();

            SportEntryItem sportEntryItem = new SportEntryItem();
            sportEntryItem.setId(cursor.getInt(0));
            sportEntryItem.setLabel(cursor.getString(1));
            sportEntryItem.setCalories(cursor.getInt(2));
            sportEntryItem.setDayEntryId(cursor.getInt(3));



            cursor.close();
            return sportEntryItem;
        }else {
            //cursor ist hier null also kein close aufruf !
            return null;
        }
    }



    //get SPORTENTRYITEMs
    public ArrayList<SportEntryItem> getAllSportEntryItemsByDayEntryId(int dayEntryId){
        ArrayList<SportEntryItem> sportEntryItemsAL = new ArrayList<>();

        Cursor cursor = this.getReadableDatabase().query(TABLE_SPORTENTRYITEM, new String[]{KEY_ID, KEY_LABEL, KEY_CALORIES, KEY_FK_SPORTENTRYITEM_DAYENTRY},
                KEY_FK_SPORTENTRYITEM_DAYENTRY + "=?",new String[]{String.valueOf(dayEntryId)}, null, null, null);

        try{
            while (cursor.moveToNext()){
                SportEntryItem sportEntryItem = new SportEntryItem();
                sportEntryItem.setId(cursor.getInt(0));
                sportEntryItem.setLabel(cursor.getString(1));
                sportEntryItem.setCalories(cursor.getInt(2));
                sportEntryItem.setDayEntryId(cursor.getInt(3));
                sportEntryItemsAL.add(sportEntryItem);
            }
            //Log.d(TAG, "sportEntryItemsAL size: " + sportEntryItemsAL.size());
        }catch (Exception e){
           e.printStackTrace();
        }finally {
            cursor.close();
        }

        return sportEntryItemsAL;
    }

/*
    //get ALL SPORTENTRYITEMs
    public ArrayList<SportEntryItem> getAllSportEntryItems(){
        ArrayList<SportEntryItem> sportEntryItemsAL = new ArrayList<>();

        Cursor cursor = this.getReadableDatabase().query(TABLE_SPORTENTRYITEM, new String[]{KEY_ID, KEY_LABEL, KEY_CALORIES, KEY_FK_SPORTENTRYITEM_DAYENTRY},
                null,null, null, null, null);


        try{

            while (cursor.moveToNext()){
                SportEntryItem sportEntryItem = new SportEntryItem();
                sportEntryItem.setId(cursor.getInt(0));
                sportEntryItem.setLabel(cursor.getString(1));
                sportEntryItem.setCalories(cursor.getInt(2));
                sportEntryItem.setDayEntryId(cursor.getInt(3));
                sportEntryItemsAL.add(sportEntryItem);
            }


            Log.d(TAG, "sportEntryItemsAL size: " + sportEntryItemsAL.size());
            return sportEntryItemsAL;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cursor.close();
        }

        return sportEntryItemsAL;

    }

 */


    //add PERSONALDATA----------------------------------------------------------------------------------------------------------
    public int addPersonalData(PersonalData personalData){
        //etwas der db hinzufuegen >> writableDB
        SQLiteDatabase db = this.getWritableDatabase();

        //hilfsklasse um daten in db zu schieben
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_GENDER, personalData.getGender());
        contentValues.put(KEY_BODYSIZE, personalData.getBodySize());
        contentValues.put(KEY_FIRSTIMAGE, personalData.getFirstImage());
        contentValues.put(KEY_PALACTIVITY, personalData.getPalActivity());
        contentValues.put(KEY_WEIGHT, personalData.getWeight());
        contentValues.put(KEY_AGE, personalData.getAge());
        contentValues.put(KEY_DATE, personalData.getDate() );

        //in db schreiben
        return (int) db.insert(TABLE_PERSONALDATA,null, contentValues);


    }

    //update PERSONALDATA
    public int updatePersonalData(PersonalData personalData){
        SQLiteDatabase db = this.getWritableDatabase();

        //hilfsklasse um daten in db zu schieben
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_GENDER, personalData.getGender());
        contentValues.put(KEY_BODYSIZE, personalData.getBodySize());
        contentValues.put(KEY_FIRSTIMAGE, personalData.getFirstImage());
        contentValues.put(KEY_PALACTIVITY, personalData.getPalActivity());
        contentValues.put(KEY_WEIGHT, personalData.getWeight());
        contentValues.put(KEY_AGE, personalData.getAge());
        contentValues.put(KEY_DATE, personalData.getDate() );


        //update the row
        //update PERSONALDATA values (gender, bodysize, weight, age, palactivity, firstimage, date) where id = 21
        return db.update(TABLE_PERSONALDATA, contentValues, KEY_ID + "=?", new String[]{String.valueOf(personalData.getId())});
    }

    //get/read PERSONALDATA
    public PersonalData getPersonalData(int personalDataId){
        SQLiteDatabase db = this.getReadableDatabase();

        //cursors to iterate over the table
        //SELECT id, gender, bodysize, weight, age, palactivity, firstiamge from PERSONALDATA where id = personalDataId
        Cursor cursor = db.query(TABLE_PERSONALDATA, new String[]{KEY_ID, KEY_GENDER,KEY_BODYSIZE, KEY_WEIGHT, KEY_AGE, KEY_PALACTIVITY, KEY_FIRSTIMAGE, KEY_DATE},
                KEY_ID + "=?",
                new String[]{String.valueOf(personalDataId)},
                null, null, null);

        if(cursor != null){
            cursor.moveToFirst();

            PersonalData personalData = new PersonalData();
            personalData.setId(cursor.getInt(0));
            personalData.setGender(cursor.getString(1));
            personalData.setBodySize(cursor.getInt(2));
            personalData.setWeight(cursor.getInt(3));
            personalData.setAge(cursor.getInt(4));
            personalData.setPalActivity(cursor.getFloat(5));
            personalData.setFirstImage(cursor.getBlob(6));

            //String pattern = "MM/dd/yyyy HH:mm:ss";
            //DateFormat df = new SimpleDateFormat(pattern);
            //String today = cursor.getString(7);
            //String todayAsString = df.format(today)
            personalData.setDate(cursor.getString(7));

            cursor.close();
            return personalData;
        }else {
            //cursor ist hier null also kein close aufruf !
            return null;
        }
    }
}

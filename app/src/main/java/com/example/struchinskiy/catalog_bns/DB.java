package com.example.struchinskiy.catalog_bns;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DB {
    private static final String DB_NAME = "data.sqlite";
    private static final int DB_VERSION = 1;
    private static String DB_PATH;
    //private static String DB_NAME = "data.sqlite";

    //public static final String COLUMN_ID = "_id";
    //public static final String COLUMN_IMG = "img";
    //public static final String COLUMN_TXT = "txt";

    //private static final String DB_CREATE =
    //        "create table " + DB_TABLE + "(" +
    //                COLUMN_ID + " integer primary key autoincrement, " +
    //                COLUMN_TXT + " text" +
    //                ");";

    private final Context mCtx;


    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    DB(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() throws IOException {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDBHelper.createDataBase();
        mDB = mDBHelper.openDataBase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper != null)
            mDBHelper.close();
    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData(String search) {
      //  return mDB.query(DB_TABLE, null, null, null, null, null, null);
        return mDB.rawQuery(search, null);
    }

    // класс по созданию и управлению БД
    private static class DBHelper extends SQLiteOpenHelper {
        private SQLiteDatabase myDataBase;
        private final Context myContext;

        DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                 int version) {
            super(context, name, factory, version);
            myContext = context;
            DB_PATH = myContext.getDatabasePath(DB_NAME).getAbsolutePath();
        }


        void createDataBase() throws IOException {
          //  boolean dbExist = checkDataBase();
            File dbFile = myContext.getDatabasePath(DB_NAME);
          //  return dbFile.exists();

             if (!dbFile.exists()) {
                //By calling this method and empty database will be created into the default system path
                //of your application so we are gonna be able to overwrite that database with our database.
                 this.getReadableDatabase();
                 this.close();

                try {
                    copyDataBase();
                } catch (IOException e) {
                    throw new Error("Error copying database");
                }
            }
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            //db.disableWriteAheadLogging();
        }

        /* private boolean checkDataBase() {
            SQLiteDatabase checkDB = null;
           // DB_PATH = myContext.getDatabasePath(DB_NAME).getPath();

            try {
               // String myPath = DB_PATH + DB_NAME;
                checkDB = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);

            } catch(SQLiteException e){
                //database does't exist yet.
            }

            if (checkDB != null){
                checkDB.close();
            }
            return checkDB != null;
        }*/

        private void copyDataBase() throws IOException{
           // InputStream myInput = myContext.getAssets().open(DB_NAME);
            InputStream myInput = myContext.getResources().openRawResource(R.raw.data);
            OutputStream myOutput = new FileOutputStream(DB_PATH);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer))>0){
                myOutput.write(buffer, 0, length);
            }

            myOutput.flush();
            myOutput.close();
            myInput.close();
        }

        SQLiteDatabase openDataBase() throws SQLException {
            myDataBase = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
            //myDataBase.execSQL("PRAGMA case_sensitive_like = false");
            return myDataBase;
        }

        @Override
        public synchronized void close() {
            if(myDataBase != null)
                myDataBase.close();
            super.close();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}

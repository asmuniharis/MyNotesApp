package com.smartdev.mynotesapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.smartdev.mynotesapp.db.DatabaseContract.NoteColumns;

import androidx.annotation.Nullable;

import static com.smartdev.mynotesapp.db.DatabaseContract.TABLE_NAME;
/*Kelas ini untuk keperluan DDL*/
public class DatabaseHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "dbnoteapp";
    private static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_TABLE_NOTE = String.format("CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL)",
            TABLE_NAME,
            NoteColumns._ID,
            NoteColumns.TITLE,
            NoteColumns.DESCRIPTION,
            NoteColumns.DATE
    );

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_NOTE);
    }

    /*
   Method onUpgrade akan di panggil ketika terjadi perbedaan versi
   Gunakan method onUpgrade untuk melakukan proses migrasi data
    */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 /*
        Drop table tidak dianjurkan ketika proses migrasi terjadi dikarenakan data user akan hilang,
        */
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
/*
* Tanggung jawab utama dari kelas di atas adalah menciptakan database dengan tabel yang dibutuhkan dan handle ketika terjadi perubahan skema pada tabel (terjadi pada metode onUpgrade()).
Nah, di kelas ini kita menggunakan variabel yang ada pada DatabaseContract untuk mengisi kolom nama tabel. Begitu juga dengan kelas-kelas lainnya nanti. Dengan memanfaatkan kelas contract,
* maka akses nama tabel dan nama kolom tabel menjadi lebih mudah.
* */

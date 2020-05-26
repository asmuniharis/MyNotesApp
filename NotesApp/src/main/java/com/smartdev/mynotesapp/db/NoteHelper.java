package com.smartdev.mynotesapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;
import static com.smartdev.mynotesapp.db.DatabaseContract.TABLE_NAME;

/*Kelas ini untuk DML*/
public class NoteHelper {
    private static final String DATABASE_TABLE = TABLE_NAME;
    private static DatabaseHelper dataBaseHelper;
    private static NoteHelper INSTANCE;
    private static SQLiteDatabase database;

    public NoteHelper(Context context) {
        dataBaseHelper = new DatabaseHelper(context);
    }

    /*untuk menginisiasi database*/
    public static NoteHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NoteHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    /*untuk membuka dan menutup koneksi ke database*/
    public void open() throws SQLException {
        database = dataBaseHelper.getWritableDatabase();
    }
    public void close() {
        dataBaseHelper.close();
        if (database.isOpen())
            database.close();
    }

    /**
     * Ambil data dari semua note yang ada di dalam database
     *
     * @return cursor hasil queryAll
     */
    //crud: mengambildata
    public Cursor queryAll() {
        return database.query(
                DATABASE_TABLE,
                null,
                null,
                null,
                null,
                null,
                _ID + " ASC");
    }

    /**
     * Ambil data dari note berdasarakan parameter id
     *
     * @param id id note yang dicari
     * @return cursor hasil queryAll
     */
    //crud: mengambildata
    public Cursor queryById(String id) {
        return database.query(
                DATABASE_TABLE,
                null,
                _ID + " = ?",
                new String[]{id},
                null,
                null,
                null,
                null);
    }

    /**
     * Simpan data ke dalam database
     *
     * @param values nilai data yang akan di simpan
     * @return long id dari data yang baru saja di masukkan
     */
    //crud: menyimpan data
    public long insert(ContentValues values) {
        return database.insert(DATABASE_TABLE, null, values);
    }

    /**
     * Update data dalam database
     *
     * @param id     data dengan id berapa yang akan di update
     * @param values nilai data baru
     * @return int jumlah data yang ter-update
     */
    //crud: mengupdate data
    public int update(String id, ContentValues values) {
        return database.update(DATABASE_TABLE, values, _ID + " = ?", new String[]{id});
    }

    /**
     * Delete data dalam database
     *
     * @param id data dengan id berapa yang akan di delete
     * @return int jumlah data yang ter-delete
     */
    //crud: menghapus data
    public int deleteById(String id) {
        return database.delete(DATABASE_TABLE, _ID + " = ?", new String[]{id});
    }
}

package com.smartdev.mynotesapp.db;

import android.provider.BaseColumns;

public class DatabaseContract {
    public static String TABLE_NAME = "note";

    public static final class NoteColumns implements BaseColumns {

        //Note title
        public static final String TITLE = "title";
        //Note description
        public static final String DESCRIPTION = "description";
        //Note date
        public static final String DATE = "date";
    }
}

/*
* Jika Anda perhatikan di dalam koe Java, tidak ada kolom id di dalam kelas contract. Alasannya, kolom id sudah ada secara otomatis di dalam kelas BaseColumns.
* Pada kode kelas dbhelper selanjutnya perhatikan bagaimana pemanggilan id dengan menggunakan identifier _ID.
 * */

package com.smartdev.mynotesapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.smartdev.mynotesapp.adapter.NoteAdapter;
import com.smartdev.mynotesapp.db.NoteHelper;
import com.smartdev.mynotesapp.entity.Note;
import com.smartdev.mynotesapp.helper.MappingHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/*
 * Tugas utama MainActivity ada dua. Pertama, menampilkan data dari database pada tabel Note secara ascending.
 * Kedua, menerima nilai balik dari setiap aksi dan proses yang dilakukan di NoteAddUpdateActivity.
 * */
public class MainActivity extends AppCompatActivity implements LoadNotesCallback {
    private ProgressBar progressBar;
    private RecyclerView rvNotes;
    private NoteAdapter adapter;

    private FloatingActionButton fabAdd;
    private NoteHelper noteHelper;

    private static final String EXTRA_STATE = "EXTRA_STATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Notes");

        progressBar = findViewById(R.id.progressbar);
        rvNotes = findViewById(R.id.rv_notes);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        rvNotes.setHasFixedSize(true);
        adapter = new NoteAdapter(this);
        rvNotes.setAdapter(adapter);

        fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NoteAddUpdateActivity.class);
                startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_ADD);
            }
        });

        /*
         * mengambil data dari database dengan memanfaatkan NoteHelper yang sudah kita buat
         * 1. inisialisai
         * 2. panggil open()
         * */
        //Aturan utama dalam penggunaan dan akses database SQLite adalah membuat instance dan membuka koneksi pada metode onCreate():
        noteHelper = NoteHelper.getInstance(getApplicationContext());
        noteHelper.open();

        // proses ambil data
        new LoadNotesAsync(noteHelper, this).execute();

        /*
        Cek jika savedInstaceState null makan akan melakukan proses asynctask nya
        jika tidak,akan mengambil arraylist nya dari yang sudah di simpan
         */
        //supaya data tidak hilang saat rotasi layar
        if (savedInstanceState == null) {
            // proses ambil data
            new LoadNotesAsync(noteHelper, this).execute();
        } else {
            ArrayList<Note> list = savedInstanceState.getParcelableArrayList(EXTRA_STATE);
            if (list != null) {
                adapter.setListNotes(list);
            }
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_STATE, adapter.getListNotes());
    }

    @Override
    public void preExecute() {
         /*
        Callback yang akan dipanggil di onPreExecute Asyntask
        Memunculkan progressbar
        */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void postExecute(ArrayList<Note> notes) {
         /*
        Callback yang akan dipanggil di onPostExture Asynctask
        Menyembunyikan progressbar, kemudian isi adapter dengan data yang ada
         */
        progressBar.setVisibility(View.INVISIBLE);
        if (notes.size() > 0) {
            adapter.setListNotes(notes);
        } else {
            adapter.setListNotes(new ArrayList<Note>());
            showSnackbarMessage("Tidak ada data saat ini");
        }
    }

    /*
    * Fungsi ini digunakan untuk load data dari tabel dan dan kemudian menampilkannya ke dalam list
    * secara asynchronous dengan menggunakan Background process
     * */
    private static class LoadNotesAsync extends AsyncTask<Void, Void, ArrayList<Note>> {
        private final WeakReference<NoteHelper> weakNoteHelper;
        private final WeakReference<LoadNotesCallback> weakCallback;

        public LoadNotesAsync(NoteHelper noteHelper, LoadNotesCallback callback) {
            weakNoteHelper = new WeakReference<>(noteHelper);
            weakCallback = new WeakReference<>(callback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }

        @Override
        protected ArrayList<Note> doInBackground(Void... voids) {
            Cursor dataCursor = weakNoteHelper.get().queryAll();
            return MappingHelper.mapCursorToArrayList(dataCursor);
        }

        @Override
        protected void onPostExecute(ArrayList<Note> notes) {
            super.onPostExecute(notes);
            weakCallback.get().postExecute(notes);
        }
    }

    /*
    * Setiap aksi yang dilakukan pada NoteAddUpdateActivity
    *  akan berdampak pada MainActivity baik itu untuk
    *  penambahan, pembaharuan atau penghapusan.
    * Metode onActivityResult() akan melakukan penerimaan data
    *  dari intent yang dikirimkan dan diseleksi berdasarkan
    *  jenis requestCode dan resultCode-nya.
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            // Akan dipanggil jika request codenya ADD
            if (requestCode == NoteAddUpdateActivity.REQUEST_ADD) {
                if (resultCode == NoteAddUpdateActivity.RESULT_ADD) {
                    Note note = data.getParcelableExtra(NoteAddUpdateActivity.EXTRA_NOTE);

                    adapter.addItem(note);
                    rvNotes.smoothScrollToPosition(adapter.getItemCount() - 1);

                    showSnackbarMessage("Satu item berhasil ditambahkan");
                }
            }
            /*
            * Baris di atas akan dijalankan ketika terjadi penambahan data pada NoteAddUpdateActivity.
            * Alhasil, ketika metode ini dijalankan maka kita akan membuat objek note baru
            * dan inisiasikan dengan getParcelableExtra.
            * Lalu panggil metode addItem yang berada di adapter
            * dengan memasukan objek note sebagai argumen.
            * Metode tersebut akan menjalankan notifyItemInserted
            * dan penambahan arraylist-nya.
            * Lalu objek rvNotes akan melakukan smoothscrolling,
            * dan terakhir muncul notifikasi pesan dengan
            * menggunakan Snackbar.

             * */


            // Update dan Delete memiliki request code sama akan tetapi result codenya berbeda
            else if (requestCode == NoteAddUpdateActivity.REQUEST_UPDATE) {
                /*
                Akan dipanggil jika result codenya  UPDATE
                Semua data di load kembali dari awal
                */
                if (resultCode == NoteAddUpdateActivity.RESULT_UPDATE) {
                    Note note = data.getParcelableExtra(NoteAddUpdateActivity.EXTRA_NOTE);
                    int position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0);
                    adapter.updateItem(position, note);
                    rvNotes.smoothScrollToPosition(position);
                    showSnackbarMessage("Satu item berhasil diubah");
                }
                /*
                * Baris di atas akan dijalankan ketika terjadi perubahan data pada NoteAddUpdateActivity. Prosesnya hampir sama seperti ketika ada penambahan data, tetapi di sini kita harus membuat objek baru yaitu position. Sebabnya, metode updateItem membutuhkan 2 argumen yaitu position dan Note.
                * */


                /*
                Akan dipanggil jika result codenya DELETE
                Delete akan menghapus data dari list berdasarkan dari position
                */
                else if (resultCode == NoteAddUpdateActivity.RESULT_DELETE) {
                    int position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0);
                    adapter.removeItem(position);
                    showSnackbarMessage("Satu item berhasil dihapus");
                }
                /*
                * Baris di atas akan dijalankan jika nilai resultCode-nya adalah RESULT_DELETE. Di sini kita hanya membutuhkan position karena metode removeItem hanya membutuhkan position untuk digunakan pada notifyItemRemoved dan penghapusan data pada arraylist-nya.
                * */
            }

        }
    }

    /**
     * Tampilkan snackbar
     *
     * @param message inputan message
     * */
    private void showSnackbarMessage(String message) {
        Snackbar.make(rvNotes, message, Snackbar.LENGTH_SHORT).show();
    }

    //menutup interaksi dg database saat aktivity ditutup
    //tutup koneksi pada metode onDestroy() (atau onStop()).
    @Override
    protected void onDestroy() {
        super.onDestroy();
        noteHelper.close();
    }
}


interface LoadNotesCallback {
    void preExecute();
    void postExecute(ArrayList<Note> notes);
}

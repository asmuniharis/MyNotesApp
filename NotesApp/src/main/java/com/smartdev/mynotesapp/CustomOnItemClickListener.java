package com.smartdev.mynotesapp;

import android.view.View;

public class CustomOnItemClickListener implements View.OnClickListener {
    private int position;
    private OnItemClickCallback onItemClickCallback;

    public CustomOnItemClickListener(int position, OnItemClickCallback onItemClickCallback) {
        this.position = position;
        this.onItemClickCallback = onItemClickCallback;
    }

    @Override
    public void onClick(View view) {
        onItemClickCallback.onItemClicked(view, position);
    }

    public interface OnItemClickCallback {
        void onItemClicked(View view, int position);
    }

}

/*
* Kelas di atas bertugas membuat item seperti CardView bisa diklik
* di dalam adapter. Caranya lakukan penyesuaian pada kelas event
* OnClickListener. Alhasil kita bisa mengimplementasikan interface
* listener yang baru bernama OnItemClickCallback.
* Kelas tersebut dibuat untuk menghindari nilai final dari posisi
* yang tentunya sangat tidak direkomendasikan.
* */


package com.garethevans.church.opensongtablet.songsandsetsmenu;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.garethevans.church.opensongtablet.R;

class SetItemViewHolder extends RecyclerView.ViewHolder {

    final TextView vItem;
    final TextView vSongTitle;
    final TextView vSongFolder;
    final RelativeLayout vCard;

    SetItemViewHolder(View v) {
        super(v);
        vCard = v.findViewById(R.id.cardview_layout);
        vItem = v.findViewById(R.id.cardview_item);
        vSongTitle = v.findViewById(R.id.cardview_songtitle);
        vSongFolder = v.findViewById(R.id.cardview_folder);
    }
}
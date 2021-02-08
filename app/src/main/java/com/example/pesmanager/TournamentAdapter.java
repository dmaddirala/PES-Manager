package com.example.pesmanager;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class TournamentAdapter extends ArrayAdapter<Tournament> {
    Context context;
    ArrayList<Tournament> matches;

    public TournamentAdapter(Activity context, ArrayList<Tournament> matches) {
        super(context, 0, matches);
        this.context = context;
        this.matches = matches;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_view_item, parent, false);
        }

        final Tournament currentTournament = getItem(position);

        final TextView matchName = (TextView) listItemView.findViewById(R.id.tv_tournament_name);
        final TextView serialNumber = (TextView) listItemView.findViewById(R.id.tv_serial_number);
        final TextView dateTv = (TextView) listItemView.findViewById(R.id.tv_date);
        final TextView timeTv = (TextView) listItemView.findViewById(R.id.tv_time);

        String tournamenthNameString = currentTournament.getName();
        String currentTime = currentTournament.getTime();
        String currentDate = currentTournament.getDate();

        dateTv.setText(currentDate);
        timeTv.setText("-"+currentTime);
        matchName.setText(tournamenthNameString);
        serialNumber.setText( (position+1) + ") " );

        return listItemView;
    }
}
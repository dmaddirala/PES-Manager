package com.example.pesmanager;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class PlayerAdapter extends ArrayAdapter<Player> {
    Context context;
    ArrayList<Player> players;

    public PlayerAdapter(Activity context, ArrayList<Player> players) {
        super(context, 0, players);
        this.context = context;
        this.players = players;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_view_player_item, parent, false);
        }

        final Player currentPlayer = getItem(position);

        final TextView playerName = (TextView) listItemView.findViewById(R.id.tv_player_name);
        final TextView playerValue = (TextView) listItemView.findViewById(R.id.tv_player_value);
        final TextView serialNumber = (TextView) listItemView.findViewById(R.id.tv_serial_number);

        NumberFormat formatter = new DecimalFormat("#,##,###");
        String formattedMoney = formatter.format(currentPlayer.getValue() );

        playerName.setText(""+currentPlayer.getName().toUpperCase());
        serialNumber.setText((position+1)+") ");
        Log.i("TAG4" , "Adapter Player Value: "+currentPlayer.getValue());
        playerValue.setText(formattedMoney+" Rs.");

        return listItemView;
    }
}

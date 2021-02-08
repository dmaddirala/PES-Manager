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

public class CaptainAdapter extends ArrayAdapter<Captain> {

    Context context;
    ArrayList<Captain> captains;

    public CaptainAdapter(Activity context, ArrayList<Captain> captains) {
        super(context, 0, captains);
        this.context = context;
        this.captains = captains;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_view_captain_item, parent, false);
        }

        final Captain currentCaptain = getItem(position);

        final TextView captainName = (TextView) listItemView.findViewById(R.id.tv_captain_name);
        final TextView serialNumber = (TextView) listItemView.findViewById(R.id.tv_serial_number);
        final TextView balanceMoney = (TextView) listItemView.findViewById(R.id.tv_balance_money);

        NumberFormat formatter = new DecimalFormat("#,##,###");
        String formattedMoney = formatter.format(currentCaptain.getBalanceMoney());

        captainName.setText(""+currentCaptain.getName().toUpperCase());
        serialNumber.setText((position+1)+") ");
        balanceMoney.setText(formattedMoney+" Rs Left");

        return listItemView;
    }
}

package com.example.pesmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class TournamentDetailsActivity extends AppCompatActivity {

    private ListView listView;
    private TextView tournamentNameTv;
    private CaptainAdapter adapter;
    private ArrayList<Captain> captains;
    private ArrayList<Tournament> tournaments;
    private int longPressedItemPosition;

    private int tournamentItemPosition;
    private Tournament currentTournament;

    private Dialog dialog,dialogDelete;
    private Button save, cancel, yes, no;
    private EditText captainName;
    private TextView dialogMessage;
    public static final String CAPTAIN_DELETE_MESSAGE = "Delete This Captain?";
    public static final String CLEAR_DATA_MESSAGE = "Clear All Data?";
    private static final String CAPTAIN_ITEM_POSITION = "CaptainItemPositin";
    private static final String TOURNAMENT_ITEM_POSITION = "TournamentItemPositin";

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TOURNAMENT_LIST = "tournamentList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_details);
        Bundle extras = getIntent().getExtras();
        currentTournament = (Tournament) getIntent().getSerializableExtra("CurrentTournament");

        tournamentItemPosition = extras.getInt("ItemPosition");
//        CAPTAIN_LIST = "tournamentList"+itemPosition;

        loadData();
        dialog = new Dialog(TournamentDetailsActivity.this);
        dialog.setContentView(R.layout.dialog_new_captain);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        dialogDelete = new Dialog(TournamentDetailsActivity.this);
        dialogDelete.setContentView(R.layout.dialog_delete);
        dialogDelete.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background));
        dialogDelete.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogDelete.setCancelable(true);
        dialogDelete.getWindow().getAttributes().windowAnimations = R.style.animation;


        dialogMessage = dialogDelete.findViewById(R.id.tv_message);
        listView = findViewById(R.id.list_view_captains);
        tournamentNameTv = findViewById(R.id.tv_tournament_name);
        save = dialog.findViewById(R.id.btn_save);
        cancel = dialog.findViewById(R.id.btn_cancel);
        captainName = dialog.findViewById(R.id.et_captain_name);
        yes = dialogDelete.findViewById(R.id.btn_yes);
        no = dialogDelete.findViewById(R.id.btn_no);

        tournamentNameTv.setText("Tournament-  "+extras.getString("TournamentName"));
        adapter = new CaptainAdapter(this, captains);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), CaptainDetailsActivity.class);
                intent.putExtra(CAPTAIN_ITEM_POSITION, position);
                intent.putExtra(TOURNAMENT_ITEM_POSITION, tournamentItemPosition);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                longPressedItemPosition = position;
                dialogMessage.setText(CAPTAIN_DELETE_MESSAGE);
                dialogDelete.show();
                return true;
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (    (captainName.getText().toString().isEmpty()) ) {
                    hideKeyboard();
                    Toast.makeText(TournamentDetailsActivity.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                String name = captainName.getText().toString();
                captains.add(new Captain(name, tournaments.get(tournamentItemPosition).getStartingMoney()) );
                tournaments.get(tournamentItemPosition).setCaptains(captains);
                saveData();
                refresh();
                captainName.setText("");
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogMessage.getText().equals(CLEAR_DATA_MESSAGE)){
                    clearData();
                    Toast.makeText(getApplicationContext(), "Data Cleared", Toast.LENGTH_SHORT).show();
                } else{
                    captains.remove(longPressedItemPosition);
                    Toast.makeText(getApplicationContext(), "Captain Removed", Toast.LENGTH_SHORT).show();
                }

                saveData();
                refresh();
                dialogDelete.dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDelete.dismiss();

            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tournament_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            /*
             * When you click the reset menu item, we want to start all over
             * and display the pretty gradient again. There are a few similar
             * ways of doing this, with this one being the simplest of those
             * ways. (in our humble opinion)
             */
            case R.id.action_clear_captains:
                // COMPLETED (14) Pass in this as the ListItemClickListener to the GreenAdapter constructor

                dialogMessage.setText(CLEAR_DATA_MESSAGE);
                dialogDelete.show();
                clearData();

                return true;

            case R.id.action_new_captain:
                dialog.show();


        }

        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        loadData();
        listView.setAdapter(new CaptainAdapter(this, captains));
    }

    private void clearData(){
        captains = new ArrayList<Captain>();
        saveData();
        refresh();

        Toast.makeText(this, "All Data Cleared", Toast.LENGTH_SHORT).show();
    }
    //
    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        String json = gson.toJson(tournaments);
        editor.putString(TOURNAMENT_LIST, json);
        editor.apply();
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
//        String json = sharedPreferences.getString(CAPTAIN_LIST, null);
        String jsonTournaments = sharedPreferences.getString(TOURNAMENT_LIST, null);

        Type matchType = new TypeToken<ArrayList<Captain>>() {}.getType();
        Type tournamentType = new TypeToken<ArrayList<Tournament>>() {}.getType();

        tournaments = gson.fromJson(jsonTournaments, tournamentType);
//        captains = gson.fromJson(json, matchType);

        if (tournaments.get(tournamentItemPosition).getCaptains()==null){
            captains = new ArrayList<Captain>();
        }else{
            captains = tournaments.get(tournamentItemPosition).getCaptains();
        }

    }
    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(captainName.getWindowToken(), 0);
    }
}
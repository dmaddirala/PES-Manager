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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private ListView listView;
    private TournamentAdapter adapter;
    private ArrayList<Tournament> tournaments;
    private int longPressedItemPosition;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TOURNAMENT_LIST = "tournamentList";
    public static final String TOURNAMENT_DELETE_MESSAGE = "Delete This Tournament?";
    public static final String CLEAR_DATA_MESSAGE = "Clear All Data?";


    private Dialog dialogNewTournament,dialogDelete;
    private Button save, cancel, yes, no;
    private EditText captainName,startingMoney;
    private TextView dialogMessage;

    private String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    private String currentTime = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadData();

        dialogNewTournament = new Dialog(MainActivity.this);
        dialogNewTournament.setContentView(R.layout.dialog_new_tournament);
        dialogNewTournament.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background));
        dialogNewTournament.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogNewTournament.setCancelable(true);
        dialogNewTournament.getWindow().getAttributes().windowAnimations = R.style.animation;

        dialogDelete = new Dialog(MainActivity.this);
        dialogDelete.setContentView(R.layout.dialog_delete);
        dialogDelete.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background));
        dialogDelete.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogDelete.setCancelable(true);
        dialogDelete.getWindow().getAttributes().windowAnimations = R.style.animation;

        dialogMessage = dialogDelete.findViewById(R.id.tv_message);
        save = dialogNewTournament.findViewById(R.id.btn_save);
        cancel = dialogNewTournament.findViewById(R.id.btn_cancel);
        yes = dialogDelete.findViewById(R.id.btn_yes);
        no = dialogDelete.findViewById(R.id.btn_no);
        captainName = dialogNewTournament.findViewById(R.id.et_tournament_name);
        startingMoney = dialogNewTournament.findViewById(R.id.et_starting_money);
        listView = findViewById(R.id.list_view_tournaments);

        adapter = new TournamentAdapter(this, tournaments);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), TournamentDetailsActivity.class);
                intent.putExtra("ItemPosition", position);
                intent.putExtra("TournamentName",tournaments.get(position).getName());
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                longPressedItemPosition = position;
                dialogMessage.setText(TOURNAMENT_DELETE_MESSAGE);
                dialogDelete.show();
                return true;
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (    (captainName.getText().toString().isEmpty()) ||
                        (startingMoney.getText().toString().isEmpty())) {
                    hideKeyboard();
                    Toast.makeText(MainActivity.this, "Please Enter all the Details", Toast.LENGTH_SHORT).show();
                    return;
                }
                float money;
                try {
                    money = Float.parseFloat(startingMoney.getText().toString());
                } catch (Exception e) {
                    hideKeyboard();
                    startingMoney.setText("");
                    Toast.makeText(MainActivity.this, "Enter Valid Player Value", Toast.LENGTH_SHORT).show();
                    return;
                }
                String name = captainName.getText().toString();
                Toast.makeText(MainActivity.this, "Captain "+name+" Saved", Toast.LENGTH_SHORT).show();

                Tournament newTournament = new Tournament(name);
                newTournament.setStartingMoney(money);
                newTournament.setDate(currentDate);
                newTournament.setTime(currentTime);
                tournaments.add( newTournament );

                captainName.setText("");
                startingMoney.setText("");

                saveData();
                refresh();
                dialogNewTournament.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                dialogNewTournament.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Tournament Removed", Toast.LENGTH_SHORT).show();

                if(dialogMessage.getText().equals(CLEAR_DATA_MESSAGE)){
                    clearData();
                    Toast.makeText(getApplicationContext(), "Data Cleared", Toast.LENGTH_SHORT).show();
                } else{
                    tournaments.remove(longPressedItemPosition);
                    Toast.makeText(getApplicationContext(), "Tournament Removed", Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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
            case R.id.action_clear:
                // COMPLETED (14) Pass in this as the ListItemClickListener to the GreenAdapter constructor
                dialogMessage.setText(CLEAR_DATA_MESSAGE);
                dialogDelete.show();
                return true;

            case R.id.action_new_match:
                dialogNewTournament.show();


        }

        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        listView.setAdapter(new TournamentAdapter(this, tournaments));
    }
//
    private void clearData(){
        tournaments = new ArrayList<Tournament>();
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
        String jsonTournament = sharedPreferences.getString(TOURNAMENT_LIST, null);

        Type tournamentType = new TypeToken<ArrayList<Tournament>>() {}.getType();

        tournaments = gson.fromJson(jsonTournament, tournamentType);

        if (tournaments==null){
            tournaments = new ArrayList<Tournament>();
        }

    }
    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(captainName.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(startingMoney.getWindowToken(), 0);
    }
}
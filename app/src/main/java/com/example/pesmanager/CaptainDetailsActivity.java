package com.example.pesmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class CaptainDetailsActivity extends AppCompatActivity {

    private Dialog dialogNewPlayer, dialogPlayerOptions;
    private Button save, cancel, tradePlayer, deletePlayer;
    private PlayerAdapter adapter;
    private ArrayList<Player> players;
    private ArrayList<Tournament> tournaments;
    private Captain currentCaptain;
    private RadioGroup playerCardGroup;
    private RadioButton playerCard;
    private int longPressedItemPosition;

    private ListView listView;
    private EditText playerName, playerValue;
    private TextView captainNameTv, balanceAmountTv;
    private int captainItemPosition, tournamentItemPosition;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TOURNAMENT_LIST = "tournamentList";
    private static final String CAPTAIN_ITEM_POSITION = "CaptainItemPositin";
    private static final String TOURNAMENT_ITEM_POSITION = "TournamentItemPositin";

    NumberFormat formatter = new DecimalFormat("#,##,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captain_details);
        Bundle extras = getIntent().getExtras();
        captainItemPosition = extras.getInt(CAPTAIN_ITEM_POSITION);
        tournamentItemPosition = extras.getInt(TOURNAMENT_ITEM_POSITION);

        loadData();

        dialogNewPlayer = new Dialog(CaptainDetailsActivity.this);
        dialogNewPlayer.setContentView(R.layout.dialog_new_player);
        dialogNewPlayer.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background));
        dialogNewPlayer.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogNewPlayer.setCancelable(true);
        dialogNewPlayer.getWindow().getAttributes().windowAnimations = R.style.animation;

        dialogPlayerOptions = new Dialog(CaptainDetailsActivity.this);
        dialogPlayerOptions.setContentView(R.layout.dialog_player_options);
        dialogPlayerOptions.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background));
        dialogPlayerOptions.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogPlayerOptions.setCancelable(true);
        dialogPlayerOptions.getWindow().getAttributes().windowAnimations = R.style.animation;

        save = dialogNewPlayer.findViewById(R.id.btn_save);
        cancel = dialogNewPlayer.findViewById(R.id.btn_cancel);
        playerName = dialogNewPlayer.findViewById(R.id.et_player_name);
        playerValue = dialogNewPlayer.findViewById(R.id.et_player_value);
        playerCardGroup = dialogNewPlayer.findViewById(R.id.radioGroup);

        tradePlayer = dialogPlayerOptions.findViewById(R.id.btn_trade_player);
        deletePlayer = dialogPlayerOptions.findViewById(R.id.btn_delete_player);

        captainNameTv = findViewById(R.id.tv_name_title);
        balanceAmountTv = findViewById(R.id.tv_captain_balance_money);
        listView = findViewById(R.id.list_view_players);

        String formattedMoney = formatter.format(currentCaptain.getBalanceMoney() );

        captainNameTv.setText(currentCaptain.getName().toUpperCase() + "'s TEAM");
        balanceAmountTv.setText(formattedMoney + " Rs. Left");


        adapter = new PlayerAdapter(this, players);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longPressedItemPosition = position;
                dialogPlayerOptions.show();
                return true;
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = playerCardGroup.getCheckedRadioButtonId();
                if ((selectedId == -1) ||
                        (playerValue.getText().toString().isEmpty()) ||
                        (playerName.getText().toString().isEmpty())) {
                    hideKeyboard();
                    Toast.makeText(CaptainDetailsActivity.this, "Please Enter all the Details", Toast.LENGTH_SHORT).show();
                    return;
                }
                float value;
                try {
                    value = Float.parseFloat(playerValue.getText().toString());
                } catch (Exception e) {
                    hideKeyboard();
                    Toast.makeText(CaptainDetailsActivity.this, "Enter Valid Player Value", Toast.LENGTH_SHORT).show();
                    return;
                }

                float balaceMoney = currentCaptain.getBalanceMoney() - value;
                if (balaceMoney < 0) {
                    Toast.makeText(CaptainDetailsActivity.this, "You need More " + (balaceMoney * (-1)) + "Rs. To add this Player", Toast.LENGTH_LONG).show();
                    dialogNewPlayer.dismiss();
                    return;
                }
                String name = getPlayerName(selectedId);
                currentCaptain.setBalanceMoney(balaceMoney);
                players.add(new Player(name, value));
                currentCaptain.setPlayers(players);
                saveData();
                refresh();
                playerName.setText("");
                playerValue.setText("");
                dialogNewPlayer.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerName.setText("");
                playerValue.setText("");
                dialogNewPlayer.dismiss();
            }
        });

        tradePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CaptainDetailsActivity.this, "This Option is not available yet", Toast.LENGTH_LONG).show();
                dialogPlayerOptions.dismiss();
            }
        });

        deletePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Player playerToBeRemoved = players.get(longPressedItemPosition);
                currentCaptain.setBalanceMoney(currentCaptain.getBalanceMoney()+playerToBeRemoved.getValue());
                players.remove(playerToBeRemoved);
                currentCaptain.setPlayers(players);
                saveData();
                refresh();
                Toast.makeText(CaptainDetailsActivity.this, "Player Deleted", Toast.LENGTH_SHORT).show();
                dialogPlayerOptions.dismiss();
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
        getMenuInflater().inflate(R.menu.menu_captain_details, menu);
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
            case R.id.action_new_player:
                dialogNewPlayer.show();


        }

        return super.onOptionsItemSelected(item);
    }

    private String getPlayerName(int selectedId) {
        playerCard = (RadioButton) dialogNewPlayer.findViewById(selectedId);
        String playerCardName = playerCard.getText().toString();
        String name;

        if (playerCardName.equals("Base Card")) {
            name = playerName.getText().toString();
        } else if (playerCardName.equals("Featured Card")) {
            name = "FT." + playerName.getText().toString();
        } else {
            name = "IM." + playerName.getText().toString();
        }
        return name;
    }

    private void refresh() {
        loadData();
        String formattedMoney = formatter.format(currentCaptain.getBalanceMoney() );
        balanceAmountTv.setText(formattedMoney + " Rs. Left");
        listView.setAdapter(new PlayerAdapter(this, players));
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        String json = gson.toJson(tournaments);
        editor.putString(TOURNAMENT_LIST, json);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonTournaments = sharedPreferences.getString(TOURNAMENT_LIST, null);
        Type tournamentType = new TypeToken<ArrayList<Tournament>>() {
        }.getType();

        tournaments = gson.fromJson(jsonTournaments, tournamentType);
        currentCaptain = tournaments.get(tournamentItemPosition).getCaptains().get(captainItemPosition);
        ArrayList<Player> playersTemp = currentCaptain.getPlayers();

        if (playersTemp == null) {
            players = new ArrayList<Player>();
        } else {
            players = playersTemp;
        }


    }

    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(playerName.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(playerValue.getWindowToken(), 0);
    }

}
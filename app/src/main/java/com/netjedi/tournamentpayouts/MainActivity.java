package com.netjedi.tournamentpayouts;

import android.content.Context;
import android.hardware.input.InputManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

// Purpose:
// This solves a simple problem for a poker game, calculating payouts
// Rounding payouts to nearest 10 units being paid out on fixed percentages
// A project to help learn android programming
//
// Here are the current percentages of the prize pool to be paid
// The last value is an approximation due to the rounding of the other places
// 3 places - {.5, .3, .2}
// 4 places - {.4, .28, .2, .12}
// 5 places - {.38, .25, .17, .12, .08}
// 6 places - {.32, .22, .165, .125, .09, .08}


// TODO:
// The rounding factor is currently hardcoded to 10 units and needs to be added as an option menu
// Use a database to store the player count and payout values.
// Need an db editor to allow altering of the values for payout and creation of new payout levels


public class MainActivity extends AppCompatActivity {

    static EditText etPool;
    static Spinner spPlayers;
    static Button btnCalculate;
    static ListView lvResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPool = findViewById(R.id.prize_pool);
        spPlayers = findViewById(R.id.players);
        btnCalculate = findViewById(R.id.calculate);
        lvResults = findViewById(R.id.lvResults);

        // Hide the keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // might change this out for a database later
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner, getResources().getStringArray(R.array.player_payouts));
        spPlayers.setAdapter(spinnerArrayAdapter);

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // could get id value and check for id 0
                int rnd_val = 10;
                String selection = spPlayers.getSelectedItem().toString();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if(etPool.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Please enter value for the pool greater than 10", Toast.LENGTH_SHORT).show();
                } else if(selection.startsWith("Select")) {
                    Toast.makeText(getApplicationContext(),"Please select a value for the number of players", Toast.LENGTH_SHORT).show();
                } else {
                    int pool = Integer.parseInt(etPool.getText().toString());
                    double[] pay_rate = getPayoutValues(selection);
                    calculatePayout(pool,pay_rate,rnd_val);
                }
            }
        });
    }

    public void calculatePayout(int pool, double[] amounts, int rounder) {

        // This is used to verify the payouts and pool match
        int total = 0;

        // This is used to track the payout amounts
        int[] payouts = new int[amounts.length];

        // This is the adapter for the list view data
        ArrayAdapter<String> adapter;

        // loop through the payout amounts and calculate the payouts
        for(int i = 0; i < amounts.length; i++) {
            double tmp = pool * amounts[i];
            double rem = tmp % rounder;

            // check to see if we should round up or down
            if(rem > (rounder/2)) {
                tmp += (rounder-(tmp % rounder));
            } else {
                tmp -= (tmp % rounder);
            }

            // check to see if it is the last item
            if(i == amounts.length -1) {
                tmp = (pool - total);
            }

            // Add this to the list of payouts
            payouts[i] = (int) tmp;
            total += (int) tmp;
        }

        // Sanity check on the payout values
        if( total != pool) {
            Toast.makeText(getApplicationContext(), "Prize pool and total payouts do not match", Toast.LENGTH_SHORT).show();
        }

        // Adding the payout data for display
        adapter = new ArrayAdapter<String>(this, R.layout.layout, prepareOutput(payouts));
        lvResults.setAdapter(adapter);
    }

    public String[] prepareOutput(int[] values)
    {
        // Here is where we format int list of payouts into strings we want to show the user
        // The case is based on a zero index but the output is based on output from 1
        // The current design is only showing up to six places
        // create an array of strings to hold the formatted payout strings
        String[] results = new String[values.length];
        // loop through the amounts and create a string for each payout
        for(int i =0; i < values.length; i++)
        {
            switch(i){
                case 0:
                    results[i] = "1st : " + values[i];
                    break;
                case 1:
                    results[i] = "2nd : " + values[i];
                    break;
                case 2:
                    results[i] = "3rd : " + values[i];
                    break;
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    results[i] = (i+1) + "th \t: " + values[i];
                    break;
            }
        }

        // Return the payout strings to be displayed
        return(results);
    }

    // Converts the string of players and places to be paid to percentage of payouts
    // I have an idea on storing this information in a database and allowing the user to edit values
    // Then display the entries from the database in spinner, find the selected string and return values
    public static double[] getPayoutValues(String selected) {

        if(selected.startsWith("2 to 11")) {
            return(new double[] {.5, .3, .2});
        } else if(selected.startsWith("12 to 17")) {
            return(new double[] {.4, .28, .2, .12});
        } else if(selected.startsWith("18 to 26")) {
            return(new double[] {.38, .25, .17, .12, .08});
        } else if(selected.startsWith("27 to 40")) {
            return(new double[] {.32, .22, .165, .125, .09, .08});
        } else {
            return(null);
        }
    }
}

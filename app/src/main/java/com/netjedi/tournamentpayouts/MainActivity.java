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

        final ArrayAdapter<String> adapter;

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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.options, menu);
//        return true;
//    }

    public void calculatePayout(int pool, double[] amounts, int rounder) {

        // This is used to verify the payouts and pool match
        int total = 0;

        // This is used to track the payout amounts
        int[] payouts = new int[amounts.length];

        // This is where the string output will go for the list
        String[] payStr;

        ArrayAdapter<String> adapter;

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

        View view = this.getCurrentFocus();
        if( total != pool)
            Toast.makeText(getApplicationContext(), "Prize pool and total payouts do not match", Toast.LENGTH_SHORT).show();

        payStr = prepareOutput(payouts);
        adapter = new ArrayAdapter<String>(this, R.layout.layout, payStr);
        lvResults.setAdapter(adapter);

        if(view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public String[] prepareOutput(int[] values)
    {
        String[] results = new String[values.length];
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

        return(results);
    }

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

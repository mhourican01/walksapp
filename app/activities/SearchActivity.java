package com.example.walksapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.walksapp.R;

/**
 * Manages user search
 */
public class SearchActivity extends AppCompatActivity {

   // Instance variables
    private Button confirmSearchBtn, confirmDistSearchBtn;
    private EditText searchEditText;
    private String search;
    private Toolbar mToolbar;
    private TextView seekBarTextView;
    private int distProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Search for a route");

        confirmSearchBtn = findViewById(R.id.confirmSearchBtn);
        confirmSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchEditText = findViewById(R.id.searchEditText);
                search = searchEditText.getText().toString();

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", search);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        final SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        seekBar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        final int progress = seekBar.getProgress();

        seekBarTextView = findViewById(R.id.seekBarTextView);
        seekBarTextView.setText("Set max. distance: " + progress + "km");

        confirmDistSearchBtn = findViewById(R.id.confirmDistSearchBtn);
        confirmDistSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                distProgress = seekBar.getProgress();

                Intent returnIntent = new Intent();
                returnIntent.putExtra("dist_result", distProgress);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    /**
     * Controls SeekBar to set max. distance
     */
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            seekBarTextView.setText("Set max. distance: " + progress + "km");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };
}

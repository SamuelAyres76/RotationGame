package com.example.rotationgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;

public class HighScoresActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        TextView highScoresTextView = findViewById(R.id.highScoresTextView);
        Button playAgainButton = findViewById(R.id.playAgainButton);
        Button returnHomeButton = findViewById(R.id.returnHomeButton);
        Button viewResultsButton = findViewById(R.id.viewResultsButton);

        // Interacting with the Db
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Grab my Top 5 Scores
        Cursor cursor = dbHelper.getTopScores();

        // Hold the Data as a String
        StringBuilder builder = new StringBuilder();

        // Extract the Name and Score of the Top 5
        while (cursor.moveToNext())
        {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
            int score = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SCORE));
            builder.append(name).append(": ").append(score).append("\n");
        }

        // And Close once not Needed
        cursor.close();

        // Display the Top 5 High-Scores
        highScoresTextView.setText(builder.toString());

        // Button to Play Again
        playAgainButton.setOnClickListener(v ->
        {
            Intent intent = new Intent(HighScoresActivity.this, SequenceActivity.class);
            startActivity(intent);
            finish();
        });

        // Button to Return Home
        returnHomeButton.setOnClickListener(v ->
        {
            Intent intent = new Intent(HighScoresActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Button to View Results
        viewResultsButton.setOnClickListener(v ->
        {
            Intent intent = new Intent(HighScoresActivity.this, ResultsActivity.class);
            startActivity(intent);
        });
    }
}
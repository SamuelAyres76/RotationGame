package com.example.rotationgame;

// My Imports
import android.os.Bundle;
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

        // Interacting with my Db
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Grab my Top 5 Scores
        Cursor cursor = dbHelper.getTopScores();

        // Hold the Data as a String
        StringBuilder builder = new StringBuilder();

        // Extract the Name and Score of the Top 5
        while (cursor.moveToNext())
        {
            // Name
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));

            // Score
            int score = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SCORE));

            builder.append(name).append(": ").append(score).append("\n");
        }

        // And Close once not Needed
        cursor.close();

        // Display the Top 5 High-Scores
        highScoresTextView.setText(builder.toString());
    }
}
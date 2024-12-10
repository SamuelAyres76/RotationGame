package com.example.rotationgame;

// My Imports
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;

public class ResultsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // The Views
        TextView scoreTextView = findViewById(R.id.scoreTextView);
        Button playAgainButton = findViewById(R.id.playAgainButton);
        Button returnHomeButton = findViewById(R.id.returnHomeButton);
        Button highScoresButton = findViewById(R.id.highScoresButton);

        // Grab the Score from the Game
        int score = getIntent().getIntExtra("score", 0);

        // Display the Players Score
        scoreTextView.setText("Final Score: " + score);

        // Is it in Top 5?
        if (isTop5Score(score))
        {
            // If it is, Get the Players Name
            promptForName(score);
        }
        else
        {
            // If not... no recognition for them
        }

        // Restart the Game
        playAgainButton.setOnClickListener(v ->
        {
            Intent intent = new Intent(ResultsActivity.this, SequenceActivity.class);
            startActivity(intent);
            finish();
        });

        // Return Home
        returnHomeButton.setOnClickListener(v ->
        {
            Intent intent = new Intent(ResultsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // View High-Scores
        highScoresButton.setOnClickListener(v ->
        {
            Intent intent = new Intent(ResultsActivity.this, com.example.rotationgame.HighScoresActivity.class);
            startActivity(intent);
        });
    }

    // Is it in the Top 5?
    private boolean isTop5Score(int score)
    {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Grab the Top Scores
        Cursor cursor = dbHelper.getTopScores();

        if (cursor != null && cursor.moveToLast())
        {
            int lowestTopScore = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SCORE));
            cursor.close();

            return score > lowestTopScore || cursor.getCount() < 5;
        }
        // Kinda useless now, but the entry will always display is there isn't 5 entries
        return true;
    }

    // Get the Users Name
    private void promptForName(int score)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New High Score!");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_get_name, null, false);
        final EditText input = viewInflated.findViewById(R.id.inputName);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                String name = input.getText().toString();

                if (!name.isEmpty())
                {
                    // Save the Score with the Entered Name in the Database
                    DatabaseHelper dbHelper = new DatabaseHelper(ResultsActivity.this);
                    dbHelper.addScore(name, score);
                }
                else
                {
                    // Validation if they Enter an Empty Name
                    Toast.makeText(ResultsActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Cancel Button
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
                Toast.makeText(ResultsActivity.this, "Score not saved", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }
}
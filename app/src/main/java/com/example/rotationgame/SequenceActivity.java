package com.example.rotationgame;

// My Imports
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;

public class SequenceActivity extends AppCompatActivity implements SensorEventListener
{
    // My Variables
    private List<String> colorSequence;
    private int currentColorIndex = 0;
    private int score = 0;
    private CountDownTimer timer;
    private TextView timerText;
    private long remainingTime = 20000;

    // Accelerometer
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private static final String[] COLORS = {"Red", "Blue", "Green", "Yellow"};
    private static final long SHAKE_TIME = 5000;

    // Image Views for the Circles
    private ImageView northCircle;
    private ImageView southCircle;
    private ImageView eastCircle;
    private ImageView westCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequence);

        // UI
        timerText = findViewById(R.id.timerText);
        northCircle = findViewById(R.id.northCircle);
        southCircle = findViewById(R.id.southCircle);
        eastCircle = findViewById(R.id.eastCircle);
        westCircle = findViewById(R.id.westCircle);

        // Random Sequence Chosen
        colorSequence = generateRandomSequence();

        // Apply the order to the colors
        northCircle.setImageResource(getColorDrawable(colorSequence.get(0)));
        southCircle.setImageResource(getColorDrawable(colorSequence.get(1)));
        eastCircle.setImageResource(getColorDrawable(colorSequence.get(2)));
        westCircle.setImageResource(getColorDrawable(colorSequence.get(3)));

        // Tell the User the Order
        Toast toast = Toast.makeText(this, "Sequence: " + colorSequence.toString(), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        // Count them down
        startTimer();

        // Start sensing where they tilt.
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    // Random Sequence Generator
    private List<String> generateRandomSequence()
    {
        List<String> sequence = new ArrayList<>();
        Collections.addAll(sequence, COLORS);
        Collections.shuffle(sequence);
        return sequence.subList(0, 4);
    }

    // Connecting Colours to the Circles
    private int getColorDrawable(String color) {
        switch (color) {
            case "Red": return R.drawable.circle_red;
            case "Blue": return R.drawable.circle_blue;
            case "Green": return R.drawable.circle_green;
            case "Yellow": return R.drawable.circle_yellow;
            // Error Correction
            default: return R.drawable.circle_red;
        }
    }

    // Timer Display
    private void startTimer()
    {
        timer = new CountDownTimer(remainingTime, 1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                timerText.setText(String.valueOf(millisUntilFinished / 1000));

                // 5 Seconds Remaining Warning
                if (millisUntilFinished <= SHAKE_TIME)
                {
                    startShakingAnimation();
                    timerText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            }

            // Game Over :(
            @Override
            public void onFinish()
            {
                showGameOver();
            }
        }
        .start();
    }

    // Shaking Animation when Time is Running Out
    private void startShakingAnimation()
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(timerText, "translationX", -20f, 20f);
        animator.setDuration(100);
        animator.setRepeatCount(5);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.start();
    }

    // Game Over Method
    private void showGameOver()
    {
        Intent intent = new Intent(SequenceActivity.this, ResultsActivity.class);
        intent.putExtra("score", score);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putLong("remainingTime", remainingTime);
        outState.putInt("currentColorIndex", currentColorIndex);
        outState.putInt("score", score);

        if (timer != null)
        {
            timer.cancel();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        remainingTime = savedInstanceState.getLong("remainingTime");
        currentColorIndex = savedInstanceState.getInt("currentColorIndex");
        score = savedInstanceState.getInt("score");

        if (timer != null)
        {
            timer.cancel();
        }

        startTimer();
    }

    // Sensor Fails to Function without this
    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (timer != null)
        {
            timer.cancel();
        }
    }

    // Check the Tilt Direction
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            onTilt(x, y, z);
        }
    }

    // Is it Tilting to the Correct Color?
    private void onTilt(float x, float y, float z)
    {
        float threshold = 5.0f;
        String currentColor = colorSequence.get(currentColorIndex);
        boolean correctTilt = false;

        // Which Color is Correct?
        switch (currentColor)
        {
            case "Yellow":
                if (x < -threshold)
                {
                    correctTilt = true;
                }
                break;
            case "Red":
                if (y > threshold)
                {
                    correctTilt = true;
                }
                break;
            case "Blue":
                if (x > threshold)
                {
                    correctTilt = true;
                }
                break;
            case "Green":
                if (y < -threshold)
                {
                    correctTilt = true;
                }
                break;
        }

        // Hide the Correct Color
        if (correctTilt)
        {
            int circleId = getCircleIdByColor(currentColor);
            correctTiltAction(circleId);
        }
    }

    // Get the Color ID for each circle
    private int getCircleIdByColor(String color)
    {
        switch (color) {
            case "Red":
                return R.id.northCircle;
            case "Blue":
                return R.id.southCircle;
            case "Green":
                return R.id.eastCircle;
            case "Yellow":
                return R.id.westCircle;
            default:
                throw new IllegalArgumentException("Unknown color: " + color);
        }
    }

    // When the Correct Circle is Chosen...
    private void correctTiltAction(int circleId)
    {
        // Hide the Circle
        findViewById(circleId).setVisibility(View.INVISIBLE);

        // Move onto the Next Circle
        currentColorIndex++;

        // Once there are no more Circles, Give the User a Point
        if (currentColorIndex == colorSequence.size())
        {
            score += 1;
            nextRound();
        }
    }

    // Next Round
    private void nextRound()
    {
        // Cancel the old Timer
        if (timer != null)
        {
            timer.cancel();
        }

        // New Sequence Needed
        colorSequence = generateRandomSequence();
        currentColorIndex = 0;

        // Make all Circles Visible Again
        northCircle.setVisibility(View.VISIBLE);
        southCircle.setVisibility(View.VISIBLE);
        eastCircle.setVisibility(View.VISIBLE);
        westCircle.setVisibility(View.VISIBLE);

        // Alert the Player of their Score plus the Next Sequence
        Toast.makeText(this, "Sequence Completed! Score: " + score, Toast.LENGTH_SHORT).show();
        Toast toast = Toast.makeText(this, "New Sequence: " + colorSequence.toString(), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        // New Timer
        startTimer();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Na/
    }
}

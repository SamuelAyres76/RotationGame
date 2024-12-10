package com.example.rotationgame;

// My Imports
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class DatabaseHelper extends SQLiteOpenHelper
{
    // Name of Database + Version
    private static final String DATABASE_NAME = "highscores.db";
    private static final int DATABASE_VERSION = 1;

    // Table & Column Names
    public static final String TABLE_NAME = "scores";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SCORE = "score";

    // SQL to Create my table (if it don't exist)
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_SCORE + " INTEGER);";

    // Initializing le database
    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the Db is Created
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(TABLE_CREATE);
    }

    // Remaking the Table on Update
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Adding a New Score to the Database
    public void addScore(String name, int score)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Storing the Players Name and Their Score
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_SCORE, score);

        // Insert the Data and Close up
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Top 5 Scores
    public Cursor getTopScores()
    {
        // Need a readable database
        SQLiteDatabase db = this.getReadableDatabase();

        // Find it by Descending Order
        return db.query(TABLE_NAME,
                // Grab the name and score
                new String[]{COLUMN_NAME, COLUMN_SCORE},
                null,
                null,
                null,
                null,
                COLUMN_SCORE + " DESC",
                // ONLY show the top 5. Change this to view more.
                "5");
    }
}
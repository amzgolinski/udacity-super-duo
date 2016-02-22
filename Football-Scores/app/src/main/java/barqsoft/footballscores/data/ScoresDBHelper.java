package barqsoft.footballscores.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import barqsoft.footballscores.data.ScoresContract.ScoresTable;


public class ScoresDBHelper extends SQLiteOpenHelper {

  public static final String DATABASE_NAME = "Scores.db";
  private static final int DATABASE_VERSION = 2;

  public ScoresDBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    final String CreateScoresTable = "CREATE TABLE " +
      ScoresTable.TABLE_NAME + " (" +
      ScoresTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
      ScoresTable.COLUMN_LEAGUE + " INTEGER NOT NULL," +
      ScoresTable.COLUMN_MATCH_ID + " INTEGER NOT NULL," +
      ScoresTable.COLUMN_MATCH_DAY + " INTEGER NOT NULL," +
      ScoresTable.COLUMN_DATE + " TEXT NOT NULL," +
      ScoresTable.COLUMN_TIME + " INTEGER NOT NULL," +
      ScoresTable.COLUMN_HOME + " TEXT NOT NULL," +
      ScoresTable.COLUMN_HOME_GOALS + " TEXT NOT NULL," +
      ScoresTable.COLUMN_AWAY + " TEXT NOT NULL," +
      ScoresTable.COLUMN_AWAY_GOALS + " TEXT NOT NULL," +

      " UNIQUE (" + ScoresTable.COLUMN_MATCH_ID + ") ON CONFLICT REPLACE );";
    db.execSQL(CreateScoresTable);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    //Remove old values when upgrading.
    db.execSQL("DROP TABLE IF EXISTS " + ScoresTable.TABLE_NAME);
  }
}

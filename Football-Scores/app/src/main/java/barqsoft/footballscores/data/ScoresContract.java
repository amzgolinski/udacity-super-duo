package barqsoft.footballscores.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class ScoresContract {

  public static final String CONTENT_AUTHORITY = "barqsoft.footballscores";

  public static Uri BASE_CONTENT_URI =
    Uri.parse("content://" + CONTENT_AUTHORITY);

  public static final String PATH_SCORES = "scores";

  public static final class ScoresTable implements BaseColumns {

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_SCORES).build();

    public static final String TABLE_NAME = "scores_table";

    public static final String COLUMN_LEAGUE = "league";
    public static final String COLUMN_MATCH_ID = "match_id";
    public static final String COLUMN_MATCH_DAY = "match_day";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_HOME = "home";
    public static final String COLUMN_AWAY = "away";
    public static final String COLUMN_HOME_GOALS = "home_goals";
    public static final String COLUMN_AWAY_GOALS = "away_goals";

    //Types
    public static final String CONTENT_TYPE =
      ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
        CONTENT_AUTHORITY + "/" + PATH_SCORES;

    public static final String CONTENT_ITEM_TYPE =
      ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
        CONTENT_AUTHORITY + "/" + PATH_SCORES;

    public static Uri buildScoreWithLeague() {
      return CONTENT_URI.buildUpon().appendPath("league").build();
    }

    public static Uri buildScoreWithMatchId(long matchID) {
      return CONTENT_URI.buildUpon()
          .appendQueryParameter(COLUMN_MATCH_ID, Long.toString(matchID))
          .build();
    }

    public static Uri buildScoreUri(long id) {
      return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    public static Uri buildScoreWithDate() {
      return CONTENT_URI.buildUpon()
          .appendPath(COLUMN_DATE).build();
    }
  }


}

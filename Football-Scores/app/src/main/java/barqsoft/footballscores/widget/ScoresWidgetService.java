package barqsoft.footballscores.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.MainScreenFragment;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilities;
import barqsoft.footballscores.data.ScoresContract;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ScoresWidgetService extends RemoteViewsService {

  public static final String LOG_TAG =
      ScoresWidgetService.class.getSimpleName();

  private static final String[] SCORES_COLUMNS = {
      ScoresContract.ScoresTable.COLUMN_MATCH_ID,
      ScoresContract.ScoresTable.COLUMN_MATCH_DAY,
      ScoresContract.ScoresTable.COLUMN_LEAGUE,
      ScoresContract.ScoresTable.COLUMN_HOME,
      ScoresContract.ScoresTable.COLUMN_HOME_GOALS,
      ScoresContract.ScoresTable.COLUMN_AWAY,
      ScoresContract.ScoresTable.COLUMN_AWAY_GOALS
  };

  // these indices must match the projection
  static final int INDEX_COLUMN_MATCH_ID = 0;
  static final int INDEX_COLUMN_MATCH_DAY = 1;
  static final int INDEX_COLUMN_LEAGUE = 2;
  static final int INDEX_COLUMN_HOME = 3;
  static final int INDEX_COLUMN_HOME_GOALS = 4;
  static final int INDEX_COLUMN_AWAY = 5;
  static final int INDEX_COLUMN_AWAY_GOALS = 6;

  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    Log.d(LOG_TAG, "onGetViewFactory");
    return new ScoresRemoteViewsFactory(getApplicationContext(), intent);
  }

  class ScoresRemoteViewsFactory implements
      RemoteViewsService.RemoteViewsFactory {

    private Cursor mCursor;

    public ScoresRemoteViewsFactory(Context context, Intent intent) {
      // empty
    }

    public void onCreate() {
      Log.d(LOG_TAG, "onCreate");
    }

    public void onDestroy() {
      Log.d(LOG_TAG, "onDestroy");
      if (mCursor != null) {
        mCursor.close();
        mCursor = null;
      }
    }

    public int getCount() {
      Log.d(LOG_TAG, "getCount");
      return (mCursor == null ? 0 : mCursor.getCount());
    }

    public RemoteViews getViewAt(int position) {

      if (position == AdapterView.INVALID_POSITION ||
          mCursor == null ||
          !mCursor.moveToPosition(position)) {
        return null;
      }

      RemoteViews views =
          new RemoteViews(getPackageName(), R.layout.widget_item);

      // league
      int leagueID = Utilities.getLeague(mCursor.getInt(INDEX_COLUMN_LEAGUE));
      String leagueName = getApplicationContext().getString(leagueID);
      views.setTextViewText(R.id.scores_widget_league, leagueName);

      // home team
      String homeTeam = mCursor.getString(INDEX_COLUMN_HOME);
      views.setTextViewText(R.id.scores_widget_home_team, homeTeam);

      // away team
      String awayTeam = mCursor.getString(INDEX_COLUMN_AWAY);
      views.setTextViewText(R.id.scores_widget_away_team, awayTeam);

      // scoreline
      String scoreline = Utilities.formatScores(
          mCursor.getInt(INDEX_COLUMN_HOME_GOALS),
          mCursor.getInt(INDEX_COLUMN_AWAY_GOALS)
      );

      views.setTextViewText(R.id.scores_widget_score, scoreline);

      // matchday
      String matchDay =
          getString(R.string.match_day, mCursor.getString(INDEX_COLUMN_MATCH_DAY));
      views.setTextViewText(R.id.scores_widget_matchday, matchDay);

      final Intent fillInIntent = new Intent();
      Uri scoreUri = ScoresContract.ScoresTable.buildScoreWithMatchId(mCursor.getLong(INDEX_COLUMN_MATCH_ID));
      fillInIntent.setData(scoreUri);
      Bundle extras = new Bundle();
      extras.putInt(MainScreenFragment.POSITION, position);
      fillInIntent.putExtras(extras);
      views.setOnClickFillInIntent(R.id.widget_score_item, fillInIntent);

      return views;
    }

    public RemoteViews getLoadingView() {
      Log.d(LOG_TAG, "getLoadingView");
      return new RemoteViews(getPackageName(), R.layout.widget_item);
    }

    public int getViewTypeCount() {
      Log.d(LOG_TAG, "getViewTypeCount");
      return 1;
    }

    public long getItemId(int position) {
      if (mCursor.moveToPosition(position))
        return mCursor.getLong(INDEX_COLUMN_MATCH_ID);
      return position;
    }

    public boolean hasStableIds() {
      Log.d(LOG_TAG, "hasStableIds");
      return true;
    }

    public void onDataSetChanged() {
      Log.d(LOG_TAG, "onDataSetChanged");

      if (mCursor != null) {
        mCursor.close();
      }
      final long identityToken = Binder.clearCallingIdentity();

      Uri scoresWithDateUri = ScoresContract.ScoresTable.buildScoreWithDate();
      // format the date argument as YYYY-MM-DD
      String pattern = getString(R.string.date_format_yyyy_MM_dd);
      SimpleDateFormat formatter = new SimpleDateFormat(pattern);
      Date today = new Date();
      String output = formatter.format(today);
      mCursor = getContentResolver().query(
          scoresWithDateUri,
          SCORES_COLUMNS,
          null,
          new String[]{output},
          ScoresContract.ScoresTable.COLUMN_LEAGUE + " ASC");

      Binder.restoreCallingIdentity(identityToken);

    }
  }
}

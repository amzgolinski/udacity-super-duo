package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ScoresAdapter extends CursorAdapter {

  public static final String LOG_TAG = ScoresAdapter.class.getSimpleName();

  private static final String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";

  // Member variables
  public double mDetailMatchID = 0;

  public ScoresAdapter(Context context, Cursor cursor, int flags) {
    super(context, cursor, flags);
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View mItem = LayoutInflater.from(context).inflate(
      R.layout.scores_list_item,
      parent,
      false
    );
    ViewHolder mHolder = new ViewHolder(mItem);
    mItem.setTag(mHolder);
    return mItem;
  }

  @Override
  public void bindView(View view, final Context context, Cursor cursor) {

    final ViewHolder mHolder = (ViewHolder) view.getTag();

    // match ID
    mHolder.matchID =
        cursor.getDouble(MainScreenFragment.INDEX_COLUMN_MATCH_ID);

    // home team
    mHolder.homeName.setText(
        cursor.getString(MainScreenFragment.INDEX_COLUMN_HOME));

    // away team
    mHolder.awayName.setText(
        cursor.getString(MainScreenFragment.INDEX_COLUMN_AWAY));

    // match date
    mHolder.date.setText(
        cursor.getString(MainScreenFragment.INDEX_COLUMN_DATE));

    // match result
    String scores = Utilities.formatScores(
        cursor.getInt(MainScreenFragment.INDEX_COLUMN_HOME_GOALS),
        cursor.getInt(MainScreenFragment.INDEX_COLUMN_AWAY_GOALS)
    );
    mHolder.score.setText(scores);

    /*

    // home crest
    int crest = Utilities.getTeamCrestByTeamName(
        cursor.getString(MainScreenFragment.COLUMN_HOME_CREST));
    mHolder.homeCrest.setImageResource(crest);

    // away crest
    crest = Utilities.getTeamCrestByTeamName(
        cursor.getString(MainScreenFragment.COLUMN_AWAY_CREST));
    mHolder.awayCrest.setImageResource(crest);
    */

    LayoutInflater vi = (LayoutInflater) context
        .getApplicationContext()
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    View v = vi.inflate(R.layout.detail_fragment, null);

    ViewGroup container =
        (ViewGroup) view.findViewById(R.id.details_fragment_container);

    if (mHolder.matchID == mDetailMatchID) {
      //Log.v(FetchScoreTask.LOG_TAG,"will insert extraView");

      container.addView(
          v,
          0,
          new ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.MATCH_PARENT)
      );

      TextView matchDay = (TextView) v.findViewById(R.id.matchday_textview);

      int leagueID = cursor.getInt(MainScreenFragment.INDEX_COLUMN_LEAGUE);

      matchDay.setText(
          Utilities.getMatchDay(
              context,
              cursor.getInt(MainScreenFragment.INDEX_COLUMN_MATCH_DAY),
              leagueID));
      TextView league = (TextView) v.findViewById(R.id.league_textview);
      league.setText(Utilities.getLeague(context,leagueID));
      Button share_button = (Button) v.findViewById(R.id.share_button);
      share_button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          //add Share Action
          context.startActivity(
            createShareForecastIntent(
              mHolder.homeName.getText() + " " +
              mHolder.score.getText() + " " +
              mHolder.awayName.getText() + " ")
          );
        }
      });
    } else {
      container.removeAllViews();
    }
  }

  public Intent createShareForecastIntent(String ShareText) {
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
    return shareIntent;
  }

}

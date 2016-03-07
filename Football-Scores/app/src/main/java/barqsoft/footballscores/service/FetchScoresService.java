package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.data.ScoresContract;
import barqsoft.footballscores.model.Score;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilities;


public class FetchScoresService extends IntentService {

  public static final String LOG_TAG = FetchScoresService.class.getSimpleName();

  public static final String DATA_UPDATED =
      "barqsoft.footballscores.DATA_UPDATED";

  public FetchScoresService() {
    super(LOG_TAG);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Log.v(LOG_TAG, "onHandleIntent");
    getData("n3");
    getData("p3");
  }

  private ContentValues[] generateContentValues(Score[] scores,
                                                boolean liveData)
      throws ParseException {

    // ContentValues to be inserted
    Vector<ContentValues> values = new Vector<>(scores.length);

    String time;
    String date;
    String matchID;

    for (int i = 0; i < scores.length; i++) {

      int leagueID = scores[i].getLeagueID();
      if (Utilities.isTrackedLeague(leagueID)) {
        matchID = Integer.toString(scores[i].getMatchID());
        date = getLocalMatchTime(scores[i].getDate());

        if (!liveData) {
          matchID = matchID + Integer.toString(i);
          long dateInMilis = Utilities.getDateInMillis(i);
          date = getDummyMatchTime(dateInMilis);
        }

        time = date.substring(date.indexOf(":") + 1);
        date = date.substring(0, date.indexOf(":"));

        ContentValues matchValues = new ContentValues();
        matchValues.put(ScoresContract.ScoresTable.COLUMN_MATCH_ID, matchID);
        matchValues.put(ScoresContract.ScoresTable.COLUMN_DATE, date);
        matchValues.put(ScoresContract.ScoresTable.COLUMN_TIME, time);

        matchValues.put(
            ScoresContract.ScoresTable.COLUMN_HOME,
            scores[i].getHomeTeamName()
        );

        matchValues.put(
            ScoresContract.ScoresTable.COLUMN_HOME_GOALS,
            scores[i].getHomeTeamGoals()
        );

        matchValues.put(
            ScoresContract.ScoresTable.COLUMN_AWAY,
            scores[i].getAwayTeamName()
        );

        matchValues.put(
            ScoresContract.ScoresTable.COLUMN_AWAY_GOALS,
            scores[i].getAwayTeamGoals()
        );

        matchValues.put(ScoresContract.ScoresTable.COLUMN_LEAGUE, leagueID);

        matchValues.put(
            ScoresContract.ScoresTable.COLUMN_MATCH_DAY,
            scores[i].getMatchday()
        );

        values.add(matchValues);
      }
    }
    return values.toArray(new ContentValues[values.size()]);
  }

  private void getData(String timeFrame) {
    Log.d(LOG_TAG, "getData");

    String scoresJSON;
    Score[] scores;
    try {
      //Base URL
      final String BASE_URL = "http://api.football-data.org/v1/fixtures";

      //Time Frame parameter to determine days
      final String QUERY_TIME_FRAME = "timeFrame";

      OkHttpClient client = new OkHttpClient();

      HttpUrl scoresURL = HttpUrl.parse(BASE_URL)
          .newBuilder()
          .addQueryParameter(QUERY_TIME_FRAME, timeFrame)
          .build();

      Log.v(LOG_TAG, scoresURL.toString());

      Request request = new Request.Builder()
          .url(scoresURL)
          .build();

      Response response = client.newCall(request).execute();
      scoresJSON = response.body().string();

      boolean liveData = verifyMatchData(scoresJSON);
      if (!liveData ) {
        scoresJSON = getString(R.string.dummy_data);
      }
      scores = parseScoreJSON(scoresJSON);
      ContentValues[] values = generateContentValues(scores, liveData);
      insertScores(values);

    } catch (IOException ioException) {
      Log.e(LOG_TAG, "Communication error: " + ioException.getMessage());

    } catch (JSONException jsonException) {
      Log.e(LOG_TAG, "Exception parsing JSON:" + jsonException.getMessage());

    } catch (ParseException parseException) {
      Log.e(LOG_TAG, "Parsing exception:" + parseException.getMessage());
    }
  }

  private String getDummyMatchTime(long dateInMilis) {
    StringBuilder matchDetails = new StringBuilder();
    final String MATCH_TIME = getString(R.string.dummy_match_time);
    Date fragmentDate = new Date(dateInMilis);
    SimpleDateFormat format =
        new SimpleDateFormat(getString(R.string.date_format_yyyy_MM_dd));
    String date = format.format(fragmentDate);
    matchDetails.append(date).append(":").append(MATCH_TIME);
    return matchDetails.toString();

  }

  private String getLocalMatchTime(String utcMatchTime) throws ParseException {
    String localMatchTime;

    SimpleDateFormat matchDate =
        new SimpleDateFormat(getString(R.string.date_format_json));
    matchDate.setTimeZone(TimeZone.getTimeZone("UTC"));
    Date parsedDate = matchDate.parse(utcMatchTime);
    SimpleDateFormat newDate =
        new SimpleDateFormat(getString(R.string.date_format_final));
    newDate.setTimeZone(TimeZone.getDefault());
    localMatchTime = newDate.format(parsedDate);

    return localMatchTime;
  }


  private void insertScores(ContentValues[] scores) {

    if (scores!= null && scores.length > 0) {

      int inserted = getApplicationContext()
          .getContentResolver()
          .bulkInsert(ScoresContract.ScoresTable.CONTENT_URI, scores);

      if (inserted > 0) {
        Intent dataUpdated =
            new Intent(DATA_UPDATED).setPackage(getPackageName());
        sendBroadcast(dataUpdated);
      }
    }
  }

  private Score[] parseScoreJSON(String jsonData) {

    Score[] scores;
    final String RESULTS = getString(R.string.fixtures_element);
    Gson gson = new Gson();
    JsonParser parser = new JsonParser();
    JsonObject object = parser.parse(jsonData).getAsJsonObject();
    scores = gson.fromJson(object.get(RESULTS), Score[].class);
    return scores;
  }

  private boolean verifyMatchData(String scoresJSON) throws JSONException {

    boolean matchData = false;
    if (scoresJSON != null) {
      JSONArray matches = new JSONObject(scoresJSON)
          .getJSONArray(getString(R.string.fixtures_element));
      if (matches.length() > 0) {
        matchData = true;
      }
    }
    return matchData;
  }


}


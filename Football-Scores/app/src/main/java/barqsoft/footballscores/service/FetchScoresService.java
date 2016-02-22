package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.data.ScoresContract;
import barqsoft.footballscores.R;


public class FetchScoresService extends IntentService {

  public static final String LOG_TAG = FetchScoresService.class.getSimpleName();

  public static final String DATA_UPDATED =
      "barqsoft.footballscores.DATA_UPDATED";

  public FetchScoresService() {
    super("FetchScoresService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Log.v(LOG_TAG, "onHandleIntent");
    getData("n3");
    getData("p3");
  }

  private void getData(String timeFrame) {
    Log.d(LOG_TAG, "getData");

    //Base URL
    final String BASE_URL = "http://api.football-data.org/v1/fixtures";

    //Time Frame parameter to determine days
    final String QUERY_TIME_FRAME = "timeFrame";
    //final String QUERY_MATCH_DAY = "matchday";

    Uri uri = Uri.parse(BASE_URL).buildUpon().
      appendQueryParameter(QUERY_TIME_FRAME, timeFrame).build();

    //Log.v(LOG_TAG, "The url we are looking at is: "+fetch_build.toString());
    HttpURLConnection connection = null;
    BufferedReader reader = null;
    String jsonData = null;
    Log.d(LOG_TAG, uri.toString());
    //Opening Connection
    try {
      URL fetch = new URL(uri.toString());
      connection = (HttpURLConnection) fetch.openConnection();
      connection.setRequestMethod("GET");
      connection.addRequestProperty("X-Auth-Token", getString(R.string.api_key));
      connection.connect();

      // Read the input stream into a String
      InputStream inputStream = connection.getInputStream();
      StringBuffer buffer = new StringBuffer();
      if (inputStream == null) {
        // Nothing to do.
        return;
      }
      reader = new BufferedReader(new InputStreamReader(inputStream));

      String line;
      while ((line = reader.readLine()) != null) {
        // Since it's JSON, adding a newline isn't necessary (it won't affect
        // parsing) But it does make debugging a *lot* easier if you print out
        // the completed buffer for debugging.
        buffer.append(line + "\n");
      }
      if (buffer.length() == 0) {
        // Stream was empty.  No point in parsing.
        return;
      }
      jsonData = buffer.toString();
      // Log.d(LOG_TAG, jsonData);
    } catch (Exception e) {
      Log.e(LOG_TAG, "Exception here" + e.getMessage());
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          Log.e(LOG_TAG, "Error Closing Stream");
        }
      }
    }
    try {
      if (jsonData != null) {
        //This bit is to check if the data contains any matches. If not, we call
        // processJson on the dummy data
        JSONArray matches = new JSONObject(jsonData).getJSONArray("fixtures");
        if (matches.length() == 0) {
          //if there is no data, call the function on dummy data
          //this is expected behavior during the off season.
          processJSONData(
            getString(R.string.dummy_data),
            getApplicationContext(),
            false
          );
          return;
        }
        processJSONData(jsonData, getApplicationContext(), true);
      } else {
        //Could not Connect
        Log.d(LOG_TAG, "Could not connect to server.");
      }
    } catch (Exception e) {
      Log.e(LOG_TAG, e.getMessage());
    }
  }

  private void processJSONData(String JSONdata, Context mContext,
                               boolean isReal) {
    // This set of league codes is for the 2015/2016 season. In fall of 2016,
    // they will need to be updated. Feel free to use the codes
    final String BUNDESLIGA1 = "394";
    final String BUNDESLIGA2 = "395";
    final String LIGUE1 = "396";
    final String LIGUE2 = "397";
    final String PREMIER_LEAGUE = "398";
    final String PRIMERA_DIVISION = "399";
    final String SEGUNDA_DIVISION = "400";
    final String SERIE_A = "401";
    final String PRIMERA_LIGA = "402";
    final String EREDIVISIE = "404";
    final String BUNDESLIGA3 = "403";
    final String CHAMPS_LEAGUE = "405";

    final String SEASON_LINK =
      "http://api.football-data.org/v1/soccerseasons/";

    final String MATCH_LINK =
      "http://api.football-data.org/v1/fixtures/";

    final String FIXTURES = "fixtures";
    final String LINKS = "_links";
    final String SOCCER_SEASON = "soccerseason";
    final String SELF = "self";
    final String MATCH_DATE = "date";
    final String HOME_TEAM = "homeTeamName";
    final String AWAY_TEAM = "awayTeamName";
    final String RESULT = "result";
    final String HOME_GOALS = "goalsHomeTeam";
    final String AWAY_GOALS = "goalsAwayTeam";
    final String MATCH_DAY = "matchday";

    //Match data
    String league = null;
    String date = null;
    String time = null;
    String home = null;
    String away = null;
    String homeGoals = null;
    String awayGoals = null;
    String matchID = null;
    String matchDay = null;

    try {
      JSONArray matches = new JSONObject(JSONdata).getJSONArray(FIXTURES);

      // ContentValues to be inserted
      Vector<ContentValues> values =
          new Vector<ContentValues>(matches.length());

      for (int i = 0; i < matches.length(); i++) {

        JSONObject matchData = matches.getJSONObject(i);

        league = matchData
            .getJSONObject(LINKS)
            .getJSONObject(SOCCER_SEASON)
            .getString("href");

        league = league.replace(SEASON_LINK, "");

        //This if statement controls which leagues we're interested in the data
        // from. add leagues here in order to have them be added to the DB.
        // If you are finding no data in the app, check that this contains all
        // the leagues. If it doesn't, that can cause an empty DB, bypassing
        // the dummy data routine.
        if (league.equals(PREMIER_LEAGUE) ||
            league.equals(SERIE_A) ||
            league.equals(BUNDESLIGA1) ||
            league.equals(LIGUE1) ||
            league.equals(PRIMERA_DIVISION) ||
            league.equals(EREDIVISIE) ||
            league.equals(CHAMPS_LEAGUE)) {

          matchID = matchData
              .getJSONObject(LINKS)
              .getJSONObject(SELF)
              .getString("href");

          matchID = matchID.replace(MATCH_LINK, "");

          if (!isReal) {
            //This if statement changes the match ID of the dummy data so that
            // it all goes into the database
            matchID = matchID + Integer.toString(i);
          }

          date = matchData.getString(MATCH_DATE);
          time = date.substring(date.indexOf("T") + 1, date.indexOf("Z"));
          date = date.substring(0, date.indexOf("T"));
          SimpleDateFormat matchDate =
            new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
          matchDate.setTimeZone(TimeZone.getTimeZone("UTC"));
          try {
            Date parsedDate = matchDate.parse(date + time);
            SimpleDateFormat newDate = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
            newDate.setTimeZone(TimeZone.getDefault());
            date = newDate.format(parsedDate);
            time = date.substring(date.indexOf(":") + 1);
            date = date.substring(0, date.indexOf(":"));

            if (!isReal) {
              //This if statement changes the dummy data's date to match our
              // current date range.
              Date fragmentDate =
                new Date(System.currentTimeMillis() + ((i - 2) * 86400000));
              SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
              date = format.format(fragmentDate);
            }
          } catch (Exception e) {
            Log.d(LOG_TAG, "error here!");
            Log.e(LOG_TAG, e.getMessage());
          }
          home = matchData.getString(HOME_TEAM);
          away = matchData.getString(AWAY_TEAM);
          homeGoals = matchData.getJSONObject(RESULT).getString(HOME_GOALS);
          awayGoals = matchData.getJSONObject(RESULT).getString(AWAY_GOALS);
          matchDay = matchData.getString(MATCH_DAY);
          ContentValues matchValues = new ContentValues();
          matchValues.put(ScoresContract.ScoresTable.COLUMN_MATCH_ID, matchID);
          matchValues.put(ScoresContract.ScoresTable.COLUMN_DATE, date);
          matchValues.put(ScoresContract.ScoresTable.COLUMN_TIME, time);
          matchValues.put(ScoresContract.ScoresTable.COLUMN_HOME, home);
          matchValues.put(
              ScoresContract.ScoresTable.COLUMN_HOME_GOALS,
              homeGoals
          );
          matchValues.put(ScoresContract.ScoresTable.COLUMN_AWAY, away);

          matchValues.put(
              ScoresContract.ScoresTable.COLUMN_AWAY_GOALS,
              awayGoals
          );
          matchValues.put(ScoresContract.ScoresTable.COLUMN_LEAGUE, league);
          matchValues.put(
              ScoresContract.ScoresTable.COLUMN_MATCH_DAY,
              matchDay
          );

          values.add(matchValues);
        }
      }

      ContentValues[] insert_data = new ContentValues[values.size()];
      values.toArray(insert_data);

      int inserted = mContext.getContentResolver().bulkInsert(
          ScoresContract.ScoresTable.CONTENT_URI,
          insert_data
      );

      if (inserted > 0) {
        Log.d(LOG_TAG, "Inserted data for " + inserted + " matches");
        Intent dataUpdated =
            new Intent(DATA_UPDATED).setPackage(getPackageName());
        sendBroadcast(dataUpdated);
      }

    } catch (JSONException e) {
      Log.e(LOG_TAG, e.getMessage());
    }
  }
}


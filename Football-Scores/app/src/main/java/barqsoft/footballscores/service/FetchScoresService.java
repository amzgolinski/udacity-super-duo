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

  public FetchScoresService() {
    super("FetchScoresService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Log.v(LOG_TAG, "onHandleIntent");
    getData("n2");
    getData("p2");
  }

  private void getData(String timeFrame) {
    Log.d(LOG_TAG, "getData");

    //Base URL
    final String BASE_URL = "http://api.football-data.org/alpha/fixtures";

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
      Log.d(LOG_TAG, jsonData);
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
    final String BUNDESLIGA3 = "403";
    final String LIGUE1 = "396";
    final String LIGUE2 = "397";
    final String PREMIER_LEAGUE = "398";
    final String PRIMERA_DIVISION = "399";
    final String SEGUNDA_DIVISION = "400";
    final String SERIE_A = "401";
    final String PRIMERA_LIGA = "402";
    final String EREDIVISIE = "404";

    final String SEASON_LINK =
      "http://api.football-data.org/alpha/soccerseasons/";

    final String MATCH_LINK =
      "http://api.football-data.org/alpha/fixtures/";

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

        JSONObject match_data = matches.getJSONObject(i);
        league = match_data.getJSONObject(LINKS).getJSONObject(SOCCER_SEASON).
          getString("href");
        league = league.replace(SEASON_LINK, "");
        //This if statement controls which leagues we're interested in the data
        // from. add leagues here in order to have them be added to the DB.
        // If you are finding no data in the app, check that this contains all
        // the leagues. If it doesn't, that can cause an empty DB, bypassing
        // the dummy data routine.
        if (league.equals(PREMIER_LEAGUE) ||
          league.equals(SERIE_A) ||
          league.equals(BUNDESLIGA1) ||
          league.equals(BUNDESLIGA2) ||
          league.equals(PRIMERA_DIVISION)) {
          matchID = match_data.getJSONObject(LINKS).getJSONObject(SELF).
            getString("href");
          matchID = matchID.replace(MATCH_LINK, "");
          if (!isReal) {
            //This if statement changes the match ID of the dummy data so that
            // it all goes into the database
            matchID = matchID + Integer.toString(i);
          }

          date = match_data.getString(MATCH_DATE);
          time = date.substring(date.indexOf("T") + 1, date.indexOf("Z"));
          date = date.substring(0, date.indexOf("T"));
          SimpleDateFormat match_date =
            new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
          match_date.setTimeZone(TimeZone.getTimeZone("UTC"));
          try {
            Date parsedDate = match_date.parse(date + time);
            SimpleDateFormat new_date =
              new SimpleDateFormat("yyyy-MM-dd:HH:mm");
            new_date.setTimeZone(TimeZone.getDefault());
            date = new_date.format(parsedDate);
            time = date.substring(date.indexOf(":") + 1);
            date = date.substring(0, date.indexOf(":"));

            if (!isReal) {
              //This if statement changes the dummy data's date to match our
              // current date range.
              Date fragmentdate =
                new Date(System.currentTimeMillis() + ((i - 2) * 86400000));
              SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
              date = mformat.format(fragmentdate);
            }
          } catch (Exception e) {
            Log.d(LOG_TAG, "error here!");
            Log.e(LOG_TAG, e.getMessage());
          }
          home = match_data.getString(HOME_TEAM);
          away = match_data.getString(AWAY_TEAM);
          homeGoals = match_data.getJSONObject(RESULT).getString(HOME_GOALS);
          awayGoals = match_data.getJSONObject(RESULT).getString(AWAY_GOALS);
          matchDay = match_data.getString(MATCH_DAY);
          ContentValues match_values = new ContentValues();
          match_values.put(ScoresContract.ScoresTable.MATCH_ID, matchID);
          match_values.put(ScoresContract.ScoresTable.DATE_COL, date);
          match_values.put(ScoresContract.ScoresTable.TIME_COL, time);
          match_values.put(ScoresContract.ScoresTable.HOME_COL, home);
          match_values.put(ScoresContract.ScoresTable.AWAY_COL, away);
          match_values.put(ScoresContract.ScoresTable.HOME_GOALS_COL, homeGoals);
          match_values.put(ScoresContract.ScoresTable.AWAY_GOALS_COL, awayGoals);
          match_values.put(ScoresContract.ScoresTable.LEAGUE_COL, league);
          match_values.put(ScoresContract.ScoresTable.MATCH_DAY, matchDay);

          //Log.v(LOG_TAG,match_id);
          //Log.v(LOG_TAG,mDate);
          //Log.v(LOG_TAG,mTime);
          //Log.v(LOG_TAG,Home);
          //Log.v(LOG_TAG,Away);
          //Log.v(LOG_TAG,Home_goals);
          //Log.v(LOG_TAG,Away_goals);

          values.add(match_values);
        }
      }
      int inserted = 0;
      ContentValues[] insert_data = new ContentValues[values.size()];
      values.toArray(insert_data);
      inserted = mContext.getContentResolver().bulkInsert(
        ScoresContract.BASE_CONTENT_URI, insert_data);

      //Log.v(LOG_TAG,"Successfully Inserted : " + String.valueOf(inserted_data));
    } catch (JSONException e) {
      Log.e(LOG_TAG, e.getMessage());
    }
  }
}


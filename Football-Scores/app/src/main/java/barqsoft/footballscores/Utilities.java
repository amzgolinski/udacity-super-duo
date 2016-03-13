package barqsoft.footballscores;

import android.content.Context;
import android.util.Log;


import barqsoft.footballscores.model.League;

public class Utilities {

  private static final String LOG_TAG = Utilities.class.getSimpleName();

  public static final int MILLISECONDS_IN_DAY = 86400000;
  public static final String EMPTY_STRING = "";

  public static long getDateInMillis(int offset) {
    return (System.currentTimeMillis() + ((offset - 2) * MILLISECONDS_IN_DAY));
  }

  public static int getLeague(int leagueNum) {

    int leagueString = -1;

    if (leagueNum == League.BUNDESLIGA1.getId()) {
      leagueString = R.string.league_name_bundesliga;

    } else if (leagueNum == League.CHAMPS_LEAGUE.getId()) {
      leagueString = R.string.league_name_champs_league;
    } else if (leagueNum == League.EREDIVISIE.getId()) {
      leagueString = R.string.league_name_eredivisie;
    } else if (leagueNum == League.LIGUE1.getId()) {
      leagueString = R.string.league_name_ligue1;
    } else if (leagueNum == League.PREMIER_LEAGUE.getId()) {
      leagueString = R.string.league_name_premier_league;
    } else if (leagueNum == League.PRIMERA_DIVISION.getId()) {
      leagueString = R.string.league_name_primera_division;
    } else if (leagueNum == League.SERIE_A.getId()) {
      leagueString = R.string.league_name_seria_a;
    }

    return leagueString;
  }

  public static String getMatchDay(Context context, int matchDay,
                                   int leagueID) {
     String matchDayString;

    if (leagueID == League.CHAMPS_LEAGUE.getId()) {
      int matchdayResource;
      if (matchDay <= 6) {
        matchdayResource = R.string.champs_league_group_stages;
      } else if (matchDay == 7 || matchDay == 8) {
        matchdayResource = R.string.champs_league_first_knockout;
      } else if (matchDay == 9 || matchDay == 10) {
        matchdayResource = R.string.champs_league_quarterfinal;
      } else if (matchDay == 11 || matchDay == 12) {
        matchdayResource = R.string.champs_league_semifinal;
      } else {
        matchdayResource = R.string.champs_league_final;
      }
      matchDayString = context.getString(matchdayResource);
    } else {
      matchDayString =
          context.getString(R.string.match_day, String.valueOf(matchDay));
    }
    return matchDayString;
  }

  public static String formatScores(int homeGoals, int awayGoals) {
    if (homeGoals < 0 || awayGoals < 0) {
      return " - ";
    } else {
      return String.valueOf(homeGoals) + " : " + String.valueOf(awayGoals);
    }
  }

  public static int getTeamCrestByTeamName(Context context, String name) {
    int toReturn = R.drawable.no_icon;

    if (name.equals(context.getString(R.string.team_name_arsenal))) {
      toReturn = R.drawable.arsenal;
    } else if (name.equals(context.getString(R.string.team_name_manchester_united))) {
      toReturn = R.drawable.manchester_united;
    } else if (name.equals(context.getString(R.string.team_name_swansea))) {
      toReturn = R.drawable.swansea_city_afc;
    } else if (name.equals(context.getString(R.string.team_name_leicester_city))) {
      toReturn = R.drawable.leicester_city_fc_hd_logo;
    } else if (name.equals(context.getString(R.string.team_name_everton))) {
      toReturn = R.drawable.everton_fc_logo1;
    } else if (name.equals(context.getString(R.string.team_name_west_ham))) {
      toReturn = R.drawable.west_ham;
    } else if (name.equals(context.getString(R.string.team_name_tottenham))) {
      toReturn = R.drawable.tottenham_hotspur;
    } else if (name.equals(context.getString(R.string.team_name_west_bromwich))) {
      toReturn = R.drawable.west_bromwich_albion_hd_logo;
    } else if (name.equals(context.getString(R.string.team_name_sunderland))) {
      toReturn = R.drawable.sunderland;
    } else if (name.equals(context.getString(R.string.team_name_stoke_city))) {
      toReturn = R.drawable.stoke_city;
    }
    return toReturn;
  }

  public static boolean isTrackedLeague(int leagueID) {
    return (
        leagueID == League.BUNDESLIGA1.getId() ||
        leagueID == League.PREMIER_LEAGUE.getId() ||
        leagueID == League.CHAMPS_LEAGUE.getId() ||
        leagueID == League.EREDIVISIE.getId() ||
        leagueID == League.PRIMERA_DIVISION.getId() ||
        leagueID == League.LIGUE1.getId() ||
        leagueID == League.SERIE_A.getId()
    );

  }

}

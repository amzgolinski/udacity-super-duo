package barqsoft.footballscores;

import android.content.Context;

public class Utilities {

  public static final int MILLISECONDS_IN_DAY = 86400000;

  public interface Leagues {

    int BUNDESLIGA1 = 394;
    int BUNDESLIGA2 = 395;
    int LIGUE1 = 396;
    int LIGUE2 = 397;
    int PREMIER_LEAGUE = 398;
    int PRIMERA_DIVISION = 399;
    int SEGUNDA_DIVISION = 400;
    int SERIE_A = 401;
    int PRIMERA_LIGA = 402;
    int BUNDESLIGA3 = 403;
    int EREDIVISIE = 404;
    int CHAMPS_LEAGUE = 405;
  }

  public static long getDateInMillis(int offset) {
    return (System.currentTimeMillis() + ((offset - 2) * MILLISECONDS_IN_DAY));
  }

  public static String getLeague(Context context, int leagueNum) {

    int leagueString;
    switch (leagueNum) {

      case Leagues.SERIE_A:
        leagueString = R.string.league_name_seria_a;
        break;
      case Leagues.PREMIER_LEAGUE:
        leagueString = R.string.league_name_premier_league;
        break;
      case Leagues.CHAMPS_LEAGUE:
        leagueString = R.string.league_name_champs_league;
        break;
      case Leagues.PRIMERA_DIVISION:
        leagueString = R.string.league_name_primera_division;
        break;
      case Leagues.BUNDESLIGA1:
        leagueString = R.string.league_name_bundesliga;
        break;
      case Leagues.EREDIVISIE:
        leagueString = R.string.league_name_eredivisie;
        break;
      default:
        leagueString = R.string.league_not_listed;
    }
    return context.getString(leagueString);
  }

  public static String getMatchDay(Context context, int matchDay,
                                   int leagueNum) {
     String matchDayString;

    if (leagueNum == Leagues.CHAMPS_LEAGUE) {
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
      matchDayString =  "Matchday : " + String.valueOf(matchDay);
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
    if (name == null) {
      toReturn = R.drawable.no_icon;
    }

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
}

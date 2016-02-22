package barqsoft.footballscores;

public class Utilities {

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

  public static String getLeague(int leagueNum) {
    switch (leagueNum) {
      case Leagues.SERIE_A:
        return "Seria A";
      case Leagues.PREMIER_LEAGUE:
        return "Premier League";
      case Leagues.CHAMPS_LEAGUE:
        return "UEFA Champions League";
      case Leagues.PRIMERA_DIVISION:
        return "Primera Division";
      case Leagues.BUNDESLIGA1:
        return "Bundesliga";
      case Leagues.EREDIVISIE:
        return "Eredivisie";
      default:
        return "League not listed";
    }
  }

  public static String getMatchDay(int matchDay, int leagueNum) {
    if (leagueNum == Leagues.CHAMPS_LEAGUE) {
      if (matchDay <= 6) {
        return "Group Stages, Matchday : 6";
      } else if (matchDay == 7 || matchDay == 8) {
        return "First Knockout round";
      } else if (matchDay == 9 || matchDay == 10) {
        return "QuarterFinal";
      } else if (matchDay == 11 || matchDay == 12) {
        return "SemiFinal";
      } else {
        return "Final";
      }
    } else {
      return "Matchday : " + String.valueOf(matchDay);
    }
  }

  public static String getScores(int homeGoals, int awayGoals) {
    if (homeGoals < 0 || awayGoals < 0) {
      return " - ";
    } else {
      return String.valueOf(homeGoals) + " - " + String.valueOf(awayGoals);
    }
  }

  public static int getTeamCrestByTeamName(String name) {
    if (name == null) {
      return R.drawable.no_icon;
    }
    switch (name) {
      //This is the set of icons that are currently in the
      // app. Feel free to find and add more as you go.
      case "Arsenal London FC":
        return R.drawable.arsenal;
      case "Manchester United FC":
        return R.drawable.manchester_united;
      case "Swansea City":
        return R.drawable.swansea_city_afc;
      case "Leicester City":
        return R.drawable.leicester_city_fc_hd_logo;
      case "Everton FC":
        return R.drawable.everton_fc_logo1;
      case "West Ham United FC":
        return R.drawable.west_ham;
      case "Tottenham Hotspur FC":
        return R.drawable.tottenham_hotspur;
      case "West Bromwich Albion":
        return R.drawable.west_bromwich_albion_hd_logo;
      case "Sunderland AFC":
        return R.drawable.sunderland;
      case "Stoke City FC":
        return R.drawable.stoke_city;
      default:
        return R.drawable.no_icon;
    }
  }
}

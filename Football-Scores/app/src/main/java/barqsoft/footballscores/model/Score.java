package barqsoft.footballscores.model;

import barqsoft.footballscores.Utilities;

public class Score {
  private String date;
  private String status;
  private int matchday;
  private String homeTeamName;
  private String awayTeamName;
  private Result result;
  private Links _links;

  static class Result {

    int goalsHomeTeam;
    int goalsAwayTeam;

    int getGoalsHomeTeam() {
      return goalsHomeTeam;
    }

    int getGoalsAwayTeam() {
      return goalsAwayTeam;
    }

  }

  static class Links {

    Self self;
    SoccerSeason soccerseason;
    HomeTeam homeTeam;
    AwayTeam awayTeam;

    Links() {
      // empty
    }

    static class Self {
      String href;
      final String MATCH_LINK = "http://api.football-data.org/v1/fixtures/";

      Self() {
        // empty
      }

      String getHref() {
        return href;
      }

      String getMatchID() {
        return href.replace(MATCH_LINK, Utilities.EMPTY_STRING);
      }
    }

    static class SoccerSeason {

      String href;
      String leagueID;
      final String SEASON_LINK = "http://api.football-data.org/v1/soccerseasons/";

      SoccerSeason() {
        // empty
      }

      String getHref() {
        return href;
      }

      String getLeagueID() {
        return href.replace(SEASON_LINK, Utilities.EMPTY_STRING);
      }

    }

    static class HomeTeam {
      String href;

      HomeTeam() {

      }

      String getHref() {
        return href;
      }
    }

    static class AwayTeam {
      String href;
      AwayTeam() {
        // empty
      }
      String getHref() {
        return href;
      }
    }

  }

  public Score() {
    // empty
  }

  public String getDate() {
    return date;
  }

  public String getStatus() {
    return status;
  }

  public int getMatchday() {
    return matchday;
  }

  public String getHomeTeamName() {
    return homeTeamName;
  }

  public String getAwayTeamName() {
    return awayTeamName;
  }

  public int getLeagueID() {
    String leagueID = _links.soccerseason.getLeagueID();
    return Integer.parseInt(leagueID);
  }

  public int getMatchID() {
    String matchID = _links.self.getMatchID();
    return  Integer.parseInt(matchID);
  }

  public int getHomeTeamGoals() {
    return result.getGoalsHomeTeam();
  }

  public int getAwayTeamGoals() {
    return result.getGoalsAwayTeam();
  }


  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Date: " + date + "\n");
    sb.append("Status: " + status + "\n");
    sb.append("Match day: " + matchday + "\n");
    sb.append("Home team: " + homeTeamName + "\n");
    sb.append("Home team score: " + result.getGoalsHomeTeam() + "\n");
    sb.append("Away team: " + awayTeamName + "\n");
    sb.append("Away team score: " + result.getGoalsAwayTeam() + "\n");
    sb.append("Self: " + _links.self.getHref() + "\n");
    sb.append("SoccerSeason: " + _links.soccerseason.getHref() + "\n");
    return sb.toString();
  }

}




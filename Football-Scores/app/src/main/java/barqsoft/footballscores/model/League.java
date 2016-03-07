package barqsoft.footballscores.model;


public enum League {

  BUNDESLIGA1(394),
  BUNDESLIGA2(395),
  LIGUE1(396),
  LIGUE2(397),
  PREMIER_LEAGUE(398),
  PRIMERA_DIVISION(399),
  SEGUNDA_DIVISION(400),
  SERIE_A(401),
  PRIMERA_LIGA(402),
  BUNDESLIGA3(403),
  EREDIVISIE(404),
  CHAMPS_LEAGUE(405);

  private final int id;

  League(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }
}

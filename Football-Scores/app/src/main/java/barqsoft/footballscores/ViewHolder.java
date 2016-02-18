package barqsoft.footballscores;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViewHolder {

  @Bind(R.id.home_name) TextView homeName;
  @Bind(R.id.away_name) TextView awayName;
  @Bind(R.id.score_textview) TextView score;
  @Bind(R.id.data_textview) TextView date;
  @Bind(R.id.home_crest) ImageView homeCrest;
  @Bind(R.id.away_crest) ImageView awayCrest;

  public double matchID;

  public ViewHolder(View view) {
    ButterKnife.bind(this, view);
  }
}

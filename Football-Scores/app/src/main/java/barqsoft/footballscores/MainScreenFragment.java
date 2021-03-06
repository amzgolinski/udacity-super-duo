package barqsoft.footballscores;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import barqsoft.footballscores.data.ScoresContract;
import barqsoft.footballscores.service.FetchScoresService;


public class MainScreenFragment extends Fragment
  implements LoaderManager.LoaderCallbacks<Cursor> {

  public static final String LOG_TAG = MainScreenFragment.class.getSimpleName();
  public static final String POSITION = "barqsoft.footballscores.position";

  // Constants
  public static final int SCORES_LOADER = 0;

  public static final String SORT_ORDER =
      ScoresContract.ScoresTable.COLUMN_LEAGUE + " ASC";

  public static final String[] SCORES_COLUMNS = {
      ScoresContract.ScoresTable._ID,
      ScoresContract.ScoresTable.COLUMN_LEAGUE,
      ScoresContract.ScoresTable.COLUMN_MATCH_ID,
      ScoresContract.ScoresTable.COLUMN_MATCH_DAY,
      ScoresContract.ScoresTable.COLUMN_DATE,
      ScoresContract.ScoresTable.COLUMN_TIME,
      ScoresContract.ScoresTable.COLUMN_HOME,
      ScoresContract.ScoresTable.COLUMN_HOME_GOALS,
      ScoresContract.ScoresTable.COLUMN_AWAY,
      ScoresContract.ScoresTable.COLUMN_AWAY_GOALS
  };

  public static final int INDEX_COLUMN_ID = 0;
  public static final int INDEX_COLUMN_LEAGUE = 1;
  public static final int INDEX_COLUMN_MATCH_ID = 2;
  public static final int INDEX_COLUMN_MATCH_DAY = 3;
  public static final int INDEX_COLUMN_DATE = 4;
  public static final int INDEX_COLUMN_TIME = 5;
  public static final int INDEX_COLUMN_HOME = 6;
  public static final int INDEX_COLUMN_HOME_GOALS = 7;
  public static final int INDEX_COLUMN_AWAY = 8;
  public static final int INDEX_COLUMN_AWAY_GOALS = 9;

  // Member variables
  public ScoresAdapter mAdapter;
  private String[] mFragmentDate = new String[1];
  private ListView mListView;
  private int mPosition = ListView.INVALID_POSITION;
  private int mLastSelectedItem = -1;

  public MainScreenFragment() {
    // empty
  }

  private void updateScores() {
    Intent fetchScoresIntent =
      new Intent(getActivity(), FetchScoresService.class);
    getActivity().startService(fetchScoresIntent);
  }

  public void setFragmentDate(String date) {
    mFragmentDate[0] = date;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           final Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_main, container, false);

    mListView = (ListView) rootView.findViewById(R.id.scores_list);

    Intent test = getActivity().getIntent();
    Bundle extras = test.getExtras();
    if (extras != null) {
      int value = extras.getInt(POSITION, ListView.INVALID_POSITION);
      mPosition = value;
    }

    mAdapter = new ScoresAdapter(getActivity(), null, 0);
    mListView.setAdapter(mAdapter);
    getLoaderManager().initLoader(SCORES_LOADER, null, this);
    mAdapter.mDetailMatchID = MainActivity.mSelectedMatchID;

    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position,
                              long id) {
        ViewHolder selected = (ViewHolder) view.getTag();
        mAdapter.mDetailMatchID = selected.matchID;
        MainActivity.mSelectedMatchID = (int) selected.matchID;
        mAdapter.notifyDataSetChanged();
      }
    });
    updateScores();
    return rootView;
  }

  @Override
  public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

    return new CursorLoader(
        getActivity(),
        ScoresContract.ScoresTable.buildScoreWithDate(),
        SCORES_COLUMNS,
        null,
        mFragmentDate,
        SORT_ORDER
    );
  }

  @Override
  public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    int i = 0;
    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      i++;
      cursor.moveToNext();
    }
    mAdapter.swapCursor(cursor);

    if (mPosition != ListView.INVALID_POSITION) {
      Log.d(LOG_TAG, "Position is " + mPosition);
      mListView.setSelection(mPosition);
      mPosition = ListView.INVALID_POSITION;
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> cursorLoader) {
    mAdapter.swapCursor(null);
  }
}

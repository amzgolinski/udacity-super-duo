package barqsoft.footballscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

  public static final String LOG_TAG = MainActivity.class.getSimpleName();

  private static final String CURRENT_PAGE =
      "barqsoft.footballscores.CURRENT_PAGE";

  private static final String SELECTED_MATCH =
      "barqsoft.footballscores.SELECTED_MATCH";

  private static final String PAGER_FRAGMENT =
      "barqsoft.footballscores.PAGER_FRAGMENT";

  public static int mSelectedMatchID;
  public static int mCurrentFragment = 2;
  private PagerFragment mMyMain;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.d(LOG_TAG, "onCreate");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (savedInstanceState == null) {
      mMyMain = new PagerFragment();
      getSupportFragmentManager().beginTransaction()
        .add(R.id.container, mMyMain)
        .commit();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_about) {
      Intent start_about = new Intent(this, AboutActivity.class);
      startActivity(start_about);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {

    outState.putInt(CURRENT_PAGE, mMyMain.mPagerHandler.getCurrentItem());
    outState.putInt(SELECTED_MATCH, mSelectedMatchID);
    getSupportFragmentManager().putFragment(outState, PAGER_FRAGMENT, mMyMain);
    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {

    mCurrentFragment = savedInstanceState.getInt(CURRENT_PAGE);
    mSelectedMatchID = savedInstanceState.getInt(SELECTED_MATCH);
    mMyMain = (PagerFragment) getSupportFragmentManager()
        .getFragment(savedInstanceState, PAGER_FRAGMENT);
    super.onRestoreInstanceState(savedInstanceState);
  }
}

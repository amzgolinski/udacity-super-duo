package barqsoft.footballscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

  public static final String LOG_TAG = MainActivity.class.getSimpleName();

  public static int mSelectedMatchID;
  public static int mCurrentFragment = 2;
  private PagerFragment mMyMain;

  private final String SAVE_TAG = "Save Test";

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
    Log.v(LOG_TAG, "onSaveInstanceState");
    Log.v(SAVE_TAG,
      "fragment: " + String.valueOf(mMyMain.mPagerHandler.getCurrentItem()));
    Log.v(SAVE_TAG, "selected id: " + mSelectedMatchID);
    outState.putInt("Pager_Current", mMyMain.mPagerHandler.getCurrentItem());
    outState.putInt("Selected_match", mSelectedMatchID);
    getSupportFragmentManager().putFragment(outState, "my_main", mMyMain);
    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    Log.v(LOG_TAG, "onRestoreInstanceState");
    Log.v(SAVE_TAG,
      "fragment: " + String.valueOf(savedInstanceState.getInt("Pager_Current")));
    Log.v(SAVE_TAG,
      "selected id: " + savedInstanceState.getInt("Selected_match"));
    mCurrentFragment = savedInstanceState.getInt("Pager_Current");
    mSelectedMatchID = savedInstanceState.getInt("Selected_match");
    mMyMain = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState, "my_main");
    super.onRestoreInstanceState(savedInstanceState);
  }
}

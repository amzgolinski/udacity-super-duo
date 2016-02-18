package barqsoft.footballscores;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;


public class PagerFragment extends Fragment {

  // Constants
  public static final int NUM_PAGES = 5;
  private static final int MILLISECONDS_IN_DAY = 86400000;

  public ViewPager mPagerHandler;
  private PageAdapter mPagerAdapter;
  private MainScreenFragment[] viewFragments = new MainScreenFragment[5];

  @Override
  public View onCreateView(LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
    mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
    mPagerAdapter = new PageAdapter(getChildFragmentManager());

    for (int i = 0; i < NUM_PAGES; i++) {
      Date fragmentDate =
        new Date(System.currentTimeMillis() + ((i - 2) * MILLISECONDS_IN_DAY));
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      viewFragments[i] = new MainScreenFragment();
      viewFragments[i].setFragmentDate(dateFormat.format(fragmentDate));
    }
    mPagerHandler.setAdapter(mPagerAdapter);
    mPagerHandler.setCurrentItem(MainActivity.mCurrentFragment);
    return rootView;
  }

  private class PageAdapter extends FragmentStatePagerAdapter {

    @Override
    public Fragment getItem(int i) {
      return viewFragments[i];
    }

    @Override
    public int getCount() {
      return NUM_PAGES;
    }

    public PageAdapter(FragmentManager fm) {
      super(fm);
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
      return getDayName(getActivity(),
        System.currentTimeMillis() + ((position - 2) * MILLISECONDS_IN_DAY));
    }

    public String getDayName(Context context, long dateInMillis) {
      // If the date is today, return the localized version of "Today" instead
      // of the actual day name.

      Time t = new Time();
      t.setToNow();
      int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
      int currentJulianDay =
        Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
      if (julianDay == currentJulianDay) {
        return context.getString(R.string.today);
      } else if (julianDay == currentJulianDay + 1) {
        return context.getString(R.string.tomorrow);
      } else if (julianDay == currentJulianDay - 1) {
        return context.getString(R.string.yesterday);
      } else {
        Time time = new Time();
        time.setToNow();
        // Otherwise, the format is just the day of the week (e.g "Wednesday").
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        return dayFormat.format(dateInMillis);
      }
    }
  }
}
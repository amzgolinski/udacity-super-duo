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

  // Member variables
  public ViewPager mPagerHandler;
  private MainScreenFragment[] mViewFragments;

  public PagerFragment() {
    mViewFragments = new MainScreenFragment[NUM_PAGES];
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
    mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
    PageAdapter pagerAdapter = new PageAdapter(getChildFragmentManager());

    for (int i = 0; i < NUM_PAGES; i++) {

      Date fragmentDate = new Date(Utilities.getDateInMillis(i));

      SimpleDateFormat dateFormat =
          new SimpleDateFormat(getString(R.string.date_format_yyyy_MM_dd));

      mViewFragments[i] = new MainScreenFragment();
      mViewFragments[i].setFragmentDate(dateFormat.format(fragmentDate));
    }
    mPagerHandler.setAdapter(pagerAdapter);
    mPagerHandler.setCurrentItem(MainActivity.mCurrentFragment);
    return rootView;
  }

  private class PageAdapter extends FragmentStatePagerAdapter {

    @Override
    public Fragment getItem(int i) {
      return mViewFragments[i];
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
      return getDayName(getActivity(), Utilities.getDateInMillis(position));
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
        SimpleDateFormat dayFormat =
            new SimpleDateFormat(getString(R.string.date_format_day));
        return dayFormat.format(dateInMillis);
      }
    }
  }
}
package it.jaschke.alexandria;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import it.jaschke.alexandria.services.BookService;

public class Utility {

  public static final String EMPTY_STRING = "";

  public static String addEanPrefix(Context context, String ean) {
    String prefix = context.getString(R.string.ean_prefix);

    if (ean.length() == 10 && !ean.startsWith(prefix)) {
      ean = prefix + ean;
    }
    return ean;
  }

  @SuppressWarnings("ResourceType")
  public static @BookService.ServerStatus int getServerStatus(Context context) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    return prefs.getInt(
      context.getString(R.string.server_status_key),
      BookService.SERVER_STATUS_OK
    );
  }

  public static boolean isNetworkConnected(Context context) {
    ConnectivityManager cm =
      (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

    return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());

  }

  public static void resetServerStatus(Context context) {
    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putInt(
      context.getString(R.string.server_status_key),
      BookService.SERVER_STATUS_RESET);
    editor.apply();
  }

}

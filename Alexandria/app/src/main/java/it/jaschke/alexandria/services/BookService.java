package it.jaschke.alexandria.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.jaschke.alexandria.model.Book;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import it.jaschke.alexandria.MainActivity;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;


public class BookService extends IntentService {

  private final String LOG_TAG = BookService.class.getSimpleName();

  public static final String FETCH_BOOK
    = "it.jaschke.alexandria.services.action.FETCH_BOOK";

  public static final String DELETE_BOOK
    = "it.jaschke.alexandria.services.action.DELETE_BOOK";

  public static final String EAN = "it.jaschke.alexandria.services.extra.EAN";

  public static final String APP_NAME = "Alexandria";

  @IntDef({
    SERVER_STATUS_OK,
    SERVER_STATUS_INVALID,
    SERVER_STATUS_SERVER_DOWN,
    SERVER_STATUS_SERVER_INVALID,
    SERVER_STATUS_UNKNOWN,
    SERVER_STATUS_RESET})

  @Retention(RetentionPolicy.SOURCE)
  public @interface ServerStatus {}
  public static final int SERVER_STATUS_OK= 0;
  public static final int SERVER_STATUS_INVALID = 1;
  public static final int SERVER_STATUS_SERVER_DOWN = 2;
  public static final int SERVER_STATUS_SERVER_INVALID = 3;
  public static final int SERVER_STATUS_UNKNOWN = 4;
  public static final int SERVER_STATUS_RESET = 5;

  public BookService() {
    super(APP_NAME);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent != null) {
      final String action = intent.getAction();
      if (FETCH_BOOK.equals(action)) {

        final String ean = intent.getStringExtra(EAN);
        fetchBook(ean);

      } else if (DELETE_BOOK.equals(action)) {
        final String ean = intent.getStringExtra(EAN);
        deleteBook(ean);
      }
    }
  }

  private void deleteBook(String ean) {
    if (ean != null) {
      getContentResolver().delete(
        AlexandriaContract.BookEntry.buildBookUri(Long.parseLong(ean)),
        null,
        null
      );
    }
  }

  private Book parseBookJSON(String jsonData) {

    Book book;
    final String ITEMS = "items";
    final String VOLUME_INFO = "volumeInfo";
    Gson gson = new Gson();
    JsonParser parser = new JsonParser();
    JsonObject object = parser.parse(jsonData).getAsJsonObject();
    JsonArray items = object.getAsJsonArray(ITEMS);
    JsonObject itemEntry = items.get(0).getAsJsonObject();
    book = gson.fromJson(itemEntry.get(VOLUME_INFO), Book.class);
    Log.d(LOG_TAG, book.toString());
    return book;
  }

  /**
   * Handle action fetchBook in the provided background thread with the provided
   * parameters.
   */
  private void fetchBook(String ean) {

    Log.d(LOG_TAG, "fetchBook");

    if (ean.length() != 13) {
      Log.d(LOG_TAG, "EAN too short: " + ean);
      return;
    }

    if (bookExists(ean)) {
      Log.d(LOG_TAG, "Book already exists: " + ean);
      return;
    }

    String booksJSON;
    Book book;

    try {

      final String FORECAST_BASE_URL
        = "https://www.googleapis.com/books/v1/volumes?";

      final String QUERY_PARAM = "q";

      final String ISBN_PARAM = "isbn:" + ean;

      OkHttpClient client = new OkHttpClient();

      HttpUrl scoresURL = HttpUrl.parse(FORECAST_BASE_URL)
          .newBuilder()
          .addQueryParameter(QUERY_PARAM, ISBN_PARAM)
          .build();

      Log.d(LOG_TAG, scoresURL.toString());

      Request request = new Request.Builder()
          .url(scoresURL)
          .build();

      Response response = client.newCall(request).execute();
      booksJSON = response.body().string();

      Log.d(LOG_TAG, "JSON String: " + booksJSON);

      book = parseBookJSON(booksJSON);
      writeBackBook(ean, book);
      if (book.hasAuthors()) {
        writeBackAuthors(ean, book.getAuthors());
      }
      if (book.hasCategories()) {
        writeBackCategories(ean, book.getCategories());
      }

    } catch (IOException e) {

      Log.e(LOG_TAG, "IOException: " + e.getMessage(), e);
      setServerStatus(SERVER_STATUS_SERVER_DOWN);
      return;

    } catch(JSONException jsonException) {
      setServerStatus(SERVER_STATUS_SERVER_INVALID);
      Log.e(LOG_TAG, "JSONException:  ", jsonException);
      return;

    }

    final String ITEMS = "items";
    final String VOLUME_INFO = "volumeInfo";
    final String TITLE = "title";
    final String SUBTITLE = "subtitle";
    final String AUTHORS = "authors";
    final String DESC = "description";
    final String CATEGORIES = "categories";
    final String IMG_URL_PATH = "imageLinks";
    final String IMG_URL = "thumbnail";

    try {
      JSONObject bookJson = new JSONObject(booksJSON);
      JSONArray bookArray;
      if (bookJson.has(ITEMS)) {
        bookArray = bookJson.getJSONArray(ITEMS);
      } else {
        Intent messageIntent = new Intent(MainActivity.MESSAGE_EVENT);
        messageIntent.putExtra(
          MainActivity.MESSAGE_KEY,
            getResources().getString(R.string.not_found)
        );
        LocalBroadcastManager.getInstance(
            getApplicationContext()).sendBroadcast(messageIntent);
        return;
      }

      JSONObject bookInfo =
          ((JSONObject) bookArray.get(0)).getJSONObject(VOLUME_INFO);

      String title = bookInfo.getString(TITLE);

      String subtitle = "";
      if (bookInfo.has(SUBTITLE)) {
        subtitle = bookInfo.getString(SUBTITLE);
      }

      String desc = "";
      if (bookInfo.has(DESC)) {
        desc = bookInfo.getString(DESC);
      }

      String imgUrl = "";
      if (bookInfo.has(IMG_URL_PATH) &&
          bookInfo.getJSONObject(IMG_URL_PATH).has(IMG_URL)) {
        imgUrl = bookInfo.getJSONObject(IMG_URL_PATH).getString(IMG_URL);
      }

      //writeBackBook(ean, title, subtitle, desc, imgUrl);

      if (bookInfo.has(AUTHORS)) {
        //writeBackAuthors(ean, bookInfo.getJSONArray(AUTHORS));
      }

      if (bookInfo.has(CATEGORIES)) {
        //writeBackCategories(ean, bookInfo.getJSONArray(CATEGORIES));
      }

      setServerStatus(SERVER_STATUS_OK);

    } catch (JSONException e) {
      setServerStatus(SERVER_STATUS_SERVER_INVALID);
      Log.e(LOG_TAG, "JSONException:  ", e);
      return;
    }
  }

  private boolean bookExists(String ean) {
    boolean exists = false;

    Cursor bookEntry = getContentResolver().query(
      AlexandriaContract.BookEntry.buildBookUri(Long.parseLong(ean)),
      null, // leaving "columns" null just returns all the columns.
      null, // cols for "where" clause
      null, // values for "where" clause
      null  // sort order
    );

    if (bookEntry.getCount() > 0) {
      exists = true;
    }

    bookEntry.close();
    Log.d(LOG_TAG, "Book exists: " + exists);
    return exists;
  }

  private void setServerStatus(@ServerStatus int status) {
    Log.d(LOG_TAG, "setServerStatus");
    SharedPreferences sharedPref =
      PreferenceManager.getDefaultSharedPreferences(this);
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putInt(getString(R.string.server_status_key), status);
    editor.commit();
  }

  private void writeBackAuthors(String ean, JSONArray jsonArray)
    throws JSONException {
    ContentValues values = new ContentValues();
    for (int i = 0; i < jsonArray.length(); i++) {
      values.put(AlexandriaContract.AuthorEntry._ID, ean);
      values.put(AlexandriaContract.AuthorEntry.AUTHOR, jsonArray.getString(i));
      getContentResolver().insert(
          AlexandriaContract.AuthorEntry.CONTENT_URI,
          values
      );
      values = new ContentValues();
    }
  }

  private void writeBackBook(String ean, String title, String subtitle,
                             String desc, String imgUrl) {
    Log.d(LOG_TAG, "Adding book " + ean);
    ContentValues values = new ContentValues();
    values.put(AlexandriaContract.BookEntry._ID, ean);
    values.put(AlexandriaContract.BookEntry.TITLE, title);
    values.put(AlexandriaContract.BookEntry.IMAGE_URL, imgUrl);
    values.put(AlexandriaContract.BookEntry.SUBTITLE, subtitle);
    values.put(AlexandriaContract.BookEntry.DESC, desc);
    getContentResolver().insert(
        AlexandriaContract.BookEntry.CONTENT_URI,
        values
    );
  }

  private void writeBackCategories(String ean, JSONArray jsonArray)
      throws JSONException {
    ContentValues values = new ContentValues();
    for (int i = 0; i < jsonArray.length(); i++) {
      values.put(AlexandriaContract.CategoryEntry._ID, ean);
      values.put(
          AlexandriaContract.CategoryEntry.CATEGORY,
          jsonArray.getString(i)
      );
      getContentResolver().insert(
          AlexandriaContract.CategoryEntry.CONTENT_URI,
          values
      );
      values = new ContentValues();
    }
  }

  private void writeBackBook(String ean, Book book) {
    Log.d(LOG_TAG, "Adding book " + ean);
    ContentValues values = new ContentValues();
    values.put(AlexandriaContract.BookEntry._ID, ean);
    values.put(AlexandriaContract.BookEntry.TITLE, book.getTitle());
    values.put(AlexandriaContract.BookEntry.SUBTITLE, book.getSubtitle());
    values.put(AlexandriaContract.BookEntry.IMAGE_URL, book.getThumbnail());
    values.put(AlexandriaContract.BookEntry.DESC, book.getDescription());
    getContentResolver().insert(
        AlexandriaContract.BookEntry.CONTENT_URI,
        values
    );
  }


  private void writeBackCategories(String ean, String[] categories)
      throws JSONException {
    ContentValues values = new ContentValues();
    for (int i = 0; i < categories.length; i++) {
      values.put(AlexandriaContract.CategoryEntry._ID, ean);
      values.put(
          AlexandriaContract.CategoryEntry.CATEGORY,
          categories[i]
      );
      getContentResolver().insert(
          AlexandriaContract.CategoryEntry.CONTENT_URI,
          values
      );
      values = new ContentValues();
    }
  }

  private void writeBackAuthors(String ean, String[] authors)
      throws JSONException {
    ContentValues values = new ContentValues();
    for (int i = 0; i < authors.length; i++) {
      values.put(AlexandriaContract.AuthorEntry._ID, ean);
      values.put(AlexandriaContract.AuthorEntry.AUTHOR, authors[i]);
      getContentResolver().insert(
          AlexandriaContract.AuthorEntry.CONTENT_URI,
          values
      );
      values = new ContentValues();
    }
  }

}
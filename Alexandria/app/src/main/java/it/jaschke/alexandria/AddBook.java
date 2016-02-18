package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;


public class AddBook extends Fragment implements
  LoaderManager.LoaderCallbacks<Cursor>,
  SharedPreferences.OnSharedPreferenceChangeListener {

  private static final String LOG_TAG = AddBook.class.getSimpleName();
  private static final int BARCODE_CAPTURE = 1001;
  private final int LOADER_ID = 1;

  private EditText ean;
  private View mRrootView;
  private final String EAN_CONTENT = "eanContent";

  @Bind(R.id.ean) EditText mEan;
  @Bind(R.id.noBookData) TextView mNoBookData;
  @Bind(R.id.bookTitle) TextView mBookTitle;
  @Bind(R.id.bookSubTitle) TextView mBookSubTitle;
  @Bind(R.id.authors) TextView mAuthors;
  @Bind(R.id.bookCover) ImageView mBookCover;
  @Bind(R.id.categories) TextView mCategories;
  @Bind(R.id.save_button) AppCompatButton mSave;
  @Bind(R.id.delete_button) AppCompatButton mDelete;
  @Bind(R.id.scan_button) Button mScan;

  public AddBook() {
    // empty
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {

    Log.d(LOG_TAG, "onActivityResult");

    if (requestCode == BARCODE_CAPTURE) {
      if (resultCode == CommonStatusCodes.SUCCESS) {
        if (data != null) {
          Barcode barcode =
            data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
          Log.d(LOG_TAG, "Barcode read: " + barcode.displayValue);
          mEan.setText(barcode.displayValue);
          downloadBook(barcode.displayValue);
        } else {
          Log.d(LOG_TAG, "No barcode captured, intent data is null");
        }
      }
    }
  }

  @Override
  public void onAttach(Activity activity) {
    Log.d(LOG_TAG, "onAttach");
    super.onAttach(activity);
    activity.setTitle(R.string.scan);
  }

  @Override
  public android.support.v4.content.Loader<Cursor> onCreateLoader(int id,
                                                                  Bundle args) {
    Log.d(LOG_TAG, "onCreateLoader");
    if (mEan.getText().length() == 0) {
      return null;
    }
    String eanStr = mEan.getText().toString();
    Utility.addEanPrefix(getContext(), eanStr);

    return new CursorLoader(
      getActivity(),
      AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
      null,
      null,
      null,
      null
    );
  }

  @Override
  public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    Log.d(LOG_TAG, "onCreateView");

    mRrootView = inflater.inflate(R.layout.fragment_add_book, container, false);
    ButterKnife.bind(this, mRrootView);

    if (savedInstanceState != null) {
      mEan.setText(savedInstanceState.getString(EAN_CONTENT));
      mEan.setHint("");
    }

    return mRrootView;
  }

  @OnClick(R.id.save_button)
  public void saveBook() {
    mEan.setText("");
  }

  @OnClick(R.id.scan_button)
  public void scanBook() {

    Context context = getActivity();
    Intent intent = new Intent(context, BarcodeCaptureActivity.class);
    //TODO: right now these are hard-coded, but they probably should be controls
    intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
    intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
    startActivityForResult(intent, BARCODE_CAPTURE);
  }

  @OnClick(R.id.delete_button)
  public void deleteBook() {
    Intent bookIntent = new Intent(getActivity(), BookService.class);
    bookIntent.putExtra(BookService.EAN, mEan.getText().toString());
    bookIntent.setAction(BookService.DELETE_BOOK);
    getActivity().startService(bookIntent);
    mEan.setText("");
  }

  @OnTextChanged(R.id.ean)
  public void searchBook(CharSequence s) {
    Log.d(LOG_TAG, "searchBook");
    String ean = s.toString();
    //catch isbn10 numbers
    ean = Utility.addEanPrefix(getContext(), ean);

    if (ean.length() < 13) {
      clearFields();
      return;
    }
    this.downloadBook(ean);
  }

  @Override
  public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader,
                             Cursor data) {

    Log.d(LOG_TAG, "onLoadFinished");
    if (!data.moveToFirst()) {
      return;
    }

    String bookTitle =
      data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
    mBookTitle.setText(bookTitle);

    String bookSubTitle = data.getString(
      data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
    mBookSubTitle.setText(bookSubTitle);

    String authors = data.getString(
      data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
    String[] authorsArr = authors.split(",");
    mAuthors.setLines(authorsArr.length);
    mAuthors.setText(authors.replace(",", "\n"));

    String imgUrl = data.getString(
      data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));

    Picasso.with(getContext())
      .load(imgUrl)
      .error(R.drawable.no_poster_available)
      .into(mBookCover);
    mBookCover.setVisibility(View.VISIBLE);

    String categories = data.getString(
        data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
    mCategories.setText(categories);

    mSave.setVisibility(View.VISIBLE);
    mDelete.setVisibility(View.VISIBLE);

  }

  @Override
  public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
    Log.d(LOG_TAG, "onLoaderReset");
    // empty
  }

  @Override
  public void onPause() {
    Log.d(LOG_TAG, "onPause");
    super.onPause();
    PreferenceManager.
      getDefaultSharedPreferences(getContext()).
      unregisterOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onResume() {
    Log.d(LOG_TAG, "onResume");
    super.onResume();
    PreferenceManager.
      getDefaultSharedPreferences(getContext()).
      registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (ean != null) {
      outState.putString(EAN_CONTENT, ean.getText().toString());
    }
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
    Log.d(LOG_TAG, "onSharedPreferenceChanged");

    if (key.equals(getString(R.string.server_status_key))) {
      @BookService.ServerStatus int status = Utility.getServerStatus(getActivity());
      Log.d(LOG_TAG, Integer.toString(status));
      if (status != BookService.SERVER_STATUS_OK &&
        status != BookService.SERVER_STATUS_RESET) {
        setErrorMessage(status);
      }
      Utility.resetServerStatus(getContext());
    }
  }

  private void clearFields() {
    Log.d(LOG_TAG, "clearFields");
    mBookTitle.setText(Utility.EMPTY_STRING);
    mBookSubTitle.setText(Utility.EMPTY_STRING);
    mAuthors.setText(Utility.EMPTY_STRING);
    mCategories.setText(Utility.EMPTY_STRING);
    mBookCover.setVisibility(View.INVISIBLE);
    mSave.setVisibility(View.INVISIBLE);
    mDelete.setVisibility(View.INVISIBLE);
    mNoBookData.setText(Utility.EMPTY_STRING);
    mNoBookData.setVisibility(View.INVISIBLE);
  }

  private void downloadBook(String ean) {
    Log.d(LOG_TAG, "downloadBook");
    // Once we have an ISBN, start a book intent
    CharSequence msg = getString(R.string.downloading) + ": " + ean;
    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    Intent bookIntent = new Intent(getContext(), BookService.class);
    bookIntent.putExtra(BookService.EAN, ean);
    bookIntent.setAction(BookService.FETCH_BOOK);
    getActivity().startService(bookIntent);
    restartLoader();
  }

  private void restartLoader() {
    Log.d(LOG_TAG, "restartLoader");
    getLoaderManager().restartLoader(LOADER_ID, null, this);
  }

  private void setErrorMessage(@BookService.ServerStatus int status) {

    int message = R.string.no_book_info;

    if (!Utility.isNetworkConnected(getActivity())) {

      message = R.string.no_book_info_no_internet_access;

    } else {

      switch (status) {
        case BookService.SERVER_STATUS_SERVER_DOWN:
          message = R.string.no_book_info_server_down;
          break;
        case BookService.SERVER_STATUS_SERVER_INVALID:
          message = R.string.no_book_info_server_error;
          break;
      }
    }

    Log.d(LOG_TAG, mNoBookData.getText().toString());
    mNoBookData.setText(message);
    mNoBookData.setVisibility(View.VISIBLE);
  }

}



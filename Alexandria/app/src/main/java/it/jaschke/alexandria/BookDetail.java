package it.jaschke.alexandria;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookDetail extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

  public static final String EAN_KEY = "EAN";
  private final int LOADER_ID = 10;
  private View rootView;
  private String ean;
  private String bookTitle;
  private ShareActionProvider shareActionProvider;

  // Butterknife bindings
  @Bind(R.id.fullBookTitle) TextView mFullBookTitle;
  @Bind(R.id.fullBookSubTitle) TextView mFullBookSubTitle;
  @Bind(R.id.fullBookDesc) TextView mFullBookDesc;
  @Bind(R.id.authors) TextView mAuthorsView;
  @Bind(R.id.categories) TextView mCategories;
  @Bind(R.id.fullBookCover) ImageView mBookCover;

  public BookDetail() {
    // empty
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public android.support.v4.content.Loader<Cursor> onCreateLoader(int id,
                                                                  Bundle args) {
    return new CursorLoader(
      getActivity(),
      AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(ean)),
      null,
      null,
      null,
      null
    );
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.book_detail, menu);

    MenuItem menuItem = menu.findItem(R.id.action_share);
    shareActionProvider =
        (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
  }

  @Override
  public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    Bundle arguments = getArguments();
    if (arguments != null) {
      ean = arguments.getString(BookDetail.EAN_KEY);
      getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    rootView = inflater.inflate(R.layout.fragment_full_book, container, false);
    ButterKnife.bind(this, rootView);
    rootView.findViewById(R.id.delete_button).setOnClickListener(
        new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent bookIntent = new Intent(getActivity(), BookService.class);
        bookIntent.putExtra(BookService.EAN, ean);
        bookIntent.setAction(BookService.DELETE_BOOK);
        getActivity().startService(bookIntent);
        getActivity().getSupportFragmentManager().popBackStack();
      }
    });
    return rootView;
  }


  @Override
  public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader,
                             Cursor data) {
    if (!data.moveToFirst()) {
      return;
    }

    // book title
    bookTitle =
        data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
    mFullBookTitle.setText(bookTitle);

    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + bookTitle);
    shareActionProvider.setShareIntent(shareIntent);

    // book sub-title
    String bookSubTitle = data.getString(
        data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE)
    );
    mFullBookSubTitle.setText(bookSubTitle);

    // book description
    String desc = data.getString(
        data.getColumnIndex(AlexandriaContract.BookEntry.DESC)
    );
    mFullBookDesc.setText(desc);

    // book authors
    String authors = data.getString(
        data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR)
    );
    String[] authorsArr = authors.split(",");
    mAuthorsView.setLines(authorsArr.length);
    mAuthorsView.setText(authors.replace(",", "\n"));

    // book cover
    String imgUrl = data.getString(
        data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL)
    );

    Picasso.with(getContext())
        .load(imgUrl)
        .error(R.drawable.no_poster_available)
        .into(mBookCover);
    mBookCover.setVisibility(View.VISIBLE);

    String categories = data.getString(
        data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY)
    );
    mCategories.setText(categories);

    if (rootView.findViewById(R.id.right_container) != null) {
      rootView.findViewById(R.id.backButton).setVisibility(View.INVISIBLE);
    }

  }

  @Override
  public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
    // empty
  }

  @Override
  public void onPause() {
    super.onDestroyView();
    if (MainActivity.IS_TABLET &&
        rootView.findViewById(R.id.right_container) == null) {
      getActivity().getSupportFragmentManager().popBackStack();
    }
  }
}
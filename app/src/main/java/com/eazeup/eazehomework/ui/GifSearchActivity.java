package com.eazeup.eazehomework.ui;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.eazeup.eazehomework.R;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class GifSearchActivity extends AbstractGifGridActivity {

    private static final String TAG = "GifSearchActivity";

    @BindView(R.id.search_view)
    SearchView searchView;

    private TextView noResults;

    @Override
    int getLayoutRes() {
        return R.layout.activity_search;
    }

    @Override
    String getToolbarTitle() {
        return "Search";
    }

    @Override
    void onLoadMoreItems(int offset) {
        String searchQuery = searchView.getQuery().toString();
        if (TextUtils.isEmpty(searchQuery)) {
            Log.d(TAG, "nothing in the search box. ignore");
            return;
        }
        performSearch(searchView.getQuery().toString(), offset);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupSearchView();
    }

    private void setupSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        // hint, inputType & ime options seem to be ignored from XML! Set in code
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_ACTION_SEARCH | EditorInfo.IME_FLAG_NO_EXTRACT_UI
            | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query, mGifAdapter.getItemCount());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (TextUtils.isEmpty(query)) {
                    mGifAdapter.clear();
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
                return true;
        }
        return false;
    }

    @OnClick(R.id.searchback)
    protected void dismiss() {
        // clear the background else the touch ripple moves with the translation which looks bad
        //        searchBack.setBackground(null);
        finishAfterTransition();
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }

    void performSearch(String searchText, int currentGifCount) {
        if (TextUtils.isEmpty(searchText)) {
            Toast.makeText(this, "Cannot have an empty search query!", Toast.LENGTH_LONG).show();
            return;
        }
        setKeyboardVisible(false);
        setNoResultsVisibility(View.GONE);
        searchView.clearFocus();

        //@formatter:off
        mCompositeDisposable.add(mNetworkManager.getGifsForQuery(searchText, currentGifCount)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(subscription-> {
                Log.d(TAG, "getGifsForQuery() onSubscribe "+searchText+" : pageStart = " + currentGifCount);
                setLoadingState(LoadingState.LOADING);
            }).doOnNext(gifs-> setNoResultsVisibility((mGifAdapter.getItemCount() < 1 && gifs.isEmpty())? View.VISIBLE: View
                .GONE))
            .subscribeWith(gifReceivedObserver()));

    }
    void setNoResultsVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            if (noResults == null) {
                noResults = (TextView) ((ViewStub)
                    findViewById(R.id.stub_no_search_results)).inflate();
                noResults.setOnClickListener(v-> {
                    searchView.setQuery("", false);
                    searchView.requestFocus();
                    setKeyboardVisible(true);
                });
            }
            String message = String.format(
                getString(R.string.no_search_results), searchView.getQuery().toString());
            SpannableStringBuilder ssb = new SpannableStringBuilder(message);
            ssb.setSpan(new StyleSpan(Typeface.ITALIC),
                message.indexOf('“') + 1,
                message.length() - 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            noResults.setText(ssb);
        }
        if (noResults != null) {
            noResults.setVisibility(visibility);
        }
    }

    void setKeyboardVisible(boolean visible){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        IBinder windowToken = searchView.getWindowToken();

        if(visible){
            imm.toggleSoftInputFromWindow(searchView.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
        }else{
            imm.hideSoftInputFromWindow(windowToken, 0);
        }
    }

}

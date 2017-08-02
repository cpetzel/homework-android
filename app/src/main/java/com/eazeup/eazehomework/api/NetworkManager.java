package com.eazeup.eazehomework.api;

import java.util.LinkedList;
import java.util.List;

import android.support.annotation.NonNull;
import android.util.Log;

import com.eazeup.eazehomework.api.model.Gif;
import com.eazeup.eazehomework.api.model.response.DataItem;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Is in charge of wiring up the APi with the observable response.
 *
 * Acts as a wrapper to the {@link GiphyApiInteface}
 */
public class NetworkManager {

    private static final String TAG = "NetworkManager";

    final GiphyApiInteface mGiphyApi;

    public NetworkManager(GiphyApiInteface giphyApi) {
        mGiphyApi = giphyApi;
    }

    /**
     * @param pageStart
     * @return a {@link Flowable} to observe on to get the latest gifs from the
     * {@link GiphyApiInteface#latestTrending(int, int)}  endpoint
     */
    public Flowable<List<Gif>> getTrendingFromApi(int pageStart) {

        //@formatter:off
        return Flowable.just(pageStart)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(integer -> mGiphyApi.latestTrending(integer, 25))
            .doOnError(exception -> Log.e(TAG, "Exception getDataFlowable", exception))
            .map(dataItems-> {
                List<Gif> gifs = new LinkedList<>();
                for(DataItem item : dataItems){
                    gifs.add(Gif.from(item));
                }
                Log.d("getTrendingFromApi", "created " + gifs.size() + " gifs");
                return gifs;
            })
            .share();

        //@formatter:on
    }

    /**
     * Gets {@link Gif}s from the search endpoint
     */
    public Flowable<List<Gif>> getGifsForQuery(@NonNull String query, int pageStart) {

        //@formatter:off
        return Flowable.just(pageStart)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(integer -> mGiphyApi.search(query, integer, 25))
            .doOnError(exception -> Log.e(TAG, "Exception getDataFlowable", exception))
            .map(dataItems-> {
                List<Gif> gifs = new LinkedList<>();
                for(DataItem item : dataItems){
                    gifs.add(Gif.from(item));
                }
                Log.d("getGifsForQuery", "created " + gifs.size() + " gifs");
                return gifs;
            })
            .share();
        //@formatter:on
    }

}

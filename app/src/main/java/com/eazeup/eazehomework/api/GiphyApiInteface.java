package com.eazeup.eazehomework.api;

import java.util.List;

import com.eazeup.eazehomework.api.model.response.DataItem;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * UI facing interface that will be used to load data (without knowing about the underlying implementation {@link Retrofit})
 */
public interface GiphyApiInteface {

    String DATA_PAYLOAD = "data";

    /**
     * Returns an {@link Observable} that will stream a list of DataItems (full response) to observers
     *
     * The {@link DenvelopingConverter} will parse out the list of Giphs from the 'data' payload in the response, so we don't
     * have to do that by hand every time
     *
     * @param offset
     * @param limit
     * @return
     */
    @EnvelopePayload(DATA_PAYLOAD)
    @GET("trending")
    Flowable<List<DataItem>> latestTrending(@Query("offset") int offset, @Query("limit") int limit);

    /**
     * The {@link DenvelopingConverter} will parse out the list of Giphs from the 'data' payload
     *
     * @param offset
     * @param limit
     * @return
     */
    @EnvelopePayload(DATA_PAYLOAD)
    @GET("search")
    Flowable<List<DataItem>> search(@Query("q") String query, @Query("offset") int offset, @Query("limit") int limit);
}

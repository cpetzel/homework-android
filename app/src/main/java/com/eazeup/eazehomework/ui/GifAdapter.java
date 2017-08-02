package com.eazeup.eazehomework.ui;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.eazeup.eazehomework.R;
import com.eazeup.eazehomework.api.model.Gif;
import com.jakewharton.rxrelay2.PublishRelay;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;

public class GifAdapter extends RecyclerView.Adapter<GifAdapter.ViewHolder> {

    /**
     * This relay is basically the on click listener. Streams are much easier to manage when you perform a task on a button click
     */
    PublishRelay<Pair<ViewHolder, Gif>> mGifClickRelay = PublishRelay.create();

    private static final String TAG = "GifAdapter";
    private List<Gif> mGifs;
    private Context mContext;

    public GifAdapter(Context context, List<Gif> gifs) {
        mContext = context;
        mGifs = gifs;
    }

    public void addItems(List<Gif> gifs) {
        //todo dedupe?
        int prevEnd = mGifs.size();
        if (prevEnd == 0) {
            mGifs = gifs;
            notifyDataSetChanged();
        } else {
            mGifs.addAll(gifs);
            notifyItemRangeInserted(prevEnd, gifs.size() - 1);
        }
    }

    public void clear() {
        mGifs.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        Log.d("onViewRecycled", "onViewRecycled - will clear image");
        Glide.with(mContext).clear(holder.image);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View image = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_image, parent, false);
        return new ViewHolder(image);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.bindGif(mGifs.get(position));
    }

    @Override
    public int getItemCount() {
        return mGifs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.gridImageView)
        ImageView image;

        public ViewHolder(View gridView) {
            super(gridView);
            ButterKnife.bind(this, gridView);
            image.setOnClickListener(v -> mGifClickRelay.accept(new Pair<>(this, mGifs.get(getAdapterPosition()))));
        }

        void bindGif(final Gif gif) {

            Log.d(TAG, "Binding view to gif. " + gif);
            final RequestOptions options = new RequestOptions().centerCrop().priority(Priority.IMMEDIATE);

            Glide.with(mContext.getApplicationContext()).asGif().load(gif.getPreviewGifUrl()).apply(options).into(image);

            //preload
            RequestOptions lowPriorityOptions = new RequestOptions().priority(Priority.LOW);
            Glide.with(mContext.getApplicationContext()).download(gif.getImageUrlSmall()).apply(lowPriorityOptions);
            //todo could change to full url
            Glide.with(mContext.getApplicationContext()).download(gif.getDownsizeMediumGifUrl()).apply(lowPriorityOptions);
        }
    }

    /**
     * @return the {@link PublishRelay} for hooking up a stream of clicks
     */
    Observable<Pair<ViewHolder, Gif>> getGifClickObservable() {
        return mGifClickRelay;
    }

}

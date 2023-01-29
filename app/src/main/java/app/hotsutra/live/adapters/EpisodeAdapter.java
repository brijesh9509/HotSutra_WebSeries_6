package app.hotsutra.live.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import app.hotsutra.live.DetailsActivity;
import app.hotsutra.live.R;
import app.hotsutra.live.models.EpiModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.OriginalViewHolder> {

    private final List<EpiModel> items;
    private final Context ctx;
    final EpisodeAdapter.OriginalViewHolder[] viewHolderArray = {null};
    private OnTVSeriesEpisodeItemClickListener mOnTVSeriesEpisodeItemClickListener;
    EpisodeAdapter.OriginalViewHolder viewHolder;

    public interface OnTVSeriesEpisodeItemClickListener {
        void onEpisodeItemClickTvSeries(String type, View view, EpiModel obj, int position, OriginalViewHolder holder);
    }

    public void setOnEmbedItemClickListener(OnTVSeriesEpisodeItemClickListener mItemClickListener) {
        this.mOnTVSeriesEpisodeItemClickListener = mItemClickListener;
    }

    public EpisodeAdapter(Context context, List<EpiModel> items) {
        this.items = items;
        ctx = context;
    }


    @NonNull
    @Override
    public EpisodeAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        EpisodeAdapter.OriginalViewHolder vh;
        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_episode_item, parent, false);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_episode_item_vertical, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final EpisodeAdapter.OriginalViewHolder holder, final int position) {

        final EpiModel obj = items.get(position);
        holder.name.setText(obj.getEpi());
        holder.seasonName.setText("Season: " + obj.getSeson());
        //holder.publishDate.setText(obj.);

        //check if isDark or not.
        //if not dark, change the text color
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("push", MODE_PRIVATE);
        boolean isDark = sharedPreferences.getBoolean("dark", false);
        if (!isDark){
            holder.name.setTextColor(ctx.getResources().getColor(R.color.black));
            holder.seasonName.setTextColor(ctx.getResources().getColor(R.color.black));
            holder.publishDate.setTextColor(ctx.getResources().getColor(R.color.black));
        }

        Picasso.get()
                .load(obj.getImageUrl())
                .placeholder(R.drawable.poster_placeholder)
                .into(holder.episodeIv);


        holder.cardView.setOnClickListener(v -> {
            ((DetailsActivity)ctx).hideDescriptionLayout();
            ((DetailsActivity)ctx).showSeriesLayout();
            ((DetailsActivity)ctx).setMediaUrlForTvSeries(obj.getStreamURL(), obj.getSeson(), obj.getEpi());
            boolean castSession = ((DetailsActivity)ctx).getCastSession();
            //Toast.makeText(ctx, "cast:"+castSession, Toast.LENGTH_SHORT).show();
            if (!castSession) {
                if (obj.getServerType().equalsIgnoreCase("embed")){
                    if (mOnTVSeriesEpisodeItemClickListener != null){
                        mOnTVSeriesEpisodeItemClickListener.onEpisodeItemClickTvSeries("embed", v, obj, position, viewHolder);
                    }
                }else {
                    //new DetailsActivity().initMoviePlayer(obj.getStreamURL(), obj.getServerType(), ctx);
                    if (mOnTVSeriesEpisodeItemClickListener != null){
                        mOnTVSeriesEpisodeItemClickListener.onEpisodeItemClickTvSeries("normal", v, obj, position, viewHolder);
                    }
                }
            } else {
                ((DetailsActivity)ctx).showQueuePopup(ctx, holder.cardView, ((DetailsActivity)ctx).getMediaInfo());

            }

            changeColor(viewHolderArray[0]);
            holder.name.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
            holder.playStatusTv.setText("Playing");
            holder.playStatusTv.setVisibility(View.VISIBLE);


            viewHolderArray[0] =holder;
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView name, playStatusTv , seasonName, publishDate;
        public MaterialRippleLayout cardView;
        public ImageView episodeIv;

        public OriginalViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            playStatusTv = v.findViewById(R.id.play_status_tv);
            cardView=v.findViewById(R.id.lyt_parent);
            episodeIv =v.findViewById(R.id.image);
            seasonName = v.findViewById(R.id.season_name);
            publishDate = v.findViewById(R.id.publish_date);
        }
    }

    private void changeColor(EpisodeAdapter.OriginalViewHolder holder){

        if (holder!=null){
            holder.name.setTextColor(ctx.getResources().getColor(R.color.grey_20));
            holder.playStatusTv.setVisibility(View.GONE);
        }
    }


}
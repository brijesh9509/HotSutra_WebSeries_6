package app.hotsutra.live.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import app.hotsutra.live.database.continueWatching.ContinueWatchingModel;
import app.hotsutra.live.DetailsActivity;
import app.hotsutra.live.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static app.hotsutra.live.utils.Constants.CATEGORY_TYPE;
import static app.hotsutra.live.utils.Constants.CONTENT_ID;
import static app.hotsutra.live.utils.Constants.CONTENT_TITLE;
import static app.hotsutra.live.utils.Constants.IMAGE_URL;
import static app.hotsutra.live.utils.Constants.IS_FROM_CONTINUE_WATCHING;
import static app.hotsutra.live.utils.Constants.POSITION;
import static app.hotsutra.live.utils.Constants.SERVER_TYPE;
import static app.hotsutra.live.utils.Constants.STREAM_URL;

public class ContinueWatchingAdapter extends RecyclerView.Adapter<ContinueWatchingAdapter.ContinueWatchingViewHolder> {

    private final Context context;
    private List<ContinueWatchingModel> list = new ArrayList<>();

    public ContinueWatchingAdapter(Context context, List<ContinueWatchingModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ContinueWatchingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_continue_watching, parent, false);
        return new ContinueWatchingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContinueWatchingViewHolder holder, int position) {
        final ContinueWatchingModel model = list.get(position);
        if (model != null){
            holder.title.setText(model.getName());
            holder.progressBar.setProgress((int) model.getProgress());
            Picasso.get().load(model.getImgUrl()).placeholder(R.drawable.poster_placeholder).into(holder.posterIV);
            holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra(CONTENT_ID, model.getContentId());
                    intent.putExtra(CONTENT_TITLE, model.getName());
                    intent.putExtra(IMAGE_URL, model.getImgUrl());
                    intent.putExtra(STREAM_URL, model.getStreamUrl());
                    intent.putExtra(SERVER_TYPE, model.getvType());
                    intent.putExtra(CATEGORY_TYPE, model.getType());
                    intent.putExtra(POSITION, model.getPosition());
                    intent.putExtra(IS_FROM_CONTINUE_WATCHING, true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ContinueWatchingViewHolder extends RecyclerView.ViewHolder{
        private final TextView title;
        private final ImageView posterIV;
        private final ProgressBar progressBar;
        private final MaterialRippleLayout lyt_parent;

        public ContinueWatchingViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.name);
            title.setSelected(true);
            posterIV = itemView.findViewById(R.id.image);
            progressBar = itemView.findViewById(R.id.progressBar);
            lyt_parent = itemView.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

package app.hotsutra.live.adapters;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.hotsutra.live.R;
import app.hotsutra.live.models.NavigationModel;

import java.util.List;

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.OriginalViewHolder> {

    private final List<NavigationModel> items;
    private final Context ctx;
    private OnItemClickListener mOnItemClickListener;
    NavigationAdapter.OriginalViewHolder viewHolder;
    private final String menuStyle;

    public interface OnItemClickListener {
        void onItemClick(View view, NavigationModel obj, int position, OriginalViewHolder holder);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public NavigationAdapter(Context context, List<NavigationModel> items, String menuStyle) {
        this.items = items;
        this.ctx = context;
        this.menuStyle = menuStyle;
    }

    @NonNull
    @Override
    public NavigationAdapter.OriginalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NavigationAdapter.OriginalViewHolder vh;
        View v;
        if (menuStyle.equals("grid")) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_nav_view, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_nav_view_2, parent, false);
        }
        vh = new NavigationAdapter.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final NavigationAdapter.OriginalViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        NavigationModel obj = items.get(position);

        if (position == 0) {
            viewHolder = holder;
            if (menuStyle.equals("grid")) {
                holder.cardView.setCardBackgroundColor(ctx.getResources().getColor(R.color.colorPrimary));
                holder.name.setTextColor(ctx.getResources().getColor(R.color.white));
            } else {
                holder.selectedLayout.setBackgroundResource(R.drawable.round_grey_transparent);
                holder.name.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
            }
        }
        holder.name.setText(obj.getTitle());

        switch (obj.getTitle()){
            case "Home":
                holder.image.setImageResource(R.drawable.outline_home_24);
                break;
            case "Movies":
                holder.image.setImageResource(R.drawable.outline_movie_24);
                break;
            case "Series":
                holder.image.setImageResource(R.drawable.outline_local_movies_24);
                break;
            case "Live TV":
                holder.image.setImageResource(R.drawable.outline_live_tv_24);
                break;
            case "Genre":
                holder.image.setImageResource(R.drawable.outline_folder_24);
                break;
            case "Country":
                holder.image.setImageResource(R.drawable.outline_outlined_flag_24);
                break;
            case "Profile":
                holder.image.setImageResource(R.drawable.outline_person_24);
                break;
            case "Favorite":
                holder.image.setImageResource(R.drawable.outline_favorite_border_24);
                break;
            case "Subscription":
                holder.image.setImageResource(R.drawable.ic_subscriptions_black_24dp);
                break;
            case "Downloads":
                holder.image.setImageResource(R.drawable.ic_file_download_black_24dp);
                break;
            case "Settings":
                holder.image.setImageResource(R.drawable.outline_settings_24);
                break;
            case "Login":
            case "Sign Out":
            default:
                holder.image.setImageResource(R.drawable.outline_lock_24);
                break;
        }

        if (menuStyle.equals("grid")) {
            holder.cardView.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, items.get(position), position, holder);
                }
            });
        } else {
            holder.itemLayout.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, items.get(position), position, holder);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView name;
        public CardView cardView;
        public LinearLayout itemLayout;
        public LinearLayout selectedLayout;

        public OriginalViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.image);
            name = v.findViewById(R.id.name);

            if (menuStyle.equals("grid")) {
                cardView = v.findViewById(R.id.card_view_home);
            } else {
                itemLayout = v.findViewById(R.id.item_layout);
                selectedLayout = v.findViewById(R.id.selected_layout);
            }
        }
    }

    public void chanColor(NavigationAdapter.OriginalViewHolder holder, int pos, int bgColor) {

        if (pos != 0) {
            if (menuStyle.equals("grid")) {
                viewHolder.cardView.setCardBackgroundColor(ctx.getResources().getColor(bgColor));
                viewHolder.name.setTextColor(ctx.getResources().getColor(R.color.grey_60));
            } else {
                viewHolder.name.setTextColor(ctx.getResources().getColor(R.color.default_text));
                viewHolder.selectedLayout.setBackgroundColor(ctx.getResources()
                        .getColor(android.R.color.transparent));
            }
        }

        if (holder != null) {
            if (menuStyle.equals("grid")) {
                holder.cardView.setCardBackgroundColor(ctx.getResources().getColor(bgColor));
                holder.name.setTextColor(ctx.getResources().getColor(R.color.grey_60));
            } else {
                holder.name.setTextColor(ctx.getResources().getColor(R.color.default_text));
                holder.selectedLayout.setBackgroundColor(ctx.getResources()
                        .getColor(android.R.color.transparent));
            }
        }
    }
}

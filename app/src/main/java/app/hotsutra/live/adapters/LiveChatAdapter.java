package app.hotsutra.live.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import app.hotsutra.live.R;
import app.hotsutra.live.models.LiveChat;
import app.hotsutra.live.utils.ItemAnimation;
import de.hdodenhof.circleimageview.CircleImageView;

public class LiveChatAdapter extends RecyclerView.Adapter<LiveChatAdapter.LiveCommentViewHolder> {
    private List<LiveChat> liveChatList;
    private Context context;
    private int lastPosition = -1;
    private boolean on_attach = true;
    private int animation_type = 2;


    public LiveChatAdapter(List<LiveChat> liveChatList, Context context) {
        this.liveChatList = liveChatList;
        this.context = context;
    }

    @NonNull
    @Override
    public LiveCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.live_comment_item, parent, false);
        return new LiveCommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveCommentViewHolder holder, int position) {
        LiveChat liveChat = liveChatList.get(position);
        Glide.with(this.context)
                .load(generateImageUrl(liveChat))
                .placeholder(R.drawable.logo)
                .into(holder.userIV);
        holder.nameTv.setText(generateName(liveChat));
        holder.commentTv.setText(generateComments(liveChat));
    }

    private String generateName(LiveChat liveChat) {
      if (liveChat != null){
          if (liveChat.getUserName() != null){
              return liveChat.getUserName();
          }
      }
        return "";
    }

    private String generateComments(LiveChat liveChat) {
        if (liveChat != null){
            if (!liveChat.getComments().isEmpty()){
                return liveChat.getComments();
            }
        }
        return "";
    }

    private String generateImageUrl(LiveChat liveChat) {
        if (liveChat != null){
            if (liveChat.getUserImageUrl() != null){
                return liveChat.getUserImageUrl();
            }
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return liveChatList.size();
    }

    public static class LiveCommentViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv, commentTv;
        CircleImageView userIV;

        public LiveCommentViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.userNameTv);
            userIV = itemView.findViewById(R.id.user_iv);
            commentTv = itemView.findViewById(R.id.commentTv);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }

        });
        super.onAttachedToRecyclerView(recyclerView);
    }

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }
}

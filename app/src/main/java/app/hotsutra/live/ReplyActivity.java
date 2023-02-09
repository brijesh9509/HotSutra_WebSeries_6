package app.hotsutra.live;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import app.hotsutra.live.adapters.ReplyAdapter;
import app.hotsutra.live.models.GetCommentsModel;
import app.hotsutra.live.models.PostCommentModel;
import app.hotsutra.live.network.RetrofitClient;
import app.hotsutra.live.network.apis.CommentApi;
import app.hotsutra.live.utils.ApiResources;
import app.hotsutra.live.utils.PreferenceUtils;
import app.hotsutra.live.utils.RtlUtils;
import app.hotsutra.live.utils.ToastMsg;
import com.google.firebase.analytics.FirebaseAnalytics;
import app.hotsutra.live.R;

import java.util.ArrayList;
import java.util.List;

import app.hotsutra.live.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ReplyActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText etComment;
    private Button btnComment;

    private final List<GetCommentsModel> list = new ArrayList<>();
    private ReplyAdapter replyAdapter;
    private String strCommentID, strAllReplyURL, videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RtlUtils.setScreenDirection(this);
        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        boolean isDark = sharedPreferences.getBoolean("dark", false);

        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Reply");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //---analytics-----------
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "reply_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        btnComment = findViewById(R.id.btn_comment);
        etComment = findViewById(R.id.et_comment);
        recyclerView = findViewById(R.id.recyclerView);

        if (!isDark) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            etComment.setBackground(getResources().getDrawable(R.drawable.rounded_black_transparent));
            btnComment.setTextColor(getResources().getColor(R.color.grey_20));
            toolbar.setBackgroundColor(getResources().getColor(R.color.black_window_light));

        }


        replyAdapter = new ReplyAdapter(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(replyAdapter);


        strCommentID = getIntent().getStringExtra("commentId");
        videoId = getIntent().getStringExtra("videoId");

        strAllReplyURL = new ApiResources().getGetAllReply() + "&&id=" + strCommentID;


        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etComment.getText().toString().equals("")) {

                    new ToastMsg(ReplyActivity.this).toastIconError(getString(R.string.comment_empty));

                } else {
                    addComment(etComment.getText().toString());
                }
            }
        });

        getComments();
    }


    private void addComment(String comments) {
        String userId = PreferenceUtils.getUserId(ReplyActivity.this);
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        CommentApi api = retrofit.create(CommentApi.class);
        Call<PostCommentModel> call = api.postReply(AppConfig.API_KEY, videoId, userId, comments,
                strCommentID, BuildConfig.VERSION_CODE, Constants.getDeviceId(this));
        call.enqueue(new Callback<PostCommentModel>() {
            @Override
            public void onResponse(Call<PostCommentModel> call, retrofit2.Response<PostCommentModel> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        if (response.body().getStatus().equals("success")) {
                            recyclerView.removeAllViews();
                            list.clear();
                            getComments();
                            etComment.setText("");
                            new ToastMsg(ReplyActivity.this).toastIconSuccess(response.body().getMessage());
                        } else {
                            new ToastMsg(ReplyActivity.this).toastIconError(response.body().getMessage());
                        }
                    }
                } else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    ReplyActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(ReplyActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<PostCommentModel> call, Throwable t) {

            }
        });

    }

    private void getComments() {
        String userId = PreferenceUtils.getUserId(ReplyActivity.this);
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        CommentApi api = retrofit.create(CommentApi.class);
        Call<List<GetCommentsModel>> call = api.getAllReply(AppConfig.API_KEY, strCommentID,
                BuildConfig.VERSION_CODE,userId, Constants.getDeviceId(this));
        call.enqueue(new Callback<List<GetCommentsModel>>() {
            @Override
            public void onResponse(Call<List<GetCommentsModel>> call, retrofit2.Response<List<GetCommentsModel>> response) {
                if (response.code() == 200) {
                    list.addAll(response.body());

                    replyAdapter.notifyDataSetChanged();
                }else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    ReplyActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(ReplyActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<GetCommentsModel>> call, Throwable t) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}

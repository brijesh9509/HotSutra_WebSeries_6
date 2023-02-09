package app.hotsutra.live;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import app.hotsutra.live.R;

import app.hotsutra.live.adapters.LiveTVAdapter;
import app.hotsutra.live.models.CommonModels;
import app.hotsutra.live.network.RetrofitClient;
import app.hotsutra.live.network.apis.LiveTvApi;
import app.hotsutra.live.network.model.Channel;
import app.hotsutra.live.utils.HelperUtils;
import app.hotsutra.live.utils.NetworkInst;
import app.hotsutra.live.utils.PreferenceUtils;
import app.hotsutra.live.utils.RtlUtils;
import app.hotsutra.live.utils.SpacingItemDecoration;
import app.hotsutra.live.utils.Tools;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import app.hotsutra.live.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ItemTVActivity extends AppCompatActivity {

    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerView;
    private LiveTVAdapter mAdapter;
    private final List<CommonModels> list = new ArrayList<>();

    private boolean isLoading = false;
    private ProgressBar progressBar;
    private int pageCount = 1;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoItem;
    private HelperUtils helperUtils;
    private boolean vpnStatus;

    @Override
    protected void onResume() {
        super.onResume();
        //check vpn connection
        helperUtils = new HelperUtils(this);
        vpnStatus = helperUtils.isVpnConnectionAvailable();
        if (vpnStatus) {
            helperUtils.showWarningDialog(this, getString(R.string.vpn_detected), getString(R.string.close_vpn));

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //check vpn connection
        helperUtils = new HelperUtils(this);
        vpnStatus = helperUtils.isVpnConnectionAvailable();
        if (vpnStatus) {
            helperUtils.showWarningDialog(this, getString(R.string.vpn_detected), getString(R.string.close_vpn));

        }
    }

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
        setContentView(R.layout.activity_item_tv);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //check vpn connection
        helperUtils = new HelperUtils(this);
        vpnStatus = helperUtils.isVpnConnectionAvailable();
        if (vpnStatus) {
            helperUtils.showWarningDialog(this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
            return;
        }
        if (!isDark) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.black_window_light));
        }
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //---analytics-----------
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "tv_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        progressBar = findViewById(R.id.item_progress_bar);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();
        coordinatorLayout = findViewById(R.id.coordinator_lyt);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        tvNoItem = findViewById(R.id.tv_noitem);

        //----tv recycler view-----------------
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(this, 12), true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        mAdapter = new LiveTVAdapter(this, list);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && !isLoading) {

                    pageCount = pageCount + 1;
                    isLoading = true;

                    progressBar.setVisibility(View.VISIBLE);

                    getData(pageCount);
                }
            }
        });


        if (new NetworkInst(this).isNetworkAvailable()) {
            getData(pageCount);
        } else {
            tvNoItem.setText(getString(R.string.no_internet));
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            coordinatorLayout.setVisibility(View.VISIBLE);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                coordinatorLayout.setVisibility(View.GONE);
                pageCount = 1;

                list.clear();
                recyclerView.removeAllViews();
                mAdapter.notifyDataSetChanged();

                if (new NetworkInst(ItemTVActivity.this).isNetworkAvailable()) {
                    getData(pageCount);
                } else {
                    tvNoItem.setText(getString(R.string.no_internet));
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    coordinatorLayout.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void getData(int pageNum) {
        String userId = PreferenceUtils.getUserId(this);
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        LiveTvApi api = retrofit.create(LiveTvApi.class);
        Call<List<Channel>> call = api.getFeaturedTV(AppConfig.API_KEY, pageNum,
                BuildConfig.VERSION_CODE, userId, Constants.getDeviceId(this));
        call.enqueue(new Callback<List<Channel>>() {
            @Override
            public void onResponse(Call<List<Channel>> call, retrofit2.Response<List<Channel>> response) {
                if (response.code() == 200) {
                    isLoading = false;
                    progressBar.setVisibility(View.GONE);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    if (response.body().size() == 0 && pageCount == 1) {
                        coordinatorLayout.setVisibility(View.VISIBLE);
                    } else {
                        coordinatorLayout.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < response.body().size(); i++) {
                        Channel channel = response.body().get(i);
                        CommonModels models = new CommonModels();
                        models.setImageUrl(channel.getPosterUrl());
                        models.setTitle(channel.getTvName());
                        models.setVideoType("tv");
                        models.setId(channel.getLiveTvId());
                        list.add(models);
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    isLoading = false;
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    if (pageCount == 1) {
                        coordinatorLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Channel>> call, Throwable t) {
                isLoading = false;
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                if (pageCount == 1) {
                    coordinatorLayout.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

package app.hotsutra.live;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import app.hotsutra.live.adapters.CommonGridAdapter;
import app.hotsutra.live.models.CommonModels;
import app.hotsutra.live.models.home_content.Video;
import app.hotsutra.live.network.RetrofitClient;
import app.hotsutra.live.network.apis.TvSeriesApi;
import app.hotsutra.live.utils.ApiResources;
import app.hotsutra.live.utils.HelperUtils;
import app.hotsutra.live.utils.MyAppClass;
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

public class ItemSeriesActivity extends AppCompatActivity {

    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerView;
    private CommonGridAdapter mAdapter;
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
        setContentView(R.layout.activity_item_series);

        //check vpn connection
        helperUtils = new HelperUtils(this);
        vpnStatus = helperUtils.isVpnConnectionAvailable();
        if (vpnStatus) {
            helperUtils.showWarningDialog(this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
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
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "series_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        progressBar = findViewById(R.id.item_progress_bar);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        coordinatorLayout = findViewById(R.id.coordinator_lyt);
        tvNoItem = findViewById(R.id.tv_noitem);


        //----movie's recycler view-----------------
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(this, 12), true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        mAdapter = new CommonGridAdapter(this, list);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && !isLoading) {

                    pageCount = pageCount + 1;
                    isLoading = true;

                    progressBar.setVisibility(View.VISIBLE);

                    getTvSeriesData(pageCount);
                }
            }
        });

        if (new NetworkInst(this).isNetworkAvailable()) {
            getTvSeriesData(pageCount);
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

                if (new NetworkInst(ItemSeriesActivity.this).isNetworkAvailable()) {
                    getTvSeriesData(pageCount);
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

    private void getTvSeriesData(int pageNum) {
        String userId = PreferenceUtils.getUserId(this);
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        TvSeriesApi api = retrofit.create(TvSeriesApi.class);
        Call<List<Video>> call = api.getTvSeries(MyAppClass.API_KEY, pageNum,
                BuildConfig.VERSION_CODE,userId, Constants.getDeviceId(this));
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, retrofit2.Response<List<Video>> response) {
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
                        Video video = response.body().get(i);
                        CommonModels models = new CommonModels();
                        models.setImageUrl(video.getThumbnailUrl());
                        models.setTitle(video.getTitle());
                        models.setVideoType("tvseries");
                        models.setReleaseDate(video.getRelease());
                        models.setQuality(video.getVideoQuality());
                        models.setId(video.getVideosId());
                        list.add(models);
                    }
                    mAdapter.notifyDataSetChanged();

                } else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    ItemSeriesActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(ItemSeriesActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }else {
                    isLoading = false;
                    progressBar.setVisibility(View.GONE);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    if (pageCount == 1) {
                        coordinatorLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
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

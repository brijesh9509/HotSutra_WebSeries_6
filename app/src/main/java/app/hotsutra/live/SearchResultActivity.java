package app.hotsutra.live;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import app.hotsutra.live.adapters.LiveTvAdapter2;
import app.hotsutra.live.adapters.SearchAdapter;
import app.hotsutra.live.network.RetrofitClient;
import app.hotsutra.live.network.apis.SearchApi;
import app.hotsutra.live.network.model.CommonModel;
import app.hotsutra.live.network.model.SearchModel;
import app.hotsutra.live.network.model.TvModel;
import app.hotsutra.live.utils.ApiResources;
import app.hotsutra.live.utils.PreferenceUtils;
import app.hotsutra.live.utils.RtlUtils;
import app.hotsutra.live.utils.ToastMsg;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import app.hotsutra.live.R;

import java.util.ArrayList;
import java.util.List;

import app.hotsutra.live.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class SearchResultActivity extends AppCompatActivity implements SearchAdapter.OnItemClickListener {
    private TextView tvTitle, movieTitle, tvSeriesTv, searchQueryTv;
    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView movieRv, tvRv, tvSeriesRv;
    private SearchAdapter movieAdapter, tvSeriesAdapter;
    private LiveTvAdapter2 tvAdapter;
    private final List<CommonModel> movieList = new ArrayList<>();
    private final List<TvModel> tvList = new ArrayList<>();
    private final List<CommonModel> tvSeriesList = new ArrayList<>();

    private ApiResources apiResources;

    private String URL = null;
    private final boolean isLoading = false;
    private ProgressBar progressBar;
    private final int pageCount = 1;
    private LinearLayout movieLayout, tvSeriesLayout, tvLayout;
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;

    private String type;
    private String query = "";
    private int range_to, range_from, tvCategoryId, genreId, countryId;

    @SuppressLint("SetTextI18n")
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
        setContentView(R.layout.activity_search_result);

        //---analytics-----------
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "search_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        query = getIntent().getStringExtra("q");
        type = getIntent().getStringExtra("type");
        range_to = getIntent().getIntExtra("range_to", 0);
        range_from = getIntent().getIntExtra("range_from", 0);
        tvCategoryId = getIntent().getIntExtra("tv_category_id", 0);
        genreId = getIntent().getIntExtra("genre_id", 0);
        countryId = getIntent().getIntExtra("country_id", 0);

        tvTitle = findViewById(R.id.title);
        tvLayout = findViewById(R.id.tv_layout);
        movieLayout = findViewById(R.id.movie_layout);
        tvSeriesLayout = findViewById(R.id.tv_series_layout);
        tvTitle = findViewById(R.id.tv_title);
        movieTitle = findViewById(R.id.movie_title);
        tvSeriesTv = findViewById(R.id.tv_series_title);
        movieRv = findViewById(R.id.movie_rv);
        tvRv = findViewById(R.id.tv_rv);
        tvSeriesRv = findViewById(R.id.tv_series_rv);
        searchQueryTv = findViewById(R.id.title_tv);
        toolbar = findViewById(R.id.toolbar);

        searchQueryTv.setText("Showing Result for : " + query);

        progressBar = findViewById(R.id.item_progress_bar);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search Result");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (isDark) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.black_window_light));
            toolbar.setTitleTextColor(Color.WHITE);
        }

        URL = new ApiResources().getSearchUrl() + "&&q=" + query + "&&page=";

        coordinatorLayout = findViewById(R.id.coordinator_lyt);
        movieRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //movieRv.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(this, 4), true));
        movieRv.setHasFixedSize(true);
        movieAdapter = new SearchAdapter(movieList, this);
        movieAdapter.setOnItemClickListener(this);
        movieRv.setAdapter(movieAdapter);


        tvRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //tvRv.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(this, 8), true));
        tvRv.setHasFixedSize(true);
        tvAdapter = new LiveTvAdapter2(this, tvList);
        tvRv.setAdapter(tvAdapter);

        tvSeriesRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //tvSeriesRv.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(this, 4), true));
        tvSeriesRv.setHasFixedSize(true);
        tvSeriesAdapter = new SearchAdapter(tvSeriesList, this);
        tvSeriesAdapter.setOnItemClickListener(this);
        tvSeriesRv.setAdapter(tvSeriesAdapter);

        if (query != null) {
            getSearchData();
        }
    }


    public void getSearchData() {
        String userId = PreferenceUtils.getUserId(this);
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SearchApi searchApi = retrofit.create(SearchApi.class);
        Call<SearchModel> call = searchApi.getSearchData(AppConfig.API_KEY, query, type, range_to, range_from,
                tvCategoryId, genreId, countryId, BuildConfig.VERSION_CODE, userId, Constants.getDeviceId(this));
        call.enqueue(new Callback<SearchModel>() {
            @Override
            public void onResponse(Call<SearchModel> call, retrofit2.Response<SearchModel> response) {
                progressBar.setVisibility(View.GONE);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);

                if (response.code() == 200) {

                    SearchModel searchModel = response.body();

                    movieList.addAll(searchModel.getMovie());
                    tvList.addAll(searchModel.getTvChannels());
                    tvSeriesList.addAll(searchModel.getTvseries());

                    if (movieList.size() > 0) {
                        movieAdapter.notifyDataSetChanged();
                    } else {
                        movieLayout.setVisibility(View.GONE);
                    }

                    if (tvList.size() > 0) {
                        tvAdapter.notifyDataSetChanged();
                    } else {
                        tvLayout.setVisibility(View.GONE);
                    }

                    if (tvSeriesList.size() > 0) {
                        tvSeriesAdapter.notifyDataSetChanged();
                    } else {
                        tvSeriesLayout.setVisibility(View.GONE);
                    }

                    if (tvList.size() == 0 && movieList.size() == 0 && tvSeriesList.size() == 0) {
                        coordinatorLayout.setVisibility(View.VISIBLE);
                    }


                } else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    SearchResultActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(SearchResultActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }  else {
                    new ToastMsg(SearchResultActivity.this).toastIconSuccess("Something went wrong.");
                    coordinatorLayout.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<SearchModel> call, Throwable t) {

                progressBar.setVisibility(View.GONE);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                coordinatorLayout.setVisibility(View.VISIBLE);
                t.printStackTrace();
                new ToastMsg(SearchResultActivity.this).toastIconSuccess("Something went wrong.");
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

    @Override
    public void onItemClick(CommonModel commonModel) {

        String type = "";
        if (commonModel.getIsTvseries().equals("1")) {
            type = "tvseries";
        } else {
            type = "movie";
        }

        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("vType", type);
        intent.putExtra("id", commonModel.getVideosId());
        startActivity(intent);

    }


}

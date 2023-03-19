package app.hotsutra.live.fragments;


import static app.hotsutra.live.utils.Constants.getDeviceId;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import app.hotsutra.live.BuildConfig;
import app.hotsutra.live.adapters.ContinueWatchingAdapter;
import app.hotsutra.live.adapters.CountryAdapter;
import app.hotsutra.live.adapters.GenreAdapter;
import app.hotsutra.live.adapters.GenreHomeAdapter;
import app.hotsutra.live.adapters.HomePageAdapter;
import app.hotsutra.live.adapters.LiveTvHomeAdapter;
import app.hotsutra.live.adapters.PopularStarAdapter;
import app.hotsutra.live.adapters.SliderAdapter;
import app.hotsutra.live.database.DatabaseHelper;
import app.hotsutra.live.database.continueWatching.ContinueWatchingViewModel;
import app.hotsutra.live.database.homeContent.HomeContentViewModel;
import app.hotsutra.live.network.apis.HomeContentApi;
import app.hotsutra.live.utils.ApiResources;
import app.hotsutra.live.utils.MyAppClass;
import app.hotsutra.live.utils.PreferenceUtils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.islamkhsh.CardSliderViewPager;
import app.hotsutra.live.ItemMovieActivity;
import app.hotsutra.live.ItemSeriesActivity;
import app.hotsutra.live.ItemTVActivity;
import app.hotsutra.live.MainActivity;
import app.hotsutra.live.R;
import app.hotsutra.live.models.home_content.PopularStars;
import app.hotsutra.live.models.home_content.Slider;
import app.hotsutra.live.models.CommonModels;
import app.hotsutra.live.models.GenreModel;
import app.hotsutra.live.models.home_content.AllCountry;
import app.hotsutra.live.models.home_content.AllGenre;
import app.hotsutra.live.models.home_content.FeaturedTvChannel;
import app.hotsutra.live.models.home_content.FeaturesGenreAndMovie;
import app.hotsutra.live.models.home_content.HomeContent;
import app.hotsutra.live.models.home_content.LatestMovie;
import app.hotsutra.live.models.home_content.LatestTvseries;
import app.hotsutra.live.models.home_content.Video;
import app.hotsutra.live.network.RetrofitClient;
import app.hotsutra.live.utils.NetworkInst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment {

    CardSliderViewPager cViewPager;
    private final ArrayList<CommonModels> listSlider = new ArrayList<>();
    private Timer timer;

    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerViewMovie, recyclerViewTv, recyclerViewTvSeries, recyclerViewGenre, recyclerViewContinueWatching;
    private LinearLayout continueWatchingLayout;
    private RecyclerView genreRv;
    private RecyclerView countryRv;
    private RecyclerView popularStarsRv;
    private GenreAdapter genreAdapter;
    private CountryAdapter countryAdapter;
    private PopularStarAdapter popularStarAdapter;
    private HomePageAdapter adapterMovie, adapterSeries;
    private LiveTvHomeAdapter adapterTv;
    private final List<CommonModels> listMovie = new ArrayList<>();
    private final List<CommonModels> listTv = new ArrayList<>();
    private final List<CommonModels> listSeries = new ArrayList<>();
    private final List<CommonModels> genreList = new ArrayList<>();
    private final List<CommonModels> countryList = new ArrayList<>();
    private final List<PopularStars> popularStarsList = new ArrayList<>();
    private Button btnMoreMovie, btnMoreTv, btnMoreSeries, btnContinueWatchingClear;

    private TextView tvNoItem;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NestedScrollView scrollView;

    private final List<GenreModel> listGenre = new ArrayList<>();

    private GenreHomeAdapter genreHomeAdapter;
    private View sliderLayout;

    private MainActivity activity;
    private LinearLayout searchRootLayout;

    private ImageView menuIv, searchIv;
    private DatabaseHelper db = new DatabaseHelper(getContext());
    private HomeContentViewModel homeContentViewModel;
    private HomeContent homeContent = null;
    private RelativeLayout popular_stars_layout, featuredTVLayout, movieLayout, tvSeriesLayout;
    private ContinueWatchingViewModel continueWatchingViewModel;


    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        activity = (MainActivity) getActivity();

        return inflater.inflate(R.layout.fragment_home, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DatabaseHelper(getContext());

        btnMoreSeries = view.findViewById(R.id.btn_more_series);
        btnMoreTv = view.findViewById(R.id.btn_more_tv);
        btnMoreMovie = view.findViewById(R.id.btn_more_movie);
        btnContinueWatchingClear = view.findViewById(R.id.continue_watching_clear_btn);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        tvNoItem = view.findViewById(R.id.tv_noitem);
        coordinatorLayout = view.findViewById(R.id.coordinator_lyt);
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        scrollView = view.findViewById(R.id.scrollView);
        sliderLayout = view.findViewById(R.id.slider_layout);
        genreRv = view.findViewById(R.id.genre_rv);
        countryRv = view.findViewById(R.id.country_rv);
        RelativeLayout genreLayout = view.findViewById(R.id.genre_layout);
        popularStarsRv = view.findViewById(R.id.popular_stars_rv);
        RelativeLayout countryLayout = view.findViewById(R.id.country_layout);
        cViewPager = view.findViewById(R.id.c_viewPager);
        searchRootLayout = view.findViewById(R.id.search_root_layout);
        CardView searchBar = view.findViewById(R.id.search_bar);
        menuIv = view.findViewById(R.id.bt_menu);
        TextView pageTitle = view.findViewById(R.id.page_title_tv);
        searchIv = view.findViewById(R.id.search_iv);
        continueWatchingLayout = view.findViewById(R.id.continueWatchingLayout);
        recyclerViewContinueWatching = view.findViewById(R.id.recyclerViewContinueWatching);
        popular_stars_layout = view.findViewById(R.id.popular_stars_layout);
        featuredTVLayout = view.findViewById(R.id.featuredTvLayout);
        movieLayout = view.findViewById(R.id.movieLayout);
        tvSeriesLayout = view.findViewById(R.id.tvSeriesLayout);

        if (db.getConfigurationData().getAppConfig().getGenreVisible()) {
            genreLayout.setVisibility(View.VISIBLE);
        }
        if (db.getConfigurationData().getAppConfig().getCountryVisible()) {
            countryLayout.setVisibility(View.VISIBLE);
        }

        pageTitle.setText(getResources().getString(R.string.home));

        if (activity.isDark) {
            pageTitle.setTextColor(activity.getResources().getColor(R.color.white));
            searchBar.setCardBackgroundColor(activity.getResources().getColor(R.color.black_window_light));
            menuIv.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_menu));
            searchIv.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_search_white));
        }

        //----init timer slider--------------------
        timer = new Timer();

        //----btn click-------------
        btnClick();

        /*----continue Watching view----*/
        continueWatchingViewModel = ViewModelProviders.of(getActivity()).get(ContinueWatchingViewModel.class);
        continueWatchingViewModel.getAllContents().observe(getActivity(), items -> {
            if (items.size() > 0) {
                Collections.reverse(items);
                continueWatchingLayout.setVisibility(View.VISIBLE);
                ContinueWatchingAdapter adapter = new ContinueWatchingAdapter(getContext(), items);
                recyclerViewContinueWatching.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
                recyclerViewContinueWatching.setHasFixedSize(true);
                recyclerViewContinueWatching.setNestedScrollingEnabled(false);
                recyclerViewContinueWatching.setAdapter(adapter);

            } else {
                recyclerViewContinueWatching.removeAllViews();
                continueWatchingLayout.setVisibility(View.GONE);
            }
        });

        // --- genre recycler view ---------
        genreRv.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        genreRv.setHasFixedSize(true);
        genreRv.setNestedScrollingEnabled(false);
        genreAdapter = new GenreAdapter(getActivity(), genreList, "genre", "home");
        genreRv.setAdapter(genreAdapter);

        // --- country recycler view ---------
        countryRv.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        countryRv.setHasFixedSize(true);
        countryRv.setNestedScrollingEnabled(false);
        countryAdapter = new CountryAdapter(getActivity(), countryList, "home");
        countryRv.setAdapter(countryAdapter);

        // --- popular stars recycler view ---------
        popularStarsRv.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        popularStarsRv.setHasFixedSize(true);
        popularStarsRv.setNestedScrollingEnabled(false);
        popularStarAdapter = new PopularStarAdapter(activity, popularStarsList);
        popularStarsRv.setAdapter(popularStarAdapter);

        //----featured tv recycler view-----------------
        recyclerViewTv = view.findViewById(R.id.recyclerViewTv);
        recyclerViewTv.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTv.setHasFixedSize(true);
        recyclerViewTv.setNestedScrollingEnabled(false);
        adapterTv = new LiveTvHomeAdapter(getActivity(), listTv, "MainActivity");
        recyclerViewTv.setAdapter(adapterTv);

        //----movie's recycler view-----------------
        recyclerViewMovie = view.findViewById(R.id.recyclerView);
        recyclerViewMovie.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMovie.setHasFixedSize(true);
        recyclerViewMovie.setNestedScrollingEnabled(false);
        adapterMovie = new HomePageAdapter(getContext(), listMovie);
        recyclerViewMovie.setAdapter(adapterMovie);

        //----series's recycler view-----------------
        recyclerViewTvSeries = view.findViewById(R.id.recyclerViewTvSeries);
        recyclerViewTvSeries.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTvSeries.setHasFixedSize(true);
        recyclerViewTvSeries.setNestedScrollingEnabled(false);
        adapterSeries = new HomePageAdapter(getActivity(), listSeries);
        recyclerViewTvSeries.setAdapter(adapterSeries);

        //----genre's recycler view--------------------
        recyclerViewGenre = view.findViewById(R.id.recyclerView_by_genre);
        recyclerViewGenre.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewGenre.setHasFixedSize(true);
        recyclerViewGenre.setNestedScrollingEnabled(false);
        genreHomeAdapter = new GenreHomeAdapter(getContext(), listGenre);
        recyclerViewGenre.setAdapter(genreHomeAdapter);

        shimmerFrameLayout.startShimmer();

        //home content live data
        homeContentViewModel = new ViewModelProvider(getActivity()).get(HomeContentViewModel.class);
        homeContentViewModel.getAllContents().observe(getActivity(), data -> {
            if (data != null) {
                //populate screen with data
                homeContent = data;
                populateViews();
                Log.e("HomeContentDatabase", "onChanged");
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            recyclerViewMovie.removeAllViews();
            recyclerViewTv.removeAllViews();
            recyclerViewTvSeries.removeAllViews();
            recyclerViewGenre.removeAllViews();
            genreRv.removeAllViews();
            countryRv.removeAllViews();
            popularStarsRv.removeAllViews();
            genreList.clear();
            countryList.clear();
            listMovie.clear();
            listSeries.clear();
            listSlider.clear();
            listTv.clear();
            listGenre.clear();
            popularStarsList.clear();

            if (new NetworkInst(getContext()).isNetworkAvailable()) {
                getHomeContentDataFromServer();
            } else {
                tvNoItem.setText(getString(R.string.no_internet));
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                coordinatorLayout.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
            }
        });

        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY < oldScrollY) { // up
                animateSearchBar(false);
            }
            if (scrollY > oldScrollY) { // down
                animateSearchBar(true);
            }
        });

        new Handler().postDelayed(this::getHomeContentDataFromServer, 1000);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void populateViews() {
        if (homeContent == null) {
            //tvNoItem.setText(getString(R.string.no_internet));
            shimmerFrameLayout.startShimmer();
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            return;
        }
        swipeRefreshLayout.setRefreshing(false);
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
        coordinatorLayout.setVisibility(View.GONE);

        //clear all array list
        recyclerViewMovie.removeAllViews();
        recyclerViewTv.removeAllViews();
        recyclerViewTvSeries.removeAllViews();
        recyclerViewGenre.removeAllViews();
        genreRv.removeAllViews();
        countryRv.removeAllViews();
        popularStarsRv.removeAllViews();
        genreList.clear();
        countryList.clear();
        listMovie.clear();
        listSeries.clear();
        listSlider.clear();
        listTv.clear();
        listGenre.clear();
        popularStarsList.clear();

        //slider data
        Slider slider = homeContent.getSlider();
        if (slider.getSliderType().equalsIgnoreCase("disable")) {
            sliderLayout.setVisibility(View.GONE);
        } else if (slider.getSliderType().equalsIgnoreCase("movie")) {

        } else if (slider.getSliderType().equalsIgnoreCase("image")) {

        }

        SliderAdapter sliderAdapter = new SliderAdapter(slider.getSlideArrayList());
        cViewPager.setAdapter(sliderAdapter);
        sliderAdapter.notifyDataSetChanged();

        //genre data
        if (db.getConfigurationData().getAppConfig().getGenreVisible()) {
            if (homeContent.getAllGenre() != null) {
                for (int i = 0; i < homeContent.getAllGenre().size(); i++) {
                    AllGenre genre = homeContent.getAllGenre().get(i);
                    CommonModels models = new CommonModels();
                    models.setId(genre.getGenreId());
                    models.setTitle(genre.getName());
                    models.setImageUrl(genre.getImageUrl());
                    genreList.add(models);
                }
                genreAdapter.notifyDataSetChanged();
            }
        }

        //country data
        if (db.getConfigurationData().getAppConfig().getCountryVisible()) {
            if (homeContent.getAllCountry() != null) {
                for (int i = 0; i < homeContent.getAllCountry().size(); i++) {
                    AllCountry country = homeContent.getAllCountry().get(i);
                    CommonModels models = new CommonModels();
                    models.setId(country.getCountryId());
                    models.setTitle(country.getName());
                    models.setImageUrl(country.getImageUrl());
                    countryList.add(models);
                }
                countryAdapter.notifyDataSetChanged();
            }
        }

        //popular stars data
        if (homeContent.getPopularStarsList() != null) {
            if (homeContent.getPopularStarsList().size() > 0) {
                popular_stars_layout.setVisibility(View.VISIBLE);
                popularStarsList.addAll(homeContent.getPopularStarsList());
                popularStarAdapter.notifyDataSetChanged();
            }
        }

        //tv channel data
        if (homeContent.getFeaturedTvChannel() != null) {
            if (homeContent.getFeaturedTvChannel().size() > 0) {
                for (int i = 0; i < homeContent.getFeaturedTvChannel().size(); i++) {
                    FeaturedTvChannel tvChannel = homeContent.getFeaturedTvChannel().get(i);
                    CommonModels models = new CommonModels();
                    models.setImageUrl(tvChannel.getPosterUrl());
                    models.setTitle(tvChannel.getTvName());
                    models.setVideoType("tv");
                    models.setId(tvChannel.getLiveTvId());
                    models.setIsPaid(tvChannel.getIsPaid());
                    listTv.add(models);
                }
                featuredTVLayout.setVisibility(View.VISIBLE);
                adapterTv.notifyDataSetChanged();
            }
        }

        //latest movies data
        if (homeContent.getLatestMovies() != null) {
            if (homeContent.getLatestMovies().size() > 0) {
                for (int i = 0; i < homeContent.getLatestMovies().size(); i++) {
                    LatestMovie movie = homeContent.getLatestMovies().get(i);
                    CommonModels models = new CommonModels();
                    models.setImageUrl(movie.getThumbnailUrl());
                    models.setTitle(movie.getTitle());
                    models.setVideoType("movie");
                    models.setReleaseDate(movie.getRelease());
                    models.setQuality(movie.getVideoQuality());
                    models.setId(movie.getVideosId());
                    models.setIsPaid(movie.getIsPaid());
                    listMovie.add(models);
                }
                movieLayout.setVisibility(View.VISIBLE);
                adapterMovie.notifyDataSetChanged();
            }
        }

        //latest tv series
        if (homeContent.getLatestTvseries() != null) {
            if (homeContent.getLatestTvseries().size() > 0) {
                for (int i = 0; i < homeContent.getLatestTvseries().size(); i++) {
                    LatestTvseries tvSeries = homeContent.getLatestTvseries().get(i);
                    CommonModels models = new CommonModels();
                    models.setImageUrl(tvSeries.getThumbnailUrl());
                    models.setTitle(tvSeries.getTitle());
                    models.setVideoType("tvseries");
                    models.setReleaseDate(tvSeries.getRelease());
                    models.setQuality(tvSeries.getVideoQuality());
                    models.setId(tvSeries.getVideosId());
                    models.setIsPaid(tvSeries.getIsPaid());
                    listSeries.add(models);
                }
                tvSeriesLayout.setVisibility(View.VISIBLE);
                adapterSeries.notifyDataSetChanged();
            }
        }

        //get data by genre
        if (homeContent.getFeaturesGenreAndMovie() != null) {
            for (int i = 0; i < homeContent.getFeaturesGenreAndMovie().size(); i++) {
                FeaturesGenreAndMovie genreAndMovie = homeContent.getFeaturesGenreAndMovie().get(i);
                GenreModel models = new GenreModel();

                models.setName(genreAndMovie.getName());
                models.setId(genreAndMovie.getGenreId());
                List<CommonModels> listGenreMovie = new ArrayList<>();
                for (int j = 0; j < genreAndMovie.getVideos().size(); j++) {
                    Video video = genreAndMovie.getVideos().get(j);
                    CommonModels commonModels = new CommonModels();

                    commonModels.setId(video.getVideosId());
                    commonModels.setTitle(video.getTitle());
                    commonModels.setIsPaid(video.getIsPaid());

                    if (video.getIsTvseries().equals("0")) {
                        commonModels.setVideoType("movie");
                    } else {
                        commonModels.setVideoType("tvseries");
                    }

                    commonModels.setReleaseDate(video.getRelease());
                    commonModels.setQuality(video.getVideoQuality());
                    commonModels.setImageUrl(video.getThumbnailUrl());

                    listGenreMovie.add(commonModels);
                }
                models.setList(listGenreMovie);

                listGenre.add(models);
                genreHomeAdapter.notifyDataSetChanged();
            }
        }
    }

    private void getHomeContentDataFromServer() {
        String userId = PreferenceUtils.getUserId(activity);
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        HomeContentApi api = retrofit.create(HomeContentApi.class);
        Call<HomeContent> call = api.getHomeContent(MyAppClass.API_KEY, BuildConfig.VERSION_CODE, userId,
                getDeviceId(activity));
        call.enqueue(new Callback<HomeContent>() {
            @Override
            public void onResponse(@NonNull Call<HomeContent> call, @NonNull retrofit2.Response<HomeContent> response) {
                if (response.code() == 200) {

                    //insert data to room database
                    // homeContentViewModel = ViewModelProviders.of(getActivity()).get(HomeContentViewModel.class);
                    homeContent = response.body();
                    homeContent.setHomeContentId(1);
                    homeContentViewModel.insert(homeContent);

                    populateViews();
                    swipeRefreshLayout.setRefreshing(false);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    coordinatorLayout.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);

                } else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    requireContext());
                            activity.finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(requireContext(),
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    coordinatorLayout.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<HomeContent> call, @NonNull Throwable t) {
                //              swipeRefreshLayout.setRefreshing(false);
//                shimmerFrameLayout.stopShimmer();
//                shimmerFrameLayout.setVisibility(View.GONE);
//                coordinatorLayout.setVisibility(View.VISIBLE);
//                scrollView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void btnClick() {

        btnMoreMovie.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ItemMovieActivity.class);
            intent.putExtra("title", "Movies");
            getActivity().startActivity(intent);
        });
        btnMoreTv.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ItemTVActivity.class);
            intent.putExtra("title", "Live TV");
            getActivity().startActivity(intent);
        });

        btnMoreSeries.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ItemSeriesActivity.class);
            intent.putExtra("title", "TV Series");
            getActivity().startActivity(intent);
        });

        btnContinueWatchingClear.setOnClickListener(v -> continueWatchingViewModel.deleteAllContent());
    }

    @Override
    public void onStart() {
        super.onStart();

        menuIv.setOnClickListener(view -> activity.openDrawer());

        searchIv.setOnClickListener(view -> activity.goToSearchActivity());

        shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
        timer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    boolean isSearchBarHide = false;

    private void animateSearchBar(final boolean hide) {
        if (isSearchBarHide && hide || !isSearchBarHide && !hide) return;
        isSearchBarHide = hide;
        int moveY = hide ? -(2 * searchRootLayout.getHeight()) : 0;
        searchRootLayout.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }
}

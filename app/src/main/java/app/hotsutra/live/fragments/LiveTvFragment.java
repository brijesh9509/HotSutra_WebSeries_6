package app.hotsutra.live.fragments;

import static app.hotsutra.live.utils.Constants.getDeviceId;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import app.hotsutra.live.BuildConfig;
import app.hotsutra.live.utils.ApiResources;
import app.hotsutra.live.utils.PreferenceUtils;
import com.facebook.shimmer.ShimmerFrameLayout;
import app.hotsutra.live.AppConfig;
import app.hotsutra.live.MainActivity;
import app.hotsutra.live.R;
import app.hotsutra.live.adapters.LiveTvCategoryAdapter;
import app.hotsutra.live.network.RetrofitClient;
import app.hotsutra.live.network.apis.LiveTvApi;
import app.hotsutra.live.network.model.LiveTvCategory;
import app.hotsutra.live.utils.NetworkInst;
import app.hotsutra.live.utils.ToastMsg;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class LiveTvFragment extends Fragment {

    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerView;
    private LiveTvCategoryAdapter adapter;
    private final List<LiveTvCategory> liveTvCategories = new ArrayList<>();
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CoordinatorLayout coordinatorLayout;
    private TextView tvNoItem;


    private MainActivity activity;
    private LinearLayout searchRootLayout;

    private CardView searchBar;
    private ImageView menuIv, searchIv;
    private TextView pageTitle;

    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.fragment_livetv, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //getActivity().setTitle(getResources().getString(R.string.live_tv));

        initComponent(view);

        pageTitle.setText(getResources().getString(R.string.live_tv));

        if (activity.isDark) {
            pageTitle.setTextColor(activity.getResources().getColor(R.color.white));
            searchBar.setCardBackgroundColor(activity.getResources().getColor(R.color.black_window_light));
            menuIv.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_menu));
            searchIv.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_search_white));
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private void initComponent(View view) {
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();
        progressBar = view.findViewById(R.id.item_progress_bar);
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        coordinatorLayout = view.findViewById(R.id.coordinator_lyt);
        tvNoItem = view.findViewById(R.id.tv_noitem);

        searchRootLayout = view.findViewById(R.id.search_root_layout);
        searchBar = view.findViewById(R.id.search_bar);
        menuIv = view.findViewById(R.id.bt_menu);
        pageTitle = view.findViewById(R.id.page_title_tv);
        searchIv = view.findViewById(R.id.search_iv);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new LiveTvCategoryAdapter(activity, liveTvCategories);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                    animateSearchBar(true);
                    controlsVisible = false;
                    scrolledDistance = 0;
                } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                    animateSearchBar(false);
                    controlsVisible = true;
                    scrolledDistance = 0;
                }

                if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
                    scrolledDistance += dy;
                }


            }
        });


        if (new NetworkInst(activity).isNetworkAvailable()) {
            getLiveTvData();
        } else {
            tvNoItem.setText(getString(R.string.no_internet));
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            coordinatorLayout.setVisibility(View.VISIBLE);
        }


        swipeRefreshLayout.setOnRefreshListener(() -> {

            coordinatorLayout.setVisibility(View.GONE);
            liveTvCategories.clear();
            recyclerView.removeAllViews();
            adapter.notifyDataSetChanged();
            if (new NetworkInst(activity).isNetworkAvailable()) {
                getLiveTvData();
            } else {
                tvNoItem.setText(getString(R.string.no_internet));
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                coordinatorLayout.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        menuIv.setOnClickListener(view -> activity.openDrawer());
        searchIv.setOnClickListener(view -> activity.goToSearchActivity());

    }

    private void getLiveTvData() {
        String userId = PreferenceUtils.getUserId(requireContext());
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        LiveTvApi api = retrofit.create(LiveTvApi.class);
        api.getLiveTvCategories(AppConfig.API_KEY, BuildConfig.VERSION_CODE,userId,getDeviceId(requireContext()))
                .enqueue(new Callback<List<LiveTvCategory>>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<List<LiveTvCategory>> call,
                                           @NonNull retrofit2.Response<List<LiveTvCategory>> response) {
                        swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            liveTvCategories.addAll(response.body());

                            if (liveTvCategories.size() == 0) {
                                coordinatorLayout.setVisibility(View.VISIBLE);
                            } else {
                                adapter.notifyDataSetChanged();
                            }

                        }else if (response.code() == 412) {
                            try {
                                if (response.errorBody() != null) {
                                    try {
                                        ApiResources.openLoginScreen(response.errorBody().string(),
                                                requireContext());
                                        activity.finish();
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                Toast.makeText(requireContext(),
                                        e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            swipeRefreshLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);
                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);

                            coordinatorLayout.setVisibility(View.VISIBLE);
                            tvNoItem.setText(getResources().getString(R.string.something_went_text));
                            new ToastMsg(activity).toastIconError("Something went wrong...");
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<List<LiveTvCategory>> call, @NonNull Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);

                        coordinatorLayout.setVisibility(View.VISIBLE);
                        tvNoItem.setText(getResources().getString(R.string.something_went_text));

                        t.printStackTrace();
                    }
                });

    }

    boolean isSearchBarHide = false;

    private void animateSearchBar(final boolean hide) {
        if (isSearchBarHide && hide || !isSearchBarHide && !hide) return;
        isSearchBarHide = hide;
        int moveY = hide ? -(2 * searchRootLayout.getHeight()) : 0;
        searchRootLayout.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }


}
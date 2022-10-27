package app.hotsutra.live.nav_fragments;

import static app.hotsutra.live.utils.Constants.getDeviceId;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import app.hotsutra.live.BuildConfig;
import app.hotsutra.live.utils.ApiResources;
import app.hotsutra.live.utils.PreferenceUtils;
import com.facebook.shimmer.ShimmerFrameLayout;
import app.hotsutra.live.AppConfig;
import app.hotsutra.live.MainActivity;
import app.hotsutra.live.R;
import app.hotsutra.live.adapters.CountryAdapter;
import app.hotsutra.live.models.CommonModels;
import app.hotsutra.live.models.home_content.AllCountry;
import app.hotsutra.live.network.RetrofitClient;
import app.hotsutra.live.network.apis.CountryApi;
import app.hotsutra.live.utils.NetworkInst;
import app.hotsutra.live.utils.SpacingItemDecoration;
import app.hotsutra.live.utils.ToastMsg;
import app.hotsutra.live.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class CountryFragment extends Fragment {

    ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerView;
    private final List<CommonModels> list = new ArrayList<>();
    private CountryAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CoordinatorLayout coordinatorLayout;

    private TextView tvNoItem;

    private MainActivity activity;

    private LinearLayout searchRootLayout;

    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;

    private ImageView menuIv, searchIv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.layout_country,container,false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getResources().getString(R.string.country));

        coordinatorLayout=view.findViewById(R.id.coordinator_lyt);
        swipeRefreshLayout=view.findViewById(R.id.swipe_layout);
        shimmerFrameLayout=view.findViewById(R.id.shimmer_view_container);
        tvNoItem=view.findViewById(R.id.tv_noitem);
        searchRootLayout=view.findViewById(R.id.search_root_layout);
        CardView searchBar = view.findViewById(R.id.search_bar);
        menuIv              = view.findViewById(R.id.bt_menu);
        TextView pageTitle = view.findViewById(R.id.page_title_tv);
        searchIv            = view.findViewById(R.id.search_iv);

        pageTitle.setText(getContext().getResources().getString(R.string.country));

        if (activity.isDark) {
            pageTitle.setTextColor(activity.getResources().getColor(R.color.white));
            searchBar.setCardBackgroundColor(activity.getResources().getColor(R.color.black_window_light));
            menuIv.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_menu));
            searchIv.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_search_white));
        }

        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(getActivity(), 10), true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        mAdapter = new CountryAdapter(activity, list, "");
        recyclerView.setAdapter(mAdapter);

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

                if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {
                    scrolledDistance += dy;
                }


            }
        });

        shimmerFrameLayout.startShimmer();


        if (new NetworkInst(getContext()).isNetworkAvailable()){
            getAllCountry();
        }else {
            tvNoItem.setText(getString(R.string.no_internet));
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            coordinatorLayout.setVisibility(View.VISIBLE);
        }

        swipeRefreshLayout.setOnRefreshListener(() -> {

            coordinatorLayout.setVisibility(View.GONE);

            recyclerView.removeAllViews();
            list.clear();
            mAdapter.notifyDataSetChanged();

            if (new NetworkInst(getContext()).isNetworkAvailable()){
                getAllCountry();
            }else {
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


    private void getAllCountry(){
        String userId = PreferenceUtils.getUserId(requireActivity());
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        CountryApi api = retrofit.create(CountryApi.class);
        Call<List<AllCountry>> call = api.getAllCountry(AppConfig.API_KEY,
                BuildConfig.VERSION_CODE,userId,getDeviceId(requireContext()));
        call.enqueue(new Callback<List<AllCountry>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<AllCountry>> call, @NonNull retrofit2.Response<List<AllCountry>> response) {
                if (response.code() == 200){
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

                    if (response.body().size() == 0){
                        coordinatorLayout.setVisibility(View.VISIBLE);
                    }else {
                        coordinatorLayout.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < response.body().size(); i++) {
                        AllCountry country = response.body().get(i);
                        CommonModels models = new CommonModels();
                        models.setId(country.getCountryId());
                        models.setTitle(country.getName());
                        models.setImageUrl(country.getImageUrl());
                        list.add(models);
                    }
                    mAdapter.notifyDataSetChanged();
                }else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    requireActivity());
                            requireActivity().finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(requireActivity(),
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }else {
                    swipeRefreshLayout.setRefreshing(false);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    new ToastMsg(getActivity()).toastIconError(getString(R.string.fetch_error));
                    coordinatorLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AllCountry>> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                new ToastMsg(getActivity()).toastIconError(getString(R.string.fetch_error));
                coordinatorLayout.setVisibility(View.VISIBLE);
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

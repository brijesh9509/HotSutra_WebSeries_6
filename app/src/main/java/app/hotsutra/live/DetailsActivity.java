package app.hotsutra.live;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.mediarouter.app.MediaRouteButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import app.hotsutra.live.R;

import app.hotsutra.live.models.single_details_tv.AllTvChannel;
import app.hotsutra.live.models.single_details_tv.ProgramGuide;
import com.balysv.materialripple.MaterialRippleLayout;
import app.hotsutra.live.adapters.CastCrewAdapter;
import app.hotsutra.live.adapters.CommentsAdapter;
import app.hotsutra.live.adapters.DownloadAdapter;
import app.hotsutra.live.adapters.EpisodeDownloadAdapter;
import app.hotsutra.live.adapters.HomePageAdapter;
import app.hotsutra.live.adapters.ProgramAdapter;
import app.hotsutra.live.adapters.RelatedTvAdapter;
import app.hotsutra.live.adapters.ServerAdapter;
import app.hotsutra.live.database.DatabaseHelper;
import app.hotsutra.live.database.continueWatching.ContinueWatchingModel;
import app.hotsutra.live.database.continueWatching.ContinueWatchingViewModel;
import app.hotsutra.live.database.downlaod.DownloadViewModel;
import app.hotsutra.live.network.apis.CommentApi;
import app.hotsutra.live.network.apis.FavouriteApi;
import app.hotsutra.live.network.apis.ReportApi;
import app.hotsutra.live.network.apis.SingleDetailsApi;
import app.hotsutra.live.network.apis.SingleDetailsTVApi;
import app.hotsutra.live.network.model.FavoriteModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.cast.CastPlayer;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.firebase.analytics.FirebaseAnalytics;
import app.hotsutra.live.adapters.EpisodeAdapter;
import app.hotsutra.live.models.CastCrew;
import app.hotsutra.live.models.CommonModels;
import app.hotsutra.live.models.EpiModel;
import app.hotsutra.live.models.GetCommentsModel;
import app.hotsutra.live.models.PostCommentModel;
import app.hotsutra.live.models.Program;
import app.hotsutra.live.models.SubtitleModel;
import app.hotsutra.live.models.single_details.Cast;
import app.hotsutra.live.models.single_details.Director;
import app.hotsutra.live.models.single_details.DownloadLink;
import app.hotsutra.live.models.single_details.Episode;
import app.hotsutra.live.models.single_details.Genre;
import app.hotsutra.live.models.single_details.RelatedMovie;
import app.hotsutra.live.models.single_details.Season;
import app.hotsutra.live.models.single_details.SingleDetails;
import app.hotsutra.live.models.single_details.Subtitle;
import app.hotsutra.live.models.single_details.Video;
import app.hotsutra.live.models.single_details_tv.AdditionalMediaSource;
import app.hotsutra.live.models.single_details_tv.SingleDetailsTV;
import app.hotsutra.live.network.RetrofitClient;
import app.hotsutra.live.utils.ApiResources;
import app.hotsutra.live.utils.HelperUtils;
import app.hotsutra.live.utils.PreferenceUtils;
import app.hotsutra.live.utils.RtlUtils;
import app.hotsutra.live.utils.ToastMsg;
import app.hotsutra.live.utils.Tools;
import app.hotsutra.live.utils.TrackSelectionDialog;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import app.hotsutra.live.utils.Constants;
import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

@SuppressLint("StaticFieldLeak")
public class DetailsActivity extends AppCompatActivity implements CastPlayer.SessionAvailabilityListener,
        ProgramAdapter.OnProgramClickListener,
        EpisodeAdapter.OnTVSeriesEpisodeItemClickListener,
        RelatedTvAdapter.RelatedTvClickListener {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PRELOAD_TIME_S = 20;
    public static final String TAG = DetailsActivity.class.getSimpleName();
    private TextView tvName, tvDirector, tvRelease, tvDes, tvGenre, tvRelated;
    private RecyclerView rvServer, rvServerForTV, rvRelated, rvComment, castRv;
    private Spinner seasonSpinner;
    private LinearLayout seasonSpinnerContainer;
    public static RelativeLayout lPlay;
    private RelativeLayout contentDetails;
    private LinearLayout subscriptionLayout, topBarLayout;
    private Button subscribeBt;
    private ImageView backIv, subBackIv;

    private ServerAdapter serverAdapter;
    private HomePageAdapter relatedAdapter;
    private RelatedTvAdapter relatedTvAdapter;
    private CastCrewAdapter castCrewAdapter;

    private String V_URL = "";
    public static WebView webView;
    public static ProgressBar progressBar;
    private boolean isFav = false;
    private TextView chromeCastTv;
    private final List<CommonModels> listServer = new ArrayList<>();
    private final List<CommonModels> listRelated = new ArrayList<>();
    private final List<GetCommentsModel> listComment = new ArrayList<>();
    //private final List<CommonModels> listDownload = new ArrayList<>();
    private final List<CommonModels> listInternalDownload = new ArrayList<>();
    private final List<CommonModels> listExternalDownload = new ArrayList<>();
    private final List<CastCrew> castCrews = new ArrayList<>();
    private String strDirector = "", strGenre = "";
    public static LinearLayout llBottom, llBottomParent;
    public static RelativeLayout llComment;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String categoryType = "", id = "";
    private ImageButton imgAddFav, shareIv2, reportIv;
    public static ImageView imgBack, serverIv;
    private Button watchNowBt, downloadBt, trailerBt;
    private LinearLayout downloadAndTrailerBtContainer;
    private ImageView posterIv, thumbIv;

    private ShimmerFrameLayout shimmerFrameLayout;
    private Button btnComment;
    private EditText etComment;
    private CommentsAdapter commentsAdapter;

    public static SimpleExoPlayer player;
    public static PlayerView simpleExoPlayerView;
    public static FrameLayout youtubePlayerView;
    private static RelativeLayout exoplayerLayout;
    public PlayerControlView castControlView;
    public static SubtitleView subtitleView;
    private DefaultTrackSelector trackSelector;

    public static ImageView imgFull;
    public ImageView aspectRatioIv, externalPlayerIv, volumeControlIv;
    private LinearLayout volumeControlLayout;
    private SeekBar volumeSeekbar;
    public MediaRouteButton mediaRouteButton;

    public static boolean isPlaying, isFullScr;
    public static View playerLayout;

    private int playerHeight;
    public static boolean isVideo = true;
    private final String strSubtitle = "Null";
    public static MediaSource mediaSource = null;
    public static ImageView imgSubtitle, imgAudio;
    private final List<SubtitleModel> listSub = new ArrayList<>();
    private AlertDialog alertDialog;
    private String mediaUrl;
    private boolean isFromContinueWatching = false;
    private boolean tv = false;
    private String download_check = "";
    private String trailerUrl = "";

    private CastPlayer castPlayer;
    private boolean castSession;
    private String title;
    String castImageUrl;

    private LinearLayout tvLayout, scheduleLayout, tvTopLayout;
    private TextView tvTitleTv, watchStatusTv, timeTv, programTv, proGuideTv, watchLiveTv;
    private ProgramAdapter programAdapter;
    List<Program> programs = new ArrayList<>();
    private RecyclerView programRv;
    private ImageView tvThumbIv, shareIv, tvReportIV;

    private LinearLayout exoRewind, exoForward, seekbarLayout;
    private TextView liveTv;

    boolean isDark;
    private static String serverType;

    private String currentProgramTime;
    private String currentProgramTitle;
    private String userId;

    private MaterialRippleLayout descriptionContainer;
    private TextView dGenreTv;
    private boolean activeMovie;

    private TextView seriesTitleTv;
    private RelativeLayout seriesLayout;
    private ImageView favIv;

    private AudioManager mAudioManager;
    private int aspectClickCount = 1;

    private HelperUtils helperUtils;
    private boolean vpnStatus;
    private ContinueWatchingViewModel viewModel;
    private long resumePosition = 0L;
    private static long playerCurrentPosition = 0L;
    private static long mediaDuration = 0L;
    //season download
    private LinearLayout seasonDownloadLayout;
    private Spinner seasonDownloadSpinner;
    private RecyclerView seasonDownloadRecyclerView;
    private DownloadViewModel downloadViewModel;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RtlUtils.setScreenDirection(this);
        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        isDark = sharedPreferences.getBoolean("dark", false);
        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //check vpn connection
        helperUtils = new HelperUtils(DetailsActivity.this);
        vpnStatus = helperUtils.isVpnConnectionAvailable();
        if (vpnStatus) {
            helperUtils.showWarningDialog(DetailsActivity.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
            return;
        }

        DatabaseHelper db = new DatabaseHelper(DetailsActivity.this);

        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        //---analytics-----------
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "details_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        initViews();

        if (isDark) {
            tvTopLayout.setBackgroundColor(getResources().getColor(R.color.black_window_light));
            scheduleLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_black_transparent));
            etComment.setBackground(ContextCompat.getDrawable(this, R.drawable.round_grey_transparent));
            btnComment.setTextColor(getResources().getColor(R.color.grey_20));
            topBarLayout.setBackgroundColor(getResources().getColor(R.color.dark));
            subscribeBt.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_rounded_dark));

            descriptionContainer.setBackground(ContextCompat.getDrawable(this, R.drawable.gradient_black_transparent));
        }
        // chrome cast
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), mediaRouteButton);
        CastContext castContext = CastContext.getSharedInstance(this);
        castPlayer = new CastPlayer(castContext);
        castPlayer.setSessionAvailabilityListener(this);

        // cast button will show if the cast device will be available
        if (castContext.getCastState() != CastState.NO_DEVICES_AVAILABLE)
            mediaRouteButton.setVisibility(View.VISIBLE);
        // start the shimmer effect
        shimmerFrameLayout.setVisibility(VISIBLE);
        shimmerFrameLayout.startShimmer();
        playerHeight = lPlay.getLayoutParams().height;
        progressBar.setMax(100); // 100 maximum value for the progress value
        progressBar.setProgress(50);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());

        imgBack.setOnClickListener(v -> {
            //updateContinueWatchingData();
            if (activeMovie) {
                setPlayerNormalScreen();
                if (player != null) {
                    player.setPlayWhenReady(false);
                    player.stop();
                }
                showDescriptionLayout();
                activeMovie = false;
            } else {
                //finish();
                onBackPressed();
            }
        });

        categoryType = getIntent().getStringExtra("vType");
        id = getIntent().getStringExtra("id");
        castSession = getIntent().getBooleanExtra("castSession", false);

        //handle Continue watching task
        isFromContinueWatching = getIntent().getBooleanExtra(Constants.IS_FROM_CONTINUE_WATCHING, false);
        try {
            if (isFromContinueWatching) {
                id = getIntent().getStringExtra(Constants.CONTENT_ID);
                title = getIntent().getStringExtra(Constants.CONTENT_TITLE);
                castImageUrl = getIntent().getStringExtra(Constants.IMAGE_URL);
                categoryType = getIntent().getStringExtra(Constants.CATEGORY_TYPE);
                serverType = getIntent().getStringExtra(Constants.SERVER_TYPE);
                mediaUrl = getIntent().getStringExtra(Constants.STREAM_URL);
                playerCurrentPosition = getIntent().getLongExtra(Constants.POSITION, 0);
                resumePosition = playerCurrentPosition;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        // getting user login info for favourite button visibility
        userId = db.getUserData().getUserId();
        //ContinueWatching State
        viewModel = ViewModelProviders.of(this).get(ContinueWatchingViewModel.class);
        /*initialize view model and pass it to adapter*/
        downloadViewModel = ViewModelProviders.of(this).get(DownloadViewModel.class);


        if (PreferenceUtils.isLoggedIn(DetailsActivity.this)) {
            imgAddFav.setVisibility(VISIBLE);
        } else {
            imgAddFav.setVisibility(GONE);
        }

        commentsAdapter = new CommentsAdapter(this, listComment);
        rvComment.setLayoutManager(new LinearLayoutManager(this));
        rvComment.setNestedScrollingEnabled(false);
        rvComment.setAdapter(commentsAdapter);
        getComments();
        imgFull.setOnClickListener(v -> controlFullScreenPlayer());
        imgSubtitle.setOnClickListener(v -> showSubtitleDialog(DetailsActivity.this, listSub));

        imgAudio.setOnClickListener(view -> {
            TrackSelectionDialog trackSelectionDialog =
                    TrackSelectionDialog.createForTrackSelector(
                            trackSelector,
                            /* onDismissListener= */ dismissedDialog -> {
                            });
            trackSelectionDialog.show(getSupportFragmentManager(), null);
        });

        btnComment.setOnClickListener(v -> {
            if (!PreferenceUtils.isLoggedIn(DetailsActivity.this)) {
                startActivity(new Intent(DetailsActivity.this, LoginActivity.class));
                new ToastMsg(DetailsActivity.this).toastIconError(getString(R.string.login_first));
            } else if (etComment.getText().toString().equals("")) {
                new ToastMsg(DetailsActivity.this).toastIconError(getString(R.string.comment_empty));
            } else {
                String comment = etComment.getText().toString();
                addComment(id, PreferenceUtils.getUserId(DetailsActivity.this), comment);
            }
        });

        imgAddFav.setOnClickListener(v -> {
            if (isFav) {
                removeFromFav();
            } else {
                addToFav();
            }
        });

        // its for tv series only when description layout visibility gone.
        favIv.setOnClickListener(v -> {
            if (isFav) {
                removeFromFav();
            } else {
                addToFav();
            }
        });


        if (!isNetworkAvailable()) {
            new ToastMsg(DetailsActivity.this).toastIconError(getString(R.string.no_internet));
        }
        swipeRefreshLayout.setOnRefreshListener(() -> {
            clear_previous();
            initGetData();
        });

    }

    public void initViews() {
        llBottom = findViewById(R.id.llbottom);
        tvDes = findViewById(R.id.tv_details);
        tvRelease = findViewById(R.id.tv_release_date);
        tvName = findViewById(R.id.text_name);
        tvDirector = findViewById(R.id.tv_director);
        tvGenre = findViewById(R.id.tv_genre);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        imgAddFav = findViewById(R.id.add_fav);
        imgBack = findViewById(R.id.img_back);
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        llBottomParent = findViewById(R.id.llbottomparent);
        lPlay = findViewById(R.id.play);
        rvRelated = findViewById(R.id.rv_related);
        tvRelated = findViewById(R.id.tv_related);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        btnComment = findViewById(R.id.btn_comment);
        etComment = findViewById(R.id.et_comment);
        rvComment = findViewById(R.id.recyclerView_comment);
        llComment = findViewById(R.id.llcomments);
        simpleExoPlayerView = findViewById(R.id.video_view);
        youtubePlayerView = findViewById(R.id.youtubePlayerView);
        exoplayerLayout = findViewById(R.id.exoPlayerLayout);
        subtitleView = findViewById(R.id.subtitle);
        playerLayout = findViewById(R.id.player_layout);
        imgFull = findViewById(R.id.img_full_scr);
        aspectRatioIv = findViewById(R.id.aspect_ratio_iv);
        externalPlayerIv = findViewById(R.id.external_player_iv);
        volumeControlIv = findViewById(R.id.volumn_control_iv);
        volumeControlLayout = findViewById(R.id.volumn_layout);
        volumeSeekbar = findViewById(R.id.volumn_seekbar);
        rvServer = findViewById(R.id.rv_server_list);
        rvServerForTV = findViewById(R.id.rv_server_list_for_tv);
        seasonSpinner = findViewById(R.id.season_spinner);
        seasonSpinnerContainer = findViewById(R.id.spinner_container);
        imgSubtitle = findViewById(R.id.img_subtitle);
        imgAudio = findViewById(R.id.img_audio);
        mediaRouteButton = findViewById(R.id.media_route_button);
        chromeCastTv = findViewById(R.id.chrome_cast_tv);
        castControlView = findViewById(R.id.cast_control_view);
        tvLayout = findViewById(R.id.tv_layout);
        scheduleLayout = findViewById(R.id.p_shedule_layout);
        tvTitleTv = findViewById(R.id.tv_title_tv);
        programRv = findViewById(R.id.program_guide_rv);
        tvTopLayout = findViewById(R.id.tv_top_layout);
        tvThumbIv = findViewById(R.id.tv_thumb_iv);
        shareIv = findViewById(R.id.share_iv);
        tvReportIV = findViewById(R.id.tv_report_iv);
        watchStatusTv = findViewById(R.id.watch_status_tv);
        timeTv = findViewById(R.id.time_tv);
        programTv = findViewById(R.id.program_type_tv);
        exoRewind = findViewById(R.id.rewind_layout);
        exoForward = findViewById(R.id.forward_layout);
        seekbarLayout = findViewById(R.id.seekbar_layout);
        liveTv = findViewById(R.id.live_tv);
        castRv = findViewById(R.id.cast_rv);
        proGuideTv = findViewById(R.id.pro_guide_tv);
        watchLiveTv = findViewById(R.id.watch_live_tv);

        contentDetails = findViewById(R.id.content_details);
        subscriptionLayout = findViewById(R.id.subscribe_layout);
        subscribeBt = findViewById(R.id.subscribe_bt);
        backIv = findViewById(R.id.des_back_iv);
        subBackIv = findViewById(R.id.back_iv);
        topBarLayout = findViewById(R.id.topbar);

        descriptionContainer = findViewById(R.id.lyt_parent);
        watchNowBt = findViewById(R.id.watch_now_bt);
        downloadBt = findViewById(R.id.download_bt);
        trailerBt = findViewById(R.id.trailer_bt);
        downloadAndTrailerBtContainer = findViewById(R.id.downloadBt_container);
        posterIv = findViewById(R.id.poster_iv);
        thumbIv = findViewById(R.id.image_thumb);
        dGenreTv = findViewById(R.id.genre_tv);
        serverIv = findViewById(R.id.img_server);

        seriesLayout = findViewById(R.id.series_layout);
        favIv = findViewById(R.id.add_fav2);
        seriesTitleTv = findViewById(R.id.seriest_title_tv);
        shareIv2 = findViewById(R.id.share_iv2);
        reportIv = findViewById(R.id.report_iv);
        //season download
        seasonDownloadLayout = findViewById(R.id.seasonDownloadLayout);
        seasonDownloadSpinner = findViewById(R.id.seasonSpinnerField);
        seasonDownloadRecyclerView = findViewById(R.id.seasonDownloadRecyclerView);

    }

    @SuppressLint("SourceLockedOrientationActivity")
    public void controlFullScreenPlayer() {
        if (isFullScr) {
            isFullScr = false;
            swipeRefreshLayout.setVisibility(VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            if (isVideo) {
                lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, playerHeight));
            } else {
                lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, playerHeight));
            }

            // reset the orientation
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        } else {

            isFullScr = true;
            swipeRefreshLayout.setVisibility(GONE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

            // reset the orientation
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!AppConfig.ENABLE_EXTERNAL_PLAYER) {
            externalPlayerIv.setVisibility(GONE);
        }
        try {
            if (isFromContinueWatching) {
                releasePlayer();
                resetCastPlayer();
                setPlayerFullScreen();
                progressBar.setVisibility(VISIBLE);
                swipeRefreshLayout.setVisibility(GONE);
                lPlay.setVisibility(VISIBLE);
                imgFull.setVisibility(GONE);
                initVideoPlayer(mediaUrl, DetailsActivity.this, serverType);

            } else {
                initGetData();
            }

        } catch (NullPointerException e) {
            initGetData();
        }

        if (mAudioManager != null) {
            volumeSeekbar.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            int currentVolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            volumeSeekbar.setProgress(currentVolumn);
        }

        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    //volumnTv.setText(i+"");
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, AudioManager.ADJUST_SAME);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        volumeControlIv.setOnClickListener(view -> volumeControlLayout.setVisibility(VISIBLE));

        aspectRatioIv.setOnClickListener(view -> {
            if (aspectClickCount == 1) {
                simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                aspectClickCount = 2;
            } else if (aspectClickCount == 2) {
                simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                aspectClickCount = 3;
            } else if (aspectClickCount == 3) {
                simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                aspectClickCount = 1;
            }

        });

        externalPlayerIv.setOnClickListener(view -> {

            if (mediaUrl != null) {
                if (!tv) {
                    // set player normal/ portrait screen if not tv
                    setPlayerNormalScreen();
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(mediaUrl), "video/*");
                startActivity(Intent.createChooser(intent, "Complete action using"));
            }

        });

        watchNowBt.setOnClickListener(v -> {
            if (!listServer.isEmpty()) {
                if (listServer.size() == 1) {
                    releasePlayer();
                    //resetCastPlayer();
                    preparePlayer(listServer.get(0));
                    lPlay.setVisibility(VISIBLE);
                } else {
                    openServerDialog();
                }
            } else {
                Toast.makeText(DetailsActivity.this, R.string.no_video_found, Toast.LENGTH_SHORT).show();
            }
        });

        downloadBt.setOnClickListener(v -> {
            if (!listInternalDownload.isEmpty() || !listExternalDownload.isEmpty()) {
                if (AppConfig.ENABLE_DOWNLOAD_TO_ALL) {
                    openDownloadServerDialog();
                } else {
                    if (PreferenceUtils.isLoggedIn(DetailsActivity.this) && PreferenceUtils.isActivePlan(DetailsActivity.this)) {
                        openDownloadServerDialog();
                    } else {
                        Toast.makeText(DetailsActivity.this, R.string.download_not_permitted, Toast.LENGTH_SHORT).show();
                        Log.e("Download", "not permitted");
                    }
                }
            } else {
                Toast.makeText(DetailsActivity.this, R.string.no_download_server_found, Toast.LENGTH_SHORT).show();
            }
        });

        trailerBt.setOnClickListener(v -> {
            if (trailerUrl != null && !trailerUrl.equalsIgnoreCase("")) {
                serverType = Constants.YOUTUBE;
                mediaUrl = trailerUrl;
                CommonModels commonModels = new CommonModels();
                commonModels.setStremURL(trailerUrl);
                commonModels.setServerType(Constants.YOUTUBE);
                lPlay.setVisibility(VISIBLE);
                releasePlayer();
                preparePlayer(commonModels);
            }

        });

        watchLiveTv.setOnClickListener(v -> {
            hideExoControlForTv();
            initMoviePlayer(mediaUrl, serverType, DetailsActivity.this);

            watchStatusTv.setText(getString(R.string.watching_on) + " " + getString(R.string.app_name));
            watchLiveTv.setVisibility(GONE);

            timeTv.setText(currentProgramTime);
            programTv.setText(currentProgramTitle);
        });

        shareIv.setOnClickListener(v -> Tools.share(DetailsActivity.this, title));

        tvReportIV.setOnClickListener(v -> reportMovie());

        shareIv2.setOnClickListener(v -> {
            if (title == null) {
                new ToastMsg(DetailsActivity.this).toastIconError("Title should not be empty.");
                return;
            }
            Tools.share(DetailsActivity.this, title);
        });

        //report icon
        reportIv.setOnClickListener(v -> reportMovie());

        castPlayer.addListener(new Player.Listener() {


            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if (playWhenReady && playbackState == CastPlayer.STATE_READY) {
                    progressBar.setVisibility(View.GONE);

                    Log.e("STATE PLAYER:::", String.valueOf(isPlaying));

                } else if (playbackState == CastPlayer.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                    Log.e("STATE PLAYER:::", String.valueOf(isPlaying));
                } else if (playbackState == CastPlayer.STATE_BUFFERING) {
                    progressBar.setVisibility(VISIBLE);

                    Log.e("STATE PLAYER:::", String.valueOf(isPlaying));
                } else {
                    Log.e("STATE PLAYER:::", String.valueOf(isPlaying));
                }

            }
        });

        serverIv.setOnClickListener(v -> openServerDialog());

        simpleExoPlayerView.setControllerVisibilityListener(visibility -> {
            if (visibility == 0) {
                imgBack.setVisibility(VISIBLE);

                if (categoryType.equals("tv") || categoryType.equals("tvseries")) {
                    imgFull.setVisibility(VISIBLE);
                } else {
                    imgFull.setVisibility(GONE);
                }

                // invisible download icon for live tv
                if (download_check.equals("1")) {
                    if (!tv) {
                        if (activeMovie) {
                            serverIv.setVisibility(VISIBLE);
                        }
                    } else {
                    }
                } else {
                }

                if (listSub.size() != 0) {
                    imgSubtitle.setVisibility(VISIBLE);
                }
                //imgSubtitle.setVisibility(VISIBLE);
            } else {
                imgBack.setVisibility(GONE);
                imgFull.setVisibility(GONE);
                imgSubtitle.setVisibility(GONE);
                volumeControlLayout.setVisibility(GONE);
            }
        });

        subscribeBt.setOnClickListener(v -> {

            if (userId == null) {
                new ToastMsg(DetailsActivity.this).toastIconError(getResources().getString(R.string.subscribe_error));
                startActivity(new Intent(DetailsActivity.this, LoginActivity.class));
                finish();
            } else {
                startActivity(new Intent(DetailsActivity.this, PurchasePlanActivity.class));
            }

        });
        backIv.setOnClickListener(v -> finish());

        subBackIv.setOnClickListener(v -> finish());

    }

    String videoReport = "", audioReport = "", subtitleReport = "", messageReport = "";

    private void reportMovie() {
        //open movie report dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.movie_report_dialog, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        TextView movieTitle = view.findViewById(R.id.movie_title);
        RadioGroup videoGroup = view.findViewById(R.id.radio_group_video);
        RadioGroup audioGroup = view.findViewById(R.id.radio_group_audio);
        RadioGroup subtitleGroup = view.findViewById(R.id.radio_group_subtitle);
        TextInputEditText message = view.findViewById(R.id.report_message_et);
        Button submitButton = view.findViewById(R.id.submit_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        LinearLayout subtitleLayout = view.findViewById(R.id.subtitleLayout);
        if (this.categoryType.equalsIgnoreCase("tv")) {
            subtitleLayout.setVisibility(GONE);
        }

        movieTitle.setText("Report for: " + title);
        if (!isDark) {
            movieTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
        }


        videoGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // find the radiobutton by returned id
            RadioButton radioButton = view.findViewById(checkedId);
            videoReport = radioButton.getText().toString();
        });

        audioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // find the radiobutton by returned id
            RadioButton radioButton = view.findViewById(checkedId);
            audioReport = radioButton.getText().toString();
        });

        subtitleGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // find the radiobutton by returned id
            RadioButton radioButton = view.findViewById(checkedId);
            subtitleReport = radioButton.getText().toString();
        });

        submitButton.setOnClickListener(v -> {
            messageReport = message.getText().toString().trim();
            Retrofit retrofit = RetrofitClient.getRetrofitInstance();
            ReportApi api = retrofit.create(ReportApi.class);
            Call<ResponseBody> call = api.submitReport(AppConfig.API_KEY, categoryType, id, videoReport,
                    audioReport, subtitleReport, messageReport, BuildConfig.VERSION_CODE,
                    PreferenceUtils.getUserId(DetailsActivity.this), Constants.getDeviceId(this));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        new ToastMsg(getApplicationContext()).toastIconSuccess("Report submitted");
                    }else if (response.code() == 412) {
                        try {
                            if (response.errorBody() != null) {
                                ApiResources.openLoginScreen(response.errorBody().string(),
                                        DetailsActivity.this);
                                finish();
                            }
                        } catch (Exception e) {
                            Toast.makeText(DetailsActivity.this,
                                    e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        new ToastMsg(getApplicationContext()).toastIconError(getResources().getString(R.string.something_went_text));
                    }
                    dialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    new ToastMsg(getApplicationContext()).toastIconError(getResources().getString(R.string.something_went_text));
                    dialog.dismiss();
                }
            });
        });
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }

    private void updateContinueWatchingData() {
        if (!categoryType.equals("tv")) {
            try {
                long position = playerCurrentPosition;
                long duration = mediaDuration;
                float progress = 0;
                if (position != 0 && duration != 0) {
                    progress = calculateProgress(position, duration);
                }

                //---update into continueWatching------
                ContinueWatchingModel model = new ContinueWatchingModel(id, title,
                        castImageUrl, progress, position, mediaUrl,
                        categoryType, serverType);
                viewModel.update(model);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public void setPlayerNormalScreen() {
        swipeRefreshLayout.setVisibility(VISIBLE);
        lPlay.setVisibility(GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //close embed link playing
        if (webView.getVisibility() == VISIBLE) {
            if (webView != null) {
                Intent intent = new Intent(DetailsActivity.this, DetailsActivity.class);
                intent.putExtra("vType", categoryType);
                intent.putExtra("id", id);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }

        lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, playerHeight));
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public void setPlayerFullScreen() {
        swipeRefreshLayout.setVisibility(GONE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (isVideo) {
            lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        } else {
            lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        }
    }

    private void openDownloadServerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_download_server_dialog, null);
        LinearLayout internalDownloadLayout = view.findViewById(R.id.internal_download_layout);
        LinearLayout externalDownloadLayout = view.findViewById(R.id.external_download_layout);
        if (listExternalDownload.isEmpty()) {
            externalDownloadLayout.setVisibility(GONE);
        }
        if (listInternalDownload.isEmpty()) {
            internalDownloadLayout.setVisibility(GONE);
        }
        RecyclerView internalServerRv = view.findViewById(R.id.internal_download_rv);
        RecyclerView externalServerRv = view.findViewById(R.id.external_download_rv);
        DownloadAdapter internalDownloadAdapter = new DownloadAdapter(this, listInternalDownload, true, downloadViewModel);
        internalServerRv.setLayoutManager(new LinearLayoutManager(this));
        internalServerRv.setAdapter(internalDownloadAdapter);

        DownloadAdapter externalDownloadAdapter = new DownloadAdapter(this, listExternalDownload, true, downloadViewModel);
        externalServerRv.setLayoutManager(new LinearLayoutManager(this));
        externalServerRv.setAdapter(externalDownloadAdapter);

        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openServerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_server_dialog, null);
        RecyclerView serverRv = view.findViewById(R.id.serverRv);
        serverAdapter = new ServerAdapter(this, listServer, "movie");
        serverRv.setLayoutManager(new LinearLayoutManager(this));
        serverRv.setAdapter(serverAdapter);

        ImageView closeIv = view.findViewById(R.id.close_iv);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        closeIv.setOnClickListener(v -> dialog.dismiss());

        serverAdapter.setOnItemClickListener(new ServerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, CommonModels obj, int position,
                                    ServerAdapter.OriginalViewHolder holder) {
                releasePlayer();
                //resetCastPlayer();
                preparePlayer(obj);

            }

            @Override
            public void getFirstUrl(String url) {
                mediaUrl = url;
            }

            @Override
            public void hideDescriptionLayout() {
                lPlay.setVisibility(VISIBLE);
                dialog.dismiss();

            }
        });

    }

    public void preparePlayer(CommonModels obj) {
        activeMovie = true;
        setPlayerFullScreen();
        mediaUrl = obj.getStremURL();
        if (!castSession) {
            initMoviePlayer(obj.getStremURL(), obj.getServerType(), DetailsActivity.this);

            listSub.clear();
            if (obj.getListSub() != null) {
                listSub.addAll(obj.getListSub());
            }

            if (listSub.size() != 0) {
                imgSubtitle.setVisibility(VISIBLE);
            } else {
                imgSubtitle.setVisibility(GONE);
            }

        } else {
            if (obj.getServerType().equalsIgnoreCase("embed")) {

                castSession = false;
                castPlayer.setSessionAvailabilityListener(null);
                castPlayer.release();

                // invisible control ui of exoplayer
                player.setPlayWhenReady(true);
                simpleExoPlayerView.setUseController(true);

                // invisible control ui of casting
                castControlView.setVisibility(GONE);
                chromeCastTv.setVisibility(GONE);


            } else {
                showQueuePopup(DetailsActivity.this, null, getMediaInfo());
            }
        }
    }

    void clear_previous() {
        //strCast = "";
        strDirector = "";
        strGenre = "";
        //listDownload.clear();
        listInternalDownload.clear();
        listExternalDownload.clear();
        programs.clear();
        castCrews.clear();
    }

    public void showSubtitleDialog(Context context, List<SubtitleModel> list) {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_subtitle, viewGroup, false);
        ImageView cancel = dialogView.findViewById(R.id.cancel);

        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerView);
        SubtitleAdapter adapter = new SubtitleAdapter(context, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        alertDialog = builder.create();
        alertDialog.show();

        cancel.setOnClickListener(v -> alertDialog.cancel());

    }

    @Override
    public void onCastSessionAvailable() {
        castSession = true;
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, title);
        //movieMetadata.putString(MediaMetadata.KEY_ALBUM_ARTIST, "Test Artist");
        movieMetadata.addImage(new WebImage(Uri.parse(castImageUrl)));
        MediaInfo mediaInfo = new MediaInfo.Builder(mediaUrl)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType(MimeTypes.VIDEO_UNKNOWN)
                .setMetadata(movieMetadata).build();

        //array of media sources
        final MediaQueueItem[] mediaItems = {new MediaQueueItem.Builder(mediaInfo).build()};

        castPlayer.loadItems(mediaItems, 0, 3000, Player.REPEAT_MODE_OFF);

        // visible control ui of casting
        castControlView.setVisibility(VISIBLE);
        castControlView.setPlayer(castPlayer);
        castControlView.addVisibilityListener(visibility -> {
            if (visibility == GONE) {
                castControlView.setVisibility(VISIBLE);
                chromeCastTv.setVisibility(VISIBLE);
            }
        });

        // invisible control ui of exoplayer
        player.setPlayWhenReady(false);
        simpleExoPlayerView.setUseController(false);
    }

    @Override
    public void onCastSessionUnavailable() {
        // make cast session false
        castSession = false;
        // invisible control ui of exoplayer
        player.setPlayWhenReady(true);
        simpleExoPlayerView.setUseController(true);

        // invisible control ui of casting
        castControlView.setVisibility(GONE);
        chromeCastTv.setVisibility(GONE);
    }

    public void initServerTypeForTv(String serverType) {
        DetailsActivity.serverType = serverType;
    }

    @Override
    public void onProgramClick(Program program) {
        if (program.getProgramStatus().equals("onaired") && program.getVideoUrl() != null) {
            showExoControlForTv();
            initMoviePlayer(program.getVideoUrl(), "tv", this);
            timeTv.setText(program.getTime());
            programTv.setText(program.getTitle());
        } else {
            new ToastMsg(DetailsActivity.this).toastIconError("Not Yet");
        }
    }

    //this method will be called when related tv channel is clicked
    @Override
    public void onRelatedTvClicked(CommonModels obj) {
        categoryType = obj.getVideoType();
        id = obj.getId();
        initGetData();
    }

    // this will call when any episode is clicked
    //if it is embed player will go full screen
    @Override
    public void onEpisodeItemClickTvSeries(String type, View view, EpiModel obj, int position, EpisodeAdapter.OriginalViewHolder holder) {
        if (type.equalsIgnoreCase("embed")) {
            CommonModels model = new CommonModels();
            model.setStremURL(obj.getStreamURL());
            model.setServerType(obj.getServerType());
            model.setListSub(null);
            releasePlayer();
            // resetCastPlayer();
            preparePlayer(model);
        } else {
            if (obj != null) {
                if (obj.getSubtitleList().size() != 0) {
                    listSub.clear();
                    listSub.addAll(obj.getSubtitleList());
                    imgSubtitle.setVisibility(VISIBLE);
                } else {
                    listSub.clear();
                    imgSubtitle.setVisibility(GONE);
                }

                initMoviePlayer(obj.getStreamURL(), obj.getServerType(), DetailsActivity.this);
            }
        }
    }

    private class SubtitleAdapter extends RecyclerView.Adapter<SubtitleAdapter.OriginalViewHolder> {
        private final List<SubtitleModel> items;
        private final Context ctx;

        public SubtitleAdapter(Context context, List<SubtitleModel> items) {
            this.items = items;
            ctx = context;
        }

        @NonNull
        @Override
        public SubtitleAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SubtitleAdapter.OriginalViewHolder vh;
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_subtitle, parent, false);
            vh = new SubtitleAdapter.OriginalViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(SubtitleAdapter.OriginalViewHolder holder, final int position) {
            final SubtitleModel obj = items.get(position);
            holder.name.setText(obj.getLanguage());

            holder.lyt_parent.setOnClickListener(v -> {
                setSelectedSubtitle(mediaSource, obj.getUrl(), ctx);
                alertDialog.cancel();
            });

        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class OriginalViewHolder extends RecyclerView.ViewHolder {
            public TextView name;
            private final View lyt_parent;

            public OriginalViewHolder(View v) {
                super(v);
                name = v.findViewById(R.id.name);
                lyt_parent = v.findViewById(R.id.lyt_parent);
            }
        }

    }

    private void initGetData() {
        //check vpn connection
        helperUtils = new HelperUtils(DetailsActivity.this);
        vpnStatus = helperUtils.isVpnConnectionAvailable();
        if (vpnStatus) {
            helperUtils.showWarningDialog(DetailsActivity.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
        } else {
            if (!categoryType.equals("tv")) {

                //----related rv----------
                relatedAdapter = new HomePageAdapter(this, listRelated);
                rvRelated.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                        false));
                rvRelated.setAdapter(relatedAdapter);

                if (categoryType.equals("tvseries")) {

                    seasonSpinnerContainer.setVisibility(VISIBLE);
                    rvServer.setVisibility(VISIBLE);
                    serverIv.setVisibility(GONE);

                    rvRelated.removeAllViews();
                    listRelated.clear();
                    rvServer.removeAllViews();
                    listServer.clear();

                    downloadBt.setVisibility(GONE);
                    watchNowBt.setVisibility(GONE);
                    trailerBt.setVisibility(GONE);

                    // cast & crew adapter
                    castCrewAdapter = new CastCrewAdapter(this, castCrews);
                    castRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
                    castRv.setAdapter(castCrewAdapter);

                    getSeriesData(categoryType, id);

                    if (listSub.size() == 0) {
                        imgSubtitle.setVisibility(GONE);
                    }

                } else {
                    imgFull.setVisibility(GONE);
                    listServer.clear();
                    rvRelated.removeAllViews();
                    listRelated.clear();
                    if (listSub.size() == 0) {
                        imgSubtitle.setVisibility(GONE);
                    }

                    // cast & crew adapter
                    castCrewAdapter = new CastCrewAdapter(this, castCrews);
                    castRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
                    castRv.setAdapter(castCrewAdapter);

                    getMovieData(categoryType, id);

                }

                if (PreferenceUtils.isLoggedIn(DetailsActivity.this)) {
                    getFavStatus();
                }

            } else {
                tv = true;
                imgSubtitle.setVisibility(GONE);
                llComment.setVisibility(GONE);
                serverIv.setVisibility(GONE);

                rvServer.setVisibility(VISIBLE);
                lPlay.setVisibility(VISIBLE);

                // hide exo player some control
                hideExoControlForTv();

                tvLayout.setVisibility(VISIBLE);

                // hide program guide if its disable from api
                if (!PreferenceUtils.isProgramGuideEnabled(DetailsActivity.this)) {
                    proGuideTv.setVisibility(GONE);
                    programRv.setVisibility(GONE);

                }

                watchStatusTv.setText(getString(R.string.watching_on) + " " + getString(R.string.app_name));

                tvRelated.setText(getString(R.string.all_tv_channel));

                rvServer.removeAllViews();
                listServer.clear();
                rvRelated.removeAllViews();
                listRelated.clear();

                programAdapter = new ProgramAdapter(programs, this);
                programRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                programRv.setAdapter(programAdapter);
                programAdapter.setOnProgramClickListener(this);

                //----related rv----------
                //relatedTvAdapter = new LiveTvHomeAdapter(this, listRelated, TAG);
                relatedTvAdapter = new RelatedTvAdapter(listRelated, DetailsActivity.this);
                rvRelated.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                rvRelated.setAdapter(relatedTvAdapter);
                relatedTvAdapter.setListener(DetailsActivity.this);

                imgAddFav.setVisibility(GONE);

                serverAdapter = new ServerAdapter(this, listServer, "tv");
                rvServerForTV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                rvServerForTV.setAdapter(serverAdapter);
                Log.e(TAG, "initGetData: TV");
                getTvData(categoryType, id);
                llBottom.setVisibility(GONE);

                final ServerAdapter.OriginalViewHolder[] viewHolder = {null};
                serverAdapter.setOnItemClickListener(new ServerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, CommonModels obj, int position, ServerAdapter.OriginalViewHolder holder) {
                        mediaUrl = obj.getStremURL();

                        if (!castSession) {
                            initMoviePlayer(obj.getStremURL(), obj.getServerType(), DetailsActivity.this);

                        } else {

                            if (obj.getServerType().equalsIgnoreCase("embed")) {

                                castSession = false;
                                castPlayer.setSessionAvailabilityListener(null);
                                castPlayer.release();

                                // invisible control ui of exoplayer
                                player.setPlayWhenReady(true);
                                simpleExoPlayerView.setUseController(true);

                                // invisible control ui of casting
                                castControlView.setVisibility(GONE);
                                chromeCastTv.setVisibility(GONE);
                            } else {
                                showQueuePopup(DetailsActivity.this, null, getMediaInfo());
                            }
                        }

                        serverAdapter.chanColor(viewHolder[0], position);
                        holder.name.setTextColor(getResources().getColor(R.color.colorPrimary));
                        viewHolder[0] = holder;
                    }

                    @Override
                    public void getFirstUrl(String url) {
                        mediaUrl = url;
                    }

                    @Override
                    public void hideDescriptionLayout() {

                    }
                });


            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void openWebActivity(String s) {

        if (isPlaying) {
            player.release();
        }
        progressBar.setVisibility(GONE);
        playerLayout.setVisibility(GONE);

        webView.loadUrl(s);
        webView.setWebChromeClient(new WebChromeClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setVisibility(VISIBLE);

    }

    public void initMoviePlayer(String url, String type, Context context) {
        serverType = type;
        if (type.equals("embed") || type.equals("vimeo") || type.equals("gdrive")
            /*|| type.equals("youtube-live")*/) {
            isVideo = false;

            openWebActivity(url);
        } else {
            isVideo = true;
            initVideoPlayer(url, context, type);
        }
    }

    public void initVideoPlayer(String url, Context context, String type) {
        progressBar.setVisibility(VISIBLE);
        Log.e(TAG, "initVideoPlayer: type: " + type);
        if (!categoryType.equals("tv")) {
            ContinueWatchingModel model = new ContinueWatchingModel(id, title, castImageUrl, 0, 0, url, categoryType, type);
            viewModel.insert(model);
        }

        if (player != null) {
            player.stop();
            player.release();
        }

        webView.setVisibility(GONE);
        playerLayout.setVisibility(VISIBLE);
        exoplayerLayout.setVisibility(VISIBLE);
        youtubePlayerView.setVisibility(GONE);
        swipeRefreshLayout.setVisibility(VISIBLE);

        trackSelector = new DefaultTrackSelector(DetailsActivity.this);
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this);
        renderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
        player = new SimpleExoPlayer.Builder(context, renderersFactory)
                .setTrackSelector(trackSelector)
                .build();

        Uri uri = Uri.parse(url);
        switch (type) {
            case "hls":
                mediaSource = hlsMediaSource(uri, context);
                break;
            case Constants.YOUTUBE:
                /**tag 18 : 360p, tag: 22 : 720p, 133: live*/
                extractYoutubeUrl(url, context, 18);
                // initYoutubePlayer(url);
                break;
            case Constants.YOUTUBE_LIVE:
                /**play Youtube-live video**/
                initYoutubePlayer(url);
                break;
            case "rtmp":
                mediaSource = rtmpMediaSource(uri);
                break;
            default:
                mediaSource = mediaSource(uri);
                break;
        }

        if (!type.equalsIgnoreCase(Constants.YOUTUBE) &&
                !type.equalsIgnoreCase(Constants.YOUTUBE_LIVE)) {
            try {
                player.prepare(mediaSource, true, false);
                simpleExoPlayerView.setPlayer(player);
                player.setPlayWhenReady(true);

                if (resumePosition > 0) {
                    player.seekTo(resumePosition);
                    player.setPlayWhenReady(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //add listener to player
        if (player != null) {
            player.addListener(playerListener);
        }
    }

    private void initYoutubePlayer(String url) {
        Log.e(TAG, "youtube_live: " + url);
        progressBar.setVisibility(GONE);

        playerLayout.setVisibility(GONE);
        exoplayerLayout.setVisibility(GONE);
        youtubePlayerView.setVisibility(VISIBLE);
        swipeRefreshLayout.setVisibility(VISIBLE);
        releasePlayer();
        String[] separated = url.split("=");

        YouTubePlayerFragment fragment = YouTubePlayerFragment.newInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.youtubePlayerView, fragment);
        transaction.commit();
        fragment.initialize("AIzaSyBURBj1a4kTjmOJzaZ3naLOJ7x66vEb_KI"
                , new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                        if (!wasRestored) {
                            youTubePlayer.cueVideo(separated[1]);
                            youTubePlayer.play();
                        }
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });
    }

    private final Handler handler = new Handler();
    private final Player.Listener playerListener = new Player.Listener() {
        @Override
        public void onTimelineChanged(@NonNull Timeline timeline, int reason) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playWhenReady && playbackState == Player.STATE_READY) {
                isPlaying = true;
                progressBar.setVisibility(View.GONE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!categoryType.equals("tv") && player != null) {
                            playerCurrentPosition = player.getCurrentPosition();
                            mediaDuration = player.getDuration();
                            updateContinueWatchingData();
                        }
                        handler.postDelayed(this, 1000);
                    }
                }, 1000);
            } else if (playbackState == Player.STATE_READY) {
                progressBar.setVisibility(View.GONE);
                isPlaying = false;
            } else if (playbackState == Player.STATE_BUFFERING) {
                isPlaying = false;
                progressBar.setVisibility(VISIBLE);
            } else if (playbackState == Player.STATE_ENDED) {
                //---delete into continueWatching------
                ContinueWatchingModel model = new ContinueWatchingModel(id, title,
                        castImageUrl, 0, 0, mediaUrl,
                        categoryType, serverType);
                viewModel.delete(model);

            } else {
                // player paused in any state
                isPlaying = false;
                playerCurrentPosition = player.getCurrentPosition();
                mediaDuration = player.getDuration();
            }
        }

        @Override
        public void onPlayerError(@NonNull ExoPlaybackException error) {
            isPlaying = false;
            progressBar.setVisibility(VISIBLE);
        }
    };

    private long calculateProgress(long position, long duration) {
        return (position * 100 / duration);
    }


    @SuppressLint("StaticFieldLeak")
    private void extractYoutubeUrl(String url, final Context context, final int tag) {
        Log.e("Trailer", "onExtractUrl");
        new YouTubeExtractor(context) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles != null) {

                    try {
                        Log.e("Trailer", "onPlayUrl");
                        String extractedUrl = ytFiles.get(tag).getUrl();
                        //youtubeUrl = extractedUrl;
                        MediaSource source = mediaSource(Uri.parse(extractedUrl));
                        player.prepare(source, true, false);
                        simpleExoPlayerView.setPlayer(player);
                        player.setPlayWhenReady(true);
                        if (resumePosition > 0) {
                            player.seekTo(resumePosition);
                            player.setPlayWhenReady(true);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.extract(url, true, true);
    }

    private MediaSource rtmpMediaSource(Uri uri) {
        MediaSource videoSource;
        RtmpDataSourceFactory dataSourceFactory = new RtmpDataSourceFactory();
        videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);

        return videoSource;
    }

    private MediaSource hlsMediaSource(Uri uri, Context context) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, getResources().getString(R.string.app_name)),
                bandwidthMeter);

        return new HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    private MediaSource mediaSource(Uri uri) {
        return new DefaultMediaSourceFactory(
                new DefaultHttpDataSourceFactory("exoplayer")).
                createMediaSource(uri);
    }

    public void setSelectedSubtitle(MediaSource mediaSource, String subtitle, Context context) {
        MergingMediaSource mergedSource;
        if (subtitle != null) {
            Uri subtitleUri = Uri.parse(subtitle);

            Format subtitleFormat = Format.createTextSampleFormat(
                    null, // An identifier for the track. May be null.
                    MimeTypes.TEXT_VTT, // The mime type. Must be set correctly.
                    Format.NO_VALUE, // Selection flags for the track.
                    "en"); // The subtitle language. May be null.

            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, BuildConfig.APPLICATION_ID), new DefaultBandwidthMeter());


            MediaSource subtitleSource = new SingleSampleMediaSource
                    .Factory(dataSourceFactory)
                    .createMediaSource(subtitleUri, subtitleFormat, C.TIME_UNSET);


            mergedSource = new MergingMediaSource(mediaSource, subtitleSource);
            player.prepare(mergedSource, false, false);
            player.setPlayWhenReady(true);
            //resumePlayer();

        } else {
            Toast.makeText(context, "there is no subtitle", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToFav() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        FavouriteApi api = retrofit.create(FavouriteApi.class);
        Call<FavoriteModel> call = api.addToFavorite(AppConfig.API_KEY, userId, id,
                BuildConfig.VERSION_CODE, Constants.getDeviceId(this));
        call.enqueue(new Callback<FavoriteModel>() {
            @Override
            public void onResponse(@NonNull Call<FavoriteModel> call, @NonNull retrofit2.Response<FavoriteModel> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        new ToastMsg(DetailsActivity.this).toastIconSuccess(response.body().getMessage());
                        isFav = true;
                        //imgAddFav.setBackgroundResource(R.drawable.ic_favorite_white);
                        imgAddFav.setImageResource(R.drawable.ic_favorite_white);
                    } else {
                        new ToastMsg(DetailsActivity.this).toastIconError(response.body().getMessage());
                    }
                }else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    DetailsActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(DetailsActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    new ToastMsg(DetailsActivity.this).toastIconError(getString(R.string.error_toast));
                }
            }

            @Override
            public void onFailure(@NonNull Call<FavoriteModel> call, @NonNull Throwable t) {
                new ToastMsg(DetailsActivity.this).toastIconError(getString(R.string.error_toast));

            }
        });

    }

    private void paidControl(String isPaid) {
        if (isPaid.equals("1")) {
            if (PreferenceUtils.isLoggedIn(DetailsActivity.this)) {
                if (PreferenceUtils.isActivePlan(DetailsActivity.this)) {
                    if (PreferenceUtils.isValid(DetailsActivity.this)) {
                        contentDetails.setVisibility(VISIBLE);
                        subscriptionLayout.setVisibility(GONE);
                    } else {
                        PreferenceUtils.updateSubscriptionStatus(DetailsActivity.this);
                    }
                } else {
                    contentDetails.setVisibility(GONE);
                    subscriptionLayout.setVisibility(VISIBLE);
                    releasePlayer();
                }
            } else {
                startActivity(new Intent(DetailsActivity.this, LoginActivity.class));
                finish();
            }
        } else {
            //free content
            contentDetails.setVisibility(VISIBLE);
            subscriptionLayout.setVisibility(GONE);
        }
    }

    private void getTvData(final String vType, final String vId) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SingleDetailsTVApi api = retrofit.create(SingleDetailsTVApi.class);
        Call<SingleDetailsTV> call = api.getSingleDetails(AppConfig.API_KEY, vType, vId,
                BuildConfig.VERSION_CODE, PreferenceUtils.getUserId(this), Constants.getDeviceId(this));
        call.enqueue(new Callback<SingleDetailsTV>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<SingleDetailsTV> call, @NonNull retrofit2.Response<SingleDetailsTV> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        swipeRefreshLayout.setRefreshing(false);
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(GONE);
                        if (response.body().getIsPaid().equalsIgnoreCase("1")) {
                            paidControl(response.body().getIsPaid());
                        }

                        SingleDetailsTV detailsModel = response.body();

                        title = detailsModel.getTvName();
                        tvName.setText(title);
                        tvName.setVisibility(GONE);
                        tvTitleTv.setText(title);

                        tvDes.setText(detailsModel.getDescription());
                        V_URL = detailsModel.getStreamUrl();
                        castImageUrl = detailsModel.getThumbnailUrl();

                        Picasso.get().load(detailsModel.getThumbnailUrl()).placeholder(R.drawable.album_art_placeholder)
                                .into(tvThumbIv);

                        CommonModels model = new CommonModels();
                        model.setTitle("HD");
                        model.setStremURL(V_URL);
                        model.setServerType(detailsModel.getStreamFrom());
                        listServer.add(model);

                        initMoviePlayer(detailsModel.getStreamUrl(), detailsModel.getStreamFrom(), DetailsActivity.this);

                        currentProgramTime = detailsModel.getCurrentProgramTime();
                        currentProgramTitle = detailsModel.getCurrentProgramTitle();

                        timeTv.setText(currentProgramTime);
                        programTv.setText(currentProgramTitle);
                        if (PreferenceUtils.isProgramGuideEnabled(DetailsActivity.this)) {
                            List<ProgramGuide> programGuideList = response.body().getProgramGuide();
                            for (int i = 0; i < programGuideList.size(); i++) {
                                ProgramGuide programGuide = programGuideList.get(i);
                                Program program = new Program();
                                program.setId(programGuide.getId());
                                program.setTitle(programGuide.getTitle());
                                program.setProgramStatus(programGuide.getProgramStatus());
                                program.setTime(programGuide.getTime());
                                program.setVideoUrl(programGuide.getVideoUrl());

                                programs.add(program);
                            }

                            if (programs.size() <= 0) {
                                proGuideTv.setVisibility(GONE);
                                programRv.setVisibility(GONE);
                            } else {
                                proGuideTv.setVisibility(VISIBLE);
                                programRv.setVisibility(VISIBLE);
                                programAdapter.notifyDataSetChanged();
                            }
                        }
                        //all tv channel data
                        List<AllTvChannel> allTvChannelList = response.body().getAllTvChannel();
                        for (int i = 0; i < allTvChannelList.size(); i++) {
                            AllTvChannel allTvChannel = allTvChannelList.get(i);
                            CommonModels models = new CommonModels();
                            models.setImageUrl(allTvChannel.getPosterUrl());
                            models.setTitle(allTvChannel.getTvName());
                            models.setVideoType("tv");
                            models.setIsPaid(allTvChannel.getIsPaid());
                            models.setId(allTvChannel.getLiveTvId());
                            listRelated.add(models);
                        }
                        if (listRelated.size() == 0) {
                            tvRelated.setVisibility(GONE);
                        }
                        relatedTvAdapter.notifyDataSetChanged();

                        //additional media source data
                        List<AdditionalMediaSource> serverArray = response.body().getAdditionalMediaSource();
                        for (int i = 0; i < serverArray.size(); i++) {
                            AdditionalMediaSource jsonObject = serverArray.get(i);
                            CommonModels models = new CommonModels();
                            models.setTitle(jsonObject.getLabel());
                            models.setStremURL(jsonObject.getUrl());
                            models.setServerType(jsonObject.getSource());

                            listServer.add(models);
                        }
                        serverAdapter.notifyDataSetChanged();
                    }
                }else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    DetailsActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(DetailsActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<SingleDetailsTV> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void getSeriesData(String vType, String vId) {
        Log.e(TAG, "getSeriesData: " + vId + ", userId: " + userId);
        final List<String> seasonList = new ArrayList<>();
        final List<String> seasonListForDownload = new ArrayList<>();
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SingleDetailsApi api = retrofit.create(SingleDetailsApi.class);
        Call<SingleDetails> call = api.getSingleDetails(AppConfig.API_KEY, vType, vId,
                BuildConfig.VERSION_CODE, PreferenceUtils.getUserId(this), Constants.getDeviceId(this));
        call.enqueue(new Callback<SingleDetails>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<SingleDetails> call, @NonNull retrofit2.Response<SingleDetails> response) {
                if (response.code() == 200) {
                    swipeRefreshLayout.setRefreshing(false);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(GONE);

                    SingleDetails singleDetails = response.body();
                    String isPaid = singleDetails.getIsPaid();
                    paidControl(isPaid);

                    title = singleDetails.getTitle();
                    seriesTitleTv.setText(title);
                    castImageUrl = singleDetails.getThumbnailUrl();
                    tvName.setText(title);
                    tvRelease.setText("Release On " + singleDetails.getRelease());
                    tvDes.setText(singleDetails.getDescription());

                    Picasso.get().load(singleDetails.getPosterUrl()).placeholder(R.drawable.album_art_placeholder_large)
                            .into(posterIv);
                    Picasso.get().load(singleDetails.getThumbnailUrl()).placeholder(R.drawable.poster_placeholder)
                            .into(thumbIv);

                    download_check = singleDetails.getEnableDownload();
                    trailerUrl = singleDetails.getTrailerUrl();
                    castImageUrl = singleDetails.getThumbnailUrl();
                    downloadBt.setVisibility(GONE);
                    trailerBt.setVisibility(GONE);
                    downloadAndTrailerBtContainer.setVisibility(GONE);
                    if (trailerUrl != null && !trailerUrl.equalsIgnoreCase("")) {
                        downloadAndTrailerBtContainer.setVisibility(VISIBLE);
                        trailerBt.setVisibility(VISIBLE);
                        downloadBt.setVisibility(GONE);
                    }

                    //----director---------------
                    for (int i = 0; i < singleDetails.getDirector().size(); i++) {
                        Director director = singleDetails.getDirector().get(i);
                        if (i == singleDetails.getDirector().size() - 1) {
                            strDirector = strDirector + director.getName();
                        } else {
                            strDirector = strDirector + director.getName() + ", ";
                        }
                    }
                    tvDirector.setText(strDirector);

                    //----cast---------------
                    for (int i = 0; i < singleDetails.getCast().size(); i++) {
                        Cast cast = singleDetails.getCast().get(i);

                        CastCrew castCrew = new CastCrew();
                        castCrew.setId(cast.getStarId());
                        castCrew.setName(cast.getName());
                        castCrew.setUrl(cast.getUrl());
                        castCrew.setImageUrl(cast.getImageUrl());
                        castCrews.add(castCrew);
                    }
                    castCrewAdapter.notifyDataSetChanged();
                    //---genre---------------
                    for (int i = 0; i < singleDetails.getGenre().size(); i++) {
                        Genre genre = singleDetails.getGenre().get(i);
                        if (i == singleDetails.getCast().size() - 1) {
                            strGenre = strGenre + genre.getName();
                        } else {
                            if (i == singleDetails.getGenre().size() - 1) {
                                strGenre = strGenre + genre.getName();
                            } else {
                                strGenre = strGenre + genre.getName() + ", ";
                            }
                        }
                    }
                    setGenreText();

                    //----related tv series---------------
                    for (int i = 0; i < singleDetails.getRelatedTvseries().size(); i++) {
                        RelatedMovie relatedTvSeries = singleDetails.getRelatedTvseries().get(i);

                        CommonModels models = new CommonModels();
                        models.setTitle(relatedTvSeries.getTitle());
                        models.setImageUrl(relatedTvSeries.getThumbnailUrl());
                        models.setId(relatedTvSeries.getVideosId());
                        models.setVideoType("tvseries");
                        models.setIsPaid(relatedTvSeries.getIsPaid());
                        listRelated.add(models);
                    }

                    if (listRelated.size() == 0) {
                        tvRelated.setVisibility(GONE);
                    }
                    relatedAdapter.notifyDataSetChanged();

                    //----season and episode download------------
                    for (int i = 0; i < singleDetails.getSeason().size(); i++) {
                        Season season = singleDetails.getSeason().get(i);
                        CommonModels models = new CommonModels();
                        String season_name = season.getSeasonsName();
                        models.setTitle(season.getSeasonsName());
                        seasonList.add("Season: " + season.getSeasonsName());
                        seasonListForDownload.add(season.getSeasonsName());

                        //----episode------
                        List<EpiModel> epList = new ArrayList<>();
                        for (int j = 0; j < singleDetails.getSeason().get(i).getEpisodes().size(); j++) {
                            Episode episode = singleDetails.getSeason().get(i).getEpisodes().get(j);

                            EpiModel model = new EpiModel();
                            model.setSeson(season_name);
                            model.setEpi(episode.getEpisodesName());
                            model.setStreamURL(episode.getFileUrl());
                            model.setServerType(episode.getFileType());
                            model.setImageUrl(episode.getImageUrl());
                            model.setSubtitleList(episode.getSubtitle());
                            epList.add(model);
                        }
                        models.setListEpi(epList);
                        listServer.add(models);
                    }

                     /*if season downloads are enable
                        generate a list of downloads of every season*/
                    //----download list--------
                    if (seasonList.size() > 0) {
                        setSeasonData(seasonList);
                        //check if download is enabled
                        if (singleDetails.getEnableDownload().equalsIgnoreCase("1")) {
                            setSeasonDownloadData(seasonListForDownload, singleDetails.getSeason());
                        } else {
                            seasonDownloadLayout.setVisibility(GONE);
                        }
                    } else {
                        seasonSpinnerContainer.setVisibility(GONE);
                    }
                }else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    DetailsActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(DetailsActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<SingleDetails> call, @NonNull Throwable t) {

            }
        });
    }

    public void setSeasonData(List<String> seasonData) {
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, seasonData);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        seasonSpinner.setAdapter(aa);
        seasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rvServer.removeAllViewsInLayout();
                rvServer.setLayoutManager(new LinearLayoutManager(DetailsActivity.this,
                        RecyclerView.VERTICAL, false));
                EpisodeAdapter episodeAdapter = new EpisodeAdapter(DetailsActivity.this,
                        listServer.get(i).getListEpi());
                rvServer.setAdapter(episodeAdapter);
                episodeAdapter.setOnEmbedItemClickListener(DetailsActivity.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setSeasonDownloadData(List<String> seasonListArray, List<Season> seasonList) {
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, seasonListArray);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seasonDownloadSpinner.setAdapter(arrayAdapter);

        seasonDownloadSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<DownloadLink> selectedSeasonDownloadList = new ArrayList<>(seasonList.get(position).getDownloadLinks());
                seasonDownloadRecyclerView.removeAllViewsInLayout();
                seasonDownloadRecyclerView.setLayoutManager(new LinearLayoutManager(DetailsActivity.this,
                        RecyclerView.VERTICAL, false));
                EpisodeDownloadAdapter adapter = new EpisodeDownloadAdapter(DetailsActivity.this, selectedSeasonDownloadList, downloadViewModel);
                seasonDownloadRecyclerView.setAdapter(adapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setGenreText() {
        tvGenre.setText(strGenre);
        dGenreTv.setText(strGenre);
    }

    private void getMovieData(String vType, String vId) {
        shimmerFrameLayout.setVisibility(VISIBLE);
        shimmerFrameLayout.startShimmer();
        //strCast = "";
        strDirector = "";
        strGenre = "";

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SingleDetailsApi api = retrofit.create(SingleDetailsApi.class);
        Call<SingleDetails> call = api.getSingleDetails(AppConfig.API_KEY, vType, vId,
                BuildConfig.VERSION_CODE, PreferenceUtils.getUserId(this), Constants.getDeviceId(this));
        call.enqueue(new Callback<SingleDetails>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<SingleDetails> call, @NonNull retrofit2.Response<SingleDetails> response) {
                if (response.code() == 200) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(GONE);
                    swipeRefreshLayout.setRefreshing(false);

                    SingleDetails singleDetails = response.body();
                    paidControl(singleDetails.getIsPaid());
                    download_check = singleDetails.getEnableDownload();
                    trailerUrl = singleDetails.getTrailerUrl();
                    castImageUrl = singleDetails.getThumbnailUrl();
                    if (download_check.equals("1")) {
                        downloadBt.setVisibility(VISIBLE);
                    } else {
                        downloadBt.setVisibility(GONE);
                    }
                    if (trailerUrl == null || trailerUrl.equalsIgnoreCase("")) {
                        trailerBt.setVisibility(GONE);
                    } else {
                        trailerBt.setVisibility(VISIBLE);
                    }
                    //check if download and trailer is unable or not
                    // control button container
                    if (!download_check.equalsIgnoreCase("1")) {
                        if (trailerUrl == null || trailerUrl.equalsIgnoreCase(""))
                            downloadAndTrailerBtContainer.setVisibility(GONE);
                    } else {
                        downloadAndTrailerBtContainer.setVisibility(VISIBLE);
                    }
                    title = singleDetails.getTitle();

                    tvName.setText(title);
                    tvRelease.setText("Release On " + singleDetails.getRelease());
                    tvDes.setText(singleDetails.getDescription());


                    Picasso.get().load(singleDetails.getPosterUrl()).placeholder(R.drawable.album_art_placeholder_large)
                            .into(posterIv);
                    Picasso.get().load(singleDetails.getThumbnailUrl()).placeholder(R.drawable.poster_placeholder)
                            .into(thumbIv);

                    //----director---------------
                    for (int i = 0; i < singleDetails.getDirector().size(); i++) {
                        Director director = response.body().getDirector().get(i);
                        if (i == singleDetails.getDirector().size() - 1) {
                            strDirector = strDirector + director.getName();
                        } else {
                            strDirector = strDirector + director.getName() + ", ";
                        }
                    }
                    tvDirector.setText(strDirector);

                    //----cast---------------
                    for (int i = 0; i < singleDetails.getCast().size(); i++) {
                        Cast cast = singleDetails.getCast().get(i);

                        CastCrew castCrew = new CastCrew();
                        castCrew.setId(cast.getStarId());
                        castCrew.setName(cast.getName());
                        castCrew.setUrl(cast.getUrl());
                        castCrew.setImageUrl(cast.getImageUrl());

                        castCrews.add(castCrew);
                    }
                    castCrewAdapter.notifyDataSetChanged();

                    //---genre---------------
                    for (int i = 0; i < singleDetails.getGenre().size(); i++) {
                        Genre genre = singleDetails.getGenre().get(i);
                        if (i == singleDetails.getCast().size() - 1) {
                            strGenre = strGenre + genre.getName();
                        } else {
                            if (i == singleDetails.getGenre().size() - 1) {
                                strGenre = strGenre + genre.getName();
                            } else {
                                strGenre = strGenre + genre.getName() + ", ";
                            }
                        }
                    }
                    tvGenre.setText(strGenre);
                    dGenreTv.setText(strGenre);

                    //-----server----------
                    List<Video> serverList = new ArrayList<>(singleDetails.getVideos());
                    for (int i = 0; i < serverList.size(); i++) {
                        Video video = serverList.get(i);

                        CommonModels models = new CommonModels();
                        models.setTitle(video.getLabel());
                        models.setStremURL(video.getFileUrl());
                        models.setServerType(video.getFileType());

                        if (video.getFileType().equals("mp4")) {
                            V_URL = video.getFileUrl();
                        }

                        //----subtitle-----------
                        List<Subtitle> subArray =
                                new ArrayList<>(singleDetails.getVideos().get(i).getSubtitle());
                        if (subArray.size() != 0) {

                            List<SubtitleModel> list = new ArrayList<>();
                            for (int j = 0; j < subArray.size(); j++) {
                                Subtitle subtitle = subArray.get(j);
                                SubtitleModel subtitleModel = new SubtitleModel();
                                subtitleModel.setUrl(subtitle.getUrl());
                                subtitleModel.setLanguage(subtitle.getLanguage());
                                list.add(subtitleModel);
                            }
                            if (i == 0) {
                                listSub.addAll(list);
                            }
                            models.setListSub(list);
                        } else {
                            models.setSubtitleURL(strSubtitle);
                        }
                        listServer.add(models);
                    }

                    if (serverAdapter != null) {
                        serverAdapter.notifyDataSetChanged();
                    }

                    //----related post---------------
                    for (int i = 0; i < singleDetails.getRelatedMovie().size(); i++) {
                        RelatedMovie relatedMovie = singleDetails.getRelatedMovie().get(i);
                        CommonModels models = new CommonModels();
                        models.setTitle(relatedMovie.getTitle());
                        models.setImageUrl(relatedMovie.getThumbnailUrl());
                        models.setId(relatedMovie.getVideosId());
                        models.setVideoType("movie");
                        models.setIsPaid(relatedMovie.getIsPaid());
                        models.setIsPaid(relatedMovie.getIsPaid());
                        listRelated.add(models);
                    }

                    if (listRelated.size() == 0) {
                        tvRelated.setVisibility(GONE);
                    }
                    relatedAdapter.notifyDataSetChanged();

                    //----download list---------
                    listExternalDownload.clear();
                    listInternalDownload.clear();
                    for (int i = 0; i < singleDetails.getDownloadLinks().size(); i++) {
                        DownloadLink downloadLink = singleDetails.getDownloadLinks().get(i);

                        CommonModels models = new CommonModels();
                        models.setTitle(downloadLink.getLabel());
                        models.setStremURL(downloadLink.getDownloadUrl());
                        models.setFileSize(downloadLink.getFileSize());
                        models.setResulation(downloadLink.getResolution());
                        models.setInAppDownload(downloadLink.isInAppDownload());
                        if (downloadLink.isInAppDownload()) {
                            listInternalDownload.add(models);
                        } else {
                            listExternalDownload.add(models);
                        }
                    }

                }else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    DetailsActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(DetailsActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<SingleDetails> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getFavStatus() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        FavouriteApi api = retrofit.create(FavouriteApi.class);
        Call<FavoriteModel> call = api.verifyFavoriteList(AppConfig.API_KEY, userId, id, BuildConfig.VERSION_CODE,
                Constants.getDeviceId(this));
        call.enqueue(new Callback<FavoriteModel>() {
            @Override
            public void onResponse(@NonNull Call<FavoriteModel> call, @NonNull retrofit2.Response<FavoriteModel> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        isFav = true;
                        //imgAddFav.setBackgroundResource(R.drawable.ic_favorite_white);
                        imgAddFav.setImageResource(R.drawable.ic_favorite_white);
                    } else {
                        isFav = false;
                        //imgAddFav.setBackgroundResource(R.drawable.ic_favorite_border_white);
                        imgAddFav.setImageResource(R.drawable.ic_favorite_border_white);
                    }
                    imgAddFav.setVisibility(VISIBLE);
                }else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    DetailsActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(DetailsActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<FavoriteModel> call, @NotNull Throwable t) {

            }
        });

    }

    private void removeFromFav() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        FavouriteApi api = retrofit.create(FavouriteApi.class);
        Call<FavoriteModel> call = api.removeFromFavorite(AppConfig.API_KEY, userId, id,
                BuildConfig.VERSION_CODE, Constants.getDeviceId(this));
        call.enqueue(new Callback<FavoriteModel>() {
            @Override
            public void onResponse(@NonNull Call<FavoriteModel> call, @NonNull retrofit2.Response<FavoriteModel> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        isFav = false;
                        new ToastMsg(DetailsActivity.this).toastIconSuccess(response.body().getMessage());
                        //imgAddFav.setBackgroundResource(R.drawable.ic_favorite_border_white);
                        imgAddFav.setImageResource(R.drawable.ic_favorite_border_white);
                    } else {
                        isFav = true;
                        new ToastMsg(DetailsActivity.this).toastIconError(response.body().getMessage());
                        //imgAddFav.setBackgroundResource(R.drawable.ic_favorite_white);
                        imgAddFav.setImageResource(R.drawable.ic_favorite_white);
                    }
                }else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    DetailsActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(DetailsActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<FavoriteModel> call, @NonNull Throwable t) {
                new ToastMsg(DetailsActivity.this).toastIconError(getString(R.string.fetch_error));
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void addComment(String videoId, String userId, final String comments) {

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        CommentApi api = retrofit.create(CommentApi.class);
        Call<PostCommentModel> call = api.postComment(AppConfig.API_KEY, videoId, userId, comments,
                BuildConfig.VERSION_CODE, Constants.getDeviceId(this));
        call.enqueue(new Callback<PostCommentModel>() {
            @Override
            public void onResponse(@NonNull Call<PostCommentModel> call, @NonNull retrofit2.Response<PostCommentModel> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus().equals("success")) {
                        rvComment.removeAllViews();
                        listComment.clear();
                        getComments();
                        etComment.setText("");
                        new ToastMsg(DetailsActivity.this).toastIconSuccess(response.body().getMessage());
                    } else {
                        new ToastMsg(DetailsActivity.this).toastIconError(response.body().getMessage());
                    }
                } else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    DetailsActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(DetailsActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostCommentModel> call, @NonNull Throwable t) {

            }
        });
    }

    private void getComments() {

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        CommentApi api = retrofit.create(CommentApi.class);
        Call<List<GetCommentsModel>> call = api.getAllComments(AppConfig.API_KEY, id, BuildConfig.VERSION_CODE,
                PreferenceUtils.getUserId(this), Constants.getDeviceId(this));
        call.enqueue(new Callback<List<GetCommentsModel>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<GetCommentsModel>> call,
                                   @NonNull retrofit2.Response<List<GetCommentsModel>> response) {
                if (response.code() == 200) {
                    listComment.addAll(response.body());

                    commentsAdapter.notifyDataSetChanged();
                }else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    DetailsActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(DetailsActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GetCommentsModel>> call, @NonNull Throwable t) {

            }
        });

    }

    public void hideDescriptionLayout() {
        lPlay.setVisibility(VISIBLE);
    }

    public void showSeriesLayout() {
        seriesLayout.setVisibility(VISIBLE);
    }

    public void showDescriptionLayout() {
        lPlay.setVisibility(GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isPlaying && player != null) {
            //Log.e("PLAY:::","PAUSE");
            player.setPlayWhenReady(false);

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        //castManager.removeProgressWatcher(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //updateContinueWatchingData();
        resetCastPlayer();
        releasePlayer();

    }


    @Override
    public void onBackPressed() {
        if (activeMovie) {
            setPlayerNormalScreen();
            if (player != null) {
                player.setPlayWhenReady(false);
                player.stop();
            }
            showDescriptionLayout();
            activeMovie = false;
        } else {
            releasePlayer();
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check vpn connection
        helperUtils = new HelperUtils(DetailsActivity.this);
        vpnStatus = helperUtils.isVpnConnectionAvailable();
        if (vpnStatus) {
            helperUtils.showWarningDialog(DetailsActivity.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
            player.setPlayWhenReady(false);
        }
    }

    public void releasePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
            player = null;
            simpleExoPlayerView.setPlayer(null);
            //simpleExoPlayerView = null;

        }
    }

    public void setMediaUrlForTvSeries(String url) {
        mediaUrl = url;
    }

    public boolean getCastSession() {
        return castSession;
    }

    public void resetCastPlayer() {
        if (castPlayer != null) {
            castPlayer.setPlayWhenReady(false);
            castPlayer.release();
        }
    }

    public void showQueuePopup(final Context context, View view, final MediaInfo mediaInfo) {
        CastSession castSession =
                CastContext.getSharedInstance(context).getSessionManager().getCurrentCastSession();
        if (castSession == null || !castSession.isConnected()) {
            Log.w(TAG, "showQueuePopup(): not connected to a cast device");
            return;
        }
        final RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            Log.w(TAG, "showQueuePopup(): null RemoteMediaClient");
            return;
        }

        MediaQueueItem queueItem = new MediaQueueItem.Builder(mediaInfo).setAutoplay(
                true).setPreloadTime(PRELOAD_TIME_S).build();
        MediaQueueItem[] newItemArray = new MediaQueueItem[]{queueItem};
        remoteMediaClient.queueLoad(newItemArray, 0,
                MediaStatus.REPEAT_MODE_REPEAT_OFF, null);
    }


    public MediaInfo getMediaInfo() {
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, title);
        //movieMetadata.putString(MediaMetadata.KEY_ALBUM_ARTIST, "Test Artist");
        movieMetadata.addImage(new WebImage(Uri.parse(castImageUrl)));

        return new MediaInfo.Builder(mediaUrl)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType(MimeTypes.VIDEO_UNKNOWN)
                .setMetadata(movieMetadata).build();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new ToastMsg(DetailsActivity.this).toastIconSuccess("Now You can download.");
                Log.e("value", "Permission Granted, Now you can use local drive .");
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
        }
    }

    public void hideExoControlForTv() {
        exoRewind.setVisibility(GONE);
        exoForward.setVisibility(GONE);
        liveTv.setVisibility(VISIBLE);
        seekbarLayout.setVisibility(GONE);
    }

    public void showExoControlForTv() {
        exoRewind.setVisibility(VISIBLE);
        exoForward.setVisibility(VISIBLE);
        liveTv.setVisibility(GONE);
        seekbarLayout.setVisibility(VISIBLE);
        watchLiveTv.setVisibility(VISIBLE);
        liveTv.setVisibility(GONE);
        watchStatusTv.setText(getResources().getString(R.string.watching_catch_up_tv));
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}

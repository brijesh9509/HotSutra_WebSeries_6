package app.hotsutra.live;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import app.hotsutra.live.R;

import app.hotsutra.live.utils.ApiResources;
import com.onesignal.OneSignal;
import app.hotsutra.live.network.RetrofitClient;
import app.hotsutra.live.network.apis.MovieRequestApi;
import app.hotsutra.live.utils.PreferenceUtils;
import app.hotsutra.live.utils.RtlUtils;
import app.hotsutra.live.utils.ToastMsg;
import app.hotsutra.live.utils.Tools;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.util.Objects;

import app.hotsutra.live.utils.Constants;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SettingsActivity extends AppCompatActivity {

    TextView txtAppVersion;

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
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        txtAppVersion = findViewById(R.id.txtAppVersion);

        if (!isDark) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.black_window_light));
        }

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //---analytics-----------
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "settings_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        SwitchCompat switchCompat = findViewById(R.id.notify_switch);
        LinearLayout tvTerms = findViewById(R.id.tv_term);
        LinearLayout shareLayout = findViewById(R.id.share_layout);
        LinearLayout movieRequestLayout = findViewById(R.id.movieRequestLayout);

        txtAppVersion.setText(BuildConfig.VERSION_NAME);

        SharedPreferences preferences = getSharedPreferences("push", MODE_PRIVATE);
        switchCompat.setChecked(preferences.getBoolean("status", true));

        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences("push", MODE_PRIVATE).edit();
            editor.putBoolean("status", isChecked);
            editor.apply();

            OneSignal.disablePush(!preferences.getBoolean("status", true));
        });

        tvTerms.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, TermsActivity.class)));

        shareLayout.setOnClickListener(v -> Tools.share(SettingsActivity.this, ""));

        movieRequestLayout.setOnClickListener(v -> movieRequestDialog(isDark));

    }

    private void movieRequestDialog(boolean isDark) {
        //open movie request dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.movie_request_dialog, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        TextInputEditText nameEt, emailEt, movieNameEt, messageEt;
        TextView title;
        Button sendButton, closeButton;
        nameEt = view.findViewById(R.id.nameEditText);
        emailEt = view.findViewById(R.id.emailEditText);
        movieNameEt = view.findViewById(R.id.movieNameEditText);
        messageEt = view.findViewById(R.id.messageEditText);
        sendButton = view.findViewById(R.id.sendButton);
        closeButton = view.findViewById(R.id.closeButton);
        title = view.findViewById(R.id.title);

        if (!isDark)
            title.setTextColor(getResources().getColor(R.color.colorPrimary));

        sendButton.setOnClickListener(v -> {
            String name = nameEt.getText().toString().trim();
            String email = emailEt.getText().toString().trim();
            String movieName = movieNameEt.getText().toString().trim();
            String message = messageEt.getText().toString().trim();
            String userId = PreferenceUtils.getUserId(SettingsActivity.this);
            if (!name.isEmpty() && !email.isEmpty() && !movieName.isEmpty() && !message.isEmpty()) {
                Retrofit retrofit = RetrofitClient.getRetrofitInstance();
                MovieRequestApi api = retrofit.create(MovieRequestApi.class);
                Call<ResponseBody> call = api.submitRequest(AppConfig.API_KEY, name, email, movieName,
                        message, BuildConfig.VERSION_CODE, userId, Constants.getDeviceId(this));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            new ToastMsg(getApplicationContext()).toastIconSuccess("Request submitted");
                        }else if (response.code() == 412) {
                            try {
                                if (response.errorBody() != null) {
                                    ApiResources.openLoginScreen(response.errorBody().string(),
                                            SettingsActivity.this);
                                    finish();
                                }
                            } catch (Exception e) {
                                Toast.makeText(SettingsActivity.this,
                                        e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }  else {
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
            }
        });

        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

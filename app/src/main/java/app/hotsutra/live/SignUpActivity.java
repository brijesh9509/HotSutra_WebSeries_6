package app.hotsutra.live;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import app.hotsutra.live.database.DatabaseHelper;
import app.hotsutra.live.network.RetrofitClient;
import app.hotsutra.live.network.apis.FirebaseAuthApi;
import app.hotsutra.live.network.apis.SignUpApi;
import app.hotsutra.live.network.apis.SubscriptionApi;
import app.hotsutra.live.network.model.ActiveStatus;
import app.hotsutra.live.network.model.User;
import app.hotsutra.live.utils.ApiResources;
import app.hotsutra.live.utils.Constants;
import app.hotsutra.live.utils.MyAppClass;
import app.hotsutra.live.utils.RtlUtils;
import app.hotsutra.live.utils.ToastMsg;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private static final int RC_PHONE_SIGN_IN = 1231;
    private static final int RC_FACEBOOK_SIGN_IN = 1241;
    private static final int RC_GOOGLE_SIGN_IN = 1251;
    private TextInputEditText etName, etEmail, etPass;
    private ProgressDialog dialog;
    FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

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
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = findViewById(R.id.toolbar);

        if (!isDark) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.black_window_light));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SignUp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //---analytics-----------
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "sign_up_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait");
        dialog.setCancelable(false);

        etName = findViewById(R.id.name);
        etEmail = findViewById(R.id.email);
        etPass = findViewById(R.id.password);
        Button btnSignup = findViewById(R.id.signup);
        View backgroundView = findViewById(R.id.background_view);
        progressBar = findViewById(R.id.progress_bar);
        ImageView phoneAuthButton = findViewById(R.id.phoneAuthButton);
        ImageView facebookAuthButton = findViewById(R.id.facebookAuthButton);
        ImageView googleAuthButton = findViewById(R.id.googleAuthButton);
        TextView tvLogin = findViewById(R.id.login);

        if (isDark) {
            backgroundView.setBackgroundColor(getResources().getColor(R.color.nav_head_bg));
            btnSignup.setBackground(ContextCompat.getDrawable(this, R.drawable.login_field_button_dark));
        }
        btnSignup.setOnClickListener(v -> {
            if (!isValidEmailAddress(etEmail.getText().toString())) {
                new ToastMsg(SignUpActivity.this).toastIconError("please enter valid email");
            } else if (etPass.getText().toString().equals("")) {
                new ToastMsg(SignUpActivity.this).toastIconError("please enter password");
            } else if (etName.getText().toString().equals("")) {
                new ToastMsg(SignUpActivity.this).toastIconError("please enter name");
            } else {
                String email = etEmail.getText().toString();
                String pass = etPass.getText().toString();
                String name = etName.getText().toString();
                signUp(email, pass, name);
            }
        });

        //social login button
        if (AppConfig.ENABLE_FACEBOOK_LOGIN) {
            facebookAuthButton.setVisibility(View.VISIBLE);
        }
        if (AppConfig.ENABLE_GOOGLE_LOGIN) {
            googleAuthButton.setVisibility(View.VISIBLE);
        }
        if (AppConfig.ENABLE_PHONE_LOGIN) {
            phoneAuthButton.setVisibility(View.VISIBLE);
        }

        firebaseAuth = FirebaseAuth.getInstance();

        phoneAuthButton.setOnClickListener(v -> phoneSignIn());

        facebookAuthButton.setOnClickListener(v -> facebookSignIn());

        googleAuthButton.setOnClickListener(v -> googleSignIn());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void signUp(String email, String pass, String name) {
        dialog.show();
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SignUpApi signUpApi = retrofit.create(SignUpApi.class);
        Call<User> call = signUpApi.signUp(MyAppClass.API_KEY, email, pass, name,
                BuildConfig.VERSION_CODE, Constants.getDeviceId(this));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                User user = response.body();

                if (user.getStatus().equals("success")) {
                    new ToastMsg(SignUpActivity.this).toastIconSuccess("Successfully registered");

                    // save user info to sharedPref
                    saveUserInfo(user, user.getName(), etEmail.getText().toString(),
                            user.getUserId());

                } else if (user.getStatus().equals("error")) {
                    new ToastMsg(SignUpActivity.this).toastIconError(user.getData());
                    dialog.cancel();
                }

            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                new ToastMsg(SignUpActivity.this).toastIconError("Something went wrong." + t.getMessage());
                t.printStackTrace();
                dialog.cancel();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public void saveUserInfo(User user, String name, String email, String id) {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("id", id);
        editor.putBoolean("status", true);
        editor.putBoolean(Constants.USER_LOGIN_STATUS, true);
        editor.apply();

        DatabaseHelper db = new DatabaseHelper(SignUpActivity.this);
        db.deleteUserData();
        db.insertUserData(user);
        ApiResources.USER_PHONE = user.getPhone();
        //save user login time, expire time
        updateSubscriptionStatus(user.getUserId());

    }

    private void updateSubscriptionStatus(String userId) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);

        Call<ActiveStatus> call = subscriptionApi.getActiveStatus(MyAppClass.API_KEY, userId,
                BuildConfig.VERSION_CODE, Constants.getDeviceId(this));
        call.enqueue(new Callback<ActiveStatus>() {
            @Override
            public void onResponse(@NonNull Call<ActiveStatus> call, @NonNull Response<ActiveStatus> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        ActiveStatus activeStatus = response.body();

                        DatabaseHelper db = new DatabaseHelper(SignUpActivity.this);
                        db.deleteAllActiveStatusData();
                        db.insertActiveStatusData(activeStatus);

                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                        startActivity(intent);
                        finish();
                        dialog.cancel();
                    }
                }else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    SignUpActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(SignUpActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ActiveStatus> call, @NonNull Throwable t) {
                t.printStackTrace();
                new ToastMsg(SignUpActivity.this).toastIconError(getResources().getString(R.string.something_went_wrong));
            }
        });
    }

    /*social login related task*/

    private void phoneSignIn() {
        progressBar.setVisibility(View.VISIBLE);
        if (firebaseAuth.getCurrentUser() != null) {
            if (!FirebaseAuth.getInstance().getCurrentUser().getUid().isEmpty()) {
                final String phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                //already signed in
                if (!phoneNumber.isEmpty())
                    sendDataToServer();
            }

        } else {
            progressBar.setVisibility(View.GONE);
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Collections.singletonList(
                    new AuthUI.IdpConfig.PhoneBuilder().build());
            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_PHONE_SIGN_IN);
        }
    }

    private void facebookSignIn() {
        progressBar.setVisibility(View.VISIBLE);
        if (firebaseAuth.getCurrentUser() != null) {
            if (!FirebaseAuth.getInstance().getCurrentUser().getUid().isEmpty()) {
                //already signed in
                //send data to server
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                sendFacebookDataToServer(user.getDisplayName(), String.valueOf(user.getPhotoUrl()), user.getEmail());

            }

        } else {
            progressBar.setVisibility(View.GONE);
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Collections.singletonList(
                    new AuthUI.IdpConfig.FacebookBuilder()
                            //.setPermissions(Arrays.asList("email", "default"))
                            .build());
            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false)
                            .build(),
                    RC_FACEBOOK_SIGN_IN);
        }
    }

    private void googleSignIn() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            sendGoogleDataToServer();

        } else {
            progressBar.setVisibility(View.VISIBLE);
            // Choose authentication providers
            GoogleSignInOptions googleOptions = new GoogleSignInOptions.Builder(
                    GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .requestProfile()
                    .build();


            List<AuthUI.IdpConfig> providers = Collections.singletonList(
                    new AuthUI.IdpConfig.GoogleBuilder().setSignInOptions(googleOptions).build());

           /* List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.GoogleBuilder().build());*/

            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false)
                            .build(),
                    RC_GOOGLE_SIGN_IN);
        }
    }

    private void sendDataToServer() {
        progressBar.setVisibility(View.VISIBLE);
        String phoneNo = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        FirebaseAuthApi api = retrofit.create(FirebaseAuthApi.class);
        Call<User> call = api.getPhoneAuthStatus(MyAppClass.API_KEY, uid, phoneNo,
                BuildConfig.VERSION_CODE, Constants.getDeviceId(this));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus().equals("success")) {

                        User user = response.body();
                        DatabaseHelper db = new DatabaseHelper(SignUpActivity.this);
                        db.deleteUserData();
                        db.insertUserData(user);
                        ApiResources.USER_PHONE = user.getPhone();

                        SharedPreferences.Editor preferences = getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
                        preferences.putBoolean(Constants.USER_LOGIN_STATUS, true);
                        preferences.apply();
                        //save user login time, expire time
                        updateSubscriptionStatus(user.getUserId());
                    }

                } else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    SignUpActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(SignUpActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                phoneSignIn();
            }
        });


    }

    private void sendGoogleDataToServer() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String username = user.getDisplayName();
        String email = user.getEmail();
        Uri image = user.getPhotoUrl();
        String uid = user.getUid();
        String phone = "";
        if (user.getPhoneNumber() != null)
            phone = user.getPhoneNumber();

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        FirebaseAuthApi api = retrofit.create(FirebaseAuthApi.class);
        Call<User> call = api.getGoogleAuthStatus(MyAppClass.API_KEY, uid, email, username, image, phone,
                BuildConfig.VERSION_CODE, Constants.getDeviceId(this));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus().equals("success")) {
                        User user = response.body();
                        DatabaseHelper db = new DatabaseHelper(SignUpActivity.this);
                        db.deleteUserData();
                        db.insertUserData(user);
                        ApiResources.USER_PHONE = user.getPhone();

                        SharedPreferences.Editor preferences = getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
                        preferences.putBoolean(Constants.USER_LOGIN_STATUS, true);
                        preferences.apply();

                        //save user login time, expire time
                        updateSubscriptionStatus(user.getUserId());
                    }

                }else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    SignUpActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(SignUpActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                googleSignIn();
            }
        });
    }

    private void sendFacebookDataToServer(String username, String photoUrl, String email) {
        progressBar.setVisibility(View.VISIBLE);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        FirebaseAuthApi api = retrofit.create(FirebaseAuthApi.class);
        Call<User> call = api.getFacebookAuthStatus(MyAppClass.API_KEY, uid, username, email, Uri.parse(photoUrl),
                BuildConfig.VERSION_CODE, Constants.getDeviceId(this));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus().equals("success")) {

                        User user = response.body();
                        DatabaseHelper db = new DatabaseHelper(SignUpActivity.this);
                        db.deleteUserData();
                        db.insertUserData(user);
                        ApiResources.USER_PHONE = user.getPhone();

                        SharedPreferences.Editor preferences = getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
                        preferences.putBoolean(Constants.USER_LOGIN_STATUS, true);
                        preferences.apply();

                        //save user login time, expire time
                        updateSubscriptionStatus(user.getUserId());
                    }

                } else if (response.code() == 412) {
                    try {
                        if (response.errorBody() != null) {
                            ApiResources.openLoginScreen(response.errorBody().string(),
                                    SignUpActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(SignUpActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                facebookSignIn();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHONE_SIGN_IN) {

            final IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (!user.getPhoneNumber().isEmpty()) {
                    sendDataToServer();
                } else {
                    //empty
                    phoneSignIn();
                }
            } else {
                // sign in failed
                if (response == null) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "Error !!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }

            }
        } else if (requestCode == RC_FACEBOOK_SIGN_IN) {
            final IdpResponse response = com.firebase.ui.auth.IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (!user.getUid().isEmpty()) {
                    String username = user.getDisplayName();
                    String photoUrl = String.valueOf(user.getPhotoUrl());
                    String email = user.getEmail();

                    sendFacebookDataToServer(username, photoUrl, email);

                } else {
                    //empty
                    facebookSignIn();
                }
            } else {
                // sign in failed
                if (response == null) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "Error !!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        } else if (requestCode == RC_GOOGLE_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (!user.getUid().isEmpty()) {
                    sendGoogleDataToServer();

                } else {
                    //empty
                    googleSignIn();
                }
            } else {
                // sign in failed
                if (response == null) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "Error !!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        }
    }


}

package app.hotsutra.live.bottomshit;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import app.hotsutra.live.R;
import app.hotsutra.live.database.DatabaseHelper;
import app.hotsutra.live.network.model.config.PaymentConfig;
import app.hotsutra.live.utils.Constants;

public class PaymentBottomShitDialog extends BottomSheetDialogFragment {

    private final boolean isInAppPurchase;

    public PaymentBottomShitDialog(boolean isInAppPurchase) {
        this.isInAppPurchase = isInAppPurchase;
    }

    private OnBottomShitClickListener bottomShitClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_payment_bottom_shit, container,
                false);
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        PaymentConfig config = databaseHelper.getConfigurationData().getPaymentConfig();
        CardView paypalBt, stripBt, razorpayBt, offlineBtn, googlePlay_btn;
        paypalBt = view.findViewById(R.id.paypal_btn);
        stripBt = view.findViewById(R.id.stripe_btn);
        razorpayBt = view.findViewById(R.id.razorpay_btn);
        offlineBtn = view.findViewById(R.id.offline_btn);
        googlePlay_btn = view.findViewById(R.id.googlePlay_btn);
        Space space = view.findViewById(R.id.space2);
        Space space4 = view.findViewById(R.id.space4);
        Space space6 = view.findViewById(R.id.space6);

        if (!config.getPaypalEnable()) {
            paypalBt.setVisibility(View.GONE);
            space.setVisibility(View.GONE);
        }

        if (!config.getStripeEnable()) {
            stripBt.setVisibility(View.GONE);
            space.setVisibility(View.GONE);
        }
        if (!config.getRazorpayEnable()) {
            razorpayBt.setVisibility(View.GONE);
        }
        if (!config.isOfflinePaymentEnable()) {
            offlineBtn.setVisibility(View.GONE);
            space4.setVisibility(View.GONE);
        }
        if (!isInAppPurchase) {
            googlePlay_btn.setVisibility(View.GONE);
            space6.setVisibility(View.GONE);
        }

        paypalBt.setOnClickListener(view1 -> bottomShitClickListener.onBottomShitClick(Constants.PAYPAL));

        stripBt.setOnClickListener(view12 -> bottomShitClickListener.onBottomShitClick(Constants.STRIP));

        razorpayBt.setOnClickListener(v -> bottomShitClickListener.onBottomShitClick(Constants.RAZOR_PAY));

        offlineBtn.setOnClickListener(v -> bottomShitClickListener.onBottomShitClick(Constants.OFFLINE_PAY));

        googlePlay_btn.setOnClickListener(v -> {
            dismiss();
            bottomShitClickListener.onBottomShitClick(Constants.GOOGLE_PAY);
        });

        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            bottomShitClickListener = (OnBottomShitClickListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context + " must be implemented");
        }

    }

    public interface OnBottomShitClickListener {
        void onBottomShitClick(String paymentMethodName);
    }

}


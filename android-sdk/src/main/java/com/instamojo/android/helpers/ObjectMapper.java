package com.instamojo.android.helpers;

import com.instamojo.android.models.Card;
import com.instamojo.android.models.GatewayOrder;

import java.util.HashMap;
import java.util.Map;

public class ObjectMapper {

    public static Map<String, String> populateCardRequest(GatewayOrder order, Card card,
                                                          String emiBankCode, int emiTenure) {

        //For maestro, add the default values if empty
        if (CardUtil.isMaestroCard(card.getCardNumber())) {
            if (card.getDate() == null || card.getDate().isEmpty()) {
                card.setDate("12/49");
            }

            if (card.getCvv() == null || card.getCvv().isEmpty()) {
                card.setDate("111");
            }
        }

        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("order_id", order.getPaymentOptions().getCardOptions().getSubmissionData().getOrderID());
        fieldMap.put("merchant_id", order.getPaymentOptions().getCardOptions().getSubmissionData().getMerchantID());
        fieldMap.put("payment_method_type", "CARD");
        fieldMap.put("card_number", card.getCardNumber());
        fieldMap.put("card_exp_month", card.getMonth());
        fieldMap.put("card_exp_year", card.getYear());
        fieldMap.put("card_security_code", card.getCvv());
        fieldMap.put("save_to_locker", card.canSaveCard() ? "true" : "false");
        fieldMap.put("redirect_after_payment", "true");
        fieldMap.put("format", "json");

        if (card.getCardHolderName() != null) {
            fieldMap.put("name_on_card", card.getCardHolderName());
        }

        if (order.getPaymentOptions().getEmiOptions() != null && emiBankCode != null) {
            fieldMap.put("is_emi", "true");
            fieldMap.put("emi_bank", emiBankCode);
            fieldMap.put("emi_tenure", String.valueOf(emiTenure));
        }

        return fieldMap;
    }
}

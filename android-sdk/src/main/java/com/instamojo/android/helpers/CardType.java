package com.instamojo.android.helpers;

import com.instamojo.android.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum CardType {

    VISA("Visa", R.drawable.ic_visa_card, 16, "^4.*",3),

    MASTER_CARD("Master", R.drawable.ic_master_card, 16, "^5[1-5].*",3),

    DISCOVER("Discover", R.drawable.ic_discover_card, 16, "^(6011|65|64[4-9]|622[1-9]).*",3),

    MAESTRO("Maestro", R.drawable.ic_maestro_card, 19, "^(5018|5081|5044|504681|504993|5020|502260|5038|603845|603123|6304|6759|676[1-3]|6220|504834|504817|504645|504775).*",3),

    DINERS_CLUB("Dinners club Int", R.drawable.ic_dinners_club_int_card, 14, "^(3[689]|30[0-5]|309).*",3),

    AMEX("Amex", R.drawable.ic_amex_card, 15, "^3[47].*",4),

    RUPAY("Rupay", R.drawable.ic_rupay_card, 16, "^(508227|508[5-9]|603741|60698[5-9]|60699|607[0-8]|6079[0-7]|60798[0-4]|60800[1-9]|6080[1-9]|608[1-4]|608500|6521[5-9]|652[2-9]|6530|6531[0-4]).*",3),

    UNKNOWN("Unknown", R.drawable.ic_unknown_card, 19, "[1-9]*",3);

    private final int cvvLength
            ;
    private String displayName;

    private int imageResource;

    private int numberLength;

    private String numberPattern;

    CardType(String displayName, int imageResource, int numberLength, String numberPattern,int cvvLength) {
        this.displayName = displayName;
        this.numberLength = numberLength;
        this.imageResource = imageResource;
        this.numberPattern = numberPattern;
        this.cvvLength = cvvLength;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getNumberLength() {
        return numberLength;
    }

    public boolean matches(String cardNumber) {
        Pattern pattern = Pattern.compile(this.numberPattern);
        Matcher matcher = pattern.matcher(cardNumber);
        return matcher.matches();
    }

    public int getCvvLength() {
        return cvvLength;
    }
}

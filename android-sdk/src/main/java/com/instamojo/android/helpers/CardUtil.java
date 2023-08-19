package com.instamojo.android.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class to validate card details.
 */
public class CardUtil {

    private static final String TAG = CardUtil.class.getSimpleName();

    /**
     * Luhn's algorithm implementation to validate a card number.
     *
     * @param cardNumber Card number
     * @return True for valid card number, False for invalid card number.
     */
    public static boolean isCardNumberValid(String cardNumber) {

        int cardLength = cardNumber.length();
        if (cardNumber == null || cardNumber.isEmpty() || cardLength < 4) {
            return false;
        }

        CardType cardType = CardUtil.getCardType(cardNumber);

        // No length check for MAESTRO
        if (cardType != CardType.MAESTRO && cardType.getNumberLength() != cardLength) {
            return false;
        }

        // If the card number starts with 0
        if (cardNumber.charAt(0) == 0) {
            return false;
        }

        int total = 0;
        boolean isEvenPosition = false;
        for (int i = cardLength - 1; i >= 0; i--) {

            int digit = Character.getNumericValue(cardNumber.charAt(i));

            if (isEvenPosition) {
                digit *= 2;
            }

            total += digit / 10; // For 'digit' with two digits
            total += digit % 10;

            isEvenPosition = !isEvenPosition;
        }

        return (total % 10 == 0);
    }

    /**
     * Check for Maestro Card.
     *
     * @param cardNumber Card Number
     * @return True if card is Maestro else False.
     */
    public static boolean isMaestroCard(String cardNumber) {
        return CardType.MAESTRO.matches(cardNumber);
    }

    /**
     * Returns the CardType of the card issuer.
     *
     * @param cardNumber Card number.
     * @return CardType
     */
    public static CardType getCardType(String cardNumber) {
        for (CardType cardType : CardType.values()) {
            if (cardType.matches(cardNumber)) {
                return cardType;
            }
        }

        return CardType.UNKNOWN;
    }

    /**
     * Check method to see if the card expiry date is valid.
     *
     * @param expiryDateStr Date string in the format - MM/yy.
     * @return True if the Date is expired else False.
     */

    public static boolean isDateInValid(String date) {
        if(!date.contains("/")){
            return true;
        }
        DateValidator dateValidator = new DateValidator(Calendar.getInstance());
        String [] strings =  date.split("/");
        return !dateValidator.isValidHelper(strings[0],strings[1]);
    }

}

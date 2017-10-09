package com.daunkredit.program.sulu.view.certification.status;

import com.daunkredit.program.sulu.R;

/**
 * Created by Miaoke on 11/05/2017.
 */

public enum PayMethodEnum implements InfoValueType {
    ALFAMART("ALFAMART", R.string.payment_alfmart),
    BCA("BCA",R.string.payment_bca),
    MANDIRI("MANDIRI",R.string.payment_mandiri),
    BNI("BNI",R.string.payment_bni),
    OTHERS("OTHERS",R.string.payment_others);

    private final int mStringId;
    private final String mValue;

    PayMethodEnum(String mValue, int mStringId) {
        this.mStringId = mStringId;
        this.mValue = mValue;
    }

    @Override
    public String getValue() {
        return mValue;
    }

    @Override
    public int getShowString() {
        return mStringId;
    }
}

package com.daunkredit.program.sulu.widget.selfdefdialog;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;

import java.util.ArrayList;

import static com.alibaba.mobileim.YWChannel.getResources;

/**
 * @作者:My
 * @创建日期: 2017/4/14 15:21
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */


public class StringToFormattedSpannable {
    private String mString;
    private float mIntend = 14;
    private SpannableStringBuilder mSsb;
    private int mPaddingLeft;

    public float getIntend() {
        return mIntend;
    }

    public void setIntend(float intend) {
        mIntend = intend;
    }

    public SpannableStringBuilder getSsb() {
        return mSsb;
    }

    public void setSsb(SpannableStringBuilder ssb) {
        mSsb = ssb;
    }

    public int getPaddingLeft() {
        return mPaddingLeft;
    }

    public void setPaddingLeft(int paddingLeft) {
        mPaddingLeft = paddingLeft;
    }

    public StringToFormattedSpannable(String string, boolean doFormat) {
        mString = string;
        if (mString != null) {
            formatString();
        }
    }

    private void formatString() {
        mSsb = new SpannableStringBuilder();
        if (mString.matches("%%b.*/%%b")) {
            mString = mString.replace("/%%b", "");
            mString = mString.replace("%%b", "");
            mSsb.append(mString);
            mSsb.setSpan(new StyleSpan(Typeface.BOLD),0,mString.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (mString.contains("%%p")) {
            mPaddingLeft = (int) (getResources().getDisplayMetrics().density * mIntend);

            mString = mString.replace("/%%p", "");
            mString = mString.replace("%%p", "");
            if (mString.contains("%%b")) {
                String[] result = mString.split("/%%b");
                StringBuffer sb = new StringBuffer();
                ArrayList<String> boldStrings = new ArrayList<>();
                for (int i = 0; i < result.length; i++) {
                    if (result[i].contains("%%b")) {
                        String[] split = result[i].split("%%b");
                        if (split.length >= 2) {
                            for (int i1 = 0; i1 < split.length; i1++) {
                                sb.append(split[i1]);
                            }
                            boldStrings.add(split[1]);
                        } else {
                            boldStrings.add(split[0]);
                            sb.append(split[0]);
                        }
                    } else {
                        sb.append(result[i]);
                    }
                }
                String s = sb.toString();
                mSsb.append(s);
                for (String boldString : boldStrings) {
                    mSsb.setSpan(new StyleSpan(Typeface.BOLD), s.indexOf(boldString), s.indexOf(boldString) + boldString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                mSsb.append(mString);
            }
        } else if (mString.contains("%%2p")) {
            mPaddingLeft = (int) (getResources().getDisplayMetrics().density * mIntend * 2);
            mString = mString.replace("/%%2p", "");
            mString = mString.replace("%%2p", "");

            if (mString.contains("%%b")) {
                StringBuffer sb = new StringBuffer();
                ArrayList<String> boldStrings = new ArrayList<>();
                String[] result = mString.split("/%%b");
                for (int i = 0; i < result.length; i++) {
                    if (result[i].contains("%%b")) {
                        String[] split = result[i].split("%%b");
                        if (split.length >= 2) {
                            for (int i1 = 0; i1 < split.length; i1++) {
                                sb.append(split[i1]);
                            }
                            boldStrings.add(split[1]);
                        } else {
                            boldStrings.add(split[0]);
                            sb.append(split[0]);
                        }
                    } else {
                        sb.append(result[i]);
                    }
                }
                String s = sb.toString();
                mSsb.append(s);
                for (String boldString : boldStrings) {
                    mSsb.setSpan(new StyleSpan(Typeface.BOLD), s.indexOf(boldString), s.indexOf(boldString) + boldString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                mSsb.append(mString);
            }
        } else if (mString.contains("%%3p")) {
            mPaddingLeft=(int) (getResources().getDisplayMetrics().density * mIntend * 3);
            mString = mString.replace("/%%3p", "");
            mString = mString.replace("%%3p", "");

            if (mString.contains("%%b")) {
                StringBuffer sb = new StringBuffer();
                ArrayList<String> boldStrings = new ArrayList<>();
                String[] result = mString.split("/%%b");
                for (int i = 0; i < result.length; i++) {
                    if (result[i].contains("%%b")) {
                        String[] split = result[i].split("%%b");
                        if (split.length >= 2) {
                            for (int i1 = 0; i1 < split.length; i1++) {
                                sb.append(split[i1]);
                            }
                            boldStrings.add(split[1]);
                        } else {
                            boldStrings.add(split[0]);
                            sb.append(split[0]);
                        }
                    } else {
                        sb.append(result[i]);
                    }
                }
                String s = sb.toString();
                mSsb.append(s);
                for (String boldString : boldStrings) {
                    mSsb.setSpan(new StyleSpan(Typeface.BOLD), s.indexOf(boldString), s.indexOf(boldString) + boldString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                mSsb.append(mString);
            }
        } else {
            if (mString.contains("%%b")) {
                StringBuffer sb = new StringBuffer();
                ArrayList<String> boldStrings = new ArrayList<>();
                String[] result = mString.split("/%%b");
                for (int i = 0; i < result.length; i++) {
                    if (result[i].contains("%%b")) {
                        String[] split = result[i].split("%%b");
                        if (split.length >= 2) {
                            for (int i1 = 0; i1 < split.length; i1++) {
                                sb.append(split[i1]);
                            }
                            boldStrings.add(split[1]);
                        } else {
                            boldStrings.add(split[0]);
                            sb.append(split[0]);
                        }
                    } else {
                        sb.append(result[i]);
                    }
                }
                String s = sb.toString();
                mSsb.append(s);
                for (String boldString : boldStrings) {
                    mSsb.setSpan(new StyleSpan(Typeface.BOLD), s.indexOf(boldString), s.indexOf(boldString) + boldString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                mSsb.append(mString);
            }
        }
    }
}


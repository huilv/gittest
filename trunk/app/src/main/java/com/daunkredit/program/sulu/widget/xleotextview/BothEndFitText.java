package com.daunkredit.program.sulu.widget.xleotextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.daunkredit.program.sulu.R;

import java.util.ArrayList;


/**
 * @作者:My
 * @创建日期: 2017/4/24 16:05
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class BothEndFitText extends android.support.v7.widget.AppCompatTextView {

    private int mMeasuredWidth;
    private int mContentLength;

    public void setDoIndent(boolean doIndent) {
        this.doIndent = doIndent;
    }

    private boolean doIndent;

    public BothEndFitText(Context context) {
        this(context, null);
    }

    public BothEndFitText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public BothEndFitText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public BothEndFitText(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        setLineSpacing(0, 1.3f);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BothEndFitText);
        doIndent = typedArray.getBoolean(R.styleable.BothEndFitText_isIndent, true);
        CharSequence text = getText();
        if (text != null) {
            setJustedText(text);
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        mMeasuredWidth = getMeasuredWidth();
        mContentLength = mMeasuredWidth - getPaddingLeft() - getPaddingRight();
    }


    public void setJustedText(final CharSequence text) {
        if (text != null) {

            post(new Runnable() {
                @Override
                public void run() {
                    String justify = justify(text.toString());
                    setText(justify);
                }
            });
        }
    }

    public String justify(String text) {
        if (mContentLength == 0) {
            return null;
        }
        String tempText;
        StringBuffer resultText = new StringBuffer("");
        Paint paint = getPaint();

        ArrayList<String> paraList = new ArrayList<String>();
        paraList = paraBreak(text);
        if (doIndent) {
            for (int i = 0; i < paraList.size(); i++) {
                ArrayList<String> lineList = lineBreak("%%t" + paraList.get(i).trim(), paint, mContentLength);
                tempText = TextUtils.join(" ", lineList).replaceFirst("\\s*", "");
                resultText.append(tempText.replaceFirst("\\s*", "") + "\n" + "\n");
            }
            return resultText.toString().replace("%%t", "\t" + " " + "\t");

        } else {
            for (int i = 0; i < paraList.size(); i++) {
                ArrayList<String> lineList = lineBreak(paraList.get(i).trim(), paint, mContentLength);
                tempText = TextUtils.join(" ", lineList).replaceFirst("\\s*", "");
                resultText.append(tempText.replaceFirst("\\s*", "") + "\n");
            }
            return resultText.toString();
        }
    }

    //分开每个段落
    public ArrayList<String> paraBreak(String text) {
        ArrayList<String> paraList = new ArrayList<String>();
        String[] paraArray = text.split("\\n+");
        for (String para : paraArray) {
            paraList.add(para);
        }
        return paraList;
    }

    //分开每一行，使每一行填入最多的单词数
    private ArrayList<String> lineBreak(String text, Paint paint, float contentWidth) {
        String[] wordArray = text.split("\\s");
        ArrayList<String> lineList = new ArrayList<String>();
        StringBuffer myText = new StringBuffer("");
        for (int i = 0; i < wordArray.length; i++) {
            if (paint.measureText(myText.append(" " + wordArray[i]).toString()) <= contentWidth)
                continue;
            else {
                int start = myText.lastIndexOf(" " + wordArray[i]);
                myText = myText.delete(start, myText.length());
                int totalSpacesToInsert = (int) ((contentWidth - paint.measureText(myText.toString())) / paint.measureText(" "));
                lineList.add(justifyLine(myText.toString(), totalSpacesToInsert));
                myText = new StringBuffer(wordArray[i]);
            }
        }
        lineList.add(myText.toString());
        return lineList;
    }

    //已填入最多单词数的一行，插入对应的空格数直到该行满
    private String justifyLine(String text, int totalSpacesToInsert) {
        String[] wordArray = text.split("\\s");
        String toAppend = " ";
        int temp = totalSpacesToInsert;

        while ((totalSpacesToInsert) >= (wordArray.length - 1)) {
            toAppend = toAppend + " ";
            totalSpacesToInsert = totalSpacesToInsert - (wordArray.length - 1);
        }
        int i = 0;
        StringBuffer justifiedText = new StringBuffer("");
        for (String word : wordArray) {
            if (i < totalSpacesToInsert)
                justifiedText.append(word + " " + toAppend);

            else
                justifiedText.append(word + toAppend);
            i++;
        }
        return justifiedText.toString();
    }
}

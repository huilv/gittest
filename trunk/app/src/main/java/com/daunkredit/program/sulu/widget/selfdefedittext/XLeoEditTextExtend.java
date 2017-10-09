package com.daunkredit.program.sulu.widget.selfdefedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.common.utils.DensityUtils;
import com.daunkredit.program.sulu.widget.adapter.SelfDefTextWatcher;

/**
 * @作者:My
 * @创建日期: 2017/4/1 10:36
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class XLeoEditTextExtend extends LinearLayout {
    private Button   mEndButton;
    private TextView mPreText;
    private EditText mEditText;
    private int defTextSize = DensityUtils.dp2px(getContext(), 14);
    private OnCheckInputResult       mOnCheckInputResult;
    private OnEndButtonClickListener mOnEndButtonClickListener;

    public XLeoEditTextExtend(Context context) {
        this(context, null);
    }

    public XLeoEditTextExtend(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XLeoEditTextExtend(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public XLeoEditTextExtend(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs) {
        setClickable(true);
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.XLeoEditTextExtend);
        int anInt = attr.getInt(R.styleable.XLeoEditTextExtend_customStyleExtend, 1);
        if (anInt == 0) {
            setBackgroundResource(R.drawable.selector_edittext_input_extexd);
        } else {
        }
        int resourceId = attr.getResourceId(R.styleable.XLeoEditTextExtend_endButton, -1);
        if (resourceId != -1) {
            createEndButton(context, resourceId);
        }

        String string = attr.getString(R.styleable.XLeoEditTextExtend_preText);
        if (string != null) {
            mPreText = new TextView(context);
            mPreText.setText(string);
        }
        createEditText(context, attr);
        constructView();
    }

    private void createEndButton(Context context, int resourceId) {
        mEndButton = new Button(context);
        if (mOnEndButtonClickListener == null) {
            mOnEndButtonClickListener = new OnEndButtonClickListener() {
                @Override
                public void onClick(XLeoEditTextExtend v) {
                    v.setText(null);
                }
            };
        }
        mEndButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnEndButtonClickListener.onClick(XLeoEditTextExtend.this);
            }
        });
        mEndButton.setBackgroundResource(resourceId);
    }

    private void constructView() {

        setOrientation(LinearLayout.HORIZONTAL);
        if (mPreText != null) {
            LinearLayout.LayoutParams preParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            preParams.gravity = Gravity.CENTER;
            mPreText.setLayoutParams(preParams);
            addView(mPreText);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.width = 0;
        params.weight = 1;
        mEditText.setLayoutParams(params);
        addView(mEditText);
        if (mEndButton != null) {
            LinearLayout.LayoutParams endParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            endParams.gravity = Gravity.CENTER;
            mEndButton.setLayoutParams(endParams);
            addView(mEndButton);
        }
    }

    private void createEditText(Context context, TypedArray attr) {
        mEditText = new EditText(context);
        mEditText.setHighlightColor(Color.parseColor("#FF8636"));
        //        mEditText.clearFocus();
        mEditText.setBackground(null);
        String hint = attr.getString(R.styleable.XLeoEditTextExtend_hint);
        mEditText.setHint(hint);
        String text = attr.getString(R.styleable.XLeoEditTextExtend_text);
        mEditText.setText(text);
        int color = attr.getColor(R.styleable.XLeoEditTextExtend_hintColor, Color.GRAY);
        mEditText.setHintTextColor(color);
        int textColor = attr.getColor(R.styleable.XLeoEditTextExtend_textColor, Color.BLACK);
        mEditText.setTextColor(textColor);
        float textSize = attr.getDimension(R.styleable.XLeoEditTextExtend_textSize, defTextSize);
        textSize = DensityUtils.px2sp(context, textSize);
        mEditText.setTextSize(textSize);
        if (mPreText != null) {
            mPreText.setTextColor(Color.parseColor("#53c792"));
            mPreText.setTextSize(textSize);
        }
        int inputType = attr.getInt(R.styleable.XLeoEditTextExtend_inputType, 1);
        if (inputType == 0) {
            mEditText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        } else if (inputType == 1) {
            mEditText.setInputType(EditorInfo.TYPE_TEXT_VARIATION_NORMAL);
        }
        int lines = attr.getInt(R.styleable.XLeoEditTextExtend_maxLines, -1);
        mEditText.setMaxLines(lines == -1 ? Integer.MAX_VALUE : lines);
        int imeOptions = attr.getInt(R.styleable.XLeoEditTextExtend_imeOptions, 0);
        if (imeOptions == 0) {
            mEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        } else if (imeOptions == 1) {
            mEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
        mEditText.setImeActionLabel("Next", 5);
        int gravity = mEditText.getGravity();
        if (gravity != Gravity.CENTER_VERTICAL && gravity != Gravity.CENTER_VERTICAL && gravity != Gravity.CENTER) {
            mEditText.setGravity(Gravity.CENTER_VERTICAL);
        }
        if (!isFocusable()) {
            setFocusable(true);
        }
        if (!isFocusableInTouchMode()) {
            setFocusableInTouchMode(true);
        }
        mEditText.setImeActionLabel(context.getString(R.string.text_next), EditorInfo.IME_ACTION_NEXT);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (mOnCheckInputResult != null && actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (!mOnCheckInputResult.onCheckResult(mEditText)) {
                        mOnCheckInputResult.onWrong(mEditText);
                    } else {
                        mOnCheckInputResult.onRight(mEditText);
                    }
                    return true;
                }
                return false;
            }
        });

        //设置光标的可见性
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.requestFocus();
                mEditText.setCursorVisible(true);
                showInputSoftware();
            }
        });
        mEditText.setCursorVisible(false);
        hideInputSoftware();


        mEditText.addTextChangedListener(new SelfDefTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                super.beforeTextChanged(s, start, count, after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mOnCheckInputResult != null) {
                    mOnCheckInputResult.onReEdit(mEditText);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);

            }
        });
        //            if (Build.VERSION.SDK_INT >= 21) {
        //                mEditText.setShowSoftInputOnFocus(false);
        //            }
        mEditText.setCursorVisible(true);

        mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //                        showInputSoftware();
                    setActivated(true);
                } else {
                    //                        hideInputSoftware();
                    setActivated(false);
                }
            }
        });
}


    public void setOnCheckInputResult(OnCheckInputResult l) {
        mOnCheckInputResult = l;
    }

    public EditText getEditText() {
        return mEditText;
    }

    public void setText(CharSequence c) {
        mEditText.setText(c);
    }

    public CharSequence getText() {
        return mEditText.getText();
    }

    public void setOnEndButtonClickListener(OnEndButtonClickListener l) {
        mOnEndButtonClickListener = l;
    }

public interface OnEndButtonClickListener {
    void onClick(XLeoEditTextExtend v);

}

    public void toggleSoftInput() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, 0);
    }

    public void showInputSoftware() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(this, 0);
    }

    public void hideInputSoftware() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindowToken(), 0);
    }
}

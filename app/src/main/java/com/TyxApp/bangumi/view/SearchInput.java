package com.TyxApp.bangumi.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.TyxApp.bangumi.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

public class SearchInput extends RelativeLayout {
    private AppCompatEditText mEditText;
    private ImageButton clearButton;
    private OnTextChangeListener mOnTextChangeListener;
    private OnFocusChangeListener mOnFocusChangeListener;

    public SearchInput(Context context) {
        this(context, null);
    }

    public SearchInput(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchInput(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        intiChildView();
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == VISIBLE) {
            mEditText.requestFocus();
        }
        super.setVisibility(visibility);
    }

    public void editTextRequestFocus() {
        mEditText.requestFocus();
    }

    public void editTextClearFocus() {
        mEditText.clearFocus();
    }

    private void intiChildView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_searchinput, this, false);
        mEditText = view.findViewById(R.id.input);
        clearButton = view.findViewById(R.id.bt_clear);
        clearButton.setOnClickListener(v -> {
            mEditText.setText(null);
            if (!mEditText.isFocused()) {
                editTextRequestFocus();
            }
        });


        mEditText.setOnFocusChangeListener((v, hasFocus) -> {
            InputMethodManager methodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (mOnFocusChangeListener != null) {
                mOnFocusChangeListener.onFocusChange(v, hasFocus);
            }
            if (hasFocus) {
                methodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
            } else {
                methodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });


        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String word = s.toString();
                if (mOnTextChangeListener != null) {
                    mOnTextChangeListener.onTextChange(word);
                }
                if (TextUtils.isEmpty(word)) {
                    clearButton.setVisibility(GONE);
                } else {
                    if (clearButton.getVisibility() == GONE) {
                        clearButton.setVisibility(VISIBLE);
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) { }
        });
        addView(view);
    }

    public void setText(String text) {
        mEditText.setText(text);
    }

    public String getText() {
        return mEditText.getText().toString();
    }

    public AppCompatEditText getEditText() {
        return mEditText;
    }

    public interface OnTextChangeListener {
        void onTextChange(String text);
    }

    public void setOnTextChangeListener(OnTextChangeListener onTextChangeListener) {
        mOnTextChangeListener = onTextChangeListener;
    }

    public interface OnFocusChangeListener {
        void onFocusChange(View view, boolean hasFocus);
    }

    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        mOnFocusChangeListener = onFocusChangeListener;
    }

    public void removeOnFocusChangeListener() {
        mOnFocusChangeListener = null;
    }

    public void removeOnTextChangeListener() {
        mOnTextChangeListener = null;
    }

}

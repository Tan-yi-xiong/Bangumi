package com.TyxApp.bangumi.player.bottomsheet;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.SwitchCompat;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.util.PreferenceUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DanmakuSetingBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_KEY = "D_A_K";
    @BindView(R.id.switchDanmaku)
    SwitchCompat switchDanmaku;
    @BindView(R.id.danmakuTextSizeSeekBar)
    AppCompatSeekBar danmakuTextSizeSeekBar;
    @BindView(R.id.danmakuTextSizeTextView)
    TextView danmakuTextSizeTextView;
    @BindView(R.id.danmakuMaxLineSeekBar)
    AppCompatSeekBar danmakuMaxLineSeekBar;
    @BindView(R.id.danmakuMaxLineTextView)
    TextView danmakuMaxLineTextView;
    private Unbinder mUnbinder;
    private OnCheckedChangeListener mListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_danmaku_setting_bottomsheet, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        boolean isShow = getArguments().getBoolean(ARG_KEY);
        switchDanmaku.setChecked(isShow);
        switchDanmaku.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mListener != null) {
                    mListener.onCheckedChanged(isChecked);
                }
            }
        });

        //弹幕字体设置
        int size = PreferenceUtil.getInt(getString(R.string.key_danmaku_textsize), 70);
        danmakuTextSizeSeekBar.setProgress(size);
        danmakuTextSizeTextView.setText(size + "%");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            danmakuTextSizeSeekBar.setMin(50);
        }
        danmakuTextSizeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    if (progress < 50) {
                        danmakuTextSizeSeekBar.setProgress(50);
                    }
                }
                danmakuTextSizeTextView.setText(seekBar.getProgress() + "%");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                PreferenceUtil.setInt(getString(R.string.key_danmaku_textsize), seekBar.getProgress());
            }
        });

        //弹幕行数设置
        int maxLine = PreferenceUtil.getInt(getString(R.string.key_danmaku_maxline), 3);
        danmakuMaxLineTextView.setText(String.valueOf(maxLine));
        danmakuMaxLineSeekBar.setProgress(maxLine);
        danmakuMaxLineSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                PreferenceUtil.setInt(getString(R.string.key_danmaku_maxline), seekBar.getProgress());
                danmakuMaxLineTextView.setText(String.valueOf(seekBar.getProgress()));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置BottomSheet全展开
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        FrameLayout frameLayout = dialog.getDelegate().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(frameLayout)
                .setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }

    public static DanmakuSetingBottomSheet newInstance(boolean isDanmakuShow) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_KEY, isDanmakuShow);
        DanmakuSetingBottomSheet fragment = new DanmakuSetingBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mListener = listener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(boolean isCheck);
    }

    class OnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}

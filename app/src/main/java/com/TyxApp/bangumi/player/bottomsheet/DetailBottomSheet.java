package com.TyxApp.bangumi.player.bottomsheet;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.util.AnimationUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DetailBottomSheet extends BottomSheetDialogFragment {
    @BindView(R.id.cover)
    ImageView cover;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.ji)
    TextView ji;
    @BindView(R.id.niandai)
    TextView niandai;
    @BindView(R.id.type)
    TextView type;
    @BindView(R.id.cast)
    TextView cast;
    @BindView(R.id.staff)
    TextView staff;
    @BindView(R.id.intro)
    TextView intro;
    private Unbinder mUnbinder;

    private static final String ARGS_KEY = "a_k";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_bottom_sheet, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        Bangumi bangumi = getArguments().getParcelable(ARGS_KEY);
        name.setText(bangumi.getName());
        ji.setText(bangumi.getLatestJi());
        niandai.setText(bangumi.getNiandai());
        intro.setText(bangumi.getIntro());
        type.setText(bangumi.getType());
        cast.setText(bangumi.getCast());
        staff.setText(bangumi.getStaff());
        Glide.with(this).load(bangumi.getCover())
                .transform(new CenterCrop(), new RoundedCorners(AnimationUtil.dp2px(requireContext(), 3)))
                .into(cover);
    }

    @OnClick(R.id.close)
    public void onClick(View view) {
        dismiss();
    }

    public static DetailBottomSheet newInstance(Bangumi bangumi) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_KEY, bangumi);
        DetailBottomSheet fragment = new DetailBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }
}

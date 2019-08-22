package com.TyxApp.bangumi.player.bottomsheet;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.util.ExceptionUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public abstract class BasePlayerBottomSheet extends BottomSheetDialogFragment {
    public static final String ARGUMENT_TAG = "a_t";
    BaseAdapter.OnItemClickListener mClickListener;
    private RecyclerView.Adapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = new RecyclerView(requireContext());
        recyclerView.setBackgroundColor(Color.WHITE);
        mAdapter = getAdapter();
        ExceptionUtil.checkNull(mAdapter, "继承BasePlayerBottomSheet必须传进一个Adapter");
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false));
        return recyclerView;
    }

    abstract RecyclerView.Adapter getAdapter();

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().setOnShowListener(null);
        getDialog().setOnCancelListener(null);
        getDialog().setOnDismissListener(null);
    }

    public void setOnItemClickListener(BaseAdapter.OnItemClickListener onItemClickListener) {
        mClickListener = onItemClickListener;
    }

    @Override
    public void onDestroyView() {
        RecyclerView recyclerView = (RecyclerView) getView();
        recyclerView.removeAllViews();
        super.onDestroyView();
    }
}

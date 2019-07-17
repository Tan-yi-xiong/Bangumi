package com.TyxApp.bangumi.player.bottomsheet;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VideoSpeedBottomSheet extends BasePlayerBottomSheet {
    @Override
    RecyclerView.Adapter getAdapter() {
        String[] speedTexts = getArguments().getStringArray(ARGUMENT_TAG);
        return new BaseAdapter<String, BaseViewHolder>(Arrays.asList(speedTexts), requireContext()) {
            @NonNull
            @Override
            public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return BaseViewHolder.get(getContext(), parent, R.layout.item_search_history);
            }

            @Override
            public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
                TextView textView = holder.getView(R.id.tv_history_word);
                textView.setTextColor(Color.BLACK);
                textView.setText(getData(position));

                holder.getView(R.id.hint_imageView).setVisibility(View.GONE);

                holder.itemView.setOnClickListener(v -> {
                    if (mClickListener != null) {
                        mClickListener.onItemClick(position);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return getDataList().size();
            }
        };
    }


    public static VideoSpeedBottomSheet newInstance(String[] speedTexts) {
        Bundle args = new Bundle();
        args.putStringArray(ARGUMENT_TAG, speedTexts);
        VideoSpeedBottomSheet fragment = new VideoSpeedBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }
}

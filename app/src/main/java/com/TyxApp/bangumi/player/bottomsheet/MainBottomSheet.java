package com.TyxApp.bangumi.player.bottomsheet;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.TyxApp.bangumi.BangumiApp;
import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MainBottomSheet extends BasePlayerBottomSheet {

    public static MainBottomSheet newInstance(String[] itemText) {
        Bundle bundle = new Bundle();
        bundle.putStringArray(ARGUMENT_TAG, itemText);
        MainBottomSheet bottomSheet = new MainBottomSheet();
        bottomSheet.setArguments(bundle);
        return bottomSheet;
    }

    @Override
    RecyclerView.Adapter getAdapter() {
        List<String> itemTexts = Arrays.asList(getArguments().getStringArray(ARGUMENT_TAG));
        return new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history, parent, false);
                return new RecyclerView.ViewHolder(view) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                int imageResId = R.drawable.ic_bottom_sheet_replay;
                if (position == 1) {
                    imageResId = R.drawable.ic_bottomsheet_motion_video;
                } else if (position == 2) {
                    imageResId = R.drawable.ic_bottom_sheet_download;
                } else if (position == 3) {
                    imageResId = R.drawable.ic_danmaku;
                }
                ImageView hintImage = holder.itemView.findViewById(R.id.hint_imageView);
                hintImage.setImageResource(imageResId);
                hintImage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(
                        getContext(), R.color.grey_500)));

                TextView textView = holder.itemView.findViewById(R.id.tv_history_word);
                textView.setTextColor(Color.BLACK);
                textView.setText(itemTexts.get(position));

                holder.itemView.setOnClickListener(v -> {
                    if (mClickListener != null) {
                        mClickListener.onItemClick(position);
                    }
                });
            }
            @Override
            public int getItemCount() {
                return itemTexts.size();
            }
        };

    }
}

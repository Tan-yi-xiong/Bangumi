package com.TyxApp.bangumi.main.bangumi.adapter.Dilidili;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.data.source.remote.Dilidili;
import com.TyxApp.bangumi.player.adapter.RecommendAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MoreNewBangumiFragment extends BottomSheetDialogFragment {
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private Unbinder mUnbinder;
    private boolean isFristLoading = true;
    private Disposable mDisposable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bottomsheet_dilidli_newbangumi, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick(R.id.close)
    public void onClick(View view) {
        dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFristLoading) {
            isFristLoading = false;
            mDisposable = Dilidili.getInstance()
                    .getCategoryBangumis("最近更新")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            bangumis -> {
                                RecommendAdapter recommendAdapter = new RecommendAdapter(requireActivity());
                                mRecyclerView.setAdapter(recommendAdapter);
                                recommendAdapter.addAllInserted(bangumis);
                                mRecyclerView.setVisibility(View.VISIBLE);
                                mProgressBar.setVisibility(View.GONE);
                            },
                            throwable -> {
                                mProgressBar.setVisibility(View.GONE);
                                Toast.makeText(requireContext(), "发生错误", Toast.LENGTH_SHORT).show();
                            });
        }
    }

    public static MoreNewBangumiFragment newInstance() {
        return new MoreNewBangumiFragment();
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        super.onDestroyView();
    }
}

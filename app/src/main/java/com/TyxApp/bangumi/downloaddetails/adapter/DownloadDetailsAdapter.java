package com.TyxApp.bangumi.downloaddetails.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.bean.VideoDownloadTask;
import com.TyxApp.bangumi.player.LocalPlayerActivity;
import com.TyxApp.bangumi.player.PlayerActivity;
import com.TyxApp.bangumi.server.DownloadBinder;
import com.TyxApp.bangumi.server.DownloadServer;

import java.io.File;
import java.util.List;

public class DownloadDetailsAdapter extends BaseAdapter<VideoDownloadTask, BaseViewHolder> {
    private DownloadBinder mDownloadBinder;
    private ServiceConnection mConnection;

    public DownloadDetailsAdapter(Context context) {
        super(context);
        bindService();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BaseViewHolder.get(getContext(), parent, R.layout.item_download_detail);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        VideoDownloadTask task = getData(position);
        holder.setText(R.id.file_name, task.getFileName());
        holder.setText(R.id.file_total, byteToMB(task.getTotal()));
        ImageButton stateButton = holder.getView(R.id.stateButton);
        ProgressBar progressBar = holder.getView(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax((int) task.getTotal());
        progressBar.setProgress((int) task.getDownloadLength());
        stateButton.setVisibility(View.VISIBLE);
        stateButton.setSelected(true);

        if (mDownloadBinder == null) {
            bindService();
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (mOnItemLongClickLisener != null) {
                return mOnItemLongClickLisener.onItemLongClick(position);
            }
            return true;
        });

        if (task.getState() == DownloadServer.STATE_FINISH) {
            holder.setText(R.id.download_state, "下载完成");
            stateButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.INVISIBLE);
            holder.itemView.setOnClickListener(v -> {
                if (!new File(task.getPath()).exists()) {
                    Toast.makeText(getContext(), getContext().getString(R.string.toast_file_already_delete), Toast.LENGTH_SHORT).show();
                } else {
                    LocalPlayerActivity.startLocalPlayerActivity(getContext(), task.getPath());
                }
            });

        } else if (task.getState() == DownloadServer.STATE_DOWNLOADING) {
            stateButton.setSelected(false);
            if (mDownloadBinder != null) {
                mDownloadBinder.setProgressUpdateLintener((progress, total) -> {
                    String progressText = (int) (((float) progress / (float) total) * 100) + "%";
                    holder.setText(R.id.download_state, progressText);
                    progressBar.setProgress((int) progress);
                    progressBar.setMax((int) total);
                });
            }
            stateButton.setOnClickListener(v -> mDownloadBinder.pause());

        } else if (task.getState() == DownloadServer.STATE_ERROR || task.getState() == DownloadServer.STATE_PAUSE) {
            stateButton.setOnClickListener(v -> mDownloadBinder.addTask(task));//重新加入队列
            if (task.getState() == DownloadServer.STATE_ERROR) {
                holder.setText(R.id.download_state, "下载出错");
            } else {
                holder.setText(R.id.download_state, "下载暂停");
            }

        } else if (task.getState() == DownloadServer.STATE_AWAIT) {
            holder.setText(R.id.download_state, "等待中");
            stateButton.setOnClickListener(v -> mDownloadBinder.setInterruptTask(task));

        }
    }

    private void bindService() {
        if (mConnection == null) {
            mConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    mDownloadBinder = (DownloadBinder) service;
                    notifyDataSetChanged();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            };
            Intent intent = new Intent(getContext(), DownloadServer.class);
            getContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private String byteToMB(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size > kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format("%d B", size);
        }
    }

    @Override
    public void remove(int position) {
        VideoDownloadTask task = getData(position);
        if (task.getState() == DownloadServer.STATE_DOWNLOADING) {
            if (mDownloadBinder != null) {
                mDownloadBinder.pause();
            }
        }
    }

    public void unbindService() {
        getContext().unbindService(mConnection);
    }

    public void notifyDataSetChanged(List<VideoDownloadTask> tasks) {
        getDataList().clear();
        getDataList().addAll(tasks);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return getDataList().size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        super.onAttachedToRecyclerView(recyclerView);
    }
}

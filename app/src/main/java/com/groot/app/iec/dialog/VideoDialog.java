package com.groot.app.iec.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.youtube.player.YouTubeThumbnailView;
import com.groot.app.iec.R;

import java.util.Objects;

public class VideoDialog extends Dialog {
    public AppCompatImageView btnClose;
    public YouTubeThumbnailView youtubePlayerThumbnail;
    public ImageView btnYoutubePlay;
    private Context mContext;

    public VideoDialog(Context context) {
        super(context);
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_video);
        btnClose = findViewById(R.id.btnClose);
        youtubePlayerThumbnail = findViewById(R.id.youtube_player);
        btnYoutubePlay = findViewById(R.id.btn_youtube_play);

        View v = Objects.requireNonNull(getWindow()).getDecorView();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        v.setBackgroundResource(R.color.black_transperent);
        setCanceledOnTouchOutside(true);
        setCancelable(false);
    }
}
package com.groot.app.iec.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;


import com.groot.app.iec.R;

import java.util.Objects;

public class AppUpdateDialog extends Dialog {
    public RelativeLayout btnSkip,btnUpdate;
    private Context mContext;

    public AppUpdateDialog(Context context) {
        super(context);
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_app_update);
        btnSkip = findViewById(R.id.btnSkip);
        btnUpdate = findViewById(R.id.btnUpdate);

        View v = Objects.requireNonNull(getWindow()).getDecorView();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        v.setBackgroundResource(android.R.color.transparent);
        setCanceledOnTouchOutside(true);
        setCancelable(false);
    }
}
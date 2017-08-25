package com.google.android.apps.muzei.gallery;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class GalleryImportPhotosDialogFragment extends DialogFragment {
    public static final String TAG = "GalleryImportPhotosDialogFragment";

    private List<ActivityInfo> mGetContentActivities = new ArrayList<>();
    private OnRequestContentListener mListener;
    private ArrayAdapter<CharSequence> mAdapter;

    public static GalleryImportPhotosDialogFragment createInstance() {
        return new GalleryImportPhotosDialogFragment();
    }

    public GalleryImportPhotosDialogFragment () {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TypedArray a = getContext().obtainStyledAttributes(null, R.styleable.AlertDialog,
                R.attr.alertDialogStyle, 0);
        @LayoutRes int listItemLayout = a.getResourceId(R.styleable.AlertDialog_listItemLayout, 0);
        a.recycle();
        mAdapter = new ArrayAdapter<>(getContext(), listItemLayout);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.gallery_import_dialog_title)
                .setAdapter(mAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.requestGetContent(mGetContentActivities.get(which));
                    }
                }).create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRequestContentListener) {
            mListener = (OnRequestContentListener) context;
        } else {
            throw new IllegalArgumentException(context.getClass().getSimpleName() + " must implement OnRequestContentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGetContentActivities = mListener.getGetContentActivityInfoList();
        if (mAdapter == null || mGetContentActivities == null || mGetContentActivities.isEmpty()) {
            this.dismiss();
            return;
        }

        updateAdapter();
    }

    private void updateAdapter() {
        PackageManager packageManager = getContext().getPackageManager();
        int size = mGetContentActivities.size();
        List<CharSequence> items = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            items.add(mGetContentActivities.get(i).loadLabel(packageManager));
        }
        mAdapter.clear();
        mAdapter.addAll(items);
        mAdapter.notifyDataSetChanged();
    }

    public interface OnRequestContentListener {
        void requestGetContent(ActivityInfo info);
        List<ActivityInfo> getGetContentActivityInfoList();
    }
}
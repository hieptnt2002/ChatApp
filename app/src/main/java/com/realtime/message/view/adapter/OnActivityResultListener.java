package com.realtime.message.view.adapter;

import android.content.Intent;

public interface OnActivityResultListener {
    void onResult(int requestCode, int resultCode, Intent data);
}
package com.city.trash.common.imageloader;

import android.graphics.Bitmap;


public interface BitmapLoadingListener {

    void onSuccess(Bitmap b);

    void onError();
}

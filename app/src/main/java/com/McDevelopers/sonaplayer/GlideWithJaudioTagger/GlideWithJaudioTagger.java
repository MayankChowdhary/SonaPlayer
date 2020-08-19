package com.McDevelopers.sonaplayer.GlideWithJaudioTagger;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;

import java.io.InputStream;

public final class GlideWithJaudioTagger implements ModelLoader<String, InputStream> {

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(String model, int width, int height, Options options) {
        return new LoadData<>(new ObjectKey(model), new GlideWithJaudioTaggerFetcher(model));
    }

    @Override
    public boolean handles(String model) {
        return true;
    }

}
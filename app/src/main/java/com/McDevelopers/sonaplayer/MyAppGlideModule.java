package com.McDevelopers.sonaplayer;

import android.content.Context;

import com.McDevelopers.sonaplayer.GlideWithJaudioTagger.GlideWithJaudioTaggerFactory;
import com.McDevelopers.sonaplayer.MediaListActivity.Recycler_View_Adapter_Large;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

@GlideModule
public class MyAppGlideModule extends AppGlideModule {

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    public void registerComponents(@NotNull Context context,@NotNull Glide glide,@NotNull Registry registry) {
        registry.prepend(String.class, InputStream.class, new GlideWithJaudioTaggerFactory());
    }
}
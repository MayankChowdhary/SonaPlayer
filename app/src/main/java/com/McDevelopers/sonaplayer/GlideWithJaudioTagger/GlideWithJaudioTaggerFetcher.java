package com.McDevelopers.sonaplayer.GlideWithJaudioTagger;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import android.util.Log;

import com.McDevelopers.sonaplayer.ApplicationContextProvider;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagOptionSingleton;
import org.jaudiotagger.tag.images.Artwork;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class GlideWithJaudioTaggerFetcher implements DataFetcher<InputStream> {

    private final String model;
     private InputStream albumArtStream;

    GlideWithJaudioTaggerFetcher(String model) {
        this.model = model;
    }

    @Override
    public void loadData(Priority priority, DataCallback<? super InputStream> callback)  {


        try {
            Log.d("GlideWithJaudioTgger", "loadData: Started1"+model);
            TagOptionSingleton.getInstance().setAndroid(true);
            AudioFile f = AudioFileIO.read(new File(model));
            Tag tag = f.getTag();
            Artwork jArtwork = tag.getFirstArtwork();
            albumArtStream = new ByteArrayInputStream(jArtwork.getBinaryData());
            callback.onDataReady(albumArtStream);
            Log.d("GlideWithJaudioTagger", "loadData:Succcess ");

        } catch (Throwable e) {

            try {
                Log.e("GlideWithFFmpeg", "loadData:Entered "+model);
                 MediaMetadataRetriever metadataRetrieverG=new MediaMetadataRetriever();
                metadataRetrieverG.setDataSource(model);
                albumArtStream = new ByteArrayInputStream( metadataRetrieverG.getEmbeddedPicture());
                callback.onDataReady(albumArtStream);
                Log.e("GlideWithFFmpeg", "loadData:Finished ");

            }catch (Throwable eZ){
                Context context = ApplicationContextProvider.getContext();
                ContentResolver res = context.getContentResolver();
                try {
                    Log.e("GlideCachedByMediaStore", "loadData:MediaStoreBegin "+model);

                    albumArtStream = res.openInputStream(getSongIdFromMediaStore(model, context));
                    callback.onDataReady(albumArtStream);

                    Log.e("GlideCachedByMediaStore", "loadData:Successful ");
                } catch (Throwable eX) {
                    Log.e("GlideFailed", "loadData:Unsuccessful ");

                }

            }

        }
    }

    private  Uri getSongIdFromMediaStore(String songPath, Context context) {
        long id = 0;

        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media.DATA;
            String[] selectionArgs = {songPath};
            String[] projection = {MediaStore.Audio.Media.ALBUM_ID};
            String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

            Cursor cursor = cr.query(uri, projection, selection + "=?", selectionArgs, sortOrder);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int idIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                    id = Long.parseLong(cursor.getString(idIndex));
                }
            }
            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Log.d("AlbumArtUri", ContentUris.withAppendedId(sArtworkUri, id).toString());
            return ContentUris.withAppendedId(sArtworkUri, id);
        }catch (Throwable e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void cleanup() {

        try {
            albumArtStream.close();
        }catch (Throwable e){
            e.printStackTrace();
        }

    }

    @Override
    public void cancel() {}

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }
}
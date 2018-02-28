package au.com.elegantmedia.videoapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import life.knowledge4.videotrimmer.utils.FileUtils;

public class MainActivity extends AppCompatActivity {

    private static final int VIDEO_GALLERY_REQUEST = 11;
    private static final int TRIMMER_REQUEST = 22;
    private static final int WRITE_PERMISSION = 23;
    SimpleExoPlayerView mSimpleExoPlayerView;
    SimpleExoPlayer mSimpleExoPlayer;
    String videoURL = "http://techslides.com/demos/sample-videos/small.mp4";
    private DefaultBandwidthMeter bandwidthMeter;
    private DefaultDataSourceFactory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private boolean shouldAutoPlay = false;

    @BindView(R.id.img_btn_play)
    ImageButton imgBtnPlay;
    private Uri uri;

    @OnClick(R.id.img_btn_play)
    public void onClickPlay() {
        shouldAutoPlay = true;
        mSimpleExoPlayer.seekTo(0);
        mSimpleExoPlayer.setPlayWhenReady(shouldAutoPlay);
        imgBtnPlay.setVisibility(View.GONE);
    }

    @OnClick(R.id.img_profile_pic)
    public void onClickProfilePic() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    WRITE_PERMISSION);
            return;
        }

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), VIDEO_GALLERY_REQUEST);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "mediaPlayerSample"), (TransferListener<? super DataSource>) bandwidthMeter);

        mSimpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.video);

        uri = Uri.parse(videoURL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == VIDEO_GALLERY_REQUEST) {
                Uri selectVideoUri = data.getData();
                Intent intent = new Intent(this, TrimVideoActivity.class);
                intent.putExtra(TrimVideoActivity.URI_EXTRA, FileUtils.getPath(this, selectVideoUri));
                startActivityForResult(intent, TRIMMER_REQUEST);
            } else if (requestCode == TRIMMER_REQUEST) {
                uri = Uri.parse(String.valueOf(data.getData()));

                if (uri != null)
                    initializePlayer(uri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == WRITE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();
                onClickProfilePic();
            } else {
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializePlayer(Uri uri) {

        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        mSimpleExoPlayerView.setPlayer(mSimpleExoPlayer);

        mSimpleExoPlayer.setPlayWhenReady(shouldAutoPlay);

        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        MediaSource mediaSource = new ExtractorMediaSource(uri,
                mediaDataSourceFactory, extractorsFactory, null, null);

        //LoopingMediaSource loopingMediaSource = new LoopingMediaSource(mediaSource);

        mSimpleExoPlayer.prepare(mediaSource);

        mSimpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                switch (playbackState) {
                    case Player.STATE_ENDED:
                        shouldAutoPlay = false;
                        mSimpleExoPlayer.seekTo(0);
                        mSimpleExoPlayer.setPlayWhenReady(shouldAutoPlay);
                        imgBtnPlay.setVisibility(View.VISIBLE);
                        break;

                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity() {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });

    }


    private void releasePlayer() {
        if (mSimpleExoPlayer != null) {
            shouldAutoPlay = mSimpleExoPlayer.getPlayWhenReady();
            mSimpleExoPlayer.release();
            mSimpleExoPlayer = null;
            trackSelector = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer(uri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || mSimpleExoPlayer == null)) {
            initializePlayer(uri);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void pausePlayer() {
        if (shouldAutoPlay)
            mSimpleExoPlayer.setPlayWhenReady(false);
        mSimpleExoPlayer.getPlaybackState();
    }

    private void startPlayer() {
        if (shouldAutoPlay)
            mSimpleExoPlayer.setPlayWhenReady(true);
        mSimpleExoPlayer.getPlaybackState();
    }
}

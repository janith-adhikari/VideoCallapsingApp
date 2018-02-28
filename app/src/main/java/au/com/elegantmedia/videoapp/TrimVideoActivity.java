package au.com.elegantmedia.videoapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnK4LVideoListener;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;

public class TrimVideoActivity extends AppCompatActivity implements OnTrimVideoListener, OnK4LVideoListener {

    public static final String URI_EXTRA = "uri";
    @BindView(R.id.timeLine)
    K4LVideoTrimmer mK4LVideoTrimmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim_video);
        ButterKnife.bind(this);

        String uri = getIntent().getStringExtra(URI_EXTRA);

        if (mK4LVideoTrimmer != null) {
            mK4LVideoTrimmer.setMaxDuration(20);
            mK4LVideoTrimmer.setDestinationPath("/storage/emulated/0/Movies/");
            mK4LVideoTrimmer.setOnTrimVideoListener(this);
            mK4LVideoTrimmer.setVideoURI(Uri.parse(uri));
            mK4LVideoTrimmer.setVideoInformationVisibility(true);
        }
    }

    @Override
    public void onTrimStarted() {

    }

    @Override
    public void getResult(final Uri uri) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setData(uri);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void cancelAction() {
        mK4LVideoTrimmer.destroy();
        finish();
    }

    @Override
    public void onError(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(TrimVideoActivity.this, "" + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onVideoPrepared() {

    }
}

package com.schutzstaffel.fmplayer;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.schutzstaffel.fmplayer.utils.ClientAPIs;
import com.schutzstaffel.fmplayer.utils.PlayActions;
import com.schutzstaffel.fmplayer.utils.UserActions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ShowToast")
public class MainActivity extends Activity {

    public static RequestQueue mQueue;
    public static int channel = 188;
    boolean isPlaying = false;

    public static Button btn_play, btn_pause, btn_next, btn_change;
    public static TextView text_title, text_albutm_title;
    public static ImageView img_album;

    public static ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        mQueue = Volley.newRequestQueue(MainActivity.this);

        UserActions.intl(MainActivity.this);
        PlayActions.intl(MainActivity.this);

    }

    /**
     * Bind views and listeners
     */
    private void initView() {
        text_title = (TextView) findViewById(R.id.text_title);
        text_albutm_title = (TextView) findViewById(R.id.text_album_title);

        img_album = (ImageView) findViewById(R.id.img_album);

        btn_play = (Button) findViewById(R.id.btn_play);
        btn_pause = (Button) findViewById(R.id.btn_pause);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_change = (Button) findViewById(R.id.btn_changelist);

        btn_play.setOnClickListener(onc);
        btn_pause.setOnClickListener(onc);
        btn_next.setOnClickListener(onc);
        btn_change.setOnClickListener(onc);

        dialog = new ProgressDialog(MainActivity.this);
    }

    /**
     * Login action
     */

    OnClickListener onc = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int action = v.getId();
            switch (action) {
            case R.id.btn_play:
                if (PlayActions.getListSize() == 0) {
                    PlayActions.getSongList(channel, "n");
                    dialog.setMessage("���ڻ�ȡ��");
                    dialog.show();
                }
                while (true) {
                    if (PlayActions.getListSize() != 0) {
                        PlayActions.play();
                    }
                    break;
                }
                break;
            case R.id.btn_pause:
                PlayActions.pause();
                break;
            case R.id.btn_next:
                PlayActions.next();
                break;
            case R.id.btn_changelist:
                // UserActions.getChannelList();
                dialog.setMessage("������");
                dialog.setCancelable(false);
                dialog.show();
                if (channel != 188) {
                    channel = 188;
                } else {
                    channel = 14;
                }
                PlayActions.clearSongList();
                PlayActions.getSongList(channel, "n");
                PlayActions.play();
                Log.i(ClientAPIs.TAG, String.valueOf(channel));
                break;
            }
        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int action = item.getItemId();
        if (action == R.id.action_login) {
            UserActions.Login();
        }
        if (action == R.id.action_exit) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        PlayActions.player.reset();
        PlayActions.player.release();
        super.onDestroy();
    }

}

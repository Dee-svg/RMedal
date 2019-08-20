package com.name.rmedal.ui.zxing.android;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;

import com.name.rmedal.R;

import static android.content.Context.AUDIO_SERVICE;

/**
 * Created by Vondear on 2017/10/11.
 * 提示音工具
 */
public class BeepManager {

    private static final float BEEP_VOLUME = 0.50f;
    private static final int VIBRATE_DURATION = 200;
    private static MediaPlayer mediaPlayer;

    public static void playBeep(Activity mContext, boolean playBeep, boolean vibrate) {
        AudioManager audioService = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        } else {
            mContext.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = MediaPlayer.create(mContext, R.raw.beep);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.seekTo(0);
                }
            });
            mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) mContext
                    .getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

}

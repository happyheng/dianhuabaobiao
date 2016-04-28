package com.hengswings.phoneguard.ActivityClock;

import java.util.Timer;
import java.util.TimerTask;

import com.hengswings.phoneguard.R;

import android.R.integer;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

public class ClockRingActivity extends Activity {
	/* 管理手机所有音量的类 */
	private AudioManager audioManager;
	/* 管理手机播放音乐的类 */
	private MediaPlayer mediaPlayer;
	/* 一秒的时间 */
	private final int second = 1000;
	/* 管理手机震动的类 */
	private Vibrator vibrator = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clock);
		// 设置媒体音量与铃声音量一样
		setMusicFollowRing();
		mediaPlayer = null;
		// 开始播放音乐的源码
		mediaPlayer = MediaPlayer.create(this, R.raw.clock);
		// 设置重复播放
		mediaPlayer.setLooping(true);
		if (!mediaPlayer.isPlaying()) {
			mediaPlayer.start();
		}
		// 调用手机震动方法
		phoneVibrator();

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				// 如果正在运行，关闭
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
				}
				vibrator.cancel();// 立即停止震动
				finish();
			}
		};
		Timer timer = new Timer();
		// 等待50秒后执行
		timer.schedule(task, 55 * second);

	}

	/**
	 * 设置媒体音量与铃声音量一样
	 */
	private void setMusicFollowRing() {
		// 实例化
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		// 得到现在的铃声音量
		int current = audioManager.getStreamVolume(AudioManager.STREAM_RING);
		// 设置媒体音量为铃声音量
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);

	}

	/**
	 * 点击取消闹钟播放
	 */
	public void exitbutton0(View v) {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
		vibrator.cancel();// 立即停止震动
		// 结束自己的生命周期
		finish();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
		vibrator.cancel();// 立即停止震动
	}

	// 震动提醒
	private void phoneVibrator() {
		// 创建Vibrator对象
		vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		/**
		 * 数组参数意义：第一个参数为等待指定时间后开始震动，震动时间为第二个参数。后边的参数依次为等待震动和震动的时间
		 * 第二个参数为重复次数，-1为不重复，0为一直震动
		 */
		vibrator.vibrate(new long[] { 0, 500, 500, 300 }, 0);

	}

}
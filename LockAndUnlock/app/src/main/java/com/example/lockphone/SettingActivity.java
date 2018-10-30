package com.example.lockphone;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SettingActivity extends Activity {

	TextView textView1;
	TextView textView2;
	SeekBar seekBar1;
	SeekBar seekBar2;
	Button saveButton;
	Button cancelButton;
	
	PreManager preManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);

		textView1 = (TextView) findViewById(R.id.textView1);
		textView2 = (TextView) findViewById(R.id.textView2);
		seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
		seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
		saveButton = (Button) findViewById(R.id.save);
		cancelButton = (Button) findViewById(R.id.cancel);
		preManager = new PreManager(this);

		textView1.setText(getResources().getString(R.string.set1)
				+ preManager.getTimes());
		textView2.setText(getResources().getString(R.string.set2)
				+ preManager.getDelay());

		seekBar1.setProgress(preManager.getTimes());
		seekBar2.setProgress(preManager.getDelay());

		seekBar1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (progress < 2) {
					seekBar1.setProgress(2);
					progress = 2;
				}
				textView1.setText(getResources().getString(R.string.set1)
						+ progress);
			}
		});

		seekBar2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (progress < 5) {
					seekBar2.setProgress(5);
					progress = 5;
				}
				textView2.setText(getResources().getString(R.string.set2)
						+ progress);
			}
		});

		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int times = seekBar1.getProgress();
				int delay = seekBar2.getProgress();
				preManager.saveTimes(times);
				preManager.saveDelay(delay);
				ScreenService.delay = delay * 100;
				ScreenService.times = times;
				SettingActivity.this.finish();
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SettingActivity.this.finish();
			}
		});
	}

}

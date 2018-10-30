// Copyright 2016 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.android.handgestureservice;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;

import edu.washington.cs.touchfreelibrary.sensors.CameraGestureSensor;
import edu.washington.cs.touchfreelibrary.sensors.ClickSensor;
import edu.washington.cs.touchfreelibrary.utilities.LocalOpenCV;

import java.util.ArrayDeque;
import java.util.Deque;

public class HandGestureService extends AccessibilityService implements CameraGestureSensor.Listener, ClickSensor.Listener{

    FrameLayout mLayout;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private static final String TAG = "CS591E2";



    @Override
    public void onGestureUp(CameraGestureSensor caller, long gestureLength) {
        AccessibilityNodeInfo scrollable = findScrollableNode(getRootInActiveWindow());
        if (scrollable != null) {
            scrollable.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD.getId());
        }
        Log.i(TAG,"Gesture Up");


    }

    @Override
    public void onGestureDown(CameraGestureSensor caller, long gestureLength) {
        AccessibilityNodeInfo scrollable = findScrollableNode(getRootInActiveWindow());
        if (scrollable != null) {
            scrollable.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD.getId());
        }
        Log.i(TAG,"Gesture Down");


    }

    @Override
    public void onGestureLeft(CameraGestureSensor caller, long gestureLength) {
        AccessibilityNodeInfo scrollable = findScrollableNode(getRootInActiveWindow());
        if (scrollable != null) {
            scrollable.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_LEFT.getId());
        }
        Log.i(TAG,"Gesture Left");

    }

    @Override
    public void onGestureRight(CameraGestureSensor caller, long gestureLength) {
        AccessibilityNodeInfo scrollable = findScrollableNode(getRootInActiveWindow());
        if (scrollable != null) {
            scrollable.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_RIGHT.getId());
        }
        Log.i(TAG,"Gesture Right");

    }

    @Override
    public void onSensorClick(ClickSensor caller) {

    }

    private void configureSwipeButton() {
        Button swipeButton = (Button) mLayout.findViewById(R.id.swipe);
        swipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Path swipePath = new Path();
                swipePath.moveTo(1000, 1000);
                swipePath.lineTo(100, 1000);
                GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
                gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 0, 500));
                dispatchGesture(gestureBuilder.build(), null, null);
            }
        });
    }

    private void configurePowerButton() {
        Button powerButton = (Button) mLayout.findViewById(R.id.power);
        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performGlobalAction(GLOBAL_ACTION_POWER_DIALOG);
            }
        });
    }

    private void configureVolumeButton() {
        Button volumeUpButton = (Button) mLayout.findViewById(R.id.volume_up);
        volumeUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            }
        });
    }

    private AccessibilityNodeInfo findScrollableNode(AccessibilityNodeInfo root) {
        Deque<AccessibilityNodeInfo> deque = new ArrayDeque<>();
        deque.add(root);
        while (!deque.isEmpty()) {
            AccessibilityNodeInfo node = deque.removeFirst();
            if (node.getActionList().contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD)) {
                return node;
            }
            for (int i = 0; i < node.getChildCount(); i++) {
                deque.addLast(node.getChild(i));
            }
        }
        return null;
    }

    private void configureScrollButton() {
        Button scrollButton = (Button) mLayout.findViewById(R.id.scroll);
        scrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                try {
//
//                    //intent: start voice
//                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, Locale.ENGLISH);
//                    //begin voice recognition
//                    startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
//
//                }catch (Exception e) {
//                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(), "Can't find audit device", Toast.LENGTH_LONG).show();
//                }

                AccessibilityNodeInfo scrollable = findScrollableNode(getRootInActiveWindow());
                if (scrollable != null) {
                    scrollable.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD.getId());
                }
            }
        });
    }

    @Override
    protected void onServiceConnected() {
        Log.i(TAG, "Connecting");
        // Create an overlay and display the action bar
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mLayout = new FrameLayout(this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.TOP;
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.action_bar, mLayout);
//        wm.addView(mLayout, lp);
        LocalOpenCV loader = new LocalOpenCV(this,this,this);
//        lp.format = PixelFormat.TRANSPARENT;
//        loader.CameraLayout.setVisibility(View.INVISIBLE);
//        loader.CameraLayout.setAlpha(0);
        wm.addView(loader.CameraLayout,lp);


        Log.i(TAG, "Made mLoaderCallback");




        configurePowerButton();
        configureVolumeButton();
        configureScrollButton();

        configureSwipeButton();

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

}

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

package com.example.android.globalactionbarservice;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import com.sac.speech.GoogleVoiceTypingDisabledException;
import com.sac.speech.Speech;
import com.sac.speech.SpeechDelegate;
import com.sac.speech.SpeechRecognitionNotAvailable;
import com.tbruyelle.rxpermissions.RxPermissions;


public class GlobalActionBarService extends AccessibilityService implements SpeechDelegate, Speech.stopDueToDelay{//implements RecognitionListener{

    /***********************************/
    public static SpeechDelegate delegate;
    /***********************************/

    FrameLayout mLayout;
    private SpeechRecognizer speechRecognizer;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;


    @Override
    public void onCreate() {
        super.onCreate();


        /***************************/
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ((AudioManager) Objects.requireNonNull(
                        getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Speech.init(this);
        delegate = this;
        Speech.getInstance().setListener(this);

        if (Speech.getInstance().isListening()) {
            Speech.getInstance().stopListening();
            muteBeepSoundOfRecorder();
        } else {
            System.setProperty("rx.unsafe-disable", "True");
            RxPermissions.getInstance(this).request(Manifest.permission.RECORD_AUDIO).subscribe(granted -> {
                if (granted) { // Always true pre-M
                    try {
                        Speech.getInstance().stopTextToSpeech();
                        Speech.getInstance().startListening(null, this);
                    } catch (SpeechRecognitionNotAvailable exc) {
                        //showSpeechNotSupportedDialog();

                    } catch (GoogleVoiceTypingDisabledException exc) {
                        //showEnableGoogleVoiceTyping();
                    }
                } else {
                    Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show();
                }
            });
            muteBeepSoundOfRecorder();
        }
        /***************************/
//        /*************************/
//
//        Log.d("Check","Start Listening");
//
//        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
//        speechRecognizer.setRecognitionListener(GlobalActionBarService.this);
//
//        Intent voice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        voice.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
//                .getPackage().getName());
//        voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        voice.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
//
//        speechRecognizer.startListening(voice);
//
//        /*************************/
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


//                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
//                speechRecognizer.setRecognitionListener(GlobalActionBarService.this);
//
//                Intent voice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//                voice.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
//                        .getPackage().getName());
//                voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//                voice.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
//
//                speechRecognizer.startListening(voice);

                AccessibilityNodeInfo scrollable = findScrollableNode(getRootInActiveWindow());
                if (scrollable != null) {
                    scrollable.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD.getId());
                }
            }
        });
    }

    @Override
    protected void onServiceConnected() {
        // Create an overlay and display the action bar
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mLayout = new FrameLayout(this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.TOP;
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.action_bar, mLayout);
//        wm.addView(mLayout, lp);


//        /***************************/
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                ((AudioManager) Objects.requireNonNull(
//                        getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Speech.init(this);
//        delegate = this;
//        Speech.getInstance().setListener(this);
//
//        if (Speech.getInstance().isListening()) {
//            Speech.getInstance().stopListening();
//            muteBeepSoundOfRecorder();
//        } else {
//            System.setProperty("rx.unsafe-disable", "True");
//            RxPermissions.getInstance(this).request(Manifest.permission.RECORD_AUDIO).subscribe(granted -> {
//                if (granted) { // Always true pre-M
//                    try {
//                        Speech.getInstance().stopTextToSpeech();
//                        Speech.getInstance().startListening(null, this);
//                    } catch (SpeechRecognitionNotAvailable exc) {
//                        //showSpeechNotSupportedDialog();
//
//                    } catch (GoogleVoiceTypingDisabledException exc) {
//                        //showEnableGoogleVoiceTyping();
//                    }
//                } else {
//                    Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show();
//                }
//            });
//            muteBeepSoundOfRecorder();
//        }
//        /***************************/

//        /*************************/
//
//        Log.d("Check","Start Listening");
//
//        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
//        speechRecognizer.setRecognitionListener(GlobalActionBarService.this);
//
//        Intent voice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        voice.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
//                .getPackage().getName());
//        voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        voice.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
//
//        speechRecognizer.startListening(voice);
//
//        /*************************/



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


//    @Override
//    public void onReadyForSpeech(Bundle bundle) {
//
//        Log.d("onReady", "service");
//        Log.d("Check","onReadyForSpeech");
//    }
//
//    @Override
//    public void onBeginningOfSpeech() {
//        Log.d("Check","onBeginningOfSpeech");
//    }
//
//    @Override
//    public void onRmsChanged(float v) {
//        Log.d("Check","onRmsChanged");
//
//    }
//
//    @Override
//    public void onBufferReceived(byte[] bytes) {
//        Log.d("Check","onBufferReceived");
//
//    }
//
//    @Override
//    public void onEndOfSpeech() {
//        Log.d("Check","onEndOfSpeech");
//
//    }
//
//    @Override
//    public void onError(int i) {
//        Log.d("ERROR","ERROR");
//        Log.d("Check","onError :" + i);
//
//    }
//
//    @Override
//    public void onResults(Bundle resultsBundle) {
//        Log.d("Results", "onResults");
//
//        ArrayList<String> matches = resultsBundle.getStringArrayList(speechRecognizer.RESULTS_RECOGNITION);
//        Log.d("Check",matches.get(0));
//    }
//
//    @Override
//    public void onPartialResults(Bundle bundle) {
//        Log.d("Check","onPartialResults");
//
//    }
//
//    @Override
//    public void onEvent(int i, Bundle bundle) {
//        Log.d("Check","onEvent");
//
//    }

    @Override
    public void onDestroy(){
        if(speechRecognizer!=null)
        {
            speechRecognizer.destroy();
        }
        super.onDestroy();
    }




    /******************************************/
    @Override
    public void onStartOfSpeech() {
    }

    @Override
    public void onSpeechRmsChanged(float value) {

    }

    @Override
    public void onSpeechPartialResults(List<String> results) {
        for (String partial : results) {
            Log.d("Result", partial+"");
        }
    }

    public void scrollDown() {
        AccessibilityNodeInfo scrollable = findScrollableNode(getRootInActiveWindow());
        if (scrollable != null) {
            scrollable.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_DOWN.getId());
        }
    }

    public void scrollUp() {
        AccessibilityNodeInfo scrollable = findScrollableNode(getRootInActiveWindow());
        if (scrollable != null) {
            scrollable.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_UP.getId());
        }
    }

    public void scrollLeft() {
        AccessibilityNodeInfo scrollable = findScrollableNode(getRootInActiveWindow());
        if (scrollable != null) {
            scrollable.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_LEFT.getId());
        }
    }

    public void scrollRight() {
        AccessibilityNodeInfo scrollable = findScrollableNode(getRootInActiveWindow());
        if (scrollable != null) {
            scrollable.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_RIGHT.getId());
        }
    }

    public void scrollBack() {
        AccessibilityNodeInfo scrollable = findScrollableNode(getRootInActiveWindow());
        if (scrollable != null) {
            scrollable.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD.getId());
        }
    }

    public void scrollForward() {
        AccessibilityNodeInfo scrollable = findScrollableNode(getRootInActiveWindow());
        if (scrollable != null) {
            scrollable.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD.getId());
        }
    }

    public void swipeLeft() {
        Path swipePath = new Path();
        swipePath.moveTo(1000, 1000);
        swipePath.lineTo(100, 1000);
        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 0, 500));
        dispatchGesture(gestureBuilder.build(), null, null);
    }

    public void swipeRight() {
        Path swipePath = new Path();
        swipePath.moveTo(100, 1000);
        swipePath.lineTo(1000, 1000);
        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 0, 500));
        dispatchGesture(gestureBuilder.build(), null, null);
    }





    @Override
    public void onSpeechResult(String result) {
        Log.d("ResultHere", result+"");
        if (!TextUtils.isEmpty(result)) {

            /************************************/

            Set<Character> res = new HashSet<>();
            for(char c:result.toCharArray()) {
                res.add(c);
            }

            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();

            if(res.contains('s') && res.contains('c') && res.contains('d')) {
 //               Toast.makeText(this, "scroll", Toast.LENGTH_SHORT).show();
                Log.d("ComesHere",result+"");
                scrollDown();
            } else

            if(res.contains('s') && res.contains('c') && res.contains('u') && res.contains('p')) {
                //               Toast.makeText(this, "scroll", Toast.LENGTH_SHORT).show();
                Log.d("ComesHere",result+"");
                scrollUp();
            } else

//            if(res.contains('s') && res.contains('c') && res.contains('l') ) {
//                //               Toast.makeText(this, "scroll", Toast.LENGTH_SHORT).show();
//                Log.d("ComesHere",result+"");
//                scrollLeft();
//            }
//
//            if(res.contains('s') && res.contains('c') && res.contains('r') ) {
//                //               Toast.makeText(this, "scroll", Toast.LENGTH_SHORT).show();
//                Log.d("ComesHere",result+"");
//                scrollRight();
//            }

//            if(res.contains('s') && res.contains('c') && res.contains('b') ) {
//                //               Toast.makeText(this, "scroll", Toast.LENGTH_SHORT).show();
//                Log.d("ComesHere",result+"");
//                scrollBack();
//            }
//
//            if(res.contains('s') && res.contains('f') ) {
//                //               Toast.makeText(this, "scroll", Toast.LENGTH_SHORT).show();
//                Log.d("ComesHere",result+"");
//                scrollForward();
//            }
//
            if(res.contains('s') && res.contains('w') && res.contains('l') ) {
                //               Toast.makeText(this, "scroll", Toast.LENGTH_SHORT).show();
                Log.d("ComesHere",result+"");
                swipeLeft();
            } else

            if(res.contains('s') && res.contains('w') && res.contains('r') ) {
                //               Toast.makeText(this, "scroll", Toast.LENGTH_SHORT).show();
                Log.d("ComesHere",result+"");
                swipeRight();
            } else
//
            if(res.contains('w') && res.contains('o') && res.contains('e')) {
                performGlobalAction(GLOBAL_ACTION_POWER_DIALOG);
            }





            /*************************************/


        }
    }

    @Override
    public void onSpecifiedCommandPronounced(String event) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ((AudioManager) Objects.requireNonNull(
                        getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Speech.getInstance().isListening()) {
            muteBeepSoundOfRecorder();
            Speech.getInstance().stopListening();
        } else {
            RxPermissions.getInstance(this).request(Manifest.permission.RECORD_AUDIO).subscribe(granted -> {
                if (granted) { // Always true pre-M
                    try {
                        Speech.getInstance().stopTextToSpeech();
                        Speech.getInstance().startListening(null, this);
                    } catch (SpeechRecognitionNotAvailable exc) {
                        //showSpeechNotSupportedDialog();

                    } catch (GoogleVoiceTypingDisabledException exc) {
                        //showEnableGoogleVoiceTyping();
                    }
                } else {
                    Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show();
                }
            });
            muteBeepSoundOfRecorder();
        }
    }
    /**
     * Function to remove the beep sound of voice recognizer.
     */
    private void muteBeepSoundOfRecorder() {
        AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (amanager != null) {
            amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
            amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            amanager.setStreamMute(AudioManager.STREAM_RING, true);
            amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //Restarting the service if it is removed.
        PendingIntent service =
                PendingIntent.getService(getApplicationContext(), new Random().nextInt(),
                        new Intent(getApplicationContext(), GlobalActionBarService.class), PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
        super.onTaskRemoved(rootIntent);
    }
}

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

    private static final String TAG = "CS591E2";

    // TODO: GET RID OF BLACK BOX FROM CAMERA LAYOUT
    //      - possibly make it an underlay if possible
    //      - setting visibility to invisible or gone breaks gesture detection
    //      - setting alpha does nothing
    // TODO: Update all onGesture events to see what actions are possible.
    // TODO: Add hover gesture
    // TODO: See if multipart gestures can be recognized (gesture up then down)
    // TODO: Update gesture sensitivity


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
//        if (scrollable != null) {
//            scrollable.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_LEFT.getId());
//        }

        Path swipePath = new Path();
        swipePath.moveTo(100, 1000);
        swipePath.lineTo(1000, 1000);
        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 0, 500));
        dispatchGesture(gestureBuilder.build(), null, null);
        Log.i(TAG,"Gesture Left");

    }

    @Override
    public void onGestureRight(CameraGestureSensor caller, long gestureLength) {
        AccessibilityNodeInfo scrollable = findScrollableNode(getRootInActiveWindow());
//        if (scrollable != null) {
//            scrollable.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_RIGHT.getId());
//        }

        Path swipePath = new Path();
        swipePath.moveTo(1000, 1000);
        swipePath.lineTo(100, 1000);
        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 0, 500));
        dispatchGesture(gestureBuilder.build(), null, null);
        Log.i(TAG,"Gesture Right");

    }

    @Override
    public void onSensorClick(ClickSensor caller) {

    }


    // Figures out if scrolling is possible.
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



    @Override
    protected void onServiceConnected() {
        Log.i(TAG, "Loading OpenCV");

        // Load OpenCV
        LocalOpenCV loader = new LocalOpenCV(this,this,this);

        // Get window manager to be able to bind layouts to service
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        // Set up layout parameters
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.TOP;

        // Add view to service
        wm.addView(loader.CameraLayout,lp);


        Log.i(TAG, "Finished loading");

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

}

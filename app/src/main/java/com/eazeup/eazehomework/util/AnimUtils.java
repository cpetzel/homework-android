/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eazeup.eazehomework.util;

import android.transition.Transition;

/**
 * Utility methods for working with animations.
 */
public class AnimUtils {

    private AnimUtils() {
    }

    public static abstract class TransitionEndListener implements Transition.TransitionListener {
        public abstract void onTransitionCompleted(Transition transition);

        @Override
        public void onTransitionStart(Transition transition) {

        }

        @Override
        public void onTransitionEnd(Transition transition) {
            onTransitionCompleted(transition);
        }

        @Override
        public void onTransitionCancel(Transition transition) {
            onTransitionCompleted(transition);
        }

        @Override
        public void onTransitionPause(Transition transition) {

        }

        @Override
        public void onTransitionResume(Transition transition) {

        }
    }

}
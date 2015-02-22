/*
 * Copyright (C) 2014 Trillian Mobile AB
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * 
 * Portions of this code is based on Apple Inc's MoveMe sample (v3.0)
 * which is copyright (C) 2008-2013 Apple Inc.
 */

package org.robovm.samples.moveme.views;

import org.robovm.apple.coreanimation.CAAnimation;
import org.robovm.apple.coreanimation.CAAnimationDelegateAdapter;
import org.robovm.apple.coreanimation.CAAnimationGroup;
import org.robovm.apple.coreanimation.CABasicAnimation;
import org.robovm.apple.coreanimation.CAKeyframeAnimation;
import org.robovm.apple.coreanimation.CALayer;
import org.robovm.apple.coreanimation.CAMediaTimingFunction;
import org.robovm.apple.coreanimation.CAMediaTimingFunctionName;
import org.robovm.apple.coreanimation.CATransform3D;
import org.robovm.apple.coregraphics.CGAffineTransform;
import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSSet;
import org.robovm.apple.foundation.NSValue;
import org.robovm.apple.uikit.UIBezierPath;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UITouch;
import org.robovm.apple.uikit.UIView;
import org.robovm.objc.block.VoidBooleanBlock;

public class APLMoveMeView : UIView() {
    private var displayStrings: Array<String>? = null;
    var nextDisplayStringIndex: Int = 0;
    var placardView: APLPlacardView;

    {
        setFrame(UIScreen.getMainScreen().getApplicationFrame());
        setBackgroundColor(UIColor.darkGray());

        placardView = APLPlacardView();
        addSubview(placardView);
    }

    override fun touchesBegan (touches: NSSet<UITouch>, event: UIEvent) {
        // We only support single touches, so any retrieves just that touch from touches.
        val touch = touches.any();

        // Only move the placard view if the touch was in the placard view.
        if (!touch.getView().identityEquals(placardView)) {
            // In case of a double tap outside the placard view, update the placard's display string.
            if (touch.getTapCount() == 2.toLong() ) {
                setupNextDisplayString();
            }
            return;
        }

        // Animate the first touch.
        val touchPoint = touch.getLocationInView(this);
        animateFirstTouch(touchPoint);
    }

    override fun touchesMoved (touches: NSSet<UITouch>, event: UIEvent) {
        val touch = touches.any();

        // If the touch was in the placardView, move the placardView to its location.
        if (touch.getView() identityEquals placardView) {
            val location = touch.getLocationInView(this);
            placardView.setCenter(location);
            return;
        }
    }

    override fun touchesEnded (touches: NSSet<UITouch>, event: UIEvent) {
        val touch = touches.any();

        // If the touch was in the placardView, bounce it back to the center.
        if (touch.getView() identityEquals  placardView) {
            /*
             * Disable user interaction so subsequent touches don't interfere with animation until the placard has returned to the
             * center. Interaction is reenabled in animationDidStop:finished:.
             */
            setUserInteractionEnabled(false);
            animatePlacardViewToCenter();
            return;
        }
    }

    override fun touchesCancelled (touches: NSSet<UITouch>, event: UIEvent) {
        /*
         * To impose as little impact on the device as possible, simply set the placard view's center and transformation to the
         * original values.
         */
        placardView.setCenter(getCenter());
        placardView.setTransform(CGAffineTransform.Identity());
    }

    val GROW_FACTOR = 1.2;
    val SHRINK_FACTOR = 1.1;
    val GROW_ANIMATION_DURATION_SECONDS = 0.15;
    val SHRINK_ANIMATION_DURATION_SECONDS = 0.15;

    /** "Pulse" the placard view by scaling up then down, then move the placard to under the finger. */
    fun animateFirstTouch (touchPoint: CGPoint) {
        /*
         * Create two separate animations. The first animation is for the grow and partial shrink. The grow animation is performed
         * in a block. The method uses a completion block that itself includes an animation block to perform the shrink. The
         * second animation lasts for the total duration of the grow and shrink animations and contains a block responsible for
         * performing the move.
         */
        UIView.animate(GROW_ANIMATION_DURATION_SECONDS,{
                val transform = CGAffineTransform.createScale(GROW_FACTOR, GROW_FACTOR);
                placardView.setTransform(transform);
        }, {
            UIView.animate(SHRINK_ANIMATION_DURATION_SECONDS, {
                placardView.setTransform(CGAffineTransform.createScale(SHRINK_FACTOR, SHRINK_FACTOR));
            })
        });


        UIView.animate(GROW_ANIMATION_DURATION_SECONDS + SHRINK_ANIMATION_DURATION_SECONDS, {
                placardView.setCenter(touchPoint);
            }
        );
    }

    /** Bounce the placard back to the center. */
    fun animatePlacardViewToCenter () {
        val welcomeLayer = placardView.getLayer();

        // Create a keyframe animation to follow a path back to the center.
        var bounceAnimation = CAKeyframeAnimation.create("position");
        bounceAnimation.setRemovedOnCompletion(false);

        var animationDuration = 1.5;

        // Create the path for the bounces.
        val bouncePath = UIBezierPath();

        val centerPoint = getCenter();
        val midX = centerPoint.getX();
        val midY = centerPoint.getY();
        val originalOffsetX = placardView.getCenter().getX() - midX;
        val originalOffsetY = placardView.getCenter().getY() - midY;
        var offsetDivider = 4;

        var stopBouncing = false;

        // Start the path at the placard's current location.
        bouncePath.move(CGPoint(placardView.getCenter().getX(), placardView.getCenter().getY()));
        bouncePath.addLine(CGPoint(midX, midY));

        // Add to the bounce path in decreasing excursions from the center.
        while (!stopBouncing) {
            val excursion = CGPoint(midX + originalOffsetX / offsetDivider, midY + originalOffsetY / offsetDivider);
            bouncePath.addLine(excursion);
            bouncePath.addLine(centerPoint);

            offsetDivider += 4;
            animationDuration += 1 / offsetDivider;
            if (Math.abs(originalOffsetX / offsetDivider) < 6 && Math.abs(originalOffsetY / offsetDivider) < 6) {
                stopBouncing = true;
            }
        }

        bounceAnimation.setPath(bouncePath.getCGPath());
        bounceAnimation.setDuration(animationDuration);

        // Create a basic animation to restore the size of the placard.
        var transformAnimation = CABasicAnimation.create("transform");
        transformAnimation.setRemovedOnCompletion(true);
        transformAnimation.setDuration(animationDuration);
        transformAnimation.setToValue(NSValue.valueOf(CATransform3D.Identity()));

        // Create an animation group to combine the keyframe and basic animations.
        val group = CAAnimationGroup();

        group.setDelegate(object : CAAnimationDelegateAdapter() {
            /** Animation delegate method called when the animation's finished: restore the transform and reenable user
             * interaction. */
            override fun didStop(anim: CAAnimation?, flag: Boolean) {
                placardView.setTransform(CGAffineTransform.Identity());
                setUserInteractionEnabled(true);
            }
        });
        group.setDuration(animationDuration);
        group.setTimingFunction(CAMediaTimingFunction.create(CAMediaTimingFunctionName.EaseIn));

        group.setAnimations(NSArray<CAAnimation>(bounceAnimation, transformAnimation));

        // Add the animation group to the layer.
        welcomeLayer.addAnimation(group, "animatePlacardViewToCenter");

        // Set the placard view's center and transformation to the original values in preparation for the end of the animation.
        placardView.setCenter(centerPoint);
        placardView.setTransform(CGAffineTransform.Identity());
    }

    fun setupNextDisplayString () {
        var nextIndex = nextDisplayStringIndex;
        val displayString = displayStrings?.get(nextIndex);
        placardView.setDisplayString(displayString);

        nextIndex++;
        if (nextIndex.equals(displayStrings?.size())) {
            nextIndex = 0;
        }
        nextDisplayStringIndex = nextIndex;

        placardView.setCenter(getCenter());
    }


    fun setDisplayStrings(value: Array<String>) {
        displayStrings = value;
    }

}

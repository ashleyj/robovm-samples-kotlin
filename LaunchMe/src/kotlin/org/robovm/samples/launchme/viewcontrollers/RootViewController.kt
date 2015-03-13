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
 * Portions of this code is based on Apple Inc's Adventure sample (v1.3)
 * which is copyright (C) 2013-2014 Apple Inc.
 */

package org.robovm.samples.launchme.viewcontrollers

import org.robovm.apple.uikit.UIViewController
import org.robovm.apple.uikit.UIView
import org.robovm.apple.uikit.UIColor
import org.robovm.apple.uikit.UILabel
import org.robovm.apple.coregraphics.CGRect
import org.robovm.apple.uikit.NSTextAlignment
import org.robovm.apple.uikit.UIViewContentMode
import org.robovm.apple.uikit.UIFont
import org.robovm.apple.uikit.UIControl
import org.robovm.apple.uikit.UISlider
import org.robovm.apple.uikit.UITextView
import org.robovm.apple.uikit.UITextAutocapitalizationType
import org.robovm.apple.uikit.UITapGestureRecognizer
import org.robovm.apple.uikit.UIGestureRecognizer

import org.robovm.apple.foundation.NSRange
import org.robovm.apple.uikit.UIMenuController
import org.robovm.apple.uikit.NSLineBreakMode
import org.robovm.apple.uikit.UIButton
import org.robovm.apple.uikit.UIButtonType
import org.robovm.apple.uikit.UIControlState
import org.robovm.apple.uikit.UIEvent
import org.robovm.apple.uikit.UIApplication
import org.robovm.apple.foundation.NSURL
import org.robovm.apple.uikit.UITouch
import org.robovm.apple.foundation.NSSet
import org.robovm.apple.uikit.UIControl.OnTouchUpInsideListener

public class RootViewController : UIViewController() {

    private var redSlider: UISlider?;
    private var greenSlider: UISlider?;
    private var blueSlider: UISlider?;
    private var colorView: UIView?;
    private var urlFieldHeader: UILabel?;
    private var urlField: UITextView?;

    private var selectedColor = UIColor.white();

    {


        var view = getView();
        view.setBackgroundColor(UIColor.white());

        var redLabel = UILabel(CGRect(22.0, 278.0, 59.0, 21.0));
        redLabel.setText("Red:");
        redLabel.setTextAlignment(NSTextAlignment.Right);
        redLabel.setContentMode(UIViewContentMode.Left);
        redLabel.setFont(UIFont.getSystemFont(17.0));
        redLabel.setTextColor(UIColor.darkText());
        view.addSubview(redLabel);

        var greenLabel = UILabel(CGRect(22.0, 318.0, 59.0, 21.0));
        greenLabel.setText("Green:");
        greenLabel.setTextAlignment(NSTextAlignment.Right);
        greenLabel.setContentMode(UIViewContentMode.Left);
        greenLabel.setFont(UIFont.getSystemFont(17.0));
        greenLabel.setTextColor(UIColor.darkText());
        view.addSubview(greenLabel);

        var blueLabel = UILabel(CGRect(22.0, 357.0, 59.0, 21.0));
        blueLabel.setText("Blue:");
        blueLabel.setTextAlignment(NSTextAlignment.Right);
        blueLabel.setContentMode(UIViewContentMode.Left);
        blueLabel.setFont(UIFont.getSystemFont(17.0));
        blueLabel.setTextColor(UIColor.darkText());
        view.addSubview(blueLabel);

        var sliderListener = UIControl.OnValueChangedListener() {
                sliderValueChanged();
        };

        redSlider = UISlider(CGRect(87.0, 279.0, 200.0, 23.0));
        redSlider?.setMinimumValue(0f);
        redSlider?.setMaximumValue(1f);
        redSlider?.addOnValueChangedListener(sliderListener);
        view.addSubview(redSlider);

        greenSlider = UISlider(CGRect(87.0, 319.0, 200.0, 23.0));
        greenSlider?.setMinimumValue(0f);
        greenSlider?.setMaximumValue(1f);
        greenSlider?.addOnValueChangedListener(sliderListener);
        view.addSubview(greenSlider);

        blueSlider = UISlider(CGRect(87.0, 358.0, 200.0, 23.0));
        blueSlider?.setMinimumValue(0f);
        greenSlider?.setMaximumValue(1f);
        blueSlider?.addOnValueChangedListener(sliderListener);
        view.addSubview(blueSlider);

        colorView = UIView(CGRect(110.0, 166.0, 100.0, 100.0));
        colorView?.setBackgroundColor(UIColor.darkText());
        view.addSubview(colorView);

        urlField = UITextView(CGRect(101.0, 127.0, 199.0, 36.0));
        urlField?.setText("launchme://#000000");
        urlField?.setEditable(false);
        urlField?.setScrollEnabled(false);
        urlField?.setShowsHorizontalScrollIndicator(false);
        urlField?.setShowsVerticalScrollIndicator(false);
        urlField?.setMultipleTouchEnabled(true);
        urlField?.setBackgroundColor(UIColor.white());
        urlField?.setFont(UIFont.getSystemFont(17.0));
        urlField?.setAutocapitalizationType(UITextAutocapitalizationType.Sentences);
        urlField?.addGestureRecognizer(UITapGestureRecognizer(UIGestureRecognizer.OnGestureListener () {
                // Select the url.
                urlField?.setSelectedRange(NSRange(0L, urlField?.getText()?.length()?.toLong() as Long));
                // Show the copy menu.
                UIMenuController.getSharedMenuController().setTargetRect(urlField?.getBounds(), urlField);
                UIMenuController.getSharedMenuController().setMenuVisible(true, true);
        }));
        view.addSubview(urlField);

        var urlLabel = UILabel(CGRect(20.0, 127.0, 85.0, 36.0));
        urlLabel.setText("URL:");
        urlLabel.setTextAlignment(NSTextAlignment.Right);
        urlLabel.setFont(UIFont.getSystemFont(17.0));
        urlLabel.setTextColor(UIColor.darkText());
        view.addSubview(urlLabel);

        urlFieldHeader = UILabel(CGRect(20.0, 117.0, 280.0, 22.0));
        urlFieldHeader?.setText("Tap to select the URL");
        urlFieldHeader?.setTextAlignment(NSTextAlignment.Center);
        urlFieldHeader?.setFont(UIFont.getSystemFont(10.0));
        urlFieldHeader?.setTextColor(UIColor.darkText());
        view.addSubview(urlFieldHeader);

        var descriptionLabel = UILabel(CGRect(20.0, 13.0, 280.0, 101.0));
        descriptionLabel
                .setText("Using this sample:\nDrag the sliders to configure the URL for a specific color.  Copy the displayed launchme URL and tap the button below to launch Mobile Safari.  Paste the URL into the address bar and tap Go.  Optionally, try modifying parts of the URL in Mobile Safari.");
        descriptionLabel.setTextAlignment(NSTextAlignment.Center);
        descriptionLabel.setLineBreakMode(NSLineBreakMode.TruncatingTail);
        descriptionLabel.setNumberOfLines(6);
        descriptionLabel.setFont(UIFont.getSystemFont(12.0));
        descriptionLabel.setTextColor(UIColor.darkText());
        view.addSubview(descriptionLabel);

        var startSafariButton = UIButton.create(UIButtonType.RoundedRect);
        startSafariButton.setFrame(CGRect(20.0, 396.0, 280.0, 44.0));
        startSafariButton.getTitleLabel().setFont(UIFont.getBoldSystemFont(15.0));
        startSafariButton.setTitle("Launch Mobile Safari", UIControlState.Normal);
        startSafariButton.setTintColor(UIColor.fromRGBA(0.196, 0.309, 0.521, 1.0));
        startSafariButton.setTitleShadowColor(UIColor.fromWhiteAlpha(0.5, 1.0), UIControlState.Normal);
        startSafariButton.setTitleColor(UIColor.white(), UIControlState.Highlighted);
        startSafariButton.addOnTouchUpInsideListener(OnTouchUpInsideListener { (UIControl, UIEvent) -> UIApplication.getSharedApplication().openURL(NSURL("http://www.apple.com")) })
        view.addSubview(startSafariButton);
    }


     fun update (aColor: UIColor) {
        /*
         * There is a possibility that getRGBA could fail if aColor is not in a compatible color space. In such a case, the
         * arguments are not modified. Having default values will allow for a more graceful failure than picking up whatever is
         * currently on the stack.
         */

        var red = 0.0;
        var green = 0.0;
        var blue = 0.0;

        var rgba = aColor.getRGBA();

        //if (rgba == null) {
            /*
             * While setting default values for red, green, blue and alpha guards against undefined results if getRGBA fails,
             * aColor will be assigned as the backgroundColor of colorView a few lines down. Initialize aColor to the black color
             * so it matches the color code that will be displayed in the urlLabel.
             */
       //     aColor = UIColor.black();
      //  } else {
            red = rgba[0];
            green = rgba[1];
            blue = rgba[2];
      //  }

        redSlider?.setValue(red.toFloat());
        greenSlider?.setValue(green.toFloat());
        blueSlider?.setValue(blue.toFloat());

        colorView?.setBackgroundColor(aColor);

        /*
         * Construct the URL for the specified color. This URL allows another app to start LauncMe with the specific color
         * displayed initially.
         */
        urlField?.setText(java.lang.String.format("launchme://#%02X%02X%02X", (red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt()));

        urlFieldHeader?.setText("Tap to select the URL");
    }

    /** Custom implementation of the setter for the selectedColor property.
     * @param selectedColor */
    fun setSelectedColor (selectedColor: UIColor ) {
        if (!selectedColor.equals(this.selectedColor)) {
            this.selectedColor = selectedColor;
            update(selectedColor);
        }
    }

   override fun viewDidLoad () {
        super.viewDidLoad();
    }

    /** Deselects the text in the urlField if the user taps in the white space of this view controller's view. */
    override fun touchesEnded (touches: NSSet<UITouch>, event: UIEvent) {
        urlField?.setSelectedRange(NSRange(0, 0));
    }

    fun sliderValueChanged () {
        /*
         * Create a new UIColor object with the current value of all three sliders (it does not matter which one was actually
         * modified).
         */
        setSelectedColor(UIColor.fromRGBA(redSlider?.getValue()?.toDouble() as Double,
                greenSlider?.getValue()?.toDouble() as Double, blueSlider?.getValue()?.toDouble() as Double, 1.0));
    }

    fun getUrlFieldHeader () : UILabel? {
        return urlFieldHeader;
    }

}
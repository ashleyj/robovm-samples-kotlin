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

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIView;

class APLPlacardView : UIView() {
    private var background: UIImageView;
    private var textLabel: UILabel;

    {
        setFrame(CGRect(43.0, 131.0, 228.0, 98.0));

        background = UIImageView(UIImage.create("Placard.png"));
        background.setFrame(CGRect(0.0, 0.0, 228.0, 98.0));
        addSubview(background);

        textLabel = UILabel(CGRect(20.0, 38.0, 188.0, 22.0));
        textLabel.setText("PlacardView");
        textLabel.setFont(UIFont.getSystemFont(17.0));
        textLabel.setTextColor(UIColor.darkText());
        textLabel.setShadowColor(UIColor.lightText());
        textLabel.setShadowOffset(CGSize(1.0, 1.0));
        textLabel.setTextAlignment(NSTextAlignment.Center);
        addSubview(textLabel);
    }

    fun setDisplayString (displayString: String?) {
        textLabel.setText(displayString);
    }

}

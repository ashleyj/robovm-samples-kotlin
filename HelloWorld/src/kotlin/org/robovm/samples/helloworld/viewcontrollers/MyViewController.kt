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
package org.robovm.samples.helloworld.viewcontroller

import org.robovm.apple.uikit
import org.robovm.apple.uikit.UIView
import org.robovm.apple.uikit.UIImageView
import org.robovm.apple.uikit.UIImage
import org.robovm.apple.uikit.UIScreen
import org.robovm.apple.uikit.UITextField
import org.robovm.apple.uikit.UITextBorderStyle
import org.robovm.apple.uikit.UIKeyboardType
import org.robovm.apple.uikit.UIReturnKeyType
import org.robovm.apple.uikit.UITextFieldViewMode
import org.robovm.apple.uikit.UITextFieldDelegateAdapter
import org.robovm.apple.uikit.UILabel
import org.robovm.apple.coregraphics.CGRect
import org.robovm.apple.uikit.UIFont
import org.robovm.apple.uikit.UIColor
import org.robovm.apple.uikit.NSTextAlignment
import org.robovm.apple.foundation.NSSet
import org.robovm.apple.uikit.UITouch
import org.robovm.apple.uikit.UIEvent
import org.robovm.apple.uikit.UIViewController
import org.robovm.apple.foundation.Foundation

public class MyViewController() : UIViewController() {

    var textField: UITextField;
    var label: UILabel;
    var string: String = "";

    {
        var view = getView();

        // Setup background.
        var background: UIImageView = UIImageView(UIImage.create("Background.png"));
        background.setFrame(UIScreen.getMainScreen().getApplicationFrame());
        view.addSubview(background);

        // Setup textfield.
        textField = UITextField(CGRect(44.0, 32.0, 232.0, 31.0));
        textField.setBorderStyle(UITextBorderStyle.RoundedRect);
        textField.setPlaceholder("Hello, World!");
        textField.setClearsOnBeginEditing(true);
        textField.setKeyboardType(UIKeyboardType.ASCIICapable);
        textField.setReturnKeyType(UIReturnKeyType.Done);

      //  When the user starts typing, show the clear button in the text field.
        textField.setClearButtonMode(UITextFieldViewMode.WhileEditing);
        textField.setDelegate( object : UITextFieldDelegateAdapter() {
            override fun shouldReturn(textField: UITextField?): Boolean {
                // When the user presses return, take focus away from the text field so that the keyboard is dismissed.
                if (textField == textField) {
                    textField?.resignFirstResponder();
                    // Invoke the method that changes the greeting.
                    updateString();
                }
                return true;
            }
        });
        view.addSubview(textField);

        // Setup label.
        label = UILabel(CGRect(20.0, 104.0, 280.0, 44.0));
        label.setFont(UIFont.getSystemFont(24.0));
        label.setTextColor(UIColor.white());
        label.setTextAlignment(NSTextAlignment.Center);
        // When the view first loads, display the placeholder text that's in the text field in the label.
        label.setText(textField.getPlaceholder());
        view.addSubview(label);
    }


    override fun touchesBegan (touches: NSSet<UITouch>, event: UIEvent) {
        // Dismiss the keyboard when the view outside the text field is touched.
        textField.resignFirstResponder();
        // Revert the text field to the previous value.
        textField.setText(string);

        super.touchesBegan(touches, event);
    }

    fun updateString () {
        // Store the text of the text field in the 'string' instance variable.
        string = textField.getText();
        // Set the text of the label to the value of the 'string' instance variable.
        label.setText(string);
    }
}
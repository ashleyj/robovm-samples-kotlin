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

package org.robovm.samples.launchme;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSMatchingOptions;
import org.robovm.apple.foundation.NSPropertyList;
import org.robovm.apple.foundation.NSRange;
import org.robovm.apple.foundation.NSRegularExpression;
import org.robovm.apple.foundation.NSRegularExpressionOptions;
import org.robovm.apple.foundation.NSStringEncoding;
import org.robovm.apple.foundation.NSTextCheckingResult;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.samples.launchme.viewcontrollers.RootViewController;
import kotlin.platform.platformStatic
import org.robovm.apple.mobilecoreservices.UTType.URL

object LaunchMeApp : UIApplicationDelegateAdapter() {

    override fun didFinishLaunching (application: UIApplication, launchOptions: UIApplicationLaunchOptions? ): Boolean {

        // Set up the view controller.
        val rootViewController = RootViewController();

        // Create a new window at screen size.
        val window = UIWindow(UIScreen.getMainScreen().getBounds());
        // Set our viewcontroller as the root controller for the window.
        window.setRootViewController(rootViewController);
        // Make the window visible.
        window.makeKeyAndVisible();

        /*
         * Retains the window object until the application is deallocated. Prevents Java GC from collecting the window object too
         * early.
         */
        addStrongRef(window);

        return true;
    }

    override fun openURL(application: UIApplication?, url: NSURL?, sourceApplication: String?, annotation: NSPropertyList?): Boolean {

        /*
         * You should be extremely careful when handling URL requests. Take steps to validate the URL before handling it.
         */

        // Check if the incoming URL is null.
        if (url == null) return false;

        // Invoke our helper method to parse the incoming URL and extract the color to display.
        var launchColor = extractColorFromLaunchURL(url);
        // Stop if the url could not be parsed.
        if (launchColor == null) return true;

        val rootViewController = getWindow().getRootViewController() as RootViewController;

        // Assign the created color object a the selected color for display in RootViewController.
        rootViewController.setSelectedColor(launchColor!!);

        // Update the UI of RootViewController to notify the user that the app was launched from an incoming URL request.
        rootViewController.getUrlFieldHeader()?.setText(
                "The app was launched with the following URL");

        return true;
    }

    /** Helper method that parses a URL and returns a UIColor object representing the first HTML color code it finds or nil if a
     * valid color code is not found. This logic is specific to this sample. Your URL handling code will differ. */
    private fun extractColorFromLaunchURL (url: NSURL) : UIColor ? {
        /*
         * Hexadecimal color codes begin with a number sign (#) followed by six hexadecimal digits. Thus, a color in this format
         * is represented by three bytes (the number sign is ignored). The value of each byte corresponds to the intensity of
         * either the red, blue or green color components, in that order from left to right. Additionally, there is a shorthand
         * notation with the number sign (#) followed by three hexadecimal digits. This notation is expanded to the six digit
         * notation by doubling each digit: #123 becomes #112233.
         */

        // Convert the incoming URL into a string. The '#' character will be percent escaped. That must be undone.
        var urlString = NSURL.decodeURLString(url.getAbsoluteString(), NSStringEncoding.UTF8);
        // Stop if the conversion failed.
        if (urlString == null) return null;

        /*
         * Create a regular expression to locate hexadecimal color codes in the incoming URL. Incoming URLs can be malicious. It
         * is best to use vetted technology, such as NSRegularExpression, to handle the parsing instead of writing your own
         * parser.
         */
        var regex: NSRegularExpression;
        try {
            regex = NSRegularExpression("#[0-9a-f]{3}([0-9a-f]{3})?", NSRegularExpressionOptions.CaseInsensitive);
        } catch (e: Exception) {
            // Check for any error returned. This can be a result of incorrect regex syntax.
            System.err.println(e);
            return null;
        }

        /*
         * Extract all the matches from the incoming URL string. There must be at least one for the URL to be valid (though
         * matches beyond the first are ignored.)
         */
        val regexMatches : NSArray<NSTextCheckingResult> = regex.getMatches(urlString, NSMatchingOptions(0), NSRange(0L,
        urlString.length().toLong()));
        if (regexMatches.size() < 1) return null;

        // Extract the first matched string
        var start = regexMatches.get(0).getRange().getLocation();
        var end = start + regexMatches.get(0).getRange().getLength();
        val matchedString = urlString.substring(start.toInt(), end.toInt());

        /*
         * At this point matchedString will look similar to either #FFF or #FFFFFF. The regular expression has guaranteed that
         * matchedString will be no longer than seven characters.
         */

        // Convert matchedString into a long. The '#' character should not be included.
        var hexColorCode = java.lang.Long.parseLong(matchedString.substring(1), 16);

        var red : Float;
        var green: Float;
        var blue : Float;

        // If the color code is in six digit notation...
        if (matchedString.length() - 1 > 3) {
            /*
             * Extract each color component from the integer representation of the color code. Each component has a value of
             * [0-255] which must be converted into a normalized float for consumption by UIColor.
             */

            red = ((hexColorCode and 0x00FF0000) shr(16)) / 255.0f;
            green = ((hexColorCode and 0x0000FF00) shr(8)) / 255.0f;
            blue = (hexColorCode and 0x000000FF) / 255.0f;
        }
        // The color code is in shorthand notation...
        else {
            /*
             * Extract each color component from the integer representation of the color code. Each component has a value of
             * [0-255] which must be converted into a normalized float for consumption by UIColor.
             */
            red = (((hexColorCode and 0x00000F00) shr(8)) or  ((hexColorCode and 0x00000F00) shr(4))) / 255.0f;
            green = (((hexColorCode and 0x000000F0) shr(4)) or  (hexColorCode and 0x000000F0)) / 255.0f;
            blue = ((hexColorCode and 0x0000000F) or  ((hexColorCode and 0x0000000F) shr(4))) / 255.0f;
        }
        // Create and return a UIColor object with the extracted components.
        return UIColor.fromRGBA(red.toDouble(), green.toDouble(), blue.toDouble(), 1.0);
    }

}


object LaunchMe {
    platformStatic fun main (args: Array<String>) {
        val pool = NSAutoreleasePool();
        UIApplication.main(args, null: Class<UIApplication> ?, javaClass<LaunchMeApp>());
        pool.close();
    }
}


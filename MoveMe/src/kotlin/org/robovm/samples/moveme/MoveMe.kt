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

package org.robovm.samples.moveme;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.samples.moveme.viewcontrollers.APLViewController;
import kotlin.platform.platformStatic;

object MoveMeApp : UIApplicationDelegateAdapter() {


    override fun didFinishLaunching (application: UIApplication, launchOptions: UIApplicationLaunchOptions?): Boolean {
        // Set up the view controller.
        val rootViewController = APLViewController();

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

}

object MoveMe {
    platformStatic fun main (args: Array<String>) {
        val pool = NSAutoreleasePool();
        UIApplication.main(args, null: Class<UIApplication> ?, javaClass<MoveMeApp>());
        pool.close();
    }
}

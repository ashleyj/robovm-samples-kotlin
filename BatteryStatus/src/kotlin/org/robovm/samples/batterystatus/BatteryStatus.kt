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

package org.robovm.samples.batterystatus;

import org.robovm.apple.uikit.UIApplicationDelegateAdapter
import org.robovm.apple.uikit.UIApplication
import org.robovm.apple.uikit.UIApplicationLaunchOptions
import org.robovm.apple.uikit.UINavigationController
import org.robovm.apple.uikit.UIWindow
import org.robovm.apple.uikit.UIScreen
import org.robovm.apple.foundation.NSAutoreleasePool
import org.robovm.apple.foundation
import kotlin.platform.platformStatic


object BatteryStatusApp : UIApplicationDelegateAdapter() {

    override fun didFinishLaunching(application: UIApplication, launchOptions: UIApplicationLaunchOptions?): Boolean {
        // Set up the view controller.
        var batStatViewController = BatStatViewController();
        var navigationController = UINavigationController(batStatViewController);

        // Create a new window at screen size.
        var window = UIWindow(UIScreen.getMainScreen().getBounds());
        // Set our viewcontroller as the root controller for the window.
        window.setRootViewController(navigationController);
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



object BatteryStatus {
    platformStatic fun main (args : Array<String>) {
        var pool: NSAutoreleasePool = foundation.NSAutoreleasePool();
        UIApplication.main(args, null : Class<UIApplication>?, javaClass<BatteryStatusApp>());
        pool.close();
    }
}

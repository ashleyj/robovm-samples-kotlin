package org.robovm.samples.datecell

import org.robovm.apple.uikit.UIApplicationDelegateAdapter
import org.robovm.apple.uikit.UIApplication
import org.robovm.apple.uikit.UIApplicationLaunchOptions
import org.robovm.apple.uikit.UIWindow
import org.robovm.apple.uikit.UIScreen
import org.robovm.apple.foundation.NSAutoreleasePool
import org.robovm.samples.datecell.viewcontroller.MyTableViewController
import kotlin.platform.platformStatic

object DateCellApp : UIApplicationDelegateAdapter() {

    override fun didFinishLaunching(application: UIApplication, launchOptions: UIApplicationLaunchOptions?): Boolean {
        var rootViewController: MyTableViewController = MyTableViewController();
        var window: UIWindow = UIWindow(UIScreen.getMainScreen().getBounds());

        window.setRootViewController(rootViewController);
        window.makeKeyAndVisible();

        addStrongRef(window);

        return true;
    }
}

object DateCell {
    platformStatic fun main(args: Array<String>) {
        val pool: NSAutoreleasePool = NSAutoreleasePool();
        UIApplication.main(args, null : Class<UIApplication>?, javaClass<DateCellApp>());
        pool.close();
    }
}
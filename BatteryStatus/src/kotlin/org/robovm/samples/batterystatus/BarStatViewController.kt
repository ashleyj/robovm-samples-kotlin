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

package org.robovm.samples.batterystatus

import org.robovm.apple.uikit.UITableViewController
import org.robovm.apple.uikit.UITableView
import org.robovm.apple.uikit.UIScreen
import org.robovm.apple.uikit.UITableViewStyle
import org.robovm.apple.uikit.UIColor
import org.robovm.apple.uikit.UIControl
import org.robovm.apple.uikit.UIDevice
import org.robovm.apple.uikit.UISwitch
import org.robovm.apple.foundation.NSString
import org.robovm.apple.foundation.NSNumberFormatter
import org.robovm.apple.foundation.NSNumberFormatterStyle
import org.robovm.apple.foundation.NSNumber
import org.robovm.apple.uikit.UITableViewCell
import org.robovm.apple.uikit.UIDeviceBatteryState
import org.robovm.apple.uikit.UITableViewCellAccessoryType
import org.robovm.apple.uikit.UILabel
import java.util.ArrayList
import java.util.Arrays
import org.robovm.apple.foundation.NSIndexPath
import org.robovm.apple.uikit.UIView
import org.robovm.apple.coregraphics.CGRect
import org.robovm.apple.uikit.UIFont


public class BatStatViewController : UITableViewController() {

    private var monitorSwitch: UISwitch? = null;
    private var switchAction: UIControl.OnValueChangedListener;

    private var levelLabel: UILabel? = null;
    private var unknownCell = UITableViewCell(CGRect(0.0, 265.0, 320.0, 44.0));
    private var unpluggedCell = UITableViewCell(CGRect(0.0, 309.0, 320.0, 44.0));
    private var chargingCell = UITableViewCell(CGRect(0.0, 353.0, 320.0, 44.0));
    private var fullCell = UITableViewCell(CGRect(0.0, 397.0, 320.0, 44.0));

    {
        getNavigationItem().setTitle("Battery Status");

        val tableView = UITableView(UIScreen.getMainScreen().getApplicationFrame(), UITableViewStyle.Grouped)
        tableView.setBackgroundColor(UIColor.groupTableViewBackground())
        tableView.setDataSource(this)
        setTableView(tableView)

        switchAction = object : UIControl.OnValueChangedListener {
            override fun onValueChanged(control: UIControl) {
                UIDevice.getCurrentDevice().setBatteryMonitoringEnabled((control as UISwitch).isOn())
                updateBatteryLevel()
                updateBatteryState()
            }
        }
    }

    fun updateBatteryLevel () {
        var batteryLevel = UIDevice.getCurrentDevice().getBatteryLevel();
        if (batteryLevel < 0) {
            // -1.0 means battery state is UIDeviceBatteryState.Unknown
            levelLabel?.setText(NSString.getLocalizedString("Unknown"));
        } else {
            var numberFormatter = NSNumberFormatter();
            numberFormatter.setNumberStyle(NSNumberFormatterStyle.Percent);
            numberFormatter.setMaximumFractionDigits(1);

            var levelObj = NSNumber.valueOf(batteryLevel);
            levelLabel?.setText(numberFormatter.format(levelObj));
        }
    }

    fun updateBatteryState () {


        val batteryStateCells = array<UITableViewCell>(unknownCell, unpluggedCell, chargingCell, fullCell)

        val currentState = UIDevice.getCurrentDevice().getBatteryState();

        for ( i in batteryStateCells.indices) {
            var cell = batteryStateCells[i];

            if (i + UIDeviceBatteryState.Unknown.value() == currentState.value()) {
                cell.setAccessoryType(UITableViewCellAccessoryType.Checkmark);
            } else {
                cell.setAccessoryType(UITableViewCellAccessoryType.None);
            }
        }
    }

    override fun viewDidLoad () {
        super.viewDidLoad();

        // Register for battery level and state change notifications.
        UIDevice.Notifications.observeBatteryLevelDidChange( {
                updateBatteryLevel();
        });
        UIDevice.Notifications.observeBatteryStateDidChange({
                updateBatteryLevel();
                updateBatteryState();
        });
    }

    override fun viewDidLayoutSubviews () {
        // Enable battery monitoring.
        UIDevice.getCurrentDevice().setBatteryMonitoringEnabled(true);
        updateBatteryLevel();
        updateBatteryState();
    }

    override fun getNumberOfSections (tableView: UITableView): Long {
        return 3;
    }

    override fun getNumberOfRowsInSection (tableView: UITableView,section: Long): Long {
        when (section) {
            0L, 1L -> return 1;
            else -> {
                return 4;
            }
        }
    }

    override fun getCellForRow(tableView: UITableView, indexPath: NSIndexPath): UITableViewCell {
        val contentView: UIView

        when (indexPath.getSection().toInt()) {
            0 -> {
                val switchCell = UITableViewCell(CGRect(0.0, 99.0, 320.0, 44.0));
                contentView = switchCell.getContentView();

                val monitoringLabel = UILabel(CGRect(20.0, 11.0, 83.0, 21.0));
                monitoringLabel.setText("Monitoring");
                monitoringLabel.setFont(UIFont.getSystemFont(17.0));
                monitoringLabel.setTextColor(UIColor.darkText());
                contentView.addSubview(monitoringLabel);

                monitorSwitch = UISwitch(CGRect(251.0, 6.0, 51.0, 31.0));
                monitorSwitch!!.setOn(true);
                monitorSwitch!!.addOnValueChangedListener(switchAction);
                contentView.addSubview(monitorSwitch);

                return switchCell;
            }
            1 -> {
                val levelCell = UITableViewCell(CGRect(0.0, 173.0, 320.0, 44.0));
                contentView = levelCell.getContentView();

                val levelCaptionLabel = UILabel(CGRect(20.0, 11.0, 42.0, 21.0));
                levelCaptionLabel.setText("Level");
                levelCaptionLabel.setFont(UIFont.getSystemFont(17.0));
                levelCaptionLabel.setTextColor(UIColor.darkText());
                contentView.addSubview(levelCaptionLabel);

                levelLabel = UILabel(CGRect(220.0, 11.0, 80.0, 21.0));
                levelLabel!!.setFont(UIFont.getSystemFont(17.0));
                levelLabel!!.setTextColor(UIColor.darkText());
                contentView.addSubview(levelLabel);

                return levelCell;
            }
            else -> when (indexPath.getRow().toInt()) {
                0 -> {
                    contentView = unknownCell.getContentView();

                    val unknownLabel = UILabel(CGRect(20.0, 11.0, 80.0, 21.0));
                    unknownLabel.setText("Unknown");
                    unknownLabel.setFont(UIFont.getSystemFont(17.0));
                    contentView.addSubview(unknownLabel);

                    return unknownCell;
                }
                1 -> {
                    contentView = unpluggedCell.getContentView();

                    val unpluggedLabel = UILabel(CGRect(20.0, 11.0, 90.0, 21.0));
                    unpluggedLabel.setText("Unplugged");
                    unpluggedLabel.setFont(UIFont.getSystemFont(17.0));
                    contentView.addSubview(unpluggedLabel);

                    return unpluggedCell;
                }
                2 -> {
                    contentView = chargingCell.getContentView();

                    val chargingLabel = UILabel(CGRect(20.0, 11.0, 74.0, 21.0));
                    chargingLabel.setText("Charging");
                    chargingLabel.setFont(UIFont.getSystemFont(17.0));
                    contentView.addSubview(chargingLabel);

                    return chargingCell;
                }
                else -> {
                    contentView = fullCell.getContentView();

                    val fullLabel = UILabel(CGRect(20.0, 11.0, 42.0, 21.0));
                    fullLabel.setText("Full");
                    fullLabel.setFont(UIFont.getSystemFont(17.0));
                    contentView.addSubview(fullLabel);

                    return fullCell;
                }
            }
        }
    }

    override fun getTitleForHeader(tableView: UITableView, section: Long): String? {
        when (section.toInt()) {
            2 -> return "Battery State"
            else -> return null
        }
    }

}


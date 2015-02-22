package org.robovm.samples.datecell.viewcontroller

import org.robovm.apple.uikit.UITableViewController
import org.robovm.apple.foundation.NSDate
import org.robovm.apple.uikit.UITableViewCell
import org.robovm.apple.uikit.UITableViewCellStyle
import org.robovm.apple.foundation.NSIndexPath
import org.robovm.apple.uikit.UIDatePicker
import org.robovm.apple.uikit.UIBarButtonItem
import org.robovm.apple.foundation.NSObject
import org.robovm.apple.uikit.UIControl
import org.robovm.objc.block.VoidBooleanBlock
import org.robovm.apple.uikit.UITableView
import java.util.ArrayList
import org.robovm.apple.foundation.NSDateFormatter
import org.robovm.apple.uikit.UITableViewStyle
import org.robovm.apple.foundation.NSDateFormatterStyle
import org.robovm.apple.uikit.UIDatePickerMode
import org.robovm.apple.uikit.UIBarButtonSystemItem
import org.robovm.apple.uikit.UIView
import org.robovm.apple.foundation.NSLocale
import org.robovm.apple.foundation.NSNotificationCenter
import org.robovm.apple.uikit.UITableViewCellSelectionStyle
import org.robovm.apple.foundation.NSArray
import org.robovm.apple.uikit.UITableViewRowAnimation
import org.robovm.apple.foundation.Foundation
import org.robovm.apple.coregraphics.CGRect

public class MyTableViewController : UITableViewController() {

    public inner class CellData(var title: String, var date: NSDate?)

    public inner class DateViewCell : UITableViewCell() {
        override fun init(style: UITableViewCellStyle, reuseIdentifier: String): Long {
            // ignore the style argument and force the creation with a specific style
            return super.init(UITableViewCellStyle.Value1, reuseIdentifier);
        }
    }

    private val data = ArrayList<CellData?>();
    private val dateFormatter = NSDateFormatter();

    // keep track which indexPath points to the cell with UIDatePicker
    private var datePickerIndexPath : NSIndexPath ?= null;

    private val pickerView: UIDatePicker;

    // this button appears only when the date picker is shown (iOS 6.1.x or earlier)
    private val doneButton: UIBarButtonItem;

    private val localeNotif: NSObject;

    {
        setTitle("DateCell");

        setTableView(UITableView(CGRect(0.0, 64.0, 320.0, 460.0), UITableViewStyle.Grouped));

        // setup our data source
        data.add(CellData("Tap a cell to change its date:", null));
        data.add(CellData("Start Date", NSDate()));
        data.add(CellData("End Date", NSDate()));
        data.add(CellData("(other item1)", null));
        data.add(CellData("(other item2)", null));

        dateFormatter.setDateStyle(NSDateFormatterStyle.Short);
        dateFormatter.setTimeStyle(NSDateFormatterStyle.No);

        pickerView = UIDatePicker(CGRect(0.0, 0.0, 320.0, 216.0));
        pickerView.setDatePickerMode(UIDatePickerMode.Date);
        pickerView.addOnValueChangedListener(object : UIControl.OnValueChangedListener {
            /** User chose to change the date by changing the values inside the UIDatePicker.
             * @param control */
            override fun onValueChanged(control: UIControl) {
                var targetedCellIndexPath: NSIndexPath?;

                if (hasInlineDatePicker()) {
                    // inline date picker: update the cell's date "above" the date picker cell
                    targetedCellIndexPath = NSIndexPath.createWithRow(datePickerIndexPath!!.getRow() - 1, 0);
                } else {
                    // external date picker: update the current "selected" cell's date
                    targetedCellIndexPath = getTableView().getIndexPathForSelectedRow();
                }

                val cell = getTableView().getCellForRow(targetedCellIndexPath);
                val targetedDatePicker = pickerView;

                // update our data model
                val itemData = data.get(targetedCellIndexPath!!.getRow().toInt());
                itemData?.date = targetedDatePicker.getDate();

                // update the cell's date string
                cell.getDetailTextLabel().setText(dateFormatter.format(targetedDatePicker.getDate()));
            }
        })

        doneButton = UIBarButtonItem(UIBarButtonSystemItem.Done, object : UIBarButtonItem.OnClickListener {
            /** User chose to finish using the UIDatePicker by pressing the "Done" button (used only for "non-inline" date picker,
             * iOS 6.1.x or earlier)
             * @param barButtonItem */
            override fun onClick(barButtonItem: UIBarButtonItem) {
                val pickerFrame = pickerView.getFrame();
                pickerFrame.getOrigin().setY(getView().getFrame().getHeight());

                // animate the date picker out of view
                UIView.animate(PICKER_ANIMATION_DURATION, {
                        pickerView.setFrame(pickerFrame);
                }, {
                        pickerView.removeFromSuperview();
                    }
                );

                // remove the "Done" button in the navigation bar
                getNavigationItem().setRightBarButtonItem(null);

                // deselect the current table cell
                val indexPath = getTableView().getIndexPathForSelectedRow();
                getTableView().deselectRow(indexPath, true);
            }
        })

        // if the local changes while in the background, we need to be notified so we can update the date
        // format in the table view cells
        localeNotif = NSLocale.Notifications.observeCurrentLocaleDidChange {
                // the user changed the locale (region format) in Settings, so we are notified here to
                // update the date format in the table view cells
                getTableView().reloadData();
        };

        getTableView().registerReusableCellClass(javaClass<DateViewCell>(), DATE_CELL_ID);
        getTableView().registerReusableCellClass(javaClass<UITableViewCell>(), DATE_PICKER_ID);
        getTableView().registerReusableCellClass(javaClass<UITableViewCell>(), OTHER_CELL_ID);
    }

    override fun dispose(finalizing: Boolean) {
        NSNotificationCenter.getDefaultCenter().removeObserver(localeNotif);
        super.dispose(finalizing);
    }

    /** Determines if the given indexPath has a cell below it with a UIDatePicker.
     * @param indexPath The indexPath to check if its cell has a UIDatePicker below it. */
    private fun hasPickerForIndexPath(indexPath: NSIndexPath): Boolean {
        var hasDatePicker = false;

        var targetedRow = indexPath.getRow();
        targetedRow++;

        val checkDatePickerCell = getTableView().getCellForRow(NSIndexPath.createWithRow(targetedRow, 0));
        val checkDatePicker = checkDatePickerCell.getViewWithTag(DATE_PICKER_TAG);

        hasDatePicker = checkDatePicker != null;
        return hasDatePicker;
    }

    /** Updates the UIDatePicker's value to match with the date of the cell above it. */
    private fun updateDatePicker() {
        if (datePickerIndexPath != null) {
            val associatedDatePickerCell = getTableView().getCellForRow(datePickerIndexPath);

            val targetedDatePicker: UIDatePicker? = associatedDatePickerCell.getViewWithTag(DATE_PICKER_TAG) as UIDatePicker?;
            // we found a UIDatePicker in this cell, so update it's date value
            val itemData = data.get(datePickerIndexPath!!.getRow().toInt() - 1);
            targetedDatePicker?.setDate(itemData!!.date, false);
        }
    }

    /** @return if the UITableViewController has a UIDatePicker in any of its cells. */
    private fun hasInlineDatePicker(): Boolean {
        return datePickerIndexPath != null;
    }

    /** Determines if the given indexPath points to a cell that contains the UIDatePicker.
     * @param indexPath The indexPath to check if it represents a cell with the UIDatePicker.
     * @return */
    private fun indexPathHasPicker(indexPath: NSIndexPath): Boolean {
        return hasInlineDatePicker() && datePickerIndexPath!!.getRow() == indexPath.getRow();
    }

    /** Determines if the given indexPath points to a cell that contains the start/end dates.
     * @param indexPath The indexPath to check if it represents start/end date cell.
     * @return */
    private fun indexPathHasDate(indexPath: NSIndexPath): Boolean {
        var hasDate = false;

        if ((indexPath.getRow() == DATE_START_ROW) || (indexPath.getRow() == DATE_END_ROW || (hasInlineDatePicker() && (indexPath.getRow() == DATE_END_ROW + 1)))) {
            hasDate = true;
        }

        return hasDate;
    }

    override fun getHeightForRow(tableView: UITableView, indexPath: NSIndexPath): Double {
        return if (indexPathHasPicker(indexPath)) PICKER_CELL_ROW_HEIGHT else tableView.getRowHeight();
    }

    override fun getNumberOfRowsInSection(tableView: UITableView, section: Long): Long {
        if (hasInlineDatePicker()) {
            // we have a date picker, so allow for it in the number of rows in this section
            var numRows = data.size();
            return (++numRows).toLong();
        }
        return data.size().toLong();
    }

    override fun getCellForRow(tableView: UITableView, indexPath: NSIndexPath): UITableViewCell {

        var cellID = OTHER_CELL_ID;

        if (indexPathHasPicker(indexPath)) {
            // the indexPath is the one containing the inline date picker
            cellID = DATE_PICKER_ID ;// the current/opened date picker cell
        } else if (indexPathHasDate(indexPath)) {
            // the indexPath is one that contains the date information
            cellID = DATE_CELL_ID; // the start/end date cells
        }

        var cell: UITableViewCell = getTableView().dequeueReusableCell(cellID)
        if (cellID == DATE_PICKER_ID) {
            cell.getContentView().addSubview(pickerView);
        }

        if (indexPath.getRow() == 0.toLong()) {
            // we decide here that first cell in the table is not selectable (it's just an indicator)
            cell.setSelectionStyle(UITableViewCellSelectionStyle.None);
        }

        // if we have a date picker open whose cell is above the cell we want to update,
        // then we have one more cell than the model allows
        var modelRow = indexPath.getRow().toInt();
        if (datePickerIndexPath != null && datePickerIndexPath!!.getRow() <= indexPath.getRow()) {
            modelRow--;
        }

        val itemData = data.get(modelRow);

        // proceed to configure our cell
        when (cellID) {
            DATE_CELL_ID -> {
                // we have either start or end date cells, populate their date field
                cell.getDetailTextLabel().setText(dateFormatter.format(itemData?.date));
                // this cell is a non-date cell, just assign it's text label
                cell.getTextLabel().setText(itemData?.title);
            }
            OTHER_CELL_ID -> cell.getTextLabel().setText(itemData?.title);
            else -> {
            }
        }

        return cell;
    }

    /** Adds or removes a UIDatePicker cell below the given indexPath.
     * @param indexPath The indexPath to reveal the UIDatePicker. */
    private fun toggleDatePicker(indexPath: NSIndexPath) {
        getTableView().beginUpdates();

        val indexPaths = NSArray<NSIndexPath>(NSIndexPath.createWithRow(indexPath.getRow() + 1, 0));

        // check if 'indexPath' has an attached date picker below it
        if (hasPickerForIndexPath(indexPath)) {
            // found a picker below it, so remove it
            getTableView().deleteRows(indexPaths, UITableViewRowAnimation.Fade);
        } else {
            // didn't find a picker below it, so we should insert it
            getTableView().insertRows(indexPaths, UITableViewRowAnimation.Fade);
        }

        getTableView().endUpdates();
    }

    /** Reveals the date picker inline for the given indexPath, called by "didSelectRowAtIndexPath".
     * @param indexPath The indexPath to reveal the UIDatePicker. */
    private fun displayInlineDatePickerForRow(indexPath: NSIndexPath) {
        // display the date picker inline with the table content
        getTableView().beginUpdates();

        var before = false; // indicates if the date picker is below "indexPath", help us determine which row to reveal
        if (hasInlineDatePicker()) {
            before = datePickerIndexPath!!.getRow() < indexPath.getRow();
        }

        val sameCellClicked = datePickerIndexPath != null && datePickerIndexPath!!.getRow() - 1 == indexPath.getRow();

        // remove any date picker cell if it exists
        if (hasInlineDatePicker()) {
            getTableView().deleteRows(NSArray<NSIndexPath>(NSIndexPath.createWithRow(datePickerIndexPath!!.getRow(), 0)), UITableViewRowAnimation.Fade);
            datePickerIndexPath = null;
        }

        if (!sameCellClicked) {
            // hide the old date picker and display the new one
            val rowToReveal = if (before) indexPath.getRow() - 1 else indexPath.getRow();
            val indexPathToReveal = NSIndexPath.createWithRow(rowToReveal, 0);

            toggleDatePicker(indexPathToReveal);
            datePickerIndexPath = NSIndexPath.createWithRow(indexPathToReveal.getRow() + 1, 0);
        }

        // always deselect the row containing the start or end date
        getTableView().deselectRow(indexPath, true);

        getTableView().endUpdates();

        // inform our date picker of the current date to match the current cell
        updateDatePicker();
    }

    /** Reveals the UIDatePicker as an external slide-in view, iOS 6.1.x and earlier, called by "didSelectRowAtIndexPath".
     * @param indexPath The indexPath used to display the UIDatePicker. */
    private fun displayExternalDatePickerForRow(indexPath: NSIndexPath) {
        // first update the date picker's date value according to our model
        val itemData = data.get(indexPath.getRow().toInt());
        pickerView.setDate(itemData?.date, true);

        // the date picker might already be showing, so don't add it to our view
        if (pickerView.getSuperview() == null) {
            val startFrame = pickerView.getFrame();
            val endFrame = pickerView.getFrame();

            // the start position is below the bottom of the visible frame
            startFrame.getOrigin().setY(getView().getFrame().getHeight());

            // the end position is slid up by the height of the view
            endFrame.getOrigin().setY(startFrame.getOrigin().getY() - endFrame.getHeight());

            pickerView.setFrame(startFrame);

            getView().addSubview(pickerView);

            // animate the date picker into view
            UIView.animate(PICKER_ANIMATION_DURATION, {
                    pickerView.setFrame(endFrame);
            }, {
                    // add the "Done" button to the nav bar
                    getNavigationItem().setRightBarButtonItem(doneButton);
                }
            );
        }
    }

    override fun didSelectRow(tableView: UITableView, indexPath: NSIndexPath) {
        val cell = tableView.getCellForRow(indexPath);
        if (cell.getReuseIdentifier() == DATE_CELL_ID) {
            if (Foundation.getMajorSystemVersion() >= 7) {
                displayInlineDatePickerForRow(indexPath);
            } else {
                displayExternalDatePickerForRow(indexPath);
            }
        } else {
            tableView.deselectRow(indexPath, true);
        }
    }

    class object {
        private val PICKER_ANIMATION_DURATION = 0.40; // duration for the animation to slide the date picker into view
        private val PICKER_CELL_ROW_HEIGHT = 216.0;
        private val DATE_PICKER_TAG = 99.toLong(); // view tag identifiying the date picker view

        // keep track of which rows have date cells
        private val DATE_START_ROW = 1.toLong();
        private val DATE_END_ROW = 2.toLong()

        private val DATE_CELL_ID = "dateCell"; // the cells with the start or end date
        private val DATE_PICKER_ID = "datePicker"; // the cell containing the date picker
        private val OTHER_CELL_ID = "otherCell"; // the remaining cells at the end
    }
}
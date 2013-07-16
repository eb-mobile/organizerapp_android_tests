package com.eventbrite.organizer.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ExpandableListView;

import com.eventbrite.organizer.R;
import com.eventbrite.organizer.activities.AttendeeListActivity;
import com.eventbrite.organizer.activities.EventDashboardActivity;
import com.eventbrite.organizer.activities.LoginActivity;
import com.eventbrite.organizer.activities.SettingsActivity;
import com.eventbrite.organizer.utils.EBDateUtils;
import com.google.zxing.client.android.eventbrite.EBCaptureActivity;
import com.jayway.android.robotium.solo.Solo;

public class OrganizerTests extends ActivityInstrumentationTestCase2<LoginActivity> {
	private static final String donateTicketTypeName = "D-1";

	private static final String paidTicketTypeName = "P-1";

	private static final String freeTicketTypeName = "F-1";

	// member variables
	private Solo s;

	private final String attendeeName = "Tu, Raymond";

	// constructor
	public OrganizerTests() {
		super(LoginActivity.class);
	}

	// android junit methods
	@Override
	protected void setUp() throws Exception {
		s = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		if (!s.searchButton(s.getString(R.string.log_in))) {
			logout();
		}
		s.finishOpenedActivities();
	}

	// non-test private methods
	// login
	protected void login(String username, String password) {


		while (!s.searchButton(s.getString(R.string.log_in))) {
			s.waitForText(s.getString(R.string.dashboard_title));
			s.clickOnText(s.getString(R.string.dashboard_title));
			s.clickOnMenuItem(s.getString(R.string.exit_menu));
			s.clickOnButton(s.getString(R.string.yes));
		}


		s.enterText(0, username);
		s.enterText(1, password);
		s.clickOnButton(s.getString(R.string.log_in));
		s.waitForDialogToClose(20000);
		if (s.searchText(s.getString(R.string.wrongtoken_dialog_title))) {
			s.clickOnButton(s.getString(R.string.ok));
			login(username, password);
		} else {
			s.waitForText(s.getString(R.string.dashboard_title));
		}

	}

	// logout
	protected void logout() {
		s.clickOnMenuItem(s.getString(R.string.logout));
		try {
			s.clickOnButton(s.getString(R.string.yes));
		} catch (junit.framework.AssertionFailedError e) {
			s.clickOnButton(s.getString(R.string.logout));
		}
		assertTrue(s.searchText(s.getString(R.string.log_in)));

	}

	protected void goToAttendeesScreen() {

		s.clickOnText("TestRegularEvent");
		assertTrue(s.waitForText(s.getString(R.string.check_in)));
		s.clickOnButton(s.getString(R.string.check_in));
		assertTrue(s.waitForDialogToClose(20000));
		assertTrue(s.waitForText(s.getString(R.string.attendeelist_title)));
	}

	protected void goToSearchAttendeeScreen() {
		try {
			s.clickOnMenuItem(s.getString(R.string.search_menu));
		} catch (junit.framework.AssertionFailedError e) {

			s.clickOnActionBarItem(R.id.menu_search);
		}

		assertTrue(s.searchText(s.getString(R.string.searchbox_hint)));
	}

	protected void goToBarcodeScanScreen() {
		s.clickOnText(s.getString(R.string.attendeelist_title));
		s.clickOnActionBarItem(R.id.menu_scan);
		s.waitForText(s.getString(R.string.center_barcode));
	}

	void waitForNoText(String text) {
		while (s.searchText(text, true)) {
			s.sleep(200);
		}
	}

	// test cases
	// Submit "Log in" without any text entered for email and password
	public void testLoginNoEmailNoPassword() {
		s.enterText(0, "");
		s.enterText(1, "");
		s.sendKey(Solo.ENTER);
		s.clickOnButton(s.getString(R.string.log_in));
		assertTrue(s.searchText(s.getString(R.string.log_in)));
	}

	// Submit "Log in" without any text entered for email
	public void testLoginNoEmail() {
		s.enterText(0, "");
		s.enterText(1, "qwert12345");
		// enter next/enter key
		s.clickOnButton(s.getString(R.string.log_in));
		assertTrue(s.searchText(s.getString(R.string.log_in)));
	}

	// Submit "Log in" without any text entered for password
	public void testLoginNoPassword() {
		s.enterText(0, "raymond+0@evbqa.com");
		s.enterText(1, "");
		s.clickOnButton(s.getString(R.string.log_in));
		assertTrue(s.searchText(s.getString(R.string.log_in)));
	}

	// Submit "Log in" with invalid email address format
	public void testLoginInvalidEmailFormat() {
		// enter garbage text for email
		s.enterText(0, "qwert12345");
		s.enterText(1, "qwert12345");
		s.clickOnButton(s.getString(R.string.log_in));

		assertTrue(s.waitForText(s.getString(R.string.wrong_loginpassword_dialog_text)));
		s.clickOnButton(s.getString(R.string.ok));
	}

	// Submit "Log in" with unrecognized email address which is a valid email
	// format
	public void testLoginUnrecognizedEmail() {
		s.enterText(0, "oyrtu90nhjc@gmail.com");
		s.enterText(1, "qwert12345");
		s.clickOnButton(s.getString(R.string.log_in));

		assertTrue(s.waitForText(s.getString(R.string.wrong_loginpassword_dialog_text)));
		s.clickOnButton(s.getString(R.string.ok));
	}

	// Submit "Log in" with valid email but with wrong password
	public void testLoginValidEmailWrongPassword() {
		// enter a valid email
		s.enterText(0, "raymond+0@evbqa.com");
		// enter wrong password
		s.enterText(1, "wrongPassword");
		s.clickOnButton(s.getString(R.string.log_in));

		assertTrue(s.waitForText(s.getString(R.string.wrong_loginpassword_dialog_text)));
		s.clickOnButton(s.getString(R.string.ok));
	}

	// Log in with valid email and valid password and then log out
	public void testLoginAndLogout() {
		// enter a valid email
		login("raymond+0@evbqa.com", "qwert");
	}

	// Log in, click on "Log Out" in menu, then press cancel button
	public void testLogoutCancel() {
		login("raymond+0@evbqa.com", "qwert");
		s.clickOnMenuItem(s.getString(R.string.logout));
		// press cancel button
		s.clickOnButton(s.getString(R.string.cancel));
		s.assertCurrentActivity("Expecting EventDashboardActivity!", EventDashboardActivity.class);
	}

	// Log in with an account with no events
	public void testAccountWithNoEvents() {
		login("raymond+0@evbqa.com", "qwert");
		// look for text you have no events...
		assertTrue(s.searchText(s.getString(R.string.no_upcoming_event)));
	}

	// Log in with an account then go to about screen
	public void testAboutScreen() {
		s.clickOnActionBarItem(R.id.menu_info);
		//s.clickOnView(s.getView(R.id.menu_info));
		s.assertCurrentActivity("Expecting about screen!", SettingsActivity.class);
		s.goBack();

		login("raymond+0@evbqa.com", "qwert");
		s.clickOnMenuItem(s.getString(R.string.menu_item_settings));
		s.assertCurrentActivity("Expecting about screen!", SettingsActivity.class);
		s.goBack();
	}

	// Testing the refresh button on the EventListActivity screen
	public void testEventRefreshButton() {
		login("raymond+2@evbqa.com", "qwert");
		s.clickOnActionBarItem(R.id.menu_refresh);
		assertTrue(s.searchText(s.getString(R.string.loading)));

	}

	// Log in with an account with some events and check event details format
	public void testTicketDetailsFormat() {
		login("raymond+2@evbqa.com", "qwert");
		s.clickInList(0);
		// search text TICKETS SOLD
		assertTrue(s.searchText(s.getString(R.string.ticketsold_lb)));
		// search text ATTENDANCE
		assertTrue(s.searchText(s.getString(R.string.attendance_lb)));
		// search #/# number format for tickets and attendance
		assertTrue(s.searchText("[0-9]+\\/[0-9]+"));
		// search percentage format
		assertTrue(s.searchText("^((100)|(\\d{0,2}))%$"));

	}

	// Click an event in events list and check for refresh in details screen
	public void testEventDetailsRefreshAfterSelect() {
		login("raymond+2@evbqa.com", "qwert");
		s.clickInList(0);
		assertTrue(s.searchText(s.getString(R.string.loading)));

	}

	// Check in by clicking on an attendee's name in the attendees list
	public void testManualCheckIn() {
		login("raymond+2@evbqa.com", "qwert");
		goToAttendeesScreen();

		s.clickOnText(attendeeName);
		s.clickOnText(s.getString(R.string.check_in));

		// undo check in
		s.clickOnText(attendeeName);
		assertTrue(s.searchText(s.getString(R.string.undo_check_in), true));
		s.clickOnText(s.getString(R.string.undo_check_in));

		// verify check in undone
		s.clickOnText(attendeeName);
		assertTrue(s.searchText(s.getString(R.string.check_in)));
	}

	// Test reset attendance counter feature
	public void testResetAttendanceCounter() {
		login("raymond+2@evbqa.com", "qwert");
		goToAttendeesScreen();

		s.clickOnText(attendeeName);
		s.clickOnText(s.getString(R.string.check_in));
		assertTrue(s.searchText("1 " + s.getString(R.string.checked_in_on_device)));

		s.clickOnMenuItem(s.getString(R.string.resetcounter_menu));
		s.clickOnButton(s.getString(R.string.cancel));
		assertTrue(s.searchText("1 " + s.getString(R.string.checked_in_on_device)));

		s.clickOnMenuItem(s.getString(R.string.resetcounter_menu));
		s.clickOnButton(s.getString(R.string.yes));
		assertTrue(s.searchText("0 " + s.getString(R.string.checked_in_on_device)));

		// clean up
		s.clickOnText(attendeeName);
		s.clickOnText(s.getString(R.string.undo_check_in));
		s.clickOnMenuItem(s.getString(R.string.resetcounter_menu));
		s.clickOnButton(s.getString(R.string.yes));

	}

	// Enable and disable the search box
	public void testEnableDisableSearchBox() {
		login("raymond+2@evbqa.com", "qwert");
		goToAttendeesScreen();

		goToSearchAttendeeScreen();

		// go back using home icon
		s.hideSoftKeyboard();
		s.goBack();
		assertFalse(s.searchText(s.getString(R.string.searchbox_hint), true));

		goToSearchAttendeeScreen();

		// go back using back button
		s.hideSoftKeyboard();
		s.goBack();
		assertFalse(s.searchText(s.getString(R.string.searchbox_hint), true));

	}

	// Test the search attendee by first name and last name feature
	public void testSearchByFirstOrLastName() {
		login("raymond+2@evbqa.com", "qwert");
		goToAttendeesScreen();

		goToSearchAttendeeScreen();

		s.enterText(0, "Ra");
		s.sleep(2000);
		assertTrue(s.searchText("Tu, Raymond"));

		// click on x button in search bar
		s.clickOnView(s.getView(R.id.clear_searchtxt));
		assertTrue(s.searchText(s.getString(R.string.searchbox_hint)));

		s.enterText(0, "Tu");
		s.sleep(2000);
		assertTrue(s.searchText("Tu, Raymond"));

		// manually clear text in search bar
		s.clearEditText(0);
		assertTrue(s.searchText(s.getString(R.string.searchbox_hint)));

		// enter a name with no record
		s.enterText(0, "MISSINGNO.");
		assertTrue(s.searchText(s.getString(R.string.search_results) + " 0"));

	}

	// Test the limit entry by ticket type feature
	public void testLimitTicketTypeEntry() {
		login("raymond+2@evbqa.com", "qwert");
		goToAttendeesScreen();

		s.clickOnMenuItem(s.getString(R.string.settings_menu));
		assertTrue(s.searchText(s.getString(R.string.settings_desc)));

		// disable following ticket types, only D-1 ticket active
		s.clickOnText(freeTicketTypeName);
		s.clickOnText(paidTicketTypeName);
		s.clickOnText(donateTicketTypeName);
		s.clickOnButton(s.getString(R.string.ok));

		assertTrue(s.searchText(s.getString(R.string.all_ticket_limited_text)));
		assertFalse(s.searchText(freeTicketTypeName, true));
		assertFalse(s.searchText(paidTicketTypeName, true));
		assertFalse(s.searchText(donateTicketTypeName, true));

		// enable F-1 ticket
		s.clickOnMenuItem(s.getString(R.string.settings_menu));
		s.clickOnText(freeTicketTypeName);
		s.clickOnButton(s.getString(R.string.ok));

		assertFalse(s.searchText(paidTicketTypeName, true));
		assertFalse(s.searchText(donateTicketTypeName, true));

		// enable P-1 ticket
		s.clickOnMenuItem(s.getString(R.string.settings_menu));
		s.clickOnText(paidTicketTypeName);
		s.clickOnButton(s.getString(R.string.ok));

		assertFalse(s.searchText(donateTicketTypeName, true));
	}

	// Check for repeating event
	public void testRepeatingEvents() {
		login("raymond+2@evbqa.com", "qwert");
		ExpandableListView l = (ExpandableListView) s.getView(R.id.event_list_view);
		int unexpandedListCount = l.getAdapter().getCount();

		s.clickOnText(s.getString(R.string.repeating));
		// list should now be expanded
		ExpandableListView le = (ExpandableListView) s.getView(R.id.event_list_view);
		assertNotNull("No list views!", le);
		assertTrue("ExpandableListView not expanded!", le.getAdapter().getCount() > unexpandedListCount);
	}

	// Log in and go to event with zero attendees (the Checkin button should be
	// absent)
	public void testEventNoAttendee() {
		login("raymond+2@evbqa.com", "qwert");
		s.clickOnText("TestNoAttendeeEvent");
		assertTrue(s.waitForText("0%"));
		assertTrue(s.waitForText("0/0"));
		assertFalse(s.searchButton(s.getString(R.string.check_in), true));
	}

	public void testEventListDateTimeFormat() {
		login("raymond+2@evbqa.com", "qwert");
		// test date on regular event
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
			Date d = sdf.parse("2020-01-01 19");
			String expectedDateStr = EBDateUtils.buildDateAtHourString(d, s.getCurrentActivity().getApplicationContext());
			assertTrue(s.searchText(expectedDateStr));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// test date on repeating event
		s.clickOnText(s.getString(R.string.repeating));
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
			Date d = sdf.parse("2020-11-11 19");
			String expectedDateStr = EBDateUtils.buildDateAtHourString(d, s.getCurrentActivity().getApplicationContext());
			assertTrue(s.searchText(expectedDateStr));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void testSameDayEventDateTimeFormat() {
		login("raymond+2@evbqa.com", "qwert");
		s.clickOnText("TestRegularEvent");
		assertTrue(s.searchText("Wednesday, January 1, 2020") && s.searchText("from 7:00 PM to 10:00 PM"));
	}

	public void testOvernightEventDateTimeFormat() {
		login("raymond+2@evbqa.com", "qwert");
		s.clickOnText("TestOvernightEvent");
		assertTrue(s.searchText("Sunday, February 2, 2020") && s.searchText("from 11:00 PM to 2:00 AM"));
	}

	public void testMultiDayEventDateTimeFormat() {
		login("raymond+2@evbqa.com", "qwert");
		s.clickOnText("TestMultiDayEvent");
		assertTrue(s.searchText("from Tuesday, March 3, 2020 7:00 PM to") && s.searchText("Friday, March 6, 2020 10:00 PM"));
	}

	public void testNewYearsEveEventDateTimeFormat() {
		login("raymond+2@evbqa.com", "qwert");
		s.scrollToBottom();
		s.clickOnText("TestNewYearsEveEvent", 0, true);
		assertTrue(s.searchText("Thursday, December 31, 2020") && s.searchText("from 11:00 PM to 2:00 AM"));
	}

	// Toggle between Scan screen and go to Attendee list
	public void testToggleBarcodeScanAndAttendeeScreen() {
		login("raymond+2@evbqa.com", "qwert");
		goToAttendeesScreen();

		// click on barcode button
		goToBarcodeScanScreen();

		// click on go back button
		s.goBack();
		assertTrue("Not in AttendeeListActivity!", s.waitForActivity(AttendeeListActivity.class));

		goToBarcodeScanScreen();

		// click on list icon
		EBCaptureActivity c = (EBCaptureActivity) s.getCurrentActivity();
		c.onClickList(null);
		assertTrue("Not in AttendeeListActivity!", s.waitForActivity(AttendeeListActivity.class));

		goToSearchAttendeeScreen();
		goToBarcodeScanScreen();

		s.goBack();
	}

	public void testBarcodeScanFlashLight() {
		login("raymond+2@evbqa.com", "qwert");
		goToAttendeesScreen();
		goToBarcodeScanScreen();


		s.clickOnText(s.getString(R.string.flash_off));
		assertTrue(s.searchText(s.getString(R.string.flash_on), true));

		s.clickOnText(s.getString(R.string.flash_on));
		assertTrue(s.searchText(s.getString(R.string.flash_off), true));

		s.goBack();
	}
}

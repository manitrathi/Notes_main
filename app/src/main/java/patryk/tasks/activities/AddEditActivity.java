package patryk.tasks.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.allyants.notifyme.NotifyMe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import patryk.tasks.R;

public class AddEditActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "package patryk.notes.EXTRA_ID";
    public static final String EXTRA_TEXT = "package patryk.notes.EXTRA_TEXT";
    public static final String EXTRA_DATE = "package patryk.notes.EXTRA_DATE";
    public static final String EXTRA_PRIORITY = "package patryk.notes.EXTRA_PRIORITY";
    private static final Calendar calendar = Calendar.getInstance();
    public static boolean IS_ADD_ACTIVITY;
    private static Intent intent = new Intent();
    private static Date date;
    private static DatePickerDialogFragment datePickerDialogFragment;
    private static TimePickerDialogFragment timePickerDialogFragment;
    private static int pickedYear, pickedMonth, pickedDay, pickedHour, pickedMinute;
    private EditText userInput;
    private NumberPicker priority;
    private Intent incomingIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_activity);

        userInput = findViewById(R.id.editText);
        priority = findViewById(R.id.numberPicker);
        priority.setMinValue(1);
        priority.setMaxValue(10);

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close);

        datePickerDialogFragment = new DatePickerDialogFragment();
        datePickerDialogFragment.setCancelable(false);
        timePickerDialogFragment = new TimePickerDialogFragment();
        timePickerDialogFragment.setCancelable(false);

        incomingIntent = getIntent();

        if (incomingIntent.hasExtra(EXTRA_ID)) {
            setTitle(R.string.activity_edit_title);
            IS_ADD_ACTIVITY = false;
            userInput.setText(incomingIntent.getStringExtra(EXTRA_TEXT));
            priority.setValue(incomingIntent.getIntExtra(EXTRA_PRIORITY, 1));
            date = (Date) incomingIntent.getSerializableExtra(EXTRA_DATE);
            calendar.setTime(date);
        } else {
            setTitle(R.string.activity_add_title);
            IS_ADD_ACTIVITY = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_add_note, menu);

        if (IS_ADD_ACTIVITY) {
            menu.findItem(R.id.menu_share).setVisible(false);
            menu.findItem(R.id.reminder).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveNote();
                return true;
            case R.id.reminder:
                setNotification();
                return true;
            case android.R.id.home:
                showSaveProgressPrompt();
                return true;
            case R.id.menu_share:
                shareNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveNote() {
        String text = userInput.getText().toString();
        int prio = priority.getValue();

        if (text.trim().isEmpty()) {
            userInput.setError(getResources().getString(R.string.empty_edittext));
            return;
        }

        intent.putExtra(EXTRA_TEXT, text);
        intent.putExtra(EXTRA_PRIORITY, prio);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            intent.putExtra(EXTRA_ID, id);
        }

        // Pick date for the note
        datePickerDialogFragment.setFlag(DatePickerDialogFragment.FLAG_SAVE_NOTE);
        datePickerDialogFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void setNotification() {
        String text = userInput.getText().toString();
        if (text.trim().isEmpty()) {
            userInput.setError(getResources().getString(R.string.empty_edittext));
        } else {
            // Pick date for notification
            datePickerDialogFragment.setFlag(DatePickerDialogFragment.FLAG_SET_REMINDER);
            datePickerDialogFragment.show(getSupportFragmentManager(), "datePickerForReminder");
            timePickerDialogFragment.setNotificationContent(text);
        }
    }

    private void shareNote() {
        Intent sendIntent = new Intent();
        String dataToSend = incomingIntent.getStringExtra(EXTRA_TEXT);
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, dataToSend);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, getString(R.string.share_note_string) + dataToSend);
        startActivity(shareIntent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showSaveProgressPrompt();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private AlertDialog.Builder getBuilder() {
        return new AlertDialog.Builder(this)
                .setTitle(getString(R.string.alert_save_progress_prompt_title_unsaved_progress))
                .setCancelable(false);
    }

    private void showSaveProgressPrompt() {
        String note = userInput.getText().toString();
        if (!note.isEmpty()) {
            if (!note.equals(incomingIntent.getStringExtra(EXTRA_TEXT))) {
                AlertDialog alertDialog = getBuilder()
                        .setMessage(getString(R.string.save_progress_prompt_mesage))
                        .setPositiveButton(getString(R.string.save_progress_prompt_button_save), (dialog, whichButton) -> {
                            saveNote();
                        })
                        .setNegativeButton(getString(R.string.save_progress_prompt_button_discard), (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        })
                        .setNeutralButton(getString(R.string.save_progress_prompt_button_cancel), (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .create();
                alertDialog.show();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    public static class DatePickerDialogFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        private static final int FLAG_SAVE_NOTE = 0;
        private static final int FLAG_SET_REMINDER = 1;

        private int flag = 0;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            if (flag == FLAG_SAVE_NOTE) {
                if (AddEditActivity.IS_ADD_ACTIVITY) {
                    Calendar calendar = Calendar.getInstance();
                    pickedYear = calendar.get(Calendar.YEAR);
                    pickedMonth = calendar.get(Calendar.MONTH);
                    pickedDay = calendar.get(Calendar.DAY_OF_MONTH);
                } else {
                    pickedYear = calendar.get(Calendar.YEAR);
                    pickedMonth = calendar.get(Calendar.MONTH);
                    pickedDay = calendar.get(Calendar.DAY_OF_MONTH);
                }
            } else if (flag == FLAG_SET_REMINDER) {
                pickedYear = calendar.get(Calendar.YEAR);
                pickedMonth = calendar.get(Calendar.MONTH);
                pickedDay = calendar.get(Calendar.DAY_OF_MONTH);
            }

            return new DatePickerDialog(getActivity(), this, pickedYear, pickedMonth, pickedDay);
        }

        private void setFlag(int i) {
            flag = i;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            pickedYear = year;
            pickedMonth = monthOfYear;
            pickedDay = dayOfMonth;

            if (flag == FLAG_SAVE_NOTE) {
                timePickerDialogFragment.setFlag(DatePickerDialogFragment.FLAG_SAVE_NOTE);
                timePickerDialogFragment.show(getFragmentManager(), "timePicker");
            } else if (flag == FLAG_SET_REMINDER) {
                timePickerDialogFragment.setFlag(DatePickerDialogFragment.FLAG_SET_REMINDER);
                timePickerDialogFragment.show(getFragmentManager(), "timePicker");
            }
        }
    }

    public static class TimePickerDialogFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {
        private static final int FLAG_SAVE_NOTE = 0;
        private static final int FLAG_SET_REMINDER = 1;
        private String notificationContent;

        private int flag = 0;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            if (flag == FLAG_SAVE_NOTE) {
                if (AddEditActivity.IS_ADD_ACTIVITY) {
                    Calendar calendar = Calendar.getInstance();
                    pickedHour = calendar.get(Calendar.HOUR_OF_DAY);
                    pickedMinute = calendar.get(Calendar.MINUTE);
                } else {
                    pickedHour = calendar.get(Calendar.HOUR_OF_DAY);
                    pickedMinute = calendar.get(Calendar.MINUTE);
                }
            } else if (flag == FLAG_SET_REMINDER) {
                pickedHour = calendar.get(Calendar.HOUR_OF_DAY);
                pickedMinute = calendar.get(Calendar.MINUTE);
            }

            return new TimePickerDialog(getActivity(), this, pickedHour, pickedMinute, android.text.format.DateFormat.is24HourFormat(getContext()));
        }

        private void setFlag(int i) {
            flag = i;
        }

        private void setNotificationContent(String content) {
            this.notificationContent = content;
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            calendar.set(pickedYear, pickedMonth, pickedDay, hourOfDay, minute);

            // If menu icon 'SAVE' was clicked
            if (flag == FLAG_SAVE_NOTE) {
                // Set selected date and time as a Date variable
                date = calendar.getTime();

                // Put it as extra into intent
                intent.putExtra(EXTRA_DATE, date);

                // Everything already set up so send result to another activity
                Objects.requireNonNull(getActivity()).setResult(RESULT_OK, intent);

                // And finish AddEditActivity
                getActivity().finish();
            }
            // If menu icon 'REMINDER' was clicked
            else if (flag == FLAG_SET_REMINDER) {

                // Date format to display selected date in a toast
                // previous method to set date format:
                // new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
                // Use for example: getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
                // To format locale date and time
                DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();

                // Set a new reminder for selected note using NotifyMe
                new NotifyMe.Builder(getContext())
                        .title(R.string.reminder_title)
                        .content(notificationContent)
                        .time(calendar)
                        .color(255, 0, 0, 255)
                        .led_color(255, 255, 255, 255)
                        .large_icon(R.drawable.ic_notification)
                        .small_icon(R.drawable.ic_notification)
                        .rrule("FREQ=MINUTELY;INTERVAL=5;COUNT=1") // COUNT - how many notifications will be set up
                        .addAction(new Intent(), getContext().getString(R.string.notification_close_button_text), true, false)
                        .addAction(new Intent(getContext(), MainActivity.class), getContext().getString(R.string.notification_open_button_text))
                        .build();

                Toasty.success(Objects.requireNonNull(getContext()), getContext().getString(R.string.notification_setup_string) + dateFormat.format(calendar.getTime()), Toasty.LENGTH_LONG).show();
                // Finish an activity
                Objects.requireNonNull(getActivity()).finish();
            }
        }
    }
}

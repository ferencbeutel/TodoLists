package beutel.ferenc.de.todolists.todo.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TimePicker;
import beutel.ferenc.de.todolists.R;

import java.time.LocalTime;

import static java.lang.Integer.parseInt;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private static final String DELIMITER = ":";

    public static LocalTime parseIntoLocalTime(final String toParse) {
        final String[] timeParts = toParse.split(DELIMITER);
        return LocalTime.of(parseInt(timeParts[0]), parseInt(timeParts[1]));
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final LocalTime now = LocalTime.now();

        return new TimePickerDialog(getActivity(), this, now.getHour(), now.getMinute(), DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        final Button dueTimeButton = getActivity().findViewById(R.id.due_time_button);
        String newButtonText = hourOfDay + DELIMITER + minute;
        dueTimeButton.setText(newButtonText);
    }
}

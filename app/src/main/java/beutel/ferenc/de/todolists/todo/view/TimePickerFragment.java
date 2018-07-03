package beutel.ferenc.de.todolists.todo.view;

import static java.lang.Integer.parseInt;

import java.time.LocalTime;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TimePicker;
import beutel.ferenc.de.todolists.R.id;

public class TimePickerFragment extends DialogFragment implements OnTimeSetListener {

  private static final String DELIMITER = ":";

  static LocalTime parseIntoLocalTime(final String toParse) {
    final String[] timeParts = toParse.split(DELIMITER);
    return LocalTime.of(parseInt(timeParts[0]), parseInt(timeParts[1]));
  }

  @Override
  public Dialog onCreateDialog(final Bundle savedInstanceState) {
    final LocalTime now = LocalTime.now();

    return new TimePickerDialog(getActivity(), this, now.getHour(), now.getMinute(), DateFormat.is24HourFormat(getActivity()));
  }

  @Override
  public void onTimeSet(final TimePicker view, final int hourOfDay, final int minute) {
    final Button dueTimeButton = getActivity().findViewById(id.due_time_button);
    final String newButtonText = hourOfDay + DELIMITER + minute;
    dueTimeButton.setText(newButtonText);
  }
}

package beutel.ferenc.de.todolists.todo.view;

import static java.lang.Integer.parseInt;

import java.time.LocalDate;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import beutel.ferenc.de.todolists.R.id;

public class DatePickerFragment extends DialogFragment implements OnDateSetListener {

  private static final String DELIMITER = ".";

     static LocalDate parseIntoLocalDate(final String toParse) {
        final String[] dateParts = toParse.split("\\" + DELIMITER);
        return LocalDate.of(parseInt(dateParts[2]), parseInt(dateParts[1]), parseInt(dateParts[0]));
    }

    static String getButtonText(final int year, final int month, final int dayOfMonth) {
        return dayOfMonth + DELIMITER + month + DELIMITER + year;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final LocalDate today = LocalDate.now();

    return new DatePickerDialog(getActivity(), this, today.getYear(), today.getMonthValue(), today.getDayOfMonth());
  }

  @Override
  public void onDateSet(final DatePicker view, final int year, final int month, final int dayOfMonth) {
    final Button dueDateButton = getActivity().findViewById(id.due_date_button);
    final String newButtonText = DatePickerFragment.getButtonText(year, month, dayOfMonth);
    dueDateButton.setText(newButtonText);
  }
}

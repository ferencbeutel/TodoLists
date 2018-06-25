package beutel.ferenc.de.todolists.todo.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import beutel.ferenc.de.todolists.R;

import java.time.LocalDate;

import static java.lang.Integer.parseInt;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static final String DELIMITER = ".";

    public static LocalDate parseIntoLocalDate(final String toParse) {
        final String[] dateParts = toParse.split("\\" + DELIMITER);
        return LocalDate.of(parseInt(dateParts[2]), parseInt(dateParts[1]), parseInt(dateParts[0]));
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final LocalDate today = LocalDate.now();

        return new DatePickerDialog(getActivity(), this, today.getYear(), today.getMonthValue(), today.getDayOfMonth());
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        final Button dueDateButton = getActivity().findViewById(R.id.due_date_button);
        String newButtonText = dayOfMonth + DELIMITER + month + DELIMITER + year;
        dueDateButton.setText(newButtonText);

    }
}

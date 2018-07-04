package beutel.ferenc.de.todolists.todo.view;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import beutel.ferenc.de.todolists.R;
import beutel.ferenc.de.todolists.todo.domain.TodoRepository;

public abstract class AbstractCreateEditActivity extends AppCompatActivity {

    TodoRepository todoRepository;

    EditText titleInput;
    EditText descriptionInput;
    ToggleButton favouriteButton;

    Button dueDateButton;
    Button dueTimeButton;

    TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit);

        todoRepository = new TodoRepository(this);


        titleInput = findViewById(R.id.title_input);
        descriptionInput = findViewById(R.id.description_input);
        favouriteButton = findViewById(R.id.favourite_button);

        dueDateButton = findViewById(R.id.due_date_button);
        dueTimeButton = findViewById(R.id.due_time_button);

        errorMessage = findViewById(R.id.creation_errors);

        favouriteButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_star_border_black_24dp));
        favouriteButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                favouriteButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_star_black_24dp));
            else
                favouriteButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_star_border_black_24dp));
        });
    }

    public void onDatePickButtonClick(final View view) {
        final DialogFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getFragmentManager(), "Date Picker");
    }

    public void onTimePickButtonClick(final View view) {
        final DialogFragment timePickFragment = new TimePickerFragment();
        timePickFragment.show(getFragmentManager(), "Date Picker");
    }

    public abstract void onCreationButtonClick(final View view);
}

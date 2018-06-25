package beutel.ferenc.de.todolists.todo.view;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import beutel.ferenc.de.todolists.R;
import beutel.ferenc.de.todolists.todo.domain.Todo;
import beutel.ferenc.de.todolists.todo.domain.TodoRepository;
import beutel.ferenc.de.todolists.todolist.view.TodoListActivity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static beutel.ferenc.de.todolists.todo.view.DatePickerFragment.parseIntoLocalDate;
import static beutel.ferenc.de.todolists.todo.view.TimePickerFragment.parseIntoLocalTime;

public class CreationActivity extends AppCompatActivity {

    static final String TODO_ID_KEY = "todoId";

    private TodoRepository todoRepository;

    private EditText titleInput;
    private EditText descriptionInput;
    private ToggleButton favouriteButton;

    private Button dueDateButton;
    private Button dueTimeButton;

    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);

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

    public void onCreationButtonClick(final View view) {
        final String descriptionText = descriptionInput.getText().toString();
        final String titleText = titleInput.getText().toString();

        LocalDateTime localDateTime;
        try {
            localDateTime = LocalDateTime.of(parseIntoLocalDate(dueDateButton.getText().toString()), parseIntoLocalTime(dueTimeButton.getText().toString()));
        } catch (Exception exception) {
            Log.d("Creation", "error", exception);
            localDateTime = null;
        }

        final Todo todoToCreate = Todo.builder()
                .title(titleText)
                .description(descriptionText)
                .dueDateTime(localDateTime)
                .favorite(favouriteButton.isChecked())
                .completed(false)
                .contactIds(new ArrayList<>())
                .build();

        if (!todoToCreate.isValid()) {
            final List<String> errors = todoToCreate.errorMessages();
            String errorMessage = "Error during Todo creation. " + String.join(", ", errors);
            this.errorMessage.setText(errorMessage);

            return;
        }

        todoRepository.insert(todoToCreate);

        final Intent todoListIntent = new Intent(this, TodoListActivity.class);
        startActivity(todoListIntent);
    }
}

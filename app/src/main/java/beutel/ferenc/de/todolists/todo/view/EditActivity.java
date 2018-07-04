package beutel.ferenc.de.todolists.todo.view;

import static beutel.ferenc.de.todolists.todo.view.DatePickerFragment.parseIntoLocalDate;
import static beutel.ferenc.de.todolists.todo.view.TimePickerFragment.parseIntoLocalTime;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import beutel.ferenc.de.todolists.R;
import beutel.ferenc.de.todolists.todo.domain.Todo;
import beutel.ferenc.de.todolists.todolist.view.TodoListActivity;

public class EditActivity extends AbstractCreateEditActivity {

  static final String TODO_ID_KEY = "todoId";

  private TextView hiddenId;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    hiddenId = findViewById(R.id.create_hidden_id);

    final Bundle extras = getIntent().getExtras();
    if (extras == null) {
      return;
    }

    final String savedTodoId = extras.getString(TODO_ID_KEY);
    if (savedTodoId == null || savedTodoId.isEmpty()) {
      return;
    }

    final Optional<Todo> savedTodo = todoRepository.findById(String.valueOf(savedTodoId));
    savedTodo.ifPresent(this::prefilInputs);
  }

  @Override
  public void onSaveButtonClicked(final View view) {
    final String descriptionText = descriptionInput.getText().toString();
    final String titleText = titleInput.getText().toString();

    LocalDateTime localDateTime;
    try {
      localDateTime = LocalDateTime.of(parseIntoLocalDate(dueDateButton.getText().toString()),
        parseIntoLocalTime(dueTimeButton.getText().toString()));
    } catch (final Exception exception) {
      Log.d("Creation", "error", exception);
      localDateTime = null;
    }

    final Todo todoToUpdate = Todo.builder()
      ._id(hiddenId.getText().toString())
      .title(titleText)
      .description(descriptionText)
      .dueDateTime(localDateTime)
      .favorite(favouriteButton.isChecked())
      .completed(false)
      .contactIds(new HashSet<>())
      .build();

    if (todoToUpdate.isNotValid()) {
      final List<String> errors = todoToUpdate.errorMessages();
      final String errorMessage = "Error during Todo update. " + String.join(", ", errors);
      this.errorMessage.setText(errorMessage);

      return;
    }

    todoRepository.update(todoToUpdate);

    final Intent todoListIntent = new Intent(this, TodoListActivity.class);
    startActivity(todoListIntent);
  }

  private void prefilInputs(final Todo savedTodo) {
    hiddenId.setText(savedTodo.get_id());
    titleInput.setText(savedTodo.getTitle());
    descriptionInput.setText(savedTodo.getDescription());
    favouriteButton.setChecked(savedTodo.isFavorite());
    final LocalDateTime savedDueDateTime = savedTodo.getDueDateTime();
    dueDateButton.setText(DatePickerFragment.getButtonText(savedDueDateTime.getYear(), savedDueDateTime.getMonthValue(),
      savedDueDateTime.getDayOfMonth()));
    dueTimeButton.setText(TimePickerFragment.getButtonText(savedDueDateTime.getHour(), savedDueDateTime.getMinute()));
  }
}

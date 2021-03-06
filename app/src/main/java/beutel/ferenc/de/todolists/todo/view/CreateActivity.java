package beutel.ferenc.de.todolists.todo.view;

import static beutel.ferenc.de.todolists.todo.view.DatePickerFragment.parseIntoLocalDate;
import static beutel.ferenc.de.todolists.todo.view.TimePickerFragment.parseIntoLocalTime;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import beutel.ferenc.de.todolists.todo.domain.Todo;
import beutel.ferenc.de.todolists.todolist.view.TodoListActivity;

public class CreateActivity extends AbstractCreateEditActivity {

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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

    final Todo todoToCreate = Todo.builder()
      .title(titleText)
      .description(descriptionText)
      .dueDateTime(localDateTime)
      .favorite(favouriteButton.isChecked())
      .completed(false)
      .contactIds(new HashSet<>())
      .build();

    if (todoToCreate.isNotValid()) {
      final List<String> errors = todoToCreate.errorMessages();
      final String errorMessage = "Error during Todo creation. " + String.join(", ", errors);
      this.errorMessage.setText(errorMessage);

      return;
    }

    todoRepository.insert(todoToCreate);

    final Intent todoListIntent = new Intent(this, TodoListActivity.class);
    startActivity(todoListIntent);
  }
}

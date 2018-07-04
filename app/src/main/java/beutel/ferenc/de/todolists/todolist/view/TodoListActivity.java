package beutel.ferenc.de.todolists.todolist.view;

import static java.util.Optional.ofNullable;

import static beutel.ferenc.de.todolists.todo.view.DetailActivity.TODO_ID_KEY;

import java.time.LocalDateTime;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import beutel.ferenc.de.todolists.R.id;
import beutel.ferenc.de.todolists.R.layout;
import beutel.ferenc.de.todolists.R.menu;
import beutel.ferenc.de.todolists.common.domain.DBHelper;
import beutel.ferenc.de.todolists.todo.domain.TodoRepository;
import beutel.ferenc.de.todolists.todo.view.CreateActivity;
import beutel.ferenc.de.todolists.todo.view.DetailActivity;
import beutel.ferenc.de.todolists.todo.view.TodoArrayAdapter;
import beutel.ferenc.de.todolists.todolist.domain.SortOrder;

public class TodoListActivity extends AppCompatActivity {

  private TodoRepository todoRepository;
  private ListView todoListView;

  private SortOrder currentSortOrder;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(layout.activity_list);

    todoListView = findViewById(id.list_todo);
    todoRepository = new TodoRepository(this);

    currentSortOrder = SortOrder.IMPORTANCE;

    updateUI();

    final TextView backendNotAvailableErrorView = findViewById(id.backend_not_available_error);
    if (!DBHelper.NETWORK_REACHABLE) {
      backendNotAvailableErrorView.setVisibility(View.VISIBLE);
    }

    updateUI();
  }

  @Override
  public boolean onCreateOptionsMenu(final Menu menuView) {
    getMenuInflater().inflate(menu.main_menu, menuView);
    return super.onCreateOptionsMenu(menuView);
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    switch (item.getItemId()) {
      case id.action_add_todo:
        final Intent creationIntent = new Intent(this, CreateActivity.class);
        startActivity(creationIntent);

        return true;

      case id.action_change_todo_sort:
        flipSortOrder();
        item.setIcon(flipIcon(item.getIcon()));
        updateUI();

        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void onTodoClick(final View clickedView) {
    ofNullable((TextView) clickedView.findViewById(id.todo_id)).ifPresent(view -> {
      final String todoId = view.getText().toString();
      if (!todoId.isEmpty()) {
        todoRepository.findById(todoId).ifPresent(todo -> {

          final Intent detailIntent = new Intent(this, DetailActivity.class);
          detailIntent.putExtra(TODO_ID_KEY, todoId);
          startActivity(detailIntent);
        });
      }
    });
  }

  public void onDeleteButtonClick(final View clickedView) {
    ofNullable((TextView) ((View) clickedView.getParent()).findViewById(id.todo_id)).ifPresent(view -> {
      final String todoId = view.getText().toString();
      if (!todoId.isEmpty()) {
        todoRepository.findById(todoId).ifPresent(todo -> {
          if (todo.isCompleted()) {
            todoRepository.deleteById(todoId);
          } else {
            todoRepository.update(todo.toBuilder().completed(true).completionDateTime(LocalDateTime.now()).build());
          }
        });
      }
    });

    updateUI();
  }

  private void updateUI() {
    todoListView.setAdapter(new TodoArrayAdapter(this, todoRepository.findAllOrderedBy(currentSortOrder)));
  }

  private Drawable flipIcon(final Drawable originalIcon) {
    final Matrix matrix = new Matrix();
    matrix.preScale(1, -1);

    final Bitmap oldBitmap = ((BitmapDrawable) originalIcon).getBitmap();

    final Bitmap flippedIcon = Bitmap.createBitmap(oldBitmap, 0, 0, oldBitmap.getWidth(), oldBitmap.getHeight(), matrix, true);

    return new BitmapDrawable(getResources(), flippedIcon);
  }

  private void flipSortOrder() {
    if (currentSortOrder == SortOrder.IMPORTANCE) {
      currentSortOrder = SortOrder.DATE;
    } else {
      currentSortOrder = SortOrder.IMPORTANCE;
    }
  }
}

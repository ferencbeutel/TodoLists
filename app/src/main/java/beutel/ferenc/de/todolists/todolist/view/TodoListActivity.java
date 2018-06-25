package beutel.ferenc.de.todolists.todolist.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import beutel.ferenc.de.todolists.todo.view.CreationActivity;
import beutel.ferenc.de.todolists.todo.view.DetailActivity;
import beutel.ferenc.de.todolists.R;
import beutel.ferenc.de.todolists.todo.domain.Todo;
import beutel.ferenc.de.todolists.todo.view.TodoArrayAdapter;
import beutel.ferenc.de.todolists.todo.domain.TodoRepository;

import static beutel.ferenc.de.todolists.todo.view.DetailActivity.*;
import static java.util.Optional.ofNullable;

public class TodoListActivity extends AppCompatActivity {

    private TodoRepository todoRepository;
    private ListView todoListView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        todoListView = findViewById(R.id.list_todo);
        todoRepository = new TodoRepository(this);

        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_todo:
                final Intent creationIntent = new Intent(this, CreationActivity.class);
                startActivity(creationIntent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onTodoClick(final View clickedView) {
        ofNullable((TextView) ((View) clickedView.getParent()).findViewById(R.id.todo_id))
                .ifPresent((TextView view) -> {
                    String todoId = view.getText().toString();
                    if (!todoId.isEmpty()) {
                        todoRepository.findById(todoId).ifPresent((todo) -> {
                            if (!todo.isCompleted()) {
                                final Intent detailIntent = new Intent(this, DetailActivity.class);
                                detailIntent.putExtra(TODO_ID_KEY, todoId);
                                startActivity(detailIntent);
                            }
                        });
                    }
                });
    }

    public void onDeleteButtonClick(final View clickedView) {
        ofNullable((TextView) ((View) clickedView.getParent()).findViewById(R.id.todo_id))
                .ifPresent((TextView view) -> {
                    final String todoId = view.getText().toString();
                    if (!todoId.isEmpty()) {
                        todoRepository.findById(todoId).ifPresent((Todo todo) -> {
                            if (todo.isCompleted()) {
                                todoRepository.deleteById(todoId);
                            } else {
                                todoRepository.update(todo.toBuilder()
                                        .completed(true)
                                        .build());
                            }
                        });
                    }
                });

        updateUI();
    }

    private void updateUI() {
        todoListView.setAdapter(new TodoArrayAdapter(this, todoRepository.findAllOrderedByPriority()));
    }
}

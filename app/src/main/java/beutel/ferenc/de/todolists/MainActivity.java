package beutel.ferenc.de.todolists;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import beutel.ferenc.de.todolists.todo.Todo;
import beutel.ferenc.de.todolists.todo.TodoContract;
import beutel.ferenc.de.todolists.todo.TodoDbHelper;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity {

    private TodoDbHelper todoDbHelper;
    private ListView todoListView;
    private ArrayAdapter<String> todoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        todoListView = findViewById(R.id.list_todo);
        todoDbHelper = new TodoDbHelper(this);

        updateUi();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add a new task")
                        .setMessage("What do you want to do next?")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final Todo todoToCreate = new Todo(String.valueOf(taskEditText.getText()));

                                SQLiteDatabase db = todoDbHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(TodoContract.TodoEntry.COL_TODO_TITLE, todoToCreate.getTitle());
                                db.insertWithOnConflict(TodoContract.TodoEntry.TABLE,
                                        null,
                                        values,
                                        SQLiteDatabase.CONFLICT_REPLACE);
                                db.close();
                                updateUi();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUi() {
        final List<String> storedTodos = new ArrayList<>();
        final SQLiteDatabase db = todoDbHelper.getReadableDatabase();

        final Cursor findAllCursor = db.query(
                TodoContract.TodoEntry.TABLE,
                new String[]{TodoContract.TodoEntry._ID, TodoContract.TodoEntry.COL_TODO_TITLE},
                null, null, null, null, null);
        while (findAllCursor.moveToNext()) {
            storedTodos.add(findAllCursor.getString(findAllCursor.getColumnIndex(TodoContract.TodoEntry.COL_TODO_TITLE)));
        }

        findAllCursor.close();
        db.close();

        if (todoListAdapter == null) {
            todoListAdapter = new ArrayAdapter<>(this, R.layout.item_todo, R.id.todo_title);
            todoListAdapter.addAll(storedTodos);
            todoListView.setAdapter(todoListAdapter);
        } else {
            todoListAdapter.clear();
            todoListAdapter.addAll(storedTodos);
            todoListAdapter.notifyDataSetChanged();
        }
    }
}

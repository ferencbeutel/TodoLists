package beutel.ferenc.de.todolists.todo.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import beutel.ferenc.de.todolists.R;
import beutel.ferenc.de.todolists.contact.domain.ContactContract;
import beutel.ferenc.de.todolists.contact.domain.ContactRepository;
import beutel.ferenc.de.todolists.contact.view.ContactArrayAdapter;
import beutel.ferenc.de.todolists.todo.domain.Todo;
import beutel.ferenc.de.todolists.todo.domain.TodoRepository;
import beutel.ferenc.de.todolists.todolist.view.TodoListActivity;

import java.util.List;
import java.util.Optional;

import static android.Manifest.*;
import static android.content.pm.PackageManager.*;

public class DetailActivity extends AppCompatActivity {

    public static final String TODO_ID_KEY = "todoId";

    private static final int PICK_CONTACT_REQUEST_CODE = 4711;

    private ContactRepository contactRepository;
    private TodoRepository todoRepository;

    private Todo todo;

    private TextView titleTextView;
    private ListView contactsListView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        contactRepository = new ContactRepository(this);
        todoRepository = new TodoRepository(this);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            initTodoListActivity();
            return;
        }

        String todoId = extras.getString(TODO_ID_KEY);
        if (todoId == null) {
            initTodoListActivity();
            return;
        }

        Optional<Todo> todoOptional = todoRepository.findById(todoId);
        if (!todoOptional.isPresent()) {
            initTodoListActivity();
            return;
        }

        this.todo = todoOptional.get();

        titleTextView = findViewById(R.id.detail_title);
        contactsListView = findViewById(R.id.detail_contacts_list);

        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_todo:
                final Intent editActivityIntent = new Intent(this, CreationActivity.class);
                editActivityIntent.putExtra(CreationActivity.TODO_ID_KEY, todo.get_id());
                startActivity(editActivityIntent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_CONTACT_REQUEST_CODE:
                final Integer contactId = contactRepository.insertByUri(data.getData());
                if (contactId != null) {
                    final List<String> newContactIds = todo.getContactIds();
                    newContactIds.add(String.valueOf(contactId));
                    todoRepository.update(todo.toBuilder()
                            .contactIds(newContactIds)
                            .build());
                }

                updateUI();
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onAddContactClick(View view) {
        if (ContextCompat.checkSelfPermission(this, permission.READ_CONTACTS) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission.READ_CONTACTS}, 0);
            return;
        }
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST_CODE);
    }

    private void updateUI() {
        titleTextView.setText(todo.getTitle());
        contactsListView.setAdapter(new ContactArrayAdapter(this, contactRepository.findForTodo(todo)));
    }

    private void initTodoListActivity() {
        final Intent todoListIntent = new Intent(this, TodoListActivity.class);
        startActivity(todoListIntent);
    }
}
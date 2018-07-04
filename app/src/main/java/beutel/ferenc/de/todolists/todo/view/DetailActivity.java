package beutel.ferenc.de.todolists.todo.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import beutel.ferenc.de.todolists.R;
import beutel.ferenc.de.todolists.contact.domain.ContactRepository;
import beutel.ferenc.de.todolists.contact.view.ContactArrayAdapter;
import beutel.ferenc.de.todolists.todo.domain.Todo;
import beutel.ferenc.de.todolists.todo.domain.TodoRepository;
import beutel.ferenc.de.todolists.todolist.view.TodoListActivity;

import java.util.Optional;
import java.util.Set;

import static android.Manifest.permission;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static java.util.Optional.ofNullable;

public class DetailActivity extends AppCompatActivity {

  public static final String TODO_ID_KEY = "todoId";

  private static final int PICK_CONTACT_REQUEST_CODE = 4711;

  private ContactRepository contactRepository;
  private TodoRepository todoRepository;

  private Todo todo;

  private TextView titleTextView;
  private TextView descriptionTextView;
    private TextView dueDateTimeTextView;
    private TextView completionDateTimeTextView;
    private ImageView favouriteIconImageView;
    private ListView contactsListView;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
    contactRepository = new ContactRepository(this);
    todoRepository = new TodoRepository(this);

    final Bundle extras = getIntent().getExtras();
    if (extras == null) {
      initTodoListActivity();
      return;
    }

    final String todoId = extras.getString(TODO_ID_KEY);
    if (todoId == null) {
      initTodoListActivity();
      return;
    }

    final Optional<Todo> todoOptional = todoRepository.findById(todoId);
    if (!todoOptional.isPresent()) {
      initTodoListActivity();
      return;
    }

    todo = todoOptional.get();

        titleTextView = findViewById(R.id.detail_title);
        descriptionTextView = findViewById(R.id.detail_description);
        dueDateTimeTextView = findViewById(R.id.detail_dueDateTime);
        completionDateTimeTextView = findViewById(R.id.detail_doneDateTime);
        favouriteIconImageView = findViewById(R.id.detail_favourite_icon);
      contactsListView = findViewById(R.id.detail_contacts_list);

    updateUI();
  }

  @Override
  public boolean onCreateOptionsMenu(final Menu menuView) {
    getMenuInflater().inflate(R.menu.detail_menu, menuView);
    return super.onCreateOptionsMenu(menuView);
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_edit_todo:
        final Intent editActivityIntent = new Intent(this, EditActivity.class);
        editActivityIntent.putExtra(EditActivity.TODO_ID_KEY, todo.get_id());
        startActivity(editActivityIntent);

        return true;

            case R.id.action_delete_todo:
                new AlertDialog.Builder(this)
                        .setTitle("Delete todo")
                        .setMessage("Do you really want to delete this todo?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> deleteTodo())
                        .setNegativeButton(android.R.string.no, null).show();

                return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

    private void deleteTodo() {
        todoRepository.deleteById(todo.get_id());
        final Intent todoListIntent = new Intent(this, TodoListActivity.class);
        startActivity(todoListIntent);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case PICK_CONTACT_REQUEST_CODE:
                if (data == null || data.getData() == null) {
                    return;
                }final String contactId = contactRepository.insertByUri(data.getData());
                if (contactId != null) {
                    final Set<String> newContactIds = todo.getContactIds();
                    newContactIds.add(contactId);
                    todoRepository.update(todo.toBuilder()
                            .contactIds(newContactIds)
                            .build());
                }

        updateUI();
      default:
        super.onActivityResult(requestCode, resultCode, data);
    }
  }

  public void onAddContactClick(final View view) {
    if (ContextCompat.checkSelfPermission(this, permission.READ_CONTACTS) != PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{permission.READ_CONTACTS}, 0);
      return;
    }
    final Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
    startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST_CODE);
  }

    public void removeContact(final View clickedView) {
        ofNullable((TextView) ((View) clickedView.getParent()).findViewById(R.id.contact_id))
                .ifPresent((TextView view) -> {
                    final String contactId = view.getText().toString();
                    if (!contactId.isEmpty()) {
                        contactRepository.deleteById(contactId);
                        final Set<String> newContactIds = todo.getContactIds();
                        newContactIds.remove(contactId);
                        final Todo updatedTodo = todo.toBuilder()
                                .contactIds(newContactIds)
                                .build();
                        todoRepository.update(updatedTodo);

                        this.todo = updatedTodo;
                    }
                });

        updateUI();

    }

    private void updateUI() {
        titleTextView.setText(todo.getTitle());descriptionTextView.setText(todo.getDescription());
        final String dueDateTimeText = "due to: " + todo.dueDateTimeAsString();
        dueDateTimeTextView.setText(dueDateTimeText);
        favouriteIconImageView.setImageResource(todo.isFavorite() ? R.drawable.ic_star_black_24dp : R.drawable.ic_star_border_black_24dp);
        contactsListView.setAdapter(new ContactArrayAdapter(this, contactRepository.findForTodo(todo)));
  if (todo.isCompleted()) {
            final String completionDateTimeText = "completed: " + todo.completionDateTimeAsString();
            completionDateTimeTextView.setText(completionDateTimeText);
        } else {
            completionDateTimeTextView.setText("");
        }  }

  private void initTodoListActivity() {
    final Intent todoListIntent = new Intent(this, TodoListActivity.class);
    startActivity(todoListIntent);
  }
}

package beutel.ferenc.de.todolists.todo.domain;

import static java.time.Instant.ofEpochSecond;
import static java.time.LocalDateTime.ofInstant;

import static android.provider.BaseColumns._ID;
import static beutel.ferenc.de.todolists.common.http.UrlHelper.BACKEND_BASE_URL;
import static beutel.ferenc.de.todolists.common.http.UrlHelper.TODOS_ENDPOINT;
import static beutel.ferenc.de.todolists.common.http.UrlHelper.URL_DELIMITER;
import static beutel.ferenc.de.todolists.common.http.UrlHelper.toUrl;
import static beutel.ferenc.de.todolists.todo.domain.TodoContract.TodoEntry.ALL_COLUMNS;
import static beutel.ferenc.de.todolists.todo.domain.TodoContract.TodoEntry.COL_TODO_COMPLETED;
import static beutel.ferenc.de.todolists.todo.domain.TodoContract.TodoEntry.COL_TODO_COMPLETION_DATE_TIME;
import static beutel.ferenc.de.todolists.todo.domain.TodoContract.TodoEntry.COL_TODO_CONTACT_IDS;
import static beutel.ferenc.de.todolists.todo.domain.TodoContract.TodoEntry.COL_TODO_DESCRIPTION;
import static beutel.ferenc.de.todolists.todo.domain.TodoContract.TodoEntry.COL_TODO_DUE_DATE_TIME;
import static beutel.ferenc.de.todolists.todo.domain.TodoContract.TodoEntry.COL_TODO_FAVOURITE;
import static beutel.ferenc.de.todolists.todo.domain.TodoContract.TodoEntry.COL_TODO_TITLE;
import static beutel.ferenc.de.todolists.todo.domain.TodoContract.TodoEntry.TABLE;
import static beutel.ferenc.de.todolists.todo.domain.TodoRepository.OrderDirection.ASC;
import static beutel.ferenc.de.todolists.todo.domain.TodoRepository.OrderDirection.DESC;

import java.net.URL;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import beutel.ferenc.de.todolists.common.domain.DBHelper;
import beutel.ferenc.de.todolists.common.domain.Pair;
import beutel.ferenc.de.todolists.common.http.AsyncDeleteRequest;
import beutel.ferenc.de.todolists.common.http.AsyncPutRequest;
import beutel.ferenc.de.todolists.common.http.ObjectMapperFactory;
import beutel.ferenc.de.todolists.todolist.domain.SortOrder;

public class TodoRepository extends SQLiteOpenHelper {

  private static final String ID_SELECTION_CLAUSE = _ID + "=?";

  public TodoRepository(final Context context) {
    super(context, TodoContract.DB_NAME, null, TodoContract.DB_VERSION);
  }

  private static List<Todo> cursorToTodo(final Cursor cursor) {
    final List<Todo> storedTodos = new ArrayList<>();

    while (cursor.moveToNext()) {
      storedTodos.add(Todo.builder()
        ._id(String.valueOf(cursor.getString(cursor.getColumnIndex(_ID))))
        .title(cursor.getString(cursor.getColumnIndex(COL_TODO_TITLE)))
        .description(cursor.getString(cursor.getColumnIndex(COL_TODO_DESCRIPTION)))
        .dueDateTime(
          ofInstant(ofEpochSecond(cursor.getLong(cursor.getColumnIndex(COL_TODO_DUE_DATE_TIME)), 0), ZoneId.systemDefault()))
        .completionDateTime(ofInstant(ofEpochSecond(cursor.getLong(cursor.getColumnIndex(COL_TODO_COMPLETION_DATE_TIME)), 0),
          ZoneId.systemDefault()))
        .favorite(cursor.getInt(cursor.getColumnIndex(COL_TODO_FAVOURITE)) > 0)
        .completed(cursor.getInt(cursor.getColumnIndex(COL_TODO_COMPLETED)) > 0)
        .contactIds(Todo.contactIdsFromString(cursor.getString(cursor.getColumnIndex(COL_TODO_CONTACT_IDS))))
        .build());
    }

    cursor.close();

    return storedTodos;
  }

  @Override
  public void onCreate(final SQLiteDatabase db) {
    DBHelper.CREATE_DB_COMMANDS().forEach(db::execSQL);
  }

  @Override
  public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
    DBHelper.UPDATE_DB_COMMANDS().forEach(db::execSQL);
  }

  public Optional<Todo> findById(final String todoId) {
    final SQLiteDatabase readableDB = getReadableDatabase();
    final String[] selectionArgs = {todoId};
    final List<Todo> foundTodos = cursorToTodo(
      readableDB.query(TABLE, ALL_COLUMNS, ID_SELECTION_CLAUSE, selectionArgs, null, null, null));
    readableDB.close();

    return !foundTodos.isEmpty() ? Optional.of(foundTodos.get(0)) : Optional.empty();
  }

  public List<Todo> findAllOrderedBy(final SortOrder sortOrder) {
    final SQLiteDatabase readableDB = getReadableDatabase();
    final List<Todo> result = cursorToTodo(
      readableDB.query(TABLE, ALL_COLUMNS, null, null, null, null, determineOrderParams(sortOrder)));

    readableDB.close();
    return result;
  }

  public List<Todo> findAll() {
    final SQLiteDatabase readableDB = getReadableDatabase();
    final List<Todo> result = cursorToTodo(readableDB.query(TABLE, ALL_COLUMNS, null, null, null, null, null));

    readableDB.close();
    return result;
  }

  public void insert(final Todo todo) {
    final SQLiteDatabase writeableDB = getWritableDatabase();
    final long insertedId = writeableDB.insert(TABLE, null, todoToContentValues(todo));
    writeableDB.close();

    if (DBHelper.NETWORK_REACHABLE) {
      updateOnRemove(todo.toBuilder()._id(String.valueOf(insertedId)).build());
    }
  }

  public void deleteById(final String todoId) {
    final SQLiteDatabase writeableDB = getWritableDatabase();
    final String[] selectionArgs = {todoId};
    writeableDB.delete(TABLE, ID_SELECTION_CLAUSE, selectionArgs);
    writeableDB.close();

    if (DBHelper.NETWORK_REACHABLE) {
      deleteOnRemote(todoId);
    }
  }

  public void update(final Todo todo) {
    final SQLiteDatabase writeableDB = getWritableDatabase();
    final String[] selectionArgs = {todo.get_id()};

    writeableDB.update(TABLE, todoToContentValues(todo), ID_SELECTION_CLAUSE, selectionArgs);
    writeableDB.close();

    if (DBHelper.NETWORK_REACHABLE) {
      updateOnRemove(todo);
    }
  }

  @SneakyThrows
  @SuppressWarnings("unchecked")
  private void updateOnRemove(final Todo todo) {
    AsyncPutRequest.builder()
      .onResponse(asyncResponses -> {
      })
      .build()
      .execute(Pair.<URL, String>builder().left(toUrl(BACKEND_BASE_URL + TODOS_ENDPOINT + URL_DELIMITER + todo.get_id()))
        .right(ObjectMapperFactory.mapper().writeValueAsString(todo))
        .build());
  }

  private void deleteOnRemote(final String todoId) {
    AsyncDeleteRequest.builder().onResponse(asyncResponses -> {
    }).build().execute(toUrl(BACKEND_BASE_URL + TODOS_ENDPOINT + URL_DELIMITER + todoId));
  }

  private ContentValues todoToContentValues(final Todo todo) {
    final ContentValues values = new ContentValues();
    values.put(COL_TODO_TITLE, todo.getTitle());
    values.put(COL_TODO_DESCRIPTION, todo.getDescription());
    values.put(COL_TODO_DUE_DATE_TIME, todo.getDueDateTime().atZone(ZoneId.systemDefault()).toEpochSecond());
    values.put(COL_TODO_DUE_DATE_TIME, todo.getDueDateTime().atZone(ZoneId.systemDefault()).toEpochSecond());
    values.put(COL_TODO_FAVOURITE, todo.isFavorite());
    values.put(COL_TODO_COMPLETED, todo.isCompleted());
    values.put(COL_TODO_CONTACT_IDS, todo.contactIdString());

    if (todo.isCompleted()) {
      values.put(COL_TODO_COMPLETION_DATE_TIME, todo.getCompletionDateTime().atZone(ZoneId.systemDefault()).toEpochSecond());
    }
    return values;
  }

  private String determineOrderParams(final SortOrder sortOrder) {
    final List<OrderParam> orderParams = new ArrayList<>();

    orderParams.add(new OrderParam(COL_TODO_COMPLETED, ASC));

    if (sortOrder == SortOrder.DATE) {
      orderParams.add(new OrderParam(COL_TODO_DUE_DATE_TIME, ASC));
      orderParams.add(new OrderParam(COL_TODO_FAVOURITE, DESC));
    } else {
      orderParams.add(new OrderParam(COL_TODO_FAVOURITE, DESC));
      orderParams.add(new OrderParam(COL_TODO_DUE_DATE_TIME, ASC));
    }

    return orderParams.stream().map(OrderParam::toString).collect(Collectors.joining(","));
  }

  enum OrderDirection {
    ASC, DESC
  }

  @AllArgsConstructor
  class OrderParam {

    private final String column;
    private final OrderDirection orderDirection;

    @Override
    public String toString() {
      return column + " " + orderDirection.name();
    }
  }
}



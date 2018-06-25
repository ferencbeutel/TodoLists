package beutel.ferenc.de.todolists.todo.domain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import beutel.ferenc.de.todolists.common.domain.DBHelper;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static android.provider.BaseColumns._ID;
import static beutel.ferenc.de.todolists.todo.domain.TodoContract.TodoEntry.*;
import static beutel.ferenc.de.todolists.todo.domain.TodoContract.TodoEntry.COL_TODO_COMPLETED;
import static beutel.ferenc.de.todolists.todo.domain.TodoRepository.OrderDirection.ASC;
import static beutel.ferenc.de.todolists.todo.domain.TodoRepository.OrderDirection.DESC;

public class TodoRepository extends SQLiteOpenHelper {

    private static final String ID_SELECTION_CLAUSE = _ID + "=?";

    private static List<Todo> cursorToTodo(final Cursor cursor) {
        final List<Todo> storedTodos = new ArrayList<>();

        while (cursor.moveToNext()) {
            storedTodos.add(Todo.builder()
                    ._id(cursor.getInt(cursor.getColumnIndex(_ID)))
                    .title(cursor.getString(cursor.getColumnIndex(COL_TODO_TITLE)))
                    .description(cursor.getString(cursor.getColumnIndex(COL_TODO_DESCRIPTION)))
                    .dueDateTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(cursor.getLong(cursor.getColumnIndex(COL_TODO_DUE_DATE_TIME)), 0), ZoneId.systemDefault()))
                    .favorite(cursor.getInt(cursor.getColumnIndex(COL_TODO_FAVOURITE)) > 0)
                    .completed(cursor.getInt(cursor.getColumnIndex(COL_TODO_COMPLETED)) > 0)
                    .contactIds(Todo.contactIdsFromString(cursor.getString(cursor.getColumnIndex(COL_TODO_CONTACT_IDS))))
                    .build());
        }

        cursor.close();

        return storedTodos;
    }

    public TodoRepository(Context context) {
        super(context, TodoContract.DB_NAME, null, TodoContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DBHelper.CREATE_DB_COMMANDS().forEach(db::execSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DBHelper.UPDATE_DB_COMMANDS().forEach(db::execSQL);
    }

    public Optional<Todo> findById(final String todoId) {
        final SQLiteDatabase readableDB = getReadableDatabase();
        final String[] selectionArgs = {todoId};
        final List<Todo> foundTodos = cursorToTodo(readableDB.query(TABLE,
                ALL_COLUMNS,
                ID_SELECTION_CLAUSE,
                selectionArgs,
                null,
                null,
                null));
        readableDB.close();

        return foundTodos.size() > 0 ? Optional.of(foundTodos.get(0)) : Optional.empty();
    }

    public List<Todo> findAllOrderedByPriority() {
        final SQLiteDatabase readableDB = getReadableDatabase();
        final List<Todo> result = cursorToTodo(readableDB.query(TABLE,
                ALL_COLUMNS,
                null,
                null,
                null,
                null,
                concatOrderParams(new OrderParam(COL_TODO_COMPLETED, ASC),
                        new OrderParam(COL_TODO_FAVOURITE, DESC),
                        new OrderParam(COL_TODO_DUE_DATE_TIME, ASC))));

        readableDB.close();
        return result;
    }

    public void insert(final Todo todo) {
        final SQLiteDatabase writeableDB = getWritableDatabase();
        writeableDB.insert(TABLE, null, todoToContentValues(todo));
        writeableDB.close();
    }

    public void deleteById(final String todoId) {
        final SQLiteDatabase writeableDB = getWritableDatabase();
        final String[] selectionArgs = {todoId};
        writeableDB.delete(TABLE, ID_SELECTION_CLAUSE, selectionArgs);
        writeableDB.close();
    }

    public void update(final Todo todo) {
        final SQLiteDatabase writeableDB = getWritableDatabase();
        final String[] selectionArgs = {String.valueOf(todo.get_id())};

        writeableDB.update(TABLE, todoToContentValues(todo), ID_SELECTION_CLAUSE, selectionArgs);
        writeableDB.close();
    }

    private ContentValues todoToContentValues(final Todo todo) {
        final ContentValues values = new ContentValues();
        values.put(COL_TODO_TITLE, todo.getTitle());
        values.put(COL_TODO_DESCRIPTION, todo.getDescription());
        values.put(COL_TODO_DUE_DATE_TIME, todo.getDueDateTime().atZone(ZoneId.systemDefault()).toEpochSecond());
        values.put(COL_TODO_FAVOURITE, todo.isFavorite());
        values.put(COL_TODO_COMPLETED, todo.isCompleted());
        values.put(COL_TODO_CONTACT_IDS, todo.contactIdString());

        return values;
    }

    private String concatOrderParams(final OrderParam... orderParams) {
        return Arrays.stream(orderParams)
                .map(OrderParam::toString)
                .collect(Collectors.joining(","));
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

    enum OrderDirection {
        ASC, DESC
    }
}



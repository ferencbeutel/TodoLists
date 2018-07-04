package beutel.ferenc.de.todolists.todo.domain;

import android.provider.BaseColumns;

public class TodoContract {

  static final String DB_NAME = "de.beutel.ferenc.todolists.db";
  static final int DB_VERSION = 1;

  public static class TodoEntry implements BaseColumns {

    public static final String TABLE = "todos";

    public static final String COL_TODO_TITLE = "title";
    public static final String COL_TODO_DESCRIPTION = "description";
    public static final String COL_TODO_DUE_DATE_TIME = "dueDateTime";
    public static final String COL_TODO_COMPLETION_DATE_TIME = "completionDateTime";
        public static final String COL_TODO_FAVOURITE = "favourite";
    public static final String COL_TODO_COMPLETED = "completed";
    public static final String COL_TODO_CONTACT_IDS = "contact_ids";

         static final String[] ALL_COLUMNS = {_ID, COL_TODO_TITLE, COL_TODO_DESCRIPTION, COL_TODO_DUE_DATE_TIME,COL_TODO_COMPLETION_DATE_TIME, COL_TODO_FAVOURITE, COL_TODO_COMPLETED, COL_TODO_CONTACT_IDS
    };
  }
}

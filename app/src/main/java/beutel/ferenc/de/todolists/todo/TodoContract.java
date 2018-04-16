package beutel.ferenc.de.todolists.todo;

import android.provider.BaseColumns;

public class TodoContract {

    public static final String DB_NAME = "de.beutel.ferenc.todolists.db";
    public static final int DB_VERSION = 1;

    public class TodoEntry implements BaseColumns {
        public static final String TABLE = "todos";

        public static final String COL_TODO_TITLE = "title";
    }
}

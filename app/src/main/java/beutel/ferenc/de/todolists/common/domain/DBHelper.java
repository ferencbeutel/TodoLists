package beutel.ferenc.de.todolists.common.domain;

import beutel.ferenc.de.todolists.contact.domain.ContactContract;
import beutel.ferenc.de.todolists.todo.domain.TodoContract;

import java.util.ArrayList;
import java.util.List;

public class DBHelper {
    public static List<String> CREATE_DB_COMMANDS() {
        final List<String> commands = new ArrayList<>();
        commands.add("CREATE TABLE " + ContactContract.ContactEntry.TABLE + " ( " +
                ContactContract.ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                ");");

        commands.add("CREATE TABLE " + TodoContract.TodoEntry.TABLE + " ( " +
                TodoContract.TodoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TodoContract.TodoEntry.COL_TODO_TITLE + " TEXT NOT NULL, " +
                TodoContract.TodoEntry.COL_TODO_DESCRIPTION + " TEXT NOT NULL, " +
                TodoContract.TodoEntry.COL_TODO_DUE_DATE_TIME + " DATETIME NOT NULL, " +
                TodoContract.TodoEntry.COL_TODO_COMPLETED + " BOOLEAN NOT NULL, " +
                TodoContract.TodoEntry.COL_TODO_FAVOURITE + " BOOLEAN NOT NULL, " +
                TodoContract.TodoEntry.COL_TODO_CONTACT_IDS + " TEXT NOT NULL" +
                ");");

        return commands;
    }

    public static List<String> UPDATE_DB_COMMANDS() {
        final List<String> commands = new ArrayList<>();
        commands.add("DROP TABLE IF EXISTS " + TodoContract.TodoEntry.TABLE);
        commands.add("DROP TABLE IF EXISTS " + ContactContract.ContactEntry.TABLE);

        commands.addAll(CREATE_DB_COMMANDS());

        return commands;
    }
}

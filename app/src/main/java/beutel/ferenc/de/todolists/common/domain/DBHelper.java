package beutel.ferenc.de.todolists.common.domain;

import java.util.ArrayList;
import java.util.List;

import beutel.ferenc.de.todolists.contact.domain.ContactContract.ContactEntry;
import beutel.ferenc.de.todolists.todo.domain.TodoContract.TodoEntry;

public class DBHelper {

  public static List<String> CREATE_DB_COMMANDS() {
    final List<String> commands = new ArrayList<>();
    commands.add("CREATE TABLE " + ContactEntry.TABLE + " ( " + ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ");");

    commands.add("CREATE TABLE " + TodoEntry.TABLE + " ( " + TodoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
      TodoEntry.COL_TODO_TITLE + " TEXT NOT NULL, " + TodoEntry.COL_TODO_DESCRIPTION + " TEXT NOT NULL, " +
      TodoEntry.COL_TODO_DUE_DATE_TIME + " DATETIME NOT NULL, " + TodoEntry.COL_TODO_COMPLETED + " BOOLEAN NOT NULL, " +
      TodoEntry.COL_TODO_FAVOURITE + " BOOLEAN NOT NULL, " + TodoEntry.COL_TODO_CONTACT_IDS + " TEXT NOT NULL" + ");");

    return commands;
  }

  public static List<String> UPDATE_DB_COMMANDS() {
    final List<String> commands = new ArrayList<>();
    commands.add("DROP TABLE IF EXISTS " + TodoEntry.TABLE);
    commands.add("DROP TABLE IF EXISTS " + ContactEntry.TABLE);

    commands.addAll(CREATE_DB_COMMANDS());

    return commands;
  }
}

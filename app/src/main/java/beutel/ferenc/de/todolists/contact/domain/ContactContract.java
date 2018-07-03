package beutel.ferenc.de.todolists.contact.domain;

import android.provider.BaseColumns;

public class ContactContract {

  static final String DB_NAME = "de.beutel.ferenc.todolists.db";
  static final int DB_VERSION = 1;

  public static class ContactEntry implements BaseColumns {

    public static final String TABLE = "contacts";

    static final String[] ALL_COLUMNS = {_ID};
  }
}

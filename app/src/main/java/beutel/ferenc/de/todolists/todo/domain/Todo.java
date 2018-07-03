package beutel.ferenc.de.todolists.todo.domain;

import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.Builder;
import lombok.Data;

import beutel.ferenc.de.todolists.todo.domain.Todo.TodoBuilder;

@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = TodoBuilder.class)
public class Todo {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
  private static final String CONTACT_IDS_DELIMITER = ",";

  private final String _id;
  private final String title;
  private final String description;
  private final LocalDateTime dueDateTime;
  private final boolean favorite;
  private final boolean completed;
  private final List<String> contactIds;

  static List<String> contactIdsFromString(final String contactIdsString) {
    return Arrays.stream(contactIdsString.split(CONTACT_IDS_DELIMITER))
      .filter(contactId -> !contactId.isEmpty())
      .collect(toList());
  }

  @JsonIgnore
  public boolean isValid() {
    final LocalDateTime now = LocalDateTime.now();
    return title != null && description != null && dueDateTime != null && !title.isEmpty() && dueDateTime.isAfter(now);
  }

  @JsonIgnore
  public String contactIdString() {
    return String.join(CONTACT_IDS_DELIMITER, contactIds);
  }

  @JsonIgnore
  public List<String> errorMessages() {
    final LocalDateTime now = LocalDateTime.now();
    final List<String> errors = new ArrayList<>();

    if (title == null || title.isEmpty()) {
      errors.add("Title is mandatory");
    }
    if (dueDateTime == null || !dueDateTime.isAfter(now)) {
      errors.add("due date & time are mandatory and have to be in the future");
    }

    return errors;
  }

  @JsonIgnore
  public String dueDateTimeAsString() {
    return dueDateTime.format(DATE_TIME_FORMATTER);
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class TodoBuilder {

  }
}

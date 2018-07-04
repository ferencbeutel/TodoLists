package beutel.ferenc.de.todolists.todo.domain;

import static java.util.stream.Collectors.toSet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
  private final LocalDateTime completionDateTime;
  private final boolean favorite;
  private final boolean completed;
  private final Set<String> contactIds;

  static Set<String> contactIdsFromString(final String contactIdsString) {
    return Arrays.stream(contactIdsString.split(CONTACT_IDS_DELIMITER))
      .filter(contactId -> !contactId.isEmpty())
      .collect(toSet());
  }

  @JsonIgnore
  public boolean isNotValid() {
    return title == null || description == null || dueDateTime == null || title.isEmpty();
  }

  @JsonIgnore
  public String contactIdString() {
    return String.join(CONTACT_IDS_DELIMITER, contactIds);
  }

  @JsonIgnore
  public List<String> errorMessages() {
    final List<String> errors = new ArrayList<>();

    if (title == null || title.isEmpty()) {
      errors.add("Title is mandatory");
    }
    if (dueDateTime == null) {
      errors.add("due date & time are mandatory");
    }

    return errors;
  }

  @JsonIgnore
  public boolean isOverDue() {
    return LocalDateTime.now().isAfter(dueDateTime);
  }

  @JsonIgnore
  public String dueDateTimeAsString() {
    return dueDateTime.format(DATE_TIME_FORMATTER);
  }

  @JsonIgnore
  public String completionDateTimeAsString() {
    return completionDateTime.format(DATE_TIME_FORMATTER);
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class TodoBuilder {

  }
}



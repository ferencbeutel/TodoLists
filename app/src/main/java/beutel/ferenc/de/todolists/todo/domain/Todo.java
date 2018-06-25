package beutel.ferenc.de.todolists.todo.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
@Builder(toBuilder = true)
public class Todo {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final String CONTACT_IDS_DELIMITER = ",";

    private final int _id;
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

    public boolean isValid() {
        final LocalDateTime now = LocalDateTime.now();
        return title != null &&
                description != null &&
                dueDateTime != null &&
                !title.isEmpty() &&
                dueDateTime.isAfter(now);
    }

    public String contactIdString() {
        return String.join(CONTACT_IDS_DELIMITER, contactIds);
    }

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

    public String dueDateTimeAsString() {
        return dueDateTime.format(DATE_TIME_FORMATTER);
    }
}

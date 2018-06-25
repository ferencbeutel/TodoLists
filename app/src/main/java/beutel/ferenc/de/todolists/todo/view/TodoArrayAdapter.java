package beutel.ferenc.de.todolists.todo.view;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import beutel.ferenc.de.todolists.R;
import beutel.ferenc.de.todolists.todo.domain.Todo;

import java.util.List;

public class TodoArrayAdapter extends ArrayAdapter<Todo> {
    private Context context;
    private List<Todo> todos;

    public TodoArrayAdapter(final Context context, final List<Todo> todos) {
        super(context, 0, todos);
        this.context = context;
        this.todos = todos;
    }

    @NonNull
    @Override
    public View getView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        final View listItem = convertView == null ?
                LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false) :
                convertView;

        final Todo currentTodo = todos.get(position);

        final TextView todoTitleView = listItem.findViewById(R.id.todo_title);
        todoTitleView.setText(currentTodo.getTitle());

        final TextView todoDueDateTimeView = listItem.findViewById(R.id.todo_dueDateTime);
        todoDueDateTimeView.setText(currentTodo.dueDateTimeAsString());

        final ImageView favouriteIconView = listItem.findViewById(R.id.todo_favourite_icon);
        favouriteIconView.setImageResource(currentTodo.isFavorite() ? R.drawable.ic_star_black_24dp : R.drawable.ic_star_border_black_24dp);

        final Button doneRemoveButton = listItem.findViewById(R.id.todo_delete);
        doneRemoveButton.setText(currentTodo.isCompleted() ? "Remove" : "Done");

        final TextView idTextView = listItem.findViewById(R.id.todo_id);
        idTextView.setText(String.valueOf(currentTodo.get_id()));

        if (currentTodo.isCompleted()) {
            todoTitleView.setPaintFlags(todoTitleView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            todoTitleView.setTextColor(ContextCompat.getColor(context, R.color.disabledTextColor));

            todoDueDateTimeView.setPaintFlags(todoDueDateTimeView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            todoDueDateTimeView.setTextColor(ContextCompat.getColor(context, R.color.disabledTextColor));
        } else {
            todoTitleView.setPaintFlags(todoTitleView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            todoTitleView.setTextColor(ContextCompat.getColor(context, R.color.textColor));

            todoDueDateTimeView.setPaintFlags(todoTitleView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            todoDueDateTimeView.setTextColor(ContextCompat.getColor(context, R.color.textColor));
        }

        return listItem;

    }
}

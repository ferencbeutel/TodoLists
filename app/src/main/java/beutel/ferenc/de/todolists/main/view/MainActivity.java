package beutel.ferenc.de.todolists.main.view;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.Arrays.stream;

import static beutel.ferenc.de.todolists.common.http.UrlHelper.BACKEND_BASE_URL;
import static beutel.ferenc.de.todolists.common.http.UrlHelper.HEALTH_ENDPOINT;
import static beutel.ferenc.de.todolists.common.http.UrlHelper.TODOS_ENDPOINT;
import static beutel.ferenc.de.todolists.common.http.UrlHelper.URL_DELIMITER;
import static beutel.ferenc.de.todolists.common.http.UrlHelper.toUrl;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import lombok.SneakyThrows;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import beutel.ferenc.de.todolists.R.layout;
import beutel.ferenc.de.todolists.common.domain.DBHelper;
import beutel.ferenc.de.todolists.common.domain.Pair;
import beutel.ferenc.de.todolists.common.http.AsyncDeleteRequest;
import beutel.ferenc.de.todolists.common.http.AsyncGetRequest;
import beutel.ferenc.de.todolists.common.http.AsyncPutRequest;
import beutel.ferenc.de.todolists.common.http.AsyncResponse;
import beutel.ferenc.de.todolists.common.http.ObjectMapperFactory;
import beutel.ferenc.de.todolists.login.view.LoginActivity;
import beutel.ferenc.de.todolists.todo.domain.Todo;
import beutel.ferenc.de.todolists.todo.domain.TodoRepository;
import beutel.ferenc.de.todolists.todolist.view.TodoListActivity;

public class MainActivity extends AppCompatActivity {

  private TodoRepository todoRepository;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(layout.activity_main);

    todoRepository = new TodoRepository(this);

    initServerConnection();
  }

  private void initServerConnection() {
    AsyncGetRequest.builder().onResponse(this::onHealthCheckResponse).build().execute(toUrl(BACKEND_BASE_URL + HEALTH_ENDPOINT));
  }

  private void onHealthCheckResponse(final List<AsyncResponse> asyncResponse) {
    if (asyncResponse.isEmpty() || asyncResponse.get(0).getResponseCode() != HTTP_OK) {
      Log.d("MainActivity", "backend is not available. Response: " + asyncResponse);
      onBackendNotAvailable();
      return;
    }
    initTodos();
  }

  @SneakyThrows
  private void initTodos() {
    final List<Todo> localTodos = todoRepository.findAll();
    if (localTodos.isEmpty()) {
      Log.d("MainActivity", "no local todos, fetching from remote");
      AsyncGetRequest.builder().onResponse(this::fetchRemoteTodos).build().execute(toUrl(BACKEND_BASE_URL + TODOS_ENDPOINT));
      return;
    }

    Log.d("MainActivity", "local todos present, updating remote");
    AsyncDeleteRequest.builder()
      .onResponse(asyncResponse -> updateRemoteTodos(localTodos))
      .build()
      .execute(toUrl(BACKEND_BASE_URL + TODOS_ENDPOINT));
  }

  @SuppressWarnings("unchecked")
  private void updateRemoteTodos(final List<Todo> localTodos) {
    final Pair<URL, String>[] putRequests = localTodos.stream()
      .map(localTodo -> Pair.<URL, String>builder().left(
        toUrl(BACKEND_BASE_URL + TODOS_ENDPOINT + URL_DELIMITER + localTodo.get_id())).right(serialize(localTodo)).build())
      .toArray(Pair[]::new);

    AsyncPutRequest.builder().onResponse(asyncResponses -> onBackendAvailable()).build().execute(putRequests);
  }

  private void fetchRemoteTodos(final List<AsyncResponse> asyncResponse) {
    if (asyncResponse.isEmpty() || asyncResponse.get(0).getResponseCode() != HTTP_OK) {
      return;
    }
    deserializeAll(asyncResponse.get(0).getResponse()).forEach(todoRepository::insert);
    onBackendAvailable();
  }

  private void onBackendAvailable() {
    DBHelper.NETWORK_REACHABLE = true;
    final Intent loginIntent = new Intent(this, LoginActivity.class);
    startActivity(loginIntent);
  }

  private void onBackendNotAvailable() {
    DBHelper.NETWORK_REACHABLE = false;
    final Intent localListIntent = new Intent(this, TodoListActivity.class);
    startActivity(localListIntent);
  }

  @SneakyThrows
  private String serialize(final Todo todo) {
    return ObjectMapperFactory.mapper().writeValueAsString(todo);
  }

  @SneakyThrows
  private List<Todo> deserializeAll(final String todosString) {
    return stream(ObjectMapperFactory.mapper().readValue(todosString, String[].class)).map(this::deserialize)
      .collect(Collectors.toList());
  }

  @SneakyThrows
  private Todo deserialize(final String todoString) {
    return ObjectMapperFactory.mapper().readValue(todoString, Todo.class);
  }
}

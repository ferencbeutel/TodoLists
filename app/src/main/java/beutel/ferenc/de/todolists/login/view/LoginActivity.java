package beutel.ferenc.de.todolists.login.view;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.Arrays.asList;

import static beutel.ferenc.de.todolists.common.http.UrlHelper.BACKEND_BASE_URL;
import static beutel.ferenc.de.todolists.common.http.UrlHelper.LOGIN_ENDPOINT;
import static beutel.ferenc.de.todolists.common.http.UrlHelper.USER_ENDPOINT;
import static beutel.ferenc.de.todolists.common.http.UrlHelper.toUrl;

import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import beutel.ferenc.de.todolists.R.id;
import beutel.ferenc.de.todolists.R.layout;
import beutel.ferenc.de.todolists.common.domain.Pair;
import beutel.ferenc.de.todolists.common.http.AsyncPostRequest;
import beutel.ferenc.de.todolists.common.http.AsyncResponse;
import beutel.ferenc.de.todolists.common.http.ObjectMapperFactory;
import beutel.ferenc.de.todolists.login.domain.User;
import beutel.ferenc.de.todolists.todolist.view.TodoListActivity;

public class LoginActivity extends AppCompatActivity {

  private TextView loginErrorView;
  private EditText emailInput;
  private TextView emailErrorView;
  private EditText passwordInput;
  private TextView passwordErrorView;
  private ProgressBar loginSpinner;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(layout.activity_login);

    loginErrorView = findViewById(id.login_error);
    emailInput = findViewById(id.login_email);
    emailErrorView = findViewById(id.email_login_error);
    passwordInput = findViewById(id.login_password);
    passwordErrorView = findViewById(id.password_login_error);
    loginSpinner = findViewById(id.login_spinner);

    setupChangeListener();
  }

  @SuppressWarnings("unchecked")
  @SneakyThrows
  public void onLoginButtonClicked(final View view) {
    final String email = emailInput.getText().toString();
    final String password = passwordInput.getText().toString();
    final Boolean isEmailValid = isValidEmail(email);
    final Boolean isPasswordValid = isValidPassword(password);

    if (!isEmailValid) {
      emailErrorView.setVisibility(View.VISIBLE);
    }
    if (!isPasswordValid) {
      passwordErrorView.setVisibility(View.VISIBLE);
    }
    if (!isEmailValid || !isPasswordValid) {
      return;
    }

    loginSpinner.setVisibility(View.VISIBLE);
    AsyncPostRequest.builder()
      .onResponse(this::onLoginResponse)
      .build()
      .execute(Pair.<URL, String>builder().left(toUrl(BACKEND_BASE_URL + USER_ENDPOINT + LOGIN_ENDPOINT))
        .right(ObjectMapperFactory.mapper().writeValueAsString(User.builder().username(email).password(password).build()))
        .build());
  }

  private void onLoginResponse(final List<AsyncResponse> asyncResponses) {
    loginSpinner.setVisibility(View.GONE);
    if (asyncResponses.isEmpty() || asyncResponses.get(0).getResponseCode() != HTTP_OK) {
      loginErrorView.setVisibility(View.VISIBLE);
      return;
    }
    final Intent listIntent = new Intent(this, TodoListActivity.class);
    startActivity(listIntent);
  }

  private void setupChangeListener() {
    emailInput.addTextChangedListener(new HideErrorsTextWatcher(asList(emailErrorView, loginErrorView)));
    passwordInput.addTextChangedListener(new HideErrorsTextWatcher(asList(passwordErrorView, loginErrorView)));
  }

  private Boolean isValidEmail(final String textInput) {
    final Pattern emailPattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");

    return emailPattern.matcher(textInput).matches();
  }

  private Boolean isValidPassword(final String textInput) {
    final Pattern emailPattern = Pattern.compile("[0-9]+");

    return textInput.length() == 6 && emailPattern.matcher(textInput).matches();
  }

  @AllArgsConstructor
  private class HideErrorsTextWatcher implements TextWatcher {

    private final List<TextView> viewsToHide;

    @Override
    public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
    }

    @Override
    public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
      viewsToHide.forEach(view -> view.setVisibility(View.GONE));
    }

    @Override
    public void afterTextChanged(final Editable s) {
    }
  }
}

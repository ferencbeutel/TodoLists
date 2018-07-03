package beutel.ferenc.de.todolists.common.http;

import static java.util.Arrays.asList;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import android.os.AsyncTask;
import android.util.Log;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class AsyncDeleteRequest extends AsyncTask<URL, String, List<AsyncResponse>> {

  private final Consumer<List<AsyncResponse>> onResponse;

  @Override
  protected List<AsyncResponse> doInBackground(final URL... urls) {
    final List<AsyncResponse> responses = new ArrayList<>();
    asList(urls).forEach(url -> {
      try {
        final HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("DELETE");
        responses.add(AsyncResponse.builder()
          .responseCode(httpURLConnection.getResponseCode())
          .build());
      } catch (final Exception exception) {
        Log.d("AsnycRequest", "Error during async request", exception);
      }
    });

    return responses;
  }

  @Override
  protected void onPostExecute(final List<AsyncResponse> result) {
    super.onPostExecute(result);
    onResponse.accept(result);
  }
}

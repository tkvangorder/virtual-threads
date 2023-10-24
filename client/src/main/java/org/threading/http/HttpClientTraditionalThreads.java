package org.threading.http;

import lombok.extern.slf4j.Slf4j;
import org.threading.utils.TestHarness;
import org.threading.utils.Utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;

@Slf4j
public class HttpClientTraditionalThreads {

  private static final HttpClient httpClient = HttpClient.newBuilder()
      .build();
  private static final HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:8080/sleepy"))
      .build();


  public static void main(String[] args) {
    Utils.waitForPrompt("Press enter to start Imperative HTTP Client Tests");

    try (ExecutorService executorService = Utils.traditionalUnboundedExecutorService()) {
      TestHarness.run("Http Requests Imperative, Unbounded Threads", executorService,
          500, HttpClientTraditionalThreads::httpClientTask);
    }

    httpClient.close();
    Utils.waitForPrompt("Press enter to exit");
  }
  public static void httpClientTask() {
    try {
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      log.info(response.body());
    } catch (IOException | InterruptedException e) {
      log.error("Error making request", e);
    }
  }


}

package org.threading.imperative;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class HttpClientImperative {

  public static void main(String[] args) {
    Utils.waitForPrompt("Press enter to start Imperative HTTP Client Tests");

    try (ExecutorService executorService = Utils.traditionalUnboundedExecutorService()) {
      TestHarness.run("Http Requests Imperative, Unbounded Threads", executorService,
          2000, HttpClientImperative::httpClientTask);
    }

    try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
      TestHarness.run("Http Requests Imperative, Virtual Threads", executorService,
          2000, HttpClientImperative::httpClientTask);
    }

    Utils.waitForPrompt("Press enter to exit");
  }

  public static void httpClientTask() {
    try (HttpClient httpClient = getHttpClient()) {

      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create("http://localhost:8080/sleepy"))
          .build();
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      log.info(response.body());
    } catch (IOException | InterruptedException e) {
      log.error("Error making request", e);
    }
  }

  public static HttpClient getHttpClient() {
    return HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();
  }
}

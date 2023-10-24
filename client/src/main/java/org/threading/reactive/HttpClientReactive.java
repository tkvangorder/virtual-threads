package org.threading.reactive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.threading.imperative.Utils;
import reactor.core.publisher.Mono;

@Slf4j
public class HttpClientReactive {

  private static final WebClient client = WebClient.builder().build();

  public static void main(String[] args) {
    Utils.waitForPrompt("Press enter to start Imperative HTTP Client Tests");

    ReactiveTestHarness.run("Reactive HTTP Client", 1000, HttpClientReactive::makeSleepyCall);

    Utils.waitForPrompt("Press enter to exit");
  }


  private static Mono<String> makeSleepyCall() {
    return client.get()
        .uri("http://localhost:8080/sleepy")
        .retrieve()
        .bodyToMono(String.class)
        .doOnNext(s -> log.info("Response: " + s));
  }

}

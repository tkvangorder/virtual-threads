package org.threading.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.threading.utils.ReactiveTestHarness;
import org.threading.utils.Utils;
import reactor.core.publisher.Mono;

@Slf4j
public class HttpClientReactive {

  private static final WebClient client = WebClient.builder().build();

  public static void main(String[] args) {
    ReactiveTestHarness.run("Reactive HTTP Client", 500, HttpClientReactive::makeSleepyCall);
  }


  private static Mono<String> makeSleepyCall() {
    return client.get()
        .uri("http://localhost:8080/sleepy")
        .retrieve()
        .bodyToMono(String.class)
        .doOnNext(s -> log.info("Response: " + s));
  }

}

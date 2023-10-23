package org.threading.sleepyserver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SleepyController {

  @GetMapping("/sleepy")
  String sleepResponse() {
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return "Hello World!";
  }

}

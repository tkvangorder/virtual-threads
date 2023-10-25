package org.threading.sleepyserver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SleepyController {

  @GetMapping("/sleepy")
  public String sleepResponse() {
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return "Hello World!";
  }

}

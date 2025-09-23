package org.threading.scoped;

import org.threading.coordinate.model.Cart;
import org.threading.utils.Utils;

import java.util.concurrent.StructuredTaskScope;

public class ScopedValueExperiment {

  private static final ScopedValue<String> CURRENT_NAME = ScopedValue.newInstance();
  public static void main(String[] args) {

    ScopedValueExperiment experiment = new ScopedValueExperiment();
//    experiment.experiment1();
//    experiment.nested();
//    experiment.threaded();
    experiment.structuredAndScoped();

  }


  private void experiment1() {
    greetCurrentUser();
    ScopedValue.where(CURRENT_NAME, "fred").run(() -> {
      greetCurrentUser();
      someMethod();
    });

    greetCurrentUser();
  }
  private void nested() {
    ScopedValue.where(CURRENT_NAME, "fred").run(() -> {
      greetCurrentUser();
      ScopedValue.where(CURRENT_NAME, "sally").run(() -> {
        System.out.println("In nested");
        someMethod();
        System.out.println("Leaving nested");
      });
      someMethod();
    });
  }

  private void threaded() {

    try (var executor = Utils.newVirtualThreadExecutor("scoped-")) {
      ScopedValue.where(CURRENT_NAME, "fred").run(() -> {
        greetCurrentUser();
        executor.submit(() -> {
          System.out.println("In thread");
          someMethod();
        });
      });
    }
  }

  private void structuredAndScoped() {
    ScopedValue.where(CURRENT_NAME, "fred").run(() -> {

      try (var scope = StructuredTaskScope.open()) {
        StructuredTaskScope.Subtask<Void> cartTask = scope.fork(ScopedValueExperiment::someMethod);
        try {
          scope.join();
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        greetCurrentUser();
      }
    });
  }
  private static void greetCurrentUser() {
    if (CURRENT_NAME.isBound()) {
      System.out.println("Hello " + CURRENT_NAME.get());
    } else {
      System.out.println("There is no user set!");
    }
  }

  private static void someMethod() {
    System.out.println("In some method");
    greetCurrentUser();
  }
}

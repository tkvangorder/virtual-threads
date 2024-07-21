package org.threading.utils;

@FunctionalInterface
public interface CheckedSupplier<T> {

  /**
   * Gets a result.
   *
   * @return a result
   * @throws Exception if an error occurs
   */
  T get() throws Exception;
}

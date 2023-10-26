/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

package com.cognifide.apm.core.grammar.common;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class StackWithRoot<T> implements Iterable<T> {

  private final T root;

  private final Deque<T> internal;

  public StackWithRoot(T root) {
    this.root = root;
    this.internal = new ArrayDeque<>();
  }

  @Override
  public Iterator<T> iterator() {
    List<T> values = new ArrayList<>(internal);
    values.add(root);
    return values.iterator();
  }

  public void push(T element) {
    internal.push(element);
  }

  public T pop() {
    return internal.isEmpty() ? root : internal.pop();
  }

  public T peek() {
    return internal.isEmpty() ? root : internal.peek();
  }
}

package com.njd5475.github.slim.material;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Emitter {

  private Map<String, Set<EmitListener>> messages = new HashMap<>();

  public Emitter() {

  }

  public void register(String event, EmitListener listener) {
    Set<EmitListener> listeners = messages.get(event);
    if (listeners == null) {
      listeners = new LinkedHashSet<EmitListener>();
      messages.put(event, listeners);
    }
    listeners.add(listener);
  }

  public void send(String event, Object text) {
    Set<EmitListener> listeners = messages.get(event);
    if (listeners != null) {
      for (EmitListener l : listeners) {
        l.handle(event, text);
      }
    }
  }

}

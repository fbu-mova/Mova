package com.example.mova.components;

import java.util.HashMap;

public abstract class ComponentManager {

    private HashMap<String, Component> activeComponents;
    private String focusedKey;

    public ComponentManager() {
        activeComponents = new HashMap<>();
    }

    public void launch(String key, Component component) {
        if (activeComponents.containsKey(key)) {
            throw new IllegalArgumentException("Key already exists on ComponentManager");
        }
        activeComponents.put(key, component);
    }

    public Component stop(String key) {
        return activeComponents.remove(key);
    }

    public abstract void onSwap(String fromKey, Component fromComponent, String toKey, Component toComponent);

    public void swap(String key) {
        String fromKey = focusedKey;
        Component fromComponent = getFocused();
        focusedKey = key;
        Component toComponent = getFocused();
        onSwap(fromKey, fromComponent, focusedKey, toComponent);
    }

    public Component getFocused() {
        return activeComponents.get(focusedKey);
    }
}

package com.bothengine.ui;

import android.graphics.Rect;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIEventSystem {
    public enum EventType {
        POINTER_DOWN,
        POINTER_UP,
        POINTER_MOVE,
        POINTER_CANCEL,
        CLICK,
        DOUBLE_CLICK,
        LONG_CLICK,
        HOVER_ENTER,
        HOVER_EXIT,
        DRAG_START,
        DRAG_END,
        DRAG_MOVE,
        SCROLL,
        ZOOM,
        KEY_DOWN,
        KEY_UP,
        KEY_PRESS,
        FOCUS_GAINED,
        FOCUS_LOST
    }

    public static class UIEvent {
        public EventType type;
        public UIComponent target;
        public float x;
        public float y;
        public int pointerId;
        public long timestamp;
        public int keyCode;
        public char character;
        public int modifiers;
        public float scrollDeltaX;
        public float scrollDeltaY;
        public float zoomFactor;
        
        public UIEvent(EventType type) {
            this.type = type;
            this.timestamp = System.currentTimeMillis();
        }

        public UIEvent(EventType type, UIComponent target) {
            this(type);
            this.target = target;
        }

        @Override
        public String toString() {
            return "UIEvent{type=" + type + ", target=" + (target != null ? target.getName() : "null") + 
                   ", x=" + x + ", y=" + y + ", pointerId=" + pointerId + "}";
        }
    }

    public interface EventListener {
        void onEvent(UIEvent event);
    }

    public interface PointerListener {
        void onPointerDown(UIEvent event);
        void onPointerUp(UIEvent event);
        void onPointerMove(UIEvent event);
        void onPointerCancel(UIEvent event);
    }

    public interface ClickListener {
        void onClick(UIEvent event);
    }

    public interface HoverListener {
        void onHoverEnter(UIEvent event);
        void onHoverExit(UIEvent event);
    }

    public interface DragListener {
        void onDragStart(UIEvent event);
        void onDragMove(UIEvent event);
        void onDragEnd(UIEvent event);
    }

    public interface ScrollListener {
        void onScroll(UIEvent event);
    }

    public interface KeyListener {
        void onKeyDown(UIEvent event);
        void onKeyUp(UIEvent event);
        void onKeyPress(UIEvent event);
    }

    private Canvas canvas;
    private Map<UIComponent, List<EventListener>> eventListeners;
    private Map<UIComponent, PointerListener> pointerListeners;
    private Map<UIComponent, ClickListener> clickListeners;
    private Map<UIComponent, HoverListener> hoverListeners;
    private Map<UIComponent, DragListener> dragListeners;
    private Map<UIComponent, ScrollListener> scrollListeners;
    private Map<UIComponent, KeyListener> keyListeners;
    
    private UIComponent hoveredComponent;
    private UIComponent draggedComponent;
    private float lastClickTime;
    private float lastClickX;
    private float lastClickY;
    private UIComponent lastClickedComponent;
    private long lastDragStartTime;
    private float dragThreshold;
    private float doubleClickThreshold;

    public UIEventSystem(Canvas canvas) {
        this.canvas = canvas;
        this.eventListeners = new HashMap<>();
        this.pointerListeners = new HashMap<>();
        this.clickListeners = new HashMap<>();
        this.hoverListeners = new HashMap<>();
        this.dragListeners = new HashMap<>();
        this.scrollListeners = new HashMap<>();
        this.keyListeners = new HashMap<>();
        this.dragThreshold = 10f;
        this.doubleClickThreshold = 300f;
    }

    public void update(float deltaTime) {
    }

    public boolean handleEvent(UIEvent event) {
        switch (event.type) {
            case POINTER_DOWN:
                handlePointerDown(event);
                break;
            case POINTER_UP:
                handlePointerUp(event);
                break;
            case POINTER_MOVE:
                handlePointerMove(event);
                break;
            case POINTER_CANCEL:
                handlePointerCancel(event);
                break;
            case SCROLL:
                handleScroll(event);
                break;
            case KEY_DOWN:
            case KEY_UP:
            case KEY_PRESS:
                handleKeyEvent(event);
                break;
        }
        
        dispatchEvent(event);
        return true;
    }

    private void handlePointerDown(UIEvent event) {
        UIComponent component = findComponentAt(event.x, event.y);
        event.target = component;
        
        if (component != null) {
            PointerListener listener = pointerListeners.get(component);
            if (listener != null) {
                listener.onPointerDown(event);
            }
        }
        
        hoveredComponent = component;
        lastClickTime = System.currentTimeMillis();
        lastClickX = event.x;
        lastClickY = event.y;
        lastDragStartTime = lastClickTime;
        draggedComponent = component;
    }

    private void handlePointerMove(UIEvent event) {
        UIComponent component = findComponentAt(event.x, event.y);
        event.target = component;
        
        if (hoveredComponent != component) {
            if (hoveredComponent != null) {
                UIEvent exitEvent = new UIEvent(EventType.HOVER_EXIT, hoveredComponent);
                exitEvent.x = event.x;
                exitEvent.y = event.y;
                dispatchEvent(exitEvent);
            }
            
            hoveredComponent = component;
            
            if (component != null) {
                UIEvent enterEvent = new UIEvent(EventType.HOVER_ENTER, component);
                enterEvent.x = event.x;
                enterEvent.y = event.y;
                dispatchEvent(enterEvent);
            }
        }
        
        if (draggedComponent != null) {
            float dx = Math.abs(event.x - lastClickX);
            float dy = Math.abs(event.y - lastClickY);
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            
            if (distance > dragThreshold) {
                UIEvent dragEvent = new UIEvent(EventType.DRAG_MOVE, draggedComponent);
                dragEvent.x = event.x;
                dragEvent.y = event.y;
                dispatchEvent(dragEvent);
            }
        }
        
        PointerListener listener = pointerListeners.get(component);
        if (listener != null) {
            listener.onPointerMove(event);
        }
    }

    private void handlePointerUp(UIEvent event) {
        UIComponent component = findComponentAt(event.x, event.y);
        event.target = component;
        
        if (pointerListeners.containsKey(component)) {
            pointerListeners.get(component).onPointerUp(event);
        }
        
        if (draggedComponent != null) {
            float dx = Math.abs(event.x - lastClickX);
            float dy = Math.abs(event.y - lastClickY);
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            
            if (distance > dragThreshold) {
                UIEvent dragEndEvent = new UIEvent(EventType.DRAG_END, draggedComponent);
                dragEndEvent.x = event.x;
                dragEndEvent.y = event.y;
                dispatchEvent(dragEndEvent);
            } else {
                if (lastClickedComponent == component && 
                    System.currentTimeMillis() - lastClickTime < doubleClickThreshold) {
                    UIEvent doubleClickEvent = new UIEvent(EventType.DOUBLE_CLICK, component);
                    doubleClickEvent.x = event.x;
                    doubleClickEvent.y = event.y;
                    dispatchEvent(doubleClickEvent);
                } else {
                    UIEvent clickEvent = new UIEvent(EventType.CLICK, component);
                    clickEvent.x = event.x;
                    clickEvent.y = event.y;
                    dispatchEvent(clickEvent);
                    
                    ClickListener clickListener = clickListeners.get(component);
                    if (clickListener != null) {
                        clickListener.onClick(clickEvent);
                    }
                }
                
                lastClickedComponent = component;
                lastClickTime = System.currentTimeMillis();
            }
        }
        
        draggedComponent = null;
    }

    private void handlePointerCancel(UIEvent event) {
        UIComponent component = findComponentAt(event.x, event.y);
        event.target = component;
        
        PointerListener listener = pointerListeners.get(component);
        if (listener != null) {
            listener.onPointerCancel(event);
        }
        
        if (draggedComponent != null) {
            UIEvent dragEndEvent = new UIEvent(EventType.DRAG_END, draggedComponent);
            dragEndEvent.x = event.x;
            dragEndEvent.y = event.y;
            dispatchEvent(dragEndEvent);
            draggedComponent = null;
        }
    }

    private void handleScroll(UIEvent event) {
        UIComponent component = findComponentAt(event.x, event.y);
        event.target = component;
        
        ScrollListener listener = scrollListeners.get(component);
        if (listener != null) {
            listener.onScroll(event);
        }
    }

    private void handleKeyEvent(UIEvent event) {
        for (Map.Entry<UIComponent, KeyListener> entry : keyListeners.entrySet()) {
            KeyListener listener = entry.getValue();
            switch (event.type) {
                case KEY_DOWN:
                    listener.onKeyDown(event);
                    break;
                case KEY_UP:
                    listener.onKeyUp(event);
                    break;
                case KEY_PRESS:
                    listener.onKeyPress(event);
                    break;
            }
        }
    }

    private UIComponent findComponentAt(float x, float y) {
        if (canvas == null) return null;
        
        for (UIComponent component : canvas.getUIComponents()) {
            if (component.isVisible() && component.isInteractable()) {
                Rect bounds = component.getBounds();
                if (bounds != null && bounds.contains((int) x, (int) y)) {
                    return component;
                }
            }
        }
        return null;
    }

    private void dispatchEvent(UIEvent event) {
        if (event.target == null) return;
        
        List<EventListener> listeners = eventListeners.get(event.target);
        if (listeners != null) {
            for (EventListener listener : listeners) {
                listener.onEvent(event);
            }
        }
    }

    public void addEventListener(UIComponent component, EventListener listener) {
        eventListeners.computeIfAbsent(component, k -> new ArrayList<>()).add(listener);
    }

    public void removeEventListener(UIComponent component, EventListener listener) {
        List<EventListener> listeners = eventListeners.get(component);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    public void addPointerListener(UIComponent component, PointerListener listener) {
        pointerListeners.put(component, listener);
    }

    public void removePointerListener(UIComponent component) {
        pointerListeners.remove(component);
    }

    public void addClickListener(UIComponent component, ClickListener listener) {
        clickListeners.put(component, listener);
    }

    public void removeClickListener(UIComponent component) {
        clickListeners.remove(component);
    }

    public void addHoverListener(UIComponent component, HoverListener listener) {
        hoverListeners.put(component, listener);
    }

    public void removeHoverListener(UIComponent component) {
        hoverListeners.remove(component);
    }

    public void addDragListener(UIComponent component, DragListener listener) {
        dragListeners.put(component, listener);
    }

    public void removeDragListener(UIComponent component) {
        dragListeners.remove(component);
    }

    public void addScrollListener(UIComponent component, ScrollListener listener) {
        scrollListeners.put(component, listener);
    }

    public void removeScrollListener(UIComponent component) {
        scrollListeners.remove(component);
    }

    public void addKeyListener(UIComponent component, KeyListener listener) {
        keyListeners.put(component, listener);
    }

    public void removeKeyListener(UIComponent component) {
        keyListeners.remove(component);
    }

    public UIComponent getHoveredComponent() {
        return hoveredComponent;
    }

    public UIComponent getDraggedComponent() {
        return draggedComponent;
    }

    public float getDragThreshold() {
        return dragThreshold;
    }

    public void setDragThreshold(float dragThreshold) {
        this.dragThreshold = dragThreshold;
    }

    public float getDoubleClickThreshold() {
        return doubleClickThreshold;
    }

    public void setDoubleClickThreshold(float doubleClickThreshold) {
        this.doubleClickThreshold = doubleClickThreshold;
    }

    public void clear() {
        eventListeners.clear();
        pointerListeners.clear();
        clickListeners.clear();
        hoverListeners.clear();
        dragListeners.clear();
        scrollListeners.clear();
        keyListeners.clear();
        hoveredComponent = null;
        draggedComponent = null;
    }
}

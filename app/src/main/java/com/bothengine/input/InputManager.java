package com.bothengine.input;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.bothengine.core.EngineConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * InputManager
 * Manages all input from touch, keyboard, and other input devices
 */
public class InputManager implements View.OnTouchListener, View.OnKeyListener {
    private static final String TAG = "InputManager";
    
    private final EngineConfig config;
    private final Map<Integer, Boolean> keysDown;
    private final Map<Integer, Boolean> keysPressed;
    private final Map<Integer, Boolean> keysReleased;
    
    private final Map<Integer, TouchPoint> touchPoints;
    private final List<TouchPoint> activeTouches;
    private final List<TouchEvent> touchEvents;
    
    private Vector2 mousePosition;
    private boolean mouseLeftDown;
    private boolean mouseRightDown;
    private boolean mouseMiddleDown;
    
    private float scrollWheel;
    
    // Accelerometer data
    private Vector3 acceleration;
    private Vector3 gravity;
    private Vector3 linearAcceleration;
    
    // Gyroscope data
    private Vector3 rotationRate;
    
    // Touch state
    private int touchCount;
    private boolean multiTouchEnabled;
    
    // Input listeners
    private final List<InputListener> listeners;
    
    // Virtual joystick
    private VirtualJoystick leftJoystick;
    private VirtualJoystick rightJoystick;
    
    public InputManager(EngineConfig config) {
        this.config = config;
        this.keysDown = new HashMap<>();
        this.keysPressed = new HashMap<>();
        this.keysReleased = new HashMap<>();
        this.touchPoints = new HashMap<>();
        this.activeTouches = new ArrayList<>();
        this.touchEvents = new ArrayList<>();
        this.mousePosition = new Vector2(0, 0);
        this.acceleration = new Vector3(0, 0, 0);
        this.gravity = new Vector3(0, 0, 0);
        this.linearAcceleration = new Vector3(0, 0, 0);
        this.rotationRate = new Vector3(0, 0, 0);
        this.touchCount = 0;
        this.multiTouchEnabled = true;
        this.listeners = new ArrayList<>();
        this.leftJoystick = new VirtualJoystick();
        this.rightJoystick = new VirtualJoystick();
        
        Log.i(TAG, "InputManager initialized");
    }
    
    /**
     * Initialize the input manager with a view
     */
    public void initialize(View view) {
        view.setOnTouchListener(this);
        view.setOnKeyListener(this);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        
        Log.i(TAG, "InputManager initialized with view");
    }
    
    /**
     * Update the input manager (called every frame)
     */
    public void update(float deltaTime) {
        // Clear pressed and released states
        keysPressed.clear();
        keysReleased.clear();
        touchEvents.clear();
        
        // Update touch points
        for (Map.Entry<Integer, TouchPoint> entry : touchPoints.entrySet()) {
            TouchPoint touch = entry.getValue();
            touch.update(deltaTime);
            
            if (touch.isActive()) {
                if (!activeTouches.contains(touch)) {
                    activeTouches.add(touch);
                }
            } else {
                activeTouches.remove(touch);
            }
        }
        
        // Update virtual joysticks
        leftJoystick.update(deltaTime);
        rightJoystick.update(deltaTime);
        
        // Notify listeners
        for (InputListener listener : listeners) {
            listener.onInputUpdate(this, deltaTime);
        }
    }
    
    /**
     * Handle touch events
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getActionMasked();
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);
        
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);
        
        // Create or update touch point
        TouchPoint touch = touchPoints.get(pointerId);
        if (touch == null) {
            touch = new TouchPoint(pointerId);
            touchPoints.put(pointerId, touch);
        }
        
        // Update touch state
        touch.setPosition(x, y);
        touch.setScreenPosition(x, y);
        
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                touch.setActive(true);
                touch.setPressed(true);
                touch.setDownTime(event.getEventTime());
                touchCount++;
                
                // Create touch event
                touchEvents.add(new TouchEvent(TouchEventType.DOWN, pointerId, x, y, 0, 0));
                
                // Notify listeners
                for (InputListener listener : listeners) {
                    listener.onTouchDown(pointerId, x, y);
                }
                
                break;
                
            case MotionEvent.ACTION_MOVE:
                // Update all active touches
                for (int i = 0; i < event.getPointerCount(); i++) {
                    int pid = event.getPointerId(i);
                    TouchPoint t = touchPoints.get(pid);
                    if (t != null) {
                        float px = event.getX(i);
                        float py = event.getY(i);
                        float dx = px - t.getX();
                        float dy = py - t.getY();
                        
                        t.setPosition(px, py);
                        t.setDelta(dx, dy);
                        t.setMoved(true);
                        
                        // Create touch event
                        touchEvents.add(new TouchEvent(TouchEventType.MOVE, pid, px, py, dx, dy));
                        
                        // Notify listeners
                        for (InputListener listener : listeners) {
                            listener.onTouchMove(pid, px, py, dx, dy);
                        }
                    }
                }
                
                break;
                
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                touch.setActive(false);
                touch.setReleased(true);
                touchCount--;
                
                // Create touch event
                touchEvents.add(new TouchEvent(TouchEventType.UP, pointerId, x, y, 0, 0));
                
                // Notify listeners
                for (InputListener listener : listeners) {
                    listener.onTouchUp(pointerId, x, y);
                }
                
                break;
                
            case MotionEvent.ACTION_CANCEL:
                // Cancel all touches
                for (TouchPoint t : touchPoints.values()) {
                    t.setActive(false);
                    t.setReleased(true);
                }
                touchCount = 0;
                activeTouches.clear();
                
                // Notify listeners
                for (InputListener listener : listeners) {
                    listener.onTouchCancel();
                }
                
                break;
        }
        
        // Update mouse position
        if (event.getPointerCount() > 0) {
            mousePosition.x = event.getX(0);
            mousePosition.y = event.getY(0);
        }
        
        return true;
    }
    
    /**
     * Handle key events
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        boolean handled = false;
        
        switch (event.getAction()) {
            case KeyEvent.ACTION_DOWN:
                keysDown.put(keyCode, true);
                keysPressed.put(keyCode, true);
                handled = true;
                
                // Notify listeners
                for (InputListener listener : listeners) {
                    if (listener.onKeyDown(keyCode)) {
                        handled = true;
                    }
                }
                
                break;
                
            case KeyEvent.ACTION_UP:
                keysDown.remove(keyCode);
                keysReleased.put(keyCode, true);
                handled = true;
                
                // Notify listeners
                for (InputListener listener : listeners) {
                    if (listener.onKeyUp(keyCode)) {
                        handled = true;
                    }
                }
                
                break;
        }
        
        return handled;
    }
    
    /**
     * Handle sensor events (accelerometer, gyroscope)
     */
    public void onSensorEvent(int sensorType, float[] values) {
        switch (sensorType) {
            case android.hardware.Sensor.TYPE_ACCELEROMETER:
                if (values.length >= 3) {
                    acceleration.x = values[0];
                    acceleration.y = values[1];
                    acceleration.z = values[2];
                }
                break;
                
            case android.hardware.Sensor.TYPE_GRAVITY:
                if (values.length >= 3) {
                    gravity.x = values[0];
                    gravity.y = values[1];
                    gravity.z = values[2];
                }
                break;
                
            case android.hardware.Sensor.TYPE_LINEAR_ACCELERATION:
                if (values.length >= 3) {
                    linearAcceleration.x = values[0];
                    linearAcceleration.y = values[1];
                    linearAcceleration.z = values[2];
                }
                break;
                
            case android.hardware.Sensor.TYPE_GYROSCOPE:
                if (values.length >= 3) {
                    rotationRate.x = values[0];
                    rotationRate.y = values[1];
                    rotationRate.z = values[2];
                }
                break;
        }
        
        // Notify listeners
        for (InputListener listener : listeners) {
            listener.onSensorEvent(sensorType, values);
        }
    }
    
    /**
     * Add an input listener
     */
    public void addListener(InputListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Remove an input listener
     */
    public void removeListener(InputListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Shutdown the input manager
     */
    public void shutdown() {
        keysDown.clear();
        keysPressed.clear();
        keysReleased.clear();
        touchPoints.clear();
        activeTouches.clear();
        touchEvents.clear();
        listeners.clear();
        
        leftJoystick = null;
        rightJoystick = null;
        
        Log.i(TAG, "InputManager shutdown");
    }
    
    // Key input methods
    
    public boolean isKeyDown(int keyCode) {
        return keysDown.containsKey(keyCode);
    }
    
    public boolean isKeyPressed(int keyCode) {
        return keysPressed.containsKey(keyCode);
    }
    
    public boolean isKeyReleased(int keyCode) {
        return keysReleased.containsKey(keyCode);
    }
    
    // Touch input methods
    
    public boolean isTouchDown(int pointerId) {
        TouchPoint touch = touchPoints.get(pointerId);
        return touch != null && touch.isActive();
    }
    
    public boolean isTouchPressed(int pointerId) {
        TouchPoint touch = touchPoints.get(pointerId);
        return touch != null && touch.isPressed();
    }
    
    public boolean isTouchReleased(int pointerId) {
        TouchPoint touch = touchPoints.get(pointerId);
        return touch != null && touch.isReleased();
    }
    
    public TouchPoint getTouchPoint(int pointerId) {
        return touchPoints.get(pointerId);
    }
    
    public List<TouchPoint> getActiveTouches() {
        return new ArrayList<>(activeTouches);
    }
    
    public List<TouchEvent> getTouchEvents() {
        return new ArrayList<>(touchEvents);
    }
    
    public int getTouchCount() {
        return touchCount;
    }
    
    // Mouse input methods
    
    public Vector2 getMousePosition() {
        return mousePosition;
    }
    
    public void setMousePosition(Vector2 position) {
        this.mousePosition = position;
    }
    
    public boolean isMouseLeftDown() {
        return mouseLeftDown;
    }
    
    public boolean isMouseRightDown() {
        return mouseRightDown;
    }
    
    public boolean isMouseMiddleDown() {
        return mouseMiddleDown;
    }
    
    public float getScrollWheel() {
        return scrollWheel;
    }
    
    public void setScrollWheel(float scrollWheel) {
        this.scrollWheel = scrollWheel;
    }
    
    // Sensor input methods
    
    public Vector3 getAcceleration() {
        return acceleration;
    }
    
    public Vector3 getGravity() {
        return gravity;
    }
    
    public Vector3 getLinearAcceleration() {
        return linearAcceleration;
    }
    
    public Vector3 getRotationRate() {
        return rotationRate;
    }
    
    // Virtual joystick methods
    
    public VirtualJoystick getLeftJoystick() {
        return leftJoystick;
    }
    
    public VirtualJoystick getRightJoystick() {
        return rightJoystick;
    }
    
    public boolean isMultiTouchEnabled() {
        return multiTouchEnabled;
    }
    
    public void setMultiTouchEnabled(boolean multiTouchEnabled) {
        this.multiTouchEnabled = multiTouchEnabled;
    }
    
    @Override
    public String toString() {
        return "InputManager{" +
                "touchCount=" + touchCount +
                ", activeTouches=" + activeTouches.size() +
                ", mousePosition=" + mousePosition +
                '}';
    }
    
    /**
     * TouchPoint class
     */
    public static class TouchPoint {
        private final int pointerId;
        private Vector2 position;
        private Vector2 screenPosition;
        private Vector2 startPosition;
        private Vector2 delta;
        private boolean active;
        private boolean pressed;
        private boolean released;
        private boolean moved;
        private long downTime;
        private float pressure;
        private float size;
        
        public TouchPoint(int pointerId) {
            this.pointerId = pointerId;
            this.position = new Vector2(0, 0);
            this.screenPosition = new Vector2(0, 0);
            this.startPosition = new Vector2(0, 0);
            this.delta = new Vector2(0, 0);
            this.active = false;
            this.pressed = false;
            this.released = false;
            this.moved = false;
            this.downTime = 0;
            this.pressure = 0;
            this.size = 0;
        }
        
        public void update(float deltaTime) {
            // Reset transient states
            pressed = false;
            released = false;
            moved = false;
        }
        
        // Getters and setters
        
        public int getPointerId() {
            return pointerId;
        }
        
        public Vector2 getPosition() {
            return position;
        }
        
        public void setPosition(float x, float y) {
            this.position = new Vector2(x, y);
        }
        
        public Vector2 getScreenPosition() {
            return screenPosition;
        }
        
        public void setScreenPosition(float x, float y) {
            this.screenPosition = new Vector2(x, y);
        }
        
        public Vector2 getStartPosition() {
            return startPosition;
        }
        
        public void setStartPosition(Vector2 startPosition) {
            this.startPosition = startPosition;
        }
        
        public Vector2 getDelta() {
            return delta;
        }
        
        public void setDelta(float x, float y) {
            this.delta = new Vector2(x, y);
        }
        
        public float getX() {
            return position.x;
        }
        
        public float getY() {
            return position.y;
        }
        
        public boolean isActive() {
            return active;
        }
        
        public void setActive(boolean active) {
            this.active = active;
            if (active) {
                startPosition = new Vector2(position);
            }
        }
        
        public boolean isPressed() {
            return pressed;
        }
        
        public void setPressed(boolean pressed) {
            this.pressed = pressed;
        }
        
        public boolean isReleased() {
            return released;
        }
        
        public void setReleased(boolean released) {
            this.released = released;
        }
        
        public boolean isMoved() {
            return moved;
        }
        
        public void setMoved(boolean moved) {
            this.moved = moved;
        }
        
        public long getDownTime() {
            return downTime;
        }
        
        public void setDownTime(long downTime) {
            this.downTime = downTime;
        }
        
        public float getPressure() {
            return pressure;
        }
        
        public void setPressure(float pressure) {
            this.pressure = pressure;
        }
        
        public float getSize() {
            return size;
        }
        
        public void setSize(float size) {
            this.size = size;
        }
        
        @Override
        public String toString() {
            return "TouchPoint{" +
                    "pointerId=" + pointerId +
                    ", position=" + position +
                    ", active=" + active +
                    '}';
        }
    }
    
    /**
     * TouchEvent class
     */
    public static class TouchEvent {
        private final TouchEventType type;
        private final int pointerId;
        private final float x;
        private final float y;
        private final float deltaX;
        private final float deltaY;
        
        public TouchEvent(TouchEventType type, int pointerId, float x, float y, float deltaX, float deltaY) {
            this.type = type;
            this.pointerId = pointerId;
            this.x = x;
            this.y = y;
            this.deltaX = deltaX;
            this.deltaY = deltaY;
        }
        
        public TouchEventType getType() {
            return type;
        }
        
        public int getPointerId() {
            return pointerId;
        }
        
        public float getX() {
            return x;
        }
        
        public float getY() {
            return y;
        }
        
        public float getDeltaX() {
            return deltaX;
        }
        
        public float getDeltaY() {
            return deltaY;
        }
        
        @Override
        public String toString() {
            return "TouchEvent{" +
                    "type=" + type +
                    ", pointerId=" + pointerId +
                    ", x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
    
    /**
     * Touch event type enum
     */
    public enum TouchEventType {
        DOWN,
        UP,
        MOVE,
        CANCEL
    }
    
    /**
     * InputListener interface
     */
    public interface InputListener {
        default void onInputUpdate(InputManager inputManager, float deltaTime) {}
        default boolean onKeyDown(int keyCode) { return false; }
        default boolean onKeyUp(int keyCode) { return false; }
        default void onTouchDown(int pointerId, float x, float y) {}
        default void onTouchUp(int pointerId, float x, float y) {}
        default void onTouchMove(int pointerId, float x, float y, float deltaX, float deltaY) {}
        default void onTouchCancel() {}
        default void onSensorEvent(int sensorType, float[] values) {}
    }
    
    /**
     * VirtualJoystick class
     */
    public static class VirtualJoystick {
        private boolean enabled;
        private Vector2 position;
        private Vector2 size;
        private Vector2 stickPosition;
        private float deadZone;
        private boolean visible;
        private String name;
        
        public VirtualJoystick() {
            this.name = "Joystick";
            this.enabled = true;
            this.position = new Vector2(100, 100);
            this.size = new Vector2(200, 200);
            this.stickPosition = new Vector2(0, 0);
            this.deadZone = 0.1f;
            this.visible = true;
        }
        
        public VirtualJoystick(String name) {
            this();
            this.name = name;
        }
        
        /**
         * Update the joystick
         */
        public void update(float deltaTime) {
            // Update stick position based on touch input
            // This would be called from the InputManager
        }
        
        /**
         * Set stick position from touch
         */
        public void setStickPosition(float x, float y) {
            // Normalize position relative to joystick center
            float halfWidth = size.x / 2;
            float halfHeight = size.y / 2;
            
            float nx = (x - position.x - halfWidth) / halfWidth;
            float ny = (y - position.y - halfHeight) / halfHeight;
            
            // Clamp to circle
            float length = (float) Math.sqrt(nx * nx + ny * ny);
            if (length > 1) {
                nx /= length;
                ny /= length;
            }
            
            stickPosition = new Vector2(nx, ny);
        }
        
        /**
         * Reset stick position
         */
        public void reset() {
            stickPosition = new Vector2(0, 0);
        }
        
        /**
         * Get the joystick value
         */
        public Vector2 getValue() {
            // Apply dead zone
            float length = stickPosition.length();
            if (length < deadZone) {
                return new Vector2(0, 0);
            }
            
            // Normalize
            return stickPosition.normalized();
        }
        
        /**
         * Get the raw stick position
         */
        public Vector2 getStickPosition() {
            return stickPosition;
        }
        
        // Getters and setters
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public Vector2 getPosition() {
            return position;
        }
        
        public void setPosition(Vector2 position) {
            this.position = position;
        }
        
        public void setPosition(float x, float y) {
            this.position = new Vector2(x, y);
        }
        
        public Vector2 getSize() {
            return size;
        }
        
        public void setSize(Vector2 size) {
            this.size = size;
        }
        
        public void setSize(float width, float height) {
            this.size = new Vector2(width, height);
        }
        
        public float getDeadZone() {
            return deadZone;
        }
        
        public void setDeadZone(float deadZone) {
            this.deadZone = deadZone;
        }
        
        public boolean isVisible() {
            return visible;
        }
        
        public void setVisible(boolean visible) {
            this.visible = visible;
        }
        
        @Override
        public String toString() {
            return "VirtualJoystick{" +
                    "name='" + name + '\'' +
                    ", position=" + position +
                    ", stickPosition=" + stickPosition +
                    '}';
        }
    }
}

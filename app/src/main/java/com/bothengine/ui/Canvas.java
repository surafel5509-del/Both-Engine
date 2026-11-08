package com.bothengine.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.bothengine.core.ecs.Entity;
import com.bothengine.core.ecs.EntityManager;

import java.util.ArrayList;
import java.util.List;

public class Canvas {
    private final View view;
    private final EntityManager entityManager;
    private final List<UIComponent> uiComponents;
    private Paint defaultPaint;
    private boolean dirty;
    private int width;
    private int height;
    private CanvasRenderer renderer;

    public Canvas(View view, EntityManager entityManager) {
        this.view = view;
        this.entityManager = entityManager;
        this.uiComponents = new ArrayList<>();
        this.dirty = true;
        this.width = view.getWidth();
        this.height = view.getHeight();
        
        this.defaultPaint = new Paint();
        this.defaultPaint.setAntiAlias(true);
        this.defaultPaint.setFilterBitmap(true);
        this.defaultPaint.setDither(true);
        
        this.renderer = new CanvasRenderer();
    }

    public void initialize() {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return handleTouch(event);
            }
        });
    }

    public void addUIComponent(UIComponent component) {
        uiComponents.add(component);
        component.setCanvas(this);
        dirty = true;
    }

    public void removeUIComponent(UIComponent component) {
        uiComponents.remove(component);
        component.setCanvas(null);
        dirty = true;
    }

    public void clear() {
        uiComponents.clear();
        dirty = true;
    }

    public void update(float deltaTime) {
        for (UIComponent component : uiComponents) {
            component.update(deltaTime);
        }
    }

    public void render(android.graphics.Canvas androidCanvas) {
        if (dirty) {
            sortComponents();
            dirty = false;
        }
        
        androidCanvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
        
        for (UIComponent component : uiComponents) {
            if (component.isVisible()) {
                component.render(androidCanvas, defaultPaint);
            }
        }
    }

    private void sortComponents() {
        uiComponents.sort((a, b) -> Integer.compare(a.getRenderOrder(), b.getRenderOrder()));
    }

    private boolean handleTouch(MotionEvent event) {
        int action = event.getActionMasked();
        float x = event.getX();
        float y = event.getY();
        
        for (int i = uiComponents.size() - 1; i >= 0; i--) {
            UIComponent component = uiComponents.get(i);
            if (component.isVisible() && component.isInteractable()) {
                Rect bounds = component.getBounds();
                if (bounds != null && bounds.contains((int) x, (int) y)) {
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_POINTER_DOWN:
                            component.onPointerDown(x, y, event.getPointerId(event.getActionIndex()));
                            break;
                        case MotionEvent.ACTION_MOVE:
                            component.onPointerMove(x, y, event.getPointerId(event.getActionIndex()));
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_POINTER_UP:
                            component.onPointerUp(x, y, event.getPointerId(event.getActionIndex()));
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            component.onPointerCancel();
                            break;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public View getView() {
        return view;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        dirty = true;
    }

    public Paint getDefaultPaint() {
        return defaultPaint;
    }

    public List<UIComponent> getUIComponents() {
        return new ArrayList<>(uiComponents);
    }

    public UIComponent findComponentByName(String name) {
        for (UIComponent component : uiComponents) {
            if (name.equals(component.getName())) {
                return component;
            }
        }
        return null;
    }

    public UIComponent findComponentByEntity(Entity entity) {
        for (UIComponent component : uiComponents) {
            if (entity.equals(component.getEntity())) {
                return component;
            }
        }
        return null;
    }

    public void markDirty() {
        dirty = true;
    }

    public static class CanvasRenderer {
        public void render(Canvas canvas, android.graphics.Canvas androidCanvas) {
            canvas.render(androidCanvas);
        }
    }
}

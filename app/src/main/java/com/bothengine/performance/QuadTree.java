package com.bothengine.performance;

import com.bothengine.rendering.Bounds;
import com.bothengine.rendering.Vector2;
import com.bothengine.rendering.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * QuadTree
 * 2D spatial partitioning data structure
 * Divides space into 4 quadrants for efficient collision and visibility testing
 */
public class QuadTree {
    private static final int DEFAULT_MAX_DEPTH = 8;
    private static final int DEFAULT_MAX_ITEMS = 10;
    
    private Bounds bounds;
    private int maxDepth;
    private int maxItems;
    private int depth;
    
    private List<Object> items;
    private QuadTree[] children;
    
    public QuadTree(Bounds bounds) {
        this(bounds, DEFAULT_MAX_DEPTH, DEFAULT_MAX_ITEMS);
    }
    
    public QuadTree(Bounds bounds, int maxDepth, int maxItems) {
        this.bounds = bounds;
        this.maxDepth = maxDepth;
        this.maxItems = maxItems;
        this.depth = 0;
        this.items = new ArrayList<>();
        this.children = null;
    }
    
    /**
     * Create a quadtree with a center and size
     */
    public static QuadTree create(Vector2 center, Vector2 size) {
        Vector3 min = new Vector3(center.x - size.x / 2, 0, center.y - size.y / 2);
        Vector3 max = new Vector3(center.x + size.x / 2, 0, center.y + size.y / 2);
        Bounds bounds = new Bounds(min, max);
        return new QuadTree(bounds);
    }
    
    /**
     * Create a quadtree with a center and uniform size
     */
    public static QuadTree create(Vector2 center, float size) {
        return create(center, new Vector2(size, size));
    }
    
    /**
     * Insert an item into the quadtree
     */
    public void insert(Object item, Bounds itemBounds) {
        // If we have children, try to insert into them
        if (children != null) {
            int index = getQuadrant(itemBounds);
            if (index >= 0) {
                children[index].insert(item, itemBounds);
                return;
            }
        }
        
        // Add to this node
        items.add(item);
        
        // Check if we need to split
        if (children == null && items.size() > maxItems && depth < maxDepth) {
            split();
            
            // Redistribute items to children
            List<Object> toRedistribute = new ArrayList<>(items);
            items.clear();
            
            for (Object obj : toRedistribute) {
                // We need to get the bounds of the object
                // For now, we'll assume the itemBounds is the same for all items
                int index = getQuadrant(itemBounds);
                if (index >= 0) {
                    children[index].insert(obj, itemBounds);
                } else {
                    items.add(obj);
                }
            }
        }
    }
    
    /**
     * Remove an item from the quadtree
     */
    public boolean remove(Object item, Bounds itemBounds) {
        // If we have children, try to remove from them
        if (children != null) {
            int index = getQuadrant(itemBounds);
            if (index >= 0) {
                return children[index].remove(item, itemBounds);
            }
        }
        
        // Remove from this node
        return items.remove(item);
    }
    
    /**
     * Clear all items from the quadtree
     */
    public void clear() {
        items.clear();
        
        if (children != null) {
            for (QuadTree child : children) {
                child.clear();
            }
            children = null;
        }
    }
    
    /**
     * Query items in the quadtree that intersect with the given bounds
     */
    public List<Object> query(Bounds queryBounds) {
        List<Object> result = new ArrayList<>();
        query(queryBounds, result);
        return result;
    }
    
    /**
     * Query items in the quadtree that intersect with the given bounds
     */
    private void query(Bounds queryBounds, List<Object> result) {
        // Check if this node's bounds intersect with query bounds
        if (!bounds.intersects(queryBounds)) {
            return;
        }
        
        // Add items in this node
        result.addAll(items);
        
        // Query children
        if (children != null) {
            for (QuadTree child : children) {
                child.query(queryBounds, result);
            }
        }
    }
    
    /**
     * Query items at a specific point
     */
    public List<Object> query(Vector2 point) {
        List<Object> result = new ArrayList<>();
        query(new Vector3(point.x, 0, point.y), result);
        return result;
    }
    
    /**
     * Query items at a specific point
     */
    private void query(Vector3 point, List<Object> result) {
        // Check if point is in this node's bounds
        if (!bounds.contains(point)) {
            return;
        }
        
        // Add items in this node
        result.addAll(items);
        
        // Query children
        if (children != null) {
            int index = getQuadrant(point);
            if (index >= 0) {
                children[index].query(point, result);
            }
        }
    }
    
    /**
     * Query items in a circle
     */
    public List<Object> queryCircle(Vector2 center, float radius) {
        List<Object> result = new ArrayList<>();
        
        // Create bounds for the circle
        Vector3 min = new Vector3(center.x - radius, 0, center.y - radius);
        Vector3 max = new Vector3(center.x + radius, 0, center.y + radius);
        Bounds circleBounds = new Bounds(min, max);
        
        query(circleBounds, result);
        
        // Filter to only include items within the actual circle radius
        result.removeIf(item -> {
            // Get the position of the item
            // For now, we'll assume the item has a position
            return false;
        });
        
        return result;
    }
    
    /**
     * Split the quadtree into 4 children
     */
    private void split() {
        if (children != null) return;
        
        Vector3 center = bounds.getCenter();
        Vector3 min = bounds.getMin();
        Vector3 max = bounds.getMax();
        
        children = new QuadTree[4];
        
        // Create 4 children quadrants
        children[0] = new QuadTree(new Bounds(min, center), maxDepth, maxItems);
        children[1] = new QuadTree(new Bounds(new Vector3(center.x, min.y, min.z), new Vector3(max.x, center.y, center.z)), maxDepth, maxItems);
        children[2] = new QuadTree(new Bounds(new Vector3(min.x, center.y, min.z), new Vector3(center.x, max.y, center.z)), maxDepth, maxItems);
        children[3] = new QuadTree(new Bounds(center, max), maxDepth, maxItems);
        
        // Set depth for children
        for (QuadTree child : children) {
            child.depth = depth + 1;
        }
    }
    
    /**
     * Get the quadrant index for a bounds
     */
    private int getQuadrant(Bounds bounds) {
        Vector3 center = this.bounds.getCenter();
        
        int index = 0;
        
        if (bounds.getMax().x > center.x) index |= 1;
        if (bounds.getMax().z > center.z) index |= 2;
        
        return index;
    }
    
    /**
     * Get the quadrant index for a point
     */
    private int getQuadrant(Vector3 point) {
        Vector3 center = bounds.getCenter();
        
        int index = 0;
        
        if (point.x > center.x) index |= 1;
        if (point.z > center.z) index |= 2;
        
        return index;
    }
    
    // Getters and setters
    
    public Bounds getBounds() {
        return bounds;
    }
    
    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }
    
    public int getMaxDepth() {
        return maxDepth;
    }
    
    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }
    
    public int getMaxItems() {
        return maxItems;
    }
    
    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }
    
    public int getDepth() {
        return depth;
    }
    
    public List<Object> getItems() {
        return new ArrayList<>(items);
    }
    
    public QuadTree[] getChildren() {
        return children != null ? children.clone() : null;
    }
    
    public boolean hasChildren() {
        return children != null;
    }
    
    public int getItemCount() {
        int count = items.size();
        
        if (children != null) {
            for (QuadTree child : children) {
                count += child.getItemCount();
            }
        }
        
        return count;
    }
    
    @Override
    public String toString() {
        return "QuadTree{" +
                "bounds=" + bounds +
                ", depth=" + depth +
                ", items=" + items.size() +
                ", children=" + (children != null ? children.length : 0) +
                '}';
    }
}

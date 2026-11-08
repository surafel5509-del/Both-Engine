package com.bothengine.performance;

import com.bothengine.rendering.Bounds;
import com.bothengine.rendering.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Octree
 * 3D spatial partitioning data structure
 * Divides space into 8 octants for efficient collision and visibility testing
 */
public class Octree {
    private static final int DEFAULT_MAX_DEPTH = 8;
    private static final int DEFAULT_MAX_ITEMS = 10;
    
    private Bounds bounds;
    private int maxDepth;
    private int maxItems;
    private int depth;
    
    private List<Object> items;
    private Octree[] children;
    
    public Octree(Bounds bounds) {
        this(bounds, DEFAULT_MAX_DEPTH, DEFAULT_MAX_ITEMS);
    }
    
    public Octree(Bounds bounds, int maxDepth, int maxItems) {
        this.bounds = bounds;
        this.maxDepth = maxDepth;
        this.maxItems = maxItems;
        this.depth = 0;
        this.items = new ArrayList<>();
        this.children = null;
    }
    
    /**
     * Create an octree with a center and size
     */
    public static Octree create(Vector3 center, Vector3 size) {
        Bounds bounds = new Bounds(center.subtract(size.multiply(0.5f)), center.add(size.multiply(0.5f)));
        return new Octree(bounds);
    }
    
    /**
     * Create an octree with a center and uniform size
     */
    public static Octree create(Vector3 center, float size) {
        return create(center, new Vector3(size, size, size));
    }
    
    /**
     * Insert an item into the octree
     */
    public void insert(Object item, Bounds itemBounds) {
        // If we have children, try to insert into them
        if (children != null) {
            int index = getOctant(itemBounds);
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
                int index = getOctant(itemBounds);
                if (index >= 0) {
                    children[index].insert(obj, itemBounds);
                } else {
                    items.add(obj);
                }
            }
        }
    }
    
    /**
     * Remove an item from the octree
     */
    public boolean remove(Object item, Bounds itemBounds) {
        // If we have children, try to remove from them
        if (children != null) {
            int index = getOctant(itemBounds);
            if (index >= 0) {
                return children[index].remove(item, itemBounds);
            }
        }
        
        // Remove from this node
        return items.remove(item);
    }
    
    /**
     * Clear all items from the octree
     */
    public void clear() {
        items.clear();
        
        if (children != null) {
            for (Octree child : children) {
                child.clear();
            }
            children = null;
        }
    }
    
    /**
     * Query items in the octree that intersect with the given bounds
     */
    public List<Object> query(Bounds queryBounds) {
        List<Object> result = new ArrayList<>();
        query(queryBounds, result);
        return result;
    }
    
    /**
     * Query items in the octree that intersect with the given bounds
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
            for (Octree child : children) {
                child.query(queryBounds, result);
            }
        }
    }
    
    /**
     * Query items at a specific point
     */
    public List<Object> query(Vector3 point) {
        List<Object> result = new ArrayList<>();
        query(point, result);
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
            int index = getOctant(point);
            if (index >= 0) {
                children[index].query(point, result);
            }
        }
    }
    
    /**
     * Query items in a ray
     */
    public List<Object> queryRay(Vector3 origin, Vector3 direction, float maxDistance) {
        List<Object> result = new ArrayList<>();
        queryRay(origin, direction, maxDistance, result);
        return result;
    }
    
    /**
     * Query items in a ray
     */
    private void queryRay(Vector3 origin, Vector3 direction, float maxDistance, List<Object> result) {
        // Check if ray intersects this node's bounds
        if (!bounds.intersectsRay(origin, direction, maxDistance)) {
            return;
        }
        
        // Add items in this node
        result.addAll(items);
        
        // Query children
        if (children != null) {
            for (Octree child : children) {
                child.queryRay(origin, direction, maxDistance, result);
            }
        }
    }
    
    /**
     * Split the octree into 8 children
     */
    private void split() {
        if (children != null) return;
        
        Vector3 center = bounds.getCenter();
        Vector3 min = bounds.getMin();
        Vector3 max = bounds.getMax();
        
        Vector3 size = bounds.getSize();
        Vector3 halfSize = size.multiply(0.5f);
        
        children = new Octree[8];
        
        // Create 8 children octants
        children[0] = new Octree(new Bounds(min, center), maxDepth, maxItems);
        children[1] = new Octree(new Bounds(new Vector3(center.x, min.y, min.z), new Vector3(max.x, center.y, center.z)), maxDepth, maxItems);
        children[2] = new Octree(new Bounds(new Vector3(min.x, min.y, center.z), new Vector3(center.x, center.y, max.z)), maxDepth, maxItems);
        children[3] = new Octree(new Bounds(new Vector3(center.x, min.y, center.z), new Vector3(max.x, center.y, max.z)), maxDepth, maxItems);
        children[4] = new Octree(new Bounds(new Vector3(min.x, center.y, min.z), new Vector3(center.x, max.y, center.z)), maxDepth, maxItems);
        children[5] = new Octree(new Bounds(new Vector3(center.x, center.y, min.z), max), maxDepth, maxItems);
        children[6] = new Octree(new Bounds(new Vector3(min.x, center.y, center.z), new Vector3(center.x, max.y, max.z)), maxDepth, maxItems);
        children[7] = new Octree(new Bounds(center, max), maxDepth, maxItems);
        
        // Set depth for children
        for (Octree child : children) {
            child.depth = depth + 1;
        }
    }
    
    /**
     * Get the octant index for a bounds
     */
    private int getOctant(Bounds bounds) {
        Vector3 center = this.bounds.getCenter();
        
        int index = 0;
        
        if (bounds.getMax().x > center.x) index |= 1;
        if (bounds.getMax().y > center.y) index |= 2;
        if (bounds.getMax().z > center.z) index |= 4;
        
        return index;
    }
    
    /**
     * Get the octant index for a point
     */
    private int getOctant(Vector3 point) {
        Vector3 center = bounds.getCenter();
        
        int index = 0;
        
        if (point.x > center.x) index |= 1;
        if (point.y > center.y) index |= 2;
        if (point.z > center.z) index |= 4;
        
        return index;
    }
    
    /**
     * Merge child octrees
     */
    private void merge() {
        if (children == null) return;
        
        // Collect all items from children
        for (Octree child : children) {
            items.addAll(child.items);
        }
        
        children = null;
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
    
    public Octree[] getChildren() {
        return children != null ? children.clone() : null;
    }
    
    public boolean hasChildren() {
        return children != null;
    }
    
    public int getItemCount() {
        int count = items.size();
        
        if (children != null) {
            for (Octree child : children) {
                count += child.getItemCount();
            }
        }
        
        return count;
    }
    
    @Override
    public String toString() {
        return "Octree{" +
                "bounds=" + bounds +
                ", depth=" + depth +
                ", items=" + items.size() +
                ", children=" + (children != null ? children.length : 0) +
                '}';
    }
}

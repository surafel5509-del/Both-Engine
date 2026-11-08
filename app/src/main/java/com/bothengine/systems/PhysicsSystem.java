package com.bothengine.systems;

import android.util.Log;

import com.bothengine.core.EngineConfig;
import com.bothengine.core.ecs.Entity;
import com.bothengine.core.ecs.EntityManager;
import com.bothengine.components.ColliderComponent;
import com.bothengine.components.RigidbodyComponent;
import com.bothengine.components.TransformComponent;
import com.bothengine.rendering.Vector3;

import java.util.List;

/**
 * PhysicsSystem
 * Handles physics simulation for entities with physics components
 */
public class PhysicsSystem {
    private static final String TAG = "PhysicsSystem";
    
    private final EngineConfig config;
    private EntityManager entityManager;
    
    private boolean enabled;
    private float gravity;
    private int maxIterations;
    private float fixedTimeStep;
    
    // Physics settings
    private boolean useContinuousCollisionDetection;
    private boolean useGravity;
    private boolean useFriction;
    private float defaultFriction;
    private float defaultRestitution;
    
    public PhysicsSystem(EngineConfig config) {
        this.config = config;
        this.entityManager = null;
        this.enabled = true;
        this.gravity = config.getPhysicsGravity();
        this.maxIterations = 10;
        this.fixedTimeStep = 1.0f / 60.0f;
        this.useContinuousCollisionDetection = true;
        this.useGravity = true;
        this.useFriction = true;
        this.defaultFriction = 0.2f;
        this.defaultRestitution = 0.5f;
        
        Log.i(TAG, "PhysicsSystem initialized");
    }
    
    /**
     * Initialize the system
     */
    public void initialize(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    /**
     * Update physics (variable time step)
     */
    public void update(float deltaTime) {
        if (!enabled || entityManager == null) return;
        
        // Simple physics update
        // In a real engine, this would use a proper physics engine like Box2D or Bullet
        
        // Get all entities with RigidbodyComponent
        List<Entity> entities = entityManager.getEntitiesWithComponent(RigidbodyComponent.class);
        
        for (Entity entity : entities) {
            if (entity.isActive()) {
                RigidbodyComponent rigidbody = entity.getComponent(RigidbodyComponent.class);
                TransformComponent transform = entity.getComponent(TransformComponent.class);
                ColliderComponent collider = entity.getComponent(ColliderComponent.class);
                
                if (rigidbody != null && transform != null && rigidbody.isEnabled()) {
                    // Apply gravity
                    if (useGravity) {
                        rigidbody.setVelocity(rigidbody.getVelocity().add(new Vector3(0, gravity * deltaTime, 0)));
                    }
                    
                    // Apply friction
                    if (useFriction && rigidbody.isOnGround()) {
                        Vector3 velocity = rigidbody.getVelocity();
                        float frictionFactor = 1.0f - defaultFriction * deltaTime;
                        velocity.x *= frictionFactor;
                        velocity.z *= frictionFactor;
                        rigidbody.setVelocity(velocity);
                    }
                    
                    // Update position
                    Vector3 position = transform.getPosition();
                    Vector3 newPosition = position.add(rigidbody.getVelocity().multiply(deltaTime));
                    transform.setPosition(newPosition);
                    
                    // Check for collisions
                    if (collider != null && collider.isEnabled()) {
                        checkCollisions(entity, collider, deltaTime);
                    }
                }
            }
        }
    }
    
    /**
     * Fixed update physics (fixed time step)
     */
    public void fixedUpdate(float fixedDeltaTime) {
        if (!enabled || entityManager == null) return;
        
        // Same as update but with fixed time step
        update(fixedDeltaTime);
    }
    
    /**
     * Check for collisions
     */
    private void checkCollisions(Entity entity, ColliderComponent collider, float deltaTime) {
        // Simple collision detection
        // In a real engine, this would use spatial partitioning and broad/narrow phase detection
        
        List<Entity> allEntities = entityManager.getEntities();
        
        for (Entity other : allEntities) {
            if (other == entity) continue;
            
            ColliderComponent otherCollider = other.getComponent(ColliderComponent.class);
            if (otherCollider != null && otherCollider.isEnabled()) {
                // Check if colliders intersect
                if (checkColliderCollision(collider, otherCollider)) {
                    // Handle collision
                    handleCollision(entity, other, collider, otherCollider);
                }
            }
        }
    }
    
    /**
     * Check if two colliders intersect
     */
    private boolean checkColliderCollision(ColliderComponent a, ColliderComponent b) {
        // Simple AABB collision detection
        Vector3 aMin = a.getBounds().getMin();
        Vector3 aMax = a.getBounds().getMax();
        Vector3 bMin = b.getBounds().getMin();
        Vector3 bMax = b.getBounds().getMax();
        
        return !(aMax.x < bMin.x || aMin.x > bMax.x ||
                 aMax.y < bMin.y || aMin.y > bMax.y ||
                 aMax.z < bMin.z || aMin.z > bMax.z);
    }
    
    /**
     * Handle collision between two entities
     */
    private void handleCollision(Entity a, Entity b, ColliderComponent aCollider, ColliderComponent bCollider) {
        RigidbodyComponent aRigidbody = a.getComponent(RigidbodyComponent.class);
        RigidbodyComponent bRigidbody = b.getComponent(RigidbodyComponent.class);
        
        if (aRigidbody != null && bRigidbody != null) {
            // Simple collision response
            // In a real engine, this would use impulse-based collision response
            
            // Swap velocities (simple bounce)
            Vector3 temp = aRigidbody.getVelocity();
            aRigidbody.setVelocity(bRigidbody.getVelocity().multiply(defaultRestitution));
            bRigidbody.setVelocity(temp.multiply(defaultRestitution));
            
            // Set on ground flag if colliding with ground
            if (aCollider.isGround()) {
                aRigidbody.setOnGround(true);
            }
            if (bCollider.isGround()) {
                bRigidbody.setOnGround(true);
            }
        }
    }
    
    /**
     * Raycast in the physics world
     */
    public boolean raycast(Vector3 origin, Vector3 direction, float maxDistance, RaycastHit hit) {
        // Simple raycast implementation
        // In a real engine, this would use spatial partitioning
        
        List<Entity> entities = entityManager.getEntitiesWithComponent(ColliderComponent.class);
        
        for (Entity entity : entities) {
            ColliderComponent collider = entity.getComponent(ColliderComponent.class);
            if (collider != null && collider.isEnabled()) {
                if (raycastCollider(origin, direction, maxDistance, collider, hit)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Raycast against a single collider
     */
    private boolean raycastCollider(Vector3 origin, Vector3 direction, float maxDistance, 
                                     ColliderComponent collider, RaycastHit hit) {
        // Simple AABB raycast
        Vector3 min = collider.getBounds().getMin();
        Vector3 max = collider.getBounds().getMax();
        
        // Calculate intersection
        float tmin = 0;
        float tmax = Float.POSITIVE_INFINITY;
        
        float[] d0 = {origin.x, origin.y, origin.z};
        float[] d1 = {direction.x, direction.y, direction.z};
        float[] lo = {min.x, min.y, min.z};
        float[] hi = {max.x, max.y, max.z};
        for (int i = 0; i < 3; i++) {
            if (Math.abs(d1[i]) < 1e-6f) {
                // Ray is parallel to this slab: no hit if the origin is outside it.
                if (d0[i] < lo[i] || d0[i] > hi[i]) return false;
                continue;
            }
            float invD = 1.0f / d1[i];
            float t1 = (lo[i] - d0[i]) * invD;
            float t2 = (hi[i] - d0[i]) * invD;
            
            tmin = Math.max(tmin, Math.min(t1, t2));
            tmax = Math.min(tmax, Math.max(t1, t2));
        }
        
        if (tmin <= tmax && tmax >= 0 && tmin <= maxDistance) {
            hit.setDistance(tmin);
            hit.setPoint(origin.add(direction.multiply(tmin)));
            hit.setNormal(new Vector3(0, 1, 0)); // Simplified normal
            hit.setEntity(collider.getEntity());
            return true;
        }
        
        return false;
    }
    
    /**
     * Shutdown the system
     */
    public void shutdown() {
        entityManager = null;
        enabled = false;
        Log.i(TAG, "PhysicsSystem shutdown");
    }
    
    // Getters and setters
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public float getGravity() {
        return gravity;
    }
    
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }
    
    public int getMaxIterations() {
        return maxIterations;
    }
    
    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }
    
    public float getFixedTimeStep() {
        return fixedTimeStep;
    }
    
    public void setFixedTimeStep(float fixedTimeStep) {
        this.fixedTimeStep = fixedTimeStep;
    }
    
    public boolean isUseContinuousCollisionDetection() {
        return useContinuousCollisionDetection;
    }
    
    public void setUseContinuousCollisionDetection(boolean useContinuousCollisionDetection) {
        this.useContinuousCollisionDetection = useContinuousCollisionDetection;
    }
    
    public boolean isUseGravity() {
        return useGravity;
    }
    
    public void setUseGravity(boolean useGravity) {
        this.useGravity = useGravity;
    }
    
    public boolean isUseFriction() {
        return useFriction;
    }
    
    public void setUseFriction(boolean useFriction) {
        this.useFriction = useFriction;
    }
    
    public float getDefaultFriction() {
        return defaultFriction;
    }
    
    public void setDefaultFriction(float defaultFriction) {
        this.defaultFriction = defaultFriction;
    }
    
    public float getDefaultRestitution() {
        return defaultRestitution;
    }
    
    public void setDefaultRestitution(float defaultRestitution) {
        this.defaultRestitution = defaultRestitution;
    }
    
    /**
     * RaycastHit class
     */
    public static class RaycastHit {
        private Vector3 point;
        private Vector3 normal;
        private float distance;
        private Entity entity;
        
        public RaycastHit() {
            this.point = new Vector3();
            this.normal = new Vector3();
            this.distance = 0;
            this.entity = null;
        }
        
        public Vector3 getPoint() {
            return point;
        }
        
        public void setPoint(Vector3 point) {
            this.point = point;
        }
        
        public Vector3 getNormal() {
            return normal;
        }
        
        public void setNormal(Vector3 normal) {
            this.normal = normal;
        }
        
        public float getDistance() {
            return distance;
        }
        
        public void setDistance(float distance) {
            this.distance = distance;
        }
        
        public Entity getEntity() {
            return entity;
        }
        
        public void setEntity(Entity entity) {
            this.entity = entity;
        }
    }
}

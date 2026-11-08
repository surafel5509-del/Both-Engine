package com.bothengine.particles;

import com.bothengine.core.ecs.Component;
import com.bothengine.core.ecs.Entity;
import com.bothengine.rendering.Color;
import com.bothengine.rendering.Material;
import com.bothengine.rendering.Mesh;
import com.bothengine.rendering.Texture;
import com.bothengine.rendering.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ParticleSystem
 * ECS component for particle effects
 * Supports GPU-based and CPU-based particle rendering
 * Similar to Unity's ParticleSystem
 */
public class ParticleSystem extends Component {
    private String name;
    private ParticleSystemRenderer renderer;
    
    // Emission
    private float duration;
    private float rate;
    private int maxParticles;
    private boolean loop;
    private boolean emit;
    
    // Shape
    private ParticleSystemShape shape;
    
    // Velocity
    private float speed;
    private Vector3 velocity;
    private float velocityRandom;
    
    // Lifetime
    private float startLifetime;
    private float lifetimeRandom;
    
    // Size
    private float startSize;
    private float sizeRandom;
    private boolean sizeOverLifetime;
    private AnimationCurve sizeCurve;
    
    // Color
    private Color startColor;
    private Color colorRandom;
    private boolean colorOverLifetime;
    private Gradient colorGradient;
    
    // Rotation
    private float startRotation;
    private float rotationRandom;
    private boolean rotationOverLifetime;
    
    // Gravity
    private Vector3 gravity;
    
    // Collision
    private boolean collision;
    private float bounce;
    
    // Particles
    private List<Particle> particles;
    private List<Particle> activeParticles;
    private List<Particle> inactiveParticles;
    
    // Time
    private float currentTime;
    private float emitTimer;
    
    // Random
    private Random random;
    
    public ParticleSystem() {
        this.name = "ParticleSystem";
        this.renderer = new ParticleSystemRenderer(this);
        
        // Emission
        this.duration = 5f;
        this.rate = 10f;
        this.maxParticles = 1000;
        this.loop = true;
        this.emit = true;
        
        // Shape
        this.shape = new ParticleSystemShape(ParticleSystemShapeType.CONE);
        
        // Velocity
        this.speed = 5f;
        this.velocity = new Vector3(0, 1, 0);
        this.velocityRandom = 0.5f;
        
        // Lifetime
        this.startLifetime = 5f;
        this.lifetimeRandom = 0f;
        
        // Size
        this.startSize = 1f;
        this.sizeRandom = 0f;
        this.sizeOverLifetime = false;
        this.sizeCurve = new AnimationCurve();
        
        // Color
        this.startColor = new Color(1f, 1f, 1f, 1f);
        this.colorRandom = new Color(0f, 0f, 0f, 0f);
        this.colorOverLifetime = false;
        this.colorGradient = new Gradient();
        
        // Rotation
        this.startRotation = 0f;
        this.rotationRandom = 0f;
        this.rotationOverLifetime = false;
        
        // Gravity
        this.gravity = new Vector3(0, -9.81f, 0);
        
        // Collision
        this.collision = false;
        this.bounce = 0.5f;
        
        // Particles
        this.particles = new ArrayList<>();
        this.activeParticles = new ArrayList<>();
        this.inactiveParticles = new ArrayList<>();
        
        // Time
        this.currentTime = 0f;
        this.emitTimer = 0f;
        
        // Random
        this.random = new Random();
        
        // Initialize particle pool
        initializeParticlePool();
    }
    
    /**
     * Create a particle system with name
     */
    public static ParticleSystem create(String name) {
        ParticleSystem system = new ParticleSystem();
        system.setName(name);
        return system;
    }
    
    /**
     * Initialize the particle pool
     */
    private void initializeParticlePool() {
        for (int i = 0; i < maxParticles; i++) {
            Particle particle = new Particle();
            particle.setIndex(i);
            inactiveParticles.add(particle);
        }
    }
    
    /**
     * Called when component is added to an entity
     */
    @Override
    public void onAddedToEntity() {
        super.onAddedToEntity();
        
        // Initialize renderer
        renderer.initialize();
    }
    
    /**
     * Called when component is removed from an entity
     */
    @Override
    public void onRemovedFromEntity() {
        super.onRemovedFromEntity();
        
        // Shutdown renderer
        renderer.shutdown();
    }
    
    /**
     * Update the particle system
     */
    @Override
    public void onUpdate(float deltaTime) {
        super.onUpdate(deltaTime);
        
        if (!isEnabled()) return;
        
        currentTime += deltaTime;
        
        // Check if we should stop emitting
        if (duration > 0 && currentTime >= duration && !loop) {
            emit = false;
        }
        
        // Emit new particles
        if (emit) {
            emitParticles(deltaTime);
        }
        
        // Update existing particles
        updateParticles(deltaTime);
        
        // Render particles
        renderer.render();
    }
    
    /**
     * Emit new particles
     */
    private void emitParticles(float deltaTime) {
        if (rate <= 0) return;
        
        emitTimer += deltaTime;
        float emitInterval = 1f / rate;
        
        int emitCount = (int) (emitTimer / emitInterval);
        emitTimer -= emitCount * emitInterval;
        
        for (int i = 0; i < emitCount; i++) {
            if (inactiveParticles.isEmpty()) {
                // No more particles available
                break;
            }
            
            Particle particle = inactiveParticles.remove(0);
            initializeParticle(particle);
            activeParticles.add(particle);
        }
    }
    
    /**
     * Initialize a new particle
     */
    private void initializeParticle(Particle particle) {
        particle.setTime(0f);
        particle.setLifetime(startLifetime + getRandomValue(lifetimeRandom));
        
        // Position from shape
        Vector3 position = shape.getPosition(random);
        particle.setPosition(position);
        
        // Velocity
        Vector3 velocity = this.velocity.multiply(speed);
        velocity = velocity.add(getRandomVector(velocityRandom * speed));
        particle.setVelocity(velocity);
        
        // Size
        float size = startSize + getRandomValue(sizeRandom);
        particle.setSize(size);
        
        // Color
        Color color = new Color(
            startColor.r + getRandomValue(colorRandom.r),
            startColor.g + getRandomValue(colorRandom.g),
            startColor.b + getRandomValue(colorRandom.b),
            startColor.a + getRandomValue(colorRandom.a)
        );
        particle.setColor(color);
        
        // Rotation
        float rotation = startRotation + getRandomValue(rotationRandom);
        particle.setRotation(rotation);
    }
    
    /**
     * Update all active particles
     */
    private void updateParticles(float deltaTime) {
        for (int i = 0; i < activeParticles.size(); i++) {
            Particle particle = activeParticles.get(i);
            
            particle.setTime(particle.getTime() + deltaTime);
            
            // Check if particle is dead
            if (particle.getTime() >= particle.getLifetime()) {
                activeParticles.remove(i);
                inactiveParticles.add(particle);
                i--;
                continue;
            }
            
            // Update position
            Vector3 position = particle.getPosition();
            Vector3 velocity = particle.getVelocity();
            
            // Apply gravity
            velocity = velocity.add(gravity.multiply(deltaTime));
            
            // Update position
            position = position.add(velocity.multiply(deltaTime));
            particle.setPosition(position);
            particle.setVelocity(velocity);
            
            // Update size over lifetime
            if (sizeOverLifetime) {
                float t = particle.getTime() / particle.getLifetime();
                float size = startSize * sizeCurve.evaluate(t);
                particle.setSize(size);
            }
            
            // Update color over lifetime
            if (colorOverLifetime) {
                float t = particle.getTime() / particle.getLifetime();
                Color color = colorGradient.evaluate(t);
                particle.setColor(color);
            }
            
            // Update rotation over lifetime
            if (rotationOverLifetime) {
                float t = particle.getTime() / particle.getLifetime();
                float rotation = startRotation * t;
                particle.setRotation(rotation);
            }
            
            // Handle collision
            if (collision) {
                // Check for collision with world
                // For now, just bounce on y=0
                if (position.y <= 0) {
                    position.y = 0;
                    velocity.y = -velocity.y * bounce;
                    particle.setPosition(position);
                    particle.setVelocity(velocity);
                }
            }
        }
    }
    
    /**
     * Get a random value between -range and +range
     */
    private float getRandomValue(float range) {
        return (random.nextFloat() * 2 - 1) * range;
    }
    
    /**
     * Get a random vector with magnitude up to range
     */
    private Vector3 getRandomVector(float range) {
        return new Vector3(
            getRandomValue(range),
            getRandomValue(range),
            getRandomValue(range)
        );
    }
    
    /**
     * Play the particle system
     */
    public void play() {
        emit = true;
        currentTime = 0f;
        emitTimer = 0f;
        
        // Clear existing particles
        inactiveParticles.addAll(activeParticles);
        activeParticles.clear();
    }
    
    /**
     * Stop the particle system
     */
    public void stop() {
        emit = false;
    }
    
    /**
     * Pause the particle system
     */
    public void pause() {
        emit = false;
    }
    
    /**
     * Clear all particles
     */
    public void clear() {
        inactiveParticles.addAll(activeParticles);
        activeParticles.clear();
    }
    
    /**
     * Emit a burst of particles
     */
    public void emitBurst(int count) {
        for (int i = 0; i < count; i++) {
            if (inactiveParticles.isEmpty()) {
                break;
            }
            
            Particle particle = inactiveParticles.remove(0);
            initializeParticle(particle);
            activeParticles.add(particle);
        }
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public ParticleSystemRenderer getRenderer() {
        return renderer;
    }
    
    public void setRenderer(ParticleSystemRenderer renderer) {
        this.renderer = renderer;
    }
    
    public float getDuration() {
        return duration;
    }
    
    public void setDuration(float duration) {
        this.duration = duration;
    }
    
    public float getRate() {
        return rate;
    }
    
    public void setRate(float rate) {
        this.rate = rate;
    }
    
    public int getMaxParticles() {
        return maxParticles;
    }
    
    public void setMaxParticles(int maxParticles) {
        this.maxParticles = maxParticles;
    }
    
    public boolean isLoop() {
        return loop;
    }
    
    public void setLoop(boolean loop) {
        this.loop = loop;
    }
    
    public boolean isEmit() {
        return emit;
    }
    
    public void setEmit(boolean emit) {
        this.emit = emit;
    }
    
    public ParticleSystemShape getShape() {
        return shape;
    }
    
    public void setShape(ParticleSystemShape shape) {
        this.shape = shape;
    }
    
    public float getSpeed() {
        return speed;
    }
    
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    
    public Vector3 getVelocity() {
        return velocity;
    }
    
    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }
    
    public float getVelocityRandom() {
        return velocityRandom;
    }
    
    public void setVelocityRandom(float velocityRandom) {
        this.velocityRandom = velocityRandom;
    }
    
    public float getStartLifetime() {
        return startLifetime;
    }
    
    public void setStartLifetime(float startLifetime) {
        this.startLifetime = startLifetime;
    }
    
    public float getLifetimeRandom() {
        return lifetimeRandom;
    }
    
    public void setLifetimeRandom(float lifetimeRandom) {
        this.lifetimeRandom = lifetimeRandom;
    }
    
    public float getStartSize() {
        return startSize;
    }
    
    public void setStartSize(float startSize) {
        this.startSize = startSize;
    }
    
    public float getSizeRandom() {
        return sizeRandom;
    }
    
    public void setSizeRandom(float sizeRandom) {
        this.sizeRandom = sizeRandom;
    }
    
    public boolean isSizeOverLifetime() {
        return sizeOverLifetime;
    }
    
    public void setSizeOverLifetime(boolean sizeOverLifetime) {
        this.sizeOverLifetime = sizeOverLifetime;
    }
    
    public AnimationCurve getSizeCurve() {
        return sizeCurve;
    }
    
    public void setSizeCurve(AnimationCurve sizeCurve) {
        this.sizeCurve = sizeCurve;
    }
    
    public Color getStartColor() {
        return startColor;
    }
    
    public void setStartColor(Color startColor) {
        this.startColor = startColor;
    }
    
    public Color getColorRandom() {
        return colorRandom;
    }
    
    public void setColorRandom(Color colorRandom) {
        this.colorRandom = colorRandom;
    }
    
    public boolean isColorOverLifetime() {
        return colorOverLifetime;
    }
    
    public void setColorOverLifetime(boolean colorOverLifetime) {
        this.colorOverLifetime = colorOverLifetime;
    }
    
    public Gradient getColorGradient() {
        return colorGradient;
    }
    
    public void setColorGradient(Gradient colorGradient) {
        this.colorGradient = colorGradient;
    }
    
    public float getStartRotation() {
        return startRotation;
    }
    
    public void setStartRotation(float startRotation) {
        this.startRotation = startRotation;
    }
    
    public float getRotationRandom() {
        return rotationRandom;
    }
    
    public void setRotationRandom(float rotationRandom) {
        this.rotationRandom = rotationRandom;
    }
    
    public boolean isRotationOverLifetime() {
        return rotationOverLifetime;
    }
    
    public void setRotationOverLifetime(boolean rotationOverLifetime) {
        this.rotationOverLifetime = rotationOverLifetime;
    }
    
    public Vector3 getGravity() {
        return gravity;
    }
    
    public void setGravity(Vector3 gravity) {
        this.gravity = gravity;
    }
    
    public boolean isCollision() {
        return collision;
    }
    
    public void setCollision(boolean collision) {
        this.collision = collision;
    }
    
    public float getBounce() {
        return bounce;
    }
    
    public void setBounce(float bounce) {
        this.bounce = bounce;
    }
    
    public List<Particle> getActiveParticles() {
        return new ArrayList<>(activeParticles);
    }
    
    public int getActiveParticleCount() {
        return activeParticles.size();
    }
    
    public boolean isPlaying() {
        return emit || !activeParticles.isEmpty();
    }
    
    @Override
    public String toString() {
        return "ParticleSystem{" +
                "name='" + name + '\'' +
                ", maxParticles=" + maxParticles +
                ", rate=" + rate +
                ", activeParticles=" + activeParticles.size() +
                ", isPlaying=" + isPlaying() +
                '}';
    }
    
    /**
     * Particle class
     * Represents a single particle
     */
    public static class Particle {
        private int index;
        private float time;
        private float lifetime;
        private Vector3 position;
        private Vector3 velocity;
        private float size;
        private Color color;
        private float rotation;
        
        public Particle() {
            this.index = 0;
            this.time = 0f;
            this.lifetime = 0f;
            this.position = new Vector3();
            this.velocity = new Vector3();
            this.size = 1f;
            this.color = new Color(1f, 1f, 1f, 1f);
            this.rotation = 0f;
        }
        
        // Getters and setters
        
        public int getIndex() {
            return index;
        }
        
        public void setIndex(int index) {
            this.index = index;
        }
        
        public float getTime() {
            return time;
        }
        
        public void setTime(float time) {
            this.time = time;
        }
        
        public float getLifetime() {
            return lifetime;
        }
        
        public void setLifetime(float lifetime) {
            this.lifetime = lifetime;
        }
        
        public Vector3 getPosition() {
            return position;
        }
        
        public void setPosition(Vector3 position) {
            this.position = position;
        }
        
        public Vector3 getVelocity() {
            return velocity;
        }
        
        public void setVelocity(Vector3 velocity) {
            this.velocity = velocity;
        }
        
        public float getSize() {
            return size;
        }
        
        public void setSize(float size) {
            this.size = size;
        }
        
        public Color getColor() {
            return color;
        }
        
        public void setColor(Color color) {
            this.color = color;
        }
        
        public float getRotation() {
            return rotation;
        }
        
        public void setRotation(float rotation) {
            this.rotation = rotation;
        }
        
        @Override
        public String toString() {
            return "Particle{" +
                    "index=" + index +
                    ", time=" + time +
                    ", position=" + position +
                    '}';
        }
    }
    
    /**
     * ParticleSystemShape class
     * Defines the shape from which particles are emitted
     */
    public static class ParticleSystemShape {
        private ParticleSystemShapeType type;
        private Vector3 position;
        private Vector3 rotation;
        private Vector3 scale;
        private float radius;
        private float angle;
        private float length;
        
        public ParticleSystemShape(ParticleSystemShapeType type) {
            this.type = type;
            this.position = new Vector3();
            this.rotation = new Vector3();
            this.scale = new Vector3(1, 1, 1);
            this.radius = 1f;
            this.angle = 0f;
            this.length = 0f;
        }
        
        /**
         * Get a random position within the shape
         */
        public Vector3 getPosition(Random random) {
            switch (type) {
                case POINT:
                    return new Vector3();
                case SPHERE:
                    return getRandomSpherePosition(random);
                case CONE:
                    return getRandomConePosition(random);
                case BOX:
                    return getRandomBoxPosition(random);
                case CIRCLE:
                    return getRandomCirclePosition(random);
                case EDGE:
                    return getRandomEdgePosition(random);
                default:
                    return new Vector3();
            }
        }
        
        /**
         * Get a random position on a sphere
         */
        private Vector3 getRandomSpherePosition(Random random) {
            float theta = (float) (random.nextFloat() * 2 * Math.PI);
            float phi = (float) (Math.acos(2 * random.nextFloat() - 1));
            
            float x = radius * (float) Math.sin(phi) * (float) Math.cos(theta);
            float y = radius * (float) Math.sin(phi) * (float) Math.sin(theta);
            float z = radius * (float) Math.cos(phi);
            
            return new Vector3(x, y, z);
        }
        
        /**
         * Get a random position in a cone
         */
        private Vector3 getRandomConePosition(Random random) {
            float theta = (float) (random.nextFloat() * 2 * Math.PI);
            float r = radius * random.nextFloat();
            float h = length * random.nextFloat();
            
            float x = r * (float) Math.cos(theta);
            float y = h;
            float z = r * (float) Math.sin(theta);
            
            return new Vector3(x, y, z);
        }
        
        /**
         * Get a random position in a box
         */
        private Vector3 getRandomBoxPosition(Random random) {
            float x = scale.x * (random.nextFloat() * 2 - 1);
            float y = scale.y * (random.nextFloat() * 2 - 1);
            float z = scale.z * (random.nextFloat() * 2 - 1);
            
            return new Vector3(x, y, z);
        }
        
        /**
         * Get a random position on a circle
         */
        private Vector3 getRandomCirclePosition(Random random) {
            float theta = (float) (random.nextFloat() * 2 * Math.PI);
            float r = radius * random.nextFloat();
            
            float x = r * (float) Math.cos(theta);
            float z = r * (float) Math.sin(theta);
            
            return new Vector3(x, 0, z);
        }
        
        /**
         * Get a random position on the edge of a shape
         */
        private Vector3 getRandomEdgePosition(Random random) {
            // For now, just return a random position on a circle
            return getRandomCirclePosition(random);
        }
        
        // Getters and setters
        
        public ParticleSystemShapeType getType() {
            return type;
        }
        
        public void setType(ParticleSystemShapeType type) {
            this.type = type;
        }
        
        public Vector3 getPosition() {
            return position;
        }
        
        public void setPosition(Vector3 position) {
            this.position = position;
        }
        
        public Vector3 getRotation() {
            return rotation;
        }
        
        public void setRotation(Vector3 rotation) {
            this.rotation = rotation;
        }
        
        public Vector3 getScale() {
            return scale;
        }
        
        public void setScale(Vector3 scale) {
            this.scale = scale;
        }
        
        public float getRadius() {
            return radius;
        }
        
        public void setRadius(float radius) {
            this.radius = radius;
        }
        
        public float getAngle() {
            return angle;
        }
        
        public void setAngle(float angle) {
            this.angle = angle;
        }
        
        public float getLength() {
            return length;
        }
        
        public void setLength(float length) {
            this.length = length;
        }
        
        @Override
        public String toString() {
            return "ParticleSystemShape{" +
                    "type=" + type +
                    ", radius=" + radius +
                    '}';
        }
    }
    
    /**
     * ParticleSystemShapeType enum
     */
    public enum ParticleSystemShapeType {
        POINT,
        SPHERE,
        CONE,
        BOX,
        CIRCLE,
        EDGE
    }
    
    /**
     * AnimationCurve class
     * Simple animation curve for particle properties
     */
    public static class AnimationCurve {
        private List<Keyframe> keyframes;
        
        public AnimationCurve() {
            this.keyframes = new ArrayList<>();
        }
        
        /**
         * Add a keyframe
         */
        public void addKeyframe(float time, float value) {
            keyframes.add(new Keyframe(time, value));
        }
        
        /**
         * Evaluate the curve at a given time
         */
        public float evaluate(float time) {
            if (keyframes.isEmpty()) return 1f;
            if (keyframes.size() == 1) return keyframes.get(0).value;
            
            // Find the two keyframes to interpolate between
            for (int i = 0; i < keyframes.size() - 1; i++) {
                Keyframe k1 = keyframes.get(i);
                Keyframe k2 = keyframes.get(i + 1);
                
                if (time >= k1.time && time <= k2.time) {
                    float t = (time - k1.time) / (k2.time - k1.time);
                    return k1.value + (k2.value - k1.value) * t;
                }
            }
            
            // Return last keyframe value
            return keyframes.get(keyframes.size() - 1).value;
        }
        
        // Keyframe class
        private static class Keyframe {
            private float time;
            private float value;
            
            public Keyframe(float time, float value) {
                this.time = time;
                this.value = value;
            }
        }
    }
    
    /**
     * Gradient class
     * Color gradient for particle color over lifetime
     */
    public static class Gradient {
        private List<ColorKeyframe> keyframes;
        
        public Gradient() {
            this.keyframes = new ArrayList<>();
        }
        
        /**
         * Add a color keyframe
         */
        public void addKeyframe(float time, Color color) {
            keyframes.add(new ColorKeyframe(time, color));
        }
        
        /**
         * Evaluate the gradient at a given time
         */
        public Color evaluate(float time) {
            if (keyframes.isEmpty()) return new Color(1f, 1f, 1f, 1f);
            if (keyframes.size() == 1) return keyframes.get(0).color;
            
            // Find the two keyframes to interpolate between
            for (int i = 0; i < keyframes.size() - 1; i++) {
                ColorKeyframe k1 = keyframes.get(i);
                ColorKeyframe k2 = keyframes.get(i + 1);
                
                if (time >= k1.time && time <= k2.time) {
                    float t = (time - k1.time) / (k2.time - k1.time);
                    return Color.lerp(k1.color, k2.color, t);
                }
            }
            
            // Return last keyframe color
            return keyframes.get(keyframes.size() - 1).color;
        }
        
        // ColorKeyframe class
        private static class ColorKeyframe {
            private float time;
            private Color color;
            
            public ColorKeyframe(float time, Color color) {
                this.time = time;
                this.color = color;
            }
        }
    }
    
    /**
     * ParticleSystemRenderer class
     * Handles rendering of particles
     */
    public static class ParticleSystemRenderer {
        private ParticleSystem particleSystem;
        private Mesh mesh;
        private Material material;
        private Texture texture;
        
        public ParticleSystemRenderer(ParticleSystem particleSystem) {
            this.particleSystem = particleSystem;
            this.mesh = null;
            this.material = null;
            this.texture = null;
        }
        
        /**
         * Initialize the renderer
         */
        public void initialize() {
            // Create a quad mesh for particle rendering
            mesh = createParticleMesh();
            material = new Material();
        }
        
        /**
         * Create a mesh for particle rendering
         */
        private Mesh createParticleMesh() {
            // Create a simple quad mesh
            float[] vertices = {
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f,
                -0.5f, 0.5f, 0f
            };
            
            short[] indices = {0, 1, 2, 2, 3, 0};
            
            float[] texCoords = {
                0f, 0f,
                1f, 0f,
                1f, 1f,
                0f, 1f
            };
            
            Mesh mesh = new Mesh("ParticleMesh");
            mesh.setVertices(vertices);
            mesh.setIndices(indices);
            mesh.setTexCoords(texCoords);
            
            return mesh;
        }
        
        /**
         * Render the particles
         */
        public void render() {
            if (mesh == null) return;
            
            // For each active particle, render the mesh
            // In a real implementation, this would use instanced rendering
            // or a geometry shader for better performance
            
            for (Particle particle : particleSystem.activeParticles) {
                // Set particle properties (position, size, color, rotation)
                // Render the mesh
            }
        }
        
        /**
         * Shutdown the renderer
         */
        public void shutdown() {
            if (mesh != null) {
                mesh.delete();
                mesh = null;
            }
        }
        
        // Getters and setters
        
        public ParticleSystem getParticleSystem() {
            return particleSystem;
        }
        
        public Mesh getMesh() {
            return mesh;
        }
        
        public void setMesh(Mesh mesh) {
            this.mesh = mesh;
        }
        
        public Material getMaterial() {
            return material;
        }
        
        public void setMaterial(Material material) {
            this.material = material;
        }
        
        public Texture getTexture() {
            return texture;
        }
        
        public void setTexture(Texture texture) {
            this.texture = texture;
        }
    }
}

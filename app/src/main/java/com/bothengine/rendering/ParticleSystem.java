package com.bothengine.rendering;

import android.util.Log;

import com.bothengine.rendering.shaders.Shader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ParticleSystem
 * Handles particle effects (fire, smoke, sparks, etc.)
 */
public class ParticleSystem {
    private static final String TAG = "ParticleSystem";
    
    private String name;
    private Vector2 position;
    private int maxParticles;
    private List<Particle> particles;
    private List<Particle> activeParticles;
    private boolean emitting;
    private float emissionRate;
    private float emissionTime;
    private float lifeTime;
    private boolean loop;
    private Texture texture;
    private Material material;
    
    // Particle properties
    private float particleLifeMin;
    private float particleLifeMax;
    private float particleSpeedMin;
    private float particleSpeedMax;
    private float particleSizeMin;
    private float particleSizeMax;
    private float particleRotationMin;
    private float particleRotationMax;
    private Color particleColorStart;
    private Color particleColorEnd;
    private Vector2 gravity;
    private float emissionAngle;
    private float emissionAngleSpread;
    
    private Random random;
    private float emitTimer;
    private float systemTime;
    
    public ParticleSystem() {
        this.name = "ParticleSystem";
        this.position = new Vector2(0, 0);
        this.maxParticles = 100;
        this.particles = new ArrayList<>();
        this.activeParticles = new ArrayList<>();
        this.emitting = true;
        this.emissionRate = 10;
        this.emissionTime = 0;
        this.lifeTime = 5;
        this.loop = true;
        this.texture = null;
        this.material = null;
        
        this.particleLifeMin = 1;
        this.particleLifeMax = 3;
        this.particleSpeedMin = 100;
        this.particleSpeedMax = 200;
        this.particleSizeMin = 10;
        this.particleSizeMax = 30;
        this.particleRotationMin = 0;
        this.particleRotationMax = 360;
        this.particleColorStart = Color.WHITE;
        this.particleColorEnd = Color.WHITE;
        this.gravity = new Vector2(0, 100);
        this.emissionAngle = 90; // Up
        this.emissionAngleSpread = 30;
        
        this.random = new Random();
        this.emitTimer = 0;
        this.systemTime = 0;
        
        // Pre-allocate particles
        for (int i = 0; i < maxParticles; i++) {
            particles.add(new Particle());
        }
    }
    
    public ParticleSystem(String name) {
        this();
        this.name = name;
    }
    
    /**
     * Update the particle system
     */
    public void update(float deltaTime) {
        if (!emitting) return;
        
        systemTime += deltaTime;
        emitTimer += deltaTime;
        
        // Check if system lifetime has expired
        if (lifeTime > 0 && systemTime >= lifeTime) {
            if (loop) {
                systemTime = 0;
            } else {
                emitting = false;
            }
        }
        
        // Emit new particles
        float emitInterval = 1.0f / emissionRate;
        while (emitTimer >= emitInterval && activeParticles.size() < maxParticles) {
            emitParticle();
            emitTimer -= emitInterval;
        }
        
        // Update active particles
        for (int i = activeParticles.size() - 1; i >= 0; i--) {
            Particle particle = activeParticles.get(i);
            particle.update(deltaTime);
            
            if (particle.isDead()) {
                activeParticles.remove(i);
            }
        }
    }
    
    /**
     * Emit a new particle
     */
    private void emitParticle() {
        // Find an inactive particle
        for (Particle particle : particles) {
            if (!particle.isActive()) {
                activateParticle(particle);
                activeParticles.add(particle);
                return;
            }
        }
    }
    
    /**
     * Activate a particle with random properties
     */
    private void activateParticle(Particle particle) {
        particle.activate();
        particle.setPosition(position);
        
        // Random life
        particle.setLifeTime(particleLifeMin + random.nextFloat() * (particleLifeMax - particleLifeMin));
        particle.setTime(0);
        
        // Random velocity
        float angle = emissionAngle + (random.nextFloat() * 2 - 1) * emissionAngleSpread;
        float speed = particleSpeedMin + random.nextFloat() * (particleSpeedMax - particleSpeedMin);
        particle.setVelocity(new Vector2(
            (float) Math.cos(Math.toRadians(angle)) * speed,
            (float) Math.sin(Math.toRadians(angle)) * speed
        ));
        
        // Random size
        particle.setSize(particleSizeMin + random.nextFloat() * (particleSizeMax - particleSizeMin));
        
        // Random rotation
        particle.setRotation(particleRotationMin + random.nextFloat() * (particleRotationMax - particleRotationMin));
        
        // Random color
        particle.setColor(lerpColor(particleColorStart, particleColorEnd, random.nextFloat()));
    }
    
    /**
     * Linear interpolation between two colors
     */
    private Color lerpColor(Color a, Color b, float t) {
        return new Color(
            a.r + (b.r - a.r) * t,
            a.g + (b.g - a.g) * t,
            a.b + (b.b - a.b) * t,
            a.a + (b.a - a.a) * t
        );
    }
    
    /**
     * Render the particle system
     */
    public void render(Shader shader) {
        if (texture == null || activeParticles.isEmpty()) return;
        
        // Bind material if available
        if (material != null) {
            material.bind(shader);
        }
        
        // Set texture
        if (texture.isLoaded()) {
            shader.setUniform("uUseTexture", 1);
            texture.bind();
        } else {
            shader.setUniform("uUseTexture", 0);
        }
        
        // Render each particle
        for (Particle particle : activeParticles) {
            if (particle.isVisible()) {
                // Set model matrix
                Matrix4 modelMatrix = particle.getTransformMatrix();
                shader.setUniformMatrix("uModelMatrix", modelMatrix.data);
                
                // Set color
                Color color = particle.getColor();
                shader.setUniform("aColor", color.r, color.g, color.b, color.a);
                
                // Render particle quad
                particle.render();
            }
        }
        
        // Unbind texture
        if (texture != null && texture.isLoaded()) {
            texture.unbind();
        }
    }
    
    /**
     * Start emitting particles
     */
    public void start() {
        emitting = true;
        systemTime = 0;
        emitTimer = 0;
    }
    
    /**
     * Stop emitting particles
     */
    public void stop() {
        emitting = false;
    }
    
    /**
     * Reset the particle system
     */
    public void reset() {
        stop();
        activeParticles.clear();
        for (Particle particle : particles) {
            particle.deactivate();
        }
        systemTime = 0;
        emitTimer = 0;
    }
    
    /**
     * Clear all particles
     */
    public void clear() {
        activeParticles.clear();
        for (Particle particle : particles) {
            particle.deactivate();
        }
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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
    
    public int getMaxParticles() {
        return maxParticles;
    }
    
    public void setMaxParticles(int maxParticles) {
        this.maxParticles = maxParticles;
        
        // Adjust particle pool
        while (particles.size() < maxParticles) {
            particles.add(new Particle());
        }
        
        // Remove excess particles
        while (particles.size() > maxParticles) {
            particles.remove(particles.size() - 1);
        }
    }
    
    public int getActiveParticleCount() {
        return activeParticles.size();
    }
    
    public boolean isEmitting() {
        return emitting;
    }
    
    public void setEmitting(boolean emitting) {
        this.emitting = emitting;
    }
    
    public float getEmissionRate() {
        return emissionRate;
    }
    
    public void setEmissionRate(float emissionRate) {
        this.emissionRate = emissionRate;
    }
    
    public float getEmissionTime() {
        return emissionTime;
    }
    
    public void setEmissionTime(float emissionTime) {
        this.emissionTime = emissionTime;
    }
    
    public float getLifeTime() {
        return lifeTime;
    }
    
    public void setLifeTime(float lifeTime) {
        this.lifeTime = lifeTime;
    }
    
    public boolean isLoop() {
        return loop;
    }
    
    public void setLoop(boolean loop) {
        this.loop = loop;
    }
    
    public Texture getTexture() {
        return texture;
    }
    
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public void setMaterial(Material material) {
        this.material = material;
    }
    
    public float getParticleLifeMin() {
        return particleLifeMin;
    }
    
    public void setParticleLifeMin(float particleLifeMin) {
        this.particleLifeMin = particleLifeMin;
    }
    
    public float getParticleLifeMax() {
        return particleLifeMax;
    }
    
    public void setParticleLifeMax(float particleLifeMax) {
        this.particleLifeMax = particleLifeMax;
    }
    
    public float getParticleSpeedMin() {
        return particleSpeedMin;
    }
    
    public void setParticleSpeedMin(float particleSpeedMin) {
        this.particleSpeedMin = particleSpeedMin;
    }
    
    public float getParticleSpeedMax() {
        return particleSpeedMax;
    }
    
    public void setParticleSpeedMax(float particleSpeedMax) {
        this.particleSpeedMax = particleSpeedMax;
    }
    
    public float getParticleSizeMin() {
        return particleSizeMin;
    }
    
    public void setParticleSizeMin(float particleSizeMin) {
        this.particleSizeMin = particleSizeMin;
    }
    
    public float getParticleSizeMax() {
        return particleSizeMax;
    }
    
    public void setParticleSizeMax(float particleSizeMax) {
        this.particleSizeMax = particleSizeMax;
    }
    
    public float getParticleRotationMin() {
        return particleRotationMin;
    }
    
    public void setParticleRotationMin(float particleRotationMin) {
        this.particleRotationMin = particleRotationMin;
    }
    
    public float getParticleRotationMax() {
        return particleRotationMax;
    }
    
    public void setParticleRotationMax(float particleRotationMax) {
        this.particleRotationMax = particleRotationMax;
    }
    
    public Color getParticleColorStart() {
        return particleColorStart;
    }
    
    public void setParticleColorStart(Color particleColorStart) {
        this.particleColorStart = particleColorStart;
    }
    
    public Color getParticleColorEnd() {
        return particleColorEnd;
    }
    
    public void setParticleColorEnd(Color particleColorEnd) {
        this.particleColorEnd = particleColorEnd;
    }
    
    public Vector2 getGravity() {
        return gravity;
    }
    
    public void setGravity(Vector2 gravity) {
        this.gravity = gravity;
    }
    
    public float getEmissionAngle() {
        return emissionAngle;
    }
    
    public void setEmissionAngle(float emissionAngle) {
        this.emissionAngle = emissionAngle;
    }
    
    public float getEmissionAngleSpread() {
        return emissionAngleSpread;
    }
    
    public void setEmissionAngleSpread(float emissionAngleSpread) {
        this.emissionAngleSpread = emissionAngleSpread;
    }
    
    @Override
    public String toString() {
        return "ParticleSystem{" +
                "name='" + name + '\'' +
                ", position=" + position +
                ", maxParticles=" + maxParticles +
                ", activeParticles=" + activeParticles.size() +
                ", emitting=" + emitting +
                '}';
    }
    
    /**
     * Particle class
     */
    private static class Particle {
        private Vector2 position;
        private Vector2 velocity;
        private Vector2 acceleration;
        private float size;
        private float rotation;
        private float rotationSpeed;
        private Color color;
        private float lifeTime;
        private float time;
        private boolean active;
        private boolean visible;
        private Mesh mesh;
        
        public Particle() {
            this.position = new Vector2(0, 0);
            this.velocity = new Vector2(0, 0);
            this.acceleration = new Vector2(0, 0);
            this.size = 10;
            this.rotation = 0;
            this.rotationSpeed = 0;
            this.color = Color.WHITE;
            this.lifeTime = 1;
            this.time = 0;
            this.active = false;
            this.visible = true;
            
            // Create particle mesh
            createMesh();
        }
        
        /**
         * Create the mesh for this particle
         */
        private void createMesh() {
            // Simple quad mesh
            float[] vertices = {
                -0.5f, -0.5f, 0,
                0.5f, -0.5f, 0,
                0.5f, 0.5f, 0,
                -0.5f, 0.5f, 0
            };
            
            float[] texCoords = {
                0, 1,
                1, 1,
                1, 0,
                0, 0
            };
            
            short[] indices = {0, 1, 2, 0, 2, 3};
            
            mesh = Mesh.createWithIndices("Particle_Mesh", vertices, indices);
            mesh.getVertexData().texCoords = texCoords;
            mesh.uploadToGPU();
        }
        
        /**
         * Update the particle
         */
        public void update(float deltaTime) {
            if (!active) return;
            
            time += deltaTime;
            
            if (time >= lifeTime) {
                active = false;
                return;
            }
            
            // Update position
            velocity.add(Vector2.multiply(acceleration, deltaTime));
            position.add(Vector2.multiply(velocity, deltaTime));
            
            // Update rotation
            rotation += rotationSpeed * deltaTime;
        }
        
        /**
         * Render the particle
         */
        public void render() {
            if (!active || !visible || mesh == null) return;
            
            // Scale mesh to particle size
            Matrix4 modelMatrix = new Matrix4();
            modelMatrix.translate(new Vector3(position.x, position.y, 0));
            modelMatrix.rotateZ((float) Math.toRadians(rotation));
            modelMatrix.scale(new Vector3(size, size, 1));
            
            // Render mesh
            mesh.render();
        }
        
        /**
         * Get the transformation matrix
         */
        public Matrix4 getTransformMatrix() {
            Matrix4 modelMatrix = new Matrix4();
            modelMatrix.translate(new Vector3(position.x, position.y, 0));
            modelMatrix.rotateZ((float) Math.toRadians(rotation));
            modelMatrix.scale(new Vector3(size, size, 1));
            return modelMatrix;
        }
        
        // Getters and setters
        
        public Vector2 getPosition() {
            return position;
        }
        
        public void setPosition(Vector2 position) {
            this.position = position;
        }
        
        public Vector2 getVelocity() {
            return velocity;
        }
        
        public void setVelocity(Vector2 velocity) {
            this.velocity = velocity;
        }
        
        public Vector2 getAcceleration() {
            return acceleration;
        }
        
        public void setAcceleration(Vector2 acceleration) {
            this.acceleration = acceleration;
        }
        
        public float getSize() {
            return size;
        }
        
        public void setSize(float size) {
            this.size = size;
        }
        
        public float getRotation() {
            return rotation;
        }
        
        public void setRotation(float rotation) {
            this.rotation = rotation;
        }
        
        public float getRotationSpeed() {
            return rotationSpeed;
        }
        
        public void setRotationSpeed(float rotationSpeed) {
            this.rotationSpeed = rotationSpeed;
        }
        
        public Color getColor() {
            return color;
        }
        
        public void setColor(Color color) {
            this.color = color;
        }
        
        public float getLifeTime() {
            return lifeTime;
        }
        
        public void setLifeTime(float lifeTime) {
            this.lifeTime = lifeTime;
        }
        
        public float getTime() {
            return time;
        }
        
        public void setTime(float time) {
            this.time = time;
        }
        
        public boolean isActive() {
            return active;
        }
        
        public void activate() {
            this.active = true;
            this.visible = true;
            this.time = 0;
        }
        
        public void deactivate() {
            this.active = false;
            this.visible = false;
        }
        
        public boolean isVisible() {
            return visible;
        }
        
        public void setVisible(boolean visible) {
            this.visible = visible;
        }
        
        public boolean isDead() {
            return !active || time >= lifeTime;
        }
        
        public Mesh getMesh() {
            return mesh;
        }
    }
}

package com.bothengine.physics;

/**
 * PhysicsConfig
 * Configuration for the physics system
 */
public class PhysicsConfig {
    // Gravity settings
    private float gravityX;
    private float gravityY;
    private float gravityZ;
    
    // Simulation settings
    private float fixedTimeStep;
    private int maxSubSteps;
    private int velocityIterations;
    private int positionIterations;
    
    // Collision settings
    private float defaultRestitution;
    private float defaultFriction;
    private float defaultMass;
    private boolean enableContinuousCollision;
    private float ccdThreshold;
    
    // Layer settings
    private int maxLayers;
    private LayerCollisionMatrix layerMatrix;
    
    // Solver settings
    private float solverTolerance;
    private float solverDamping;
    private boolean enableSleeping;
    private float sleepThreshold;
    private float sleepTime;
    
    public PhysicsConfig() {
        // Default values
        this.gravityX = 0f;
        this.gravityY = -9.81f;
        this.gravityZ = 0f;
        
        this.fixedTimeStep = 1.0f / 60.0f;
        this.maxSubSteps = 8;
        this.velocityIterations = 8;
        this.positionIterations = 3;
        
        this.defaultRestitution = 0.5f;
        this.defaultFriction = 0.5f;
        this.defaultMass = 1.0f;
        this.enableContinuousCollision = true;
        this.ccdThreshold = 0.1f;
        
        this.maxLayers = 32;
        this.layerMatrix = new LayerCollisionMatrix(maxLayers);
        
        this.solverTolerance = 0.01f;
        this.solverDamping = 0.98f;
        this.enableSleeping = true;
        this.sleepThreshold = 0.01f;
        this.sleepTime = 0.5f;
    }
    
    // Getters and setters
    
    public float getGravityX() {
        return gravityX;
    }
    
    public void setGravityX(float gravityX) {
        this.gravityX = gravityX;
    }
    
    public float getGravityY() {
        return gravityY;
    }
    
    public void setGravityY(float gravityY) {
        this.gravityY = gravityY;
    }
    
    public float getGravityZ() {
        return gravityZ;
    }
    
    public void setGravityZ(float gravityZ) {
        this.gravityZ = gravityZ;
    }
    
    public void setGravity(float x, float y, float z) {
        this.gravityX = x;
        this.gravityY = y;
        this.gravityZ = z;
    }
    
    public float getFixedTimeStep() {
        return fixedTimeStep;
    }
    
    public void setFixedTimeStep(float fixedTimeStep) {
        this.fixedTimeStep = fixedTimeStep;
    }
    
    public int getMaxSubSteps() {
        return maxSubSteps;
    }
    
    public void setMaxSubSteps(int maxSubSteps) {
        this.maxSubSteps = maxSubSteps;
    }
    
    public int getVelocityIterations() {
        return velocityIterations;
    }
    
    public void setVelocityIterations(int velocityIterations) {
        this.velocityIterations = velocityIterations;
    }
    
    public int getPositionIterations() {
        return positionIterations;
    }
    
    public void setPositionIterations(int positionIterations) {
        this.positionIterations = positionIterations;
    }
    
    public float getDefaultRestitution() {
        return defaultRestitution;
    }
    
    public void setDefaultRestitution(float defaultRestitution) {
        this.defaultRestitution = defaultRestitution;
    }
    
    public float getDefaultFriction() {
        return defaultFriction;
    }
    
    public void setDefaultFriction(float defaultFriction) {
        this.defaultFriction = defaultFriction;
    }
    
    public float getDefaultMass() {
        return defaultMass;
    }
    
    public void setDefaultMass(float defaultMass) {
        this.defaultMass = defaultMass;
    }
    
    public boolean isEnableContinuousCollision() {
        return enableContinuousCollision;
    }
    
    public void setEnableContinuousCollision(boolean enableContinuousCollision) {
        this.enableContinuousCollision = enableContinuousCollision;
    }
    
    public float getCcdThreshold() {
        return ccdThreshold;
    }
    
    public void setCcdThreshold(float ccdThreshold) {
        this.ccdThreshold = ccdThreshold;
    }
    
    public int getMaxLayers() {
        return maxLayers;
    }
    
    public void setMaxLayers(int maxLayers) {
        this.maxLayers = maxLayers;
        this.layerMatrix = new LayerCollisionMatrix(maxLayers);
    }
    
    public LayerCollisionMatrix getLayerMatrix() {
        return layerMatrix;
    }
    
    public void setLayerMatrix(LayerCollisionMatrix layerMatrix) {
        this.layerMatrix = layerMatrix;
    }
    
    public float getSolverTolerance() {
        return solverTolerance;
    }
    
    public void setSolverTolerance(float solverTolerance) {
        this.solverTolerance = solverTolerance;
    }
    
    public float getSolverDamping() {
        return solverDamping;
    }
    
    public void setSolverDamping(float solverDamping) {
        this.solverDamping = solverDamping;
    }
    
    public boolean isEnableSleeping() {
        return enableSleeping;
    }
    
    public void setEnableSleeping(boolean enableSleeping) {
        this.enableSleeping = enableSleeping;
    }
    
    public float getSleepThreshold() {
        return sleepThreshold;
    }
    
    public void setSleepThreshold(float sleepThreshold) {
        this.sleepThreshold = sleepThreshold;
    }
    
    public float getSleepTime() {
        return sleepTime;
    }
    
    public void setSleepTime(float sleepTime) {
        this.sleepTime = sleepTime;
    }
    
    @Override
    public String toString() {
        return "PhysicsConfig{" +
                "gravity=(" + gravityX + ", " + gravityY + ", " + gravityZ + ")" +
                ", fixedTimeStep=" + fixedTimeStep +
                ", maxSubSteps=" + maxSubSteps +
                ", velocityIterations=" + velocityIterations +
                ", positionIterations=" + positionIterations +
                ", enableContinuousCollision=" + enableContinuousCollision +
                ", enableSleeping=" + enableSleeping +
                '}';
    }
    
    /**
     * LayerCollisionMatrix class
     * Manages collision matrix between physics layers
     */
    public static class LayerCollisionMatrix {
        private boolean[][] matrix;
        private int size;
        
        public LayerCollisionMatrix(int size) {
            this.size = size;
            this.matrix = new boolean[size][size];
            
            // By default, all layers collide
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = true;
                }
            }
        }
        
        /**
         * Check if two layers collide
         */
        public boolean canCollide(int layer1, int layer2) {
            if (layer1 < 0 || layer1 >= size || layer2 < 0 || layer2 >= size) {
                return false;
            }
            return matrix[layer1][layer2];
        }
        
        /**
         * Set collision between two layers
         */
        public void setCollision(int layer1, int layer2, boolean collide) {
            if (layer1 < 0 || layer1 >= size || layer2 < 0 || layer2 >= size) {
                return;
            }
            matrix[layer1][layer2] = collide;
            matrix[layer2][layer1] = collide;
        }
        
        /**
         * Set collision for a layer with all other layers
         */
        public void setCollisionWithAll(int layer, boolean collide) {
            if (layer < 0 || layer >= size) {
                return;
            }
            for (int i = 0; i < size; i++) {
                matrix[layer][i] = collide;
                matrix[i][layer] = collide;
            }
        }
        
        /**
         * Ignore collision between two layers
         */
        public void ignoreCollision(int layer1, int layer2) {
            setCollision(layer1, layer2, false);
        }
        
        /**
         * Ignore collision for a layer with all other layers
         */
        public void ignoreCollisionWithAll(int layer) {
            setCollisionWithAll(layer, false);
        }
        
        // Getters and setters
        
        public boolean[][] getMatrix() {
            boolean[][] copy = new boolean[size][size];
            for (int i = 0; i < size; i++) {
                System.arraycopy(matrix[i], 0, copy[i], 0, size);
            }
            return copy;
        }
        
        public int getSize() {
            return size;
        }
        
        @Override
        public String toString() {
            return "LayerCollisionMatrix{" +
                    "size=" + size +
                    '}';
        }
    }
}

package com.bothengine.physics;

import com.bothengine.core.ecs.Entity;
import com.bothengine.rendering.Vector3;

/**
 * PhysicsJoint
 * Base class for all physics joints
 * Similar to Unity's Joint
 */
public class PhysicsJoint {
    private String name;
    private Entity connectedBody;
    private Vector3 anchor;
    private Vector3 axis;
    private Vector3 swingAxis;
    private boolean enableProjection;
    private float projectionDistance;
    private float projectionAngle;
    private boolean enableCollision;
    private float breakForce;
    private float breakTorque;
    
    public PhysicsJoint() {
        this.name = "Joint";
        this.connectedBody = null;
        this.anchor = new Vector3();
        this.axis = new Vector3(0, 1, 0);
        this.swingAxis = new Vector3(1, 0, 0);
        this.enableProjection = false;
        this.projectionDistance = 0.1f;
        this.projectionAngle = 180f;
        this.enableCollision = false;
        this.breakForce = Float.POSITIVE_INFINITY;
        this.breakTorque = Float.POSITIVE_INFINITY;
    }
    
    /**
     * Create a physics joint with name
     */
    public PhysicsJoint(String name) {
        this();
        this.name = name;
    }
    
    /**
     * Initialize the joint
     */
    public void initialize() {
        // Override in subclasses
    }
    
    /**
     * Update the joint
     */
    public void update(float deltaTime) {
        // Override in subclasses
    }
    
    /**
     * Check if the joint is broken
     */
    public boolean isBroken() {
        // Check if break force or torque exceeded
        return false;
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Entity getConnectedBody() {
        return connectedBody;
    }
    
    public void setConnectedBody(Entity connectedBody) {
        this.connectedBody = connectedBody;
    }
    
    public Vector3 getAnchor() {
        return anchor;
    }
    
    public void setAnchor(Vector3 anchor) {
        this.anchor = anchor;
    }
    
    public void setAnchor(float x, float y, float z) {
        this.anchor = new Vector3(x, y, z);
    }
    
    public Vector3 getAxis() {
        return axis;
    }
    
    public void setAxis(Vector3 axis) {
        this.axis = axis;
    }
    
    public void setAxis(float x, float y, float z) {
        this.axis = new Vector3(x, y, z);
    }
    
    public Vector3 getSwingAxis() {
        return swingAxis;
    }
    
    public void setSwingAxis(Vector3 swingAxis) {
        this.swingAxis = swingAxis;
    }
    
    public void setSwingAxis(float x, float y, float z) {
        this.swingAxis = new Vector3(x, y, z);
    }
    
    public boolean isEnableProjection() {
        return enableProjection;
    }
    
    public void setEnableProjection(boolean enableProjection) {
        this.enableProjection = enableProjection;
    }
    
    public float getProjectionDistance() {
        return projectionDistance;
    }
    
    public void setProjectionDistance(float projectionDistance) {
        this.projectionDistance = projectionDistance;
    }
    
    public float getProjectionAngle() {
        return projectionAngle;
    }
    
    public void setProjectionAngle(float projectionAngle) {
        this.projectionAngle = projectionAngle;
    }
    
    public boolean isEnableCollision() {
        return enableCollision;
    }
    
    public void setEnableCollision(boolean enableCollision) {
        this.enableCollision = enableCollision;
    }
    
    public float getBreakForce() {
        return breakForce;
    }
    
    public void setBreakForce(float breakForce) {
        this.breakForce = breakForce;
    }
    
    public float getBreakTorque() {
        return breakTorque;
    }
    
    public void setBreakTorque(float breakTorque) {
        this.breakTorque = breakTorque;
    }
    
    @Override
    public String toString() {
        return "PhysicsJoint{" +
                "name='" + name + '\'' +
                ", connectedBody=" + (connectedBody != null ? connectedBody.getName() : "null") +
                ", anchor=" + anchor +
                ", axis=" + axis +
                '}';
    }
    
    /**
     * FixedJoint
     * Joint that welds two bodies together
     */
    public static class FixedJoint extends PhysicsJoint {
        public FixedJoint() {
            super("FixedJoint");
        }
        
        @Override
        public void initialize() {
            // Initialize fixed joint
        }
    }
    
    /**
     * HingeJoint
     * Joint that allows rotation around a single axis
     */
    public static class HingeJoint extends PhysicsJoint {
        private float lowerAngle;
        private float upperAngle;
        private float limitBounce;
        private float limitContactDistance;
        private boolean useMotor;
        private float motorSpeed;
        private float maxMotorForce;
        
        public HingeJoint() {
            super("HingeJoint");
            this.lowerAngle = -180f;
            this.upperAngle = 180f;
            this.limitBounce = 0f;
            this.limitContactDistance = 0.1f;
            this.useMotor = false;
            this.motorSpeed = 0f;
            this.maxMotorForce = Float.POSITIVE_INFINITY;
        }
        
        // Getters and setters
        
        public float getLowerAngle() {
            return lowerAngle;
        }
        
        public void setLowerAngle(float lowerAngle) {
            this.lowerAngle = lowerAngle;
        }
        
        public float getUpperAngle() {
            return upperAngle;
        }
        
        public void setUpperAngle(float upperAngle) {
            this.upperAngle = upperAngle;
        }
        
        public float getLimitBounce() {
            return limitBounce;
        }
        
        public void setLimitBounce(float limitBounce) {
            this.limitBounce = limitBounce;
        }
        
        public float getLimitContactDistance() {
            return limitContactDistance;
        }
        
        public void setLimitContactDistance(float limitContactDistance) {
            this.limitContactDistance = limitContactDistance;
        }
        
        public boolean isUseMotor() {
            return useMotor;
        }
        
        public void setUseMotor(boolean useMotor) {
            this.useMotor = useMotor;
        }
        
        public float getMotorSpeed() {
            return motorSpeed;
        }
        
        public void setMotorSpeed(float motorSpeed) {
            this.motorSpeed = motorSpeed;
        }
        
        public float getMaxMotorForce() {
            return maxMotorForce;
        }
        
        public void setMaxMotorForce(float maxMotorForce) {
            this.maxMotorForce = maxMotorForce;
        }
        
        @Override
        public String toString() {
            return "HingeJoint{" +
                    "name='" + getName() + '\'' +
                    ", lowerAngle=" + lowerAngle +
                    ", upperAngle=" + upperAngle +
                    ", useMotor=" + useMotor +
                    '}';
        }
    }
    
    /**
     * BallAndSocketJoint
     * Joint that allows free rotation in all directions
     */
    public static class BallAndSocketJoint extends PhysicsJoint {
        private float swingLimit;
        private float twistLimit;
        private float swingBounce;
        private float twistBounce;
        private float minDistance;
        private float maxDistance;
        private float tolerance;
        
        public BallAndSocketJoint() {
            super("BallAndSocketJoint");
            this.swingLimit = 180f;
            this.twistLimit = 180f;
            this.swingBounce = 0f;
            this.twistBounce = 0f;
            this.minDistance = 0f;
            this.maxDistance = Float.POSITIVE_INFINITY;
            this.tolerance = 0.1f;
        }
        
        // Getters and setters
        
        public float getSwingLimit() {
            return swingLimit;
        }
        
        public void setSwingLimit(float swingLimit) {
            this.swingLimit = swingLimit;
        }
        
        public float getTwistLimit() {
            return twistLimit;
        }
        
        public void setTwistLimit(float twistLimit) {
            this.twistLimit = twistLimit;
        }
        
        public float getSwingBounce() {
            return swingBounce;
        }
        
        public void setSwingBounce(float swingBounce) {
            this.swingBounce = swingBounce;
        }
        
        public float getTwistBounce() {
            return twistBounce;
        }
        
        public void setTwistBounce(float twistBounce) {
            this.twistBounce = twistBounce;
        }
        
        public float getMinDistance() {
            return minDistance;
        }
        
        public void setMinDistance(float minDistance) {
            this.minDistance = minDistance;
        }
        
        public float getMaxDistance() {
            return maxDistance;
        }
        
        public void setMaxDistance(float maxDistance) {
            this.maxDistance = maxDistance;
        }
        
        public float getTolerance() {
            return tolerance;
        }
        
        public void setTolerance(float tolerance) {
            this.tolerance = tolerance;
        }
        
        @Override
        public String toString() {
            return "BallAndSocketJoint{" +
                    "name='" + getName() + '\'' +
                    ", swingLimit=" + swingLimit +
                    ", twistLimit=" + twistLimit +
                    '}';
        }
    }
    
    /**
     * SpringJoint
     * Joint that connects two bodies with a spring
     */
    public static class SpringJoint extends PhysicsJoint {
        private float spring;
        private float damper;
        private float minDistance;
        private float maxDistance;
        private float tolerance;
        
        public SpringJoint() {
            super("SpringJoint");
            this.spring = 50f;
            this.damper = 1f;
            this.minDistance = 0f;
            this.maxDistance = Float.POSITIVE_INFINITY;
            this.tolerance = 0.1f;
        }
        
        // Getters and setters
        
        public float getSpring() {
            return spring;
        }
        
        public void setSpring(float spring) {
            this.spring = spring;
        }
        
        public float getDamper() {
            return damper;
        }
        
        public void setDamper(float damper) {
            this.damper = damper;
        }
        
        public float getMinDistance() {
            return minDistance;
        }
        
        public void setMinDistance(float minDistance) {
            this.minDistance = minDistance;
        }
        
        public float getMaxDistance() {
            return maxDistance;
        }
        
        public void setMaxDistance(float maxDistance) {
            this.maxDistance = maxDistance;
        }
        
        public float getTolerance() {
            return tolerance;
        }
        
        public void setTolerance(float tolerance) {
            this.tolerance = tolerance;
        }
        
        @Override
        public String toString() {
            return "SpringJoint{" +
                    "name='" + getName() + '\'' +
                    ", spring=" + spring +
                    ", damper=" + damper +
                    '}';
        }
    }
}

package com.bothengine.animation;

import com.bothengine.rendering.Vector3;
import com.bothengine.components.Quaternion;
import com.bothengine.rendering.Matrix4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SkeletalAnimation
 * Manages bone hierarchies and skeletal animation
 * Supports skinning and bone transformations
 */
public class SkeletalAnimation {
    private String name;
    private List<Bone> bones;
    private Map<String, Bone> boneMap;
    private Bone rootBone;
    private int boneCount;
    
    // Inverse bind pose matrices for skinning
    private Matrix4[] inverseBindPoses;
    
    // Current pose matrices
    private Matrix4[] currentPose;
    
    // Animation data
    private AnimationClip animationClip;
    
    public SkeletalAnimation(String name) {
        this.name = name;
        this.bones = new ArrayList<>();
        this.boneMap = new HashMap<>();
        this.rootBone = null;
        this.boneCount = 0;
        this.inverseBindPoses = null;
        this.currentPose = null;
        this.animationClip = null;
    }
    
    /**
     * Create a skeletal animation with a root bone
     */
    public static SkeletalAnimation create(String name, Bone rootBone) {
        SkeletalAnimation animation = new SkeletalAnimation(name);
        animation.setRootBone(rootBone);
        animation.buildBoneHierarchy();
        return animation;
    }
    
    /**
     * Set the root bone
     */
    public void setRootBone(Bone rootBone) {
        this.rootBone = rootBone;
        buildBoneHierarchy();
    }
    
    /**
     * Build the bone hierarchy from the root bone
     */
    private void buildBoneHierarchy() {
        bones.clear();
        boneMap.clear();
        
        if (rootBone != null) {
            addBoneRecursive(rootBone);
        }
        
        boneCount = bones.size();
        inverseBindPoses = new Matrix4[boneCount];
        currentPose = new Matrix4[boneCount];
        
        // Compute inverse bind poses
        computeInverseBindPoses();
    }
    
    /**
     * Add a bone and its children recursively
     */
    private void addBoneRecursive(Bone bone) {
        bones.add(bone);
        boneMap.put(bone.getName(), bone);
        bone.setIndex(bones.size() - 1);
        
        for (Bone child : bone.getChildren()) {
            addBoneRecursive(child);
        }
    }
    
    /**
     * Compute inverse bind pose matrices
     */
    private void computeInverseBindPoses() {
        for (int i = 0; i < boneCount; i++) {
            Bone bone = bones.get(i);
            Matrix4 bindPose = bone.getBindPoseMatrix();
            inverseBindPoses[i] = Matrix4.inverse(bindPose);
        }
    }
    
    /**
     * Update the skeletal animation
     */
    public void update(float deltaTime) {
        if (animationClip != null) {
            // Update animation clip
            // This would be handled by the AnimationController
        }
        
        // Update current pose
        updateCurrentPose();
    }
    
    /**
     * Update the current pose matrices
     */
    private void updateCurrentPose() {
        if (rootBone == null) return;
        
        // Update bone transformations
        updateBoneTransforms(rootBone, Matrix4.identity());
        
        // Store current pose
        for (int i = 0; i < boneCount; i++) {
            Bone bone = bones.get(i);
            currentPose[i] = bone.getFinalTransformation();
        }
    }
    
    /**
     * Update bone transformations recursively
     */
    private void updateBoneTransforms(Bone bone, Matrix4 parentTransform) {
        // Compute local transformation
        Matrix4 localTransform = Matrix4.identity();
        localTransform.translate(bone.getPosition());
        localTransform.rotate(bone.getRotation());
        localTransform.scale(bone.getScale());
        
        // Compute final transformation
        Matrix4 finalTransform = Matrix4.multiply(parentTransform, localTransform);
        bone.setFinalTransformation(finalTransform);
        
        // Update children
        for (Bone child : bone.getChildren()) {
            updateBoneTransforms(child, finalTransform);
        }
    }
    
    /**
     * Apply animation from an animation clip
     */
    public void applyAnimation(AnimationClip clip, float time) {
        this.animationClip = clip;
        
        // Apply animation to bones
        for (String boneName : clip.getPositionTrackNames()) {
            Bone bone = boneMap.get(boneName);
            if (bone != null) {
                Vector3 position = clip.samplePosition(boneName, time);
                if (position != null) {
                    bone.setPosition(position);
                }
            }
        }
        
        for (String boneName : clip.getRotationTrackNames()) {
            Bone bone = boneMap.get(boneName);
            if (bone != null) {
                Quaternion rotation = clip.sampleRotation(boneName, time);
                if (rotation != null) {
                    bone.setRotation(rotation);
                }
            }
        }
        
        for (String boneName : clip.getScaleTrackNames()) {
            Bone bone = boneMap.get(boneName);
            if (bone != null) {
                Vector3 scale = clip.sampleScale(boneName, time);
                if (scale != null) {
                    bone.setScale(scale);
                }
            }
        }
    }
    
    /**
     * Get the bone by name
     */
    public Bone getBone(String name) {
        return boneMap.get(name);
    }
    
    /**
     * Get the bone by index
     */
    public Bone getBone(int index) {
        if (index >= 0 && index < boneCount) {
            return bones.get(index);
        }
        return null;
    }
    
    /**
     * Get the inverse bind pose matrix for a bone
     */
    public Matrix4 getInverseBindPose(int boneIndex) {
        if (boneIndex >= 0 && boneIndex < boneCount) {
            return inverseBindPoses[boneIndex];
        }
        return Matrix4.identity();
    }
    
    /**
     * Get the current pose matrix for a bone
     */
    public Matrix4 getCurrentPose(int boneIndex) {
        if (boneIndex >= 0 && boneIndex < boneCount) {
            return currentPose[boneIndex];
        }
        return Matrix4.identity();
    }
    
    /**
     * Get the skinning matrix for a bone (inverse bind pose * current pose)
     */
    public Matrix4 getSkinningMatrix(int boneIndex) {
        if (boneIndex >= 0 && boneIndex < boneCount) {
            return Matrix4.multiply(getInverseBindPose(boneIndex), getCurrentPose(boneIndex));
        }
        return Matrix4.identity();
    }
    
    /**
     * Get all skinning matrices as an array
     */
    public Matrix4[] getSkinningMatrices() {
        Matrix4[] matrices = new Matrix4[boneCount];
        for (int i = 0; i < boneCount; i++) {
            matrices[i] = getSkinningMatrix(i);
        }
        return matrices;
    }
    
    /**
     * Add a bone to the skeleton
     */
    public void addBone(Bone bone) {
        bones.add(bone);
        boneMap.put(bone.getName(), bone);
        bone.setIndex(boneCount);
        boneCount++;
        
        // Resize arrays
        Matrix4[] newInverseBindPoses = new Matrix4[boneCount];
        Matrix4[] newCurrentPose = new Matrix4[boneCount];
        
        System.arraycopy(inverseBindPoses, 0, newInverseBindPoses, 0, inverseBindPoses.length);
        System.arraycopy(currentPose, 0, newCurrentPose, 0, currentPose.length);
        
        inverseBindPoses = newInverseBindPoses;
        currentPose = newCurrentPose;
        
        // Compute inverse bind pose for the new bone
        inverseBindPoses[bone.getIndex()] = Matrix4.inverse(bone.getBindPoseMatrix());
    }
    
    /**
     * Remove a bone from the skeleton
     */
    public void removeBone(Bone bone) {
        int index = bone.getIndex();
        bones.remove(index);
        boneMap.remove(bone.getName());
        boneCount--;
        
        // Rebuild bone indices
        for (int i = index; i < bones.size(); i++) {
            bones.get(i).setIndex(i);
        }
    }
    
    /**
     * Find a bone by name in the hierarchy
     */
    public Bone findBone(String name) {
        return boneMap.get(name);
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<Bone> getBones() {
        return new ArrayList<>(bones);
    }
    
    public Bone getRootBone() {
        return rootBone;
    }
    
    public int getBoneCount() {
        return boneCount;
    }
    
    public AnimationClip getAnimationClip() {
        return animationClip;
    }
    
    public void setAnimationClip(AnimationClip animationClip) {
        this.animationClip = animationClip;
    }
    
    @Override
    public String toString() {
        return "SkeletalAnimation{" +
                "name='" + name + '\'' +
                ", boneCount=" + boneCount +
                ", rootBone=" + (rootBone != null ? rootBone.getName() : "null") +
                '}';
    }
    
    /**
     * Bone class for skeletal animation
     */
    public static class Bone {
        private String name;
        private int index;
        private Vector3 position;
        private Quaternion rotation;
        private Vector3 scale;
        
        private Bone parent;
        private List<Bone> children;
        
        // Bind pose (local space)
        private Matrix4 bindPoseMatrix;
        
        // Final transformation (world space)
        private Matrix4 finalTransformation;
        
        public Bone(String name) {
            this.name = name;
            this.index = -1;
            this.position = new Vector3();
            this.rotation = new Quaternion();
            this.scale = new Vector3(1, 1, 1);
            this.parent = null;
            this.children = new ArrayList<>();
            this.bindPoseMatrix = Matrix4.identity();
            this.finalTransformation = Matrix4.identity();
        }
        
        /**
         * Create a bone with name and parent
         */
        public static Bone create(String name, Bone parent) {
            Bone bone = new Bone(name);
            bone.setParent(parent);
            return bone;
        }
        
        /**
         * Set the parent bone
         */
        public void setParent(Bone parent) {
            if (this.parent != null) {
                this.parent.getChildren().remove(this);
            }
            this.parent = parent;
            if (parent != null) {
                parent.getChildren().add(this);
            }
        }
        
        /**
         * Add a child bone
         */
        public void addChild(Bone child) {
            child.setParent(this);
        }
        
        /**
         * Remove a child bone
         */
        public void removeChild(Bone child) {
            children.remove(child);
            child.parent = null;
        }
        
        /**
         * Compute the bind pose matrix
         */
        public Matrix4 getBindPoseMatrix() {
            Matrix4 matrix = Matrix4.identity();
            matrix.translate(position);
            matrix.rotate(rotation);
            matrix.scale(scale);
            return matrix;
        }
        
        /**
         * Update the bind pose matrix
         */
        public void updateBindPose() {
            bindPoseMatrix = getBindPoseMatrix();
        }
        
        // Getters and setters
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public int getIndex() {
            return index;
        }
        
        public void setIndex(int index) {
            this.index = index;
        }
        
        public Vector3 getPosition() {
            return position;
        }
        
        public void setPosition(Vector3 position) {
            this.position = position;
        }
        
        public Quaternion getRotation() {
            return rotation;
        }
        
        public void setRotation(Quaternion rotation) {
            this.rotation = rotation;
        }
        
        public Vector3 getScale() {
            return scale;
        }
        
        public void setScale(Vector3 scale) {
            this.scale = scale;
        }
        
        public Bone getParent() {
            return parent;
        }
        
        public List<Bone> getChildren() {
            return new ArrayList<>(children);
        }
        
        public Matrix4 getFinalTransformation() {
            return finalTransformation;
        }
        
        public void setFinalTransformation(Matrix4 finalTransformation) {
            this.finalTransformation = finalTransformation;
        }
        
        @Override
        public String toString() {
            return "Bone{" +
                    "name='" + name + '\'' +
                    ", index=" + index +
                    ", position=" + position +
                    ", children=" + children.size() +
                    '}';
        }
    }
}

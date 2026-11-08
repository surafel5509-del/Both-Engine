package com.bothengine.assets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.util.Log;

import com.bothengine.core.EngineConfig;
import com.bothengine.rendering.Mesh;
import com.bothengine.rendering.Model;
import com.bothengine.rendering.Texture;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AssetManager
 * Manages loading and unloading of game assets (textures, meshes, sounds, etc.)
 */
public class AssetManager {
    private static final String TAG = "AssetManager";
    
    private final EngineConfig config;
    private final Context context;
    
    // Asset caches
    private final Map<String, Texture> textures;
    private final Map<String, Mesh> meshes;
    private final Map<String, Model> models;
    private final Map<String, Bitmap> bitmaps;
    private final Map<String, Sound> sounds;
    private final Map<String, MediaPlayer> music;
    private final Map<String, String> textFiles;
    private final Map<String, byte[]> binaryFiles;
    
    // Loading
    private final ExecutorService loadingThreadPool;
    private final Map<String, AssetLoader> activeLoaders;
    private int assetsLoading;
    
    // Configuration
    private boolean useAsyncLoading;
    private int maxTextureSize;
    private boolean compressTextures;
    private boolean generateMipmaps;
    
    public AssetManager(EngineConfig config) {
        this.config = config;
        this.context = config.getContext();
        
        this.textures = new HashMap<>();
        this.meshes = new HashMap<>();
        this.models = new HashMap<>();
        this.bitmaps = new HashMap<>();
        this.sounds = new HashMap<>();
        this.music = new HashMap<>();
        this.textFiles = new HashMap<>();
        this.binaryFiles = new HashMap<>();
        
        this.loadingThreadPool = Executors.newFixedThreadPool(4);
        this.activeLoaders = new HashMap<>();
        this.assetsLoading = 0;
        
        this.useAsyncLoading = true;
        this.maxTextureSize = 2048;
        this.compressTextures = true;
        this.generateMipmaps = true;
        
        Log.i(TAG, "AssetManager initialized");
    }
    
    /**
     * Load a texture from assets
     */
    public Texture loadTexture(String path) {
        return loadTexture(path, null);
    }
    
    /**
     * Load a texture from assets with a custom name
     */
    public Texture loadTexture(String path, String name) {
        if (name == null) {
            name = path;
        }
        
        // Check if already loaded
        Texture texture = textures.get(name);
        if (texture != null) {
            Log.d(TAG, "Texture already loaded: " + name);
            return texture;
        }
        
        // Create texture
        texture = new Texture(name);
        textures.put(name, texture);
        
        // Load
        if (useAsyncLoading) {
            loadTextureAsync(path, texture);
        } else {
            loadTextureSync(path, texture);
        }
        
        return texture;
    }
    
    /**
     * Load texture synchronously
     */
    private void loadTextureSync(String path, Texture texture) {
        assetsLoading++;
        
        try {
            boolean success = texture.loadFromAssets(context, path);
            if (success) {
                if (generateMipmaps) {
                    texture.setMipmapped(true);
                    texture.generateMipmaps();
                }
                Log.d(TAG, "Texture loaded: " + texture.getName());
            } else {
                Log.e(TAG, "Failed to load texture: " + path);
                textures.remove(texture.getName());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading texture: " + path, e);
            textures.remove(texture.getName());
        }
        
        assetsLoading--;
    }
    
    /**
     * Load texture asynchronously
     */
    private void loadTextureAsync(String path, Texture texture) {
        assetsLoading++;
        
        AssetLoader<Texture> loader = new AssetLoader<>(
            path,
            () -> {
                try {
                    boolean success = texture.loadFromAssets(context, path);
                    if (success) {
                        if (generateMipmaps) {
                            texture.setMipmapped(true);
                            texture.generateMipmaps();
                        }
                        Log.d(TAG, "Texture loaded async: " + texture.getName());
                        return texture;
                    } else {
                        Log.e(TAG, "Failed to load texture async: " + path);
                        textures.remove(texture.getName());
                        return null;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error loading texture async: " + path, e);
                    textures.remove(texture.getName());
                    return null;
                }
            },
            (result) -> {
                assetsLoading--;
                activeLoaders.remove(path);
            }
        );
        
        activeLoaders.put(path, loader);
        loadingThreadPool.execute(loader);
    }
    
    /**
     * Load a mesh from OBJ file
     */
    public Mesh loadMesh(String path) {
        return loadMesh(path, null);
    }
    
    /**
     * Load a mesh from OBJ file with a custom name
     */
    public Mesh loadMesh(String path, String name) {
        if (name == null) {
            name = path;
        }
        
        // Check if already loaded
        Mesh mesh = meshes.get(name);
        if (mesh != null) {
            Log.d(TAG, "Mesh already loaded: " + name);
            return mesh;
        }
        
        // Create mesh
        mesh = new Mesh(name);
        meshes.put(name, mesh);
        
        // Load
        if (useAsyncLoading) {
            loadMeshAsync(path, mesh);
        } else {
            loadMeshSync(path, mesh);
        }
        
        return mesh;
    }
    
    /**
     * Load mesh synchronously
     */
    private void loadMeshSync(String path, Mesh mesh) {
        assetsLoading++;
        
        try {
            boolean success = MeshLoader.loadOBJ(context, path, mesh);
            if (success) {
                mesh.uploadToGPU();
                Log.d(TAG, "Mesh loaded: " + mesh.getName());
            } else {
                Log.e(TAG, "Failed to load mesh: " + path);
                meshes.remove(mesh.getName());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading mesh: " + path, e);
            meshes.remove(mesh.getName());
        }
        
        assetsLoading--;
    }
    
    /**
     * Load mesh asynchronously
     */
    private void loadMeshAsync(String path, Mesh mesh) {
        assetsLoading++;
        
        AssetLoader<Mesh> loader = new AssetLoader<>(
            path,
            () -> {
                try {
                    boolean success = MeshLoader.loadOBJ(context, path, mesh);
                    if (success) {
                        mesh.uploadToGPU();
                        Log.d(TAG, "Mesh loaded async: " + mesh.getName());
                        return mesh;
                    } else {
                        Log.e(TAG, "Failed to load mesh async: " + path);
                        meshes.remove(mesh.getName());
                        return null;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error loading mesh async: " + path, e);
                    meshes.remove(mesh.getName());
                    return null;
                }
            },
            (result) -> {
                assetsLoading--;
                activeLoaders.remove(path);
            }
        );
        
        activeLoaders.put(path, loader);
        loadingThreadPool.execute(loader);
    }
    
    /**
     * Load a model (composed of multiple meshes)
     */
    public Model loadModel(String path) {
        return loadModel(path, null);
    }
    
    /**
     * Load a model with a custom name
     */
    public Model loadModel(String path, String name) {
        if (name == null) {
            name = path;
        }
        
        // Check if already loaded
        Model model = models.get(name);
        if (model != null) {
            Log.d(TAG, "Model already loaded: " + name);
            return model;
        }
        
        // Create model
        model = new Model(name);
        models.put(name, model);
        
        // Load
        if (useAsyncLoading) {
            loadModelAsync(path, model);
        } else {
            loadModelSync(path, model);
        }
        
        return model;
    }
    
    /**
     * Load model synchronously
     */
    private void loadModelSync(String path, Model model) {
        assetsLoading++;
        
        try {
            boolean success = ModelLoader.loadModel(context, path, model);
            if (success) {
                Log.d(TAG, "Model loaded: " + model.getName());
            } else {
                Log.e(TAG, "Failed to load model: " + path);
                models.remove(model.getName());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading model: " + path, e);
            models.remove(model.getName());
        }
        
        assetsLoading--;
    }
    
    /**
     * Load model asynchronously
     */
    private void loadModelAsync(String path, Model model) {
        assetsLoading++;
        
        AssetLoader<Model> loader = new AssetLoader<>(
            path,
            () -> {
                try {
                    boolean success = ModelLoader.loadModel(context, path, model);
                    if (success) {
                        Log.d(TAG, "Model loaded async: " + model.getName());
                        return model;
                    } else {
                        Log.e(TAG, "Failed to load model async: " + path);
                        models.remove(model.getName());
                        return null;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error loading model async: " + path, e);
                    models.remove(model.getName());
                    return null;
                }
            },
            (result) -> {
                assetsLoading--;
                activeLoaders.remove(path);
            }
        );
        
        activeLoaders.put(path, loader);
        loadingThreadPool.execute(loader);
    }
    
    /**
     * Load a sound effect
     */
    public Sound loadSound(String path) {
        return loadSound(path, null);
    }
    
    /**
     * Load a sound effect with a custom name
     */
    public Sound loadSound(String path, String name) {
        if (name == null) {
            name = path;
        }
        
        // Check if already loaded
        Sound sound = sounds.get(name);
        if (sound != null) {
            Log.d(TAG, "Sound already loaded: " + name);
            return sound;
        }
        
        // Create sound
        sound = new Sound(context, path);
        sounds.put(name, sound);
        
        return sound;
    }
    
    /**
     * Load music
     */
    public MediaPlayer loadMusic(String path) {
        return loadMusic(path, null);
    }
    
    /**
     * Load music with a custom name
     */
    public MediaPlayer loadMusic(String path, String name) {
        if (name == null) {
            name = path;
        }
        
        // Check if already loaded
        MediaPlayer player = music.get(name);
        if (player != null) {
            Log.d(TAG, "Music already loaded: " + name);
            return player;
        }
        
        // Create media player
        try {
            player = MediaPlayer.create(context, getResourceId(path));
            if (player != null) {
                player.setLooping(false);
                music.put(name, player);
                Log.d(TAG, "Music loaded: " + name);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading music: " + path, e);
        }
        
        return player;
    }
    
    /**
     * Load a text file
     */
    public String loadText(String path) {
        return loadText(path, null);
    }
    
    /**
     * Load a text file with a custom name
     */
    public String loadText(String path, String name) {
        if (name == null) {
            name = path;
        }
        
        // Check if already loaded
        String text = textFiles.get(name);
        if (text != null) {
            Log.d(TAG, "Text file already loaded: " + name);
            return text;
        }
        
        // Load
        try {
            text = readTextFile(path);
            if (text != null) {
                textFiles.put(name, text);
                Log.d(TAG, "Text file loaded: " + name);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading text file: " + path, e);
        }
        
        return text;
    }
    
    /**
     * Load a binary file
     */
    public byte[] loadBinary(String path) {
        return loadBinary(path, null);
    }
    
    /**
     * Load a binary file with a custom name
     */
    public byte[] loadBinary(String path, String name) {
        if (name == null) {
            name = path;
        }
        
        // Check if already loaded
        byte[] data = binaryFiles.get(name);
        if (data != null) {
            Log.d(TAG, "Binary file already loaded: " + name);
            return data;
        }
        
        // Load
        try {
            data = readBinaryFile(path);
            if (data != null) {
                binaryFiles.put(name, data);
                Log.d(TAG, "Binary file loaded: " + name);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading binary file: " + path, e);
        }
        
        return data;
    }
    
    /**
     * Load a bitmap
     */
    public Bitmap loadBitmap(String path) {
        return loadBitmap(path, null);
    }
    
    /**
     * Load a bitmap with a custom name
     */
    public Bitmap loadBitmap(String path, String name) {
        if (name == null) {
            name = path;
        }
        
        // Check if already loaded
        Bitmap bitmap = bitmaps.get(name);
        if (bitmap != null) {
            Log.d(TAG, "Bitmap already loaded: " + name);
            return bitmap;
        }
        
        // Load
        try {
            InputStream inputStream = context.getAssets().open(path);
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            
            if (bitmap != null) {
                bitmaps.put(name, bitmap);
                Log.d(TAG, "Bitmap loaded: " + name);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading bitmap: " + path, e);
        }
        
        return bitmap;
    }
    
    /**
     * Get a texture by name
     */
    public Texture getTexture(String name) {
        return textures.get(name);
    }
    
    /**
     * Get a mesh by name
     */
    public Mesh getMesh(String name) {
        return meshes.get(name);
    }
    
    /**
     * Get a model by name
     */
    public Model getModel(String name) {
        return models.get(name);
    }
    
    /**
     * Get a sound by name
     */
    public Sound getSound(String name) {
        return sounds.get(name);
    }
    
    /**
     * Get music by name
     */
    public MediaPlayer getMusic(String name) {
        return music.get(name);
    }
    
    /**
     * Get a text file by name
     */
    public String getText(String name) {
        return textFiles.get(name);
    }
    
    /**
     * Get a binary file by name
     */
    public byte[] getBinary(String name) {
        return binaryFiles.get(name);
    }
    
    /**
     * Get a bitmap by name
     */
    public Bitmap getBitmap(String name) {
        return bitmaps.get(name);
    }
    
    /**
     * Check if an asset is loaded
     */
    public boolean isLoaded(String name) {
        return textures.containsKey(name) || 
               meshes.containsKey(name) || 
               models.containsKey(name) ||
               sounds.containsKey(name) ||
               music.containsKey(name) ||
               textFiles.containsKey(name) ||
               binaryFiles.containsKey(name) ||
               bitmaps.containsKey(name);
    }
    
    /**
     * Check if an asset is currently loading
     */
    public boolean isLoading(String path) {
        return activeLoaders.containsKey(path);
    }
    
    /**
     * Get the number of assets currently loading
     */
    public int getAssetsLoading() {
        return assetsLoading;
    }
    
    /**
     * Unload a texture
     */
    public void unloadTexture(String name) {
        Texture texture = textures.remove(name);
        if (texture != null) {
            texture.delete();
            Log.d(TAG, "Texture unloaded: " + name);
        }
    }
    
    /**
     * Unload a mesh
     */
    public void unloadMesh(String name) {
        Mesh mesh = meshes.remove(name);
        if (mesh != null) {
            mesh.delete();
            Log.d(TAG, "Mesh unloaded: " + name);
        }
    }
    
    /**
     * Unload a model
     */
    public void unloadModel(String name) {
        Model model = models.remove(name);
        if (model != null) {
            model.delete();
            Log.d(TAG, "Model unloaded: " + name);
        }
    }
    
    /**
     * Unload a sound
     */
    public void unloadSound(String name) {
        Sound sound = sounds.remove(name);
        if (sound != null) {
            sound.release();
            Log.d(TAG, "Sound unloaded: " + name);
        }
    }
    
    /**
     * Unload music
     */
    public void unloadMusic(String name) {
        MediaPlayer player = music.remove(name);
        if (player != null) {
            player.release();
            Log.d(TAG, "Music unloaded: " + name);
        }
    }
    
    /**
     * Unload a text file
     */
    public void unloadText(String name) {
        textFiles.remove(name);
        Log.d(TAG, "Text file unloaded: " + name);
    }
    
    /**
     * Unload a binary file
     */
    public void unloadBinary(String name) {
        binaryFiles.remove(name);
        Log.d(TAG, "Binary file unloaded: " + name);
    }
    
    /**
     * Unload a bitmap
     */
    public void unloadBitmap(String name) {
        Bitmap bitmap = bitmaps.remove(name);
        if (bitmap != null) {
            bitmap.recycle();
            Log.d(TAG, "Bitmap unloaded: " + name);
        }
    }
    
    /**
     * Unload all assets
     */
    public void unloadAll() {
        // Unload textures
        for (Texture texture : textures.values()) {
            texture.delete();
        }
        textures.clear();
        
        // Unload meshes
        for (Mesh mesh : meshes.values()) {
            mesh.delete();
        }
        meshes.clear();
        
        // Unload models
        for (Model model : models.values()) {
            model.delete();
        }
        models.clear();
        
        // Unload sounds
        for (Sound sound : sounds.values()) {
            sound.release();
        }
        sounds.clear();
        
        // Unload music
        for (MediaPlayer player : music.values()) {
            player.release();
        }
        music.clear();
        
        // Clear other assets
        textFiles.clear();
        binaryFiles.clear();
        bitmaps.clear();
        
        Log.i(TAG, "All assets unloaded");
    }
    
    /**
     * Shutdown the asset manager
     */
    public void shutdown() {
        unloadAll();
        
        // Shutdown thread pool
        loadingThreadPool.shutdown();
        activeLoaders.clear();
        
        Log.i(TAG, "AssetManager shutdown");
    }
    
    /**
     * Read a text file from assets
     */
    private String readTextFile(String path) throws IOException {
        InputStream inputStream = context.getAssets().open(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        
        reader.close();
        inputStream.close();
        
        return builder.toString();
    }
    
    /**
     * Read a binary file from assets
     */
    private byte[] readBinaryFile(String path) throws IOException {
        InputStream inputStream = context.getAssets().open(path);
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        inputStream.close();
        return buffer;
    }
    
    /**
     * Get resource ID from path (for raw resources)
     */
    private int getResourceId(String path) {
        // Remove extension
        String name = path.substring(0, path.lastIndexOf('.'));
        return context.getResources().getIdentifier(name, "raw", context.getPackageName());
    }
    
    // Getters and setters
    
    public boolean isUseAsyncLoading() {
        return useAsyncLoading;
    }
    
    public void setUseAsyncLoading(boolean useAsyncLoading) {
        this.useAsyncLoading = useAsyncLoading;
    }
    
    public int getMaxTextureSize() {
        return maxTextureSize;
    }
    
    public void setMaxTextureSize(int maxTextureSize) {
        this.maxTextureSize = maxTextureSize;
    }
    
    public boolean isCompressTextures() {
        return compressTextures;
    }
    
    public void setCompressTextures(boolean compressTextures) {
        this.compressTextures = compressTextures;
    }
    
    public boolean isGenerateMipmaps() {
        return generateMipmaps;
    }
    
    public void setGenerateMipmaps(boolean generateMipmaps) {
        this.generateMipmaps = generateMipmaps;
    }
    
    @Override
    public String toString() {
        return "AssetManager{" +
                "textures=" + textures.size() +
                ", meshes=" + meshes.size() +
                ", models=" + models.size() +
                ", sounds=" + sounds.size() +
                ", music=" + music.size() +
                ", textFiles=" + textFiles.size() +
                ", binaryFiles=" + binaryFiles.size() +
                ", bitmaps=" + bitmaps.size() +
                ", assetsLoading=" + assetsLoading +
                '}';
    }
    
    /**
     * AssetLoader class for async loading
     */
    private static class AssetLoader<T> implements Runnable {
        private final String path;
        private final AssetLoaderTask<T> task;
        private final AssetLoaderCallback<T> callback;
        
        public AssetLoader(String path, AssetLoaderTask<T> task, AssetLoaderCallback<T> callback) {
            this.path = path;
            this.task = task;
            this.callback = callback;
        }
        
        @Override
        public void run() {
            T result = task.load();
            if (callback != null) {
                callback.onComplete(result);
            }
        }
    }
    
    /**
     * AssetLoaderTask interface
     */
    private interface AssetLoaderTask<T> {
        T load();
    }
    
    /**
     * AssetLoaderCallback interface
     */
    private interface AssetLoaderCallback<T> {
        void onComplete(T result);
    }
}

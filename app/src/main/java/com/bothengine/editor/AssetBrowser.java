package com.bothengine.editor;

import com.bothengine.assets.AssetManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * AssetBrowser
 * Browser for viewing and importing assets
 * Similar to Unity's Project window
 */
public class AssetBrowser extends EditorWindow {
    private AssetManager assetManager;
    private String currentPath;
    private String rootPath;
    private List<AssetItem> items;
    private AssetItem selectedItem;
    private List<AssetItem> selectedItems;
    
    private boolean showFolders;
    private boolean showFiles;
    private boolean showHidden;
    private boolean largeIcons;
    
    // Filter settings
    private String searchFilter;
    private List<String> typeFilters;
    
    // View mode
    private ViewMode viewMode;
    
    public AssetBrowser() {
        super("Assets");
        this.assetManager = null;
        this.currentPath = "";
        this.rootPath = "assets";
        this.items = new ArrayList<>();
        this.selectedItem = null;
        this.selectedItems = new ArrayList<>();
        this.showFolders = true;
        this.showFiles = true;
        this.showHidden = false;
        this.largeIcons = true;
        this.searchFilter = "";
        this.typeFilters = new ArrayList<>();
        this.viewMode = ViewMode.LIST;
    }
    
    /**
     * Initialize the asset browser
     */
    @Override
    public void onInitialize() {
        super.onInitialize();
        
        if (getEditorManager() != null) {
            this.assetManager = getEditorManager().getEngine().getAssetManager();
            this.rootPath = getEditorManager().getSettings().getAssetPath();
        }
        
        // Load root directory
        loadDirectory(rootPath);
    }
    
    /**
     * Update the asset browser
     */
    @Override
    public void onUpdate(float deltaTime) {
        super.onUpdate(deltaTime);
        
        // Refresh if needed
    }
    
    /**
     * Render the asset browser
     */
    @Override
    public void onRender() {
        super.onRender();
        
        // Render path navigation
        renderPathNavigation();
        
        // Render search and filter controls
        renderSearchControls();
        
        // Render view mode controls
        renderViewModeControls();
        
        // Render items
        renderItems();
    }
    
    /**
     * Render path navigation
     */
    private void renderPathNavigation() {
        // Render breadcrumb navigation for current path
    }
    
    /**
     * Render search and filter controls
     */
    private void renderSearchControls() {
        // Render search field
        // Render type filter dropdown
    }
    
    /**
     * Render view mode controls
     */
    private void renderViewModeControls() {
        // Render buttons for list/grid view modes
    }
    
    /**
     * Render items
     */
    private void renderItems() {
        switch (viewMode) {
            case LIST:
                renderListView();
                break;
            case GRID:
                renderGridView();
                break;
        }
    }
    
    /**
     * Render list view
     */
    private void renderListView() {
        for (AssetItem item : items) {
            renderListItem(item);
        }
    }
    
    /**
     * Render a single item in list view
     */
    private void renderListItem(AssetItem item) {
        // Render item name, type, size, date
    }
    
    /**
     * Render grid view
     */
    private void renderGridView() {
        // Calculate grid layout
        // Render items in grid
    }
    
    /**
     * Load a directory
     */
    public void loadDirectory(String path) {
        currentPath = path;
        items.clear();
        
        File directory = new File(path);
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }
        
        // List files and directories
        File[] files = directory.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (!showHidden && file.isHidden()) {
                continue;
            }
            
            if (file.isDirectory()) {
                if (showFolders) {
                    items.add(new AssetItem(file.getName(), AssetType.FOLDER, file.getPath()));
                }
            } else {
                if (showFiles && passesTypeFilter(file.getName())) {
                    items.add(new AssetItem(file.getName(), getAssetType(file), file.getPath()));
                }
            }
        }
        
        // Sort items (folders first, then files)
        sortItems();
    }
    
    /**
     * Check if a file passes the type filter
     */
    private boolean passesTypeFilter(String filename) {
        if (typeFilters.isEmpty()) {
            return true;
        }
        
        for (String filter : typeFilters) {
            if (filename.toLowerCase().endsWith("." + filter.toLowerCase())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get the asset type from a file
     */
    private AssetType getAssetType(File file) {
        String name = file.getName().toLowerCase();
        
        if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".bmp")) {
            return AssetType.TEXTURE;
        } else if (name.endsWith(".obj") || name.endsWith(".fbx") || name.endsWith(".gltf")) {
            return AssetType.MODEL;
        } else if (name.endsWith(".wav") || name.endsWith(".mp3") || name.endsWith(".ogg")) {
            return AssetType.AUDIO;
        } else if (name.endsWith(".prefab")) {
            return AssetType.PREFAB;
        } else if (name.endsWith(".scene")) {
            return AssetType.SCENE;
        } else if (name.endsWith(".mat")) {
            return AssetType.MATERIAL;
        } else if (name.endsWith(".shader")) {
            return AssetType.SHADER;
        } else if (name.endsWith(".script") || name.endsWith(".lua") || name.endsWith(".js")) {
            return AssetType.SCRIPT;
        } else if (name.endsWith(".anim")) {
            return AssetType.ANIMATION;
        } else if (name.endsWith(".font") || name.endsWith(".ttf")) {
            return AssetType.FONT;
        } else {
            return AssetType.FILE;
        }
    }
    
    /**
     * Sort items (folders first, then files alphabetically)
     */
    private void sortItems() {
        items.sort((a, b) -> {
            // Folders come before files
            if (a.getType() == AssetType.FOLDER && b.getType() != AssetType.FOLDER) {
                return -1;
            } else if (a.getType() != AssetType.FOLDER && b.getType() == AssetType.FOLDER) {
                return 1;
            }
            
            // Sort alphabetically
            return a.getName().compareToIgnoreCase(b.getName());
        });
    }
    
    /**
     * Refresh the current directory
     */
    public void refresh() {
        loadDirectory(currentPath);
    }
    
    /**
     * Navigate to parent directory
     */
    public void navigateUp() {
        File currentDir = new File(currentPath);
        File parentDir = currentDir.getParentFile();
        
        if (parentDir != null && parentDir.exists()) {
            loadDirectory(parentDir.getPath());
        }
    }
    
    /**
     * Navigate into a directory
     */
    public void navigateInto(String directoryName) {
        File newDir = new File(currentPath, directoryName);
        if (newDir.exists() && newDir.isDirectory()) {
            loadDirectory(newDir.getPath());
        }
    }
    
    /**
     * Select an item
     */
    public void selectItem(AssetItem item) {
        if (item == null) {
            clearSelection();
            return;
        }
        
        if (allowMultiSelect() && (getEditorManager().getInput().isShiftDown() || 
            getEditorManager().getInput().isControlDown())) {
            // Multi-select
            if (selectedItems.contains(item)) {
                selectedItems.remove(item);
            } else {
                selectedItems.add(item);
            }
        } else {
            // Single select
            selectedItem = item;
            selectedItems.clear();
            selectedItems.add(item);
        }
    }
    
    /**
     * Check if multi-select is allowed
     */
    private boolean allowMultiSelect() {
        return true;
    }
    
    /**
     * Clear selection
     */
    public void clearSelection() {
        selectedItem = null;
        selectedItems.clear();
    }
    
    /**
     * Import an asset
     */
    public void importAsset(String path) {
        if (assetManager == null) return;
        
        // Use asset manager to load the asset
        // assetManager.loadAsset(path);
    }
    
    /**
     * Create a new folder
     */
    public void createFolder(String name) {
        File newDir = new File(currentPath, name);
        if (!newDir.exists()) {
            newDir.mkdir();
            refresh();
        }
    }
    
    /**
     * Delete selected items
     */
    public void deleteSelected() {
        for (AssetItem item : selectedItems) {
            File file = new File(item.getPath());
            if (file.exists()) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        
        clearSelection();
        refresh();
    }
    
    /**
     * Delete a directory recursively
     */
    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
    
    /**
     * Rename an item
     */
    public void renameItem(AssetItem item, String newName) {
        File oldFile = new File(item.getPath());
        File newFile = new File(oldFile.getParent(), newName);
        
        if (oldFile.exists() && !newFile.exists()) {
            oldFile.renameTo(newFile);
            item.setName(newName);
            item.setPath(newFile.getPath());
            refresh();
        }
    }
    
    /**
     * Set type filters
     */
    public void setTypeFilters(List<String> filters) {
        this.typeFilters = new ArrayList<>(filters);
        refresh();
    }
    
    /**
     * Set search filter
     */
    public void setSearchFilter(String filter) {
        this.searchFilter = filter;
        // Filter items based on search
    }
    
    // Getters and setters
    
    public AssetManager getAssetManager() {
        return assetManager;
    }
    
    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }
    
    public String getCurrentPath() {
        return currentPath;
    }
    
    public String getRootPath() {
        return rootPath;
    }
    
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
    
    public List<AssetItem> getItems() {
        return new ArrayList<>(items);
    }
    
    public AssetItem getSelectedItem() {
        return selectedItem;
    }
    
    public List<AssetItem> getSelectedItems() {
        return new ArrayList<>(selectedItems);
    }
    
    public boolean isShowFolders() {
        return showFolders;
    }
    
    public void setShowFolders(boolean showFolders) {
        this.showFolders = showFolders;
        refresh();
    }
    
    public boolean isShowFiles() {
        return showFiles;
    }
    
    public void setShowFiles(boolean showFiles) {
        this.showFiles = showFiles;
        refresh();
    }
    
    public boolean isShowHidden() {
        return showHidden;
    }
    
    public void setShowHidden(boolean showHidden) {
        this.showHidden = showHidden;
        refresh();
    }
    
    public boolean isLargeIcons() {
        return largeIcons;
    }
    
    public void setLargeIcons(boolean largeIcons) {
        this.largeIcons = largeIcons;
    }
    
    public String getSearchFilter() {
        return searchFilter;
    }
    
    public List<String> getTypeFilters() {
        return new ArrayList<>(typeFilters);
    }
    
    public ViewMode getViewMode() {
        return viewMode;
    }
    
    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
    }
    
    @Override
    public String toString() {
        return "AssetBrowser{" +
                "title='" + getTitle() + '\'' +
                ", currentPath='" + currentPath + '\'' +
                ", items=" + items.size() +
                ", selectedItems=" + selectedItems.size() +
                ", viewMode=" + viewMode +
                '}';
    }
    
    /**
     * AssetItem class for representing files and folders
     */
    public static class AssetItem {
        private String name;
        private AssetType type;
        private String path;
        
        public AssetItem(String name, AssetType type, String path) {
            this.name = name;
            this.type = type;
            this.path = path;
        }
        
        // Getters and setters
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public AssetType getType() {
            return type;
        }
        
        public void setType(AssetType type) {
            this.type = type;
        }
        
        public String getPath() {
            return path;
        }
        
        public void setPath(String path) {
            this.path = path;
        }
        
        public String getExtension() {
            int dotIndex = name.lastIndexOf('.');
            if (dotIndex >= 0) {
                return name.substring(dotIndex + 1);
            }
            return "";
        }
        
        @Override
        public String toString() {
            return "AssetItem{" +
                    "name='" + name + '\'' +
                    ", type=" + type +
                    ", path='" + path + '\'' +
                    '}';
        }
    }
    
    /**
     * AssetType enum
     */
    public enum AssetType {
        FOLDER,
        FILE,
        TEXTURE,
        MODEL,
        AUDIO,
        PREFAB,
        SCENE,
        MATERIAL,
        SHADER,
        SCRIPT,
        ANIMATION,
        FONT
    }
    
    /**
     * ViewMode enum
     */
    public enum ViewMode {
        LIST,
        GRID
    }
}

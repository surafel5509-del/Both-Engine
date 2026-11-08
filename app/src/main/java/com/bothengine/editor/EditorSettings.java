package com.bothengine.editor;

import com.bothengine.rendering.Color;

/**
 * EditorSettings
 * Settings for the editor
 * Similar to Unity's EditorPrefs
 */
public class EditorSettings {
    // General settings
    private boolean autoSave;
    private int autoSaveInterval;
    private String defaultScene;
    private String assetPath;
    
    // Theme settings
    private EditorTheme theme;
    private boolean useDarkTheme;
    
    // Camera settings
    private float defaultCameraDistance;
    private float cameraMoveSpeed;
    private float cameraRotationSpeed;
    private float cameraZoomSpeed;
    
    // Grid settings
    private boolean showGrid;
    private float gridSize;
    private int gridSubdivisions;
    private Color gridColor;
    
    // Gizmo settings
    private boolean showGizmos;
    private boolean showIcons;
    private float gizmoSize;
    private Color gizmoColor;
    private Color selectedGizmoColor;
    
    // Snap settings
    private boolean snapToGrid;
    private float snapSize;
    private float rotationSnap;
    private float scaleSnap;
    
    // UI settings
    private float uiScale;
    private boolean showTooltips;
    private boolean showConsole;
    private boolean showStats;
    
    public EditorSettings() {
        // General settings
        this.autoSave = true;
        this.autoSaveInterval = 300; // 5 minutes
        this.defaultScene = "MainScene";
        this.assetPath = "assets";
        
        // Theme settings
        this.theme = new EditorTheme();
        this.useDarkTheme = true;
        
        // Camera settings
        this.defaultCameraDistance = 10f;
        this.cameraMoveSpeed = 5f;
        this.cameraRotationSpeed = 100f;
        this.cameraZoomSpeed = 1f;
        
        // Grid settings
        this.showGrid = true;
        this.gridSize = 1f;
        this.gridSubdivisions = 10;
        this.gridColor = new Color(0.5f, 0.5f, 0.5f, 0.5f);
        
        // Gizmo settings
        this.showGizmos = true;
        this.showIcons = true;
        this.gizmoSize = 1f;
        this.gizmoColor = new Color(1f, 1f, 1f, 0.7f);
        this.selectedGizmoColor = new Color(1f, 1f, 0f, 1f);
        
        // Snap settings
        this.snapToGrid = false;
        this.snapSize = 1f;
        this.rotationSnap = 15f;
        this.scaleSnap = 0.1f;
        
        // UI settings
        this.uiScale = 1f;
        this.showTooltips = true;
        this.showConsole = true;
        this.showStats = true;
    }
    
    // Getters and setters
    
    public boolean isAutoSave() {
        return autoSave;
    }
    
    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }
    
    public int getAutoSaveInterval() {
        return autoSaveInterval;
    }
    
    public void setAutoSaveInterval(int autoSaveInterval) {
        this.autoSaveInterval = autoSaveInterval;
    }
    
    public String getDefaultScene() {
        return defaultScene;
    }
    
    public void setDefaultScene(String defaultScene) {
        this.defaultScene = defaultScene;
    }
    
    public String getAssetPath() {
        return assetPath;
    }
    
    public void setAssetPath(String assetPath) {
        this.assetPath = assetPath;
    }
    
    public EditorTheme getTheme() {
        return theme;
    }
    
    public void setTheme(EditorTheme theme) {
        this.theme = theme;
    }
    
    public boolean isUseDarkTheme() {
        return useDarkTheme;
    }
    
    public void setUseDarkTheme(boolean useDarkTheme) {
        this.useDarkTheme = useDarkTheme;
    }
    
    public float getDefaultCameraDistance() {
        return defaultCameraDistance;
    }
    
    public void setDefaultCameraDistance(float defaultCameraDistance) {
        this.defaultCameraDistance = defaultCameraDistance;
    }
    
    public float getCameraMoveSpeed() {
        return cameraMoveSpeed;
    }
    
    public void setCameraMoveSpeed(float cameraMoveSpeed) {
        this.cameraMoveSpeed = cameraMoveSpeed;
    }
    
    public float getCameraRotationSpeed() {
        return cameraRotationSpeed;
    }
    
    public void setCameraRotationSpeed(float cameraRotationSpeed) {
        this.cameraRotationSpeed = cameraRotationSpeed;
    }
    
    public float getCameraZoomSpeed() {
        return cameraZoomSpeed;
    }
    
    public void setCameraZoomSpeed(float cameraZoomSpeed) {
        this.cameraZoomSpeed = cameraZoomSpeed;
    }
    
    public boolean isShowGrid() {
        return showGrid;
    }
    
    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }
    
    public float getGridSize() {
        return gridSize;
    }
    
    public void setGridSize(float gridSize) {
        this.gridSize = gridSize;
    }
    
    public int getGridSubdivisions() {
        return gridSubdivisions;
    }
    
    public void setGridSubdivisions(int gridSubdivisions) {
        this.gridSubdivisions = gridSubdivisions;
    }
    
    public Color getGridColor() {
        return gridColor;
    }
    
    public void setGridColor(Color gridColor) {
        this.gridColor = gridColor;
    }
    
    public boolean isShowGizmos() {
        return showGizmos;
    }
    
    public void setShowGizmos(boolean showGizmos) {
        this.showGizmos = showGizmos;
    }
    
    public boolean isShowIcons() {
        return showIcons;
    }
    
    public void setShowIcons(boolean showIcons) {
        this.showIcons = showIcons;
    }
    
    public float getGizmoSize() {
        return gizmoSize;
    }
    
    public void setGizmoSize(float gizmoSize) {
        this.gizmoSize = gizmoSize;
    }
    
    public Color getGizmoColor() {
        return gizmoColor;
    }
    
    public void setGizmoColor(Color gizmoColor) {
        this.gizmoColor = gizmoColor;
    }
    
    public Color getSelectedGizmoColor() {
        return selectedGizmoColor;
    }
    
    public void setSelectedGizmoColor(Color selectedGizmoColor) {
        this.selectedGizmoColor = selectedGizmoColor;
    }
    
    public boolean isSnapToGrid() {
        return snapToGrid;
    }
    
    public void setSnapToGrid(boolean snapToGrid) {
        this.snapToGrid = snapToGrid;
    }
    
    public float getSnapSize() {
        return snapSize;
    }
    
    public void setSnapSize(float snapSize) {
        this.snapSize = snapSize;
    }
    
    public float getRotationSnap() {
        return rotationSnap;
    }
    
    public void setRotationSnap(float rotationSnap) {
        this.rotationSnap = rotationSnap;
    }
    
    public float getScaleSnap() {
        return scaleSnap;
    }
    
    public void setScaleSnap(float scaleSnap) {
        this.scaleSnap = scaleSnap;
    }
    
    public float getUiScale() {
        return uiScale;
    }
    
    public void setUiScale(float uiScale) {
        this.uiScale = uiScale;
    }
    
    public boolean isShowTooltips() {
        return showTooltips;
    }
    
    public void setShowTooltips(boolean showTooltips) {
        this.showTooltips = showTooltips;
    }
    
    public boolean isShowConsole() {
        return showConsole;
    }
    
    public void setShowConsole(boolean showConsole) {
        this.showConsole = showConsole;
    }
    
    public boolean isShowStats() {
        return showStats;
    }
    
    public void setShowStats(boolean showStats) {
        this.showStats = showStats;
    }
    
    @Override
    public String toString() {
        return "EditorSettings{" +
                "autoSave=" + autoSave +
                ", autoSaveInterval=" + autoSaveInterval +
                ", defaultScene='" + defaultScene + '\'' +
                ", useDarkTheme=" + useDarkTheme +
                ", showGrid=" + showGrid +
                ", showGizmos=" + showGizmos +
                ", snapToGrid=" + snapToGrid +
                '}';
    }
    
    /**
     * EditorTheme class for editor colors and styles
     */
    public static class EditorTheme {
        // Background colors
        private Color background;
        private Color backgroundDark;
        private Color backgroundLight;
        
        // Text colors
        private Color text;
        private Color textDisabled;
        private Color textHint;
        
        // UI colors
        private Color primary;
        private Color primaryDark;
        private Color primaryLight;
        private Color accent;
        private Color accentDark;
        private Color accentLight;
        
        // Window colors
        private Color windowBackground;
        private Color windowTitle;
        private Color windowBorder;
        
        // Button colors
        private Color button;
        private Color buttonHover;
        private Color buttonActive;
        private Color buttonText;
        
        // Field colors
        private Color fieldBackground;
        private Color fieldBorder;
        private Color fieldText;
        
        // Selection colors
        private Color selection;
        private Color selectionText;
        
        public EditorTheme() {
            // Dark theme defaults
            this.background = new Color(0.18f, 0.18f, 0.18f, 1f);
            this.backgroundDark = new Color(0.12f, 0.12f, 0.12f, 1f);
            this.backgroundLight = new Color(0.25f, 0.25f, 0.25f, 1f);
            
            this.text = new Color(0.9f, 0.9f, 0.9f, 1f);
            this.textDisabled = new Color(0.5f, 0.5f, 0.5f, 1f);
            this.textHint = new Color(0.7f, 0.7f, 0.7f, 1f);
            
            this.primary = new Color(0.2f, 0.4f, 0.8f, 1f);
            this.primaryDark = new Color(0.1f, 0.2f, 0.4f, 1f);
            this.primaryLight = new Color(0.4f, 0.6f, 1f, 1f);
            
            this.accent = new Color(0.2f, 0.8f, 0.4f, 1f);
            this.accentDark = new Color(0.1f, 0.4f, 0.2f, 1f);
            this.accentLight = new Color(0.4f, 1f, 0.6f, 1f);
            
            this.windowBackground = new Color(0.22f, 0.22f, 0.22f, 1f);
            this.windowTitle = new Color(0.3f, 0.3f, 0.3f, 1f);
            this.windowBorder = new Color(0.4f, 0.4f, 0.4f, 1f);
            
            this.button = new Color(0.3f, 0.3f, 0.3f, 1f);
            this.buttonHover = new Color(0.4f, 0.4f, 0.4f, 1f);
            this.buttonActive = new Color(0.2f, 0.2f, 0.2f, 1f);
            this.buttonText = new Color(0.9f, 0.9f, 0.9f, 1f);
            
            this.fieldBackground = new Color(0.25f, 0.25f, 0.25f, 1f);
            this.fieldBorder = new Color(0.4f, 0.4f, 0.4f, 1f);
            this.fieldText = new Color(0.9f, 0.9f, 0.9f, 1f);
            
            this.selection = new Color(0.2f, 0.4f, 0.8f, 0.3f);
            this.selectionText = new Color(1f, 1f, 1f, 1f);
        }
        
        // Getters and setters
        
        public Color getBackground() {
            return background;
        }
        
        public void setBackground(Color background) {
            this.background = background;
        }
        
        public Color getBackgroundDark() {
            return backgroundDark;
        }
        
        public void setBackgroundDark(Color backgroundDark) {
            this.backgroundDark = backgroundDark;
        }
        
        public Color getBackgroundLight() {
            return backgroundLight;
        }
        
        public void setBackgroundLight(Color backgroundLight) {
            this.backgroundLight = backgroundLight;
        }
        
        public Color getText() {
            return text;
        }
        
        public void setText(Color text) {
            this.text = text;
        }
        
        public Color getTextDisabled() {
            return textDisabled;
        }
        
        public void setTextDisabled(Color textDisabled) {
            this.textDisabled = textDisabled;
        }
        
        public Color getTextHint() {
            return textHint;
        }
        
        public void setTextHint(Color textHint) {
            this.textHint = textHint;
        }
        
        public Color getPrimary() {
            return primary;
        }
        
        public void setPrimary(Color primary) {
            this.primary = primary;
        }
        
        public Color getPrimaryDark() {
            return primaryDark;
        }
        
        public void setPrimaryDark(Color primaryDark) {
            this.primaryDark = primaryDark;
        }
        
        public Color getPrimaryLight() {
            return primaryLight;
        }
        
        public void setPrimaryLight(Color primaryLight) {
            this.primaryLight = primaryLight;
        }
        
        public Color getAccent() {
            return accent;
        }
        
        public void setAccent(Color accent) {
            this.accent = accent;
        }
        
        public Color getAccentDark() {
            return accentDark;
        }
        
        public void setAccentDark(Color accentDark) {
            this.accentDark = accentDark;
        }
        
        public Color getAccentLight() {
            return accentLight;
        }
        
        public void setAccentLight(Color accentLight) {
            this.accentLight = accentLight;
        }
        
        public Color getWindowBackground() {
            return windowBackground;
        }
        
        public void setWindowBackground(Color windowBackground) {
            this.windowBackground = windowBackground;
        }
        
        public Color getWindowTitle() {
            return windowTitle;
        }
        
        public void setWindowTitle(Color windowTitle) {
            this.windowTitle = windowTitle;
        }
        
        public Color getWindowBorder() {
            return windowBorder;
        }
        
        public void setWindowBorder(Color windowBorder) {
            this.windowBorder = windowBorder;
        }
        
        public Color getButton() {
            return button;
        }
        
        public void setButton(Color button) {
            this.button = button;
        }
        
        public Color getButtonHover() {
            return buttonHover;
        }
        
        public void setButtonHover(Color buttonHover) {
            this.buttonHover = buttonHover;
        }
        
        public Color getButtonActive() {
            return buttonActive;
        }
        
        public void setButtonActive(Color buttonActive) {
            this.buttonActive = buttonActive;
        }
        
        public Color getButtonText() {
            return buttonText;
        }
        
        public void setButtonText(Color buttonText) {
            this.buttonText = buttonText;
        }
        
        public Color getFieldBackground() {
            return fieldBackground;
        }
        
        public void setFieldBackground(Color fieldBackground) {
            this.fieldBackground = fieldBackground;
        }
        
        public Color getFieldBorder() {
            return fieldBorder;
        }
        
        public void setFieldBorder(Color fieldBorder) {
            this.fieldBorder = fieldBorder;
        }
        
        public Color getFieldText() {
            return fieldText;
        }
        
        public void setFieldText(Color fieldText) {
            this.fieldText = fieldText;
        }
        
        public Color getSelection() {
            return selection;
        }
        
        public void setSelection(Color selection) {
            this.selection = selection;
        }
        
        public Color getSelectionText() {
            return selectionText;
        }
        
        public void setSelectionText(Color selectionText) {
            this.selectionText = selectionText;
        }
    }
}

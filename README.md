# Both Engine

## The Next Big and Professional Android Game Engine

Both Engine is a powerful, modern, and professional 2D/3D game engine built specifically for Android mobile devices. It provides a complete framework for developing high-quality games with minimal effort.

## Features

### Core Architecture
- **Entity Component System (ECS)** - Flexible and performant entity management
- **Game Loop** - Built-in update and fixed update loops with delta time
- **Scene Management** - Easy scene transitions and management
- **Object Pooling** - Memory-efficient object reuse

### Rendering
- **OpenGL ES 2.0** - Full hardware-accelerated rendering
- **2D Rendering** - Sprites, UI elements, particle systems
- **3D Rendering** - Meshes, models, lighting, shadows
- **Shader System** - Customizable shaders for advanced effects
- **Texture Management** - Efficient texture loading and caching
- **Material System** - Flexible material properties

### Physics
- **Basic Physics** - Rigidbody, collision detection, raycasting
- **Collider Types** - Box, Sphere, Capsule, Mesh colliders
- **Physics Settings** - Gravity, friction, restitution, etc.

### Input
- **Touch Input** - Multi-touch support with gesture recognition
- **Keyboard Input** - Full keyboard support
- **Sensor Input** - Accelerometer, gyroscope support
- **Virtual Joysticks** - Built-in virtual controls

### Asset Management
- **Texture Loading** - PNG, JPG, and other formats
- **Mesh Loading** - OBJ file support
- **Sound Loading** - WAV, MP3, OGG support
- **Async Loading** - Non-blocking asset loading
- **Asset Caching** - Efficient memory management

### Audio
- **Sound Effects** - Short sound clips with SoundPool
- **Music** - Background music with MediaPlayer
- **3D Audio** - Positional audio support

### Scene System
- **Scene Graph** - Hierarchical scene organization
- **Scene Transitions** - Fade, instant, and custom transitions
- **Entity Management** - Easy entity creation and destruction

### Components
- **TransformComponent** - Position, rotation, scale
- **MeshRendererComponent** - 3D mesh rendering
- **SpriteRendererComponent** - 2D sprite rendering
- **RigidbodyComponent** - Physics properties
- **ColliderComponent** - Collision detection
- **CameraComponent** - Camera properties
- **UpdateComponent** - Custom update logic

## Project Structure

```
both-engine/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/bothengine/
│   │       │   ├── core/              # Core engine classes
│   │       │   │   ├── Engine.java
│   │       │   │   ├── EngineConfig.java
│   │       │   │   ├── EngineApplication.java
│   │       │   │   └── ecs/
│   │       │   │       ├── Entity.java
│   │       │   │       ├── Component.java
│   │       │   │       └── EntityManager.java
│   │       │   ├── rendering/          # Rendering system
│   │       │   │   ├── RenderManager.java
│   │       │   │   ├── GLES20Helper.java
│   │       │   │   ├── Camera.java
│   │       │   │   ├── Matrix4.java
│   │       │   │   ├── Vector2.java
│   │       │   │   ├── Vector3.java
│   │       │   │   ├── Vector4.java
│   │       │   │   ├── Color.java
│   │       │   │   ├── Bounds.java
│   │       │   │   ├── Frustum.java
│   │       │   │   ├── Mesh.java
│   │       │   │   ├── Material.java
│   │       │   │   ├── Texture.java
│   │       │   │   ├── Sprite.java
│   │       │   │   ├── ParticleSystem.java
│   │       │   │   ├── Renderer2D.java
│   │       │   │   ├── Renderer3D.java
│   │       │   │   ├── MeshRenderer.java
│   │       │   │   ├── Model.java
│   │       │   │   ├── Light.java
│   │       │   │   ├── PrimitiveMesh.java
│   │       │   │   └── shaders/
│   │       │   │       ├── Shader.java
│   │       │   │       ├── ShaderManager.java
│   │       │   │       ├── Basic2DShader.java
│   │       │   │       └── Basic3DShader.java
│   │       │   ├── input/              # Input system
│   │       │   │   └── InputManager.java
│   │       │   ├── assets/             # Asset management
│   │       │   │   ├── AssetManager.java
│   │       │   │   ├── Sound.java
│   │       │   │   ├── MeshLoader.java
│   │       │   │   └── ModelLoader.java
│   │       │   ├── scene/              # Scene management
│   │       │   │   ├── SceneManager.java
│   │       │   │   ├── Scene.java
│   │       │   │   ├── SceneTransition.java
│   │       │   │   ├── FadeTransition.java
│   │       │   │   └── InstantTransition.java
│   │       │   ├── systems/            # Engine systems
│   │       │   │   ├── UpdateSystem.java
│   │       │   │   ├── RenderSystem.java
│   │       │   │   └── PhysicsSystem.java
│   │       │   ├── components/         # ECS Components
│   │       │   │   ├── TransformComponent.java
│   │       │   │   ├── Quaternion.java
│   │       │   │   ├── MeshRendererComponent.java
│   │       │   │   ├── SpriteRendererComponent.java
│   │       │   │   ├── RigidbodyComponent.java
│   │       │   │   ├── ColliderComponent.java
│   │       │   │   ├── UpdateComponent.java
│   │       │   │   └── CameraComponent.java
│   │       │   └── GameActivity.java
│   │       │   └── EditorActivity.java
│   │       └── res/                  # Resources
│   │           ├── values/
│   │           │   ├── strings.xml
│   │           │   ├── colors.xml
│   │           │   ├── styles.xml
│   │           │   └── dimens.xml
│   │           └── xml/
│   │               ├── backup_rules.xml
│   │               └── data_extraction_rules.xml
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── settings.gradle
└── README.md
```

## Getting Started

### Prerequisites
- Android Studio 4.0 or later
- Android SDK (API 24 or higher)
- Java 8 or later

### Installation
1. Clone the repository:
```bash
git clone https://github.com/surafel5509-del/Both-Engine.git
```

2. Open the project in Android Studio

3. Build and run on your Android device

### Creating a Simple Game

```java
// Get engine from application
Engine engine = ((EngineApplication) getApplication()).getEngine();

// Create a new scene
Scene scene = new Scene("MyGame");
engine.getSceneManager().addScene(scene);

// Create an entity
Entity player = scene.addEntity("Player");

// Add components
TransformComponent transform = new TransformComponent();
transform.setPosition(new Vector3(0, 0, 0));
player.addComponent(transform);

MeshRendererComponent renderer = new MeshRendererComponent();
renderer.setMesh(PrimitiveMesh.createCube());
renderer.setMaterial(Material.createWithColor("PlayerMaterial", Color.RED));
player.addComponent(renderer);

RigidbodyComponent rigidbody = new RigidbodyComponent();
rigidbody.setMass(1);
rigidbody.setUseGravity(true);
player.addComponent(rigidbody);

// Set the scene as current
engine.getSceneManager().setCurrentScene("MyGame");
```

## API Documentation

### Engine

The main engine class that manages all subsystems.

```java
Engine engine = new Engine(config);
engine.start();
engine.update();
engine.stop();
```

### Entity Component System

```java
// Create entity
Entity entity = new Entity("MyEntity");

// Add components
entity.addComponent(new TransformComponent());
entity.addComponent(new MeshRendererComponent());

// Get component
TransformComponent transform = entity.getComponent(TransformComponent.class);

// Remove component
entity.removeComponent(TransformComponent.class);

// Destroy entity
entity.destroy();
```

### 2D Rendering

```java
// Create sprite
Sprite sprite = new Sprite("Player");
sprite.setTexture(texture);
sprite.setPosition(100, 100);
sprite.setSize(100, 100);

// Add to renderer
renderer2D.addSprite(sprite);
```

### 3D Rendering

```java
// Create mesh
Mesh mesh = PrimitiveMesh.createCube();

// Create material
Material material = Material.createWithColor("Red", Color.RED);

// Create mesh renderer
MeshRenderer renderer = new MeshRenderer();
renderer.setMesh(mesh);
renderer.setMaterial(material);

// Set position
renderer.setPosition(new Vector3(0, 0, 0));
```

### Input

```java
// Check key
if (inputManager.isKeyDown(KeyEvent.KEYCODE_SPACE)) {
    // Jump
}

// Check touch
if (inputManager.isTouchDown(0)) {
    Vector2 position = inputManager.getTouchPoint(0).getPosition();
    // Handle touch
}
```

### Asset Loading

```java
// Load texture
Texture texture = assetManager.loadTexture("textures/player.png");

// Load mesh
Mesh mesh = assetManager.loadMesh("models/player.obj");

// Load sound
Sound sound = assetManager.loadSound("sounds/jump.wav");
sound.play();
```

## Building

### Gradle Commands

```bash
# Build the project
./gradlew build

# Run on connected device
./gradlew installDebug

# Clean build
./gradlew clean
```

## License

MIT License

## Support

- **Issues**: [GitHub Issues](https://github.com/surafel5509-del/Both-Engine/issues)
- **Discussions**: [GitHub Discussions](https://github.com/surafel5509-del/Both-Engine/discussions)

---

**Both Engine - Powering the next generation of mobile games**

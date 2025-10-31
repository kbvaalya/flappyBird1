# flappyBird1
Flappy Bird — JavaFX Edition

Project Overview

This project is a Flappy Bird clone built entirely with JavaFX.
The goal was to recreate the core gameplay — a bird flying through randomly generated pipes — using only fundamental object-oriented programming concepts and GUI components from the JavaFX framework.

The game includes:

Real-time animation loop using AnimationTimer

Physics simulation with gravity and jump mechanics

Collision detection with pipes and screen boundaries

Dynamic score tracking and restart functionality


This project was created as part of a learning exercise in game development and GUI programming in Java.

Design Choices

JavaFX over Swing: JavaFX offers smoother rendering and native support for scenes, nodes, and animations.

Image-based bird: Instead of using geometric shapes, the bird uses an image (bird.png) with transparent background to give a more realistic appearance.

Simple rectangles for pipes: Using Rectangle nodes keeps the logic lightweight and efficient.

Layer management: Text elements (scoreText, gameOverText) are always kept in the front layer using toFront() to ensure visibility.

Procedural level generation: Each new pipe pair’s gap position is randomized for variety.

Challenges Faced

1. Physics tuning: Finding a realistic balance between gravity and jump strength took multiple trials (GRAVITY = 0.4, JUMP = -8).
2. Collision detection: Initial collision boxes were too sensitive; fine-tuned by adjusting pipe spacing and bird size.
3. Game restart logic: Avoiding leftover pipes and ensuring a clean reset each time required careful list and node management.
4. Transparency issues: Ensuring the bird image rendered without a solid background required using a proper .png with alpha channel.
5. Keeping UI elements visible: Pipes occasionally covered score text — fixed using toFront() calls after spawning each pipe.

Algorithms and Data Structures

Data Structure:

ArrayList<PipePair> stores all active pipe pairs currently visible on the screen.

Each PipePair contains two Rectangle objects (top and bottom pipes) and a boolean scored flag.


Algorithmic Logic:

Game Loop: Implemented using AnimationTimer — executes ~60 times per second, calling update().

Physics: Bird velocity updated each frame → birdVy += GRAVITY, then applied via bird.setY(bird.getY() + birdVy).

Collision: Checked via intersects() between bird’s bounds and each pipe’s bounds.

Pipe Generation: Random gap height using Random.nextDouble(), ensuring gaps remain within safe limits.

Score Tracking: Incremented when the bird passes a pipe (if (!scored && pipePassed)).

Improvements & File Usage

Future Improvements

Add background scrolling and moving clouds for depth.

Replace pipe rectangles with image textures.

Introduce sound effects on jump, score, and collision.

Add difficulty scaling: reduce gap size or increase speed as score increases.

Display best score saved locally between sessions.

Add a pause and main menu screen.

Input / Output File Usage

Currently, this version does not read or write any external files.
However, future versions can:

Save the best score to a .txt file.

Load configuration (gravity, jump power, colors) from a .json file for easy tuning.

Additional Explanations

Game restart: Press SPACE or ENTER after Game Over to instantly restart.

Input: Press SPACE (or click mouse) to jump during gameplay.

Output: The window displays score and "Game Over" message dynamically.

Transparency: Bird image must be a .png with alpha channel to appear clean on screen.

Author

Developed by: Kabylbaeva Adelia SCA-24A
Technologies: JavaFX
IDE: IntelliJ IDEA
Purpose: Educational — learning game physics, GUI handling, and animation loops in JavaFX.

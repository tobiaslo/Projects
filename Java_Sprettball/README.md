# Simulating balls
Runs a simulation with bouncy balls. A project to learn more about JavaFX. The simulation was never the focus, but testing out different GUI functions.

### Requirements
- Java 8 (with JavaFX)

I have not tested it out, but it should run with newer versions of Java. In addition you need to install JavaFX yourself. see https://openjfx.io for more information

### Functionality
- You can choose how many bouncyballs you want on the screen
- You can set the gravitation and the bouncyness of the balls
- The size and color of the balls can change
- Click on a ball to change the settings of the ball
- Clock on the ball to show a graph of the speed of the ball

### Limitations
- Sometimes the ball will continue to bounce indefinitly
- If the user throws the ball to hard, it will never come down
- If the user changes the settings, the simulation will start again

### Usage
compile with
```Bash
javac *.java
```

run with
```Bash
java SprettballV3.java
```

Anfter running the program the user can set the setting and run the simulation. Drag a bouncy ball and realese the ball to throw it.
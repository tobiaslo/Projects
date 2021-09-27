# Snake with AI

### Summary
A version of the classic game snake with a neural network controling the snake. This is a project i worked on the first semester on university. Its not great and has a lot of problems, but still is better than controlling the snake randomly.
I wanted to write the neural network with as little help as possible. Therfore the implementation uses only standard python libary, and is very slow. A consequense is also that the neural network is badly optimized and in gerelan pretty bad.
Still a very fun project and in the end the neural netowrok worked a little.

### Requirements
- Python 3
- pygame
	Go to https://www.pygame.org/wiki/GettingStarted to download.

### Rules
- The snake gets 100 points and get one square larger for each red square it hits
- The snake gets -1 point for each time it moves, this is to stop the snake for going in circles
- If the snake has not hit any red squares in 500 moves, the game is over

### Functionality
- Implements a small neural netowork with no hidden layers
- Has a GUI, using pygame, to show the game
- Has a simple and probably bad crossover algorythm for the training
- The weights and biases are stored in SnakeV2_Bias1.txt, SnakeV2_Bias2.txt, SnakeV2_Bias3.txt
- Has a own training program to train the neural network without the GUI

### Limitations
- Not quickest program, runs pretty slow
- Not a good implementation and the algorythm is not the best

### Usage
Run the prgram with

```Python
python3 spill.py

```

This wil run the GUI of the game. It will run the game 3 times. The initial conditions of the game has all the weights and bias of the neural network set to 0. To train the neural network run

```Python

python3 train.py

```

This program will not show GUI (because of the speed consequnses of the GUI).
To see the result of the training run the first program again.

```Python
python3 spill.py

```




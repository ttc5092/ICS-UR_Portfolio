
# TETRIS V1.0.0

# Introduction

This is a clone of the popular puzzle game TETRIS available on the NES and Gameboy consoles. Since this is based on an earlier version of TETRIS, there is no "hold" feature. Your goal is to arrange the falling pieces such that they fill up lines, causing them to clear. If pieces stack up to the top, you lose. You gain points for clearing lines, and every 5 cleared lines increases the level by 1, which makes the game slightly harder (pieces fall faster). The game takes a username input at the start and uses it to save high scores to a file.

Fit pieces together to clear lines. This will increase your score.
Every five lines you clear, your level increases by 1, which makes pieces move slightly faster.
If the screen fllls up, you will lose. When the game is over, the score will save automatically.
When starting a new game, your username can be used to access the previous high score.
Can you beat your last high score? Good luck!

# What I changed from the AI code:
 - Fixed the ScoreManager (now it actually saves the scores and loads them properly)
 - Added game music (does not work on CodeHS version though)
 - Added BasicEnt abstract class to meet requirements
 - Commented the code to amke it clear what each method does
 - Added a controls popup at start of the game
 - Modified colours to make the game grid visible and make shapes look cleaner
 - Flashes the screen when a line is cleared

# How to run the game

Download the files from the "Tetris" folder (keep them together) and run Tetris.java as the main file. When the game starts, a Java Swing window will prompt you to enter a username. This will be used to save your score. Once you enter your username, the controls will be displayed as a pop-up. Closing this pop-up starts the game. The controls are listed below:

# Controls

*<- [LEFT/RIGHT arrows] ->* Move piece left/right

*[UP/DOWN arrows]* Rotate piece 

*[spacebar]* Drop piece down

*D* Drop piece by one line

*P* Pause game

*S* Save game (mid-level, saves automatically at game over) 


The purpose of this project was to create an interactive game using Java Swing for the ISC4UR computer science class. The original code was generated using Gemini AI and modified in order to make the game functional.
In order to meet requirements, I added a BasicEnt abstract class to the progvram, which served as a template for the Shape class. I also modified the ScoreManager to actually save and load the high scores properly and also save the level at which you reached the high score.
Immediately after entering your username at the start, the game will attempt to search for it in the save file to retrieve a high score. If it succeeds, the high score will be displayed, otherwise, it will be set to 0 since you are a new player. When you press "S" or lose the game, the score will be saved.
Although only one shape falls down at a time, the collision detection ensures that it does not exceed the bounds of the game window and does not phase through already fallen shapes. When the shape falls onto another, it becomes immobile. If it fills a gap to complete a row, the row will be cleared. 
Clearing a row produces a visual effect (as required) : the screen flashes when the row clears and the score increases.
Due to the nature of TETRIS and its controls, having two shapes falling at a time would be impractical. The game's interactive events are the buttons on the GUI popups and key press events for the game controls. 
The game contains an abstract class (BasicEnt) used as a template for the Shape object. The ScoreManager allows users to input a name to save scores to a file, however it does not require a password to function. I modified the original ScoreManager clas to make it functional.
With all of these added features, the game is fully playable and meets the requirements of the GUI project. 





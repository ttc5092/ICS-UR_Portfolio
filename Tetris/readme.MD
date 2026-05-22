
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

# Controls

*<- [LEFT/RIGHT arrows] ->* Move piece left/right

*[UP/DOWN arrows]* Rotate piece 

*[spacebar]* Drop piece down

*D* Drop piece by one line

*P* Pause game

*S* Save game (mid-level, saves automatically at game over) 





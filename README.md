# Minesweeper
An object created from Minefield.java is stored as an attribute in GUI.java.
This object is used for both displaying and editing data throughout the game.

Note: All media used is either royalty free or created by me. 



Displaying the data:
Everytime a tile is stepped on (using left click), a call is done to revealTiles() in GUI.java.
Within this method each tile is iterated through checked to whether its image needs to be set to revealed.
It does this by calling getMineTile(row,column) from Minefiled.java.
The tile object can now be used to check whether its revealed and/or how many neighbours it has, using its getters in Minefield.java.
Different images are set to that specific tile, that correspond to what state the tile is in.

Once a mine is stepped on, all the mines are found using the same method as seen above and their images change to being exploded.

When a tile is marked (using right click), the method tileClick(e,x,y) in GUI.java is called.
It checks whether the tile is now revealed or not and sets its image either to a flag or a unmarked tile.
It does this by using getMineTile(row, column).isMarked() from Minefield.java



Editing the data:
The user alerts data using left and right click.
When a tile is stepped on (left click), the method step(row,column) from Minefield.java is called and its output is saved.
If the step(row,column) method from Minefield.java returns true then the game continues, if is false a mine was stepped on and the lose screen is shown.

When a new game is set up with custom settings or not, the minefield object is created using Minefield(rows,columns).
The rows and columns values are set either by default or using custom user determinded values.
The method populate(mines) is then called from Minefield.java to populate the grid with mines.
Again the mine number is set either by default or by the user.



Other features:
1.
Sound effects have been added throughout the game. This is done by using the method playSound(file_name).
The volume level of the sound effects can be set using the slider shown in the main game window.
Place where sound effects used:
-marking/unmarking tiles
-winning
-losing
-error in game setup

2.
Scoring system has been added. Every click of the mouse counts as a point.
The lower the score the better.
The users score is showed and updated in real time as they play.
If they restart that round their score will reset.

3.
Instructions dialog box which gives instructions of how to play the game.
This is accessed using the "Help" button shown during the main game.

4.
Main menu added with options to play the default game, set up a custom game or to quit.




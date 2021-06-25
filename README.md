# Mancala

This repository contains the files for three modules:
- Object orientated programming: make your own implementation of the mancala game.
- Model view controller: build a website for your own mancala game (or use the sloppy default implementation).
- CI/CD: run your tests automatically when pushing code to Gitlab.

## Object oriented programming and TDD

See the `./domain/src/test/` directory and check out the `bowl.java` test file to understand the code!

The tests are organised first arbitrarily by instantiation method, with or without a predefined mancala state. Most setup mancala in a game-scenario and thus are inside the tests using a predefined state.

Then the latter tests are sub divided to tests two behaviours:

1. play behaviour
2. end of game behaviour

In the play tests we checked if the play behaviour is like that defined in the rules. The end of game behaviour is similar, but here we include the domain model for the player to tell them who won when the game is ended according to the rules.

## Model view controller

In the model view controller we had to write a front-end to our domain model, and work with a pre-existing api codebase. We focussed on the following features

### Design should be mimic the mancala game and use flex based document styling flow

To keep things nicely positioned and adjustable we used a styling flow using relative positions to the absolute root html element. Using flex display styles allowed the content to be horizontally and vertically centered.

The board itself is displayed using a 800px by 400px div, inline-flex blocks to display the pits in the board.

### Turn and player names should be indicated

### End of game should prompt users to play again

### Game should persist on page refresh





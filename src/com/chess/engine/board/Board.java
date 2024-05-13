package com.chess.engine.board;

import com.chess.engine.Alliance;
import com.chess.engine.piece.*;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.*;

public class Board {
    // immutable list of tiles representing the game board
    private final List<Tile> gameBoard;
    // 2 collections to keep track of the white and black pieces on the board
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;
    // 3 variables to keep track of the black, white and current player on the board
    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;

    private final Pawn enPassantPawn;

    // private constructor calling the createGameBoard function to create a game board and store it in gameBoard
    private Board(final Builder builder) {
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePieces(this.gameBoard, Alliance.WHITE);
        this.blackPieces = calculateActivePieces(this.gameBoard, Alliance.BLACK);
        this.enPassantPawn = builder.enPassantPawn;
        final Collection<Move> whiteStandardLegalMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackStandardLegalMoves = calculateLegalMoves(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.blackPlayer = new BlackPlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);
    }

    // overriding the toString method to print out the board in an ASCII text
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            final String tileText = this.gameBoard.get(i).toString();
            builder.append(String.format("%3s", tileText));
            if ((i + 1) % BoardUtils.NUM_TILES_PER_ROW == 0) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    // 2 methods to return white or black player
    // used for the BlackPlayer.getOpponent() and WhitePlayer.getOpponent() methods
    public Player whitePlayer(){
        return this.whitePlayer;
    }
    public Player blackPlayer(){
        return this.blackPlayer;
    }
    public Player currentPlayer(){
        return this.currentPlayer;
    }

    // getter method for black pieces
    public Collection<Piece> getBlackPieces(){
        return this.blackPieces;
    }
    // getter method for white pieces
    public Collection<Piece> getWhitePieces(){
        return this.whitePieces;
    }

    // a method to calculate the legal moves for a collection of pieces
    private Collection<Move> calculateLegalMoves(final Collection<Piece> pieces) {
        final List<Move> legalMoves = new ArrayList<>();
        // for loop to loop through all of the pieces in the collection
        for (final Piece piece : pieces) {
            legalMoves.addAll(piece.calculateLegalMoves(this));
        }
        return ImmutableList.copyOf(legalMoves);
    }


    // a method to track the active pieces on the board
    private static Collection<Piece> calculateActivePieces(final List<Tile> gameBoard, final Alliance alliance) {
        final List<Piece> activePieces = new ArrayList<>();
        // a for loop to go through all of the tiles on the board
        for (final Tile tile : gameBoard) {
            // condition to ask if the tile is occupied by a piece
            if (tile.isTileOccupied()) {
                // getting the piece on this tile
                final Piece piece = tile.getPiece();
                // condition to check on the alliance of the piece on this tile
                // if its equal to the alliance we passed to the method
                // we add to the List of Alliance pieces a new active piece
                if (piece.getPieceAlliance() == alliance) {
                    activePieces.add(piece);
                }
            }
        }
        // returning a copy of the list of active pieces
        return ImmutableList.copyOf(activePieces);
    }

    public Tile getTile(final int tileCoordinate) {
        return gameBoard.get(tileCoordinate);

    }

    // this is the method responsible for creating the game board
    private static List<Tile> createGameBoard(final Builder builder) {
        //creating an array of 64 tiles
        final Tile[] tiles = new Tile[BoardUtils.NUM_TILES];
        // a loop to create the 64 tiles and store them in the array of tiles
        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            // createTile(i, builder.boardConfig.get(i)) creates a tile numbered i and sending to it the piece at the boardConfig of i
            // for example associating the tile id number 4 with a new King object to put a King on the tile number 4
            // we get the piece that's associated with tile id number 4 and create a tile from it
            // if there is no piece on it, it's gonna come back with null
            // and the createTile method is gonna automatically build an empty tile
            tiles[i] = Tile.createTile(i, builder.boardConfig.get(i));
        }
        // returning an immutable copy of the array tiles
        return ImmutableList.copyOf(tiles);
    }

    // a method to initialize the Standard Board at the start of the game using the Builder class below
    public static Board createStandardBoard() {
        final Builder builder = new Builder();

        // Black layout

        builder.setPiece(new Rook(Alliance.BLACK, 0));
        builder.setPiece(new Knight(Alliance.BLACK, 1));
        builder.setPiece(new Bishop(Alliance.BLACK, 2));
        builder.setPiece(new Queen(Alliance.BLACK, 3));
        builder.setPiece(new King(Alliance.BLACK, 4, true, true));
        builder.setPiece(new Bishop(Alliance.BLACK, 5));
        builder.setPiece(new Knight(Alliance.BLACK, 6));
        builder.setPiece(new Rook(Alliance.BLACK, 7));
        builder.setPiece(new Pawn(Alliance.BLACK, 8));
        builder.setPiece(new Pawn(Alliance.BLACK, 9));
        builder.setPiece(new Pawn(Alliance.BLACK, 10));
        builder.setPiece(new Pawn(Alliance.BLACK, 11));
        builder.setPiece(new Pawn(Alliance.BLACK, 12));
        builder.setPiece(new Pawn(Alliance.BLACK, 13));
        builder.setPiece(new Pawn(Alliance.BLACK, 14));
        builder.setPiece(new Pawn(Alliance.BLACK, 15));

        // White Layout

        builder.setPiece(new Pawn(Alliance.WHITE, 48));
        builder.setPiece(new Pawn(Alliance.WHITE, 49));
        builder.setPiece(new Pawn(Alliance.WHITE, 50));
        builder.setPiece(new Pawn(Alliance.WHITE, 51));
        builder.setPiece(new Pawn(Alliance.WHITE, 52));
        builder.setPiece(new Pawn(Alliance.WHITE, 53));
        builder.setPiece(new Pawn(Alliance.WHITE, 54));
        builder.setPiece(new Pawn(Alliance.WHITE, 55));
        builder.setPiece(new Rook(Alliance.WHITE, 56));
        builder.setPiece(new Knight(Alliance.WHITE, 57));
        builder.setPiece(new Bishop(Alliance.WHITE, 58));
        builder.setPiece(new Queen(Alliance.WHITE, 59));
        builder.setPiece(new King(Alliance.WHITE, 60, true, true));
        builder.setPiece(new Bishop(Alliance.WHITE, 61));
        builder.setPiece(new Knight(Alliance.WHITE, 62));
        builder.setPiece(new Rook(Alliance.WHITE, 63));
        // Set White to move
        builder.setMoveMaker(Alliance.WHITE);
        return builder.build();

    }

    // returns a concat of the white player legal moves and the black player legal moves together using Guava's Iterables
    public Iterable<Move> getAllLegalMoves() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.whitePlayer.getLegalMoves(), this.blackPlayer.getLegalMoves()));
    }

    public Pawn getEnPassantPawn() {
        return this.enPassantPawn;
    }

    // inner class to help us build an instance of the board totally immutable
    public static class Builder {

        // a variable to map the tile id of a chess board to a given piece on that tile id
        Map<Integer, Piece> boardConfig;
        Alliance nextMoveMaker;
        Pawn enPassantPawn;

        public Builder() {
            this.boardConfig = new HashMap<>();
        }

        // setting a specific piece in its position
        public Builder setPiece(final Piece piece) {
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        // deciding which alliance is the next move maker
        public Builder setMoveMaker(final Alliance nextMoveMaker) {
            this.nextMoveMaker = nextMoveMaker;
            return this;
        }

        public Board build() {
            return new Board(this);
        }

        public void setEnPassantPawn(Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
        }
    }
}

package com.chess.engine.board;

import com.chess.engine.piece.Piece;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;
// inspired by Joshua Bloch's Effective Java (2nd Edition)
// making everything as immutable as possible
//

public abstract class Tile {
    protected final int tileCoordinate;

    // a Map variable to contain all the possible empty tile variances
    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPossibleEmptyTiles();

    // the method used to create all the possible Empty tiles and save them in the cache
    private static Map<Integer, EmptyTile> createAllPossibleEmptyTiles() {

        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();
        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            emptyTileMap.put(i, new EmptyTile(i));
        }

        return ImmutableMap.copyOf(emptyTileMap);
    }

    // method to return empty/occupied tiles
    // constructor replacement making our code more "immutable"
    public static Tile createTile(final int tileCoordinate, final Piece piece) {
        return piece != null ? new OccupiedTile(tileCoordinate, piece) : EMPTY_TILES_CACHE.get(tileCoordinate);
    }

    //making the constructor private to achieve mutability
    private Tile(final int tileCoordinate) {
        this.tileCoordinate = tileCoordinate;
    }

    // an abstract method to return true if the tile is occupied by a piece and false if it is not
    public abstract boolean isTileOccupied();

    // an abstract method to return the piece on a specific tile
    public abstract Piece getPiece();

    public int getTileCoordinate(){
        return this.tileCoordinate;
    }

    // subclass for the Empty tiles extending the super class Tile
    public static final class EmptyTile extends Tile {
        private EmptyTile(final int coordinate) {
            super(coordinate);
        }

        // a method to print out a "-" on the empty tiles
        @Override
        public String toString() {
            return "-";
        }

        @Override
        public boolean isTileOccupied() {
            return false;
        } //returning false cuz tile is empty

        @Override
        public Piece getPiece() {
            return null;
        } // returning null cuz there is no piece on an empty tile

    }

    //subclass for the Occupied tiles extending the super class Tile
    public static final class OccupiedTile extends Tile {

        private final Piece pieceOnTile; // a variable to store the Piece on the specified tile

        private OccupiedTile(int tileCoordinate, final Piece pieceOnTile) {
            super(tileCoordinate);
            this.pieceOnTile = pieceOnTile;
        }

        // a method to print out each piece on a specific tile
        // Black pieces as Lower Case
        // White pieces as Upper Case
        @Override
        public String toString() {
            // if the piece is black print as lower case else print default "upper case"
            return getPiece().getPieceAlliance().isBlack() ? getPiece().toString().toLowerCase() :
                    getPiece().toString();

        }

        @Override
        public boolean isTileOccupied() {
            return true;
        } // returning true cuz tile is occupied by a specific piece

        @Override
        public Piece getPiece() {
            return this.pieceOnTile;
        } // returning the piece on the tile
    }

}

/*      tile numbering reference
        0   1   2   3   4   5   6   7
        8   9   10  11  12  13  14  15
        16  17  18  19  20  21  22  23
        24  25  26  27  28  29  30  31
        32  33  34  35  36  37  38  39
        40  41  42  43  44  45  46  47
        48  49  50  51  52  53  54  55
        56  57  58  59  60  61  62  63
 */

package com.chess.engine.piece;


import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.Collection;

public abstract class Piece {

    protected final PieceType pieceType; // a variable to keep track of the piece type i.e. King, Knight, Queen... etc.
    protected final int piecePosition; // a variable to store the piece's Position "place on the Tile"
    protected final Alliance pieceAlliance; // a variable to store the piece's Alliance "Black or White"
    protected final boolean isFirstMove; // a boolean telling us whether is it a piece's first move or not
    private final int cachedHashCode;

    Piece(final PieceType pieceType,
          final int piecePosition,
          final Alliance pieceAlliance,
          final boolean isFirstMove) {
        this.pieceType = pieceType;
        this.pieceAlliance = pieceAlliance;
        this.piecePosition = piecePosition;
        this.isFirstMove = isFirstMove;
        this.cachedHashCode = computeHashCode();
    }

    private int computeHashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + pieceAlliance.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);
        return result;
    }

    // equals method to find if 2 pieces are the same or not
    // b1.equals(b1);
    @Override
    public boolean equals(final Object other) {
        // if their reference is the same then they are the same object
        if (this == other) {
            return true;
        }
        // if the other Object is not an instance of the class Piece then they cannot be the same
        if (!(other instanceof Piece)) {
            return false;
        }
        // now that we know that the other Object is an instance of Piece we can cast it into Piece
        final Piece otherPiece = (Piece) other;
        // and return true "that they are the same piece" if and only if all the following conditions are true
        return piecePosition == otherPiece.getPiecePosition() && pieceType == otherPiece.getPieceType() &&
                pieceAlliance == otherPiece.getPieceAlliance() && isFirstMove == otherPiece.isFirstMove;
    }

    @Override
    public int hashCode() {
        return this.cachedHashCode;
    }

    // method to return the current piece position
    public int getPiecePosition() {
        return this.piecePosition;
    }

    // method to return the Alliance of the current piece
    public Alliance getPieceAlliance() { // getter for the piece alliance
        return this.pieceAlliance;
    }

    // method to return the boolean value of isFirstMove
    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    // getter for the piece type
    public PieceType getPieceType() {
        return this.pieceType;
    }

    public int getPieceValue(){
        return this.pieceType.getPieceValue();
    }

    // an abstract method carrying a collection of all the legal moves for every piece i.e. Knight, Queen ...etc
    // a collection is a general term that can refer to any data structure or group of data structures used to store and manipulate a set of items
    // while a list is a specific type of data structure that stores an ordered sequence of items
    public abstract Collection<Move> calculateLegalMoves(final Board board);

    // abstract method that takes in a move and apply it to the current piece we are on
    // and return a new piece with an updated piece position
    public abstract Piece movePiece(Move move);

    // an inner enum to set an Upper Case letter for each piece Type on the board
    // containing the boolean method to return true only and only if the piece type is King
    // and a toString method to return the piece name
    public enum PieceType {
        PAWN("P", 100) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KNIGHT("N", 300) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        BISHOP("B", 300) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        ROOK("R", 500) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }
        },
        QUEEN("Q", 900) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KING("K", 10000) {
            @Override
            public boolean isKing() {
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        };

        private String pieceName;
        private int pieceValue;

        PieceType(final String pieceName, final int pieceValue) {
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }

        // setting a specific value for each piece "weight"
        public int getPieceValue(){
            return this.pieceValue;
        }
        
        public abstract boolean isKing();

        public abstract boolean isRook();
    }

}

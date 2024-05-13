package com.chess.engine.piece;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class Rook extends Piece {

    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {-1, -8, 8, 1};

    public Rook(final Alliance pieceAlliance, final int piecePosition) {
        super(PieceType.ROOK, piecePosition, pieceAlliance, true);
    }

    public Rook(final Alliance pieceAlliance,
                final int piecePosition,
                final boolean isFirstMove){
        super(PieceType.ROOK, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {

        final List<Move> legalMoves = new ArrayList<>(); // an Array list to store the legal moves

        // looping through the candidate vector coordinates
        for (final int candidateCoordinateOffset : CANDIDATE_MOVE_VECTOR_COORDINATES) {
            int candidateDestinationCoordinate = this.piecePosition;
            // going through the vector of each candidate until we get to the edge of the board
            while (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {

                //if any Exclusion is true then we break out of the loop
                if (isFirstColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset) ||
                        isEighthColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset)) {
                    break;
                }

                // if the current position is valid we add the candidate offset to the current destination
                candidateDestinationCoordinate += candidateCoordinateOffset;
                // if the destination is valid we go through the rest of the steps just like the Knight and Bishop
                if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                    final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                    if (!candidateDestinationTile.isTileOccupied()) { // if the destination is not Occupied
                        legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate)); // we add a non-attacking legal move
                    } else { // if the destination is occupied
                        final Piece pieceAtDestination = candidateDestinationTile.getPiece(); // storing the piece on the candidate destination in a new piece variable
                        final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance(); // variable to store the destination piece's alliance
                        if (this.pieceAlliance != pieceAlliance) { // if our piece alliance is not equal to the destination piece alliance then it's an enemy piece
                            legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination)); // we add an attacking legal move
                        }
                        break; // once we find an occupied tile we need to break out of the loop ending the vector "the chain effect"
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);  // returning the immutable list of the legal moves
    }

    // Exclusions
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        // if the Rook is on the FIRST column then the following candidate offsets will be excluded (will be invalid)
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -1);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        // if the Rook is on the EIGHTH column then the following candidate offsets will be excluded (will be invalid)
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == 1);
    }

    // a method to create a new Piece just like the current piece but with a new position "moved piece"
    @Override
    public Rook movePiece(final Move move) {
        return new Rook(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
    }

    // toString to print out the letter corresponding to this specific piece on the chess board
    @Override
    public String toString() {
        return PieceType.ROOK.toString();
    }
}

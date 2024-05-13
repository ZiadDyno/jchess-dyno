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

// defining a Knight class extending the Piece class
public class Knight extends Piece {

    // an array storing all the candidate moves from a "current" location on the board for any knight
    // some of them may or may not be available for specific reasons
    private final static int[] CANDIDATE_MOVE_COORDINATES = {-17, -15, -10, -6, 6, 10, 15, 17};

    public Knight(final Alliance pieceAlliance, final int piecePosition) {
        super(PieceType.KNIGHT, piecePosition, pieceAlliance, true);
    }

    public Knight(final Alliance pieceAlliance,
                final int piecePosition,
                final boolean isFirstMove){
        super(PieceType.KNIGHT, piecePosition, pieceAlliance, isFirstMove);
    }

    // overriding the abstract Piece method to calculate the legal moves for the knight
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {

        final List<Move> legalMoves = new ArrayList<>(); // an Array list to store the legal moves

        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) { // for loop to go through all the candidate moves

            // calculating the destination coordinate for the current candidate move by adding the offset to the current location
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;

            if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) { // checking if the destination coordinate is valid "not out of the bounds "0-63"

                // if any Exclusion is true then we break the current iteration of the loop "invalid candidate offset" and continue with the next iteration
                if (isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                        isSecondColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                        isSeventhColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                        isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)) {
                    continue;
                }

                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                if (!candidateDestinationTile.isTileOccupied()) { // if the destination is not Occupied
                    legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate)); // we add a non-attacking legal move
                } else { // if the destination is occupied
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece(); // storing the piece on the candidate destination in a new piece variable
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance(); // variable to store the destination piece's alliance
                    if (this.pieceAlliance != pieceAlliance) { // if our piece alliance is not equal to the destination piece alliance then it's an enemy piece
                        legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination)); // we add an attacking legal move
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves); // returning the immutable list of the legal moves
    }

    // a method to create a new Piece just like the current piece but with a new position "moved piece"
    @Override
    public Knight movePiece(final Move move) {
        return new Knight(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
    }

    // Exclusions
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        // if the knight is on the FIRST column then the following candidate offsets will be excluded (will be invalid)
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -17 || candidateOffset == -10 || candidateOffset == 6 || candidateOffset == 15);
    }

    private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset) {
        // if the knight is on the SECOND column then the following candidate offsets will be excluded (will be invalid)
        return BoardUtils.SECOND_COLUMN[currentPosition] && (candidateOffset == -10 || candidateOffset == 6);
    }

    private static boolean isSeventhColumnExclusion(final int currentPosition, final int candidateOffset) {
        // if the knight is on the SEVENTH column then the following candidate offsets will be excluded (will be invalid)
        return BoardUtils.SEVENTH_COLUMN[currentPosition] && (candidateOffset == -6 || candidateOffset == 10);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        // if the knight is on the EIGHTH column then the following candidate offsets will be excluded (will be invalid)
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -15 || candidateOffset == -6 || candidateOffset == 10 || candidateOffset == 17);
    }

    // toString to print out the letter corresponding to this specific piece on the chess board
    @Override
    public String toString() {
        return PieceType.KNIGHT.toString();
    }
}

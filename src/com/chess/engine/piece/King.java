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

public class King extends Piece {

    private final static int[] CANDIDATE_MOVE_COORDINATE = {-9, -8, -7, -1, 1, 7, 8, 9};
    private final boolean isCastled;
    private final boolean kingSideCastleCapable;
    private final boolean queenSideCastleCapable;

    public King(final Alliance pieceAlliance,
                final int piecePosition,
                final boolean kingSideCastleCapable,
                final boolean queenSideCastleCapable) {
        super(PieceType.KING, piecePosition, pieceAlliance, true);
        this.isCastled = false;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }

    public King(final Alliance pieceAlliance,
                final int piecePosition,
                final boolean isFirstMove,
                final boolean isCastled,
                final boolean kingSideCastleCapable,
                final boolean queenSideCastleCapable) {
        super(PieceType.KING, piecePosition, pieceAlliance, isFirstMove);
        this.isCastled = isCastled;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }

    public boolean isCastled() {
        return this.isCastled;
    }

    public boolean isKingSideCastleCapable() {
        return this.kingSideCastleCapable;
    }

    public boolean isQueenSideCastleCapable() {
        return this.queenSideCastleCapable;
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {

        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE) {// for loop to go through all the candidate moves

            // calculating the destination coordinate for the current candidate move by adding the offset to the current location
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;
            // if any Exclusion is true then we break the current iteration of the loop "invalid candidate offset" and continue with the next iteration
            if (isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                    isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)) {
                continue;
            }
            // if the destination is a valid tile
            if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                // bring the tile and store it in Destination Tile variable
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


        return ImmutableList.copyOf(legalMoves);
    }


    // a method to create a new Piece just like the current piece but with a new position "moved piece"
    @Override
    public King movePiece(final Move move) {
        return new King(move.getMovedPiece().getPieceAlliance(),
                move.getDestinationCoordinate(),
                false,
                move.isCastlingMove(),
                false,
                false);
    }

    // Exclusions
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        // if the knight is on the FIRST column then the following candidate offsets will be excluded (will be invalid)
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -9 || candidateOffset == -1 || candidateOffset == 7);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        // if the King is on the EIGHTH column then the following candidate offsets will be excluded (will be invalid)
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == 9 || candidateOffset == 1 || candidateOffset == -7);
    }

    // toString to print out the letter corresponding to this specific piece on the chess board
    @Override
    public String toString() {
        return PieceType.KING.toString();
    }
}

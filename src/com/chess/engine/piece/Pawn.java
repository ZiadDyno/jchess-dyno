package com.chess.engine.piece;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class Pawn extends Piece {

    private final static int[] CANDIDATE_MOVE_COORDINATE = {7, 8, 9, 16};

    public Pawn(final Alliance pieceAlliance, final int piecePosition) {
        super(PieceType.PAWN, piecePosition, pieceAlliance, true);
    }

    public Pawn(final Alliance pieceAlliance,
                final int piecePosition,
                final boolean isFirstMove) {
        super(PieceType.PAWN, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {

        final List<Move> legalMoves = new ArrayList<>(); // an Array list to store the legal moves

        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE) {
            // applying the offset to the Pawn's current position (for White applying -8 and for Black applying +8)
            // this.getPieceAlliance().getDirection() is an Alliance enum method used to determine the Pawn's direction of movement (-1 or 1)
            final int candidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * currentCandidateOffset);
            // if the candidate destination is invalid then we break the current iteration of the loop and continue through the next iteration
            if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                continue;
            }

            /* PAWN NON-ATTACKING MOVE */

            // if the current candidate offset is 8 and the tile is not occupied then we add a new non-attacking Pawn move
            if (currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                // normal pawn promotion
                if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                    legalMoves.add(new PawnPromotion(new PawnMove(board, this, candidateDestinationCoordinate)));
                } else {
                    legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
                }
            }

            /* PAWN JUMP */

            // if the candidate offset is 16 (a jump) AND this is the first move for this specific Pawn
            // AND (we are on the second row as Black OR in the seventh row as White) we can go for the next condition
            else if (currentCandidateOffset == 16 && this.isFirstMove() &&
                    ((BoardUtils.SEVENTH_RANK[this.piecePosition] && this.getPieceAlliance().isBlack()) ||
                            (BoardUtils.SECOND_RANK[this.piecePosition] && this.getPieceAlliance().isWhite()))) {
                final int behindCandidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                // if the tile in front of the pawn and the tile in front of it both are not occupied then we can do a Pawn jump as a first move
                if (!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied() &&
                        !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    // adding the Pawn jump move to the list of legal moves
                    legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
                }
            }

            /* PAWN ATTACKING MOVES */

            // if the offset is 7 then this is an attacking move for both the Black (attacking to the Right) and White (attacking to the Right)
            // Excluding the case of being White and on the Eighth column you cannot attack to the Right side
            // and if you are Black and on the First column you cannot attack to the Right side
            else if (currentCandidateOffset == 7 &&
                    !((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()) ||
                            (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))) {
                // if the destination tile is occupied
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    // get the tile's info
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    // if the Alliance of the piece on the destination tile is not equal to the Alliance of our piece
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                        // right side attack pawn promotion
                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate)));
                        } else {
                            // add new pawn attacking to the Right side move
                            legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }

                /* PAWN EN PASSANT ATTACKING MOVE "Right Side Attack" */
                // if we have an En Passant Pawn that is an enemy pawn
                // and that pawn is next to our pawn then we can make an En Passant Attack Move
                else if (board.getEnPassantPawn() != null) {
                    if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + (this.pieceAlliance.getOppositeDirection()))) {
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                            legalMoves.add(new PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }
            }

            // if the offset is 9 then this is an attacking move for both the Black (attacking to the Left) and White (attacking to the Left)
            // Excluding the case of being White and on the First column you cannot attack to the Left side
            // and if you are Black and on the Eighth column you cannot attack to the Left side
            else if (currentCandidateOffset == 9 &&
                    !((BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()) ||
                            (BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))) {
                // if the destination tile is occupied
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    // get the tile's info
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    // if the Alliance of the piece on the destination tile is not equal to the Alliance of our piece
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                        // right side attack pawn promotion
                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate)));
                        } else {
                            // add new pawn attacking to the Left side move
                            legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }

                /* PAWN EN PASSANT ATTACKING MOVE "Left Side Attack" */
                // if we have an En Passant Pawn that is an enemy pawn
                // and that pawn is next to our pawn then we can make an En Passant Attack Move
                else if (board.getEnPassantPawn() != null) {
                    if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition - (this.pieceAlliance.getOppositeDirection()))) {
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                            legalMoves.add(new PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    // a method to create a new Piece just like the current piece but with a new position "moved piece"
    @Override
    public Pawn movePiece(final Move move) {
        return new Pawn(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
    }

    // toString to print out the letter corresponding to this specific piece on the chess board
    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }

    // for the sake of simplicity and that its 98% of the time the pawn promotion is directly to a Queen
    // we will set the pawn promotion to a Queen by default
    public Piece getPromotionPiece(){
        return new Queen(this.pieceAlliance, this.piecePosition, false);
    }
}

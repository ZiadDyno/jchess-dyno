package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.piece.King;
import com.chess.engine.piece.Piece;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Player {

    protected final Board board;
    protected final King playerKing; // to keep track of the player king
    protected final Collection<Move> legalMoves; // to keep track of the player's legal moves
    private final boolean isInCheck; // to keep track of the king's check status

    Player(final Board board,
           final Collection<Move> legalMoves,
           final Collection<Move> opponentMoves) {

        this.board = board;
        this.playerKing = establishKing();
        // saving the legal moves and the castle moves to the legal moves using Guava's concat method
        this.legalMoves = ImmutableList.copyOf(Iterables.concat(legalMoves, calculateKingCastles(legalMoves, opponentMoves)));
        // if that list of attacks is not empty this means that the current player is in check
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();
    }

    public King getPlayerKing(){
        return this.playerKing;
    }

    public Collection<Move> getLegalMoves(){
        return this.legalMoves;
    }

    // method that returns the collection of moves that attacks the king's current position
    protected static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> moves) {
        final List<Move> attackMoves = new ArrayList<>();
        // for loop to find if the destination coordinate of the enemy moves overlaps with the king's current position
        // if it does then it's attacking the king
        for (final Move move : moves) {
            if (piecePosition == move.getDestinationCoordinate()){
                attackMoves.add(move);
            }
        }
        // then we return the list of moves that attacks that king's position
        return ImmutableList.copyOf(attackMoves);
    }

    // this method is to make sure that there is a king for the player on the board
    // otherwise we won't be in a valid game
    private King establishKing() {
        for (final Piece piece : getActivePieces()) {
            if (piece.getPieceType().isKing()) {
                return (King) piece;
            }
        }
        throw new RuntimeException("Should not reach here! Not a valid board.");
    }

    // to test if the move that's been passed is contained in this player's legal move collection
    public boolean isMoveLegal(final Move move) {
        return this.legalMoves.contains(move);
    }
    // returns true if the king is currently in check
    public boolean isInCheck(){
        return this.isInCheck;
    }
    // returns true if the king is currently in check and the king has no way to escape check
    public boolean isInCheckMate(){
        return this.isInCheck && !hasEscapeMoves();
    }
    // returns true if the king is not in check but still has no escape moves
    // which means that any move you make will leave you in check position
    public boolean isInStaleMate(){
        return !this.isInCheck && !hasEscapeMoves();
    }

    public boolean isKingSideCastleCapable(){
        return this.playerKing.isKingSideCastleCapable();
    }

    public boolean isQueenSideCastleCapable(){
        return this.playerKing.isQueenSideCastleCapable();
    }

    // checks if the king has any escape moves available
    // in order to calculate whether the king can escape
    // we are going to go through each of the player's legal moves
    // and we are going to make those moves on an imaginary board
    // after we make that move and it is Done we return true
    // if we can't make the move then it is going to return false
    protected boolean hasEscapeMoves(){
        for (final Move move : this.legalMoves){
            final MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone()){
                return true;
            }
        }
        return false;
    }
    // TODO more work to do here!!!
    public boolean isCastled(){
        return false;
    }

    // returns the move transition that happens when we make a move
    // if we were able to make the move or the move status is "done"
    // then we will make a new board to show the execution of that move
    public MoveTransition makeMove(final Move move){
        // if the move is illegal then the move transition returns the same board we are currently on
        if (!isMoveLegal(move)){
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }
        // if the move is not illegal we execute the move and return a new transition board
        final Board transitionBoard = move.execute();
        // then we check all the possible attacks on the current player's king
        final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                transitionBoard.currentPlayer().getLegalMoves());
        // if there are any attacks on the current player's king we shouldn't be able to actually make the move
        // " you cannot make a move that exposes your king to check "
        // then we return the same board we are currently on
        if (!kingAttacks.isEmpty()){
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }
        // otherwise its safe to make the move and it returns the new board after the transition is done
        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }


    // abstract method to return the collection of active pieces for the black player and the white player
    // will be implemented later in the BlackPlayer and WhitePlayer Classes!!!
    public abstract Collection<Piece> getActivePieces();

    // returns the alliance of the player
    public abstract Alliance getAlliance();

    // returns the opponent of the player
    public abstract Player getOpponent();
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentLegals);


}

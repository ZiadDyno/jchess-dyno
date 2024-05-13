package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.piece.Piece;
import com.chess.engine.piece.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class BlackPlayer extends Player {

    public BlackPlayer(final Board board,
                       final Collection<Move> whiteStandardLegalMoves,
                       final Collection<Move> blackStandardLegalMoves) {
        super(board, blackStandardLegalMoves, whiteStandardLegalMoves);
    }

    // returns collection of active black pieces
    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.whitePlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
                                                    final Collection<Move> opponentLegals) {

        final List<Move> kingCastles = new ArrayList<>();


        // if the king is on its first move and not currently in check
        if (this.playerKing.isFirstMove() && !this.isInCheck()) {

            /* BLACK'S KING SIDE CASTLE */

            // if the tile number 5 and 6 are not occupied "the tiles between the white king and the right side rook"
            if (!this.board.getTile(5).isTileOccupied() && !this.board.getTile(6).isTileOccupied()) {
                final Tile rookTile = this.board.getTile(7);
                // if the rook is on Tile number 7 and is on its first move
                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    // if there are no active opponent attacks on tiles number 5 and 6 "between the rook and the king"
                    // and the piece on rookTile is actually a rook
                    if (Player.calculateAttacksOnTile(5, opponentLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(6, opponentLegals).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook()) {
                        // then we can add a new king side castle move "right side castling"
                        kingCastles.add(new KingSideCastleMove(this.board,
                                this.playerKing,
                                6,
                                (Rook) rookTile.getPiece(),
                                rookTile.getTileCoordinate(),
                                5));
                    }
                }
            }

            /* BLACK'S QUEEN SIDE CASTLE */

            // if the tile number 1, 2, and 3 are not occupied "the tiles between the white king and the LEFT side rook"
            if (!this.board.getTile(1).isTileOccupied() &&
                    !this.board.getTile(2).isTileOccupied() &&
                    !this.board.getTile(3).isTileOccupied()) {
                final Tile rookTile = this.board.getTile(0);
                // if the rook is on tile number 0 and is on its first move
                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    // if there are no active opponent attacks on tiles number 1, 2, and 3 "between the rook and the king"
                    // and the piece on rookTile is actually a rook
                    if (Player.calculateAttacksOnTile(1, opponentLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(2, opponentLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(3, opponentLegals).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook()) {
                        // then we can add a new queen side castle move "left side castling"
                        kingCastles.add(new QueenSideCastleMove(this.board,
                                this.playerKing,
                                2,
                                (Rook) rookTile.getPiece(),
                                rookTile.getTileCoordinate(),
                                3));
                    }
                }
            }
        }
        return ImmutableList.copyOf(kingCastles);
    }
}

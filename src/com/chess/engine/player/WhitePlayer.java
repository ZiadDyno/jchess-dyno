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

public class WhitePlayer extends Player {

    public WhitePlayer(final Board board,
                       final Collection<Move> whiteStandardLegalMoves,
                       final Collection<Move> blackStandardLegalMoves) {
        super(board, whiteStandardLegalMoves, blackStandardLegalMoves);
    }

    // returns collection of active white pieces
    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
                                                    final Collection<Move> opponentLegals) {

        final List<Move> kingCastles = new ArrayList<>();


        // if the king is on its first move and not currently in check
        if (this.playerKing.isFirstMove() && !this.isInCheck()) {

            /* WHITE'S KING SIDE CASTLE */

            // if the tile number 61 and 62 are not occupied "the tiles between the white king and the right side rook"
            if (!this.board.getTile(61).isTileOccupied() && !this.board.getTile(62).isTileOccupied()) {
                final Tile rookTile = this.board.getTile(63);
                // if the rook is on Tile number 63 and is on its first move
                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    // if there are no active opponent attacks on tiles number 61 and 62 "between the rook and the king"
                    // and the piece on rookTile is actually a rook
                    if (Player.calculateAttacksOnTile(61, opponentLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(62, opponentLegals).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook()) {
                        // then we can add a new king side castle move "right side castling"
                        kingCastles.add(new KingSideCastleMove(this.board,
                                this.playerKing,
                                62,
                                (Rook) rookTile.getPiece(),
                                rookTile.getTileCoordinate(),
                                61));
                    }
                }
            }

            /* WHITE'S QUEEN SIDE CASTLE */

            // if the tile number 59, 58, AND 57 are not occupied "the tiles between the white king and the LEFT side rook"
            if (!this.board.getTile(59).isTileOccupied() &&
                    !this.board.getTile(58).isTileOccupied() &&
                    !this.board.getTile(57).isTileOccupied()) {
                final Tile rookTile = this.board.getTile(56);
                // if the rook is on tile number 56 and is on its first move
                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    // if there are no active opponent attacks on tiles number 59, 58, and 57 "between the rook and the king"
                    // and the piece on rookTile is actually a rook
                    if (Player.calculateAttacksOnTile(59, opponentLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(58, opponentLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(57, opponentLegals).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook()) {
                        // then we can add a new queen side castle move "left side castling"
                        kingCastles.add(new QueenSideCastleMove(this.board,
                                this.playerKing,
                                58,
                                (Rook) rookTile.getPiece(),
                                rookTile.getTileCoordinate(),
                                59));
                    }
                }
            }
        }

        return ImmutableList.copyOf(kingCastles);
    }
}

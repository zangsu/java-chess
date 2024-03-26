package chess.domain.chessGame;

import chess.domain.board.Board;
import chess.domain.location.Location;
import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.Score;
import java.util.Map;
import java.util.function.Supplier;

public class PlayingGame implements ChessGame {
    private final Board board;
    private final Color turnPlayer;

    protected PlayingGame() {
        this(new Board(), Color.WHITE);
    }

    protected PlayingGame(Board board, Color turnPlayer) {
        this.board = board;
        this.turnPlayer = turnPlayer;
    }

    @Override
    public boolean isNotEnd() {
        return true;
    }

    @Override
    public ChessGame startGame(Supplier<Boolean> checkRestart) {
        if (checkRestart.get()) {
            return new PlayingGame();
        }
        return this;
    }

    @Override
    public ChessGame endGame() {
        return new EndGame();
    }

    @Override
    public ChessGame move(Location source, Location target) {
        board.move(source, target, turnPlayer);
        if(board.isKingDead()){
            return new EndGame();
        }
        return new PlayingGame(board, turnPlayer.getOpponent());
    }

    @Override
    public Map<Location, Piece> getBoard() {
        return board.getBoard();
    }

    @Override
    public Score getScore(Color color) {
        return board.calculateScore(color);
    }

    protected Color getTurnPlayer() {
        return this.turnPlayer;
    }
}

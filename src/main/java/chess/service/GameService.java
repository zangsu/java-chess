package chess.service;

import chess.repository.GameDao;
import chess.repository.PieceDao;
import chess.repository.entity.Game;
import chess.service.domain.board.Board;
import chess.service.domain.chessGame.ChessGame;
import chess.service.domain.chessGame.InitialGame;
import chess.service.domain.chessGame.PlayingGame;
import chess.service.domain.location.Location;
import java.util.Optional;
import java.util.function.Supplier;

public class GameService {

    private final GameDao gameDao;
    private final PieceDao pieceDao;

    public GameService(GameDao gameDao, PieceDao pieceDao) {
        this.gameDao = gameDao;
        this.pieceDao = pieceDao;
    }

    public Optional<ChessGame> loadGame() {
        int lastGameId = gameDao.findLastGameId();
        Optional<Board> board = pieceDao.findAllPiecesById(lastGameId);
        if (board.isEmpty()) {
            return Optional.empty();
        }

        Optional<Game> gameEntity = gameDao.findGameById(lastGameId);
        if (gameEntity.isEmpty()) {
            throw new IllegalStateException("DB에 저장된 턴이 없습니다.");
        }
        Game game = gameEntity.get();
        return Optional.of(new PlayingGame(game.getGameId(), board.get(), game.getTurn()));
    }

    public ChessGame createNewGame() {
        int lastGameId = gameDao.findLastGameId();
        //TODO 방을 여러개 관리하게 되면 이전 데이터를 남겨두고 새로 만들어야 함
        pieceDao.deleteAllPiecesById(lastGameId);
        gameDao.deleteGameById(lastGameId);

        int newGameId = lastGameId;
        if (newGameId == 0) {
            newGameId = 1;
        }
        return new InitialGame(newGameId);
    }

    public ChessGame startGame(ChessGame chessGame, Supplier<Boolean> checkRestart) {
        ChessGame startedGame = chessGame.startGame(checkRestart);
        gameDao.saveGame(new Game(startedGame.getGameId(), startedGame.getTurn()));
        pieceDao.saveAllPieces(startedGame.getGameId(), startedGame.getBoard());
        return startedGame;
    }

    public ChessGame move(ChessGame chessGame, Location source, Location target) {
        chessGame = chessGame.move(source, target);
        pieceDao.updatePieceLocation(chessGame.getGameId(), source, target);
        gameDao.updateGame(new Game(chessGame.getGameId(), chessGame.getTurn()));
        if (chessGame.isEnd()) {
            pieceDao.deleteAllPiecesById(chessGame.getGameId());
            gameDao.deleteGameById(chessGame.getGameId());
        }
        return chessGame;
    }

    public void save(ChessGame chessGame) {
        Game game = new Game(chessGame.getGameId(), chessGame.getTurn());
        pieceDao.deleteAllPiecesById(game.getGameId());
        gameDao.deleteGameById(game.getGameId());
        gameDao.saveGame(game);
        pieceDao.saveAllPieces(game.getGameId(), chessGame.getBoard());
    }

    public ChessGame end(ChessGame chessGame) {
        return chessGame.endGame();
    }
}

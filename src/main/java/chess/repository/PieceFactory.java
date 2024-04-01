package chess.repository;

import chess.service.domain.piece.Color;
import chess.service.domain.piece.Piece;
import chess.service.domain.piece.PieceType;
import chess.service.domain.piece.implement.Bishop;
import chess.service.domain.piece.implement.King;
import chess.service.domain.piece.implement.Knight;
import chess.service.domain.piece.implement.Queen;
import chess.service.domain.piece.implement.Rook;
import chess.service.domain.piece.implement.pawn.InitialPawn;
import chess.service.domain.piece.implement.pawn.MovedPawn;
import java.util.Arrays;

public class PieceFactory {

    private PieceFactory() {
    }

    public static Piece create(String pieceTypeName, String colorName) {
        PieceType pieceType = convertToPieceType(pieceTypeName);
        Color color = convertToColor(colorName);
        return createOf(pieceType, color);
    }

    private static PieceType convertToPieceType(String pieceTypeName) {
        return Arrays.stream(PieceType.values())
                .filter(type -> type.name().equalsIgnoreCase(pieceTypeName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("DB에 잘못된 값이 저장되었습니다."));
    }

    private static Color convertToColor(String colorName) {
        return Arrays.stream(Color.values())
                .filter(color -> color.name().equalsIgnoreCase(colorName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("DB에 잘못된 값이 저장되었습니다."));
    }

    private static Piece createOf(PieceType pieceType, Color color) {
        return switch (pieceType) {
            case KING -> new King(color);
            case QUEEN -> new Queen(color);
            case ROOK -> new Rook(color);
            case KNIGHT -> new Knight(color);
            case BISHOP -> new Bishop(color);
            case INITIAL_PAWN -> new InitialPawn(color);
            case MOVED_PAWN -> new MovedPawn(color);
        };
    }
}
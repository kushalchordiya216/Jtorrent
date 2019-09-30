package jtorrent.Communication.Messages;

/**
 * @param content the byte[] array content of the piece
 * @param pieceId the Id of the piece according to relevant metadata
 */
public class Piece implements Message {

    private static final long serialVersionUID = 1L;
    private final String messageType = "PIECE";
    private byte[] content = null;
    private String pieceId = null;

    public Piece(String pieceId, byte[] content) {
        this.pieceId = pieceId;
        this.content = content;
    }

    @Override
    public String getMessageType() {
        return messageType;
    }

    public byte[] getContent() {
        return content;
    }

    public String getPieceId() {
        return pieceId;
    }

}
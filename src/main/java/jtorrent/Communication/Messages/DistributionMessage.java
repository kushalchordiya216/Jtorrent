package jtorrent.Communication.Messages;

/**
 * This message is sent by leecher to seeders to inform them what parts of the
 * file they're supposed to send
 * 
 * @param pieceIds Array of pieceIds that are assigned to the peer
 */
public class DistributionMessage implements Message {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final String messageType = "DISTRIBUTION";
    private String[] pieceIds = null;

    public DistributionMessage(String[] pieceIds) {
        this.pieceIds = pieceIds;
    }

    @Override
    public String getMessageType() {
        return messageType;
    }

    public String[] getPieceIds() {
        return this.pieceIds;
    }

}
package jtorrent.Communication.P2PMessages;

/**
 * This message is sent by leecher to seeders to inform them what parts of the
 * file they're supposed to send
 * 
 * @param pieceHashes Array of pieceIds that are assigned to the peer
 */
public class DistributionMessage implements Message {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final String messageType = "DISTRIBUTION";
    private String[] pieceHashes = null;

    public DistributionMessage(String[] pieceIds) {
        this.pieceHashes = pieceIds;
    }

    @Override
    public String getMessageType() {
        return messageType;
    }

    public String[] getPieceHashes() {
        return this.pieceHashes;
    }

}
package jtorrent.Requests;

import java.io.Serializable;

/**
 * {@summary} interface for sending request to peers for a particular file
 * 
 * @Implemented by LeechRequest, SeedRequest, ConnectRequest, AnnounceRequest,
 *              ExitRequest, UpdateRequest
 * 
 */
public interface Request extends Serializable {

    public String getRequestType();

    public String getHostName();

    public Integer getPort();
}
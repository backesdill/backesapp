package de.backesdill.helper;


public class BackesNetworkPacket {

    // command
    public static final byte VERSION_OFFSET = 0;
    public static final byte VERSION_V1     = 1;
    public static final byte VERSION_V2     = 2;

    // command
    public static final byte CMD_OFFSET     = 1;
    // BackesFest
    public static final byte CMD_REQUEST_BF_DB = 1;
    public static final byte CMD_PUBLISH_BF_DB = 2;
    public static final byte CMD_PUBLISH_TEMP  = 3;

    // size
    public static final byte SIZE_OFFSET = 2;

    // data
    public static final byte DATA_OFFSET = 3;

    // general defines
    public static final byte MAX_PACKET_SIZE = 100;
    public static final byte MAX_PAYLOAD_SIZE = MAX_PACKET_SIZE - DATA_OFFSET;
    public static final byte HEADER_SIZE = DATA_OFFSET;


    public byte   version;
    public byte   cmd;
    public byte   payloadSize;
    public byte[] payloadData;
}



/**
 * Copyright 2019 Hewlett Packard Enterprise Development LP
 */
/**
 * Copyright 2019 Hewlett Packard Enterprise Development LP
 */
package CustomObjects;

import java.io.File;
/**
 * @author rkoneru
 *
 */
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class ResponseMessage implements Serializable {

    private static final long serialVersionUID = -5399605122490343339L;

    private MessageStatus mStatus;
    private Integer size;
    private String fromAddress;
    private String toAddress;
    private String content;
    private HashSet<String> activePeers;
    private HashMap<Integer, RFCObject> rfcIndex;
    private File rfcDocument;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ResponseMessage [ \n mStatus = ");
        builder.append(mStatus);
        builder.append("\n");
        builder.append(", size = ");
        builder.append(size);
        builder.append("\n");
        builder.append(", fromAddress = ");
        builder.append(fromAddress);
        builder.append("\n");
        builder.append(", toAddress = ");
        builder.append(toAddress);
        builder.append("\n");
        builder.append(", content = ");
        builder.append(content);
        builder.append("\n");
        builder.append(", activePeers = ");
        builder.append(activePeers);
        builder.append("\n");
        builder.append(", rfcIndex = ");
        builder.append(rfcIndex);
        builder.append("\n");
        builder.append(", rfcDocument = ");
        builder.append(rfcDocument);
        builder.append("\n");
        builder.append(" ]");
        return builder.toString();
    }

    public MessageStatus getmStatus() {
        return mStatus;
    }

    public void setmStatus(MessageStatus mStatus) {
        this.mStatus = mStatus;
    }

    public ResponseMessage(MessageStatus mStatus, String fromAddress, String toAddress) {
        this.mStatus = mStatus;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public HashSet<String> getActivePeers() {
        return activePeers;
    }

    public void setActivePeers(HashSet<String> activePeers) {
        this.activePeers = activePeers;
    }

    public File getRfcDocument() {
        return rfcDocument;
    }

    public void setRfcDocument(File rfcDocument) {
        this.rfcDocument = rfcDocument;
    }

    public HashMap<Integer, RFCObject> getRfcIndex() {
        return rfcIndex;
    }

    public void setRfcIndex(HashMap<Integer, RFCObject> rfcIndex) {
        this.rfcIndex = rfcIndex;
    }

}

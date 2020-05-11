/**
 * Copyright 2019 Hewlett Packard Enterprise Development LP
 */
package CustomObjects;

/**
 * @author rkoneru
 *
 */
import java.io.Serializable;

public class RequestMessage implements Serializable {

    private static final long serialVersionUID = -5399605122490343339L;

    private Mtype mtype;
    private Integer size;
    private String RFCServerSocketHostname;
    private Integer RFCServerSocketPortNumber;
    private String content;
    private Integer fileNumber;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RequestMessage [ \n Messsage Type = ");
        builder.append(mtype);
        builder.append("\n");
        builder.append(", size = ");
        builder.append(size);
        builder.append("\n");
        builder.append(", RFCServerSocketHostname = ");
        builder.append(RFCServerSocketHostname);
        builder.append("\n");
        builder.append(", content = ");
        builder.append(content);
        builder.append("\n");
        builder.append(", Requested file Number = ");
        builder.append(fileNumber);
        builder.append("\n");
        builder.append(" ]");
        return builder.toString();
    }

    public RequestMessage(Mtype mtype, String RFCServerSocketHostname, Integer RFCServerSocketPortNumber) {
        this.mtype = mtype;
        this.RFCServerSocketHostname = RFCServerSocketHostname;
        this.RFCServerSocketPortNumber = RFCServerSocketPortNumber;
    }

    public Mtype getMtype() {
        return mtype;
    }

    public void setMtype(Mtype mtype) {
        this.mtype = mtype;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRFCServerSocketHostname() {
        return RFCServerSocketHostname;
    }

    public void setRFCServerSocketHostname(String rFCServerSocketHostname) {
        RFCServerSocketHostname = rFCServerSocketHostname;
    }

    public Integer getRFCServerSocketPortNumber() {
        return RFCServerSocketPortNumber;
    }

    public void setRFCServerSocketPortNumber(Integer rFCServerSocketPortNumber) {
        RFCServerSocketPortNumber = rFCServerSocketPortNumber;
    }

    public Integer getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(Integer fileNumber) {
        this.fileNumber = fileNumber;
    }

}

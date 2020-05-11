/**
 * Copyright 2019 Hewlett Packard Enterprise Development LP
 */
package CustomObjects;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;

public class Server {
    public static int cookieCount = 0;
    public static final int port = 4444;
    static ServerSocket ss = null;
    static HashMap<String, Peer> peerList = new HashMap<String, Peer>();
    static HashSet<String> activePeerList = new HashSet<String>();

    public Server() {
        try {
            ss = new ServerSocket(port);
            System.out.println("Server with hostname:" + ss.getInetAddress().getHostName() + " ready to accept connections...");
            while (true) {
                Socket socket = ss.accept();
                ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
                Thread t = new ClientHandler(socket, is, os);
                System.out.println("Connected to client:" + socket);
                t.start();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public HashSet<String> getActiveList() {

        return activePeerList;

    }

    public boolean alreadyregistered(String peerIdentifier) {
        //System.out.println(peerIdentifier);
        if (peerList.containsKey(peerIdentifier)) {
            return true;
        }

        return false;
    }

    public static void main(String[] args) {
        Server myServer = new Server();
    }

    // ClientHandler class
    class ClientHandler extends Thread {
        ObjectInputStream is;
        ObjectOutputStream os;
        Socket socket;
        String peerIdentifier;

        // Constructor
        public ClientHandler(Socket socket, ObjectInputStream is, ObjectOutputStream os) {
            this.socket = socket;
            this.is = is;
            this.os = os;
        }

        @Override
        public void run() {
            try {
                boolean closeSocket = false;
                while (!socket.isClosed()) {
                    RequestMessage m = (RequestMessage) this.is.readObject();
                    peerIdentifier = m.getRFCServerSocketHostname() + ":" + m.getRFCServerSocketPortNumber();
                    ResponseMessage rm = null;
                    Peer p1 = null;
                    switch (m.getMtype()) {
                    case Register:
                        if (alreadyregistered(peerIdentifier)) {
                            if (peerList.get(peerIdentifier).getCookie() == -1) {
                                rm = new ResponseMessage(MessageStatus.ERROR, this.socket.getLocalAddress().toString(), socket.getRemoteSocketAddress().toString());
                                rm.setContent("The peer has already been registered but there is no cookie value");
                            } else {
                                rm = new ResponseMessage(MessageStatus.OK, socket.getLocalAddress().toString(), socket.getRemoteSocketAddress().toString());
                                rm.setContent("The peer has already been registered with cookie value:" + peerList.get(peerIdentifier).getCookie());
                                p1 = peerList.get(peerIdentifier);
                                p1.setIsActive(true);
                                p1.setLastRegistrationDate(LocalDate.now());
                                p1.setActiveCount(p1.getActiveCount() + 1);
                                activePeerList.add(peerIdentifier);
                            }

                        }
                        //New Registration required
                        else {
                            rm = new ResponseMessage(MessageStatus.OK, socket.getLocalAddress().toString(), socket.getRemoteSocketAddress().toString());
                            rm.setContent("The peer is now registered with cookie value:" + ++cookieCount);
                            peerList.put(peerIdentifier, p1 = new Peer(socket.getRemoteSocketAddress().toString(), socket.getPort(), cookieCount));
                            activePeerList.add(peerIdentifier);
                        }
                        break;
                    case Leave:
                        if (alreadyregistered(peerIdentifier)) {
                            p1 = peerList.get(peerIdentifier);
                            if (p1.getIsActive()) {
                                rm = new ResponseMessage(MessageStatus.OK, socket.getLocalAddress().toString(), socket.getRemoteSocketAddress().toString());
                                rm.setContent("Peer with cookie value:" + peerList.get(peerIdentifier).getCookie() + " is now inactive");
                                p1.setIsActive(false);
                                activePeerList.remove(peerIdentifier);
                            } else {
                                rm = new ResponseMessage(MessageStatus.ERROR, socket.getLocalAddress().toString(), socket.getRemoteSocketAddress().toString());
                                rm.setContent("The peer has left already");
                            }

                        } else {
                            rm = new ResponseMessage(MessageStatus.ERROR, socket.getLocalAddress().toString(), socket.getRemoteSocketAddress().toString());
                            rm.setContent("Peer not registered, please register first");
                        }
                        break;
                    case PQuery:
                        if (alreadyregistered(peerIdentifier)) {
                            p1 = peerList.get(peerIdentifier);
                            rm = new ResponseMessage(MessageStatus.OK, socket.getLocalAddress().toString(), socket.getRemoteSocketAddress().toString());
                            rm.setContent("Peer cookie value:" + peerList.get(peerIdentifier).getCookie());
                            rm.setActivePeers(new HashSet<String>(activePeerList));
                        } else {
                            rm = new ResponseMessage(MessageStatus.ERROR, socket.getLocalAddress().toString(), socket.getRemoteSocketAddress().toString());
                            rm.setContent("Peer not registered, please register first");
                        }
                        break;
                    case KeepAlive:
                        if (alreadyregistered(peerIdentifier)) {
                            p1 = peerList.get(peerIdentifier);
                            rm = new ResponseMessage(MessageStatus.OK, socket.getLocalAddress().toString(), socket.getRemoteSocketAddress().toString());
                            p1.setTTL(new Integer(7200));

                        } else {
                            rm = new ResponseMessage(MessageStatus.ERROR, socket.getLocalAddress().toString(), socket.getRemoteSocketAddress().toString());
                            rm.setContent("Peer not registered, please register first");
                        }
                        break;
                    default:
                        rm = new ResponseMessage(MessageStatus.ERROR, socket.getLocalAddress().toString(), socket.getRemoteSocketAddress().toString());
                        rm.setContent("Illegal request type");
                        return;
                    }
                    os.writeObject(rm);
                }
                activePeerList.remove(peerIdentifier);
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}

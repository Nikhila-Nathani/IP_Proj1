/**
 * Copyright 2019 Hewlett Packard Enterprise Development LP
 */
package CustomObjects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author rkoneru
 *
 */
public class Client {

    final static String RSHostName = "localhost";
    final static Integer RSPortNumber = 4444;
    public static HashMap<Integer, RFCObject> rfcIndex = new HashMap<Integer, RFCObject>();
    private RFCClient rfcClient;
    private RFCServer rfcServer;
    public static int rfcServerport = 65400;
    public static ServerSocket rfcServerSocket;
    public static String rfcServerHostname;
    public static String clientName;
    public static List<File> requiredRFCFiles;
    public static HashSet<Integer> requiredRFC = new HashSet<Integer>();
    public static File downloadTimeCsv;

    public static void initRFCIndex() {
        File folder = new File("latest_rfc");
        File[] requiredRFCArray = folder.listFiles();
        requiredRFCFiles = Arrays.asList(requiredRFCArray);
        for (File i : requiredRFCFiles) {
            if (!i.getName().startsWith("r")) {
                continue;
            }
            requiredRFC.add(Integer.parseInt(i.getName().replace("rfc", "").replace(".txt", "")));
        }
        File clientFolder = new File(clientName);
        downloadTimeCsv = new File(clientName + "/" + clientName + "_rfc_dtime.csv");

        File[] rfcFileArray = clientFolder.listFiles();
        List<File> rfcFile = Arrays.asList(rfcFileArray);

        for (File i : rfcFile) {
            if (!i.getName().startsWith("r")) {
                continue;
            }
            Integer number = Integer.parseInt(i.getName().replace("rfc", "").replace(".txt", ""));
            requiredRFC.remove(number);
            String title = i.getName();
            rfcServerHostname = rfcServerSocket.getInetAddress().getHostName();
            rfcIndex.put(number, new RFCObject(number, title, rfcServerHostname, rfcServerport));

        }

    }

    public Client() {
        this.rfcServer = this.setupRfcServer();
        this.rfcClient = this.setupRfcClient();

    }

    public RFCServer setupRfcServer() {
        try {
            Thread rfcServer = this.new RFCServer();
            rfcServer.start();
            return (RFCServer) rfcServer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public RFCClient setupRfcClient() {
        try {
            Thread rfcClient = this.new RFCClient();
            rfcClient.start();
            return (RFCClient) rfcClient;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        rfcServerport = rfcServerport + Integer.parseInt(args[0]);
        clientName = "P" + args[0];
        System.out.println(clientName);
        rfcServerSocket = new ServerSocket(rfcServerport);
        System.out.println("RFCServer created:" + rfcServerSocket.getInetAddress().getHostName());
        initRFCIndex();
        Client client1 = new Client();

    }

    public class RFCClient extends Thread {

        Scanner scanner = new Scanner(System.in);

        private void copyFileUsingStream(File source, File dest) throws IOException {
            InputStream is = null;
            OutputStream os = null;
            try {
                is = new FileInputStream(source);
                os = new FileOutputStream(dest);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            } finally {
                is.close();
                os.close();
            }
        }

        public int rsMenu() {
            int action;
            System.out.println("1. Register");
            System.out.println("2. Leave");
            System.out.println("3. PQuery");
            System.out.println("4. KeepAlive");
            System.out.println("5. Not now, will select my choice after 10 seconds..");
            action = scanner.nextInt();
            return action;
        }

        public void registerClient(Socket socket, ObjectInputStream is, ObjectOutputStream os) {
            try {

                RequestMessage message = new RequestMessage(Mtype.Register, rfcServerHostname, rfcServerport);
                System.out.println(message);
                os.writeObject(message);

                ResponseMessage returnMessage = (ResponseMessage) is.readObject();
                System.out.println("return Message is=" + returnMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void startClient() {
            System.out.println("welcome client");

            try {
                Socket socket = new Socket(RSHostName, RSPortNumber);
                System.out.println("RFCClient:" + socket + "connected successfully");
                ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
                registerClient(socket, is, os);
                FileWriter filewriter = new FileWriter(downloadTimeCsv);
                Thread keepAliveThread = new KeepAlive(socket, is, os);
                //keepAliveThread.start();
                while (true) {
                    int action = rsMenu();
                    if (action >= 5) {
                        TimeUnit.SECONDS.sleep(10);
                        continue;
                    }
                    System.out.println("--------" + Mtype.values()[action - 1]);
                    RequestMessage message = new RequestMessage(Mtype.values()[action - 1], rfcServerHostname, rfcServerport);
                    os.writeObject(message);
                    ResponseMessage returnMessage = (ResponseMessage) is.readObject();
                    System.out.println("return Message is=" + returnMessage);
                    //PQuery
                    if (action - 1 == 2) {
                        for (String s : returnMessage.getActivePeers()) {
                            //No more files are required
                            if (requiredRFC.size() == 0)
                                break;

                            String[] x = s.split(":");
                            String remoteRFCServerHostName = x[0];
                            if (x[0].equals("null") || x[0].length() == 0) {
                                x[0] = "localhost";
                            }
                            Integer remoteRFCServerPortNumber = Integer.parseInt(x[1]);
                            if (remoteRFCServerPortNumber == rfcServerport)
                                continue;

                            Socket rsClientSocket = new Socket("localhost", remoteRFCServerPortNumber);
                            ObjectOutputStream rsOs = new ObjectOutputStream(rsClientSocket.getOutputStream());
                            ObjectInputStream rsIs = new ObjectInputStream(rsClientSocket.getInputStream());
                            RequestMessage reqMessage = new RequestMessage(Mtype.RFCQuery, rfcServerHostname, rfcServerport);
                            rsOs.writeObject(reqMessage);

                            ResponseMessage responseMessage = (ResponseMessage) rsIs.readObject();
                            HashMap<Integer, RFCObject> receivedRFCIndex = new HashMap<Integer, RFCObject>();
                            receivedRFCIndex.putAll(responseMessage.getRfcIndex());
                            for (Integer receivedFileNumber : receivedRFCIndex.keySet()) {
                                if (!rfcIndex.containsKey(receivedFileNumber)) {
                                    rfcIndex.put(receivedFileNumber, receivedRFCIndex.get(receivedFileNumber));
                                    long startTime = System.currentTimeMillis();
                                    RequestMessage downloadReqMessage = new RequestMessage(Mtype.GetRFC, rfcServerHostname, rfcServerport);
                                    downloadReqMessage.setFileNumber(receivedFileNumber);
                                    rsOs.writeObject(downloadReqMessage);
                                    System.out.println(downloadReqMessage);
                                    ResponseMessage downloadResponseMessage = (ResponseMessage) rsIs.readObject();
                                    System.out.println(downloadResponseMessage);
                                    File downloadedFile = downloadResponseMessage.getRfcDocument();
                                    File newFile = new File(clientName + "/" + downloadedFile.getName());
                                    //System.out.println(newFile.getAbsolutePath());
                                    newFile.createNewFile();
                                    //TODO: Actually Download
                                    copyFileUsingStream(downloadedFile, newFile);
                                    long stopTime = System.currentTimeMillis();
                                    long elapsedTime = stopTime - startTime;
                                    filewriter.append(newFile.getName() + "," + elapsedTime + "\n");
                                    requiredRFC.remove(receivedFileNumber);
                                }
                            }

                        }
                    }
                    filewriter.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            startClient();
        }

        class KeepAlive extends Thread {
            ObjectInputStream kais;
            ObjectOutputStream kaos;
            Socket kas;

            public KeepAlive(Socket kas, ObjectInputStream kais, ObjectOutputStream kaos) {
                this.kas = kas;
                this.kais = kais;
                this.kaos = kaos;
            }

            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(10);
                    RequestMessage message = new RequestMessage(Mtype.KeepAlive, rfcServerHostname, rfcServerport);
                    kaos.writeObject(message);
                    ResponseMessage returnMessage = (ResponseMessage) kais.readObject();
                } catch (Exception e) {

                }

            }

        }

    }

    public class RFCServer extends Thread {

        ServerSocket ss;
        Scanner rfcScanner = new Scanner(System.in);
        int rfcAction;

        public void startServer() {
            try {
                ss = rfcServerSocket;
                System.out.println("RFCServer is now lisitening on" + ss.getLocalPort());
                Socket s = null;
                while (true) {
                    s = ss.accept();
                    // obtaining input and out streams
                    System.out.println("RFCServer received a new client:" + s);
                    ObjectInputStream is = new ObjectInputStream(s.getInputStream());
                    ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
                    Thread t = new RFCClientHandler(s, is, os);
                    System.out.println("Connected to RFCClient:" + s);
                    t.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            startServer();
        }

        class RFCClientHandler extends Thread {
            ObjectInputStream is;
            ObjectOutputStream os;
            Socket socket;

            // Constructor
            public RFCClientHandler(Socket socket, ObjectInputStream is, ObjectOutputStream os) {
                this.socket = socket;
                this.is = is;
                this.os = os;
            }

            @Override
            public void run() {
                try {
                    String peerIdentifier = this.socket.getRemoteSocketAddress().toString();
                    boolean closeSocket = false;
                    while (!closeSocket) {
                        RequestMessage m = (RequestMessage) this.is.readObject();
                        ResponseMessage rm = null;
                        Peer p1 = null;
                        switch (m.getMtype()) {
                        case RFCQuery:
                            rm = new ResponseMessage(MessageStatus.OK, socket.getLocalAddress().toString(), socket.getRemoteSocketAddress().toString());
                            rm.setRfcIndex(new HashMap<Integer, RFCObject>(rfcIndex));
                            break;
                        case GetRFC:
                            String requestedFileName = "rfc" + m.getFileNumber() + ".txt";
                            rm = new ResponseMessage(MessageStatus.OK, socket.getLocalAddress().toString(), socket.getRemoteSocketAddress().toString());
                            String finalLocaltion = clientName + "/" + requestedFileName;
                            File rfcDocument = new File(finalLocaltion);
                            rm.setRfcDocument(rfcDocument);
                            break;
                        default:
                            break;
                        }
                        os.writeObject(rm);

                    }
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }

}

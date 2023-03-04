package com.Projects.ChatApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends JFrame{
    ServerSocket server;
    Socket socket;
    BufferedReader br;// br is used for reading
    PrintWriter out;// out is used for writing

    private JLabel heading = new JLabel("Server Area");

    private JTextArea messageArea = new JTextArea();

    private JTextField messageInput = new JTextField();

    private Font font = new Font("TimesNewRoman",Font.PLAIN, 25);

    //constructor
    public Server() {

        try {
            server = new ServerSocket(7777);
            System.out.println("Server is ready to accept connection");
            socket = server.accept();

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

              createGui();
              handleEvent();

            startReading();
//            startWriting();

        } catch (Exception e) {
            //todo: handle the exception
            e.printStackTrace();
        }
    }
    private void handleEvent() {
        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
//                System.out.println("Key released "+ e.getKeyCode());
                if(e.getKeyCode() == 10){
//                    System.out.println("you have pressed enter button!");
                    String contentToSend_S = messageInput.getText();
                    messageArea.append("Me: "+ contentToSend_S+"\n");
                    out.println(contentToSend_S);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }
        });
    }
    private void createGui() {
        //create
        this.setTitle("Server Message [End]");
        this.setLocation(500,200);
        this.setSize(500,600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //coding for component
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setIcon(new ImageIcon("src/com/Projects/ChatApplication/png.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        messageArea.setEnabled(false);
        //setting frame layout
        setLayout(new BorderLayout());
        //adding components to the frame
        this.add(heading, BorderLayout.NORTH);
        this.add(messageArea, BorderLayout.CENTER);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        jScrollPane.setAutoscrolls(true);
        this.add(jScrollPane, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);
        this.setVisible(true);
    }

    public void startReading() {
        //thread-read karke deta rehega
        Runnable r1 = () -> {
            System.out.println("reader started...");
            try {
                while (true) {
//                System.out.println("Now entered the while loop");
                    String msg = br.readLine();
                    if (msg.equals("quit")) {
//                        System.out.println("Client terminated the chat");
                        JOptionPane.showMessageDialog(this, "Client terminated the chat");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
//                    System.out.println("Client : " + msg);
                    messageArea.append("Client: "+ msg+ "\n");
                }
            }catch (Exception e) {
//                e.printStackTrace();
                System.out.println("connection is closed");
            }
        };
        new Thread(r1).start();//starting the thread r1
    }

    public void startWriting() {
        //thread - data user lega and then send karega client tak
        Runnable r2 = () -> {
            System.out.println("writer started...");
            try {
                //we have to write until the socket is not closed
                while (!socket.isClosed()) {
//                        System.out.println("In Server writing Thread's while loop ");
                    BufferedReader br1 = new BufferedReader(new
                            InputStreamReader(System.in));//InputStreamReader
                    String content = br1.readLine();

                    out.println(content);
                    out.flush();//for sometimes when data is not going.

                    if(content.equals("exit"))
                    {
                        System.out.println("Server writer has exited");
                        socket.close();
                        break;
                    }
                }
            } catch (Exception e) {
//                        e.printStackTrace();
                System.out.println("Connection closed");
            }
        };
        new Thread(r2).start();
    }
    public static void main(String[] args) {
        System.out.println("This is server...starting the server");
        //instantiation of constructor
        new Server();
    }
}










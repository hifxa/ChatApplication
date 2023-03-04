package com.Projects.ChatApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// make exit thread - by inter thread communication

//[Constructor] - here initialising Socket , BufferReader , PrintWriter
public class Client extends JFrame {
    Socket socket;
    BufferedReader br;// br is used for reading
    PrintWriter out;

    //FOR GUI - DECLARING COMPONENTS
    private JLabel heading = new JLabel("Client Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("new",Font.PLAIN,25);

    public Client(){
        try {
            System.out.println("Sending request to server");
            socket = new Socket("127.0.0.1", 7777);
            System.out.println("connection done.");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            ////calling method to create gui
            createGUI();
            handleEvents();

            startReading();
//            startWriting();this was only for the console display  part , no need to use it in the gui integration;

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleEvents() {
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
                if (e.getKeyCode()==10){
//                    System.out.println("You have pressed enter button");
                    String contentToSend = messageInput.getText();
                    messageArea.append("me: "+ contentToSend+ "\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();//returns the focus back to its place
                }
            }
        });
    }

    private void createGUI() {
         // gui code
        this.setTitle("Client messenger[END]");
        this.setSize(500,600);
        this.setLocation(500,200);// when giving it null then it is showing nullPointerException
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //coding for component
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setIcon(new ImageIcon("src/com/Projects/ChatApplication/png.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        messageArea.setEnabled(false);
        //setting frame layout
        setLayout(new BorderLayout());
        // adding components to the frame
        this.add(heading, BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        jScrollPane.setAutoscrolls(true);
        this.add(jScrollPane, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);
        this.setVisible(true);
    }

    //Start Reading [Method]
    public void startReading() {
        //thread-read karke deta rehega
        Runnable r1 = () -> {
            System.out.println("reader started...");
            try {
                while (true) {
//                System.out.println("Now entered the while loop");
                    String msg = br.readLine();
                    if (msg.equals("quit")) {
//                        System.out.println("Server terminated the chat");
                        JOptionPane.showMessageDialog(this,"Server terminated the chat");// this will show errors on the body if any exception occurs
                        messageInput.setEnabled(false);//if chat gets terminated your input will get disabled by this
                        socket.close();
                        break;
                    }
//                    System.out.println("Server : " + msg);
                    messageArea.append("Server: " + msg+"\n");
                }
            }catch (Exception e) {
//                e.printStackTrace();
                System.out.println("connection is closed");
            }
        };
        new Thread(r1).start();//starting the thread r1
    }

    // start writing [Method]
    public void startWriting() {
        //thread - data user ka lega and then send karega client tak
        Runnable r2 = () -> {
            System.out.println("writer started...");
            try {
                while (!socket.isClosed()) {
//                        System.out.println("In client writing Thread's while loop ");
                    BufferedReader br1 = new BufferedReader(new
                            InputStreamReader(System.in));//InputStreamReader
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();//for sometimes when data is not going.

                    if(content.equals("exit"))
                    {
                        System.out.println("client writer has exited");
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
        System.out.println("This is client");
        new Client();
    }
}

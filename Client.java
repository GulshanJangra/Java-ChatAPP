import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.*;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;

public class Client extends JFrame {

    Socket socket;
    BufferedReader br;
    PrintWriter out;
    
    private JLabel heading = new JLabel("Client Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font fontheading = new Font("Roboto",Font.BOLD,20);
    private Font font = new Font("Roboto",Font.PLAIN,20);
    

    public Client(){
        try{
            System.out.println("Sending request to server");
            socket = new Socket("192.168.0.101", 7778);
            System.out.println("Connection Done");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();

            startReading();
            startWriting();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void handleEvents() {
        messageInput.addKeyListener(new KeyListener(){

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub
                if(e.getKeyCode()==10){
                    System.out.println("You have pressed Enter button");
                    String contentSend = messageInput.getText();
                    messageArea.append("Me: "+contentSend+"\n");
                    out.println(contentSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();

                }
            }
            
        });

    }

    private void createGUI(){
        this.setTitle("Client");

        this.setSize(500,500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        heading.setFont(fontheading);
        messageArea.setFont(font);
        messageInput.setFont(font);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        messageArea.setEditable(false);

        

        this.setLayout(new BorderLayout());


        this.add(heading,BorderLayout.NORTH);
        DefaultCaret caret = (DefaultCaret)messageArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);

        this.setVisible(true);
    }

    public void startReading() throws Exception{
        Runnable r1 = () -> {
            System.out.println("Reader Started");
            try{
            while (true) {
                    String msg = br.readLine();
                    if(msg.equals("Exit")){
                    System.out.println("Server Terminated the chat");
                    JOptionPane.showMessageDialog(this, "Server terminated the chat");
                    messageInput.setEnabled(false);
                    socket.close();

                    break;
                    }
                    //System.out.println("Server: "+ msg);
                    messageArea.append("Server: " +msg+"\n");
                }
            }
            catch(Exception e){
                // e.printStackTrace();
                System.out.println("Connection Closed");
            }
        };
        new Thread(r1).start();
    }

    public void startWriting() {
        Runnable r2 = () -> {
            System.out.println("Writer Started");
            try{
            while(!socket.isClosed()){
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    
                    
                    out.println(content);
                    out.flush();
                    if(content.equals("Exit")){
                        socket.close();
                        break;
                    }
                }
                System.out.println("Connection Closed");
            }
            catch(Exception e){
                e.printStackTrace();

            }
        };
        new Thread(r2).start();
    }
    
    public static void main(String[] args) { 

        System.out.println("This is client");
        new Client();
    }
}
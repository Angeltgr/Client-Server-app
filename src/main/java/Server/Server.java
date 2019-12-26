package Server;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server {
    private ServerSocket server;
    public static  final int PORT = 8080; // переменная конастанта 8080 - любое число
    Server (){
        try {
            server = new ServerSocket(PORT); // создаём сокет для подключения клиента
            while (true) { // цикл для создания нескольких подключений
                Socket socket = server.accept();// адрес соединения (каждый раз уникальный) передаётся в socket
                MyThread myThread =  new MyThread(socket); // создаём объект и передаём ему socket
                myThread.start();
                System.out.println("Подключение удалось");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        Server server = new Server();

    }
}

class MyThread extends Thread {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    MyThread(Socket socket){
        this.socket = socket;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        while (true){
        String name = readMessage();
        String createMes = createMessage(name);
        sendMessage(createMes);
        }
    }

    public String readMessage(){
        String name = "";
        try {
            // Создаем объект XPathFactory

            XPathFactory xpathfactory = XPathFactory.newInstance();
            XPath path = xpathfactory.newXPath();

            Document doc = stringToDocument(in.readLine());

            String message = path.evaluate("/root/user/message", doc); // парсит сообщение, получает message
            name = path.evaluate("/root/user/name", doc); // парсит сообщение, получает name

            System.out.println("Сообщение пользователя " + message);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return name;
    }
    public String createMessage(String name){
        String answer = "";
        SimpleDateFormat dateFormat;
        String dateMess;
        Date date = new Date();
        dateFormat = new SimpleDateFormat( "dd.MM.yyyy HH:mm:ss" );
        dateMess = dateFormat.format(date);

        answer = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "                    <response>" +
                "                        <message>Добрый день, " + name + ", Ваше сообщение успешно обработано!</message>" +
                "                        <date>" + dateMess + "</date>" +
                "                    </response>\n";


        // код, который будет формировать сообщение
        return answer;
    }

    public  void sendMessage(String message){
        try {
            out.write(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // парсим строку в XML Document
    private static Document stringToDocument(String xmlStr) {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = builderFactory.newDocumentBuilder();
            // парсим переданную на вход строку с XML разметкой
            return docBuilder.parse(new InputSource(new StringReader(xmlStr)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}

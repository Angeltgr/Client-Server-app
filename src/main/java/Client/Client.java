package Client;

import org.ini4j.Ini;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {
    private Ini ini;
    private String address;
    private String port;
    private Socket socket;
    private String name;
    private String surname;
    private SimpleDateFormat dateFormat;
    private String dateMess;
    private BufferedReader in;
    private BufferedWriter out;

    Client (){

        try {
            ini = new Ini(new File("src/main/resources/config.ini")); // считываем конфигурационный файл
        } catch (IOException e) {
            e.printStackTrace();
        }
        address = ini.get("connection", "ip"); // в переменную address помещаем ip
        port = ini.get("connection","port"); // в переменную port помещаем port
        openConnection(); // вызывается функция создания соединения


        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true){
        String message = createMessage();
        sendMessage(message);
        System.out.println("Дошли");
        readMessage();
    }
    }

    // 1. Получать сообщение из потока
    // 2. Выводить на экран
    public void readMessage() {
        try {
            // Создаем объект XPathFactory

            XPathFactory xpathfactory = XPathFactory.newInstance();
            XPath path = xpathfactory.newXPath();

            Document doc = stringToDocument(in.readLine());

            String message = path.evaluate("/response/message", doc); // парсит сообщение, получает message
            System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
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

    public void setAddress(String address){
        this.address = address;
        }
    public String getAddress(){
        return address;
    }

    public void setPort(String port){
        this.port = port;
    }
    public String getPort(){
        return port;
    }

    public Socket getSocket() {
        return socket;
    }

    // функция создания соединения
    public void openConnection(){
        try {
            socket  = new Socket(address, Integer.parseInt(port)); // создаём переменную socket и передаём ей address и port и устанавливаем соединение с сервером
            System.out.println("Подключение созданно: host = " + address + " port = " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Не удалость подключиться");
        }
    }

    public  void sendMessage(String message){ // функция отправки сообщения, которое получит в свой аргумент
        try {
            out.write(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public String createMessage(){
        String result = ""; // содержит итоговое сообщение
        String message;
        Date date = new Date();
        dateFormat = new SimpleDateFormat ( "dd.MM.yyyy HH:mm:ss" );
        dateMess = dateFormat.format(date);
        BufferedReader reader = new BufferedReader(new InputStreamReader((System.in)));
        try {
            if (name == null) {
                while (true) {
                    System.out.print("Введите имя: ");
                    name = reader.readLine();

                    if (!name.equals("")) {
                        break;
                    } else {
                        System.out.println("Поле не должно быть пустым!");
                    }
                }
            }
            if (surname == null) {
                while (true) {
                    System.out.print("Введите фамилию: ");
                    surname = reader.readLine();
                    if (!surname.equals("")) {
                        break;
                    } else {
                        System.out.println("Поле не должно быть пустым!");
                    }
                }
            }
            System.out.print("Введите сообщение: ");
            message = reader.readLine();
            result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "   <root>" +
                    "       <user>" +
                    "           <name>" + name + "</name>" +
                    "           <secondname>" + surname + "</secondname>" +
                    "           <message>" + message + "</message>" +
                    "           <date>" + dateMess + "</date>" +
                    "       </user>" +
                    "   </root>";
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result + "\n";
    }


    public static void main(String[] args) {
        Client client = new Client();

    }
}
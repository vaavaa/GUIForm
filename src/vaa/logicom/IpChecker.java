package vaa.logicom;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

public class IpChecker {
    private static JFrame jFrame;
    private JComboBox comboBox1;
    private JButton button1;
    private JTextPane textPane1;
    private JTextField textField1;
    private JPanel jpanel;
    private JButton button2;
    private JCheckBox checkBox1;


    public String urlSERVICE_cache = "";
    public static String[] listServices = {"First One"};
    public String[] urlSERVICE = {"https://api.ipgeolocationapi.com/geolocate/%s"};
    public String myExternalIp = "https://api.my-ip.io/ip";
    public String browserIdentification = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";


    private static final Pattern PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static boolean validateIP(final String ip) {
        return PATTERN.matcher(ip).matches();
    }

    public IpChecker() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (textField1.getText().length() > 0) {
                    if (validateIP(textField1.getText())) {
                        if (comboBox1.getSelectedItem() != null) {
                            String urlString = String.format(urlSERVICE[0], textField1.getText());
                            try {
                                textPane1.setText("");
                                textPane1.getStyledDocument().insertString(0, urlString, null);
                                textPane1.getStyledDocument().insertString(textPane1.getText().length(), "\n \n", null);
                                textPane1.getStyledDocument().insertString(textPane1.getText().length(), getServiceResponse(urlString), null);
                                jFrame.pack();
                                jFrame.setVisible(true);

                            } catch (BadLocationException | IOException e) {
                                e.printStackTrace();
                            }
                        } else
                            JOptionPane.showMessageDialog(null, "Не указан сервис для получения данных", "Mistake", 1);
                    } else
                        JOptionPane.showMessageDialog(null, "Ip Address не корректен. Проверьте правильность ввода", "Mistake", 1);
                } else JOptionPane.showMessageDialog(null, "Пустое поле Ip Address", "Mistake", 1);

            }
        });
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        });
        checkBox1.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                String ip = "";
                try {
                    if (event.getStateChange() == 1) {
                        if (urlSERVICE_cache.length() > 0) ip = urlSERVICE_cache;
                        else {
                            ip = getServiceResponse(myExternalIp);
                            urlSERVICE_cache = ip;
                        }
                    } else {
                        ip = getInternalIP();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                textField1.setText(ip);

            }
        });

    }

    public static void main(String[] args) throws IOException {
        jFrame = new JFrame();
        jFrame.setContentPane(new IpChecker().jpanel);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
        JTextField jTextField = (JTextField) jFrame.getContentPane().getComponent(2);
        jTextField.setText(getInternalIP());
        for (String item : listServices) {
            ((JComboBox) jFrame.getContentPane().getComponent(0)).addItem(item);
        }
    }

    public static String getInternalIP() throws IOException {
        String ip = "";
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip;
    }

    public String getServiceResponse(String urlSource) throws IOException {
        String body = "";
        String a = urlSource;
        URLConnection connection = new URL(a).openConnection();
        connection
                .setRequestProperty("User-Agent", browserIdentification);
        connection.connect();
        BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream(),
                Charset.forName("UTF-8")));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            sb.append(line);
        }
        body = sb.toString();
        return body;
    }


}

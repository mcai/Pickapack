package net.pickapack.spider.noJs.crawler.apple;

import com.Ostermiller.util.CSVParser;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import net.pickapack.action.Action1;
import net.pickapack.spider.noJs.crawler.CrawlerLoggingEvent;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

public class FormiTunesAutomationStartup {
    private JTextField textFieldAppleId;
    private JTextArea textAreaCreditCard;
    private JButton buttonRegisterAppleId;
    private JPanel panel;
    private JButton buttonBuyGiftCard;
    private JTextField textFieldAppleIdPassword;
    private JButton buttonGenerateAppleIdPassword;
    private JTextArea textAreaLoggingEvents;
    private JButton buttonClearLog;
    private JButton buttonRegisterFreeAppleId;
    private JTextField textFieldGuid;
    private JTextField textFieldMachineName;
    private JTextField textFieldAppId;
    private JButton buttonBuyApp;
    private JTextArea textAreaKbsync;
    private JButton buttonLogin;
    private JButton buttonActivateAppleId;
    private JTextField textFieldEmailPassword;
    private JCheckBox checkBoxUseHttpProxy;
    private JTextField textFieldProxyHost;
    private JTextField textFieldProxyPort;

    private PropertiesConfiguration configuration;
    private File fileProperties;

    public FormiTunesAutomationStartup() {
        textFieldProxyHost.setText("");
        textFieldProxyPort.setText("" + -1);

        String lastMachineName;
        String lastGuid;
        String lastAppleId;
        String lastEmailPassword;
        String lastAppleIdPassword;
        String lastKbsync;
        String lastCreditCard;
        String lastAppId;
        boolean lastUseHttpProxy;
        String lastProxyHost;
        int lastProxyPort;

        try {
            fileProperties = new File(FileUtils.getUserDirectoryPath() + File.separator + "FormBuyGiftCardStartup.properties");
            if (!fileProperties.exists()) {
                try {
                    FileUtils.copyInputStreamToFile(FormiTunesAutomationStartup.class.getResourceAsStream("/FormBuyGiftCardStartup.properties"), fileProperties);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            configuration = new PropertiesConfiguration();
            configuration.setListDelimiter('|');
            configuration.setDelimiterParsingDisabled(false);
            configuration.load(fileProperties);
            lastMachineName = configuration.getString("lastMachineName", "");
            lastGuid = configuration.getString("lastGuid", "");
            lastAppleId = configuration.getString("lastAppleId", "");
            lastEmailPassword = configuration.getString("lastEmailPassword", "");
            lastAppleIdPassword = configuration.getString("lastAppleIdPassword", "");
            lastKbsync = configuration.getString("lastKbsync", "");
            lastCreditCard = configuration.getString("lastCreditCard", "");
            lastAppId = configuration.getString("lastAppId", "");
            lastUseHttpProxy = configuration.getBoolean("lastUseHttpProxy", false);
            lastProxyHost = configuration.getString("lastProxyHost", "");
            lastProxyPort = configuration.getInteger("lastProxyPort", -1);
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }

        textFieldMachineName.setText(lastMachineName);
        textFieldGuid.setText(lastGuid);
        textFieldAppleId.setText(lastAppleId);
        textFieldEmailPassword.setText(lastEmailPassword);
        textFieldAppleIdPassword.setText(lastAppleIdPassword);
        textAreaKbsync.setText(lastKbsync);
        textAreaCreditCard.setText(lastCreditCard);
        textFieldAppId.setText(lastAppId);

        checkBoxUseHttpProxy.setSelected(lastUseHttpProxy);
        textFieldProxyHost.setText(lastProxyHost);
        textFieldProxyPort.setText("" + lastProxyPort);

        textFieldProxyHost.setEnabled(checkBoxUseHttpProxy.isSelected());
        textFieldProxyPort.setEnabled(checkBoxUseHttpProxy.isSelected());

        buttonGenerateAppleIdPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFieldAppleIdPassword.setText(RegisterAppleIdNoJSCrawler.generateAppleIdPassword());
            }
        });
        buttonRegisterAppleId.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String machineName = textFieldMachineName.getText();
                final String guid = textFieldGuid.getText();
                final String email = textFieldAppleId.getText();
                final String appleIdPassword = textFieldAppleIdPassword.getText();

                if (machineName == null || machineName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入计算机名!");
                    return;
                }

                if (guid == null || guid.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入GUID!");
                    return;
                }

                if (email == null || email.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入Apple ID!");
                    return;
                }

                if (appleIdPassword == null || appleIdPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入Apple ID密码!");
                    return;
                }

                CSVParser parserCreditCard = new CSVParser(new StringReader(textAreaCreditCard.getText()));
                String[][] allCreditCardRows;
                try {
                    allCreditCardRows = parserCreditCard.getAllValues();
                } catch (IOException e1) {
                    throw new RuntimeException(e1);
                }

                if (allCreditCardRows == null || allCreditCardRows.length != 1) {
                    JOptionPane.showMessageDialog(null, "请输入信用卡 (以逗号隔开的12个字段)!");
                    return;
                }

                if (allCreditCardRows[0].length != 12) {
                    JOptionPane.showMessageDialog(null, "请输入信用卡 (以逗号隔开的12个字段)!");
                    return;
                }

                String[] parts = allCreditCardRows[0];
                final String cardNumber = parts[0].trim();
                final String ccv = parts[1].trim();
                final int expirationMonth = Integer.parseInt(parts[2].trim());
                final int expirationYear = Integer.parseInt(parts[3].trim());
                final String firstName = parts[4].trim();
                final String lastName = parts[5].trim();
                final String street = parts[6].trim();
                final String city = parts[7].trim();
                final String state = parts[8].trim();
                final String postalCode = parts[9].trim();
                final String areaCode = parts[10].trim();
                final String phone = parts[11].trim();

                disableButtons();

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        RegisterAppleIdNoJSCrawler.registerNewAppleId(
                                machineName, guid, email, appleIdPassword,
                                cardNumber, ccv, expirationMonth, expirationYear,
                                firstName, lastName, street, city, state, postalCode,
                                areaCode, phone,
                                new Action1<CrawlerLoggingEvent>() {
                                    @Override
                                    public void apply(final CrawlerLoggingEvent event) {
                                        SwingUtilities.invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                handleLoggingEvent(event);
                                            }
                                        });
                                    }
                                }, getProxyHost(), getProxyPort()
                        );

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                enableButtons();
                                textAreaLoggingEvents.append("\r\n");
                            }
                        });
                    }
                };
                thread.setDaemon(true);
                thread.start();
            }
        });
        buttonRegisterFreeAppleId.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String machineName = textFieldMachineName.getText();
                final String guid = textFieldGuid.getText();
                final String email = textFieldAppleId.getText();
                final String appleIdPassword = textFieldAppleIdPassword.getText();

                if (machineName == null || machineName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入计算机名!");
                    return;
                }

                if (guid == null || guid.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入GUID!");
                    return;
                }

                if (email == null || email.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入Apple ID!");
                    return;
                }

                if (appleIdPassword == null || appleIdPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入Apple ID密码!");
                    return;
                }

                disableButtons();

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        RegisterAppleIdNoJSCrawler.registerNewFreeAppleId(
                                machineName, guid, email, appleIdPassword,
                                new Action1<CrawlerLoggingEvent>() {
                                    @Override
                                    public void apply(final CrawlerLoggingEvent event) {
                                        SwingUtilities.invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                handleLoggingEvent(event);
                                            }
                                        });
                                    }
                                }, getProxyHost(), getProxyPort()
                        );

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                enableButtons();
                                textAreaLoggingEvents.append("\r\n");
                            }
                        });
                    }
                };
                thread.setDaemon(true);
                thread.start();
            }
        });
        buttonActivateAppleId.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String email = textFieldAppleId.getText();
                final String appleIdPassword = textFieldAppleIdPassword.getText();
                final String emailPassword = textFieldEmailPassword.getText();

                if (email == null || email.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入Apple ID!");
                    return;
                }

                if (appleIdPassword == null || appleIdPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入Apple ID密码!");
                    return;
                }

                if (emailPassword == null || emailPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入邮箱密码!");
                    return;
                }

                disableButtons();

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        ActivateAppleIdNoJSCrawler.activate(email, appleIdPassword,
                                emailPassword, new Action1<CrawlerLoggingEvent>() {
                            @Override
                            public void apply(final CrawlerLoggingEvent event) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        handleLoggingEvent(event);
                                    }
                                });
                            }
                        }
                        );

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                enableButtons();
                                textAreaLoggingEvents.append("\r\n");
                            }
                        });
                    }
                };
                thread.setDaemon(true);
                thread.start();
            }
        });
        buttonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String machineName = textFieldMachineName.getText();
                final String guid = textFieldGuid.getText();
                final String email = textFieldAppleId.getText();
                final String appleIdPassword = textFieldAppleIdPassword.getText();

                if (machineName == null || machineName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入计算机名!");
                    return;
                }

                if (guid == null || guid.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入GUID!");
                    return;
                }

                if (email == null || email.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入Apple ID!");
                    return;
                }

                if (appleIdPassword == null || appleIdPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入Apple ID密码!");
                    return;
                }

                disableButtons();

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        LoginAppleIdNoJSCrawler.loginAppleId(
                                machineName, guid, email, appleIdPassword,
                                new Action1<CrawlerLoggingEvent>() {
                                    @Override
                                    public void apply(final CrawlerLoggingEvent event) {
                                        SwingUtilities.invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                handleLoggingEvent(event);
                                            }
                                        });
                                    }
                                }, getProxyHost(), getProxyPort()
                        );

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                enableButtons();
                                textAreaLoggingEvents.append("\r\n");
                            }
                        });
                    }
                };
                thread.setDaemon(true);
                thread.start();
            }
        });
        buttonBuyApp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String machineName = textFieldMachineName.getText();
                final String guid = textFieldGuid.getText();
                final String email = textFieldAppleId.getText();
                final String appleIdPassword = textFieldAppleIdPassword.getText();
                final String kbsync = textAreaKbsync.getText();
                final String appId = textFieldAppId.getText();

                if (machineName == null || machineName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入计算机名!");
                    return;
                }

                if (guid == null || guid.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入GUID!");
                    return;
                }

                if (email == null || email.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入Apple ID!");
                    return;
                }

                if (appleIdPassword == null || appleIdPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入Apple ID密码!");
                    return;
                }

                if (kbsync == null || kbsync.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入kbsync!");
                    return;
                }

                if (appId == null || appId.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入appId!");
                    return;
                }

                CSVParser parserCreditCard = new CSVParser(new StringReader(textAreaCreditCard.getText()));
                String[][] allCreditCardRows;
                try {
                    allCreditCardRows = parserCreditCard.getAllValues();
                } catch (IOException e1) {
                    throw new RuntimeException(e1);
                }

                if (allCreditCardRows == null || allCreditCardRows.length != 1) {
                    JOptionPane.showMessageDialog(null, "请输入信用卡 (以逗号隔开的12个字段)!");
                    return;
                }

                if (allCreditCardRows[0].length != 12) {
                    JOptionPane.showMessageDialog(null, "请输入信用卡 (以逗号隔开的12个字段)!");
                    return;
                }

                String[] parts = allCreditCardRows[0];
                final String cardNumber = parts[0].trim();
                final String ccv = parts[1].trim();

                disableButtons();

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        BuyAppNoJSCrawler.buyApp(machineName, guid, email, appleIdPassword,
                                kbsync, cardNumber, ccv, appId,
                                new Action1<CrawlerLoggingEvent>() {
                                    @Override
                                    public void apply(final CrawlerLoggingEvent event) {
                                        SwingUtilities.invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                handleLoggingEvent(event);
                                            }
                                        });
                                    }
                                }, getProxyHost(), getProxyPort());

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                enableButtons();
                                textAreaLoggingEvents.append("\r\n");
                            }
                        });
                    }
                };
                thread.setDaemon(true);
                thread.start();
            }
        });
        buttonBuyGiftCard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String machineName = textFieldMachineName.getText();
                final String guid = textFieldGuid.getText();
                final String email = textFieldAppleId.getText();
                final String appleIdPassword = textFieldAppleIdPassword.getText();

                if (machineName == null || machineName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入计算机名!");
                    return;
                }

                if (guid == null || guid.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入GUID!");
                    return;
                }

                if (email == null || email.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入Apple ID!");
                    return;
                }

                if (appleIdPassword == null || appleIdPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入Apple ID密码!");
                    return;
                }

                disableButtons();

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        BuyGiftCardNoJSCrawler.buyGiftCard(machineName, guid, email, appleIdPassword,
                                new Action1<CrawlerLoggingEvent>() {
                                    @Override
                                    public void apply(final CrawlerLoggingEvent event) {
                                        SwingUtilities.invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                handleLoggingEvent(event);
                                            }
                                        });
                                    }
                                }, getProxyHost(), getProxyPort());

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                enableButtons();
                                textAreaLoggingEvents.append("\r\n");
                            }
                        });
                    }
                };
                thread.setDaemon(true);
                thread.start();
            }
        });
        buttonClearLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textAreaLoggingEvents.setText("");
            }
        });
        checkBoxUseHttpProxy.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                textFieldProxyHost.setEnabled(checkBoxUseHttpProxy.isSelected());
                textFieldProxyPort.setEnabled(checkBoxUseHttpProxy.isSelected());
            }
        });
    }

    private void disableButtons() {
        buttonGenerateAppleIdPassword.setEnabled(false);
        buttonRegisterAppleId.setEnabled(false);
        buttonRegisterFreeAppleId.setEnabled(false);
        buttonActivateAppleId.setEnabled(false);
        buttonLogin.setEnabled(false);
        buttonBuyApp.setEnabled(false);
        buttonBuyGiftCard.setEnabled(false);
        buttonClearLog.setEnabled(false);
        checkBoxUseHttpProxy.setEnabled(false);
    }

    private void enableButtons() {
        buttonGenerateAppleIdPassword.setEnabled(true);
        buttonRegisterAppleId.setEnabled(true);
        buttonRegisterFreeAppleId.setEnabled(true);
        buttonActivateAppleId.setEnabled(true);
        buttonLogin.setEnabled(true);
        buttonBuyApp.setEnabled(true);
        buttonBuyGiftCard.setEnabled(true);
        buttonClearLog.setEnabled(true);
        checkBoxUseHttpProxy.setEnabled(true);
    }

    public String getProxyHost() {
        return this.checkBoxUseHttpProxy.isSelected() ? this.textFieldProxyHost.getText() : null;
    }

    public int getProxyPort() {
        try {
            return this.checkBoxUseHttpProxy.isSelected() ? Integer.parseInt(this.textFieldProxyPort.getText()) : -1;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "请输入代理端口号!");
            return -1;
        }
    }

    private void handleLoggingEvent(CrawlerLoggingEvent event) {
        System.out.println(event.getMessage());
        this.textAreaLoggingEvents.setCaretPosition(this.textAreaLoggingEvents.getDocument().getLength());
        this.textAreaLoggingEvents.append(event.getMessage() + "\r\n");
    }

    public static void main(String[] args) {
        Font f = new Font("WenQuanYi Zen Hei", Font.PLAIN, 14);
        UIManager.put("Label.font", f);
        UIManager.put("Label.foreground", Color.black);
        UIManager.put("Button.font", f);
        UIManager.put("Menu.font", f);
        UIManager.put("MenuItem.font", f);
        UIManager.put("List.font", f);
        UIManager.put("CheckBox.font", f);
        UIManager.put("RadioButton.font", f);
        UIManager.put("ComboBox.font", f);
        UIManager.put("TextArea.font", f);
        UIManager.put("EditorPane.font", f);
        UIManager.put("ScrollPane.font", f);
        UIManager.put("ToolTip.font", f);
        UIManager.put("TextField.font", f);
        UIManager.put("TableHeader.font", f);
        UIManager.put("Table.font", f);

        JFrame frame = new JFrame("nilTunes");
        frame.setResizable(false);
        final FormiTunesAutomationStartup formiTunesAutomationStartup = new FormiTunesAutomationStartup();
        frame.setContentPane(formiTunesAutomationStartup.panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                formiTunesAutomationStartup.configuration.setProperty("lastMachineName", formiTunesAutomationStartup.textFieldMachineName.getText());
                formiTunesAutomationStartup.configuration.setProperty("lastGuid", formiTunesAutomationStartup.textFieldGuid.getText());
                formiTunesAutomationStartup.configuration.setProperty("lastAppleId", formiTunesAutomationStartup.textFieldAppleId.getText());
                formiTunesAutomationStartup.configuration.setProperty("lastEmailPassword", formiTunesAutomationStartup.textFieldEmailPassword.getText());
                formiTunesAutomationStartup.configuration.setProperty("lastAppleIdPassword", formiTunesAutomationStartup.textFieldAppleIdPassword.getText());
                formiTunesAutomationStartup.configuration.setProperty("lastKbsync", formiTunesAutomationStartup.textAreaKbsync.getText());
                formiTunesAutomationStartup.configuration.setProperty("lastCreditCard", formiTunesAutomationStartup.textAreaCreditCard.getText());
                formiTunesAutomationStartup.configuration.setProperty("lastAppId", formiTunesAutomationStartup.textFieldAppId.getText());

                formiTunesAutomationStartup.configuration.setProperty("lastUseHttpProxy", formiTunesAutomationStartup.checkBoxUseHttpProxy.isSelected());
                formiTunesAutomationStartup.configuration.setProperty("lastProxyHost", formiTunesAutomationStartup.textFieldProxyHost.getText());
                formiTunesAutomationStartup.configuration.setProperty("lastProxyPort", Integer.parseInt(formiTunesAutomationStartup.textFieldProxyPort.getText()));

                try {
                    formiTunesAutomationStartup.configuration.save(formiTunesAutomationStartup.fileProperties);
                } catch (ConfigurationException e1) {
                    throw new RuntimeException(e1);
                }
            }
        });
        frame.pack();
        frame.setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new FormLayout("fill:67px:noGrow,left:4dlu:noGrow,fill:150px:noGrow,left:4dlu:noGrow,fill:68px:noGrow,left:6dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:528px:noGrow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:38px:noGrow,top:4dlu:noGrow,center:87px:noGrow,top:4dlu:noGrow,center:60px:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:209px:grow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        final JLabel label1 = new JLabel();
        label1.setHorizontalAlignment(11);
        label1.setText("kbsync:");
        CellConstraints cc = new CellConstraints();
        panel.add(label1, cc.xy(1, 5));
        final JLabel label2 = new JLabel();
        label2.setHorizontalAlignment(11);
        label2.setText("信用卡:");
        panel.add(label2, cc.xy(1, 7));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setHorizontalScrollBarPolicy(31);
        scrollPane1.setVerticalScrollBarPolicy(20);
        panel.add(scrollPane1, cc.xyw(3, 7, 7));
        textAreaCreditCard = new JTextArea();
        textAreaCreditCard.setEditable(true);
        textAreaCreditCard.setLineWrap(true);
        textAreaCreditCard.setRows(2);
        textAreaCreditCard.setText("");
        scrollPane1.setViewportView(textAreaCreditCard);
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setHorizontalScrollBarPolicy(31);
        panel.add(scrollPane2, cc.xyw(1, 11, 9, CellConstraints.FILL, CellConstraints.FILL));
        textAreaLoggingEvents = new JTextArea();
        textAreaLoggingEvents.setEditable(false);
        textAreaLoggingEvents.setLineWrap(true);
        scrollPane2.setViewportView(textAreaLoggingEvents);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FormLayout("fill:147px:noGrow,left:4dlu:noGrow,fill:146px:noGrow,left:4dlu:noGrow,fill:63px:noGrow,left:4dlu:noGrow,fill:63px:noGrow,left:4dlu:noGrow,fill:66px:noGrow,left:9dlu:noGrow,fill:99px:noGrow,left:4dlu:noGrow,fill:89px:noGrow,left:4dlu:noGrow,fill:110px:noGrow", "center:d:grow"));
        panel.add(panel1, cc.xyw(1, 9, 9));
        buttonRegisterAppleId = new JButton();
        buttonRegisterAppleId.setText("收费Apple ID注册");
        panel1.add(buttonRegisterAppleId, cc.xy(1, 1));
        buttonRegisterFreeAppleId = new JButton();
        buttonRegisterFreeAppleId.setText("免费Apple ID注册");
        panel1.add(buttonRegisterFreeAppleId, cc.xy(3, 1));
        final JLabel label3 = new JLabel();
        label3.setHorizontalAlignment(11);
        label3.setText("App ID:");
        panel1.add(label3, cc.xy(9, 1));
        textFieldAppId = new JTextField();
        textFieldAppId.setEditable(true);
        textFieldAppId.setText("");
        panel1.add(textFieldAppId, cc.xy(11, 1));
        buttonBuyApp = new JButton();
        buttonBuyApp.setText("购买App");
        panel1.add(buttonBuyApp, cc.xy(13, 1));
        buttonBuyGiftCard = new JButton();
        buttonBuyGiftCard.setText("购买礼品卡");
        panel1.add(buttonBuyGiftCard, cc.xy(15, 1));
        buttonLogin = new JButton();
        buttonLogin.setText("登录");
        panel1.add(buttonLogin, cc.xy(7, 1));
        buttonActivateAppleId = new JButton();
        buttonActivateAppleId.setText("激活");
        panel1.add(buttonActivateAppleId, cc.xy(5, 1));
        final JScrollPane scrollPane3 = new JScrollPane();
        panel.add(scrollPane3, cc.xyw(3, 5, 7));
        textAreaKbsync = new JTextArea();
        textAreaKbsync.setEditable(true);
        textAreaKbsync.setRows(4);
        textAreaKbsync.setText("");
        scrollPane3.setViewportView(textAreaKbsync);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FormLayout("fill:67px:noGrow,left:4dlu:noGrow,fill:198px:noGrow,left:6dlu:noGrow,fill:max(d;4px):noGrow", "center:38px:noGrow"));
        panel.add(panel2, cc.xyw(1, 3, 9));
        final JLabel label4 = new JLabel();
        label4.setHorizontalAlignment(11);
        label4.setText("Apple ID:");
        panel2.add(label4, cc.xy(1, 1));
        textFieldAppleId = new JTextField();
        textFieldAppleId.setEditable(true);
        textFieldAppleId.setText("");
        panel2.add(textFieldAppleId, cc.xy(3, 1));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new FormLayout("fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:107px:noGrow,left:4dlu:noGrow,fill:88px:noGrow,left:4dlu:noGrow,fill:168px:noGrow,left:4dlu:noGrow,fill:110px:noGrow", "center:38px:noGrow"));
        panel2.add(panel3, cc.xy(5, 1));
        final JLabel label5 = new JLabel();
        label5.setHorizontalAlignment(11);
        label5.setText("Apple ID密码:");
        panel3.add(label5, cc.xy(5, 1));
        textFieldAppleIdPassword = new JTextField();
        textFieldAppleIdPassword.setEditable(true);
        textFieldAppleIdPassword.setText("");
        panel3.add(textFieldAppleIdPassword, cc.xy(7, 1));
        buttonGenerateAppleIdPassword = new JButton();
        buttonGenerateAppleIdPassword.setText("随机选取");
        panel3.add(buttonGenerateAppleIdPassword, cc.xy(9, 1));
        final JLabel label6 = new JLabel();
        label6.setHorizontalAlignment(11);
        label6.setText("邮箱密码:");
        panel3.add(label6, cc.xy(1, 1));
        textFieldEmailPassword = new JTextField();
        textFieldEmailPassword.setEditable(true);
        textFieldEmailPassword.setText("");
        panel3.add(textFieldEmailPassword, cc.xy(3, 1));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new FormLayout("fill:67px:noGrow,left:4dlu:noGrow,fill:198px:noGrow,left:6dlu:noGrow,fill:max(d;4px):noGrow", "center:38px:noGrow"));
        panel.add(panel4, cc.xyw(1, 1, 9));
        final JLabel label7 = new JLabel();
        label7.setHorizontalAlignment(11);
        label7.setText("计算机名:");
        panel4.add(label7, cc.xy(1, 1));
        textFieldMachineName = new JTextField();
        textFieldMachineName.setEditable(true);
        textFieldMachineName.setText("");
        panel4.add(textFieldMachineName, cc.xy(3, 1));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new FormLayout("fill:40px:noGrow,left:4dlu:noGrow,fill:519px:noGrow", "center:38px:noGrow"));
        panel4.add(panel5, cc.xy(5, 1));
        final JLabel label8 = new JLabel();
        label8.setHorizontalAlignment(11);
        label8.setText("GUID:");
        panel5.add(label8, cc.xy(1, 1));
        textFieldGuid = new JTextField();
        textFieldGuid.setEditable(true);
        textFieldGuid.setText("");
        panel5.add(textFieldGuid, cc.xy(3, 1));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new FormLayout("fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:123px:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:5dlu:noGrow,fill:105px:noGrow,left:4dlu:noGrow,fill:94px:noGrow", "center:d:grow"));
        panel.add(panel6, cc.xyw(1, 13, 9));
        buttonClearLog = new JButton();
        buttonClearLog.setText("清空日志");
        panel6.add(buttonClearLog, cc.xy(11, 1));
        final JLabel label9 = new JLabel();
        label9.setHorizontalAlignment(11);
        label9.setText("IP:");
        panel6.add(label9, cc.xy(3, 1));
        textFieldProxyHost = new JTextField();
        textFieldProxyHost.setEditable(true);
        textFieldProxyHost.setText("");
        panel6.add(textFieldProxyHost, cc.xy(5, 1));
        checkBoxUseHttpProxy = new JCheckBox();
        checkBoxUseHttpProxy.setText("使用HTTP代理");
        panel6.add(checkBoxUseHttpProxy, cc.xy(1, 1));
        final JLabel label10 = new JLabel();
        label10.setHorizontalAlignment(11);
        label10.setText("端口号:");
        panel6.add(label10, cc.xy(7, 1));
        textFieldProxyPort = new JTextField();
        textFieldProxyPort.setEditable(true);
        textFieldProxyPort.setText("");
        panel6.add(textFieldProxyPort, cc.xy(9, 1));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}

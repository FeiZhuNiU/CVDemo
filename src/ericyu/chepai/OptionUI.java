package ericyu.chepai;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by 麟 on 2016/4/4.
 */
public class OptionUI
{

    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JPanel practiceTab;
    private JTextField textField_start_hour;
    private JTextField textField_1st_add_second;
    private JTextField textField_1st_bid_second;
    private JTextField textField_2nd_add_second;
    private JTextField textField_start_minute;
    private JTextField textField_2nd_addMoney;
    private JTextField textField_2nd_bid_ready_second;
    private JTextField textField_2nd_bid_latest_second;
    private JButton runButton;
    private JCheckBox autoCheckBox;
    private JPanel inactionTab;
    private JButton updateButton;
    private JLabel label_update_status;

    public OptionUI() {
        runButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Configuration.bidTimeHour                   = Integer.parseInt(textField_start_hour.getText());
                Configuration.exitTimeHour                  = Configuration.bidTimeHour;
                Configuration.bidTimeMinute                 = Integer.parseInt(textField_start_minute.getText());
                Configuration.exitTimeMinute                = Configuration.bidTimeMinute + 1;
                Configuration.firstBidSecond                = Integer.parseInt(textField_1st_add_second.getText());
                Configuration.firstBidConfirmVCodeSecond    = Integer.parseInt(textField_1st_bid_second.getText());
                Configuration.addMoneySecond                = Integer.parseInt(textField_2nd_add_second.getText());
                Configuration.addMoneyRange                 = Integer.parseInt(textField_2nd_addMoney.getText());
                Configuration.vCodeConfirmSecond            = Integer.parseInt(textField_2nd_bid_ready_second.getText());
                Configuration.latestBidTimeSecond           = Integer.parseInt(textField_2nd_bid_latest_second.getText());
                System.out.println(Configuration.bidTimeHour);
                System.out.println(Configuration.bidTimeMinute);
                System.out.println(Configuration.firstBidSecond);
                System.out.println(Configuration.firstBidConfirmVCodeSecond);
                System.out.println(Configuration.addMoneySecond);
                System.out.println(Configuration.addMoneyRange);
                System.out.println(Configuration.vCodeConfirmSecond);
                System.out.println(Configuration.latestBidTimeSecond);
                runButton.setEnabled(false);
                Thread mainThread = new Thread(new Console(new String[]{}));
                mainThread.start();
            }
        });
        updateButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                updateButton.setEnabled(false);
                label_update_status.setText("更新中...");
                Console.main(new String[]{CommandConstants.UPGRADE});
                label_update_status.setText("更新完毕！");
            }
        });
    }

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("OptionUI");
        frame.setContentPane(new OptionUI().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


}

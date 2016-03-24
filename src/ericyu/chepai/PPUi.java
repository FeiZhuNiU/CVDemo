package ericyu.chepai;

import javax.swing.*;

/**
 * Created by 麟 on 2016/3/24.
 */
public class PPUi
{
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JPanel practiceTab;
    private JPanel inactionTab;
    private JTextField textField_startTime_hour;
    private JTextField textField_1st_add_second;
    private JTextField textField_start_minute;
    private JTextField textField_1st_bid_second;
    private JTextField textField_2nd_add_second;
    private JTextField textField_2nd_addMoney;
    private JTextField textField_2nd_bid_ready_second;
    private JTextField textField_2nd_bid_latest_second;
    private JButton runButton;
    private JCheckBox autoCheckBox;

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("PPUi");
        frame.setContentPane(new PPUi().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }


}

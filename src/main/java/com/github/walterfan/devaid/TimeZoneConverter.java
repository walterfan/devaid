package com.github.walterfan.devaid;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang.time.DateFormatUtils;

import com.github.walterfan.util.TimeZoneConv;
import com.github.walterfan.util.swing.SwingUtils;



/**
 * Timezone converter
 * @author walter.fan@gmail.com
 */

public class TimeZoneConverter extends JFrame implements ActionListener {
	/**
	 * 
	 */
	// protected final Log logger = LogFactory.getLog(TimeZoneConverter.class);
	private static final long serialVersionUID = 2347979971538515972L;

	private static final Font displayFont = new Font("Verdana", Font.PLAIN, 18);

	private static Vector<String> timeZones = new Vector<String>();

	private JList srcTimeZoneList;

	private JList destTimeZoneList;

	private static int srcTZIndex = 17;
	
	private static int destTZIndex = 69;
	
	private JTextField srcDateField;

	private JTextField destDateField;

	private JButton convButton;

	private JButton exitButton;

	private static Map<String, String> lookup = new LinkedHashMap<String, String>(75);

	static {
		lookup.put("(GMT+13:00) Nuku'alofa", "Pacific/Tongatapu");
		lookup.put("(GMT+12:00) Fiji, Kamchatka, Marshall Is.", "Pacific/Fiji");
		lookup.put("(GMT+12:00) Auckland, Wellington", "Pacific/Auckland");
		lookup.put("(GMT+11:00) Magadan, Solomon Is., New Caledonia", "Asia/Magadan");
		lookup.put("(GMT+10:00) Vladivostok", "Asia/Vladivostok");
		lookup.put("(GMT+10:00) Hobart", "Australia/Hobart");
		lookup.put("(GMT+10:00) Guam, Port Moresby", "Pacific/Guam");
		lookup.put("(GMT+10:00) Canberra, Melbourne, Sydney", "Australia/Sydney");
		lookup.put("(GMT+10:00) Brisbane", "Australia/Brisbane");
		lookup.put("(GMT+09:30) Adelaide", "Australia/Adelaide");
		lookup.put("(GMT+09:00) Yakutsk", "Asia/Yakutsk");
		lookup.put("(GMT+09:00) Seoul", "Asia/Seoul");
		lookup.put("(GMT+09:00) Osaka, Sapporo, Tokyo", "Asia/Tokyo");
		lookup.put("(GMT+08:00) Taipei", "Asia/Taipei");
		lookup.put("(GMT+08:00) Perth", "Australia/Perth");
		lookup.put("(GMT+08:00) Kuala Lumpur, Singapore", "Asia/Kuala_Lumpur");
		lookup.put("(GMT+08:00) Irkutsk, Ulaan Bataar", "Asia/Irkutsk");
		lookup.put("(GMT+08:00) Beijing, Chongqing, Hong Kong, Urumqi", "Asia/Hong_Kong");
		lookup.put("(GMT+07:00) Krasnoyarsk", "Asia/Krasnoyarsk");
		lookup.put("(GMT+07:00) Bangkok, Hanoi, Jakarta", "Asia/Bangkok");
		lookup.put("(GMT+06:30) Rangoon", "Asia/Rangoon");
		lookup.put("(GMT+06:00) Sri Jayawardenepura", "Asia/Colombo");
		lookup.put("(GMT+06:00) Astana, Dhaka", "Asia/Dhaka");
		lookup.put("(GMT+06:00) Almaty, Novosibirsk", "Asia/Almaty");
		lookup.put("(GMT+05:45) Kathmandu", "Asia/Katmandu");
		lookup.put("(GMT+05:30) Chennai, Kolkata, Mumbai, New Delhi", "Asia/Calcutta");
		lookup.put("(GMT+05:00) Islamabad, Karachi, Tashkent", "Asia/Karachi");
		lookup.put("(GMT+05:00) Ekaterinburg", "Asia/Yekaterinburg");
		lookup.put("(GMT+04:30) Kabul", "Asia/Kabul");
		lookup.put("(GMT+04:00) Baku, Tbilisi, Yerevan", "Asia/Baku");
		lookup.put("(GMT+04:00) Abu Dhabi, Muscat", "Asia/Dubai");
		lookup.put("(GMT+03:30) Tehran", "Asia/Tehran");
		lookup.put("(GMT+03:00) Nairobi", "Africa/Nairobi");
		lookup.put("(GMT+03:00) Moscow, St. Petersburg, Volgograd", "Europe/Moscow");
		lookup.put("(GMT+03:00) Kuwait, Riyadh", "Asia/Kuwait");
		lookup.put("(GMT+03:00) Baghdad", "Asia/Baghdad");
		lookup.put("(GMT+02:00) Jerusalem", "Asia/Jerusalem");
		lookup.put("(GMT+02:00) Helsinki, Kyiv, Riga, Sofia, Tallinn, Vilnius", "Europe/Helsinki");
		lookup.put("(GMT+02:00) Harare, Pretoria", "Africa/Harare");
		lookup.put("(GMT+02:00) Cairo", "Africa/Cairo");
		lookup.put("(GMT+02:00) Bucharest", "Europe/Bucharest");
		lookup.put("(GMT+02:00) Athens, Istanbul, Minsk", "Europe/Athens");
		lookup.put("(GMT+01:00) West Central Africa", "Africa/Lagos");
		lookup.put("(GMT+01:00) Sarajevo, Skopje, Warsaw, Zagreb", "Europe/Warsaw");
		lookup.put("(GMT+01:00) Brussels, Copenhagen, Madrid, Paris", "Europe/Brussels");
		lookup.put("(GMT+01:00) Belgrade, Bratislava, Budapest, Ljubljana, Prague", "Europe/Belgrade");
		lookup.put("(GMT+01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna", "Europe/Amsterdam");
		lookup.put("(GMT) Casablanca, Monrovia", "Africa/Casablanca");
		lookup.put("(GMT) Greenwich Mean Time : Dublin, Edinburgh, Lisbon, London", "Europe/Dublin");
		lookup.put("(GMT-01:00) Azores", "Atlantic/Azores");
		lookup.put("(GMT-01:00) Cape Verde Is.", "Atlantic/Cape_Verde");
		lookup.put("(GMT-02:00) Mid-Atlantic", "Atlantic/South_Georgia");
		lookup.put("(GMT-03:00) Brasilia", "America/Sao_Paulo");
		lookup.put("(GMT-03:00) Buenos Aires, Georgetown", "America/Buenos_Aires");
		lookup.put("(GMT-03:00) Greenland", "America/Thule");
		lookup.put("(GMT-03:30) Newfoundland", "America/St_Johns");
		lookup.put("(GMT-04:00) Atlantic Time (Canada)", "America/Montreal");
		lookup.put("(GMT-04:00) Caracas, La Paz", "America/Caracas");
		lookup.put("(GMT-04:00) Santiago", "America/Santiago");
		lookup.put("(GMT-05:00) Bogota, Lima, Quito", "America/Bogota");
		lookup.put("(GMT-05:00) Eastern Time (US & Canada)", " America/New_York");
		lookup.put("(GMT-05:00) Indiana (East)", "America/Indianapolis");
		lookup.put("(GMT-06:00) Central America", "America/Costa_Rica");
		lookup.put("(GMT-06:00) Central Time (US & Canada)", "America/Chicago");
		lookup.put("(GMT-06:00) Guadalajara, Mexico City, Monterrey", "America/Mexico_City");
		lookup.put("(GMT-06:00) Saskatchewan", "America/Winnipeg");
		lookup.put("(GMT-07:00) Arizona", "America/Phoenix");
		lookup.put("(GMT-07:00) Chihuahua, La Paz, Mazatlan", "America/Tegucigalpa");
		lookup.put("(GMT-07:00) Mountain Time (US & Canada)", "America/Denver");
		lookup.put("(GMT-08:00) Pacific Time (US & Canada); Tijuana", "America/Los_Angeles");
		lookup.put("(GMT-09:00) Alaska", "America/Anchorage");
		lookup.put("(GMT-10:00) Hawaii", "Pacific/Honolulu");
		lookup.put("(GMT-11:00) Midway Island, Samoa", "Pacific/Apia");
		lookup.put("(GMT-12:00) International Date Line West", "MIT");

		for (Map.Entry<String,String> entry: lookup.entrySet()) {
			timeZones.add(entry.getKey());

		} 
	}

	public TimeZoneConverter() {
		super("Time Zone Converter");
		SwingUtils.setNativeLookAndFeel();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});
		Container content = getContentPane();

		// Create the JList, set the number of visible rows, add a
		// listener, and put it in a JScrollPane.
		srcTimeZoneList = createTimeZoneList(srcTZIndex);
		destTimeZoneList = createTimeZoneList(destTZIndex);

		JScrollPane srclistPane = new JScrollPane(srcTimeZoneList);

		JScrollPane destlistPane = new JScrollPane(destTimeZoneList);
		
		JPanel rightPane = new JPanel(new BorderLayout());
		JLabel convLabel = new JLabel(" --> ", SwingConstants.CENTER);
		convLabel.setFont(displayFont);
		
		rightPane.add(convLabel,BorderLayout.WEST);
		rightPane.add(destlistPane,BorderLayout.CENTER);
		
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BorderLayout());
		listPanel.setBackground(Color.white);
		Border listPanelBorder = BorderFactory
				.createTitledBorder("Time Zone List");
		listPanel.setBorder(listPanelBorder);
		listPanel.add(srclistPane,BorderLayout.WEST);
		listPanel.add(rightPane,BorderLayout.CENTER);

		content.add(listPanel, BorderLayout.CENTER);

		JLabel dateLabel = new JLabel("Date: ");
		dateLabel.setFont(displayFont);

		srcDateField = new JTextField(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"), 12);
		srcDateField.setFont(displayFont);

		destDateField = new JTextField("yyyy-MM-dd HH:mm:ss", 12);
		destDateField.setFont(displayFont);

		JPanel convPanel = new JPanel();
		convPanel.setBackground(Color.white);
		Border valuePanelBorder = BorderFactory
				.createTitledBorder("Date Conversion");
		convPanel.setBorder(valuePanelBorder);

		convButton = new JButton(" Convert ");
		convButton.addActionListener(this);

		exitButton = new JButton(" Exit ");
		exitButton.addActionListener(this);

		convPanel.add(dateLabel);
		convPanel.add(srcDateField);
		convPanel.add(convButton);
		convPanel.add(destDateField);
		
		addAboutButton(convPanel);
		convPanel.add(exitButton);
		
		content.add(convPanel, BorderLayout.SOUTH);

			
		JScrollBar bar = srclistPane.getVerticalScrollBar();
		bar.setValue(srcTZIndex*24);
		
		JScrollBar bar1 = destlistPane.getVerticalScrollBar();
		bar1.setValue(destTZIndex*24);

	}

	private void addAboutButton(JPanel convPanel) {
		JButton aboutButton = new JButton("About");
		convPanel.add(aboutButton);

		aboutButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
            	JOptionPane.showMessageDialog(null, "\nVersion 0.2 02/26/07\n" +
            			"wrote by Walter Fan (walter.fan@gmail.com)\n\n"
            			,"About the tiny tool", JOptionPane.INFORMATION_MESSAGE);

            }
        });
	}



	private JList createTimeZoneList(int index) {
		JList timeZoneList = new JList(timeZones);
		timeZoneList.setVisibleRowCount(8);
		timeZoneList.setFont(displayFont);
		timeZoneList.ensureIndexIsVisible(index);
		timeZoneList.setSelectedIndex(index);
		timeZoneList.addListSelectionListener(new ValueReporter());
		timeZoneList.setFixedCellWidth(300);
		return timeZoneList;
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == convButton) {
			String datestr = convertTZ(srcDateField.getText(),lookup.get(srcTimeZoneList
					.getSelectedValue().toString()),lookup.get(destTimeZoneList
							.getSelectedValue().toString()));
			destDateField.setText(datestr);
		} else if (source == exitButton)
			System.exit(0);

	}
	
	private String convertTZ(String srcDataStr, String srcTZID, String destTZID) {
		String datestr = "N/A";
		try {
			TimeZone srcTZ = TimeZone.getTimeZone(srcTZID);
			TimeZone destTZ = TimeZone.getTimeZone(destTZID);
			datestr = TimeZoneConv.convTZ4Date(srcDataStr,srcTZ, destTZ);
		} catch (Exception e1) {
			// logger.error(e1.getMessage());
			e1.printStackTrace();
		}
		return datestr;
	}

	private class ValueReporter implements ListSelectionListener {
		/**
		 * You get three events in many cases -- one for the deselection of the
		 * originally selected entry, one indicating the selection is moving,
		 * and one for the selection of the new entry. In the first two cases,
		 * getValueIsAdjusting returns true, thus the test below when only the
		 * third case is of interest.
		 */
		public void valueChanged(ListSelectionEvent event) {
			if (!event.getValueIsAdjusting()) {
				if(event.getSource()==srcTimeZoneList) {
					String srcTZID = lookup.get(srcTimeZoneList.getSelectedValue().toString());
					String selTZID = lookup.get(srcTimeZoneList.getSelectedValue().toString());
					String datestr = convertTZ(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"), srcTZID, selTZID);
					srcDateField.setText(datestr);
				} else if(event.getSource()==destTimeZoneList) {
					String srcTZID = lookup.get(srcTimeZoneList.getSelectedValue().toString());
					String selTZID = lookup.get(destTimeZoneList.getSelectedValue().toString());
					String datestr = convertTZ(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"),srcTZID, selTZID);
					destDateField.setText(datestr);
				}
			}
		}
	}

	public static void main(String[] args) {
		JFrame f = new TimeZoneConverter();
		SwingUtils.run(f, 800, 600);
	}
}

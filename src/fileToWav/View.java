package fileToWav;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class View extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	File file;
	FileToWav transform = new FileToWav();

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					View frame = new View();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public View() {
		setTitle(".Wavefier");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 190);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblTransformAnyFile = new JLabel("Transform any file to a .wav file");
		lblTransformAnyFile.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblTransformAnyFile.setBounds(12, 0, 260, 30);
		contentPane.add(lblTransformAnyFile);

		JButton btnConvertToWav = new JButton("Convert to .wav");
		btnConvertToWav.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = transform.convertToAudio(file.getPath());
				JOptionPane.showMessageDialog(null, msg);
			}
		});
		btnConvertToWav.setEnabled(false);
		btnConvertToWav.setBounds(10, 96, 125, 40);
		contentPane.add(btnConvertToWav);

		JButton btnConvertToFile = new JButton("Convert to File");
		btnConvertToFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = transform.convertToFile(file.getPath());
				JOptionPane.showMessageDialog(null, msg);
			}
		});
		btnConvertToFile.setEnabled(false);
		btnConvertToFile.setBounds(147, 96, 125, 40);
		contentPane.add(btnConvertToFile);

		JLabel lblNone = new JLabel("None");
		lblNone.setBounds(122, 55, 163, 16);
		contentPane.add(lblNone);

		JButton btnBrowser = new JButton("Browser");
		btnBrowser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.showOpenDialog(fileChooser);
				file = fileChooser.getSelectedFile();

				// set browser path JLabel
				lblNone.setText(file.getPath());

				if (!FileToWav.isWaveFile(file.getName())) {
					// File isn't .wav
					btnConvertToWav.setEnabled(true);
					btnConvertToFile.setEnabled(false);
				} else {
					// File is .wav
					btnConvertToWav.setEnabled(false);
					btnConvertToFile.setEnabled(true);
				}
			}
		});
		btnBrowser.setBounds(10, 43, 100, 40);
		contentPane.add(btnBrowser);

	}
}

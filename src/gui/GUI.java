package gui;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import controller.Controller;
import controller.InvalidPathException;

/**
 * Main GUI class that initializes the user interface.
 * @author Idicious
 *
 */
public class GUI extends JFrame {

	private static final long serialVersionUID = 2941318999657277463L;
	
	private Container contents; 
	private JFileChooser fileChooser; 
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private JTextField startUrl;
	private JTextField targetUrl;
	private JButton selectTarget;
	private JButton selectStart;

	/**
	 * Initializes the GUI of the application
	 */
	public GUI() {
		
		this.setLookAndFeel();
			
		this.setTitle("Filewalker");
		this.setMinimumSize(new Dimension(250, 100));
		
		this.setJMenuBar(makeMenu());
		
		this.contents = getContentPane();
		this.contents.setLayout(new BorderLayout());
		
		this.makeContentWindow();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.pack();
		this.setLocationRelativeTo(null); // Centers application on screen
		this.setVisible(true);
	}
	
	/**
	 * Prints given string to text area and appends it with \n, then if 
	 * toEndOfLine is true sets the Caret to the end of the text.
	 * @param text String to display
	 * @param toEndOfLine Whether to move Caret to end of line
	 */
	public void displayLine(String text, boolean toEndOfLine) {
		textArea.append(text + "\n");
		
		if(toEndOfLine) {
			textArea.setCaretPosition(textArea.getDocument().getLength());
		}
	}

	/**
	 * Makes the main contentwindow area
	 */
	private void makeContentWindow() {
		this.initializeFileChooser();
		
		JPanel buttonContainer = new JPanel(new GridLayout(3, 1));
		
		JLabel startLabel = new JLabel("Start:");
		startLabel.setPreferredSize(new Dimension(70, startLabel.getPreferredSize().height));
		JPanel startContainer = new JPanel(new FlowLayout());
		
		startUrl = new JTextField();
		startUrl.setPreferredSize(new Dimension(300, startUrl.getPreferredSize().height));
		
		selectStart = new JButton("Browse"); // Button that opens file chooser
		selectStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int returnValue = fileChooser.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		          Path selectedPath = fileChooser.getSelectedFile().toPath();
		          //Controller.getInstance().setStart(selectedPath);
		          startUrl.setText(selectedPath.toString());
		        } 
			}
		});
		startContainer.add(startLabel);
		startContainer.add(startUrl);
		startContainer.add(selectStart);
		buttonContainer.add(startContainer);
		
		JLabel destinationLabel = new JLabel("Destination:");
		destinationLabel.setPreferredSize(new Dimension(70, destinationLabel.getPreferredSize().height));
		JPanel destinationContainer = new JPanel(new FlowLayout());
		
		targetUrl = new JTextField();
		targetUrl.setPreferredSize(new Dimension(300, targetUrl.getPreferredSize().height));
		
		selectTarget = new JButton("Browse"); // Button that opens file chooser
		selectTarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnValue = fileChooser.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		          Path selectedPath = fileChooser.getSelectedFile().toPath();
		          //Controller.getInstance().setTarget(selectedPath);
		          targetUrl.setText(selectedPath.toString());
		        } 
			}
		});
		destinationContainer.add(destinationLabel);
		destinationContainer.add(targetUrl);
		destinationContainer.add(selectTarget);
		buttonContainer.add(destinationContainer);
		
		JPanel beginContainer = new JPanel(new FlowLayout());
		JButton startButton = new JButton("Start"); // Button that opens file chooser
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPaths();
				
				try {
					Controller.getInstance().start();
				} catch(InvalidPathException ex){
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		JButton stopButton = new JButton("Stop"); // Button that opens file chooser
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Controller.getInstance().stop();
			}
		});
		beginContainer.add(startButton);
		beginContainer.add(stopButton);
		buttonContainer.add(beginContainer);
		

		this.textArea = new JTextArea(10, 30);
		this.textArea.setEditable(false);
		this.textArea.setWrapStyleWord(true);
		
		this.scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		this.contents.add(buttonContainer, BorderLayout.NORTH);
		this.contents.add(scrollPane, BorderLayout.CENTER);
	}
	
	private void setPaths() {
		if(!startUrl.getText().trim().isEmpty()) {
			Controller.getInstance().setStart(Paths.get(startUrl.getText().trim()));
		} else {
			Controller.getInstance().setStart(null);
		}
		
		if(!targetUrl.getText().trim().isEmpty()) {
			Controller.getInstance().setTarget(Paths.get(targetUrl.getText().trim()));
		} else {
			Controller.getInstance().setTarget(null);
		}
	}

	/**
	 * Creates and sets all the options for the FileChooser
	 */
	private void initializeFileChooser() {
		this.fileChooser = new JFileChooser();
	}

	/**
	 * Gets the current OS and sets the look and feel of application to that.
	 */
	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			/* Default look and feel */
		}
	}

	/**
	 * Makes the menubar of the application
	 * @return
	 */
	private JMenuBar makeMenu() {
		JMenuBar menuBar = new JMenuBar();
    	
    	JMenu file = new JMenu("File");
    	menuBar.add(file);
	    	JMenuItem quit = new JMenuItem("Quit");
	    	quit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					System.exit(EXIT_ON_CLOSE);
				}
	    	});
	    	file.add(quit);
    	
    	JMenu help = new JMenu("Help");
    	menuBar.add(help);
			JMenuItem about = new JMenuItem("About");
			about.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) { 
					openInfoBox();
				}
			});
			help.add(about);
    	
		return menuBar;
	}

	protected void openInfoBox() {
		String title = "Info";
		String message =  "This program iterates over the filesystem from the chosen directory or file downwards. \n";
			   message += "Every file it finds it will attempt to copy to a destination directory and add an entry \n";
			   message += "to the database containing the copied path and extension group. \n";
			    
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
}

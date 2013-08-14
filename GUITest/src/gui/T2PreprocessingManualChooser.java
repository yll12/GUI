package gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sun.java2d.xr.MutableInteger;
import utils.GUIUtilities;
import utils.Pair;
import utils.Triple;

@SuppressWarnings("serial")
public class T2PreprocessingManualChooser extends JApplet {
	private JCheckBox chckbxParcellationAndSegmentation;
	private JTextField textField_inputCount;
	private JTextField textField_numberOfConcurrentProcess;
	private List<String> ageTextFields = new LinkedList<String>();
	private List<String> inputTextFields = new LinkedList<String>();
	private List<Triple<JTextField, JTextField, JSlider>> textFields = new LinkedList<Triple<JTextField, JTextField, JSlider>>();
	private final String questionToolTip = "Specify the total number of data(Click for more details)";
	private final String dataToolTip = "Specify the total number of data";
	private final String inputToolTip = "Specify the full path to the image file";
	private final String clearToolTip = "Clear all inputs and reset age to default(36)";
	private final String openToolTip = "Open File";
	private final String go1ToolTip = "Click to set the total number of data to the specified number";
	private final String go2ToolTip = "Starts T2 Pre-processing";
	private final String helpToolTip = "Help";
	private final String cancelToolTip = "Cancel";
	private final String switchViewToolTip = "This view automatically allocates datas";
	private final String parcellationToolTip = "Enable this to run parcellation and segmentation process";
	private final String ageToolTip = "Specify age at scan(possible range are: 28 ~ 44)";
	private final String processToolTip = "Specify the number of process to be run at the same time";
	private final String segmentationToolTip = "Run this if and only if you have completed T2-Preprocessing";
	private JPanel panel;
	private static final int heightDifference = 30;
	private static int dataIndex = 0;
	private static JFrame f;
	private static JApplet applet;
	private ImageIcon icon;
	private JFrame errors;

	/**
	 * Create the applet.
	 */
	public T2PreprocessingManualChooser() {
	}

	public T2PreprocessingManualChooser(int dataIndex) {
		T2PreprocessingManualChooser.dataIndex = dataIndex;
	}

	public T2PreprocessingManualChooser(int dataIndex, List<String> inputTextFields, List<String> ageTextFields) {
		T2PreprocessingManualChooser.dataIndex = dataIndex;
		this.inputTextFields = inputTextFields;
		this.ageTextFields = ageTextFields;
	}

	public T2PreprocessingManualChooser(int dataIndex, List<String> inputTextFields, String ageTextFileLocation) {
		T2PreprocessingManualChooser.dataIndex = dataIndex;
		this.inputTextFields = inputTextFields;
		if (!ageTextFileLocation.equals("NotSpecified")) {
			populateAgeList(ageTextFields, ageTextFileLocation);
		}
		GUIUtilities.rearrangeList(inputTextFields);
	}

	private void populateAgeList(List<String> ageList, String ageTextFileLocation) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(ageTextFileLocation));
			String line;
			while ((line = br.readLine()) != null) {
				ageList.add(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init() {
		GUIUtilities.initLookAndFeel("Nimbus");
		panel = new JPanel();
		getContentPane().setLayout(null);
		getContentPane().setBackground(panel.getBackground());
		icon = initIcon();
		panel.setLayout(null);
		panel.setPreferredSize(new Dimension(520, (2 * (dataIndex + 1)) * heightDifference));
		initInputDirectory(dataIndex);
		if (dataIndex > 9) {
			JScrollPane scrollPane = new JScrollPane(panel);
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setBounds(6, 37, 510, 605);
			scrollPane.setAutoscrolls(true);
			scrollPane.getVerticalScrollBar().setUnitIncrement(16);
			getContentPane().add(scrollPane);
		} else {
			panel.setBounds(6, 37, 510, (2 * (dataIndex + 1)) * heightDifference);
			getContentPane().add(panel);
		}

		errors = new JFrame();
		errors.setVisible(false);

		errors.pack();

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnCancel.setToolTipText(cancelToolTip);
		btnCancel.setBounds(429, 175 + 2 * GUIUtilities.increaseByHeight(dataIndex, heightDifference), 71, 25);
		getContentPane().add(btnCancel);

		JButton btnStartProcess = new JButton("Go");
		btnStartProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int preferredNumberOfConcurrentProcess;
				try {
					preferredNumberOfConcurrentProcess = Integer.parseInt(textField_numberOfConcurrentProcess.getText());
					if (preferredNumberOfConcurrentProcess <= 0) {
						JOptionPane.showMessageDialog(null, "The number of concurrent process must be positive integer");
						return;
					}
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null, "Please specify the number of concurrent process(must be positive integer)");
					return;
				}

				Deque<Pair<String, String[]>> listOfInputsForT2 = new LinkedList<Pair<String, String[]>>();
				Deque<Pair<String, String[]>> listOfInputsForSegmentation = new LinkedList<Pair<String, String[]>>();
				List<Thread> listOfObserverThreads = new LinkedList<Thread>();
				for (int i = 0; i <= dataIndex; i++) {
					Triple<JTextField, JTextField, JSlider> triple = textFields.get(i);
					String inputData = triple.getA().getText();
					String age = triple.getB().getText();
					age = GUIUtilities.checkAge(age);
					if (GUIUtilities.isInputValid(i, inputData)) {

						String workingdir = GUIUtilities.getWorkingDirectory(inputData);
						
						String[] args = { inputData, age, workingdir };

						GUIUtilities.populateInputs(listOfInputsForT2, inputData, args, "./T2PreprocessingScripts.sh ");

						if (chckbxParcellationAndSegmentation.isSelected()) {

							String[] args2 = { GUIUtilities.getFileName(inputData), age, workingdir };

							GUIUtilities.populateInputs(listOfInputsForSegmentation, inputData, args2, "./segmentation.sh ");

						}

					}
				}
				
				int numberOfConcurrentProcess =
						GUIUtilities.getNumberOfConcurrentProcess(listOfInputsForT2.size(), preferredNumberOfConcurrentProcess);
				for (int i = 0; i < numberOfConcurrentProcess; i++) {
					listOfObserverThreads.add(null);
				}
				do {
					for (int i = 0; i < numberOfConcurrentProcess; i++) {
						Thread t = listOfObserverThreads.get(i);
						if (!listOfInputsForT2.isEmpty()) {
							if (t == null) {
								GUIUtilities.addThread(i, listOfInputsForT2, listOfObserverThreads);
								continue;
							} else if ((!t.isAlive() || t.getState() == Thread.State.TERMINATED)) {
								GUIUtilities.addThread(i, listOfInputsForT2, listOfObserverThreads);
								continue;
							}
						} else {
							if (t != null && (!t.isAlive() || t.getState() == Thread.State.TERMINATED)) {
								listOfObserverThreads.remove(i);
								listOfObserverThreads.add(i, null);
							}
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				} while (!isObserversDead(listOfObserverThreads));

				if (chckbxParcellationAndSegmentation.isSelected()) {

					for (Pair<String, String[]> pair : listOfInputsForSegmentation) {
						String[] cmdArray = pair.getB();
						String inputData = pair.getA();
						Thread t = GUIUtilities.createExecutingThread(cmdArray);
						t.start();
						String workdir = GUIUtilities.getWorkingDirectory(inputData);
						while (t.isAlive() || t.getState() != Thread.State.TERMINATED) {
							if (GUIUtilities.hasFinished(inputData, workdir, "_error") || GUIUtilities.hasFinished(inputData, workdir, "_success")) {
								break;
							}
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
					}
				}

			}

			private <T> boolean isObserversDead(List<T> list) {
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i) != null) {
						return false;
					}
				}
				return true;
			}

		});
		btnStartProcess.setToolTipText(go2ToolTip);
		btnStartProcess.setBounds(380, 175 + 2 * GUIUtilities.increaseByHeight(dataIndex, heightDifference), 45, 25);
		getContentPane().add(btnStartProcess);

		JButton btnClear = new JButton("Clear");
		btnClear.setBounds(310, 175 + 2 * GUIUtilities.increaseByHeight(dataIndex, heightDifference), 65, 25);
		getContentPane().add(btnClear);
		btnClear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (Triple<JTextField, JTextField, JSlider> input : textFields) {
					input.getB().setText("36");
					input.getA().setText(null);
					input.getC().setValue(36);
				}
			}

		});
		btnClear.setToolTipText(clearToolTip);

		JButton btnRunSegmentationOnly = new JButton("Run Segmentation Only");
		btnRunSegmentationOnly.setBounds(300, 140 + 2 * GUIUtilities.increaseByHeight(dataIndex, heightDifference), 180, 25);
		btnRunSegmentationOnly.setToolTipText(segmentationToolTip);
		getContentPane().add(btnRunSegmentationOnly);
		btnRunSegmentationOnly.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Deque<Pair<String, String[]>> listOfInputsForSegmentation = new LinkedList<Pair<String, String[]>>();
				for (int i = 0; i <= dataIndex; i++) {
					Triple<JTextField, JTextField, JSlider> triple = textFields.get(i);
					String inputData = triple.getA().getText();
					String age = triple.getB().getText();
					age = GUIUtilities.checkAge(age);
					if (GUIUtilities.isInputValid(i, inputData)) {
						String workingdir = GUIUtilities.getWorkingDirectory(inputData);
						String[] args = { GUIUtilities.getFileName(inputData), age, workingdir };
						GUIUtilities.populateInputs(listOfInputsForSegmentation, inputData, args, "./segmentation.sh ");
					}
				}
				Thread t;
				for (Pair<String, String[]> pair : listOfInputsForSegmentation) {
					String[] cmdArray = pair.getB();
					String inputData = pair.getA();
					t = GUIUtilities.createExecutingThread(cmdArray);
					t.start();
					String workdir = GUIUtilities.getWorkingDirectory(inputData);
					while (t.isAlive() || t.getState() != Thread.State.TERMINATED) {
						if (GUIUtilities.hasFinished(inputData, workdir, "_error") || GUIUtilities.hasFinished(inputData, workdir, "_success")) {
							break;
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							JOptionPane.showMessageDialog(null, "Unexpected Error Occurred!");
							continue;
						}
					}
				}
			}

		});

		JButton btnSwitchViewToAuto = new JButton("Switch View to Auto");
		btnSwitchViewToAuto.setBounds(6, 175 + 2 * GUIUtilities.increaseByHeight(dataIndex, heightDifference), 150, 25);
		btnSwitchViewToAuto.setToolTipText(switchViewToolTip);
		getContentPane().add(btnSwitchViewToAuto);
		btnSwitchViewToAuto.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int ret = JOptionPane.showConfirmDialog(null, "Switch to Auto file chooser?", "Switch view", JOptionPane.OK_CANCEL_OPTION);
				if (ret == JOptionPane.OK_OPTION) {
					try {
						Runtime.getRuntime().exec("java -jar T2PreprocessingAuto.jar");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					System.exit(0);
				}
			}

		});

		JButton btnHelp = new JButton("Help");
		btnHelp.setBounds(236, 176 + 2 * GUIUtilities.increaseByHeight(dataIndex, heightDifference), 66, 25);
		getContentPane().add(btnHelp);
		btnHelp.setToolTipText(helpToolTip);
		btnHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				JApplet applet = new JApplet() {
					@Override
					public void init() {
						GUIUtilities.initLookAndFeel("Nimbus");
						Container content = getContentPane();
						content.setLayout(new GridBagLayout());
						GridBagConstraints c = new GridBagConstraints();
						GUIUtilities.initializeConstraints(c);
						MutableInteger lineNumber = new MutableInteger(0);
						GUIUtilities.createLine(lineNumber, content, c, "Number of datasets : Total number of image data to be processed."
								+ " Specify the number either using the text field or the slider and click Go. ");
						GUIUtilities.createLine(lineNumber, content, c, " ");
						GUIUtilities.createLine(lineNumber, content, c,
								"Input data : Specify the full path to the image file either by typing it or using the Open button");
						GUIUtilities.createLine(lineNumber, content, c, " ");
						GUIUtilities.createLine(lineNumber, content, c,
								"Age at scan : Specify the age at scan corresponding to the image file in the text field or using the slider.");
						GUIUtilities.createLine(lineNumber, content, c, " ");
						GUIUtilities.createLine(lineNumber, content, c, "Parcellation and segmentation : Check this box to allow parcellation"
								+ " and segmentation for all image files");
						GUIUtilities.createLine(lineNumber, content, c, " ");
						GUIUtilities.createLine(lineNumber, content, c, "Number of Concurrent Process : Specify the number of process to be run "
								+ "at any given time(default as 4)");
						GUIUtilities.createLine(lineNumber, content, c, " ");
						GUIUtilities.createLine(lineNumber, content, c,
								"Switch View to Auto : A convenient way to find all the image data by specifying "
										+ "the parent folder of all the image files");
						GUIUtilities.createLine(lineNumber, content, c, " ");
						GUIUtilities
								.createLine(lineNumber, content, c, "Clear : Clear all input data text fields and reset all ages to 36 (default)");
						GUIUtilities.createLine(lineNumber, content, c, " ");
						GUIUtilities.createLine(lineNumber, content, c, "Go : Starts the T2-preprocessing");
						GUIUtilities.createLine(lineNumber, content, c, " ");
						GUIUtilities.createLine(lineNumber, content, c,
								"Run Segmentation Only : Starts the segmentation process(Only run this if T2-preprocessing is finished).");
					}
				};
				applet.init();
				frame.setContentPane(applet.getContentPane());
				frame.setBounds(getX(), getY(), 50 + (int) frame.getPreferredSize().getWidth(), 50 + (int) frame.getPreferredSize().getHeight());
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
				frame.setTitle("T2 Pre-processing Help");

				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			}
		});

		chckbxParcellationAndSegmentation = new JCheckBox("Parcellation and segmentation");
		chckbxParcellationAndSegmentation.setBounds(6, 90 + 2 * GUIUtilities.increaseByHeight(dataIndex, heightDifference), 260, 50);
		getContentPane().add(chckbxParcellationAndSegmentation);
		chckbxParcellationAndSegmentation.setToolTipText(parcellationToolTip);

		JLabel lblNumberOfConcurrentProcess = new JLabel("Number of Concurrent Process");
		lblNumberOfConcurrentProcess.setBounds(8, 140 + 2 * GUIUtilities.increaseByHeight(dataIndex, heightDifference), 200, 25);
		lblNumberOfConcurrentProcess.setToolTipText(processToolTip);
		getContentPane().add(lblNumberOfConcurrentProcess);

		textField_numberOfConcurrentProcess = new JTextField();
		textField_numberOfConcurrentProcess.setColumns(5);
		textField_numberOfConcurrentProcess.setBounds(206, 140 + 2 * GUIUtilities.increaseByHeight(dataIndex, heightDifference), 40, 27);
		textField_numberOfConcurrentProcess.setText("4");
		getContentPane().add(textField_numberOfConcurrentProcess);
		textField_numberOfConcurrentProcess.setToolTipText(processToolTip);

		JLabel lblNumberOfDatasets = new JLabel("Number of datasets");
		lblNumberOfDatasets.setBounds(6, 12, 151, 15);
		lblNumberOfDatasets.setToolTipText(dataToolTip);
		getContentPane().add(lblNumberOfDatasets);

		final JSlider dataSlider = new JSlider();
		dataSlider.setMinimum(1);
		dataSlider.setMaximum(100);
		dataSlider.setBounds(154, 12, 200, 16);
		dataSlider.setValue(dataIndex + 1);
		dataSlider.setToolTipText(dataToolTip);
		getContentPane().add(dataSlider);
		dataSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				textField_inputCount.setText(String.valueOf(dataSlider.getValue()));
			}

		});

		textField_inputCount = new JTextField();
		textField_inputCount.setBounds(362, 8, 50, 27);
		getContentPane().add(textField_inputCount);
		textField_inputCount.setColumns(10);
		textField_inputCount.setText(String.valueOf(dataSlider.getValue()));
		textField_inputCount.setToolTipText(dataToolTip);
		textField_inputCount.setInputVerifier(new InputVerifier() {

			@Override
			public boolean verify(JComponent input) {
				JTextField tf = (JTextField) input;
				if (tf.getText().isEmpty()) {
					tf.setText(String.valueOf(dataSlider.getValue()));
					return true;
				}
				try {
					Integer.parseInt(tf.getText());
					return true;
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, "Please type an integer, thanks!");
					return false;
				}
			}

		});
		textField_inputCount.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent ke) {
				String typed = textField_inputCount.getText();
				try {
					int x = Integer.parseInt(typed);
					if (!typed.matches("\\d+") || typed.length() > 3 || x > 10 || x < 1) {
						return;
					}
					int value = Integer.parseInt(typed);
					dataSlider.setValue(value);
				} catch (NumberFormatException e) {

				}

			}

		});

		JButton lblquestion2 = new JButton(icon);
		lblquestion2.setBounds(420, 8, 22, 22);
		lblquestion2.setHorizontalAlignment(SwingConstants.CENTER);
		lblquestion2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				JApplet applet = new JApplet() {
					@Override
					public void init() {
						GUIUtilities.initLookAndFeel("Nimbus");
						Container content = getContentPane();
						content.setLayout(new GridBagLayout());
						GridBagConstraints c = new GridBagConstraints();
						JLabel line1 = new JLabel("Type in any number of data to process in the given text field");
						GUIUtilities.setFont(line1);
						c.gridx = 0;
						c.gridy = 0;
						content.add(line1, c);
						JLabel line2 =
								new JLabel("Alternatively, use the slider for convinience, but note that"
										+ " there's no restriction on maximum number of data");
						GUIUtilities.setFont(line2);
						c.gridy = 1;
						content.add(line2, c);
						JLabel line3 = new JLabel("Click go to proceed to increasing/decreasing the total number of data");
						c.gridy = 2;
						GUIUtilities.setFont(line3);
						content.add(line3, c);

					}
				};
				applet.init();
				frame.setContentPane(applet.getContentPane());
				frame.setBounds(getX(), getY(), 50 + (int) frame.getPreferredSize().getWidth(), 50 + (int) frame.getPreferredSize().getHeight());
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
				frame.setTitle("T2 Pre-processing Help");

				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			}
		});
		getContentPane().add(lblquestion2);
		lblquestion2.setToolTipText(questionToolTip);

		JButton btnGo2 = new JButton("Go");
		btnGo2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dataIndex = Integer.parseInt(textField_inputCount.getText()) - 1;

				f.setSize(520, 235 + 2 * GUIUtilities.increaseByHeight(dataIndex, heightDifference));

				for (int i = 0; i < textFields.size(); i++) {
					String input = textFields.get(i).getA().getText();
					String age = textFields.get(i).getB().getText();
					init(inputTextFields, input, i);
					init(ageTextFields, age, i);
				}
				applet = new T2PreprocessingManualChooser(dataIndex, inputTextFields, ageTextFields);

				applet.init();
				f.setContentPane(applet.getContentPane());

			}

			private void init(List<String> inputTextFields, String input, int index) {
				if (index < inputTextFields.size()) {
					inputTextFields.set(index, input);
				} else {
					inputTextFields.add(input);
				}
			}
		});
		btnGo2.setToolTipText(go1ToolTip);
		btnGo2.setBounds(451, 7, 50, 25);
		getContentPane().add(btnGo2);

	}

	private void initInputDirectory(int index) {
		for (int i = 0; i <= index; i++) {
			final int x = i;
			JLabel lblInputDirectory = new JLabel("Input data " + (i + 1));
			lblInputDirectory.setBounds(6, 7 + 2 * i * heightDifference, 105, 15);
			lblInputDirectory.setToolTipText(inputToolTip);
			panel.add(lblInputDirectory);

			textFields.add(new Triple<JTextField, JTextField, JSlider>(new JTextField(), new JTextField(), new JSlider()));
			final JTextField textField_inputDirectory = textFields.get(i).getA();
			textField_inputDirectory.setToolTipText(inputToolTip);
			textField_inputDirectory.setBounds(108, 2 + 2 * i * heightDifference, 313, 27);
			panel.add(textField_inputDirectory);
			textField_inputDirectory.setColumns(10);
			if (i < inputTextFields.size()) {
				textField_inputDirectory.setText(inputTextFields.get(i));
			}
			JButton btnOpen = new JButton("Open");
			btnOpen.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					buildLoadDiag(x, textField_inputDirectory);
				}
			});

			btnOpen.setBounds(427, 2 + 2 * i * heightDifference, 62, 25);
			panel.add(btnOpen);
			btnOpen.setToolTipText(openToolTip);

			JLabel lblAgeAtScan = new JLabel("Age at scan (weeks)");
			lblAgeAtScan.setBounds(6, 9 + (2 * i + 1) * heightDifference, 150, 15);
			lblAgeAtScan.setToolTipText(ageToolTip);
			panel.add(lblAgeAtScan);

			final JTextField textField_age = textFields.get(i).getB();
			textField_age.setToolTipText(ageToolTip);
			final JSlider ageSlider = textFields.get(i).getC();
			ageSlider.setToolTipText(ageToolTip);
			ageSlider.setBounds(150, 9 + (2 * i + 1) * heightDifference, 214, 15);
			ageSlider.setMaximum(44);
			ageSlider.setMinimum(28);
			ageSlider.setValue(36);
			ageSlider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					textField_age.setText(String.valueOf(ageSlider.getValue()));
				}

			});
			panel.add(ageSlider);
			textField_age.setBounds(381, 2 + (2 * i + 1) * heightDifference, 40, 27);
			panel.add(textField_age);
			textField_age.setColumns(10);
			textField_age.setText(String.valueOf(ageSlider.getValue()));

			textField_age.setInputVerifier(new InputVerifier() {

				@Override
				public boolean verify(JComponent input) {
					JTextField tf = (JTextField) input;
					if (tf.getText().isEmpty()) {
						tf.setText(String.valueOf(ageSlider.getValue()));
						return true;
					}
					try {
						double x = Double.parseDouble(tf.getText());
						if (GUIUtilities.isDouble(x)) {
							JOptionPane.showMessageDialog(errors, "Non-integer value " + x + " will be rounded to " + checkValue(x) + " when start");

						} else if (x > 44) {
							JOptionPane.showMessageDialog(errors, x + " will default to 44 when start");
						} else if (x < 28) {
							JOptionPane.showMessageDialog(errors, x + " will default to 28 when start");
						}
						return true;
					} catch (NumberFormatException e) {
						JOptionPane.showMessageDialog(errors, "Please type an integer between 28 to 44, thanks!");
						return false;
					}
				}

				private <T extends Number> String checkValue(T x) {
					if (x.doubleValue() < 28) {
						return "28";
					} else if (x.doubleValue() > 44) {
						return "44";
					}
					return String.valueOf((int) Math.round(x.doubleValue()));
				}

			});
			textField_age.addKeyListener(new KeyAdapter() {

				@Override
				public void keyReleased(KeyEvent ke) {
					String typed = textField_age.getText();
					if (ke.getKeyCode() == KeyEvent.VK_BACK_SPACE || !typed.matches("\\d+") || typed.length() > 3 || typed.length() == 1) {
						return;
					}
					int value = Integer.parseInt(typed);
					ageSlider.setValue(value);
				}

			});
			if (i < ageTextFields.size()) {
				final int y = i;
				final String ageText = ageTextFields.get(i);
				double age = 36;
				try {
					age = Double.parseDouble(ageText);
					if (age < 28) {
						age = 28;
					} else if (age > 44) {
						age = 44;
					}
					if (GUIUtilities.isDouble(age)) {
						age = Math.round(age);
					}
				} catch (NumberFormatException e1) {
					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
							JOptionPane.showMessageDialog(null, ageText + " at " + "line " + (y + 1) + " is not a number");
						}

					});
					t.start();
				}
				textField_age.setText(String.valueOf((int) age));
				ageSlider.setValue((int) age);
			}

		}

	}

	private void buildLoadDiag(int index, JTextField input) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileSelectionMode(JFileChooser.OPEN_DIALOG);

		int returnVal = fileChooser.showOpenDialog(new JFrame("load"));

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			// This is where a real application would open the file.
			// JOptionPane.showMessageDialog(frame,"Opening: " +
			// file.getName() + ".");
			String filePath = file.getPath();
			input.setText(filePath);
		} else {
			// JOptionPane.showMessageDialog(frame,"Open command cancelled by user.");
		}
	}

	private ImageIcon initIcon() {
		Icon icn = UIManager.getIcon("OptionPane.questionIcon");

		BufferedImage image = new BufferedImage(icn.getIconWidth(), icn.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

		icn.paintIcon(null, image.getGraphics(), 0, 0);

		return new ImageIcon(image.getScaledInstance(23, 23, Image.SCALE_SMOOTH));

	}

	public static void main(String[] args) {
		f = new JFrame();
		if (args.length == 0) {
			applet = new T2PreprocessingManualChooser();
		} else {
			List<String> textField = new LinkedList<String>();
			for (int i = 0; i < args.length - 1; i++) {
				textField.add(args[i]);
			}
			applet = new T2PreprocessingManualChooser(textField.size() - 1, textField, args[args.length - 1]);
		}

		applet.init();

		f.setContentPane(applet.getContentPane());
		f.setBounds(920, 100, 520, 235 + (dataIndex <= 9 ? (2 * dataIndex * heightDifference) : (2 * 9 * heightDifference)));
		f.setTitle("T2 Pre-processing(Manual)");
		f.setVisible(true);

		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}
}

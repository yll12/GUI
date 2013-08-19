package utils;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import sun.java2d.xr.MutableInteger;

public class GUIUtilities {

	public static DirectoryStream<?> newDirectoryStream(Path dir, String glob) throws IOException {
		FileSystem fs = dir.getFileSystem();
		final PathMatcher matcher = fs.getPathMatcher("glob:" + glob);
		DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
			@Override
			public boolean accept(Path entry) throws IOException {
				return matcher.matches(entry.getFileName());
			}
		};
		return fs.provider().newDirectoryStream(dir, filter);
	}

	/**
	 * @param workdir
	 *            working directory to iterate through
	 * @param pattern
	 *            pattern to look for
	 * @return a list of files that matches the pattern in the working directory
	 */
	@SuppressWarnings("unchecked")
	public static List<String> searchFile(String workdir, String pattern) {
		List<String> result = new LinkedList<String>();
		Path folderToIterate = FileSystems.getDefault().getPath(workdir);
		try {
			DirectoryStream<Path> ds = (DirectoryStream<Path>) newDirectoryStream(folderToIterate, pattern);
			for (Path p : ds) {
				result.add(p.getParent().toString() + "/" + p.getFileName().toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @param workdir
	 *            working directory to iterate through
	 * @param pattern
	 *            pattern to look for
	 * @return a list of files that matches the pattern in the working directory
	 */
	@SuppressWarnings("unchecked")
	public static List<String> searchFileWithoutFullPath(String workdir, String pattern) {
		List<String> result = new LinkedList<String>();
		Path folderToIterate = FileSystems.getDefault().getPath(workdir);
		try {
			DirectoryStream<Path> ds = (DirectoryStream<Path>) newDirectoryStream(folderToIterate, pattern);
			for (Path p : ds) {
				result.add(p.getFileName().toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static List<String> searchAllFile(String workdir, String pattern) {
		List<String> result = new LinkedList<String>();
		File parent = new File(workdir);
		File[] files = parent.listFiles();
		if (files != null) {
			for (File subFolder : files) {
				if (subFolder.isDirectory()) {
					result.addAll(searchAllFile(subFolder.getAbsolutePath(), pattern));
				} else {
					PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
					if (matcher.matches(subFolder.toPath().getFileName())) {
						result.add(subFolder.getAbsolutePath());
					}
				}
			}
		}
		return result;

	}

	/**
	 * @param workdir
	 *            working directory to iterate through
	 * @param pattern
	 *            pattern to look for
	 * @return a list of files that matches the pattern in the working directory
	 */
	@SuppressWarnings("unchecked")
	public static void deleteFile(String workdir, String pattern) {
		Path folderToIterate = FileSystems.getDefault().getPath(workdir);
		try {
			DirectoryStream<Path> ds = (DirectoryStream<Path>) newDirectoryStream(folderToIterate, pattern);
			for (Path p : ds) {
				p.toFile().delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param given
	 *            Example : /staff/yl13/Data.nii.gz
	 * @return /staff/yl13
	 */
	public static String getWorkingDirectory(String given) {
		if (given == null) {
			JOptionPane.showMessageDialog(null, "Error in getting working directory!");
		}
		return given.substring(0, given.lastIndexOf("/"));
	}

	/**
	 * @param given
	 *            Example : /staff/yl13/Data.nii.gz
	 * @return Data.nii.gz
	 */
	public static String getFileName(String inputData) {
		assert inputData != null;
		return inputData.substring(inputData.lastIndexOf("/") + 1);
	}

	/**
	 * @param input
	 *            input to check (Example: data.nii.gz)
	 * @param expectedExtension
	 *            (Example: .nii.gz)
	 * @return true if and only if expectedExtension occurs at the end of input
	 */
	public static boolean checkInput(final String input, String expectedExtension) {
		assert input != null & expectedExtension != null;
		String test = input.trim();
		return test.substring(test.length() - expectedExtension.length()).equals(expectedExtension);
	}

	public static void initLookAndFeel(String look) {
		for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			if (look.equals(info.getName())) {
				try {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
		}
	}

	public static int increaseByHeight(int index, int heightDifference) {
		return heightDifference * (index <= 9 ? index : 9);
	}

	public static int getNumberOfConcurrentProcess(int maximum, int preferred) {
		return preferred < maximum ? preferred : maximum;
	}

	public static Thread createObserverThread(final Thread t, final String inputData) {
		return new Thread(new Runnable() {

			@Override
			public void run() {
				String workdir = getWorkingDirectory(inputData);
				while (t.isAlive() || t.getState() != Thread.State.TERMINATED) {
					if (hasFinished(inputData, workdir, "_error") || hasFinished(inputData, workdir, "_success")) {
						return;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		});

	}

	public static boolean hasFinished(final String inputData, String workdir, String pattern) {
		List<String> list = GUIUtilities.searchAllFile(workdir, getFileName(inputData) + pattern);
		if (!list.isEmpty()) {
			deleteFile(workdir, getFileName(list.get(0)));
			return true;
		}
		return false;
	}

	public static void addThread(int index, Deque<Pair<String, String[]>> listOfInputs, List<Thread> listOfObserverThreads) {
		Pair<String, String[]> input = listOfInputs.removeFirst();
		final Thread execute = GUIUtilities.createExecutingThread(input.getB());
		execute.start();
		Thread observer = GUIUtilities.createObserverThread(execute, input.getA());
		observer.start();
		listOfObserverThreads.remove(index);
		listOfObserverThreads.add(index, observer);
	}

	public static Thread createExecutingThread(final String[] cmdArray) {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				ProcessBuilder pb = new ProcessBuilder(cmdArray);

				pb.redirectErrorStream(true);
				Process p = null;
				try {
					p = pb.start();
					p.waitFor();
					InputStreamReader isr = new InputStreamReader(p.getInputStream());
					BufferedReader br = new BufferedReader(isr);

					while (br.readLine() != null) {
					}
					return;
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					if (p != null) {
						try {
							p.getOutputStream().close();
							p.getInputStream().close();
							p.getErrorStream().close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			}
		});
	}

	public static void populateInputs(Deque<Pair<String, String[]>> list, String inputData, String[] args, String scriptName) {
		String[] cmdArray = createCmdArray(args, scriptName);
		list.add(new Pair<String, String[]>(inputData, cmdArray));
	}

	public static String[] createCmdArray(String[] args, String scriptName) {
		String script = "'cd ../ScriptsRunByGUI; " + scriptName;
		for (int i = 0; i < args.length; i++) {
			script += args[i];
			if (i < args.length - 1) {
				script += " ";
			} else {
				script += ";'";
			}
		}
		String[] cmdArray = { "gnome-terminal", "--disable-factory", "-e", "bash -c " + script };
		return cmdArray;
	}

	public static boolean isInputValid(final int x, String inputData) {
		if (inputData.trim().isEmpty()) {

			return false;
		}

		if (!GUIUtilities.checkInput(inputData, ".nii.gz") && !GUIUtilities.checkInput(inputData, "nii")) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(null, "Input " + (x + 1) + ": " + "this is not an image file!");
				}
			});
			t.start();

			return false;
		}
		return true;
	}

	public static void rearrangeList(List<String> inputTextFields) {
		Collections.sort(inputTextFields, new Comparator<String>() {

			private final boolean isDigit(char ch) {
				return ch >= 48 && ch <= 57;
			}

			/**
			 * Length of string is passed in for improved efficiency (only need
			 * to calculate it once)
			 **/
			private final String getChunk(String s, int slength, int marker) {
				StringBuilder chunk = new StringBuilder();
				char c = s.charAt(marker);
				chunk.append(c);
				marker++;
				if (isDigit(c)) {
					while (marker < slength) {
						c = s.charAt(marker);
						if (!isDigit(c))
							break;
						chunk.append(c);
						marker++;
					}
				} else {
					while (marker < slength) {
						c = s.charAt(marker);
						if (isDigit(c))
							break;
						chunk.append(c);
						marker++;
					}
				}
				return chunk.toString();
			}

			@Override
			public int compare(String o1, String o2) {
				String s1 = o1;
				String s2 = o2;

				int thisMarker = 0;
				int thatMarker = 0;
				int s1Length = s1.length();
				int s2Length = s2.length();

				while (thisMarker < s1Length && thatMarker < s2Length) {
					String thisChunk = getChunk(s1, s1Length, thisMarker);
					thisMarker += thisChunk.length();

					String thatChunk = getChunk(s2, s2Length, thatMarker);
					thatMarker += thatChunk.length();

					// If both chunks contain numeric characters, sort them
					// numerically
					int result = 0;
					if (isDigit(thisChunk.charAt(0)) && isDigit(thatChunk.charAt(0))) {
						try {
							int i1 = Integer.parseInt(thisChunk);
							int i2 = Integer.parseInt(thatChunk);
							result = i1 - i2;
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					} else {
						result = thisChunk.compareTo(thatChunk);
					}

					if (result != 0)
						return result;
				}

				return s1Length - s2Length;
			}

		});
	}

	public static boolean isDouble(double x) {
		return x != Math.floor(x);
	}

	public static void setFont(JLabel label) {
		label.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
	}

	public static void initializeConstraints(GridBagConstraints c) {
		c.weighty = 1;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 0;
	}

	public static void createLine(MutableInteger lineNumber, Container content, GridBagConstraints c, String labelText) {
		JLabel line = new JLabel(labelText);
		GUIUtilities.setFont(line);
		c.gridy = lineNumber.getValue();
		lineNumber.setValue(lineNumber.getValue() + 1);
		content.add(line, c);
	}

	public static String checkAge(String age) {
		double ageAtScan = 0;
		try {
			ageAtScan = Double.parseDouble(age);
			if (ageAtScan > 44) {
				return "44";
			} else if (ageAtScan < 28) {
				return "28";
			}
			if (isDouble(ageAtScan)) {
				return String.valueOf((int) Math.round(ageAtScan));
			}
			if (age.contains(".")) {
				return age.substring(0, age.indexOf("."));
			}
		} catch (NumberFormatException e) {
			System.out.println("Should be impossible");
		}
		return age;
	}

	/**
	 * Removes from end of the line until T2 is found
	 * 
	 * @param inputData
	 *            : Example : /staff/yl13/NNU996/NNU996_T2.nii.gz
	 * @return /staff/yl13/NNU996/NNU996_
	 */
	public static String extractSubj(String inputData) {
		return inputData.substring(0, inputData.lastIndexOf("T2"));
	}

	/**
	 * @param inputData
	 * @return True if and only if subject folder is same as filename
	 * 
	 *         Example: /staff/yl13/NNU996/NNU996_T2.nii.gz returns true
	 *         /staff/yl13/NNU996/NNU123_T2.nii.gz returns false
	 */
	public static boolean isInputForT2Valid(String inputData) {
		String subjectFolder = getFileName(getWorkingDirectory(inputData));
		String fileName = getFileName(inputData);
		return fileName.contains(subjectFolder);
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Testing utilities..");
		String x = "/staff/yl13/Testing.zip";
		String y = "/staff/yl13/TestData/Test4/unreg-data.nii.gz";
		testMethod(x);
		testMethod(y);
		String inputDir = "/staff/yl13/TestDataForT2";
		String pattern = "*T2.*";
		List<String> inputs = GUIUtilities.searchAllFile(inputDir, pattern);
		System.out.println(inputs);
		rearrangeList(inputs);
		System.out.println(inputs);
		String inputData = "/staff/yl13/TestDataForT2/ABC10000/unreg-data.nii.gz";
		inputDir = "/staff/yl13/TestDataForT2/ABC1000/";
		pattern = "unreg-data.nii.gz_error";
		List<String> errors = GUIUtilities.searchAllFile(inputDir, pattern);
		System.out.println(errors);
		hasFinished(inputData, inputDir, "_error");
		System.out.println(GUIUtilities.searchAllFile(inputDir, pattern));
		String inputData2 = "/staff/yl13/TestDataForT2/NNU001/unreg-data.nii.gz";
		inputDir = "/staff/yl13/TestDataForT2/NNU001";
		pattern = "unreg-data.nii.gz_success";
		List<String> success = GUIUtilities.searchAllFile(inputDir, pattern);
		System.out.println(success);
		hasFinished(inputData2, inputDir, "_success");
		System.out.println(GUIUtilities.searchAllFile(inputDir, pattern));
		List<String> list = GUIUtilities.searchAllFile("/staff/yl13/TestDataForT2/NNU001", getFileName("NNU001_T2.nii.gz") + "_success");
		System.out.println(list);
		hasFinished("NNU001_T2.nii.gz", "/staff/yl13/TestDataForT2/NNU001", "_success");
		System.out.println(GUIUtilities.searchAllFile("/staff/yl13/TestDataForT2/NNU001", getFileName("NNU001_T2.nii.gz") + "_success"));
		String testinputData = "/staff/yl13/FULLTEST/NNU996/connectivity.nii.gz";
		System.out.println("Scan = " + getWorkingDirectory(testinputData));
		System.out.println("Subj = " + getFileName(getWorkingDirectory(testinputData)));
		System.out.println("Dir_path = " + getWorkingDirectory(getWorkingDirectory(testinputData)));
		testinputData = "/staff/yl13/FULLTEST/NNU996/NNU996_T2.nii.gz";
		System.out.println("Extract Subj for T2 = " + extractSubj(testinputData));
		testCheckT2Input(testinputData);
		testinputData = "/staff/yl13/FULLTEST/NNU996/NNU123_T2.nii.gz";
		testCheckT2Input(testinputData);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String age = null;
		while ((age = br.readLine()) != null) {
			try {
				double ageAtScan = Double.parseDouble(age);
				System.out.println(ageAtScan);
				age = checkAge(age);
				System.out.println("Final result is " + age);
			} catch (NumberFormatException e) {
				System.out.println("I wasn't expecting that!");
			}
		}

	}

	private static void testCheckT2Input(String testinputData) {
		if (isInputForT2Valid(testinputData)) {
			System.out.println("Passed the test");
		} else {
			System.out.println("Failed the test");
		}
	}

	private static void testMethod(String x) {
		System.out.println("Input is: " + x);
		System.out.println("Work directory is: " + getWorkingDirectory(x));
		System.out.println("File name is: " + getFileName(x));
		System.out.println("Error file name is: " + getFileName(x) + "_error");
		System.out.println(checkInput(x, ".zip") ? "Contains the extension" : "Does not contains the extension");
	}
}
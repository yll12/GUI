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
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
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

	public static List<String> searchAllFile(String workdir, String pattern) {
		List<String> result = new LinkedList<String>();
		File parent = new File(workdir);
		for (File subFolder : parent.listFiles()) {
			if (subFolder.isDirectory()) {
				result.addAll(searchAllFile(subFolder.getAbsolutePath(), pattern));
			} else {
				PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
				if (matcher.matches(subFolder.toPath().getFileName())) {
					result.add(subFolder.getAbsolutePath());
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
		assert given != null;
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

	private static boolean hasFinished(final String inputData, String workdir, String pattern) {
		List<String> list = GUIUtilities.searchFile(workdir, getFileName(inputData) + pattern);
		if (!list.isEmpty()) {
			deleteFile(workdir, list.get(0));
			return true;
		}
		return false;
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

	private static void testMethod(String x) {
		System.out.println("Input is: " + x);
		System.out.println("Work directory is: " + getWorkingDirectory(x));
		System.out.println("File name is: " + getFileName(x));
		System.out.println("Error file name is: " + getFileName(x) + "_error");
		System.out.println(checkInput(x, ".zip") ? "Contains the extension" : "Does not contains the extension");
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

	public static void main(String[] args) {
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

}

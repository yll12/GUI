package utils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.LinkedList;
import java.util.List;

import javax.swing.UIManager;

public class GUIUtilities {

	public static DirectoryStream<?> newDirectoryStream(Path dir, String glob)
			throws IOException {
		FileSystem fs = dir.getFileSystem();
		final PathMatcher matcher = fs.getPathMatcher("glob:" + glob);
		DirectoryStream.Filter<Path> filter =
				new DirectoryStream.Filter<Path>() {
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
			DirectoryStream<Path> ds =
					(DirectoryStream<Path>) newDirectoryStream(folderToIterate,
							pattern);
			for (Path p : ds) {
				result.add(p.getFileName().toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
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
	 * @param input
	 *            input to check (Example: data.nii.gz)
	 * @param expectedExtension
	 *            (Example: .nii.gz)
	 * @return true if and only if expectedExtension occurs at the end of input
	 */
	public static boolean checkInput(final String input,
			String expectedExtension) {
		assert input != null & expectedExtension != null;
		String test = input.trim();
		return test.substring(test.length() - expectedExtension.length())
				.equals(expectedExtension);
	}

	public static void main(String[] args) {
		System.out.println("Testing utilities..");
		String x = "/staff/yl13/Testing.zip";
		System.out.println("Input is: " + x);
		System.out.println("Work directory is: " + getWorkingDirectory(x));
		System.out.println(checkInput(x, ".zip") ? "Contains the extension"
				: "Does not contains the extension");
	}

	public static void initLookAndFeel(String look) {
		for (UIManager.LookAndFeelInfo info : UIManager
				.getInstalledLookAndFeels()) {
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
}

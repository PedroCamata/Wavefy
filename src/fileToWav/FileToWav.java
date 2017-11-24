package fileToWav;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class FileToWav {

	// All metadata concat
	byte[] header;

	// Riff
	byte[] riff = "RIFF".getBytes();
	byte[] totalSize = { 0, 0, 0, 0 };
	byte[] format = "WAVE".getBytes();
	// 12 bytes

	// Chunk
	byte[] chunck = "fmt ".getBytes();
	byte[] chunkSize = { 16, 0, 0, 0 };
	byte[] AudioFormat = { 1, 0 };
	byte[] numChannel = longToByteArray(11, 2, false); // Max: 11, Change music time
	byte[] sampleRate = longToByteArray(2304000, 4, false); // Change music time
	byte[] byteRate = longToByteArray(16, 4, false);
	byte[] blockAlign = longToByteArray(4, 2, false);
	byte[] bitsPerSample = longToByteArray(32, 2, false);
	// 24 bytes

	// Data
	byte[] data = "data".getBytes();
	byte[] dataSize = { 0, 0, 0, 0 };
	// 8 bytes

	// Extension - Not from original wave file
	byte[] extension = { 0, 0, 0, 0, 0, 0, 0, 0 };
	// 8 bytes

	// Constants
	final int METADATA_SIZE = riff.length + totalSize.length + format.length + chunck.length + chunkSize.length
			+ AudioFormat.length + numChannel.length + sampleRate.length + byteRate.length + blockAlign.length
			+ bitsPerSample.length + data.length + dataSize.length + extension.length; // 52 bytes
	final int BUFFER_SIZE = 8388608; // 8 Megabytes

	public String convertToAudio(String URL) {
		try {
			File file = new File(URL);
			if (isWaveFile(file.getName())) {
				return "ERROR: File is a .wav file";
			}

			if (!metaBuilder(file)) {
				return "ERROR: Size is too big, maximum size is 4.294.967.243 bytes( 4,2 gb )";
			}

			InputStream in = new FileInputStream(file);
			String filePath = file.getParentFile() + "\\" + getFileNameWithoutExtension(file.getName()) + ".wav";
			FileOutputStream out = new FileOutputStream(filePath);

			byte[] buffer = new byte[BUFFER_SIZE];
			int buffer_size;

			// Write metadata
			out.write(header);

			while ((buffer_size = in.read(buffer)) != -1) {
				// if buffer isn't all full, resize to the right size
				if (buffer_size != BUFFER_SIZE) {
					buffer = Arrays.copyOfRange(buffer, 0, buffer_size);
				}

				// Write file
				out.write(buffer);
			}
			in.close();
			out.close();
			return "SUCCESS: Converted file to .wav";

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "ERROR: Something isn't right";
	}

	public String convertToFile(String URL) {
		try {
			File file = new File(URL);
			if (!isWaveFile(file.getName())) {
				return "ERROR: File isn't a .wav file";
			}

			InputStream in = new FileInputStream(file);
			byte[] buffer = new byte[BUFFER_SIZE];
			int buffer_size;

			byte[] metadata_block = new byte[METADATA_SIZE];

			// Get extension
			in.read(metadata_block);
			byte[] metadata_extension = Arrays.copyOfRange(metadata_block, METADATA_SIZE - 8, METADATA_SIZE);

			String stringExtension = "";
			for (int i = 0; i < metadata_extension.length; i++) {
				if (metadata_extension[i] != 0) {
					stringExtension += (char) metadata_extension[i];
				}
			}

			String fileName = getFileNameWithoutExtension(file.getPath()) + "(Generated)." + stringExtension;
			FileOutputStream out = new FileOutputStream(fileName);

			while ((buffer_size = in.read(buffer)) != -1) {
				// if buffer isn't all full, resize to the right size
				if (buffer_size != BUFFER_SIZE) {
					buffer = Arrays.copyOfRange(buffer, 0, buffer_size);
				}

				// Write file
				out.write(buffer);
			}
			in.close();
			out.close();
			return "SUCCESS: Converted .wav to file";

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "ERROR: Something isn't right";
	}

	public boolean metaBuilder(File file) {
		byte[] bytesExtension = getFileExtension(file.getName()).getBytes();
		for (int i = 0; i < bytesExtension.length; i++) {
			extension[i] = bytesExtension[i];
		}

		dataSize = longToByteArray(file.length(), 4, false);
		totalSize = longToByteArray(file.length() + METADATA_SIZE, 4, false); // 44 bytes - Original Wave, 52 bytes -
																				// Generated file

		if (totalSize == null) {
			return false;
		}

		header = arrayConcat(riff, totalSize, format, chunck, chunkSize, AudioFormat, numChannel, sampleRate, byteRate,
				blockAlign, bitsPerSample, data, dataSize, extension);

		return true;
	}

	public static boolean isWaveFile(String fileName) {
		if (getFileExtension(fileName).equals("wav")) {
			return true;
		}
		return false;
	}

	private byte[] longToByteArray(long n, int length, boolean isBigEnding) {
		String hex = Long.toHexString(n);

		// if hex number is bigger than the space
		if (hex.length() > length * 2) {
			return null;
		}

		// if hex number use less space, complete the space
		while (hex.length() < length * 2) {
			hex = "0" + hex;
		}

		if (isBigEnding) {
			return hexStringToByteArray(hex);
		}

		byte[] byteArray = hexStringToByteArray(hex);

		for (int i = 0; i < byteArray.length / 2; i++) {
			byte tempByte = byteArray[i];
			byteArray[i] = byteArray[byteArray.length - i - 1];
			byteArray[byteArray.length - i - 1] = tempByte;
		}
		return byteArray;

	}

	private byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	private byte[] arrayConcat(byte[]... arrays) {
		int length = 0;
		for (byte[] array : arrays) {
			length += array.length;
		}
		byte[] ret = new byte[length];
		byte destPos = 0;
		for (byte[] array : arrays) {
			System.arraycopy(array, 0, ret, destPos, array.length);
			destPos += array.length;
		}
		return ret;
	}

	private static String getFileExtension(String fileName) {
		try {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		} catch (Exception e) {
			return "";
		}
	}

	private static String getFileNameWithoutExtension(String fileName) {
		try {
			return fileName.substring(0, fileName.lastIndexOf("."));
		} catch (Exception e) {
			return fileName;
		}
	}
}

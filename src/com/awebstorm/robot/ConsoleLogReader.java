package com.awebstorm.robot;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

import org.apache.log4j.Logger;

public class ConsoleLogReader {
	
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private BufferedReader results;
	private long fileStart;
	private long fileEnd;
	private String filename;
	private LinkedList<ALine> myLines = new LinkedList<ALine>();
	private boolean consoleLogHasNoErrors;
	
	public class ALine {
		private String stepNum;
		private String reportTime;
		private String replyTime;
		private String timeStarted;
		private String timeEnded;
		private String timeResponded;
		private String timeLength;
		private String bodyBytes;
		private String receiveBytes;
		private String sentBytes;
		private String throughput;
		private String stepStatus;
		private String jobID;
		
		public String getStepNum() {
			return stepNum;
		}
		public void setStepNum(String stepNum) {
			this.stepNum = stepNum;
		}
		public String getReportTime() {
			return reportTime;
		}
		public void setReportTime(String reportTime) {
			this.reportTime = reportTime;
		}
		public String getReplyTime() {
			return replyTime;
		}
		public void setReplyTime(String replyTime) {
			this.replyTime = replyTime;
		}
		public String getTimeStarted() {
			return timeStarted;
		}
		public void setTimeStarted(String timeStarted) {
			this.timeStarted = timeStarted;
		}
		public String getTimeEnded() {
			return timeEnded;
		}
		public void setTimeEnded(String timeEnded) {
			this.timeEnded = timeEnded;
		}
		public String getTimeResponded() {
			return timeResponded;
		}
		public void setTimeResponded(String timeResponded) {
			this.timeResponded = timeResponded;
		}
		public String getTimeLength() {
			return timeLength;
		}
		public void setTimeLength(String timeLength) {
			this.timeLength = timeLength;
		}
		public String getBodyBytes() {
			return bodyBytes;
		}
		public void setBodyBytes(String bodyBytes) {
			this.bodyBytes = bodyBytes;
		}
		public String getReceiveBytes() {
			return receiveBytes;
		}
		public void setReceiveBytes(String receiveBytes) {
			this.receiveBytes = receiveBytes;
		}
		public String getSentBytes() {
			return sentBytes;
		}
		public void setSentBytes(String sentBytes) {
			this.sentBytes = sentBytes;
		}
		public String getThroughput() {
			return throughput;
		}
		public void setThroughput(String throughput) {
			this.throughput = throughput;
		}
		public String getStepStatus() {
			return stepStatus;
		}
		public void setStepStatus(String stepStatus) {
			this.stepStatus = stepStatus;
		}
		public String getJobID() {
			return jobID;
		}
		public void setJobID(String jobID) {
			this.jobID = jobID;
		}
	}
	
	/**
	 * Retrieves the lines that were collected from the console.log between the two points if any
	 * @return A LinkedList<ALine> containing the values of the special steps that
	 * should be cross-referenced.
	 */
	public LinkedList<ALine> getMyLines() {
		return myLines;
	}
	
	/**
	 * Creates a new ConsoleLogReader and reads and parses the file a line at a time.
	 * @param filename Name of the file to parse
	 */
	public ConsoleLogReader(String filename, long fileStart, long fileEnd) {
		this.filename = filename;
		this.fileStart = fileStart;
		this.fileEnd = fileEnd;
		byte[] b = new byte[(int)(fileEnd-fileStart)];
		consoleLogHasNoErrors = true;
		try {
			results = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			FileInputStream results2 = new FileInputStream(filename);
			results2.read(b, (int)(fileStart+1), (int)(fileEnd-fileStart));
			results = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(b)));
			
		} catch (FileNotFoundException e) {
			consoleLog.error("Could not find a consoleLog.", e);
			consoleLogHasNoErrors = false;
		} catch (IOException e) {
			consoleLog.error("IO error on consoleLog.", e);
			consoleLogHasNoErrors = false;
		}
		try {
			String newLine = results.readLine();
			while (newLine != null) {
				consoleLogHasNoErrors = consoleLogHasNoErrors && parseALine(newLine);
				newLine = results.readLine();
			}
		} catch (IOException e) {
			consoleLog.error("Could not read a line from the file.", e);
		}
		try {
			results.close();
			deleteFile(filename);
		} catch (IOException e) {
			consoleLog.error("Could not close the stream to the console.log during parsing.", e);
		}
	}
	
	/**
	 * Parses a line of text into values.
	 * @param line A line of text without a line termination character included
	 * @return This line indicates an error
	 */
	public boolean parseALine(String line) {
		//Remove the leading dots, they indicate tests started
		int i = 0;
		while (line != null) {
			char someChar = line.charAt(i);
			if (someChar == '.') {
				i++;
				continue;
			}
		}
		//Parse a consoleLog line to throw errors when a exception was thrown to the output
		//or if the output values were bad.
		line = line.substring(i);
		char newChar;
		if (line.startsWith("[ERROR]", 0)) {
			return false;
		} else if (line.startsWith("[WARN ]", 0)) {
			return false;
		} else if (line.startsWith("[INFO ]")) {
			ALine infoLine = new ALine();
			String subline = line.substring(41);
			int j = 0;
			newChar = subline.charAt(j);
			while (newChar != ' ') {
				j++;
				newChar = subline.charAt(j);
			}
			subline = subline.substring(j+1);
			infoLine.setJobID(subline.substring(0, 4));
			subline = subline.substring(5);
			if (subline.startsWith("WAIT")) {
				if (subline.contains("success")) {
					return true;
				}
			} else if (subline.startsWith("INVOKE")) {
				subline = subline.substring(7);
				infoLine.setStepNum(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getStepNum().length() + 1);
				infoLine.setReportTime(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getReportTime().length() + 1);
				infoLine.setReplyTime(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getReplyTime().length() + 1);
				infoLine.setTimeStarted(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getTimeStarted().length() + 1);
				infoLine.setTimeEnded(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getTimeEnded().length() + 1);
				infoLine.setTimeResponded(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getTimeResponded().length() + 1);
				infoLine.setTimeLength(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getTimeLength().length() + 1);
				infoLine.setBodyBytes(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getBodyBytes().length() + 1);
				infoLine.setReceiveBytes(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getReceiveBytes().length() + 1);
				infoLine.setSentBytes(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getSentBytes().length() + 1);
				infoLine.setThroughput(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getThroughput().length() + 1);
				infoLine.setStepStatus(subline);
				if (infoLine.getStepStatus().equals("success")) {
					return true;
				}
			} else if (subline.startsWith("POST")) {
				subline = subline.substring(7);
				infoLine.setStepNum(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getStepNum().length() + 1);
				infoLine.setReportTime(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getReportTime().length() + 1);
				infoLine.setReplyTime(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getReplyTime().length() + 1);
				infoLine.setTimeStarted(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getTimeStarted().length() + 1);
				infoLine.setTimeEnded(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getTimeEnded().length() + 1);
				infoLine.setTimeResponded(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getTimeResponded().length() + 1);
				infoLine.setTimeLength(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getTimeLength().length() + 1);
				infoLine.setBodyBytes(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getBodyBytes().length() + 1);
				infoLine.setReceiveBytes(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getReceiveBytes().length() + 1);
				infoLine.setSentBytes(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getSentBytes().length() + 1);
				infoLine.setThroughput(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getThroughput().length() + 1);
				infoLine.setStepStatus(subline);
					if (infoLine.getStepStatus().equals("success")) {
						return true;
					}
			} else if (subline.startsWith("VERIFY_TITLE")) {
				if (subline.contains("success")) {
					return true;
				}
			} else {
				//TODO - Other steps that have not been implemented go here
				return true;
			}

		}
		//Random line, probably part of an stack trace
		return true;
	}

	
	/**
	 * Used to parse the part of the line that is comma delimited to pull values from the text.
	 * @param line Line of text to pull a value from
	 * @return The value pulled out
	 */
	public String parseCommaEnded(String line) {
		int k = 0;
		char someChar = line.charAt(0);
		while (someChar != ',') {
			k++;
			someChar = line.charAt(k);
		}
		return line.substring(0, k);
	}
	
	/**
	 * Deletes a file whose name was given.
	 * @param filename Name of the file to delete
	 */
	public void deleteFile(String filename) {
		(new File(filename)).delete();
	}

	public boolean isConsoleLogHasNoErrors() {
		return consoleLogHasNoErrors;
	}
	
}

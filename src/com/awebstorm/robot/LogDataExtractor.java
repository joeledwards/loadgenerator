package com.awebstorm.robot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * Contains the utility class and methods to parse the console.log file from a Robot.
 * @author Cromano
 *
 */
public class LogDataExtractor {
	
	private Logger consoleLog = Logger.getLogger(this.getClass());
	private BufferedReader results;
	private HashMap<String, ALineData> myLines = new HashMap<String, ALineData>();
	private boolean consoleLogHasNoErrors;
	
	/**
	 * Holds the data values extracted from an info line and the values getters and setters.
	 * @author Cromano
	 *
	 */
	public class ALineData {
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
		private String resultMessage;
		
		/**
		 * step number getter.
		 * @return step number
		 */
		public final String getStepNum() {
			return stepNum;
		}
		/**
		 * step number setter.
		 * @param stepNum step number
		 */
		public final void setStepNum(final String stepNum) {
			this.stepNum = stepNum;
		}
		/**
		 * time that the report was filed getter.
		 * @return report time
		 */
		public final String getReportTime() {
			return reportTime;
		}
		/**
		 * time that the report was filed setter.
		 * @param reportTime report time
		 */
		public final void setReportTime(final String reportTime) {
			this.reportTime = reportTime;
		}
		/**
		 * time it took to receive a reply form the target getter.
		 * @return reply time
		 */
		public final String getReplyTime() {
			return replyTime;
		}
		/**
		 * time it took to receive a reply form the target setter.
		 * @param replyTime reply time
		 */
		public final void setReplyTime(final String replyTime) {
			this.replyTime = replyTime;
		}
		/**
		 * time that the step started getter.
		 * @return time started
		 */
		public final String getTimeStarted() {
			return timeStarted;
		}
		/**
		 * time that the step started setter.
		 * @param timeStarted time started
		 */
		public final void setTimeStarted(final String timeStarted) {
			this.timeStarted = timeStarted;
		}
		/**
		 * time that the step ended getter.
		 * @return time ended
		 */
		public final String getTimeEnded() {
			return timeEnded;
		}
		/**
		 * time that the step ended setter.
		 * @param timeEnded time ended
		 */
		public final void setTimeEnded(final String timeEnded) {
			this.timeEnded = timeEnded;
		}
		/**
		 * time that the target responded getter.
		 * @return time responded
		 */
		public final String getTimeResponded() {
			return timeResponded;
		}
		/**
		 * time that the target responded setter.
		 * @param timeResponded time responded
		 */
		public final void setTimeResponded(final String timeResponded) {
			this.timeResponded = timeResponded;
		}
		/**
		 * The difference between the time responded and the time started getter.
		 * @return step time length
		 */
		public final String getTimeLength() {
			return timeLength;
		}
		/**
		 * The difference between the time responded and the time started setter.
		 * @param timeLength step time length
		 */
		public final void setTimeLength(final String timeLength) {
			this.timeLength = timeLength;
		}
		/**
		 * Size of a step's body getter.
		 * @return size of a step's body
		 */
		public final String getBodyBytes() {
			return bodyBytes;
		}
		/**
		 * Size of a step's body setter.
		 * @param bodyBytes size of the step's body
		 */
		public final void setBodyBytes(final String bodyBytes) {
			this.bodyBytes = bodyBytes;
		}
		/**
		 * bytes received during the step getter.
		 * @return bytes received during the step
		 */
		public final String getReceiveBytes() {
			return receiveBytes;
		}
		/**
		 * bytes received during the step setter.
		 * @param receiveBytes bytes received during the step
		 */
		public final void setReceiveBytes(final String receiveBytes) {
			this.receiveBytes = receiveBytes;
		}
		/**
		 * bytes sent during the step getter.
		 * @return bytes sent during the step
		 */
		public final String getSentBytes() {
			return sentBytes;
		}
		/**
		 * bytes sent during the step setter.
		 * @param sentBytes bytes sent during the step
		 */
		public final void setSentBytes(final String sentBytes) {
			this.sentBytes = sentBytes;
		}
		/**
		 * throughput over the length of the step getter.
		 * @return throughput over the length
		 */
		public final String getThroughput() {
			return throughput;
		}
		/**
		 * throughput over the length of the step setter.
		 * @param throughput throughput over the length
		 */
		public final void setThroughput(final String throughput) {
			this.throughput = throughput;
		}
		/**
		 * step status getter.
		 * @return the step's status
		 */
		public final String getStepStatus() {
			return stepStatus;
		}
		/**
		 * step status setter.
		 * @param stepStatus the step's status
		 */
		public final void setStepStatus(final String stepStatus) {
			this.stepStatus = stepStatus;
		}
		/**
		 * the step's job ID getter.
		 * @return the step's job ID
		 */
		public final String getJobID() {
			return jobID;
		}
		/**
		 * the step's job ID setter.
		 * @param jobID the step's job ID
		 */
		public final void setJobID(final String jobID) {
			this.jobID = jobID;
		}
		/**
		 * The step's result message yext getter
		 * @return Result message text
		 */
		public String getResultMessage() {
			return resultMessage;
		}
		/**
		 * The step's result message text setter
		 * @param resultMessage the result message text
		 */
		public void setResultMessage(String resultMessage) {
			this.resultMessage = resultMessage;
		}
	}
	
	/**
	 * Retrieves the lines that were collected from the console.log between the two points if any
	 * @return A LinkedList<ALineData> containing the values of the special steps that
	 * should be cross-referenced.
	 */
	public final HashMap<String, ALineData> getMyLines() {
		return myLines;
	}
	
	/**
	 * Creates a new LogDataExtractor and reads and parses the file a line at a time.
	 * This will parse the lines till the end-of-file so the useful data must be the
	 * last entered into the log.
	 * 
	 * @param filename Name of the file to parse
	 * @param fileStart The location in the file to begin extracting data
	 */
	public LogDataExtractor(final String filename, final long fileStart) {
		File consoleFile = new File(filename);
		FileInputStream results2 = null;
		consoleLogHasNoErrors = true;
		try {
			results2 = new FileInputStream(consoleFile);
			results2.skip(fileStart);
			results = new BufferedReader(new InputStreamReader(results2));
		} catch (FileNotFoundException e) {
			consoleLogHasNoErrors = false;
		} catch (IOException e) {
			consoleLog.error("IO error on consoleLog.", e);
			consoleLogHasNoErrors = false;
		}
		try {
			String newLine = results.readLine();
			while (newLine != null) {
				boolean tempStatus = parseALine(newLine);
				consoleLogHasNoErrors = consoleLogHasNoErrors && tempStatus;
				newLine = results.readLine();
			}
		} catch (IOException e) {
			consoleLog.error("Could not read a line from the file.", e);
			consoleLogHasNoErrors = false;
		}
		try {
			results.close();
			results2.close();
		} catch (IOException e) {
			consoleLog.error("Could not close the stream to the console.log during parsing.", e);
			consoleLogHasNoErrors = false;
		}
	}
	
	/**
	 * Parses a line of text into values.
	 * @param line A line of text without a line termination character included
	 * @return This line indicates an error
	 */
	public final boolean parseALine(String line) {
		//Remove the leading dots, they indicate tests started
		int i = 0;
		while (true) {
			char someChar = line.charAt(i);
			if (someChar == '.' || someChar == 'F') {
				i++;
				continue;
			}
			break;
		}
		//Parse a consoleLog line to throw errors when a exception was thrown to the output
		//or if the output values were bad.
		line = line.substring(i);
		char newChar;
		if (line.startsWith("[ERROR]", 0)) {
			return false;
		} else if (line.startsWith("[WARN ]", 0)) {
			return false;
		} else if (line.startsWith("[DEBUG]", 0)) {
			return true;
		} else if (line.startsWith("[INFO ]", 0)) {
			ALineData infoLine = new ALineData();
			String subline = line.substring(41);
			int j = 0;
			newChar = subline.charAt(j);
			while (newChar != ' ') {
				j++;
				newChar = subline.charAt(j);
			}
			subline = subline.substring(j + 1);
			infoLine.setJobID(subline.substring(0, 4));
			subline = subline.substring(5);
			if (subline.startsWith("pause")) {
				subline = subline.substring(6);
				//Extract Data params
				infoLine.setStepNum(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getStepNum().length() + 1);
				infoLine.setReportTime(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getReportTime().length() + 1);
				infoLine.setStepStatus(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getStepStatus().length() + 1);
				infoLine.setResultMessage(parseCommaEnded(subline));
				myLines.put(infoLine.jobID + "-" + infoLine.stepNum, infoLine);
				return true;
			} else if (subline.startsWith("open")) {
				subline = subline.substring(5);
				//Extract Data params
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
				infoLine.setStepStatus(parseCommaEnded(subline));
				subline = subline.substring(infoLine.getStepStatus().length() + 1);
				infoLine.setResultMessage(parseCommaEnded(subline));
				myLines.put(infoLine.jobID + "-" + infoLine.stepNum, infoLine);
				return true;
			} else if (subline.startsWith("verifyTitle")) {
				return true;
			} else {
				return true;
			}
		}
		//Random line, probably part of a stack trace
		return true;
	}

	
	/**
	 * Parse the part of the line that is comma or space delimited in order to pull values from the text.
	 * @param line Line of text to pull a value from
	 * @return The value pulled out
	 */
	public final String parseCommaEnded(final String line) {
		int k = 0;
		char someChar = 'a';
		while (someChar != ',' && k < line.length()) {
			someChar = line.charAt(k);
			k++;
		}
		return line.substring(0, k-1);
	}
	
	/**
	 * Retrieves the status of a set of lines parsed.
	 * @return true if the log lines parsed have no errors, else false;
	 */
	public final boolean isConsoleLogHasNoErrors() {
		return consoleLogHasNoErrors;
	}
	
}

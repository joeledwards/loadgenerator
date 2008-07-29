package com.awebstorm.robot;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class ResultLogReader {

	BufferedReader results;
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
	
	public ResultLogReader() {
		try {
			results = new BufferedReader(new InputStreamReader(new FileInputStream("Result.log")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void parse() {
		String line = null;
		int i = 0;
		try {
			line = results.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (line != null) {
			char someChar = line.charAt(i);
			if (someChar == '.') {
				i++;
				continue;
			}
			line = line.substring(i);
			if (line.startsWith("[INFO ]")) {
				String subline = line.substring(41);
				int j = 0;
				someChar = subline.charAt(j);
				while (someChar != ' ') {
					j++;
					someChar = subline.charAt(j);
				}
				subline = subline.substring(j+1);
				String jobID = subline.substring(0, 4);
				subline = subline.substring(5);
				if (subline.startsWith("WAIT")) {
					if (subline.contains("success")) {
						//success
					}
				} else if (subline.startsWith("INVOKE")) {
					subline = subline.substring(7);
					stepNum = parseCommaEnded(subline);
					subline = subline.substring(stepNum.length() + 1);
					reportTime = parseCommaEnded(subline);
					subline = subline.substring(reportTime.length() + 1);
					replyTime = parseCommaEnded(subline);
					subline = subline.substring(replyTime.length() + 1);
					timeStarted = parseCommaEnded(subline);
					subline = subline.substring(timeStarted.length() + 1);
					timeEnded = parseCommaEnded(subline);
					subline = subline.substring(timeEnded.length() + 1);
					timeResponded = parseCommaEnded(subline);
					subline = subline.substring(timeResponded.length() + 1);
					timeLength = parseCommaEnded(subline);
					subline = subline.substring(timeLength.length() + 1);
					bodyBytes = parseCommaEnded(subline);
					subline = subline.substring(bodyBytes.length() + 1);
					receiveBytes = parseCommaEnded(subline);
					subline = subline.substring(receiveBytes.length() + 1);
					sentBytes = parseCommaEnded(subline);
					subline = subline.substring(sentBytes.length() + 1);
					throughput = parseCommaEnded(subline);
					subline = subline.substring(throughput.length() + 1);
					stepStatus = subline;
				} else if (subline.startsWith("BUTTON")) {
					//failure
				} else {
					//failure
				}
			} else if (line.startsWith("[ERROR]", 0) || line.startsWith("[ERROR]", 1)) {
				//failure
			} else if (line.startsWith("[WARN ]", 0) || line.startsWith("[WARN ]", 1)) {
				//failure
			} else {
				//failure
			}
		}
	}
	
	public String parseCommaEnded(String line) {
		int k = 0;
		char someChar = line.charAt(0);
		while (someChar != ',') {
			k++;
			someChar = line.charAt(k);
		}
		return line.substring(0, k);
	}
	
}

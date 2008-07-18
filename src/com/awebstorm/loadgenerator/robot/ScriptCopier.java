package com.awebstorm.loadgenerator.robot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Used to quickly copy scripts for testing purposes
 * @author Cromano
 *
 */
public class ScriptCopier {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Queue<OutputStream> newStreams = new LinkedList<OutputStream>();
		InputStream fileInputStream = null;
		OutputStream fileOut = null;
		try {
			fileInputStream = new FileInputStream("ScriptThread1.xml");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < 100; i++) {
			try {
				fileOut = new FileOutputStream("Script3Thread" + (newStreams.size() + 1) + ".xml");
				newStreams.add(fileOut);
				int temp;
				temp = fileInputStream.read();
				while(temp != -1) {
					fileOut.write(temp);
				}
				fileOut.flush();
				fileOut.close();
				fileInputStream.close();
				fileInputStream = new FileInputStream("ScriptThread1.xml");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			fileInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

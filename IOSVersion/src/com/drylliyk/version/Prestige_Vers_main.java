package com.drylliyk.version;

import java.awt.RenderingHints.Key;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class Prestige_Vers_main {
	public static void main(String[] args) throws UnknownHostException, Exception {
		WritableWorkbook workbook = Workbook.createWorkbook(new File("outputPrestige.xls"));
		
		String ipaddressString = new String();
		ArrayList<String> unreachHost = new ArrayList<String>();
		String inputString = new String();
		ArrayList<StringBuffer> fromConsole = new ArrayList<StringBuffer>();
		
		File file = new File("unreachModem.txt");
		File OutputFile = new File("AllOutput.txt");
		
		FileReader IpAddressReader = new FileReader("address_list_Prestige.txt");
		BufferedReader IpAddressBuffer = new BufferedReader(IpAddressReader);
		
		String ToMainOutputString = new String();
		String ToMainParseString = new String();
		ArrayList<String> ToMainParseArray = new ArrayList<String>();
		
		String OSVersionString = new String();
		String UpTimeString = new String();
		String ModelString = new String();
		
		ipaddressString = IpAddressBuffer.readLine();

        //���������� ����� � ����
		PrintWriter outputFromCons = new PrintWriter(OutputFile.getAbsoluteFile());
		
		//||||||||||||������� ������ ������� ������� \\\\\\\\\\\\\
		// ������� ������� �� ���������� �����
		
		while (ipaddressString != null) {
			InetAddress adr = InetAddress.getByName(ipaddressString);
			
			if(!OutputFile.exists()){
	            OutputFile.createNewFile();
	        }
			
			// �������� �� �����������
			if (adr.isReachable(3000)) {
				// ��������
				System.out.println(ipaddressString+" is reache");
				ToMainParseArray.add(ipaddressString);
				ToMainOutputString = Connect(ipaddressString);
				//ToMainParseString = Parse(ToMainOutputString) + " ++--++  " + ipaddressString;
				Parse(ToMainOutputString, ToMainParseArray);
				//System.out.println(ToMainParseString);
				//fromConsole.add(ToMainOutputString);
				
			} else {
				// ����������
				System.out.println(ipaddressString+" unreacheble");
				unreachHost.add(ipaddressString);
			}
				ipaddressString = IpAddressBuffer.readLine();
		}
		
		for (int i = 0; i < ToMainParseArray.size(); i++) {
			outputFromCons.println(ToMainParseArray.get(i));
	   		outputFromCons.println();
		}
		
			//���������� ����
		    try {
		        //���������, ��� ���� ���� �� ���������� �� ������� ���
		        if(!file.exists()){
		            file.createNewFile();
		        }
		        //PrintWriter ��������� ����������� ������ � ����
		        PrintWriter out = new PrintWriter(file.getAbsoluteFile());
		        try {
			       	for (int j = 0; j < unreachHost.size(); j++) {
				         //���������� ����� � ����
				         out.println(unreachHost.get(j));
					}
			      } finally {
		            //����� ���� �� ������ ������� ����
		            //����� ���� �� ���������
		            out.close();
		        }
		    } catch(IOException e) {
		        throw new RuntimeException(e);
		    }
		    
		    //System.out.println(fromConsole);
		    outputFromCons.close();
		    
		    
		    
		    System.out.println(ToMainParseArray);
		    
		    ToExcelTable(workbook, ToMainParseArray);
		    
		    workbook.write();
			workbook.close();
		}
	
	public static String Connect(String ipaddressString) throws UnknownHostException, IOException {
		
		File OutputFile = new File("AllOutput.txt");
		
		Socket TlnSocket = new Socket(ipaddressString, 23);
		String OutputFromConsoleString = new String();
		String ToMainOutputString = new String();
		
		PrintWriter writeToConsole = new PrintWriter(TlnSocket.getOutputStream(), true);
		BufferedReader readFromConsole = new BufferedReader(new InputStreamReader(TlnSocket.getInputStream()));
				
		String fromConsoleString = readFromConsole.readLine(); // ���������� ���������� �� ������ �������
		ArrayList<String> arrayListInputString = new ArrayList<String>(); // ������ ��� ���� �����
		
		arrayListInputString.add(ipaddressString);
		
		// ������� �����������, ��������� ������ ������� ��� enter, ����� ���������, � ������� �����������
		writeToConsole.println("1234");
		writeToConsole.println("ocsicc");
		writeToConsole.println("24");
		writeToConsole.println("8");
		writeToConsole.println("sys version");
		writeToConsole.println("sys atsh");
		writeToConsole.println("exit");
		writeToConsole.println("99");
		//TlnSocket.close();
		
		while (true) {
			fromConsoleString = readFromConsole.readLine(); 
			
			ToMainOutputString = ToMainOutputString + fromConsoleString;
				
			//System.out.println(ToMainOutputString);
			System.out.println(fromConsoleString);
			
			if (fromConsoleString == null) {
				break;
			}
			
			if (fromConsoleString.contains("Username: ") == true) {
				break;
			}
		}
		
	//System.out.println(ToMainOutputString);	
	return ToMainOutputString; 
	}

	public static ArrayList<String> Parse(String ToMainOutputString, ArrayList<String> ToMainParseArray) {
		String ToMainParseString = new String();
		//ArrayList<String> ToMainParseArray = new ArrayList<String>();
		
		Integer OSVersionStartIndex = ToMainOutputString.indexOf("ZyNOS");
		Integer UptimeStringStartIndex = ToMainOutputString.indexOf("system up");
		Integer ModelStringStartIndex = ToMainOutputString.indexOf("Product Model");
		
		String OSVersionString = ToMainOutputString.substring(OSVersionStartIndex, OSVersionStartIndex + 40);
		String UpTimeString = ToMainOutputString.substring(UptimeStringStartIndex, UptimeStringStartIndex + 27);
		String ModelString = ToMainOutputString.substring(ModelStringStartIndex, ModelStringStartIndex + 38);
		
		ToMainParseArray.add(OSVersionString);
		ToMainParseArray.add(UpTimeString);
		ToMainParseArray.add(ModelString);
		
		ToMainParseString = OSVersionString + UpTimeString + ModelString;
		
		return ToMainParseArray;
	}

	public static void ToExcelTable(WritableWorkbook workbook, ArrayList<String> ToMainParseArray) throws RowsExceededException, WriteException {
		WritableSheet Sheet = workbook.createSheet("Prestige", 1);
		
		int k = 0;
		for (int i = 1; i < ToMainParseArray.size() / 4 +1; i++) {
			for (int j = 0; j < 4; j++) {
				Label label = new Label(j, i, ToMainParseArray.get(k));
				Sheet.addCell(label);
				k++;
			}
		}
	}
}


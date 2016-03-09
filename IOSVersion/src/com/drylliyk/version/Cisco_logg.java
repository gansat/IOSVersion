package com.drylliyk.version;

import java.io.BufferedReader;
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
import java.io.File;

import jxl.*;
import jxl.biff.WorkbookMethods;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.JxlWriteException;
import jxl.write.biff.RowsExceededException;


public class Cisco_logg {

	public static void main(String[] args) throws IOException, Exception {
		// TODO Auto-generated method stub
		// ��������. 
		//��������� ���� �� ������� ������� ��������� ���������, � ���� ������ ���������� �������� � �������, 
		//������� ������������, ������ ������ �������, ��������� ��� ������� ������ ����, �������� � ����. 
		//����������� � ������� ������, ��������� �������� �������� � ����. 
		//���� ����������� ��������� �������� � �����������. ��������� � ���������� ������. 
		//�� ��������� ������ ������� ����������� ���������� � ������������, �����
		WritableWorkbook workbook = Workbook.createWorkbook(new File("output.xls"));
		String IpAddressString = new String();
		
		FileReader IpAddressReader = new FileReader("address_list_�2610.txt");
		BufferedReader IpAddressBuffer = new BufferedReader(IpAddressReader);
		ArrayList<String> arrayListInputString = new ArrayList<String>(); // ������ ��� ���� �����
		ArrayList<String> arrayListParsedString = new ArrayList<String>(7); // ������ ��� ������������ �����
		ArrayList<String> arrayListToExcelTable = new ArrayList<String>(); // ������ ������� ����� ������� � �������
		IpAddressString = IpAddressBuffer.readLine(); // ������ ��� IP
		
		// ������ ��������� �� ���� �� � �����!
		while (IpAddressString != null) {
			InetAddress adr = InetAddress.getByName(IpAddressString);
			
			if (adr.isReachable(3000)) {
				arrayListInputString = ConnectWriteAndRead(IpAddressString); // ������ �� �������
				//arrayListParsedString = ParseOutputFromConsoleString(arrayListInputString, IpAddressString);
				
				// ��������. �� �������� �����, � ������ �� ��� ��������� ��������� � ������� ����� >< ����� ���� ����
				for (int i = 0; i < arrayListParsedString.size(); i++) {
					String TempoString = arrayListParsedString.get(i);
					arrayListToExcelTable.add(TempoString);
				}	
			} else {
				arrayListToExcelTable.add(IpAddressString);
				arrayListToExcelTable.add("unreach");
			}
			
					
			IpAddressString = IpAddressBuffer.readLine();
		}
		
		ToExcelTable(arrayListToExcelTable, workbook);
		//System.out.println(arrayListToExcelTable);
		
		workbook.write();
		workbook.close();
		IpAddressBuffer.close();
	}
	
	public static ArrayList<String> ConnectWriteAndRead(String IpAddressString) throws IOException, IOException {
		Socket TlnSocket = new Socket(IpAddressString, 23);
		String OutputFromConsoleString = new String();
		
		PrintWriter writeToConsole = new PrintWriter(TlnSocket.getOutputStream(), true);
		BufferedReader readFromConsole = new BufferedReader(new InputStreamReader(TlnSocket.getInputStream()));
				
		String fromConsoleString = readFromConsole.readLine(); // ���������� ���������� �� ������ �������
		ArrayList<String> arrayListInputString = new ArrayList<String>(); // ������ ��� ���� �����
		
		// ������� �����������, ��������� ������ ������� ��� enter, ����� ���������, � ������� �����������
		writeToConsole.println("aburyka");
		writeToConsole.println("Ba150192");
		writeToConsole.println("director");
		writeToConsole.println("ocsicc");
		writeToConsole.println("terminal length 0");
		writeToConsole.println("sh run");
		writeToConsole.println("exit");
		
		while (fromConsoleString != null) {
			fromConsoleString = readFromConsole.readLine(); 
			System.out.println(fromConsoleString);
			arrayListInputString.add(fromConsoleString);
		}
		
		return arrayListInputString;
	}
	
	public static void ParseOutputFromConsoleString(ArrayList<String> arrayListInputStrin, String IpAddressString) throws IOException {
		ArrayList<String> arrayListParsedString = new ArrayList<String>(8); // ������ ��� ������������ �����
		
		// ��������� ���������� ��� ������������ ��� ��������. ����� ������ �� � ������ � ���������� � main
		String StateString = new String();
		StateString = "ytre";
		//File file = new File("unreachModem.txt");
		FileWriter out = new FileWriter("unreachModem.txt", true);
		
		for (int i = 0; i < arrayListInputStrin.size()-1; i++) {
			String ParsingString = arrayListInputStrin.get(i);
			if (ParsingString.contains("access-list 22") == true) {
				//StateString = "bash";
				System.out.println("++||++   "+StateString + "   ++||++");
				System.out.println("++||++   "+IpAddressString + "   ++||++");
			
				out.write(IpAddressString + "\r" + "\n");
				out.close();
			}
		}
		
		// ��������� ArrayList
		//arrayListParsedString.add(StateString);
		//arrayListParsedString.add(IpAddressString);
		
		//return arrayListParsedString;
	}
	
	public static void ToExcelTable(ArrayList<String> arrayListToExcelTable, WritableWorkbook workbook) throws BiffException, IOException, JXLException {
		File OutputExcelFile = new File("out.xls");
		
		WritableSheet Sheet = workbook.createSheet("Cisco 2610", 1);
		
		Label RVCLbl = new Label(0, 0, "���");
		Sheet.addCell(RVCLbl);
		Label StationLbl = new Label(1, 0, "�������");
		Sheet.addCell(StationLbl);
		
		int k = 0;
		for (int i = 1; i < arrayListToExcelTable.size() / 2 +1; i++) {
			for (int j = 0; j < 2; j++) {
				Label label = new Label(j, i, arrayListToExcelTable.get(k));
				Sheet.addCell(label);
				k++;
			}
		}
	}
}

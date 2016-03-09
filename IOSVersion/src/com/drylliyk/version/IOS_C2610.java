package com.drylliyk.version;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.io.File;

import javax.net.ssl.HostnameVerifier;

import jxl.*;
import jxl.biff.WorkbookMethods;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.JxlWriteException;
import jxl.write.biff.RowsExceededException;


public class IOS_C2610 {

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
			while (InetAddress.getByName(IpAddressString).isReachable(3000) != true) {
				IpAddressString = IpAddressBuffer.readLine();
			}
			arrayListInputString = ConnectWriteAndRead(IpAddressString); // ������ �� �������
			arrayListParsedString = ParseOutputFromConsoleString(arrayListInputString, IpAddressString);
			// ��������. �� �������� �����, � ������ �� ��� ��������� ��������� � ������� ����� >< ����� ���� ����
			for (int i = 0; i < arrayListParsedString.size(); i++) {
				String TempoString = arrayListParsedString.get(i);
				arrayListToExcelTable.add(TempoString);
			}			
			IpAddressString = IpAddressBuffer.readLine();
		}
		
		ToExcelTable(arrayListToExcelTable, workbook);
		System.out.println(arrayListToExcelTable);
		
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
		writeToConsole.println("sh version");
		writeToConsole.println("exit");
		
		while (fromConsoleString != null) {
			fromConsoleString = readFromConsole.readLine(); 
			//System.out.println(fromConsoleString);
			arrayListInputString.add(fromConsoleString);
		}
		
		return arrayListInputString;
	}
	
	public static ArrayList<String> ParseOutputFromConsoleString(ArrayList<String> arrayListInputStrin, String IpAddressString) {
		ArrayList<String> arrayListParsedString = new ArrayList<String>(8); // ������ ��� ������������ �����
		
		// ��������� ���������� ��� ������������ ��� ��������. ����� ������ �� � ������ � ���������� � main
		String StateString = new String();
		String TypeString = new String();
		String RAMString = new String();
		String FlashString = new String();
		String VersionString = new String();
		String UpTimeString = new String();
		
		for (int i = 0; i < arrayListInputStrin.size()-1; i++) {
			String ParsingString = arrayListInputStrin.get(i);
			if (ParsingString.contains("IOS") == true) {
				Integer StartIndexVerInt = ParsingString.indexOf("IOS Software")+4; // StartIndexVerInt ������ ������� �� ������ ������ � ������
				Integer StopIndexVerInt = ParsingString.indexOf("Version"); // StopIndexVerInt ������ ������� ����� ������ ������ � ������
				VersionString = ParsingString.substring(StartIndexVerInt, StopIndexVerInt); // ���������� �������� ������ � ���������� VersionString
				System.out.println(VersionString);			
			} /*else if(ParsingString.contains("bytes of memory") == true) {
				Integer StartIndexType = ParsingString.lastIndexOf("cisco")+5; // StartIndexType ������ ������� �� ��������� � ����� 
				Integer StopIndexType = ParsingString.indexOf("("); // StopIndexType ������ ������� ����� ����
				Integer StartIndexRAM = ParsingString.lastIndexOf("with")+4; // StartIndexRAM ������ ������� �� ��������� � �����������
				Integer StopIndexRAM = ParsingString.indexOf("bytes"); // StopIndexRAM ������ ������� ����� ��������� � �����������
				
				TypeString = ParsingString.substring(StartIndexType, StopIndexType); // ���������� �������� ���� � ���������� TypeString
				RAMString = ParsingString.substring(StartIndexRAM, StopIndexRAM); // ���������� �������� ���������� � ���������� RAMString
								
				// ����� �������� ��������� ������ RAM. � ������� ��� ��������� ��� ����� ������ ��������� � ������ ����� ������
				// ������ ��������� � ������� RAMString ����
				// �� RAMString ������ ��������� ��� ������ ������: ProcessorMemoryString ��� ������ ���������� � ReadWriteMemoryInt ��� ������ �����\������
				// ������������ �������� ��������� � ��� ReadWriteMemoryInt � ProcessorMemoryInt, ����� ��������� � MemorySinteger
				// ���������� � main ����� MemorySinteger
				String ProcessorMemoryString = RAMString.substring(1,RAMString.indexOf("K/")); // ������� ��������� ProcessorMemoryString ��� ������ ���������
				String ReadWriteMemoryString = RAMString.substring(RAMString.indexOf("K/")+2, RAMString.length()-2); // ������� ��������� ReadWriteMemoryString ��� ������ ����� ������
				Integer ProcessorMemoryInt = Integer.valueOf(ProcessorMemoryString); // ������� ��������� ProcessorMemoryString ��� ������ ���������� � ��� ProcessorMemoryInt
				Integer ReadWriteMemoryInt = Integer.valueOf(ReadWriteMemoryString); // ������� ��������� ReadWriteMemoryString ��� ������ ���������� � ��� ReadWriteMemoryInt
				Integer MemorySinteger = ProcessorMemoryInt + ReadWriteMemoryInt; // �������� ��� ReadWriteMemoryInt � ��� ProcessorMemoryInt ��� ��������� ����� ������
				// �������� ���������� ��� �.�. 131072 65536 32768. �������� ������ RAMString ������ �� ����� ��������
				if (MemorySinteger == 131072) {
					RAMString = "128";
				} else if (MemorySinteger == 65536) {
					RAMString = "64";
				} else if (MemorySinteger == 32768){
					RAMString = "32";
				} else if (MemorySinteger == 98304){
					RAMString = "96";
				}
				*/
 								
			/*} else if (ParsingString.contains("flash (Read/Write)") == true) {
				Integer StopIndexFlash = ParsingString.indexOf("K"); // StopIndexFlah ������ ������� ����� ��������� � ������
				FlashString = ParsingString.substring(0, StopIndexFlash); // ��������� �� ��������� �����
				Integer FlashInt = Integer.valueOf(FlashString); // ��� �� ��������� �����
				// �������� ������ �.�. 32768 16384. �������� ������ FlashString ������ �� ����� ��������
				if (FlashInt == 32768) {
					FlashString = "32";
				} else if (FlashInt == 16384) {
					FlashString = "16";
				} else if (FlashInt == 8195){
					RAMString = "8";
				} else if (FlashInt == 8192){
					RAMString = "8";
				}*/
				
			/*} else if (ParsingString.contains("uptime") == true) {
				Integer StopIndexState = ParsingString.indexOf("uptime"); // StopIndexRAM ������ ������� ����� ��������� � ������ ������������
				StateString = ParsingString.substring(0, StopIndexState); // ��������� � ������ ������������
				
			}*/
			
		}
		
		// ��������� ArrayList
		//arrayListParsedString.add("�����");
		//arrayListParsedString.add(StateString);
		//arrayListParsedString.add(VersionString);
		//arrayListParsedString.add(TypeString);
		arrayListParsedString.add(IpAddressString);
		//arrayListParsedString.add(RAMString);
		//arrayListParsedString.add(FlashString);
		arrayListParsedString.add(VersionString);
		//System.out.println(StateString+" "+TypeString +" "+IpAddressString+" "+RAMString+" "+FlashString+" "+VersionString);
		//System.out.println(arrayListParsedString);
		
		return arrayListParsedString;
	}
	
	public static void ToExcelTable(ArrayList<String> arrayListToExcelTable, WritableWorkbook workbook) throws BiffException, IOException, JXLException {
		//File OutputExcelFile = new File("out.xls");
		
		WritableSheet Sheet = workbook.createSheet("Cisco 2610", 1);
		
		Label RVCLbl = new Label(0, 0, "���");
		Sheet.addCell(RVCLbl);
		Label StationLbl = new Label(1, 0, "�������");
		Sheet.addCell(StationLbl);
		Label TypeLbl = new Label(2, 0, "IOS");
		Sheet.addCell(TypeLbl);
		Label NameLbl = new Label(3, 0, "Name");
		Sheet.addCell(NameLbl);
		Label IPLbl = new Label(4, 0, "IP �����");
		Sheet.addCell(IPLbl);
		Label RamLbl = new Label(5, 0, "RAM, Mb");
		Sheet.addCell(RamLbl);
		Label FlashLbl = new Label(6, 0, "Flash, Mb");
		Sheet.addCell(FlashLbl);
		Label IOSLbl = new Label(7, 0, "IOS");
		Sheet.addCell(IOSLbl);
		Label lbl = new Label(1, 0, "hrte");
		
		
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

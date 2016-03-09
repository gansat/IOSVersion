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
		// начинаем. 
		//текстовый файл со списком адресов построчно считываем, в виде стринг переменной передаем в конеект, 
		//коннект подцепляется, вводит нужные команды, считывает все встринг эрреэй лист, передает в майн. 
		//стрингаррей с выводом парсим, результат парсинга передаем в мэйн. 
		//мейн зхаписывает результат парсинга в аррэйофпарс. переходим к следующему адресу. 
		//по окончании списка адресов эррейофпарс отправляем в записывателя, пишем
		WritableWorkbook workbook = Workbook.createWorkbook(new File("output.xls"));
		String IpAddressString = new String();
		
		FileReader IpAddressReader = new FileReader("address_list_с2610.txt");
		BufferedReader IpAddressBuffer = new BufferedReader(IpAddressReader);
		ArrayList<String> arrayListInputString = new ArrayList<String>(); // массив для всех строк
		ArrayList<String> arrayListParsedString = new ArrayList<String>(7); // массив для обработанных строк
		ArrayList<String> arrayListToExcelTable = new ArrayList<String>(); // массив который будет записан в таблицу
		IpAddressString = IpAddressBuffer.readLine(); // строка для IP
				
		// ударим перебором по всем ИП в файле!
		while (IpAddressString != null) {
			while (InetAddress.getByName(IpAddressString).isReachable(3000) != true) {
				IpAddressString = IpAddressBuffer.readLine();
			}
			arrayListInputString = ConnectWriteAndRead(IpAddressString); // чтение из консоли
			arrayListParsedString = ParseOutputFromConsoleString(arrayListInputString, IpAddressString);
			// быдлокод. мы получаем аррэй, и каждый из его элементов добавляем в большой эррей >< ротси меня боже
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
				
		String fromConsoleString = readFromConsole.readLine(); // построчное считывания из буфера консоли
		ArrayList<String> arrayListInputString = new ArrayList<String>(); // массив для всех строк
		
		// команды авторизации, настройка вывода консоли без enter, вывод хостнейма, и наконец интерфейсов
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
		ArrayList<String> arrayListParsedString = new ArrayList<String>(8); // массив для обработанных строк
		
		// строковые переменные для интересующих нас значений. будем сувать их в массив и передавать в main
		String StateString = new String();
		String TypeString = new String();
		String RAMString = new String();
		String FlashString = new String();
		String VersionString = new String();
		String UpTimeString = new String();
		
		for (int i = 0; i < arrayListInputStrin.size()-1; i++) {
			String ParsingString = arrayListInputStrin.get(i);
			if (ParsingString.contains("IOS") == true) {
				Integer StartIndexVerInt = ParsingString.indexOf("IOS Software")+4; // StartIndexVerInt индекс символа до номера версии в строке
				Integer StopIndexVerInt = ParsingString.indexOf("Version"); // StopIndexVerInt индекс символа после номера версии в строке
				VersionString = ParsingString.substring(StartIndexVerInt, StopIndexVerInt); // запоминаем значении версии в переменную VersionString
				System.out.println(VersionString);			
			} /*else if(ParsingString.contains("bytes of memory") == true) {
				Integer StartIndexType = ParsingString.lastIndexOf("cisco")+5; // StartIndexType индекс символа до подстроки с типом 
				Integer StopIndexType = ParsingString.indexOf("("); // StopIndexType индекс символа после типа
				Integer StartIndexRAM = ParsingString.lastIndexOf("with")+4; // StartIndexRAM индекс символа до подстроки с оперативкой
				Integer StopIndexRAM = ParsingString.indexOf("bytes"); // StopIndexRAM индекс символа после подстроки с оперативкой
				
				TypeString = ParsingString.substring(StartIndexType, StopIndexType); // записываем значение типа в переменную TypeString
				RAMString = ParsingString.substring(StartIndexRAM, StopIndexRAM); // записываем значение оперативки в переменную RAMString
								
				// нужно получить суммарную память RAM. в команде она выводится как сумма память процессор и память ввода вывода
				// парсим подстроку с памятью RAMString выше
				// из RAMString парсим подстроки для каждой памяти: ProcessorMemoryString для памяти процессора и ReadWriteMemoryInt для памяти ввода\вывода
				// распарсенное значение переводим в инт ReadWriteMemoryInt и ProcessorMemoryInt, затем суммируем в MemorySinteger
				// передавать в main будем MemorySinteger
				String ProcessorMemoryString = RAMString.substring(1,RAMString.indexOf("K/")); // парсинг подстроки ProcessorMemoryString для памяти процессор
				String ReadWriteMemoryString = RAMString.substring(RAMString.indexOf("K/")+2, RAMString.length()-2); // парсинг подстроки ReadWriteMemoryString для памяти ввода вывода
				Integer ProcessorMemoryInt = Integer.valueOf(ProcessorMemoryString); // перевод подстроки ProcessorMemoryString для памяти процессора в инт ProcessorMemoryInt
				Integer ReadWriteMemoryInt = Integer.valueOf(ReadWriteMemoryString); // перевод подстроки ReadWriteMemoryString для памяти процессора в инт ReadWriteMemoryInt
				Integer MemorySinteger = ProcessorMemoryInt + ReadWriteMemoryInt; // сложение инт ReadWriteMemoryInt и инт ProcessorMemoryInt для получения общей памяти
				// Значения суммарного инт м.б. 131072 65536 32768. Выбираем строку RAMString исходя из этого значения
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
				Integer StopIndexFlash = ParsingString.indexOf("K"); // StopIndexFlah индекс символа после подстроки с флэшем
				FlashString = ParsingString.substring(0, StopIndexFlash); // подстрока со значением флеша
				Integer FlashInt = Integer.valueOf(FlashString); // инт со значением флеша
				// Значения строки м.б. 32768 16384. Выбираем строку FlashString исходя из этого значения
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
				Integer StopIndexState = ParsingString.indexOf("uptime"); // StopIndexRAM индекс символа после подстроки с именем обородования
				StateString = ParsingString.substring(0, StopIndexState); // подстрока с именем оборудования
				
			}*/
			
		}
		
		// формируем ArrayList
		//arrayListParsedString.add("пусто");
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
		
		Label RVCLbl = new Label(0, 0, "РВЦ");
		Sheet.addCell(RVCLbl);
		Label StationLbl = new Label(1, 0, "Станция");
		Sheet.addCell(StationLbl);
		Label TypeLbl = new Label(2, 0, "IOS");
		Sheet.addCell(TypeLbl);
		Label NameLbl = new Label(3, 0, "Name");
		Sheet.addCell(NameLbl);
		Label IPLbl = new Label(4, 0, "IP адрес");
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

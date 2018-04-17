package com.axelb;

import java.io.File;

import com.axelb.pkzip.PKZipReader;
import com.axelb.pkzip.PKZipWriter;
import com.axelb.pkzip.err.PKException;

public class MainFileZip {
	public static void main(String[] args) {
		/*
		 * LEGGERE UNO ZIP
		
		// Leggo un file zip preesistente
		PKZipReader zip;
		try {
			zip = new PKZipReader("resources//CNP12000_1.zip");
			
			// Mi creo un oggetto PKZipWriter
			PKZipWriter zipOut = new PKZipWriter("resources//test.zip");
			
			// Aggiungo un file
			zipOut.addFile("resources//F2.txt");
			zipOut.addFile("resources//F1.txt");
			
			// Aggiungo lo zip precedentemente letto
			zipOut.addZipReader(zip);
			
			// Scrivo il nuovo zip
			zipOut.write();
		} catch (PKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("FATTO!");
		*/
		/*
		 * ZIPPARE UNA SERIE DI FILE DA ZERO
		*/
		try {
			PKZipWriter zip = new PKZipWriter("resources//w.zip");
			 
			zip.addFile("resources//C0030083//C0030083.txt");
			zip.addFile("resources//C0030083//CNP30083_2221307.pdf");
			
			
			zip.write();
			
			
		} catch(PKException e) {
			e.printStackTrace();
		}
		
		System.out.println("FATTO!");
		
		/*
		 * ZIPPARE UNA CARTELLA DA ZERO
		 
		try {
			PKZipWriter zip = new PKZipWriter("resources//w.zip");
			
			System.out.println("LEGGO LA CARTELLA");
			int totFile = 0;
			
			File dir = new File("resources//CNP12000_1");
			String[] children = dir.list();
			if(children == null) {
				
			} else {
				for(int i = 0;i < children.length;i++) {
					System.out.println("AGGIUNGO FILE "+i+": "+children[i]);
					zip.addFile("resources//cnp12000_1//"+children[i]);
					
				}
			}
			
			System.out.println("SCRIVO LO ZIP");
			zip.write();
		} catch(PKException e) {
			e.printStackTrace();
		}
		
		System.out.println("FATTO!");
		*/
	}
}

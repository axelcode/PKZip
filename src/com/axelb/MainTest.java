package com.axelb;

import com.axelb.pkzip.PKZipReader;
import com.axelb.pkzip.err.PKException;

public class MainTest {
	public static void main(String[] args) {
		try {
			PKZipReader zip = new PKZipReader("resources//Z0030083.zip");
			
			zip.extract();
		} catch (PKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("FINITO");
	}
}

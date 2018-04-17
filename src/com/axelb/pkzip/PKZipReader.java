/*
 * ==========================================================================
 * class name  : com.axelb.pkzip.PKZipReader
 * 
 * Begin       : 
 * Last Update : 
 *
 * Author      : Alessandro Baldini - alex.baldini72@gmail.com
 * License     : GNU-GPL v2 (http://www.gnu.org/licenses/)
 * ==========================================================================
 * 
 * PKZip
 * Copyright (C) 2017 Alessandro Baldini
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Linking PKZip(C) statically or dynamically with other 
 * modules is making a combined work based on PKZip(C). 
 * Thus, the terms and conditions of the GNU General Public License cover 
 * the whole combination.
 *
 * In addition, as a special exception, the copyright holders 
 * of PKZip(C) give you permission to combine 
 * PKZip(C) program with free software programs or libraries 
 * that are released under the GNU LGPL. 
 * You may copy and distribute such a system following the terms of the GNU GPL 
 * for PKZip(C) and the licenses of the other code concerned, 
 * provided that you include the source code of that other code 
 * when and as the GNU GPL requires distribution of source code.
 *
 * Note that people who make modified versions of PKZip(C) 
 * are not obligated to grant this special exception for their modified versions; 
 * it is their choice whether to do so. The GNU General Public License 
 * gives permission to release a modified version without this exception; 
 * this exception also makes it possible to release a modified version 
 * which carries forward this exception.
 * 
 */
package com.axelb.pkzip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.axelb.pkzip.compress.FlateDecode;
import com.axelb.pkzip.err.*;
import com.axelb.pkzip.structure.CentralDirectory;
import com.axelb.pkzip.structure.ECD;


public class PKZipReader implements PKZip {
	private String zipInput;
	private ECD ecd;
	
	/**
	 * Genera un oggetto di tipo PKZipReader.<br>
	 * Un oggetto di questo tipo è un file zip preesistente. Questo lo si può
	 * usare come base di partenza per un nuovo file zip ( a cui è possibile
	 * aggiungere altri file da zippare) oppure tramite i metodi della classe
	 * PKZipReader è possibile decomprimere lo zip ed estrarre i files che esso contiene.
	 * 
	 * @param fileInput Il nome del file zip
	 * @throws PKException
	 */
	public PKZipReader(String fileInput) throws PKException {
		this.zipInput = fileInput;
		this.ecd = null;
		
		this.read();
	}
	
	public String getPathZipFile() {
		return zipInput;
	}
	private void read() throws PKException {
		PKRandomAccessFile raf;
		
		try {
			raf = new PKRandomAccessFile(zipInput,"r");
			
			/* Per ottimizzare la riscrittura dello zip,
			 * utilizzerò il seguente algoritmo:
			 * 
			 * Procedo dalla fine del file fino a trovare la END OF CENTRAL DIRECTORY
			 * Recupero i riferimenti della CENTRAL DIRECTORY. 
			 * Mi fermo. Con la CD ho comunque la possibilità di ritrovare ogni singolo file
			 * 
			 */
			
			raf.seek(raf.length() - 22); // 22 byte è la lunghezza della ECD senza commenti
			long filePointer = raf.getFilePointer();
			
			boolean flagTrovato = false;
			
			while(!flagTrovato) {
				byte[] signature = new byte[4];
				raf.read(signature);
				
				if(signature[0] == SIGNATURE_END_CENTRAL_DIRECTORY[0] &&
						signature[1] == SIGNATURE_END_CENTRAL_DIRECTORY[1] &&
						signature[2] == SIGNATURE_END_CENTRAL_DIRECTORY[2] &&
						signature[3] == SIGNATURE_END_CENTRAL_DIRECTORY[3]) {
					
					flagTrovato = true;
				} else {
					filePointer--;
					
					if(filePointer < 0) {
						raf.close();
						throw new PKECDMissingException();
					}
					raf.seek(filePointer);
					
				}
			}
			
			// Trovato l'inizio della ECD la legge
			ecd = new ECD();
			ecd.setDiskNumber(raf.read2B());
			ecd.setDiskWCD(raf.read2B());
			ecd.setDiskEntries(raf.read2B());
			ecd.setTotalEntries(raf.read2B());
			ecd.setCentralDirectorySize(raf.read4B());
			ecd.setOffsetCD(raf.read4B());
			
			short commentLen = raf.read2B();
			if(commentLen > 0) { // Legge il commento
				byte[] stream = new byte[commentLen];
				raf.read(stream);
				ecd.setZipFileComment(new String(stream));
			}
			raf.close();
			
			
		} catch (FileNotFoundException e) {
			throw new PKFileNotFoundException(zipInput);
		} catch (IOException e) {
			throw new PKIOException();
		}
			
		
	}
	
	public void extract() throws PKException {
		this.extract(null);
	}
	public void extract(String dirName) throws PKException {
		PKRandomAccessFile rin;
		PKRandomAccessFile rout;
		String dirFileExtract = null;
		String pathExtractFile = null;
		
		try {
			rin = new PKRandomAccessFile(zipInput,"r");
			
			if(dirName == null)
				dirFileExtract = rin.getFileNameWithoutExtension();
			else
				dirFileExtract = dirName;
			
			// Crea la directory ove scompatterà i file
			pathExtractFile = rin.getPathFile()+dirFileExtract;
			boolean success = (new File(pathExtractFile).mkdir());
			
			if(!success) {
				// Se esiste già solleva un eccezione
				rin.close();
				throw new PKDirectoryAlreadyExistsException();
			}
			
			/* Operatività
			 * 1. Si posiziona all'inizio della CD
			 * 2. Legge ogni CD e per ognuna estrae il file
			 */
			rin.seek(ecd.getOffsetCD());
			byte[] signature = new byte[4];
			rin.read(signature);
			while(signature[0] == PKZip.SIGNATURE_CENTRAL_DIRECTORY_FILE_HEADER[0] &&
					signature[1] == PKZip.SIGNATURE_CENTRAL_DIRECTORY_FILE_HEADER[1] &&
					signature[2] == PKZip.SIGNATURE_CENTRAL_DIRECTORY_FILE_HEADER[2] &&
					signature[3] == PKZip.SIGNATURE_CENTRAL_DIRECTORY_FILE_HEADER[3]) {
				
				CentralDirectory cd = new CentralDirectory();
				cd.setVersion(rin.read2B());
				cd.setVersionNeeded(rin.read2B());
				cd.setFlags(rin.read2B());
				cd.setCompression(rin.read2B());
				cd.setModTime(rin.read2B());
				cd.setModDate(rin.read2B());
				cd.setCRC32(rin.read4B());
				cd.setCompressedSize(rin.read4B());
				cd.setUncompressedSize(rin.read4B());
				
				short fileNameLen = rin.read2B();
				short extraFieldLen = rin.read2B();
				short fileCommentLen = rin.read2B();
				
				cd.setDiskStart(rin.read2B());
				cd.setInternalAttr(rin.read2B());
				cd.setExternalAttr(rin.read4B());
				cd.setOffsetLocalHeader(rin.read4B());
				
				byte[] fileName = new byte[fileNameLen];
				rin.read(fileName);
				cd.setFileName(new String(fileName));
				
				if(extraFieldLen > 0) {
					byte[] extraField = new byte[extraFieldLen];
					rin.read(extraField);
					cd.setExtraField(new String(extraField));
				}
				if(fileCommentLen > 0) {
					byte[] fileComment = new byte[fileCommentLen];
					rin.read(fileComment);
					cd.setFileComment(new String(fileComment));
				}
				
				System.out.println("ESTRAGGO FILE: "+cd.getFileName());
				// Procede ad estrarre il file
				this.extractFile(pathExtractFile, cd, rin);
				
				rin.read(signature);
			}
			
			
			
			rin.close();
		} catch (FileNotFoundException e) {
			throw new PKFileNotFoundException();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			throw new PKIOException();
		}
		
		
	}
	
	private void extractFile(String path, CentralDirectory cd, PKRandomAccessFile raf) throws IOException, PKDirectoryException {
		byte[] streamFile = null;
		
		// Salvo la posizione corrente del file
		long currentPointer = raf.getFilePointer();
		
		// Mi posiziono all'inizio del FILE HEADER
		raf.seek(cd.getOffsetLocalHeader());
		
		// Leggo i primi 28 byte (identici a quelli presenti nella CD
		byte[] header = new byte[28];
		raf.read(header);
		
		// Legge la dimensione dell'extra field (che potrebbe differire da quella presente nella CD)
		short extraFieldLen = raf.read2B();
		
		// Leggo il nome del file
		byte[] streamFileName = new byte[cd.getFileNameLen()];
		raf.read(streamFileName);
		String fileName = new String(streamFileName);
		
		// Se è presente un'extraField la leggo
		if(extraFieldLen > 0) {
			byte[] streamExtraField = new byte[extraFieldLen];
			raf.read(streamExtraField);
			String extraField = new String(streamExtraField);
		}
		
		// Verifica se è un file oppure una directory
		if(cd.getCompressedSize() == 0) {
			// E' una directory. La crea
			boolean success = (new File(path+"//"+fileName).mkdir());
			
			if(!success) {
				raf.seek(currentPointer);
				throw new PKDirectoryException(path+"//"+fileName);
			}
				
			
		} else {
			// Legge lo stream da decomprimere
			byte[] streamFileCompress = new byte[cd.getCompressedSize()];
			raf.read(streamFileCompress);
			
			// Verifica il tipo di compressione
			switch(cd.getCompression()) {
			case COMPRESSION_NOCOMPRESSION:
				streamFile = streamFileCompress;
				break;
				
			case COMPRESSION_DEFLATED:
				streamFile = FlateDecode.decode(streamFileCompress,false);
				break;
			}
			
			// Se streamFile è valorizzato scrive il file decompresso
			PKRandomAccessFile rout;
			try {
				rout = new PKRandomAccessFile(path+"//"+fileName,"rw");
				
			} catch(FileNotFoundException fnfe) {
				// Se solleva un eccezione significa che si sta creando un
				// file in un percorso inesistente. In questo caso
				// si cicla sul percorso finchè non si è creata l'intera
				// struttura di directory
				File pFile = new File(path+"//"+fileName);
				createDirectory(pFile.getParent());
				
				rout = new PKRandomAccessFile(path+"//"+fileName,"rw");
			}
			
			rout.writeStream(streamFile);
			rout.close();
		}
		
		// Ripristina la posizione corrente del file
		raf.seek(currentPointer);
		
	}
	
	private void createDirectory(String path) {
		File f = new File(path);
		
		if(f.getParent() != null)
			createDirectory(f.getParent());
		
		
		f.mkdir();
	}
	public int getOffsetCD() {
		if(ecd == null)
			return -1;
		
		return ecd.getOffsetCD();
	}
	public int getCentralDirectorySize() {
		if(ecd == null)
			return -1;
		
		return ecd.getCentralDirectorySize();
	}
	public short getTotalEntries() {
		if(ecd == null)
			return -1;
		
		return ecd.getTotalEntries();
	}
}

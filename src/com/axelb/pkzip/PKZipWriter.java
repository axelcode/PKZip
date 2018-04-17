/*
 * ==========================================================================
 * class name  : com.axelb.pkzip.PKZipWriter
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

import com.axelb.pkzip.err.*;
import com.axelb.pkzip.err.PKException;
import com.axelb.pkzip.structure.FileZip;

public class PKZipWriter implements PKZip {
	public static final short FLAGS_00					= 0;	// encrypted file
	
	private String fileOutput;
	private Vector<FileZip> fileZip;
	
	private PKZipReader zipReader;
	
	/*
	 * Proprietà relative allo zipping
	 */
	private short version;
	private short flags;
	private short compression;
	
	public PKZipWriter(String fileOutput) {	
		this.fileOutput = fileOutput;
		
		fileZip = new Vector<FileZip>();
		
		// Imposta le proprietà di default
		zipReader = null;
		
		version = 20;
		flags = FLAGS_00;
		compression = COMPRESSION_DEFLATED;
		
	}
	
	public void addZipReader(PKZipReader reader) {
		this.zipReader = reader;
	}
	public void setCompressionMethod(short value) {
		// Verifica che il valore in ingresso sia gestito
		if(value != COMPRESSION_NOCOMPRESSION && value != COMPRESSION_DEFLATED)
			return;
		
		compression = value;
	}
	public void addFile(String fileName) throws PKException {
		fileZip.add(new FileZip(fileName, compression));
	}
	
	public void write() throws PKException {
		long offsetCentralDirectory = 0;
		int cdSize = 0;
		
		// Dati relativi alla presenza di uno zip reader
		PKRandomAccessFile raf;
		byte[] streamCDReader = null;
		
		try {
			raf = new PKRandomAccessFile(fileOutput,"rw");
			
			/* Gestione Zip Reder */
			if(zipReader != null) {
				RandomAccessFile rin = new RandomAccessFile(zipReader.getPathZipFile(),"r");
				byte[] streamZipReader = new byte[zipReader.getOffsetCD()];
				rin.read(streamZipReader);	// Questo è lo stream che andrà riportato nel nuovo file
				
				streamCDReader = new byte[zipReader.getCentralDirectorySize()];
				rin.read(streamCDReader);
				
				rin.close();
				
				raf.writeStream(streamZipReader);
				
			}
			
			
			
			// FILE ZIP
			// Cicla per tutti i file da zippare
			for(int i = 0;i < fileZip.size();i++) {
				FileZip fz = fileZip.get(i);
				
				// Imposta l'offset del file
				fz.setOffsetLocalHeader((int)raf.getFilePointer());
				raf.writeStream(SIGNATURE_LOCAL_FILE_HEADER);
				
				// LOCAL FILE HEADER
				raf.write2B(version);
				raf.write2B(flags);
				raf.write2B(compression);
				
				raf.write2B(fz.getModeTime());
				raf.write2B(fz.getModeDate());
				
				raf.write4B(fz.getCRC32());
				raf.write4B(fz.getCompressedSize());
				raf.write4B(fz.getUncompressedSize());
				
				raf.write2B(fz.getFileNameLen());
				raf.write2B(fz.getExtraFieldLen());
				raf.writeString(fz.getFileName());
				
				if(fz.getExtraFieldLen() > 0) {
					// Inserisce i contenuti extra
					// AL MOMENTO NON FA NULLA
				}
				
				// FILE DATA
				raf.writeStream(fz.getFileData());
			}
			
			// CENTRAL DIRECTORY
			offsetCentralDirectory = raf.getFilePointer();
			
			/* Gestione Zip Reader */
			if(zipReader != null) {
				// Riporta la CD dello zip reader
				raf.writeStream(streamCDReader);
			}
			// Anche qui cicla per tutti i file
			for(int i = 0;i < fileZip.size();i++) {
				FileZip fz = fileZip.get(i);
				
				raf.writeStream(SIGNATURE_CENTRAL_DIRECTORY_FILE_HEADER);
				cdSize += 4;
				
				raf.write1B(31);
				raf.write1B(0);
				cdSize += 2;
				
				raf.write2B(version);
				cdSize += 2;
				raf.write2B(flags);
				cdSize += 2;
				raf.write2B(compression);
				cdSize += 2;
				
				raf.write2B(fz.getModeTime());
				cdSize += 2;
				raf.write2B(fz.getModeDate());
				cdSize += 2;
				
				raf.write4B(fz.getCRC32());
				cdSize += 4;
				raf.write4B(fz.getCompressedSize());
				cdSize += 4;
				raf.write4B(fz.getUncompressedSize());
				cdSize += 4;
				
				raf.write2B(fz.getFileNameLen());
				cdSize += 2;
				raf.write2B(fz.getExtraFieldLen());
				cdSize += 2;
				raf.write2B(fz.getFileCommentLen());
				cdSize += 2;
				raf.write2B(fz.getDiskStart());
				cdSize += 2;
				raf.write2B(fz.getInternalAttr());
				cdSize += 2;
				raf.write4B(fz.getExternalAttr());
				cdSize += 4;
				
				raf.write4B(fz.getOffsetLocalHeader());
				cdSize += 4;
				raf.writeString(fz.getFileName());
				cdSize += fz.getFileNameLen();
				
				if(fz.getExtraFieldLen() > 0) {
					// Inserisce i contenuti extra
					// AL MOMENTO NON FA NULLA
				}
				if(fz.getFileCommentLen() > 0) {
					// Inserisce i commenti
					// AL MOMENTO NON FA NULLA
				}
			}
			
			/* Gestione Zip Reader */
			short totalFile = (short)fileZip.size();
			if(zipReader != null) {
				totalFile += zipReader.getTotalEntries();
				cdSize += zipReader.getCentralDirectorySize();
			}
			
			// END CENTRAL DIRECTORY
			raf.writeStream(SIGNATURE_END_CENTRAL_DIRECTORY);
			raf.write2B(0);	// Disk Number
			raf.write2B(0);	// Disk # w/cd
			
			raf.write2B(totalFile);
			raf.write2B(totalFile);
			
			raf.write4B(cdSize);
			raf.write4B(offsetCentralDirectory);
			
			// Al momento nessun commento
			raf.write2B(0);
			raf.close();
		} catch (FileNotFoundException e) {
			throw new PKFileNotFoundException();
		} catch (IOException e) {
			throw new PKIOException();
		}
		
	}
}

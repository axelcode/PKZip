/*
 * ==========================================================================
 * class name  : com.axelb.pkzip.structure.FileZip
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
package com.axelb.pkzip.structure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.zip.CRC32;

import com.axelb.pkzip.err.*;
import com.axelb.pkzip.PKZipWriter;
import com.axelb.pkzip.compress.FlateDecode;
import com.axelb.pkzip.err.PKException;

public class FileZip {
	private short compression;	// Tipo di algoritmo usato nella compressione
	
	private byte[] fileData;
	private String fileNameWithPathAbsolute;
	private short modeTime;
	private short modeDate;
	private int crc32;
	private int compressedSize;
	private int uncompressedSize;
	
	private short fileNameLen;
	private short extraFieldLen;
	private String fileName;
	private String extraField;
	
	private short fileCommentLen;
	private short diskStart;
	private short internalAttr;
	private int externalAttr;
	private int offsetLocalHeader;
	private String fileComment;
	
	public FileZip(String fileName, short compression) throws PKException {
		this.fileNameWithPathAbsolute = fileName;
		this.compression = compression;
		
		// Recupera il nome del file:
		File f = new File(fileName);
		this.fileName = f.getName();
		this.fileNameLen = (short)this.fileName.length();
		long ultimaModifica = f.lastModified();
		
		// Imposta a zero gli extrafield
		extraFieldLen = 0;
		fileCommentLen = 0;
		diskStart = 0;
		internalAttr = 0;
		externalAttr = 0;
		offsetLocalHeader = 0;
		fileComment = "";
		
		Date date = new Date(ultimaModifica);
		this.setDateTime(date);
		
		// Procede ad effettuare la compressione del file
		this.compressed();
		compressedSize = fileData.length;
		
	}
	
	public short getModeTime() {
		return modeTime;
	}
	public short getModeDate() {
		return modeDate;
	}
	public int getCRC32() {
		return crc32;
	}
	public int getCompressedSize() {
		return compressedSize;
	}
	public int getUncompressedSize() {
		return uncompressedSize;
	}
	public short getFileNameLen() {
		return fileNameLen;
	}
	public short getExtraFieldLen() {
		return extraFieldLen;
	}
	public String getFileName() {
		return fileName;
	}
	public byte[] getFileData() {
		return fileData;
	}
	public short getFileCommentLen() {
		return fileCommentLen;
	}
	public String getFileComment() {
		return fileComment;
	}
	public short getDiskStart() {
		return diskStart;
	}
	public short getInternalAttr() {
		return internalAttr;
	}
	public int getExternalAttr() {
		return externalAttr;
	}
	public void setOffsetLocalHeader(int value) {
		this.offsetLocalHeader = value;
	}
	public int getOffsetLocalHeader() {
		return offsetLocalHeader;
	}
	private void setDateTime(Date dateTime) {
		modeTime = (short)(dateTime.getHours() << 11 | dateTime.getMinutes() << 5 | dateTime.getSeconds());
		modeDate = (short)(dateTime.getYear() << 9 | dateTime.getMonth() << 5 | dateTime.getDay());
		
	}
	private void compressed() throws PKException {
		// Ottiene lo stream di bytes del file da comprimere 
		RandomAccessFile raf;
		
		try {
			raf = new RandomAccessFile(fileNameWithPathAbsolute,"r");
			
			uncompressedSize = (int)raf.length();
			byte[] stream = new byte[(int)raf.length()];
			raf.read(stream);
			
			switch(compression) {
				case PKZipWriter.COMPRESSION_NOCOMPRESSION:
					fileData = stream;
					break;
				
				case PKZipWriter.COMPRESSION_DEFLATED:
					fileData = FlateDecode.encode(stream);
					break;
					
				default:
					raf.close();
					throw new PKCompressionUnknownException();
					
			}
			
			// Ottiene il CRC32
			CRC32 checksum = new CRC32();
			checksum.update(stream);
			crc32 = (int)checksum.getValue();
			raf.close();
		} catch (FileNotFoundException e) {
			throw new PKFileNotFoundException(fileNameWithPathAbsolute);
		} catch (IOException e) {
			throw new PKIOException();
		}
		
	}
}

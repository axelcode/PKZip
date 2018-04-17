/*
 * ==========================================================================
 * class name  : com.axelb.pkzip.PKRandomAccessFile
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
import java.io.RandomAccessFile;

import com.axelb.pkzip.err.PKException;

public class PKRandomAccessFile extends RandomAccessFile {
	private String fileName;
	private String pathFile;
	
	public PKRandomAccessFile(String name, String mode)
			throws FileNotFoundException {
		super(name, mode);
		
		File f = new File(name);
		this.fileName = f.getName();
		this.pathFile = f.getAbsolutePath().replaceAll(f.getName(), "");
		
		if(mode.equals("rw")) {
			// Se il file esiste lo cancella
			f.delete();
			
		}
	}

	public String getPathFile() {
		return pathFile;
	}
	public String getFileName() {
		return fileName;
	}
	public String getFileNameWithoutExtension() {
		if(fileName == null)
			return null;
		
		for(int i = (fileName.length()-1);i >= 0;i--) {
			if(fileName.charAt(i) == '.') {
				return fileName.substring(0,i);
			}
		}
		
		return fileName;
	}
	public short read2B() throws IOException {
		return this.getShort(this.readShort());
	}
	public int read4B() throws IOException {
		return this.getInt(this.readInt());
	}
	public void write1B(int v) throws IOException {
		this.writeByte(v);
	}
	public void write2B(short v) throws IOException {
		this.writeShort(this.getShort(v));
	}
	public void write2B(int v) throws IOException {
		this.write2B((short)v);
	}
	public void write4B(int v) throws IOException {
		this.writeInt(this.getInt(v));
	}
	public void write4B(long v) throws IOException {
		this.write4B((int)v);
	}
	public void writeString(String v) throws IOException {
		this.writeBytes(v);
	}
	public void writeStream(byte[] b) throws IOException {
		this.write(b);
	}
	private short getShort(short value) {
		byte[] b = new byte[2];
		
		b[0] = (byte)value;
		b[1] = (byte)(value >> 8);
		
		int ret = 0;
		
		ret = ret << 8;
		ret = ret | (b[0] & 0xFF);
		ret = ret << 8;
		ret = ret | (b[1] & 0xFF);
		
		return (short)ret;
	}
	private int getInt(int value) {
		byte[] b = new byte[4];
		
		b[0] = (byte)value;
		b[1] = (byte)(value >> 8);
		b[2] = (byte)(value >> 16);
		b[3] = (byte)(value >> 24);
		
		int ret = 0;
		
		ret = ret << 8;
		ret = ret | (b[0] & 0xFF);
		ret = ret << 8;
		ret = ret | (b[1] & 0xFF);
		ret = ret << 8;
		ret = ret | (b[2] & 0xFF);
		ret = ret << 8;
		ret = ret | (b[3] & 0xFF);
		
		
		return ret;
	}
}

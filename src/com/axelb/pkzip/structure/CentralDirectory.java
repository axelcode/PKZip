/*
 * ==========================================================================
 * class name  : com.axelb.pkzip.structure.CentralDirectory
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

public class CentralDirectory {
	private short version;
	private short versionNeeded;
	private short flags;
	private short compression;
	private short modTime;
	private short modDate;
	private int crc32;
	private int compressedSize;
	private int uncompressedSize;
	private short diskStart;
	private short internalAttr;
	private int externalAttr;
	private int offsetLocalHeader;
	private String fileName;
	private String extraField;
	private String fileComment;
	
	public CentralDirectory() {
		fileName = "";
		extraField = "";
		fileComment = "";
	}
	
	public short getFileNameLen() {
		return (short)fileName.length();
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String value) {
		this.fileName = value;
	}
	public short getExtraFieldLen() {
		return (short)extraField.length();
	}
	public String getExtraField() {
		return extraField;
	}
	public void setExtraField(String value) {
		this.extraField = value;
	}
	public short getFileCommentLen() {
		return (short)fileComment.length();
	}
	public String getFileComment() {
		return fileComment;
	}
	public void setFileComment(String value) {
		this.fileComment = value;
	}
	
	public short getVersion() {
		return version;
	}
	public void setVersion(short value) {
		this.version = value;
	}
	public short getVersionNeeded() {
		return versionNeeded;
	}
	public void setVersionNeeded(short value) {
		this.versionNeeded = value;
	}
	public short getFlags() {
		return flags;
	}
	public void setFlags(short value) {
		this.flags = value;
	}
	public short getCompression() {
		return compression;
	}
	public void setCompression(short value) {
		this.compression = value;
	}
	public short getModTime() {
		return modTime;
	}
	public void setModTime(short value) {
		this.modTime = value;
	}
	public short getModDate() {
		return modDate;
	}
	public void setModDate(short value) {
		this.modDate = value;
	}
	public int getCRC32() {
		return crc32;
	}
	public void setCRC32(int value) {
		this.crc32 = value;
	}
	public int getCompressedSize() {
		return compressedSize;
	}
	public void setCompressedSize(int value) {
		this.compressedSize= value;
	}
	public int getUncompressedSize() {
		return uncompressedSize;
	}
	public void setUncompressedSize(int value) {
		this.uncompressedSize = value;
	}
	public short getDiskStart() {
		return diskStart;
	}
	public void setDiskStart(short value) {
		this.diskStart = value;
	}
	public short getInternalAttr() {
		return internalAttr;
	}
	public void setInternalAttr(short value) {
		this.internalAttr = value;
	}
	public int getExternalAttr() {
		return externalAttr;
	}
	public void setExternalAttr(int value) {
		this.externalAttr = value;
	}
	public int getOffsetLocalHeader() {
		return offsetLocalHeader;
	}
	public void setOffsetLocalHeader(int value) {
		this.offsetLocalHeader = value;
	}
	
}

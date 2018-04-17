/*
 * ==========================================================================
 * class name  : com.axelb.pkzip.structure.ECD
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

public class ECD {
	private short diskNumber;
	private short diskWCD;
	private short diskEntries;
	private short totalEntries;
	private int centralDirectorySize;
	private int offsetCD;
	private String zipFileComment;
	
	public ECD() {
		zipFileComment = "";
	}
	public void setDiskNumber(short value) {
		diskNumber = value;
	}
	public short getDiskNumber() {
		return diskNumber;
	}
	public void setDiskWCD(short value) {
		diskWCD = value;
	}
	public short getDiskWCD() {
		return diskWCD;
	}
	public void setDiskEntries(short value) {
		diskEntries = value;
	}
	public short getDiskEntries() {
		return diskEntries;
	}
	public void setTotalEntries(short value) {
		totalEntries = value;
	}
	public short getTotalEntries() {
		return totalEntries;
	}
	public void setCentralDirectorySize(int value) {
		centralDirectorySize = value;
	}
	public int getCentralDirectorySize() {
		return centralDirectorySize;
	}
	public void setOffsetCD(int value) {
		offsetCD = value;
	}
	public int getOffsetCD() {
		return offsetCD;
	}
	public void setZipFileComment(String value) {
		zipFileComment = value;
	}
	public String getZipFileComment() {
		return zipFileComment;
	}
	public short getCommentLen() {
		return (short)zipFileComment.length();
	}
}

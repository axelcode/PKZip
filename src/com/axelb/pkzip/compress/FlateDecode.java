/*
 * ==========================================================================
 * class name  : com.axelb.pkzip.compress.FlateDecode
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
package com.axelb.pkzip.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.InflaterInputStream;

public abstract class FlateDecode {
	public synchronized static byte[] encode(final byte[] bStream) {
		byte[] bStreamTemp = new byte[bStream.length];
		ByteArrayOutputStream bStreamCompress = new ByteArrayOutputStream();
		
		Deflater compresser = new Deflater();
		compresser.setInput(bStream);
		compresser.finish();
		
		int lenCompressTotale = 0;
		while(!compresser.finished()) {
			int lenCompress = compresser.deflate(bStreamTemp);
			
			bStreamCompress.write(bStreamTemp,0,lenCompress);
			lenCompressTotale = lenCompressTotale + lenCompress;
		}
		
		/* DA NOTARE:
		 * La classe Deflater inserisce 2 byte in testa che danno "fastidio"
		 * nella decompressione del file da parte di WinRar.
		 * Nella scrittura dello stream, quindi, procedo ad eliminare questi byte.
		 * Credo dipenda dal fatto che lo stream codificato esce in UTF/8,
		 * quindi l'header risulta illeggibile.
		 * 
		 * Lo step successivo sarà rappresentato dalla riscrittura dell'algoritmo Deflate
		 */
		ByteArrayOutputStream bStreamCompressRet = new ByteArrayOutputStream();
		bStreamCompressRet.write(bStreamCompress.toByteArray(),2,bStreamCompress.size()-6);
		return bStreamCompressRet.toByteArray();
		
		//return bStreamCompress.toByteArray();
	}
	
	public synchronized static byte[] decode(byte[] in, final boolean strict) {
        byte[] inStream = new byte[in.length + 2];
      
        inStream[0] = 120;
        inStream[1] = -100;
         
		for(int i = 0;i < in.length;i++) {
			inStream[2+i] = in[i];
		}
		ByteArrayInputStream stream = new ByteArrayInputStream(inStream);
        InflaterInputStream zip = new InflaterInputStream(stream);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte b[] = new byte[strict ? 4092 : 1];
        
        try {
            int n;
            
            while ((n = zip.read(b)) >= 0) {
            	out.write(b, 0, n);
            }
            zip.close();
            out.close();
            return out.toByteArray();
        }
        catch (Exception e) {
        	if (strict)
                return null;
            return out.toByteArray();
        }
    }
		 
}

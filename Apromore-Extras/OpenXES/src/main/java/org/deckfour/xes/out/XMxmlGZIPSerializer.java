/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2009 Christian W. Guenther (christian@deckfour.org)
 * 
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 * EXEMPTION:
 * 
 * The use of this software can also be conditionally licensed for
 * other programs, which do not satisfy the specified conditions. This
 * requires an exemption from the general license, which may be
 * granted on a per-case basis.
 * 
 * If you want to license the use of this software with a program
 * incompatible with the LGPL, please contact the author for an
 * exemption at the following email address: 
 * christian@deckfour.org
 * 
 */
package org.deckfour.xes.out;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import org.deckfour.xes.model.XLog;

/**
 * Compressed MXML serialization for XES data (legacy 
 * implementation).
 * Note that this serialization may be lossy, you should
 * preferrably use the XES.XML serialization for XES data.
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class XMxmlGZIPSerializer extends XMxmlSerializer {
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.out.XesSerializer#getDescription()
	 */
	public String getDescription() {
		return "Compressed MXML serialization (legacy)";
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.out.XesSerializer#getName()
	 */
	public String getName() {
		return "MXML Compressed";
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.out.XesSerializer#getAuthor()
	 */
	public String getAuthor() {
		return "Christian W. Günther";
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.out.XesSerializer#getSuffices()
	 */
	public String[] getSuffices() {
		return new String[] {"mxml.gz"};
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.out.XMxmlSerializer#serialize(org.deckfour.xes.model.XLog, java.io.OutputStream)
	 */
	@Override
	public void serialize(XLog log, OutputStream out) throws IOException {
		GZIPOutputStream gzos = new GZIPOutputStream(out);
		BufferedOutputStream bos = new BufferedOutputStream(gzos);
		super.serialize(log, bos);
		bos.flush();
		gzos.flush();
		bos.close();
		gzos.close();
	}
	
	/**
	 * toString() defaults to getName().
	 */
	public String toString() {
		return this.getName();
	}

}

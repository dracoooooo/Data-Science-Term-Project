/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010, 2011 Albert Pham <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.sk89q.mclauncher.update;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.sk89q.mclauncher.util.Util;

/**
 * Represents a single file.
 * 
 * @author sk89q
 */
public class SingleFile extends PackageFile {
    /**
     * Construct.
     * 
     * @param url source URL
     * @param tempFile temporary file
     * @param file file
     * @param totalEstimatedSize total estimated size
     */
    public SingleFile(URL url, File tempFile, File file, long totalEstimatedSize) {
        super(url, tempFile, file, totalEstimatedSize);
    }

    /**
     * Deploy.
     */
    @Override
    public void deploy(UninstallLog log) throws IOException {
        log.add(getFile(), getFile());
        
        if (isFiltered()) {
            InputStream in = getInputStream();
            
            BufferedOutputStream out = null;
            try {
                out = new BufferedOutputStream(
                        new FileOutputStream(getFile()));
    
                byte[] buffer = new byte[16384];
    
                int ret = in.read(buffer);
                while (ret >= 1) {
                    out.write(buffer, 0, ret);
                    ret = in.read(buffer);
                }
            } finally {
                Util.close(in);
                Util.close(out);
                getTempFile().delete();
            }
        } else {
            getFile().delete();
            getTempFile().renameTo(getFile());
        }
    }

    /**
     * Verify.
     */
    @Override
    public void verify(SignatureVerifier verifier) throws SecurityException,
            IOException {
        verifier.verify(getInputStream(), Util.getExtension(getOriginalFilename()));
    }
}

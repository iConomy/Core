package com.iConomy.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Easy File Management Class
 *
 * @copyright Copyright AniGaiku LLC (C) 2010-2011
 * @author          Nijikokun <nijikokun@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public final class FileManager {

    private String directory = "";
    private String file = "";
    private String source = "";
    private LinkedList<String> lines = new LinkedList<String>();

    public FileManager(String directory, String file, boolean create) {
        this.directory = directory;
        this.file = file;

        if (create) {
            this.existsCreate();
        }
    }

    public String getSource() {
        return source;
    }

    public LinkedList<String> getLines() {
        return lines;
    }

    public String getDirectory() {
        return directory;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setFile(String file, boolean create) {
        this.file = file;

        if (create) {
            this.create();
        }
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void setDirectory(String directory, boolean create) {
        this.directory = directory;

        if (create) {
            this.createDirectory();
        }
    }

    private void log(Level level, Object message) {
        Logger.getLogger("FileManager").log(level, null, message);
    }

    public boolean exists() {
        return this.exists(this.directory, this.file);
    }

    public boolean exists(String file) {
        return this.exists(this.directory, file);
    }

    public boolean exists(String directory, String file) {
        return (new File(directory, file)).exists();
    }

    public void existsCreate() {
        this.existsCreate(this.directory, this.file);
    }

    public void existsCreate(String directory, String file) {
        if (!((new File(directory).exists()))) {
            if (!((new File(directory, file)).exists())) {
                this.create(directory, file);
            } else {
                this.createDirectory(directory);
            }
        }
    }

    public boolean delete() {
        return new File(directory, file).delete();
    }

    public boolean create() {
        return this.create(this.directory, this.file);
    }

    public boolean create(String directory, String file) {
        if ((new File(directory)).mkdir()) {
            try {
                if (new File(directory, file).createNewFile()) {
                    return true;
                }
            } catch (IOException ex) {
                this.log(Level.SEVERE, ex);
            }
        }

        return false;
    }

    public boolean createDirectory() {
        return this.createDirectory(this.directory);
    }

    public boolean createDirectory(String directory) {
        if ((new File(directory)).mkdir()) {
            return true;
        }

        return false;
    }

    public boolean append(String data) {
        return this.append(this.directory, this.file, new String[]{data});
    }

    public boolean append(String[] lines) {
        return this.append(this.directory, this.file, lines);
    }

    public boolean append(String file, String data) {
        return this.append(this.directory, file, new String[]{data});
    }

    public boolean append(String file, String[] lines) {
        return this.append(this.directory, file, lines);
    }

    public boolean append(String directory, String file, String data) {
        return this.append(directory, file, new String[]{data});
    }

    public boolean append(String directory, String file, String[] lines) {
        BufferedWriter output;

        this.existsCreate(directory, file);

        try {
            output = new BufferedWriter(new FileWriter(new File(directory, file)));

            try {
                for (String line : lines) {
                    output.write(line);
                    output.newLine();
                }
            } catch (IOException ex) {
                this.log(Level.SEVERE, ex);
                output.close();
                return false;
            }

            output.close();
            return true;
        } catch (FileNotFoundException ex) {
            this.log(Level.SEVERE, ex);
        } catch (IOException ex) {
            this.log(Level.SEVERE, ex);
        }

        return false;
    }

    public boolean read() {
        return this.read(this.directory, this.file);
    }

    public boolean read(String file) {
        return this.read(this.directory, file);
    }

    public boolean read(String directory, String file) {
        BufferedReader input;
        String line;

        try {
            input = new BufferedReader(new FileReader(new File(directory, file)));

            try {
                this.source = input.readLine();

                while ((line = input.readLine()) != null) {
                    this.lines.add(line);
                }
            } catch (IOException ex) {
                this.log(Level.SEVERE, ex);

                return false;
            }

            return true;
        } catch (FileNotFoundException ex) {
            this.log(Level.SEVERE, ex);
        }

        return false;
    }

    public boolean write(Object data) {
        return this.write(this.directory, this.file, new Object[]{data});
    }

    public boolean write(Object[] lines) {
        return this.write(this.directory, this.file, lines);
    }

    public boolean write(String file, Object data) {
        return this.write(this.directory, file, new Object[]{data});
    }

    public boolean write(String file, String[] lines) {
        return this.write(this.directory, file, lines);
    }

    public boolean write(String directory, String file, Object data) {
        return this.write(directory, file, new Object[]{data});
    }

    public boolean write(String directory, String file, Object[] lines) {
        BufferedWriter output;

        this.existsCreate(directory, file);

        try {
            output = new BufferedWriter(new FileWriter(new File(directory, file)));

            try {
                for (Object line : lines) {
                    output.write(String.valueOf(line));
                }
            } catch (IOException ex) {
                this.log(Level.SEVERE, ex);
                output.close();
                return false;
            }

            output.close();
            return true;
        } catch (FileNotFoundException ex) {
            this.log(Level.SEVERE, ex);
        } catch (IOException ex) {
            this.log(Level.SEVERE, ex);
        }

        return false;
    }
}

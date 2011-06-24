package com.mini.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;

import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
    Copyright (c) 2011, Nijiko Yonskai (@nijikokun) <nijikokun@gmail.com>
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

        1. Redistributions of source code must retain the above copyright
            notice, this list of conditions and the following disclaimer.

        2. Redistributions in binary form must reproduce the above copyright
            notice, this list of conditions and the following disclaimer in the
            documentation and/or other materials provided with the distribution.

        3. Neither the name of Nijiko Yonskai nor the
            names of its contributors may be used to endorse or promote products
            derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
    ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
    WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
    DISCLAIMED. IN NO EVENT SHALL Nijiko Yonskai BE LIABLE FOR ANY
    DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
    (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
    LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
    ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
    SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * FileManager
 * An actual File Management class for reading, writing, deleting, and creating files.
 * Because, Java sucks at actually doing anything.
 *
 * @author Nijikokun (@nijikokun) <nijikokun@gmail.com>
 */
public final class Manager {

    private String directory = "";
    private String file = "";
    private String source = "";
    private LinkedList<String> lines = new LinkedList<String>();

    public Manager(String directory, String file, boolean create) {
        this.directory = directory;
        this.file = file;

        if (create)
            this.existsCreate();
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
        Logger.getLogger("FileManager").log(level, String.valueOf(message));
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
        } else {
            if (!((new File(directory, file)).exists())) {
                this.create(directory, file);
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
        try {
            (new File(directory)).mkdirs();
            (new File(directory, file)).createNewFile();
        } catch (IOException ex) {
            this.log(Level.SEVERE, ex);
            return false;
        }

        return true;
    }

    public boolean createDirectory() {
        return this.createDirectory(this.directory);
    }

    public boolean createDirectory(String directory) {
        return (new File(directory)).mkdirs();
    }

    public boolean append(String data) {
        return this.append(this.directory, this.file, new String[]{ data });
    }

    public boolean append(String[] lines) {
        return this.append(this.directory, this.file, lines);
    }

    public boolean append(String file, String data) {
        return this.append(this.directory, file, new String[]{ data });
    }

    public boolean append(String file, String[] lines) {
        return this.append(this.directory, file, lines);
    }

    public boolean append(String directory, String file, String data) {
        return this.append(directory, file, new String[]{data});
    }

    public boolean append(String directory, String file, String[] lines) {
        BufferedWriter output;
        String line;

        this.existsCreate(directory, file);

        try {
            output = new BufferedWriter(new FileWriter(new File(directory, file), true));

            try {
                for (String append : lines) {
                    output.write(append);
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
        this.lines = new LinkedList<String>();
        this.source = "";
        BufferedReader input;
        String line;

        try {
            input = new BufferedReader(new FileReader(new File(directory, file)));

            try {
                while ((line = input.readLine()) != null) {
                    this.lines.add(line);
                    this.source += line + '\n';
                }

                input.close();
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
                    output.newLine();
                }

                output.close();
            } catch (IOException ex) {
                this.log(Level.SEVERE, ex);
                output.close();
                return false;
            }

            return true;
        } catch (FileNotFoundException ex) {
            this.log(Level.SEVERE, ex);
        } catch (IOException ex) {
            this.log(Level.SEVERE, ex);
        }

        return false;
    }

    public boolean remove(Object line) {
        return this.remove(this.directory, this.file, new Object[] { line });
    }

    public boolean remove(Object[] lines) {
        return this.remove(this.directory, this.file, lines);
    }

    public boolean remove(String file, Object[] lines) {
        return this.remove(this.directory, file, lines);
    }

    public boolean remove(String directory, String file, Object line) {
        return this.remove(directory, file, new Object[]{ line });
    }

    public boolean remove(String directory, String file, Object[] lines) {
        BufferedWriter writer;
        this.existsCreate(directory, file);
        this.read(directory, file);

        File input = new File(directory, file);

        try {
            writer = new BufferedWriter(new FileWriter(input));

            try {
                for(String current: this.lines) {
                    boolean found = false;

                    for(Object line: lines)
                        if(current.equals(String.valueOf(line))) found = true;

                    if(!found) {
                        writer.write(current);
                        writer.newLine();
                    }
                }

                writer.close();
            } catch(IOException e) {
                writer.close();
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public void removeDuplicates() {
        removeDupilcates(directory, file);
    }

    public void removeDuplicates(String file) {
        removeDupilcates(directory, file);
    }

    public void removeDupilcates(String directory, String file) {
        Set<String> uniqueLines = new LinkedHashSet<String>();
        this.existsCreate(directory, file);
        File input = new File(directory, file);
        BufferedWriter writer;
        BufferedReader reader;
        String line;

        try {
            reader = new BufferedReader(new FileReader(input));

            try {
                while ((line = reader.readLine()) != null) {
                    uniqueLines.add(line);
                }

                reader.close();
            } catch(IOException e) {
                reader.close();
                return;
            }

            writer = new BufferedWriter(new FileWriter(input));

            try {
                for(String current: uniqueLines) {
                    writer.write(current);
                    writer.newLine();
                }

                writer.close();
            } catch(IOException e) {
                writer.close();
                return;
            }
        } catch (Exception e) {
            System.out.println(e);
            return;
        }
        return;
    }
}

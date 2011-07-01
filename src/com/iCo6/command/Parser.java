package com.iCo6.command;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Parser {
    // Linking those hashies.
    private LinkedHashMap<String, String[]> commands;
    private LinkedHashMap<String, Handler> handlers;
    private LinkedHashMap<String, Object> arguments;
    private LinkedHashMap<String, String[]> help;
    private LinkedHashMap<String, String> permissions;

    // General String data.
    private String message;
    private String[] message_split;
    private ArrayList<String> command_list;

    // Key Map
    private final String BASE = "/";
    private final String ENDVARIABLE = "~";
    private final String VARIABLE = "+";
    private final String COMMAND = "-";
    private final String FCSPLIT = "|";
    private final String CSPLIT = "\\|";
    private final String SPLIT = " ";
    private final String DVARIABLE = "\\:";

    // Mapping
    private Object[] mapping;

    // Found which
    public String found;

    public Parser() {
        this.commands = new LinkedHashMap<String, String[]>();
        this.handlers = new LinkedHashMap<String, Handler>();
        this.arguments = new LinkedHashMap<String, Object>();
        this.help = new LinkedHashMap<String, String[]>();
        this.permissions = new LinkedHashMap<String, String>();
        this.command_list = new ArrayList<String>();
        this.mapping = new Object[]{};
    }

    public String[] getCommands() {
        return command_list.toArray(new String[]{});
    }

    public void add(String Command, Handler Handler) {
        String base = Command.split(SPLIT)[0];
        this.commands.put(Command, Command.split(SPLIT));

        if(!this.handlers.containsKey(base.substring(1, base.length())))
            this.handlers.put(base.substring(1, base.length()), Handler);

        this.handlers.put(Command, Handler);
        this.command_list.add(base.substring(1, base.length()).toLowerCase());
    }

    public void setHelp(String command, String[] help) {
        this.help.put(command, help);
    }

    public boolean hasHelp(String command) {
        return this.help.containsKey(command);
    }

    public LinkedHashMap<String, String[]> getHelp() {
        return this.help;
    }

    public String[] getHelp(String command) {
        return this.help.get(command);
    }

    public void setPermission(String Command, String level) {
        this.permissions.put(Command, level);
    }

    public Handler getHandler() {
        if(this.found.isEmpty()) return null;

        return this.handlers.get(this.found);
    }

    public Handler getHandler(String Command) {
        return this.handlers.get(Command.toLowerCase());
    }

    public boolean hasPermission(String Command) {
        return this.permissions.containsKey(Command);
    }

    public String getPermission(String Command) {
        return this.permissions.get(Command);
    }

    public void save(String message) {
        this.arguments = new LinkedHashMap<String, Object>();
        this.mapping = new Object[]{};

        this.message = message;
        this.message_split = message.split(SPLIT);
    }

    public String base() {
        if (message_split.length < 0) return null;
        for (String command : commands.keySet()) {
            ArrayList<Object> container = new ArrayList<Object>();
            Object[] objects = new Object[]{};
            String[] command_split = commands.get(command);
            int location = 0;

            if (command_split.length < 0) continue;
            for (String section : command_split) {
                String symbol = section.substring(0, 1);
                String variable = section.substring(1, section.length());
                boolean split = variable.contains(FCSPLIT);

                if (section.startsWith(BASE)) {
                    if (split) {
                        for (String against : variable.split(CSPLIT))
                            if ((symbol + against).equalsIgnoreCase(message_split[location])) return against;

                        break;
                    } else {
                        if (section.equalsIgnoreCase(message_split[location])) return variable;
                        break;
                    }
                }
            }
        }

        return null;
    }

    public String command() {
        if (message_split.length < 0) return null;
        for (String command : commands.keySet()) {
            ArrayList<Object> container = new ArrayList<Object>();
            Object[] objects = new Object[]{};
            String[] command_split = commands.get(command);
            int location = 0;

            if (command_split.length < 0) continue;
            for (String section : command_split) {
                String symbol = section.substring(0, 1);
                String variable = section.substring(1, section.length());
                boolean split = variable.contains(FCSPLIT);

                if (symbol.equals(COMMAND)) {
                    if (message_split.length <= location) break;
                    if (split) {
                        for (String against : variable.split(CSPLIT))
                            if (against.equalsIgnoreCase(message_split[location]) || (symbol + against).equalsIgnoreCase(message_split[location]))
                                return against;

                        break;
                    } else {
                        if (variable.equalsIgnoreCase(message_split[location]) || section.equalsIgnoreCase(message_split[location]))
                            return variable;

                        break;
                    }
                }

                location++;
            }
        }

        return null;
    }

    public ArrayList<Object> parse() {
        this.found = "";

        if (message_split.length < 0) {
            return new ArrayList<Object>();
        }

        for (String command : commands.keySet()) {
            ArrayList<Object> container = new ArrayList<Object>();
            String[] command_split = commands.get(command);
            boolean foundCommand = false;
            int location = 0;

            if (command_split.length < 0) continue;
            for (String section : command_split) {
                String[] variables = new String[]{};
                String symbol = section.substring(0, 1);
                String variable = section.substring(1, section.length());
                boolean split = variable.contains(FCSPLIT);
                boolean found = false;

                if (section.startsWith(BASE) || section.startsWith(COMMAND)) {
                    if (message_split.length <= location) break;

                    if (split) {
                        for (String against : variable.split(CSPLIT)) {
                            if ((section.startsWith(COMMAND) ? against : symbol + against).equalsIgnoreCase(message_split[location])
                                    || (section.startsWith(COMMAND) ? symbol + against : symbol + against).equalsIgnoreCase(message_split[location])) {
                                found = true;

                                if(section.startsWith(COMMAND)) {
                                    this.found = command;
                                    foundCommand = true;
                                }
                                break;
                            }
                        }
                    } else {
                        if ((section.startsWith(COMMAND) ? variable : section).equalsIgnoreCase(message_split[location])
                                || (section.startsWith(COMMAND) ? section : section).equalsIgnoreCase(message_split[location])) {
                            found = true;

                            if(section.startsWith(COMMAND)) {
                                this.found = command;
                                foundCommand = true;
                            }
                        }
                    }

                    if(!found) break;
                }

                if (section.startsWith(VARIABLE)) {
                    if (message_split.length <= location) {
                        if (variable.contains(":")) {
                            variables = variable.split(DVARIABLE);

                            if (variables.length > 0) {
                                arguments.put(variables[0], variables[1]);
                            } else {
                                arguments.put(variable, 0);
                            }
                        } else {
                            arguments.put(variable, 0);
                        }
                    } else {
                        if (variable.contains(":")) {
                            variables = variable.split(DVARIABLE);

                            if (variables.length > 0) {
                                arguments.put(variables[0], message_split[location]);
                            } else {
                                arguments.put(variable, message_split[location]);
                            }
                        } else {
                            arguments.put(variable, message_split[location]);
                        }
                    }
                }

                if (section.startsWith(ENDVARIABLE)) {
                    if (message_split.length <= location) {
                        if (variable.contains(":")) {
                            variables = variable.split(DVARIABLE);

                            if (variables.length > 0) {
                                arguments.put(variables[0], variables[1]);
                            } else {
                                arguments.put(variable, 0);
                            }
                        } else {
                            arguments.put(variable, 0);
                        }
                    } else {
                        if (variable.contains(":")) {
                            variables = variable.split(DVARIABLE);

                            if (variables.length > 0) {
                                arguments.put(variables[0], combine(location, message_split, " "));
                            } else {
                                arguments.put(variable, combine(location, message_split, " "));
                            }
                        } else {
                            arguments.put(variable, combine(location, message_split, " "));
                        }
                    }
                }

                ++location;
            }

            if (container.size() > 0) return null;
            if (foundCommand) break;
        }

        return new ArrayList(arguments.values());
    }

    public LinkedHashMap<String, Argument> getArguments() {
        if(this.arguments == null) return null;

        LinkedHashMap<String, Argument> Arguments = new LinkedHashMap<String, Argument>();
        for(String key: this.arguments.keySet())
            Arguments.put(key, new Argument(key, this.arguments.get(key)));

        return Arguments;
    }

    public Object getValue(String argument) {
        if(this.arguments == null) return null;
        return (this.arguments.containsKey(argument)) ? this.arguments.get(argument) : null;
    }

    public String getString(String argument) {
        if(this.arguments == null) return null;
        return (this.arguments.containsKey(argument)) ? String.valueOf(this.arguments.get(argument)) : null;
    }

    public int getInteger(String argument) {
        if (this.arguments == null) return 0;
        int value = 0;

        try {
            value = (this.arguments.containsKey(argument)) ? Integer.valueOf(String.valueOf(this.arguments.get(argument))) : 0;
        } catch (NumberFormatException ex) { }

        return value;
    }

    public boolean getBoolean(String argument) {
        if (this.arguments == null) return false;
        return (this.arguments.containsKey(argument)) ? Boolean.parseBoolean(String.valueOf(this.arguments.get(argument))) : false;
    }

    public class InvalidSyntaxException extends Exception {
        public InvalidSyntaxException(String message) {
            super(message);
        }
    }

    private String combine(int startIndex, String[] string, String seperator) {
        StringBuilder builder = new StringBuilder();

        for (int i = startIndex; i < string.length; i++) {
            builder.append(string[i]);
            builder.append(seperator);
        }

        builder.deleteCharAt(builder.length() - seperator.length()); // remove
        return builder.toString();
    }

    public class Argument {
        private String name;
        private Object value;

        public Argument(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }

        public String getStringValue() {
            return String.valueOf(value);
        }

        public Boolean getBooleanValue() {
            return Boolean.parseBoolean(String.valueOf(value));
        }

        public Double getDoubleValue() throws NumberFormatException {
            return Double.valueOf(String.valueOf(this.value));
        }

        public Integer getIntegerValue() throws NumberFormatException {
            return Integer.valueOf(String.valueOf(this.value));
        }
    }
}
package com.iCo6.util;

import com.iCo6.Constants;
import com.iCo6.iConomy;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Common {

    /**
     * Checks text against two variables, if it equals at least one returns true.
     *
     * @param text The text that we were provided with.
     * @param against The first variable that needs to be checked against
     * @param or The second variable that it could possibly be.
     *
     * @return <code>Boolean</code> - True or false based on text.
     */
    public static boolean matches(String text, String... is) {
        for (String s : is)
            if (text.equalsIgnoreCase(s))
                return true;

        return false;
    }

    public static int plural(Double amount) {
        if(amount > 1 || amount < -1)
            return 1;

        return 0;
    }

    public static int plural(Integer amount) {
        if(amount != 1 || amount != -1)
            return 1;

        return 0;
    }

    public static int plural(Long amount) {
        if(amount != 1 || amount != -1)
            return 1;

        return 0;
    }

    public static String formatted(String amount, List<String> maj, List<String> min) {
        if(Constants.Nodes.isSingle.getBoolean())
            if(amount.contains("."))
                amount = amount.split("\\.")[0];

        String formatted = "";
        String famount = amount.replace(",", "");

        if(Constants.Nodes.AllowMinor.getBoolean()) {
            String[] pieces = null;
            String[] fpieces = null;

            if(amount.contains(".")) {
                pieces = amount.split("\\.");
                fpieces = new String[] { pieces[0].replace(",", ""), pieces[1] };
            } else {
                pieces = new String[] { amount, "0" };
                fpieces = new String[] { amount.replace(",", ""), "0" };
            }

            if(Constants.Nodes.isSplit.getBoolean()) {
                String major = "", minor = "";

                try {
                    major = maj.get(plural(Integer.valueOf(fpieces[0])));
                    minor = min.get(plural(Integer.valueOf(fpieces[1])));
                } catch (NumberFormatException E) {
                    major = maj.get(plural(Long.valueOf(fpieces[0])));
                    minor = min.get(plural(Long.valueOf(fpieces[1])));
                }

                if(pieces[1].startsWith("0") && !pieces[1].equals("0")) 
                    pieces[1] = pieces[1].substring(1, pieces[1].length());

                if(pieces[0].startsWith("0") && !pieces[0].equals("0"))
                    pieces[0] = pieces[0].substring(1, pieces[0].length());

                try {
                    if(Integer.valueOf(fpieces[1]) != 0 && Integer.valueOf(fpieces[0]) != 0)
                        formatted = pieces[0] + " " + major + ", " + pieces[1] + " " + minor;
                    else if(Integer.valueOf(fpieces[0]) != 0)
                        formatted = pieces[0] + " " + major;
                    else
                        formatted = pieces[1] + " " + minor;
                } catch(NumberFormatException e) {
                    if(Long.valueOf(fpieces[1]) != 0 && Long.valueOf(fpieces[0]) != 0)
                        formatted = pieces[0] + " " + major + ", " + pieces[1] + " " + minor;
                    else if(Long.valueOf(fpieces[0]) != 0)
                        formatted = pieces[0] + " " + major;
                    else
                        formatted = pieces[1] + " " + minor;
                }
            } else {
                String currency = "";

                if(Double.valueOf(famount) < 1 || Double.valueOf(famount) > -1)
                    try {
                        currency = min.get(plural(Integer.valueOf(fpieces[1])));
                    } catch (NumberFormatException e) {
                        currency = min.get(plural(Long.valueOf(fpieces[1])));
                    }
                else
                    currency = maj.get(1);

                formatted = amount + " " + currency;
            }
        } else {
            int plural = plural(Double.valueOf(famount));
            String currency = maj.get(plural);

            formatted = amount + " " + currency;
        }

        return formatted;
    }

    public static String readableSize(long size) {
        String[] units = new String[] { "B", "KB", "MB", "GB", "TB", "PB" };
        int mod = 1024, i;

        for (i = 0; size > mod; i++)
            size /= mod;

        return Math.round(size) + " " + units[i];
    }

    public static String readableProfile(long time) {
        int i = 0;
        String[] units = new String[] { "ms", "s", "m", "hr", "day", "week", "mnth", "yr" };
        int[] metric = new int[] { 1000, 60, 60, 24, 7, 30, 12 };
        long current = TimeUnit.MILLISECONDS.convert(time, TimeUnit.NANOSECONDS);

        for(i = 0; current > metric[i]; i++)
            current /= metric[i];

        return current + " " + units[i] + ((current > 1 && i > 1) ? "s" : "");
    }

    public static void extract(String... names) {
        for(String name: names) {
            File actual = new File(iConomy.directory, name);

            if(actual.exists())
                continue;

            InputStream input = iConomy.class.getResourceAsStream("/resources/" + name);

            if(input == null)
                continue;

            FileOutputStream output = null;

            try {
                output = new FileOutputStream(actual);
                byte[] buf = new byte[8192];
                int length = 0;

                while ((length = input.read(buf)) > 0)
                    output.write(buf, 0, length);

                System.out.println("[iConomy] Default setup file written: " + name);
            } catch (Exception e) {
            } finally {
                try { if (input != null) input.close();
                } catch (Exception e) { }

                try { if (output != null) output.close();
                } catch (Exception e) { }
            }
        }
    }

    public static String resourceToString(String name) {
        InputStream input = iConomy.class.getResourceAsStream("/resources/" + name);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];

        if(input != null) {
            try {
                int n;
                Reader reader = new BufferedReader(new InputStreamReader(input));
                while ((n = reader.read(buffer)) != -1)
                    writer.write(buffer, 0, n);
            } catch (IOException e) {
                try {
                    input.close();
                } catch (IOException ex) { }
                return null;
            } finally {
                try {
                    input.close();
                } catch (IOException e) { }
            }
        } else {
            return null;
        }

        String text = writer.toString().trim();
        text = text.replace("\r\n", " ").replace("\n", " ");
        return text.trim();
    }
}

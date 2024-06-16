package com.doidea.core.filters;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class VmArgumentFilter {

    private static String agentFilePath;

    public static void setAgentFilePath(String agentFilePath) {
        VmArgumentFilter.agentFilePath = agentFilePath;
    }

    public static List<String> testArgs(List<String> vmArgs) {
        if (null == vmArgs || vmArgs.isEmpty()) return vmArgs;
        if (null == agentFilePath || agentFilePath.trim().isEmpty()) return vmArgs;

        boolean modified = false;
        List<String> list = new ArrayList<>(vmArgs);
        System.out.println(">>>> VmArgumentFilter testArgs vmArgs: " + list);

        try {
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                String arg = it.next();
                if (null == arg || arg.trim().isEmpty()) continue;
                if (arg.startsWith("-Djanf.debug=")) {
                    it.remove();
                    modified = true;
                    continue;
                }
                if (arg.startsWith("-javaagent:")) {
                    System.out.println(">>>> VmArgumentFilter javaagent arg: " + arg);
                    String[] sections = arg.substring(11).split("=", 2);
                    if (sections.length < 1) continue;
                    System.out.println(">>>> VmArgumentFilter filePath1: " + sections[0].toLowerCase());
                    System.out.println(">>>> VmArgumentFilter filePath2: " + agentFilePath.toLowerCase());
                    File f = new File(sections[0].toLowerCase());
                    File d = new File(agentFilePath.toLowerCase());
                    if (!d.equals(f)) continue;
                    it.remove();
                    modified = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return vmArgs;
        }

        return modified ? Collections.unmodifiableList(list) : vmArgs;
    }
}

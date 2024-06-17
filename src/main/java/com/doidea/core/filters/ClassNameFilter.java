package com.doidea.core.filters;

public class ClassNameFilter {

    public static void testClass(String name) throws ClassNotFoundException {
        if (null == name || name.trim().isEmpty()) return;
        if (name.toLowerCase().contains("com.doidea.")) {
            ClassNotFoundException e = new ClassNotFoundException(name);
            StackTraceElement[] elements = e.getStackTrace();
            if (elements.length > 0) {
                StackTraceElement[] newElements = new StackTraceElement[elements.length - 1];
                System.arraycopy(elements, 1, newElements, 0, newElements.length);
                e.setStackTrace(newElements);
            }
            throw e;
        }
    }

}

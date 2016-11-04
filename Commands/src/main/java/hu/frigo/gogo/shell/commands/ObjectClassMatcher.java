package hu.frigo.gogo.shell.commands;

class ObjectClassMatcher {
    private ObjectClassMatcher() {
    }

    static boolean matchesAtLeastOneName(String[] names, String pattern) {
        for (String objectClass : names) {
            if (matchesName(objectClass, pattern)) {
                return true;
            }
        }
        return false;
    }

    static boolean matchesName(String name, String pattern) {
        return name.equals(pattern) || getShortName(name).equals(pattern);
    }

    static String getShortName(String name) {
        int idx = name.lastIndexOf(".");
        if (idx + 1 > name.length()) {
            idx = 0;
        }
        return name.substring(idx + 1);
    }
}
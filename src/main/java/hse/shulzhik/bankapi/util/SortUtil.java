package hse.shulzhik.bankapi.util;

import java.util.List;

public class SortUtil {
    public static boolean CheckSort(String[] sortBy, List<String> validFieldsList) {
        for (String field : sortBy) {
            if (!validFieldsList.contains(field)) {
                return false;
            }
        }
        return true;
    }
}

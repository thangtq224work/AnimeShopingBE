package com.application.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class test {
    public static void main(String[] args) {
        System.out.println(deAccent("XIn chào các bán "));
    }
    public static String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }
}

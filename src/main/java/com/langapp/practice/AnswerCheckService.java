package com.langapp.practice;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Serbest metin ceviri cevaplarini "kabaca dogru" mantigiyla degerlendirir.
 * Tam string esitligi yerine normalize edip Levenshtein benzerlik orani hesaplar;
 * kucuk yazim farklarini (buyuk/kucuk harf, noktalama, fazla bosluk) tolere eder.
 */
@Service
public class AnswerCheckService {

    private static final double SIMILARITY_THRESHOLD = 0.85;
    private static final Pattern PUNCTUATION = Pattern.compile("[\\p{Punct}]");
    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}");

    /** Kullanicinin cevabini beklenen ceviriyle karsilastirir, dogru kabul edilirse true doner. */
    public boolean isRoughlyCorrect(String userAnswer, String expected) {
        if (userAnswer == null || expected == null) return false;
        String a = normalize(userAnswer);
        String b = normalize(expected);
        if (a.isEmpty()) return false;
        if (a.equals(b)) return true;

        double similarity = similarity(a, b);
        return similarity >= SIMILARITY_THRESHOLD;
    }

    /** 0.0-1.0 arasi benzerlik skoru dondurur (kullanicinin ne kadar yaklastigini gostermek icin de kullanilabilir). */
    public double similarity(String a, String b) {
        String x = normalize(a);
        String y = normalize(b);
        int maxLen = Math.max(x.length(), y.length());
        if (maxLen == 0) return 1.0;
        int distance = levenshtein(x, y);
        return 1.0 - ((double) distance / maxLen);
    }

    private String normalize(String s) {
        String result = s.trim().toLowerCase();
        result = Normalizer.normalize(result, Normalizer.Form.NFD);
        result = DIACRITICS.matcher(result).replaceAll("");
        result = PUNCTUATION.matcher(result).replaceAll("");
        result = result.replaceAll("\\s+", " ").trim();
        return result;
    }

    private int levenshtein(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[a.length()][b.length()];
    }
}

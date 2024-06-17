package utility;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameChecker {
    private List<Pattern> naughtyWords = new ArrayList<>();


    public NameChecker() {
        addAllNaughtyWords();
    }

    public boolean checkNickname(String nickname) {
        for (Pattern naughtyWord : naughtyWords) {
            Matcher matcher = naughtyWord.matcher(nickname);
            if (matcher.find()) {
                return false;
            }
        }

        return true;
    }

    private void addAllNaughtyWords() {
        //regex from https://github.com/mogade/badwords
        //license: https://creativecommons.org/licenses/by/3.0/
        naughtyWords.add(Pattern.compile("^[a@][s$][s$]$", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("[a@][s$][s$]h[o0][l1][e3][s$]?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("b[a@][s$][t+][a@]rd", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("b[e3][a@][s$][t+][i1][a@]?[l1]([i1][t+]y)?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("b[e3][a@][s$][t+][i1][l1][i1][t+]y", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("b[e3][s$][t+][i1][a@][l1]([i1][t+]y)?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("b[i1][t+]ch[s$]?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("b[i1][t+]ch[e3]r[s$]?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("b[i1][t+]ch[e3][s$]", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("b[i1][t+]ch[i1]ng?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("b[l1][o0]wj[o0]b[s$]?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("c[l1][i1][t+]", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("^(c|k|ck|q)[o0](c|k|ck|q)[s$]?$", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("(c|k|ck|q)[o0](c|k|ck|q)[s$]u", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("(c|k|ck|q)[o0](c|k|ck|q)[s$]u(c|k|ck|q)[e3]d", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("(c|k|ck|q)[o0](c|k|ck|q)[s$]u(c|k|ck|q)[e3]r", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("(c|k|ck|q)[o0](c|k|ck|q)[s$]u(c|k|ck|q)[i1]ng", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("(c|k|ck|q)[o0](c|k|ck|q)[s$]u(c|k|ck|q)[s$]", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("^cum[s$]?$", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("cumm??[e3]r", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("cumm?[i1]ngcock", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("(c|k|ck|q)um[s$]h[o0][t+]", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("(c|k|ck|q)un[i1][l1][i1]ngu[s$]", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("(c|k|ck|q)un[i1][l1][l1][i1]ngu[s$]", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("(c|k|ck|q)unn[i1][l1][i1]ngu[s$]", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("(c|k|ck|q)un[t+][s$]?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("(c|k|ck|q)un[t+][l1][i1](c|k|ck|q)", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("(c|k|ck|q)un[t+][l1][i1](c|k|ck|q)[e3]r", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("(c|k|ck|q)un[t+][l1][i1](c|k|ck|q)[i1]ng", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("cyb[e3]r(ph|f)u(c|k|ck|q)", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("d[a@]mn", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("d[i1]ck", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("d[i1][l1]d[o0]", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("d[i1][l1]d[o0][s$]", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("d[i1]n(c|k|ck|q)", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("d[i1]n(c|k|ck|q)[s$]", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("[e3]j[a@]cu[l1]", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("(ph|f)[a@]g[s$]?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("(ph|f)[a@]gg[i1]ng", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("(ph|f)[a@]gg?[o0][t+][s$]?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("(ph|f)[a@]gg[s$]", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("(ph|f)[e3][l1][l1]?[a@][t+][i1][o0]", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("(ph|f)u(c|k|ck|q)", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("(ph|f)u(c|k|ck|q)[s$]?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("g[a@]ngb[a@]ng[s$]?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("g[a@]ngb[a@]ng[e3]d", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("g[a@]y", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("h[o0]m?m[o0]", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("h[o0]rny", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("j[a@](c|k|ck|q)-?[o0](ph|f)(ph|f)?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("j[e3]rk-?[o0](ph|f)(ph|f)?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("j[i1][s$z][s$z]?m?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("[ck][o0]ndum[s$]?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("mast(e|ur)b(8|ait|ate)", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("n+[i1]+[gq]+[e3]*r+[s$]*", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("[o0]rg[a@][s$][i1]m[s$]?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("[o0]rg[a@][s$]m[s$]?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("p[e3]nn?[i1][s$]", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("p[i1][s$][s$]", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("p[i1][s$][s$][o0](ph|f)(ph|f)", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("p[o0]rn", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("p[o0]rn[o0][s$]?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("p[o0]rn[o0]gr[a@]phy", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("pr[i1]ck[s$]?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("pu[s$][s$][i1][e3][s$]", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("pu[s$][s$]y[s$]?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("[s$][e3]x", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("[s$]h[i1][t+][s$]?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("[s$][l1]u[t+][s$]?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("[s$]mu[t+][s$]?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("[s$]punk[s$]?", Pattern.CASE_INSENSITIVE));
        naughtyWords.add(Pattern.compile("[t+]w[a@][t+][s$]?", Pattern.CASE_INSENSITIVE));
    }
}

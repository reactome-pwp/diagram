package org.reactome.web.diagram.search.autocomplete;

import com.google.gwt.regexp.shared.RegExp;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class AutoCompleteResult {

    private String result = null;

    private String displayResult = null;

    public AutoCompleteResult(String term, String result) {
        this.result = result;

        String[] terms = { term }; //TODO for now only one term is used
        setResultDisplay(terms);
    }

    public String getResult() {
        return result;
    }

    public String getDisplayResult() {
        return displayResult;
    }

    private void setResultDisplay(String[] searchTerms) {
        this.displayResult = result;

        if (searchTerms == null || searchTerms.length == 0) return;

        StringBuilder sb = new StringBuilder("(");
        for (String term : searchTerms) {
            sb.append(term).append("|");
        }
        sb.delete(sb.length() - 1, sb.length()).append(")");
        String term = sb.toString();
        /**
         * (term1|term2)    : term is between "(" and ")" because we are creating a group, so this group can
         *                    be referred later.
         * gi               : global search and case insensitive
         * <b><u>$1</u></b> : instead of replacing by input, that would change the case, we replace it by $1,
         *                    that is the reference to the first matched group. This means that we want to
         *                    replace it using the exact word that was found.
         */
        RegExp regExp = RegExp.compile(term, "gi");
        this.displayResult = regExp.replace(this.displayResult, "<u style=\"color:#1e94d0\"><strong>$1</strong></u>");
    }
}

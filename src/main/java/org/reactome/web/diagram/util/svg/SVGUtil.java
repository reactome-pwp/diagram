package org.reactome.web.diagram.util.svg;

import com.google.gwt.regexp.shared.RegExp;
import org.vectomatic.dom.svg.OMElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public abstract class SVGUtil {

    private static final String STID_PATTERN = "R-[A-Z]{3}-[0-9]{3,}(\\.[0-9]+)?";
    private static RegExp regExp = RegExp.compile(STID_PATTERN);

    public static List<OMElement> getAnnotatedOMElements(OMSVGSVGElement svg) {
        List<OMElement> rtn = new ArrayList<>();
        for (OMElement child : svg.getElementsByTagName(new OMSVGGElement().getTagName())) {
            if(isAnnotated(child.getId())) {
                rtn.add(child);
            }
        }
        return rtn;
    }

    public static boolean isAnnotated(String input) {
        return input != null && regExp.test(input);
    }

    public static String keepStableId(String identifier) {
        String rtn = null;
        if(identifier!=null && !identifier.isEmpty()) {
            rtn = identifier.substring(identifier.indexOf('-') + 1);
        }
        return rtn;
    }
}

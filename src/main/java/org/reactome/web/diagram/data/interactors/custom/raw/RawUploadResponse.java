package org.reactome.web.diagram.data.interactors.custom.raw;

import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public interface RawUploadResponse {

    RawToken getToken();

    List<String> getWarningMessages();

    List<String> getErrorMessages();

}

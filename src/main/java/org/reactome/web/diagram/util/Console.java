package org.reactome.web.diagram.util;

import com.google.gwt.core.client.GWT;
import org.reactome.web.diagram.client.DiagramFactory;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class Console {

    private static final boolean IS_SCRIPT = GWT.isScript();

    private static native void _error(String message)/*-{
        if($wnd.console){
            $wnd.console.error(message);
        }
    }-*/;

    public static void error(String msg, Object source){
        error(source.getClass().getSimpleName() + " >> " + msg);
    }

    public static void error(String msg){
        if(!DiagramFactory.CONSOLE_VERBOSE) return;
        if(IS_SCRIPT){
            Console._error(msg);
        }else{
            System.err.println(msg);
        }
    }

    private static native void _info(String message)/*-{
        if($wnd.console){
            $wnd.console.info(message);
        }
    }-*/;

    public static void info(String msg, Object source){
        info(source.getClass().getSimpleName() + " >> " + msg);
    }
    
    public static void info(String msg){
        if(!DiagramFactory.CONSOLE_VERBOSE) return;
        if(IS_SCRIPT){
            Console._info(msg);
        }else{
            System.out.println(msg);
        }
    }

    private static native void _log(String message)/*-{
        if($wnd.console){
            $wnd.console.log(message);
        }
    }-*/;

    public static void log(String msg, Object source){
        log(source.getClass().getSimpleName() + " >> " + msg);
    }
    
    public static void log(String msg){
        if(!DiagramFactory.CONSOLE_VERBOSE) return;
        if(IS_SCRIPT){
            Console._log(msg);
        }else{
            System.out.println(msg);
        }
    }

    private static native void _warn(String message)/*-{
        if($wnd.console){
            $wnd.console.warn(message)
        }
    }-*/;

    public static void warn(String msg, Object source){
        warn(source.getClass().getSimpleName() + " >> " + msg);
    }

    
    public static void warn(String msg){
        if(!DiagramFactory.CONSOLE_VERBOSE) return;
        if(IS_SCRIPT){
            Console._warn(msg);
        }else{
            System.out.println("! "  + msg);
        }
    }
}

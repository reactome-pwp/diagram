package org.reactome.web.test;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.client.DiagramViewer;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;
import org.reactome.web.diagram.util.Console;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WidgetTest implements EntryPoint {

    private final DiagramViewer diagram;
    private static String currentPathway = "R-HSA-5638302"; //"R-HSA-975634"; //R-HSA-109582";
//    private static String currentPathway = "R-HSA-5693567"; //Big one with plenty of overlap
    private static String currentAnalysis = "MjAxNjA5MzAwNTU3MjdfMg%3D%3D";

    private TextBox pathwayTB;
    private TextBox analysisTokenTB;

    public WidgetTest() {
//        DiagramFactory.SERVER = "fakeserver.com";
        DiagramFactory.CONSOLE_VERBOSE = true;
        DiagramFactory.EVENT_BUS_VERBOSE = true;
//      DiagramFactory.SHOW_INFO = true;
        diagram = DiagramFactory.createDiagramViewer();
    }

    @Override
    public void onModuleLoad() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                initialise();                 // For normal testing
//                initialiseInScrollablePage();   // For testing in a long page
                Console.info("");
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        diagram.loadDiagram(currentPathway);
                        if (pathwayTB!=null) {
                            pathwayTB.setValue(currentPathway);
                        }
                    }
                });
                diagram.addDiagramLoadedHandler(new ContentLoadedHandler() {
                    @Override
                    public void onContentLoaded(ContentLoadedEvent event) {
//                        diagram.flagItems("NODAL");
                        diagram.flagItems("R-HSA-179837");
                    }
                });
            }
        });
    }

    public void initialise() {
        SplitLayoutPanel slp = new SplitLayoutPanel(10);
        slp.addEast(getDemoLeftPanel(), 83);
        slp.addNorth(getDemoTopPanel(), 25);
//        slp.addNorth(getDiseasePanel(), 50);
        slp.add(diagram);
        RootLayoutPanel.get().add(slp);
    }

    private void initialiseInScrollablePage() {
        SimpleLayoutPanel diagramContainer = new SimpleLayoutPanel();
        diagramContainer.getElement().getStyle().setHeight(500, Style.Unit.PX);
        diagramContainer.getElement().getStyle().setWidth(700, Style.Unit.PX);
        diagramContainer.getElement().getStyle().setBackgroundColor("white");
        diagramContainer.add(diagram);

        final Element element = Document.get().getElementById("container");
        HTMLPanel container = HTMLPanel.wrap(element);
        container.add(diagramContainer);
    }

    Button getSelectionButton(final String stId, String title){
        Button button = new Button(stId,  new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.selectItem(stId);
            }
        });
        button.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                diagram.highlightItem(stId);
            }
        });
        button.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                diagram.resetHighlight();
            }
        });
        button.setTitle(title);
        return button;
    }

    private Widget getDemoLeftPanel(){
        FlowPanel fp = new FlowPanel();

        fp.add(new Label("R-HSA-1181150"));
        fp.add(new Label("Reactions"));
        fp.add(getSelectionButton("R-HSA-75153", "Cleavage of NODAL proprotein"));
        fp.add(getSelectionButton("R-HSA-5357769", "Phospho R-SMAD(SMAD2/3):CO-SMAD(SMAD4):FOXO3 binds FoxO3a-binding elements"));

        fp.add(new Label("--"));
        fp.add(new Label("Sets"));
        fp.add(getSelectionButton("R-HSA-171172", "SMAD2/3 [cytosol]"));
        fp.add(getSelectionButton("R-HSA-171182", "p-2S-SMAD2/3 [cytosol]"));

        fp.add(new Label("--"));
        fp.add(new Label("Complexes"));
        fp.add(getSelectionButton("R-HSA-1225883", "NODAL:p-ACVR1B:ACVR2:EGF-CFC [plasma membrane]"));
        fp.add(getSelectionButton("R-HSA-173511", "p-2S-SMAD2/3:SMAD4 [nucleoplasm]"));

        fp.add(new Label("--"));
        fp.add(new Label("Protein"));
        fp.add(getSelectionButton("R-HSA-1181114", "NODAL(27-347) [extracellular region]"));
        fp.add(getSelectionButton("R-HSA-1225914", "FOXH1 [nucleoplasm]"));
        fp.add(getSelectionButton("R-HSA-1181326", "LEFTY1"));

        fp.add(new Label("--"));
        fp.add(new Label("Chemical"));
        fp.add(getSelectionButton("R-ALL-113592", "ATP [cytosol]"));
        fp.add(getSelectionButton("R-ALL-29370", "ADP [cytosol]"));


        fp.add(new Label(""));
        fp.add(new Label(""));
        fp.add(new Label("--"));
        fp.add(new Label("ORA"));
        fp.add(new Button("ORA 1", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
//                No interactors: MjAxNzAxMzEwNTEyMDJfMg==
//                Interactors: MjAxNzAyMDcwOTMwMDVfMw==
                diagram.setAnalysisToken("MjAxNzAxMzEwNTEyMDJfMg==","TOTAL");
            }
        }));


        fp.add(new Label(""));
        fp.add(new Label(""));
        fp.add(new Label("--"));
        fp.add(new Label("Expression"));
        fp.add(new Button("Exp 1", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.setAnalysisToken("MjAxODAyMjEwOTQxMjhfMTA%253D","TOTAL");
            }
        }));

        fp.add(getSelectionButton("R-HSA-111465", ""));

        return fp;
    }

    Button getLoadButton(final String stId, String title){
        Button button;
        button = new Button(stId, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                currentPathway = stId;
                diagram.loadDiagram(currentPathway);
                pathwayTB.setValue(currentPathway);
            }
        });
        button.setTitle(title);
        return button;
    }

    private Widget getDemoTopPanel(){
        FlowPanel fp = new FlowPanel();
        fp.add(getPathwaySelectionPanel());
        fp.add(getAnalysisSelectionPanel());
        fp.add(getLoadButton("R-HSA-1181150", ""));
        fp.add(getLoadButton("R-HSA-2990846", ""));
        fp.add(getLoadButton("R-HSA-163841", ""));
        fp.add(getLoadButton("R-HSA-75153", ""));
        fp.add(getLoadButton("R-HSA-71291", ""));
        fp.add(new InlineLabel(" "));
        fp.add(getLoadButton("R-HSA-162909", ""));
        fp.add(new InlineLabel(" "));
        fp.add(getLoadButton("R-HSA-170834", ""));
        fp.add(getLoadButton("R-HSA-3642279", ""));
        fp.add(getLoadButton("R-HSA-3645790", ""));
        fp.add(new InlineLabel(" "));
        fp.add(getLoadButton("R-HSA-109581", "Apoptosis"));
        fp.add(getLoadButton("R-HSA-109582", "Hemostasis"));
        fp.add(new InlineLabel(" "));
        fp.add(getLoadButton("R-HSA-5637815", "Signaling by Ligand-Responsive EGFR Variants in Cancer"));
        fp.add(getLoadButton("R-HSA-2219530", "Constitutive Signaling by Aberrant PI3K in Cancer"));
//        fp.add(getLoadButton("R-HSA-5362768", ""));
        fp.add(getLoadButton("R-HSA-5579009", ""));
        fp.add(getLoadButton("R-HSA-975871", ""));
        fp.add(getLoadButton("R-HSA-5210891", ""));
        fp.add(getLoadButton("R-HSA-5467345", ""));
        fp.add(getLoadButton("R-HSA-166658", ""));
        fp.add(getLoadButton("R-HSA-391160", ""));
        fp.add(getLoadButton("R-HSA-5693567", ""));
        fp.add(getLoadButton("R-HSA-5205647", ""));
        fp.add(getLoadButton("R-PFA-75153", ""));

        return fp;
    }

    private Widget getPathwaySelectionPanel() {
        FlowPanel fp = new FlowPanel();
        pathwayTB = new TextBox();
        fp.add(new InlineLabel("Enter a pathway: "));
        fp.add(pathwayTB);
        fp.add(new Button("GO", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                currentPathway = pathwayTB.getValue();
                diagram.loadDiagram(currentPathway);
            }
        }));
        return fp;
    }

    private Widget getAnalysisSelectionPanel() {
        FlowPanel fp = new FlowPanel();
        analysisTokenTB = new TextBox();
        analysisTokenTB.setValue(currentAnalysis);
        analysisTokenTB.setWidth("200px");
        fp.add(new InlineLabel(" Overlay analysis token: "));
        fp.add(analysisTokenTB);
        fp.add(new Button("GO", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                currentAnalysis = analysisTokenTB.getValue();
                diagram.setAnalysisToken(currentAnalysis, "TOTAL");
            }
        }));
        return fp;
    }

    private Widget getTestPanel() {
        FlowPanel fp = new FlowPanel();
        fp.add(new Button("R-HSA-1181150", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("R-HSA-1181150");
            }
        }));
        fp.add(new Button("Apoptosis", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("R-HSA-109581");
            }
        }));
        fp.add(new Button("R-HSA-71291", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("R-HSA-71291");
            }
        }));
        fp.add(new Button("Raf/Map", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("R-HSA-5673001");
            }
        }));
        fp.add(new Button("R-HSA-5637815", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("R-HSA-5637815");
            }
        }));
        fp.add(new Button("R-HSA-2219530", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("R-HSA-2219530");
            }
        }));
        fp.add(new Button("R-HSA-1650814", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("R-HSA-1650814");
            }
        }));
        fp.add(new Button("R-HSA-170834", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("R-HSA-170834");
            }
        }));
        fp.add(new Button("R-HSA-400253", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("R-HSA-400253");
            }
        }));
        fp.add(new Button("R-HSA-157579", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("R-HSA-157579");
            }
        }));
        fp.add(new Button("R-HSA-1474244", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("R-HSA-1474244");
            }
        }));
        fp.add(new InlineLabel("      "));
        fp.add(new Button("R-HSA-162909", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("R-HSA-162909");
            }
        }));
        fp.add(new Button("R-HSA-5603041", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("R-HSA-5603041");
            }
        }));
        fp.add(new Button("R-HSA-3642279", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("R-HSA-3642279");
            }
        }));
        fp.add(new Button("R-HSA-3645790", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("R-HSA-3645790");
            }
        }));
        fp.add(new Button("R-HSA-73885", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("R-HSA-73885");
            }
        }));
        fp.add(new Button("R-HSA-1169408", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("R-HSA-1169408");
            }
        }));
        return fp;
    }


    private Widget getDiseasePanel() {
        FlowPanel fp = new FlowPanel();
        fp.add(getLoadButton("R-HSA-2219530", "Constitutive Signaling by Aberrant PI3K in Cancer"));
        fp.add(getLoadButton("R-HSA-2206296", ""));
        fp.add(getLoadButton("R-HSA-2206292", ""));
        fp.add(getLoadButton("R-HSA-2206308", ""));
        fp.add(getLoadButton("R-HSA-2206302", ""));
        fp.add(getLoadButton("R-HSA-2206291", ""));
        fp.add(getLoadButton("R-HSA-2474795", ""));
        fp.add(getLoadButton("R-HSA-2644602", ""));
        fp.add(getLoadButton("R-HSA-3311021", ""));
        fp.add(getLoadButton("R-HSA-3315487", ""));
        fp.add(getLoadButton("R-HSA-3371599", ""));
        fp.add(getLoadButton("R-HSA-5362768", ""));
        fp.add(getLoadButton("R-HSA-5340573", ""));
        fp.add(getLoadButton("R-HSA-5467340", ""));
        fp.add(getLoadButton("R-HSA-5545619", ""));
        fp.add(getLoadButton("R-HSA-3785653", ""));
        fp.add(getLoadButton("R-HSA-3656237", ""));
        fp.add(getLoadButton("R-HSA-4724289", ""));
        fp.add(getLoadButton("R-HSA-4793954", ""));
        fp.add(getLoadButton("R-HSA-5637815", "Signaling by Ligand-Responsive EGFR Variants in Cancer"));
        fp.add(getLoadButton("R-HSA-3595177", ""));
        fp.add(getLoadButton("R-HSA-4551295", ""));
        fp.add(getLoadButton("R-HSA-5578996", ""));
        fp.add(getLoadButton("R-HSA-4570571", ""));
        fp.add(getLoadButton("R-HSA-4687000", ""));
        fp.add(getLoadButton("R-HSA-5633231", ""));
        fp.add(getLoadButton("R-HSA-5579009", ""));
        fp.add(getLoadButton("R-HSA-4793950", ""));
        fp.add(getLoadButton("R-HSA-4549356", ""));
        fp.add(getLoadButton("R-HSA-5657560", ""));
        fp.add(getLoadButton("R-HSA-5655302", ""));
        fp.add(getLoadButton("R-HSA-5603027", ""));
        fp.add(getLoadButton("R-HSA-5602498", ""));
        fp.add(getLoadButton("R-HSA-5603029", ""));
        fp.add(getLoadButton("R-HSA-5602566", ""));
        fp.add(getLoadButton("R-HSA-5655253", ""));
        fp.add(getLoadButton("R-HSA-5602410", ""));
        fp.add(getLoadButton("R-HSA-162909", ""));
        fp.add(getLoadButton("R-HSA-977225", ""));

        return fp;
    }

    private Widget getTourPanel(){
        FlowPanel fp = new FlowPanel();
        fp.add(new Button("R_111057", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                R_HSA_1181150_tour();
            }
        }));
        fp.add(new Button("R_13", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                R_HSA_71921_tour();
            }
        }));
        fp.add(new Button("A Test 1", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.setAnalysisToken("MjAxNTA1MjgwNTQyNTNfODgz","TOTAL");
            }
        }));
        fp.add(new Button("A Test 2", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.setAnalysisToken("MjAxNTA1MjgwODM1NTRfOTE3","TOTAL");
            }
        }));
        fp.add(new Button("A Test 3", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.setAnalysisToken("MjAxNTA2MDEwOTU4MzdfNTQ0","TOTAL");
            }
        }));
        fp.add(new Button("A Test 4", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.setAnalysisToken("MjAxNTA2MDUwMzM5MzhfOA==","TOTAL");
            }
        }));
        fp.add(new Button("A Test 5", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.setAnalysisToken("MjAxNTA2MDgxMzUxNTZfMzQ2","TOTAL");
            }
        }));
        fp.add(new Button("A Test 6", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.setAnalysisToken("MjAxNTA2MDgxNDA2MjNfMzQ4","TOTAL");
            }
        }));
        fp.add(new Button("A Test 7", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.setAnalysisToken("MjAxNTA2MTExMjUyNTBfMTA5OQ==","TOTAL");
            }
        }));
        fp.add(new Button("A Test 8", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.setAnalysisToken("MjAxNTA2MTExMzM3MDFfMTEzMw==","TOTAL");
            }
        }));
        fp.add(new Button("A Test 9", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.setAnalysisToken("MjAxNTA2MTExNDA2MDVfMTE0Mg==","TOTAL");
            }
        }));
        fp.add(new Button("Exp 1", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.setAnalysisToken("MjAxNjAxMDQwOTM5NDBfMg==","TOTAL");
            }
        }));
        fp.add(new Button("Exp 2", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.setAnalysisToken("MjAxNTA2MDUwNzI1NTZfMzI=","TOTAL");
            }
        }));
        fp.add(new Button("Exp 3", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.setAnalysisToken("MjAxNTA2MTAxNDQ4MTJfNzk4","TOTAL");
            }
        }));
        return fp;
    }

    private void R_HSA_1181150_tour(){
        diagram.selectItem(1181156L);

        (new Timer() {
            @Override
            public void run() {
                diagram.selectItem(1181355L);
            }
        }).schedule(4000);

        (new Timer() {
            @Override
            public void run() {
                diagram.selectItem(1225914L);
            }
        }).schedule(8000);

        (new Timer() {
            @Override
            public void run() {
                diagram.selectItem(1181156L);
            }
        }).schedule(12000);

        (new Timer() {
            @Override
            public void run() {
                diagram.selectItem(173511L);
            }
        }).schedule(16000);

        (new Timer() {
            @Override
            public void run() {
                diagram.selectItem(171182L);
            }
        }).schedule(20000);
    }

    private void R_HSA_71921_tour(){
        diagram.selectItem(209772L);

        (new Timer() {
            @Override
            public void run() {
                diagram.selectItem(174391L);
            }
        }).schedule(4000);

        (new Timer() {
            @Override
            public void run() {
                diagram.selectItem(372480L);
            }
        }).schedule(8000);

        (new Timer() {
            @Override
            public void run() {
                diagram.selectItem(209772L);
            }
        }).schedule(12000);

        (new Timer() {
            @Override
            public void run() {
                diagram.selectItem(353555L);
            }
        }).schedule(16000);
    }
}

package org.reactome.web.test;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.client.DiagramViewer;
import org.reactome.web.diagram.util.Console;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WidgetTest implements EntryPoint {

    private final DiagramViewer diagram;

    public WidgetTest() {
        diagram = DiagramFactory.createDiagramViewer();
    }

    @Override
    public void onModuleLoad() {
        GWT.runAsync(new RunAsyncCallback() {
            public void onFailure(Throwable caught) {
            }

            @SuppressWarnings("unchecked")
            public void onSuccess() {
                DiagramFactory.CONSOLE_VERBOSE = true;
                DiagramFactory.EVENT_BUS_VERBOSE = true;
                DiagramFactory.SHOW_INFO = true;

                initialise();

                Console.info("");
                diagram.loadDiagram("REACT_111057");
            }
        });
    }

    public void initialise() {
        SplitLayoutPanel slp = new SplitLayoutPanel(10);
        slp.addWest(getDemoLeftPanel(), 83);
        slp.addNorth(getDiseasePanel(), 25);
        slp.add(diagram);
        RootLayoutPanel.get().add(slp);
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
        button.setTitle(title);
        return button;
    }

    private Widget getDemoLeftPanel(){
        FlowPanel fp = new FlowPanel();


        fp.add(new Label("REACT_111057"));
        fp.add(new Label("Reactions"));
        fp.add(getSelectionButton("REACT_111228", "Cleavage of NODAL proprotein"));
        fp.add(getSelectionButton("REACT_111216", "Phospho R-SMAD(SMAD2/3):CO-SMAD(SMAD4):FOXO3 binds FoxO3a-binding elements"));

        fp.add(new Label("--"));
        fp.add(new Label("Sets"));
        fp.add(getSelectionButton("REACT_7451", "SMAD2/3 [cytosol]"));
        fp.add(getSelectionButton("REACT_7364", "p-2S-SMAD2/3 [cytosol]"));

        fp.add(new Label("--"));
        fp.add(new Label("Complexes"));
        fp.add(getSelectionButton("REACT_111458", "NODAL:p-ACVR1B:ACVR2:EGF-CFC [plasma membrane]"));
        fp.add(getSelectionButton("REACT_7382", "p-2S-SMAD2/3:SMAD4 [nucleoplasm]"));

        fp.add(new Label("--"));
        fp.add(new Label("Protein"));
        fp.add(getSelectionButton("REACT_111585", "NODAL(27-347) [extracellular region]"));
        fp.add(getSelectionButton("REACT_111742", "FOXH1 [nucleoplasm]"));

        fp.add(new Label("--"));
        fp.add(new Label("Chemical"));
        fp.add(getSelectionButton("REACT_2812", "ATP [cytosol]"));
        fp.add(getSelectionButton("REACT_2741", "ADP [cytosol]"));


        fp.add(new Label(""));
        fp.add(new Label(""));
        fp.add(new Label("--"));
        fp.add(new Label("ORA"));
        fp.add(new Button("ORA 1", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.setAnalysisToken("MjAxNTA1MjgwODM1NTRfOTE3","TOTAL");
            }
        }));


        fp.add(new Label(""));
        fp.add(new Label(""));
        fp.add(new Label("--"));
        fp.add(new Label("Expression"));
        fp.add(new Button("Exp 1", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.setAnalysisToken("MjAxNTA2MTAxNDQ4MTJfNzk4","TOTAL");
            }
        }));

        return fp;
    }

    Button getLoadButton(final String stId, String title){
        Button button;
        button = new Button(stId, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram(stId);
            }
        });
        button.setTitle(title);
        return button;
    }

    private Widget getDemoTopPanel(){
        FlowPanel fp = new FlowPanel();
        fp.add(getLoadButton("REACT_111057", ""));
        fp.add(getLoadButton("REACT_13", ""));
        fp.add(new InlineLabel(" "));
        fp.add(getLoadButton("REACT_6288", ""));
        fp.add(new InlineLabel(" "));
        fp.add(getLoadButton("REACT_6844", ""));
        fp.add(getLoadButton("REACT_169192", ""));
        fp.add(getLoadButton("REACT_169440", ""));
        fp.add(new InlineLabel(" "));
        fp.add(getLoadButton("REACT_578", "Apoptosis"));
        fp.add(new InlineLabel(" "));
        fp.add(getLoadButton("REACT_268006", "Signaling by Ligand-Responsive EGFR Variants in Cancer"));
        fp.add(getLoadButton("REACT_147727", "Constitutive Signaling by Aberrant PI3K in Cancer"));
        return fp;
    }

    private Widget getTestPanel() {
        FlowPanel fp = new FlowPanel();
        fp.add(new Button("REACT_111057", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("REACT_111057");
            }
        }));
        fp.add(new Button("Apoptosis", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("REACT_578");
            }
        }));
        fp.add(new Button("REACT_13", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("REACT_13");
            }
        }));
        fp.add(new Button("Raf/Map", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("REACT_634");
            }
        }));
        fp.add(new Button("REACT_268006", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("REACT_268006");
            }
        }));
        fp.add(new Button("REACT_147727", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("REACT_147727");
            }
        }));
        fp.add(new Button("REACT_121139", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("REACT_121139");
            }
        }));
        fp.add(new Button("REACT_6844", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("REACT_6844");
            }
        }));
        fp.add(new Button("REACT_24941", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("REACT_24941");
            }
        }));
        fp.add(new Button("REACT_7970", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("REACT_7970");
            }
        }));
        fp.add(new Button("REACT_118779", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("REACT_118779");
            }
        }));
        fp.add(new InlineLabel("      "));
        fp.add(new Button("REACT_6288", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("REACT_6288");
            }
        }));
        fp.add(new Button("REACT_355462", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("REACT_355462");
            }
        }));
        fp.add(new Button("REACT_169192", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("REACT_169192");
            }
        }));
        fp.add(new Button("REACT_169440", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("REACT_169440");
            }
        }));
        fp.add(new Button("REACT_1826", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("REACT_1826");
            }
        }));
        fp.add(new Button("REACT_115831", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                diagram.loadDiagram("REACT_115831");
            }
        }));
        return fp;
    }


    private Widget getDiseasePanel() {
        FlowPanel fp = new FlowPanel();
        fp.add(getLoadButton("REACT_147727", "Constitutive Signaling by Aberrant PI3K in Cancer"));
        fp.add(getLoadButton("REACT_147734", ""));
        fp.add(getLoadButton("REACT_147759", ""));
        fp.add(getLoadButton("REACT_147798", ""));
        fp.add(getLoadButton("REACT_147857", ""));
        fp.add(getLoadButton("REACT_147860", ""));
        fp.add(getLoadButton("REACT_160102", ""));
        fp.add(getLoadButton("REACT_160301", ""));
        fp.add(getLoadButton("REACT_169107", ""));
        fp.add(getLoadButton("REACT_169165", ""));
        fp.add(getLoadButton("REACT_169312", ""));
        fp.add(getLoadButton("REACT_263883", ""));
        fp.add(getLoadButton("REACT_263933", ""));
        fp.add(getLoadButton("REACT_264030", ""));
        fp.add(getLoadButton("REACT_264356", ""));
        fp.add(getLoadButton("REACT_264430", ""));
        fp.add(getLoadButton("REACT_267741", ""));
        fp.add(getLoadButton("REACT_267765", ""));
        fp.add(getLoadButton("REACT_267905", ""));
        fp.add(getLoadButton("REACT_268006", "Signaling by Ligand-Responsive EGFR Variants in Cancer"));
        fp.add(getLoadButton("REACT_268113", ""));
        fp.add(getLoadButton("REACT_268132", ""));
        fp.add(getLoadButton("REACT_268413", ""));
        fp.add(getLoadButton("REACT_268458", ""));
        fp.add(getLoadButton("REACT_268619", ""));
        fp.add(getLoadButton("REACT_268645", ""));
        fp.add(getLoadButton("REACT_268761", ""));
        fp.add(getLoadButton("REACT_268813", ""));
        fp.add(getLoadButton("REACT_268849", ""));
        fp.add(getLoadButton("REACT_355030", ""));
        fp.add(getLoadButton("REACT_355033", ""));
        fp.add(getLoadButton("REACT_355059", ""));
        fp.add(getLoadButton("REACT_355196", ""));
        fp.add(getLoadButton("REACT_355210", ""));
        fp.add(getLoadButton("REACT_355226", ""));
        fp.add(getLoadButton("REACT_355281", ""));
        fp.add(getLoadButton("REACT_355533", ""));
        fp.add(getLoadButton("REACT_355549", ""));
        fp.add(getLoadButton("REACT_6288", ""));
        fp.add(getLoadButton("REACT_75925", ""));

        return fp;
    }

    private Widget getTourPanel(){
        FlowPanel fp = new FlowPanel();
        fp.add(new Button("R_111057", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                REACT_111057_tour();
            }
        }));
        fp.add(new Button("R_13", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                REACT_13_tour();
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
                diagram.setAnalysisToken("MjAxNTA2MDUwODQ0NThfNTE=","TOTAL");
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

    private void REACT_111057_tour(){
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

    private void REACT_13_tour(){
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

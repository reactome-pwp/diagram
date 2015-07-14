/*
 * Created on Sep 20, 2011
 *
 */
package org.reactome.web.diagram.renderers.common;

/**
 * This enum lists node types: e.g. protein, chemical, etc.
 *
 */
public enum RenderableObjectType {
    // Nodes
    RenderableProtein,
    RenderableInteractor,
    InteractorCountNode,
    RenderableChemical,
    RenderableEntity,
    RenderableEntitySet,
    RenderableCompartment,
    ProcessNode,
    RenderableGene,
    RenderableRNA,
    SourceOrSink,
    RenderableComplex,
    RenderablePathway,

    // Note
    Note,

    // Edges
    RenderableReaction,

    // Links
    RenderableInteraction,
    EntitySetAndMemberLink,
    EntitySetAndEntitySetLink,
    FlowLine
}

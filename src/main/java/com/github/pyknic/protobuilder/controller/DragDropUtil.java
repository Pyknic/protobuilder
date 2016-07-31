package com.github.pyknic.protobuilder.controller;

import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class DragDropUtil {
	//***********************************************************
	// 				STATIC
	//***********************************************************
	/**
	 * Adds a method which will be performed when a {@link Node} is dropped
	 * onto another node
	 * 
	 * @param soruceNode  			   the drag source node
	 * @param dragDropEventIdentifyer  the name for this drag-drop event
	 * @param action				   the method to perform when a drop is performed
	 */
	public static void add(Node soruceNode, String dragDropEventIdentifyer, DragDropAction action){			
		//The event which is fired when a drag begins from soruceNode
		soruceNode.setOnDragDetected( (event) -> {
			Dragboard dragboard = soruceNode.startDragAndDrop(TransferMode.MOVE);	//Signals that this node can handle drag events
            
            ClipboardContent clipboardContent = new ClipboardContent();		
            clipboardContent.putString(dragDropEventIdentifyer);
            dragboard.setContent(clipboardContent);		//This puts a string into the dragboard, which we can use to identify the drag event
            
            event.consume();							//Limits event propagation    (good practice)
        } ); 
		
		//The event which is fired when a dragged item begins hovering over another item 
		//Its purpose is to decide if we are allowed to drop the item here or not
		//
		//It is fired when another object hovers over soruceNode
		soruceNode.setOnDragOver( (event) -> {
			Dragboard dragboard = event.getDragboard();
			
			if(dragboard.hasString() &&						   	//These checks are to make sure that this is a valid dragDrop action
			   dragboard.getString().equals( dragDropEventIdentifyer ) ) {
				event.acceptTransferModes( TransferMode.MOVE ); //Sets the "ACCEPT DROP" flag (the cursor indication)
				event.consume();								//Limits event propagation    (good practice)
			}
		} );
		
		//This event is fired when a dragged item is "dropped" onto another object
		//It is fired when another object is dropped onto sourceNode
		soruceNode.setOnDragDropped( (event) -> {
			Dragboard dragboard = event.getDragboard();
			boolean dragDropSucceeded = false;
			
            if (dragboard.hasString()) {	
            	Node dragSource = (Node) event.getGestureSource();
            	Node dragTarget = (Node) soruceNode;             
                
                action.perform(dragSource, dragTarget);

                dragDropSucceeded = true;
            }
            event.setDropCompleted(dragDropSucceeded);	
            event.consume();							//Limits event propagation    (good practice)
		} );
	}
	
	public interface DragDropAction{
		/**
		 * The action to perform when a node is dropped onto another node
		 * 
		 * @param dragSource  the object the drag originated from
		 * @param dropTarget  the object the drop was performed on
		 */
		void perform(Node dragSource, Node dropTarget);
	}
	
	//***********************************************************
	// 				PRIVATE
	//***********************************************************	
	private DragDropUtil(){
		throw new UnsupportedOperationException("Class " + DragDropUtil.class.getSimpleName() + " should not be instanciated");
	}
}

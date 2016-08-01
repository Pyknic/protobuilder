package com.github.pyknic.protobuilder.ui;

import java.util.Optional;

import com.github.pyknic.protobuilder.proto.Proto;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class DragDropUtil {
	//***********************************************************
	// 				STATIC
	//***********************************************************
	private static String MID_STYLE = "drag-drop-mid";
	private static String LOW_STYLE = "drag-drop-low";
	private static String HIGH_STYLE = "drag-drop-high";
	
	public static EventHandler<? super MouseEvent> createOnDragDetectedHandler(TreeCell<Proto> forThis, String identifier){
		return (event) -> {
			final Dragboard dragboard = forThis.startDragAndDrop(TransferMode.MOVE);
            final ClipboardContent clipboardContent = new ClipboardContent();		
            
            clipboardContent.putString(identifier);
            dragboard.setContent(clipboardContent);
            event.consume();
        };
	}
	
	public static EventHandler<? super DragEvent> createOnDragOverHandler(TreeCell<Proto> forThis, String identifier){
		return (event) -> {
			final Dragboard dragboard = event.getDragboard();			
			if(dragboard.hasString()						   
			&& dragboard.getString().equals( identifier ) ) {
				final Optional<TreeCell<Proto>> source = getSource(event);
				if( source.isPresent() && !source.get().equals(forThis) ){
					final DropLocation location = getLocation(forThis, event);
	                final ObservableList<String> styles = forThis.getStyleClass();
					switch (location) {
					case HIGH:
						styles.removeAll(LOW_STYLE, MID_STYLE);
	                    styles.add(HIGH_STYLE);
						break;
					case LOW:
						styles.removeAll(HIGH_STYLE, MID_STYLE);
	                    styles.add(LOW_STYLE);				
						break;
					case MID:
						styles.removeAll(LOW_STYLE, HIGH_STYLE);
	                    styles.add(MID_STYLE);					
						break;
					default:
						break;
					}
				}
				event.acceptTransferModes( TransferMode.MOVE ); 
			}
			event.consume();
		};
	}
	
	public static EventHandler<? super DragEvent> createOnDragExitedHandler(TreeCell<Proto> forThis, String identifier){
		return (event) -> {
			final Optional<TreeCell<Proto>> source = getSource(event);
			if( source.isPresent() ){
				final Dragboard dragboard = event.getDragboard();
				if(dragboard.hasString()
	            && dragboard.getString().equals(identifier)
	            && !source.get().equals(forThis) ) {
					forThis.getStyleClass().removeAll( HIGH_STYLE, LOW_STYLE, MID_STYLE);
				}
			}
			event.consume();
		};
	}
	
	public static EventHandler<? super DragEvent> createOnDragDroppedHandler(TreeCell<Proto> forThis, String identifier, DragDropAction action){
		return (event) -> {
			final Optional<TreeCell<Proto>> source = getSource(event);
			if( source.isPresent() ){
				final Dragboard dragboard = event.getDragboard();
				boolean dragDropSucceeded = false;
				if(dragboard.hasString()
	            && dragboard.getString().equals(identifier)
	            && !source.get().equals(forThis) ) {						
	                action.perform( source.get(), forThis, getLocation(forThis, event));
	                dragDropSucceeded = true;
	            } 
	            event.setDropCompleted(dragDropSucceeded);	
			}
            event.consume();
		};
	}
	
	public enum DropLocation{
		HIGH, LOW, MID
	}
	
	public interface DragDropAction{
		/**
		 * The action to perform when a node is dropped onto another node
		 * 
		 * @param dragSource  the object the drag originated from
		 * @param dropTarget  the object the drop was performed on
		 */
		void perform(Node dragSource, Node dropTarget, DropLocation location);
	}
	
	//***********************************************************
	// 				PRIVATE
	//***********************************************************	
	private static DropLocation getLocation(TreeCell<?> target, DragEvent event){
		//Get Y pos inside the cell we hover over
		final double yPos = event.getY();
		final double height = target.getHeight();
		if( (height - yPos) / height > 0.75 ) {
			return DropLocation.HIGH;
		} else if ( (height - yPos) / height < 0.25 ) {
			return DropLocation.LOW;
		} else {
			return DropLocation.MID;
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Optional<TreeCell<Proto>> getSource(DragEvent event){
		final Object gesture = event.getGestureSource();
		final TreeCell<Proto> sourceNode;
		if( gesture != null && gesture instanceof TreeCell ){
			TreeCell tempCell= (TreeCell) gesture;
			if( tempCell.getItem() instanceof Proto ){
				sourceNode = (TreeCell<Proto>) tempCell;
			} else {
				sourceNode = null;
			}
		} else {
			sourceNode = null;
		}
		return Optional.ofNullable(sourceNode);
	}
	
	private DragDropUtil(){
		throw new UnsupportedOperationException("Class " + DragDropUtil.class.getSimpleName() + " should not be instanciated");
	}
}

package com.github.pyknic.protobuilder.ui;

import java.util.HashMap;
import java.util.Map;

import com.github.pyknic.protobuilder.proto.Parameter;
import com.github.pyknic.protobuilder.proto.Proto;
import com.github.pyknic.protobuilder.ui.DragDropUtil.DragDropAction;
import com.github.pyknic.protobuilder.ui.DragDropUtil.DropLocation;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;

public class ProtoTreeCell extends TreeCell<Proto>{
	private static final String ON_DETECTED = "onDetect";
	private static final String ON_OVER     = "onOver";
	private static final String ON_EXITED   = "onExited";
	private static final String ON_DROPPED  = "onDropped";
	
	private static final String DRAG_IDENTIFIER = "dragDropEvent";

	private final Map<String, EventHandler<?>> dragEvents;

	public ProtoTreeCell(){
		this.dragEvents = new HashMap<>();
		
		dragEvents.put(ON_DETECTED, DragDropUtil.createOnDragDetectedHandler(this, DRAG_IDENTIFIER));
		dragEvents.put(ON_OVER, 	DragDropUtil.createOnDragOverHandler(this, DRAG_IDENTIFIER));
		dragEvents.put(ON_EXITED, 	DragDropUtil.createOnDragExitedHandler(this, DRAG_IDENTIFIER));
		dragEvents.put(ON_DROPPED, 	DragDropUtil.createOnDragDroppedHandler(this, DRAG_IDENTIFIER, dragDropAction()));
	}
	
	@SuppressWarnings("unchecked")
	public void addDragDrop(){
		setOnDragDetected( (EventHandler<? super MouseEvent>) dragEvents.get(ON_DETECTED) );
		setOnDragOver( 	   (EventHandler<? super DragEvent>)  dragEvents.get(ON_OVER) );
		setOnDragExited(   (EventHandler<? super DragEvent>)  dragEvents.get(ON_EXITED) );
		setOnDragDropped(  (EventHandler<? super DragEvent>)  dragEvents.get(ON_DROPPED) );
	}
	
	@SuppressWarnings("unchecked")
	public void removeDragDrop(){
		removeEventHandler(MouseEvent.DRAG_DETECTED, (EventHandler<? super MouseEvent>) dragEvents.get(ON_DETECTED));
		removeEventHandler(DragEvent.DRAG_OVER, 	 (EventHandler<? super DragEvent>)  dragEvents.get(ON_OVER));
		removeEventHandler(DragEvent.DRAG_EXITED, 	 (EventHandler<? super DragEvent>)  dragEvents.get(ON_EXITED));
		removeEventHandler(DragEvent.DRAG_DROPPED, 	 (EventHandler<? super DragEvent>)  dragEvents.get(ON_DROPPED));
	}
	
	private DragDropAction dragDropAction(){
		return (source, target, location) -> {
            if( (source instanceof ProtoTreeCell) && (target instanceof ProtoTreeCell) ){
            	final ProtoTreeCell sourceCell = (ProtoTreeCell) source;
            	final ProtoTreeCell targetCell = (ProtoTreeCell) target;

            	final TreeItem<Proto> sourceTree = sourceCell.getTreeItem();
            	final TreeItem<Proto> targetTree = targetCell.getTreeItem();

            	//A parent can't move to a descendant (creates a cycle) 
            	//and a parameter can't have children
                if(isDescendant( targetTree, sourceTree )
        		|| (location == DropLocation.MID && targetCell.getItem() instanceof Parameter) ){
            		return;
            	}              
                
                //Remove sourceTree from its current location
                sourceTree.getParent().getChildren().remove(sourceTree);
                if( location == DropLocation.HIGH ){
                	final ObservableList<TreeItem<Proto>> children = targetTree.getParent().getChildren();
                	final int index = children.indexOf(targetTree);
                	children.add(index, sourceTree);                	
                } else if ( location == DropLocation.MID ){
                	targetTree.getChildren().add(sourceTree);
                } else {
                	final ObservableList<TreeItem<Proto>> children = targetTree.getParent().getChildren();
                	final int index = children.indexOf(targetTree);
                	children.add(index+1, sourceTree);       
                }
            } else {
                return;
            }
        };
	}

	private boolean isDescendant(TreeItem<Proto> target, TreeItem<Proto> source) {
		TreeItem<Proto> parent = target.getParent();
		if( parent.equals(source) ){
			return true;
		}
		while( (parent = parent.getParent()) != null  ){
			//If PARENT eventually equal TARGET, then THIS was an ancestor of TARGET
			if( parent.equals(source) ){
				return true;
			}
		}
		return false;
	}
}

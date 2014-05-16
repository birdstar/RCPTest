package com.ibm.rcptest.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TheFirstAction extends Action{

	public static final String ID = TheFirstAction.class.getName();
	
	public TheFirstAction(){
		this.setText("Action1");
		this.setId(ID);
	}
	
	public void run(){
		Shell shell = Display.getCurrent().getActiveShell();
		MessageDialog.openInformation(shell, "Information Dialog1", "Show information message dialog!");
	}
}

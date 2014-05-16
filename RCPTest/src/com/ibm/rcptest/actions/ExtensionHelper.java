package com.ibm.rcptest.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;

public class ExtensionHelper {
	
	private String ID = ExtensionConstants.EXTENSION_ID;
	
	public static ExtensionHelper instance;

	private Map<String, List<ExtensionInfoBean>> actionMap = new HashMap<String, List<ExtensionInfoBean>>(); 
	
	private Map<String, List<ExtensionInfoBean>> viewMap = new HashMap<String, List<ExtensionInfoBean>>();
	
	private Map<String, List<ExtensionInfoBean>> perspectiveMap = new HashMap<String, List<ExtensionInfoBean>>();
  
	private MenuManager extendMenu;
	
	private ExtensionHelper(){
		loadExtensions();
	}

	public static ExtensionHelper getInstance() {
		if(instance == null) instance = new	ExtensionHelper();
		return instance;
	}
	
	private void loadExtensions(){
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(ID);
		
		if (elements == null || elements.length == 0)
			return;
		
		for (int i = 0; i < elements.length; i++) { 
			IConfigurationElement element = elements[0];
			ExtensionInfoBean bean = new ExtensionInfoBean();
			bean.setId(element.getAttribute(ExtensionConstants.ATTR_ID));
			bean.setClientId(element.getAttribute(ExtensionConstants.ATTR_CLIENTID));
			bean.setDesc(element.getAttribute(ExtensionConstants.ATTR_DESC));
			bean.setType(element.getAttribute(ExtensionConstants.ATTR_TYPE));
			bean.setName(element.getAttribute(ExtensionConstants.ATTR_NAME));
			
			String indexString = element.getAttribute(ExtensionConstants.ATTR_INDEX);
			if (indexString == null || indexString.trim().isEmpty()){
				bean.setIndex(100);
			}else{
				bean.setIndex(Integer.parseInt(indexString));
			}
			
			if (ExtensionConstants.TYPE_ACTION.equalsIgnoreCase(bean.getType())) {

				String classPath = element
						.getAttribute(ExtensionConstants.ATTR_CLASS);
				Object obj = null;
				try {
					if (classPath != null && !classPath.isEmpty()) {

						obj = element
								.createExecutableExtension(ExtensionConstants.ATTR_CLASS);

					} else {
						obj = element
								.createExecutableExtension(ExtensionConstants.ATTR_CLIENTID);
					}

				} catch (CoreException e) {
					e.printStackTrace();
				}
				if (obj instanceof IAction){
					bean.setAction((IAction) obj);
				}
			}
			
			appendGroup(bean);
			
		}
	}

	private void appendGroup(ExtensionInfoBean bean){
		
		if (ExtensionConstants.TYPE_PERSPECTIVE.equalsIgnoreCase(bean.getType())){
			addtoMap(perspectiveMap, bean);
		}else if (ExtensionConstants.TYPE_ACTION.equalsIgnoreCase(bean.getType())){
			addtoMap(actionMap, bean);
		}else if (ExtensionConstants.TYPE_VIEW.equalsIgnoreCase(bean.getType())){
			addtoMap(viewMap, bean);
		}
		
	}

	private void addtoMap(Map<String, List<ExtensionInfoBean>> map,
			ExtensionInfoBean bean) {
		List<ExtensionInfoBean> list;
		if (map.containsKey(ID)){
			list = map.get(ID);
		}else{
			list = new ArrayList<>();
			map.put(ID, list);
		}
		list.add(bean);
	}
	
	public void fillMenuBar(IMenuManager menuManager){
		
		extendMenu = new MenuManager("ExtensionMenu");
		menuManager.add(extendMenu);
		
		fillMenuBar(perspectiveMap);
		fillMenuBar(viewMap);
		fillMenuBar(actionMap);
		
	}
	

	private void fillMenuBar(Map<String, List<ExtensionInfoBean>> map) {
		List<ExtensionInfoBean> list = map.get(ID);
		if (list == null || list.size() == 0) return;
		Iterator<ExtensionInfoBean> it = list.iterator();
		while(it.hasNext()){
			ExtensionInfoBean bean = it.next();
			IAction action = bean.getAction();
			if (action == null){
				action = new TheFirstAction();
			}
			extendMenu.add(action);
		}
				
	}

	



}

/*
 * Copyright (c) 2008 Neil Bartlett
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Neil Bartlett - initial implementation
 *     Stephen Evanchik - Updated to use data provider services
 */
package info.evanchik.eclipse.karaf.workbench.ui.views.bundle;

import info.evanchik.eclipse.karaf.workbench.KarafWorkbenchActivator;
import info.evanchik.eclipse.karaf.workbench.ui.views.FilteredViewPart;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.osgi.framework.Bundle;

public class BundlesView extends FilteredViewPart {

    public static final String VIEW_ID = "info.evanchik.eclipse.karaf.workbench.karafBundles";

    protected static final int MAX_COLS = 5;

    private static final String TAG_COLUMN_WIDTH = "columnWidth";

    private Tree treeTable;
    private TreeViewer treeTableViewer;
    private BundlesContentProvider contentProvider;

    private IAction propertiesAction;
    private BundleSymbolicNameFilter nameFilter;

    protected final int[] colWidth = new int[] { 185, 40, 100, 250, 250 };

    @Override
    public void createMainControl(final Composite parent) {
        parent.setLayout(new FillLayout());

        treeTable = new Tree(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        treeTable.setLinesVisible(true);
        treeTable.setHeaderVisible(true);

        TreeColumn col = new TreeColumn(treeTable, SWT.BORDER);
        col.setWidth(colWidth[0]);
        col.setText("Runtime");

        col = new TreeColumn(treeTable, SWT.BORDER);
        col.setWidth(colWidth[1]);
        col.setText("Id");

        col = new TreeColumn(treeTable, SWT.NONE);
        col.setWidth(colWidth[2]);
        col.setText("State");

        col = new TreeColumn(treeTable, SWT.NONE);
        col.setWidth(colWidth[3]);
        col.setText("Name");

        col = new TreeColumn(treeTable, SWT.NONE);
        col.setWidth(colWidth[4]);
        col.setText("Location");

        treeTableViewer = new TreeViewer(treeTable);
        treeTableViewer.setLabelProvider(new BundleTableLabelProvider());

        nameFilter = new BundleSymbolicNameFilter();
        treeTableViewer.addFilter(nameFilter);

        contentProvider = new BundlesContentProvider();

        treeTableViewer.setContentProvider(contentProvider);
        treeTableViewer.setSorter(new BundleIdSorter());
        treeTableViewer.setInput(KarafWorkbenchActivator.getDefault().getBundle().getBundleContext());

        getViewSite().setSelectionProvider(treeTableViewer);

        createActions();
        fillMenu();

        initContextMenu();
    }

    @Override
    public void init(final IViewSite site, final IMemento memento) throws PartInitException {
        super.init(site, memento);

        for (int i = 0; i < MAX_COLS; i++) {

            if (memento != null) {
                final Integer in = memento.getInteger(TAG_COLUMN_WIDTH + i);
                if (in != null && in.intValue() > 5) {
                    colWidth[i] = in.intValue();
                }
            }
        }
    }

    @Override
    public void saveState(final IMemento memento) {
        final TreeColumn[] tc = treeTable.getColumns();

        for (int i = 0; i < MAX_COLS; i++) {
            final int width = tc[i].getWidth();
            if (width != 0) {
                memento.putInteger(TAG_COLUMN_WIDTH + i, width);
            }
        }
    }

    @Override
    public void doSetFocus() {
        if (treeTable != null) {
            treeTable.setFocus();
        }
    }

    @Override
    protected void updatedFilter(final String filterString) {
        nameFilter.setFilterString(filterString);
        treeTableViewer.refresh();
    }

    private void createActions() {
        propertiesAction = new Action() {
            @Override
            public void run() {
                final IStructuredSelection selection =
                    (IStructuredSelection) treeTableViewer.getSelection();
                if (!selection.isEmpty()) {
                    /*
                    final BundleItem bundle = (BundleItem) selection.getFirstElement();

                    final BundlePropertiesDialog propsDialog = new BundlePropertiesDialog(getSite().getShell(), bundle);
                    propsDialog.open();
                    */
                }
            }
        };
        propertiesAction.setText("Properties...");
    }

    protected void fillMenu() {
        final IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
        menuManager.add(new ExcludeBundlesFilterAction("Installed", Bundle.INSTALLED, treeTableViewer));
        menuManager.add(new ExcludeBundlesFilterAction("Resolved", Bundle.RESOLVED, treeTableViewer));
        menuManager.add(new ExcludeBundlesFilterAction("Starting", Bundle.STARTING, treeTableViewer));
        menuManager.add(new ExcludeBundlesFilterAction("Active", Bundle.ACTIVE, treeTableViewer));
        menuManager.add(new ExcludeBundlesFilterAction("Stopping", Bundle.STOPPING, treeTableViewer));
    }

    protected void initContextMenu() {
        final MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(final IMenuManager manager) {
                menuMgr.add(propertiesAction);
                menuMgr.add(new Separator());
                menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            }
        });

        final Menu menu = menuMgr.createContextMenu(treeTableViewer.getControl());
        treeTableViewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, treeTableViewer);
    }

    @Override
    public void dispose() {
    }
}

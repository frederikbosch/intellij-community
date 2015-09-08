/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jetbrains.python.debugger.concurrency.tool.asyncio.table;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.table.JBTable;
import com.intellij.xdebugger.impl.XSourcePositionImpl;
import com.jetbrains.python.debugger.PyConcurrencyEvent;
import com.jetbrains.python.debugger.concurrency.PyConcurrencyGraphModel;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyPanel;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyTable;
import com.jetbrains.python.debugger.concurrency.tool.graph.GraphCell;
import com.jetbrains.python.debugger.concurrency.tool.graph.GraphCellRenderer;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AsyncioTable extends ConcurrencyTable {
  protected PyConcurrencyGraphModel myGraphModel;

  public AsyncioTable(PyConcurrencyGraphModel graphModel, Project project, ConcurrencyPanel panel) {
    super(graphModel, project, panel);
    myGraphModel = graphModel;
    setDefaultRenderer(GraphCell.class, new GraphCellRenderer(myGraphModel));

    addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          JBTable target = (JBTable)e.getSource();
          int row = target.getSelectedRow();
          if (row != -1) {
            PyConcurrencyEvent event = myGraphModel.getEventAt(row);
            VirtualFile vFile = LocalFileSystem.getInstance().findFileByPath(event.getFileName());
            navigateToSource(XSourcePositionImpl.create(vFile, event.getLine()));
            myPanel.showStackTrace(myGraphModel.getEventAt(row));
          }
        }
      }
    });

    getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        int row = getSelectedRow();
        if (row != -1) {
          myPanel.showStackTrace(myGraphModel.getEventAt(row));
        }
      }
    });
  }
}

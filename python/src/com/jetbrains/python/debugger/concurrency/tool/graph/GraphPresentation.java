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
package com.jetbrains.python.debugger.concurrency.tool.graph;

import com.jetbrains.python.debugger.concurrency.PyConcurrencyGraphModel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GraphPresentation {
  private final PyConcurrencyGraphModel myGraphModel;
  private List<PresentationListener> myListeners = new ArrayList<PresentationListener>();
  private final Object myListenersObject = new Object();
  private GraphVisualSettings myVisualSettings;

  public GraphPresentation(final PyConcurrencyGraphModel graphModel, GraphVisualSettings visualSettings) {
    myGraphModel = graphModel;
    myVisualSettings = visualSettings;

    myVisualSettings.registerListener(new GraphVisualSettings.SettingsListener() {
      @Override
      public void settingsChanged() {
        updateGraphModel();
        notifyListeners();
      }
    });

    myGraphModel.registerListener(new PyConcurrencyGraphModel.GraphListener() {
      @Override
      public void graphChanged() {
        updateGraphModel();
        notifyListeners();
      }
    });
  }

  private void updateGraphModel() {
  }

  public GraphVisualSettings getVisualSettings() {
    return myVisualSettings;
  }

  public int getLinesNumber() {
    return myGraphModel.getMaxThread();
  }

  public int getCellsNumber() {
    return (int)myGraphModel.getDuration() / myVisualSettings.getMillisPerCell();
  }

  public ArrayList<String> getThreadNames() {
    return myGraphModel.getThreadNames();
  }

  private long roundForCell(long time) {
    long millisPerCell = myVisualSettings.getMillisPerCell();
    return time - (time % millisPerCell);
  }

  public ArrayList<GraphBlock> getVisibleGraph() {
    if (myVisualSettings.getHorizontalMax() == 0) {
      return new ArrayList<GraphBlock>();
    }
    long startTime = roundForCell(myGraphModel.getStartTime() +
                                  myVisualSettings.getHorizontalValue() * myGraphModel.getDuration() /
                                  myVisualSettings.getHorizontalMax());
    ArrayList<GraphBlock> ret = new ArrayList<GraphBlock>();
    int curEventId = myGraphModel.getLastEventIndexBeforeMoment(startTime);
    long curTime, nextTime = startTime;
    int i = 0;
    int numberOfCells = myVisualSettings.getHorizontalExtent() / GraphSettings.CELL_WIDTH + 2;
    while ((i < numberOfCells) && (curEventId < myGraphModel.getSize())) {
      curTime = nextTime;
      nextTime = roundForCell(myGraphModel.getEventAt(curEventId + 1).getTime());
      long period = nextTime - curTime;
      int cellsInPeriod = (int)(period / myVisualSettings.getMillisPerCell());
      if (cellsInPeriod != 0) {
        ret.add(new GraphBlock(myGraphModel.getDrawElementsForRow(curEventId), cellsInPeriod));
        i += cellsInPeriod;
      }
      curEventId += 1;
    }
    return ret;
  }


  public interface PresentationListener {
    void graphChanged(int padding);
  }

  public void registerListener(@NotNull PresentationListener logListener) {
    synchronized (myListenersObject) {
      myListeners.add(logListener);
    }
  }

  public void notifyListeners() {
    synchronized (myListenersObject) {
      for (PresentationListener logListener : myListeners) {
        logListener.graphChanged(myVisualSettings.getHorizontalMax() == 0 ? myVisualSettings.getHorizontalValue() :
                                 myVisualSettings.getHorizontalValue() * getCellsNumber() / myVisualSettings.getHorizontalMax());
      }
    }
  }

}

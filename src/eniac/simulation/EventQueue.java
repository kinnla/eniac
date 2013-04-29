/*******************************************************************************
 * Copyright (c) 2003-2005, 2013 Till Zoppke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Till Zoppke - initial API and implementation
 ******************************************************************************/
/*
 * Created on 25.03.2004
 */
package eniac.simulation;

/**
 * Class EventQueue is a priority Queue for Events implemented as a heap.
 * 
 * @author till zopppke
 */
public class EventQueue {

    private static final int DEFAULT_SIZE = 100;

    // vector containing discreteElements maintaining a heap property
    private EEvent[] _elements = new EEvent[DEFAULT_SIZE];

    private int _size = 0;

    //=========================== constructors
    // =================================

    /**
     * Constructs an initially empty discreteEventQueue
     */
    public EventQueue() {
        // empty
    }

    //======================== private methods
    // =================================

    // compares two elements by calling compareTo
    private boolean lessThan(int i, int j) {
        return _elements[i].time < _elements[j].time;
    }

    // increases the specified element so that the heap property will maintained
    private void increase(int i) {
        int parent = (i - 1) / 2;
        while (i > 0 && lessThan(i, parent)) {
            swap(i, parent);
            i = parent;
            parent = (i - 1) / 2;
        }
    }

    // lets the specified element sink
    private void reHeap() {
        int i = 0;
        int j = 2 * i + 1;
        boolean done = false;
        while (j < _size && !done) {
            // compare left child to right child
            if (j + 1 < _size && lessThan(j + 1, j)) {
                j++;
            }
            // compare parent to smaller child
            if (lessThan(j, i)) {
                swap(i, j);
                i = j;
                j = 2 * j + 1;
            } else {
                done = true;
            }
        }
    }

    // swaps two elements in the elements Vector
    private void swap(int i, int j) {
        EEvent temp = _elements[i];
        _elements[i] = _elements[j];
        _elements[j] = temp;
    }

    private void checkSize(int n) {
        while (_size + n > _elements.length) {
            EEvent[] temp = _elements;
            _elements = new EEvent[_elements.length + DEFAULT_SIZE];
            System.arraycopy(temp, 0, _elements, 0, temp.length);
        }
    }

    //========================= public methods
    // =================================

    /**
     * Checks whether this discreteEventQueue contains more events
     * 
     * @see java.util.Enumeration#hasMoreElements()
     */
    public synchronized boolean isEmpty() {
        return _size == 0;
    }

    /**
     * Returns the next event (that will happen in the nearest future)
     * 
     * @see java.util.Enumeration#nextElement()
     */
    public synchronized EEvent removeFirst() {
        EEvent retour = _elements[0];
        _elements[0] = _elements[--_size];
        reHeap();
        return retour;
    }

    public synchronized EEvent getFirst() {
        return _elements[0];
    }

    /**
     * inserts a discreteEvent by maintaining the heap property
     * 
     * @param e
     *            the Element to be inserted
     */
    public synchronized void insert(EEvent e) {
        checkSize(1);
        _elements[_size] = e;
        increase(_size++);
    }

    public synchronized void insert(EEvent[] elements) {
        checkSize(elements.length);
        for (int i = 0; i < elements.length; ++i) {
            _elements[_size] = elements[i];
            increase(_size++);
        }
    }

    public synchronized void empty() {
        _size = 0;
    }
}

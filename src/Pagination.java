/*
 * Enhanced VNC Thumbnail Viewer 1.001
 *      - Modified for loop of pagination
 *      - Added calInitNext(), calInitPrevious(), findViewers() methods
 *      - Modified return value in next(), previous() methods
 * 
 * Enhanced VNC Thumbnail Viewer 1.000
 *      - Class for managing about page
 * 
 */

import java.util.Vector;

class Pagination {

    static int thumbsnailPerPage = 4;
    static int presentPage;
    int previousStart, previousEnd, nextStart, nextEnd, presentStart, presentEnd;
    int[] step;
    VncViewersList viewersList;
    Vector viewers;

    Pagination(VncViewersList v) {
        viewers = new Vector();
        viewersList = v;
        presentPage = 1;
        presentStart = 0;
        presentEnd = 0;
        step = new int[2];
    }

    public Vector next() {
        // Modified on evnctv 1.001
        if (hasNext()) {
            calNext();
        } else {
            calInitNext();
        }
        
        findViewers();
        
        return viewers;
    }

    public Vector previous() {
        // Modified on evnctv 1.001
        if (hasPrevious()) {
            calPrevious();
        } else {
            calInitPrevious();
        }
        
        findViewers();
        
        return viewers;
    }

    public boolean hasNext() {
        if (presentEnd < (viewersList.size() - 1)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasPrevious() {
        if (presentStart > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLimited() {
        if (viewersList.size() > thumbsnailPerPage) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEmpty() {
        if (viewersList.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    //
    // calculate viewvers that is shown on next page
    //
    private void calNext() {
        int size = viewersList.size();

        // initial value of start program to var presentEnd
        if (presentEnd == 0) {
            if (thumbsnailPerPage > size) {
                presentEnd = size - 1;
            } else {
                presentEnd = thumbsnailPerPage - 1;
            }
        }

        nextStart = presentStart + thumbsnailPerPage;
        nextEnd = presentEnd + thumbsnailPerPage;

        if (nextEnd >= size) {
            nextStart = size - thumbsnailPerPage;
            nextEnd = size - 1;
        }
        previousStart = presentStart;
        previousEnd = presentEnd;

        presentStart = nextStart;
        presentEnd = nextEnd;
    }

    //
    // calculate viewvers that is shown on next page
    //
    private void calPrevious() {
        previousStart = presentStart - thumbsnailPerPage;
        previousEnd = presentEnd - thumbsnailPerPage;

        if (previousStart < 0) {
            previousStart = 0;
            previousEnd = thumbsnailPerPage - 1;
        }
        nextStart = presentStart;
        nextEnd = presentEnd;

        presentStart = previousStart;
        presentEnd = previousEnd;
    }

    /*
     * Added on evnctv 1.001
     * Calculate next start when next loop
     */
    private void calInitNext() {
        nextStart = 0;

        int size = viewersList.size();
        if (thumbsnailPerPage > size) {
            nextEnd = size - 1;
        } else {
            nextEnd = thumbsnailPerPage - 1;
        }

        previousStart = presentStart;
        previousEnd = presentEnd;

        presentStart = nextStart;
        presentEnd = nextEnd;
    }
    
    /*
     * Added on evnctv 1.001
     * Calculate previous start when back loop
     */
    private void calInitPrevious() {
        previousStart = viewersList.size() - thumbsnailPerPage;
        previousEnd = viewersList.size() - 1;
        
        nextStart = presentStart;
        nextEnd = presentEnd;

        presentStart = previousStart;
        presentEnd = previousEnd;
    }
    
    /*
     * Added on evnctv 1.001
     * Find viewers that will be shown
     */
    private void findViewers() {
        viewers.clear();
        
        for (int i = presentStart; i <= presentEnd; i++) {
            viewers.add(viewersList.get(i));
        }
    }
}

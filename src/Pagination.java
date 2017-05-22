/*
 * Enhanced VNC Thumbnail Viewer 1.0
 * Class for managing about page
 */

class Pagination {
    static int thumbsnailPerPage = 4;
    static int presentPage;
    int previousStart, previousEnd, nextStart, nextEnd, presentStart, presentEnd;
    int[] step;
    VncViewersList viewersList;
    
    Pagination(VncViewersList v){
        viewersList = v;
        presentPage = 1;
        presentStart = 0;
        presentEnd = 0;
        step = new int[2];
    }

    public int[] next(){
        if(hasNext()){
            calNext();
            step[0] = presentStart;
            step[1] = presentEnd;
            
            System.out.println("Next: ("+ presentStart +", "+ presentEnd +") "+viewersList.size());
            return step;
        }
        else{
            return null;
        }
    }
    
    public int[] previous(){
        if(hasPrevious()){
            calPrevious();
            step[0] = presentStart;
            step[1] = presentEnd;
            
            System.out.println("Previous: ("+ presentStart +", "+ presentEnd +") "+viewersList.size());
            return step;
        }
        else{
            return null;
        }
    }
    
    public boolean hasNext(){
        if(presentEnd < (viewersList.size()-1))
            return true;
        else
            return false;
    }
    
    public boolean hasPrevious(){
        if(presentStart > 0)
            return true;
        else
            return false;
    }
    
    public boolean isLimited(){
        if(viewersList.size() > thumbsnailPerPage){
            return true;
        }
        else{
            return false;
        }
    }
    
    public boolean isEmpty(){
        if(viewersList.size() == 0){
            return true;
        }
        else{
            return false;
        }
    }
    
    //
    // calculate viewvers that is shown on next page
    //
    private void calNext(){
        int size = viewersList.size();
        
        // initial value of start program to var presentEnd
        if(presentEnd == 0){
            if(thumbsnailPerPage > size){
                presentEnd = size-1;
            }
            else{
                presentEnd = thumbsnailPerPage-1;
            }
        }

        nextStart = presentStart+thumbsnailPerPage;
        nextEnd = presentEnd+thumbsnailPerPage;
        
        if(nextEnd >= size){
            nextStart = size-thumbsnailPerPage;
            nextEnd = size-1;
        }
        previousStart = presentStart;
        previousEnd = presentEnd;
        
        presentStart = nextStart;
        presentEnd = nextEnd;
    }
    
    //
    // calculate viewvers that is shown on next page
    //
    private void calPrevious(){
        previousStart = presentStart-thumbsnailPerPage;
        previousEnd = presentEnd-thumbsnailPerPage;
        
        if(previousStart < 0){
            previousStart = 0;
            previousEnd = thumbsnailPerPage-1;
        }
        nextStart = presentStart;
        nextEnd = presentEnd;
        
        presentStart = previousStart;
        presentEnd = previousEnd;
    }
}

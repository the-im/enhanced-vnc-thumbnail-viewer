/*
 * Enhanced VNC Thumbnail Viewer 1.0
 * Class for searching viewers in list
 */

class SearchList{
    VncViewersList viewersList, viewersSearchList;
    
    public SearchList(EnhancedVncThumbnailViewer v){
        viewersSearchList = new VncViewersList(v);
    }
    
    public VncViewersList searchViewer(VncViewersList viewer,String searchText){
        viewersList = viewer;
        
        if(searchText == null || searchText.equals("")){
            return viewersList;
        }
        else{
            viewersSearchList.clear();
            VncViewer v;
            for(int i=0; i<viewersList.size(); i++){
                v = (VncViewer) viewersList.get(i);
                if(v.compname.contains(searchText)){
                    viewersSearchList.add(v);
                }
            }        
            return viewersSearchList;
        }
    }
}

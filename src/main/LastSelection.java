package main;

public class LastSelection {
    private String selectionName;
    private String selectionType;

    public LastSelection(){
    }

    public String getSelectionName() {
        return selectionName;
    }

    public void setSelectionName(String selectionName) {
        this.selectionName = selectionName;
    }

    public String getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(String selectionType) {
        this.selectionType = selectionType;
    }

    public void resetSelection() {
        this.selectionName = null;
        this.selectionType = null;
    }
}

package com.example.admin.utils.excel;

import java.util.ArrayList;
import java.util.List;

public class ExcelHeader {
    private String id;

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    private String parent;
    private String text;
    private int colspan = 1;
    private int rowspan = 1;
    private String cloumn;
    private List<ExcelHeader> children = new ArrayList<ExcelHeader>();

    public ExcelHeader() {
        // TODO Auto-generated constructor stub
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getColspan() {
        return colspan;
    }

    public void setColspan(int colspan) {
        this.colspan = colspan;
    }

    public int getRowspan() {
        return rowspan;
    }

    public void setRowspan(int rowspan) {
        this.rowspan = rowspan;
    }

    public String getCloumn() {
        return cloumn;
    }

    public void setCloumn(String cloumn) {
        this.cloumn = cloumn;
    }

    public List<ExcelHeader> getChildren() {
        return children;
    }

    public void setChildren(List<ExcelHeader> children) {
        this.children = children;
    }
}

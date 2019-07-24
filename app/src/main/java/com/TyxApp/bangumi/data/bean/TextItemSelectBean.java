package com.TyxApp.bangumi.data.bean;

public class TextItemSelectBean {
    private String text;
    private boolean isSelect;

    public TextItemSelectBean(String text) {
        this.text = text;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

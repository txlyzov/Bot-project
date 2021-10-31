package com.example.TestPages;

import lombok.Getter;
import lombok.Setter;

public class testClassForList {
    @Getter @Setter
    private String string1;
    @Getter @Setter
    private String string2;
    @Getter @Setter
    private String string3;

    public testClassForList(String string1,String string2,String string3){
        this.string1 = string1;
        this.string2 = string2;
        this.string3 = string3;
    }
}

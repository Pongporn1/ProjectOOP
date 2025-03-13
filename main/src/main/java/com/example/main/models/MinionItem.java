package com.example.main.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MinionItem {
    private String name;
    private long defense;
    private String script;
    private int imageId;
}

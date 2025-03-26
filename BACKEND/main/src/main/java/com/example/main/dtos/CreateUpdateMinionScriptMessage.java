package com.example.main.dtos;

import lombok.Getter;

@Getter
public class CreateUpdateMinionScriptMessage {
    private String roomId;
    private String script;
    private int index;
}

package com.example.main.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ConfirmMessage {
    private String username;
    private boolean confirmed;
}

package com.example.main.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class RemoveMinion {
    List<MinionItem> minions;
    int index;

}

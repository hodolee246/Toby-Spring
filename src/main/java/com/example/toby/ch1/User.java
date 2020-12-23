package com.example.toby.ch1;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String name;
    private String pwd;
}

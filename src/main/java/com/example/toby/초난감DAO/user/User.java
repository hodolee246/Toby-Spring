package com.example.toby.초난감DAO.user;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String id;      // 회원 아이디
    private String name;    // 회원 이름
    private String pwd;     // 회원 비밀번호
    private Level level;    // 회원 등급
    private int login;      // 회원 로그인 횟수
    private int recommend;  // 회원 추천수

    public enum Level {
        BASIC(1), SILVER(2), GOLD(3);

        private final int value;

        Level(int value) {
            this.value = value;
        }

        public int intValue() {
            return this.value;
        }

        public static Level valueOf(int value) {
            switch(value) {
                case 1: return BASIC;
                case 2: return SILVER;
                case 3: return GOLD;
                default: throw new AssertionError("Unknown Value: '" + value + "'");
            }
        }
    }
}

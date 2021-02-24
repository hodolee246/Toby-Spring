package com.example.toby.초난감DAO.user;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String id;      // 아이디
    private String name;    // 이름
    private String pwd;     // 비밀번호
    private Level level;    // 등급
    private int login;      // 로그인 횟수
    private int recommend;  // 추천수
    private String email;   // 이메일

    public enum Level {
        GOLD(3, null), SILVER(2, Level.GOLD), BASIC(1, Level.SILVER);

        private final int value;
        private final Level next;

        Level(int value, Level next) {
            this.value = value;
            this.next = next;
        }

        public int intValue() {
            return this.value;
        }

        public Level nextLevel() {
            return this.next;
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

    public void upgradeLevel() {
        Level nextLevel = this.level.nextLevel();
        if(nextLevel == null) {
            throw new IllegalArgumentException(this.level + "은 업그레이드가 불가능합니다.");
        } else {
            this.level = nextLevel;
        }
    }
}

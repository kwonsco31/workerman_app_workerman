package com.workerman.app.utils;

public class Schema {

    public static enum BoardIds {

        FILE("fileboard"),
        FREE("freeboard"),
        KNOWLEDGE("knowledge"),
        LOSTANDFOUND("laf"),
        SUGGEST("suggest"),
        NOTICE("notice"),
        CONTEST_INFO("contentinfo"),
        KIN("kin"),
        ENJOY("enjoy"),
        PLAYGROUND("playground"),
        MARKET("market"),
        NOTICE_MOBILE("notice_mobile");

        private final String boardId;

        private BoardIds(String boardId) {
            this.boardId = boardId;
        }

        public String toString() {
            return boardId;
        }

    }

    public static enum TransactionFrag {

        HISTORY_COMMUTE(0),
        HISTORY_SALES(1),
        HISTORY_INCENTIVE(2),
        HISTORY_MILEAGE(3),
        COMMUNITY_NOTICE(4),
        COMMUNITY_QNA(5),
        LIBRARY_MANAGER(6),
        LIBRARY_USER(7),
        MY_STORE(8);

        private final Integer flag;

        private TransactionFrag(Integer flag) {
            this.flag = flag;
        }

        public Integer toInt() {
            return flag;
        }

    }
}

package com.github.walterfan.util.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Walter Fan
 * @Date: 21/2/2020, Fri
 **/
public class Poker {
    public static class Card {
        enum Suite {
            Spades(4), Hearts(3), Clubs(2), Diamonds(1);
            int value;

            Suite(int value) {
                this.value = value;

            }

            private static Map<Integer, Suite> valueMap = new HashMap<>();

            static {
                for (Suite suite : Suite.values()) {
                    valueMap.put(suite.value, suite);
                }
            }

            public static Suite valueOf(int pageType) {
                return valueMap.get(pageType);
            }

        }
        Suite suite;
        //1~13
        int point;

        public Card(int suiteValue, int point) {
            this.suite = Suite.valueOf(suiteValue);
            this.point = point;
        }

        public String toString() {
            String strPoint = Integer.toString(point);
            if (point > 10) {
                switch (point) {
                    case 11:
                        strPoint = "J";
                        break;
                    case 12:
                        strPoint = "Q";
                        break;
                    case 13:
                        strPoint = "K";
                        break;

                }
            }

            return suite.name() + ":" + strPoint;
        }

        public int getScore() {
            return suite.value * 100 + point;
        }
    }




    public static List<Card> createCardList(int suiteCount) {
        List<Card> cards = new ArrayList<>(52);
        for(int i = 1; i < 5; i++) {
            for(int j = 1; j < 14 ;++j) {
                cards.add(new Card(i, j));
            }
        }
        if(suiteCount > 1) {
            for(int j = 0; j < suiteCount - 1; j++) {
                cards.addAll(new ArrayList<>(cards));
            }
        }
        Collections.shuffle(cards);
        return cards;
    }

    public static class CardComparator implements Comparator<Card> {

        @Override
        public int compare(Card o1, Card o2) {
            return o1.getScore() - o2.getScore();
        }
    }

}

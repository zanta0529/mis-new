package rocksdbapi.Rockdis.Models;

import rocksdbapi.Rockdis.RockdisProtocol.Keyword;

import java.util.Arrays;

public class MemberScoreVO {

    private byte[] key;
    private Integer score = 0;
    private byte[] member = new byte[0];
    private Integer scoreRangeStart = 0;
    private Integer scoreRangeEnd = 0;
    private Keyword keyword = Keyword.NORMAL;

    public MemberScoreVO(){

    }

    public MemberScoreVO(byte[] key) {
        this.key = key;
    }

    public MemberScoreVO(byte[] key, Integer score, byte[] member) {
        this.key = key;
        this.score = score;
        this.member = member;
    }

    public MemberScoreVO(byte[] key, Integer scoreRangeStart, Integer scoreRangeEnd) {
        this.key = key;
        this.scoreRangeStart = scoreRangeStart;
        this.scoreRangeEnd = scoreRangeEnd;
    }

    public MemberScoreVO(byte[] key, Integer scoreRangeStart, Integer scoreRangeEnd, Keyword keyword) {
        this.key = key;
        this.scoreRangeStart = scoreRangeStart;
        this.scoreRangeEnd = scoreRangeEnd;
        this.keyword = keyword;
    }

    public MemberScoreVO(byte[] key, byte[] member) {
        this.key = key;
        this.member = member;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public byte[] getMember() {
        return member;
    }

    public void setMember(byte[] member) {
        this.member = member;
    }

    public Integer getScoreRangeStart() {
        return scoreRangeStart;
    }

    public void setScoreRangeStart(Integer scoreRangeStart) {
        this.scoreRangeStart = scoreRangeStart;
    }

    public Integer getScoreRangeEnd() {
        return scoreRangeEnd;
    }

    public void setScoreRangeEnd(Integer scoreRangeEnd) {
        this.scoreRangeEnd = scoreRangeEnd;
    }

    public Keyword getKeyword() {
        return keyword;
    }

    public void setKeyword(Keyword keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return "MemberScoreVO{" +
                "key=" + new String(key)+
                ", score=" + score +
                ", member=" + new String(member) +
                ", scoreRangeStart=" + scoreRangeStart +
                ", scoreRangeEnd=" + scoreRangeEnd +
                ", keyword=" + keyword +
                '}';
    }
}

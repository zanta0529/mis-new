package redisapi.DAO;

/**
 * @author kkw
 */

public class ScoreMemberVO {

    private Integer score;
    private String member;

    public ScoreMemberVO(Integer score, String member) {
        this.score = score;
        this.member = member;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }
}
